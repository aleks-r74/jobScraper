package com.alexportfolio.webFace.controller;

import com.alexportfolio.webFace.Service.RestService;
import com.alexportfolio.webFace.Service.JobRepoService;
import com.alexportfolio.webFace.beans.SearchStartTime;
import com.alexportfolio.webFace.entity.JobCard;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@Controller
public class MyControllerImpl implements MyController {
    JobRepoService jobRepoService;
    SearchStartTime searchStartTime;
    RestService restService;

    public MyControllerImpl(JobRepoService jobRepoService, SearchStartTime searchStartTime, RestService restService) {
        this.jobRepoService = jobRepoService;
        this.searchStartTime = searchStartTime;
        this.restService = restService;
    }


    @Override
    @GetMapping("/")
    public String getAll(@RequestParam(required = false) Integer page,
                         @RequestParam(required = false) LocalDateTime pageDateTime,
                         @RequestParam(required = false) String filter,
                         Model model,
                         Principal principal){
        if(page == null) page = 1;
        if(filter == null) filter = principal != null ? "approved" : "";

        if(pageDateTime!=null) searchStartTime.setDateTime(pageDateTime);

        UnaryOperator<JobCard> filterFunc = card -> card.setNotes(null); // hides all notes by default
        Optional<Page<JobCard>> dbResponse =
                switch(filter){
                    default-> jobRepoService.getPage(searchStartTime.getDateTime(), page);
                    case "summarized" -> jobRepoService.GetSummarizedCards(searchStartTime.getDateTime(), page);
                    case "declined" -> {
                        // show only notes that starts with "no"
                        filterFunc = card -> card.getNotes().toLowerCase().startsWith("no") ? card : card.setNotes(null);
                        yield jobRepoService.getCardsWithDecision("no",searchStartTime.getDateTime(), page);
                    }
                    case "approved" -> jobRepoService.getCardsWithDecision("yes",searchStartTime.getDateTime(), page);
                };
        List<JobCard> pageItems = null;
        int totalPages = 0;

        if(!dbResponse.isEmpty()) {
            pageItems = dbResponse.get().getContent();
            totalPages = dbResponse.get().getTotalPages();
            pageItems = pageItems.stream()
                    .map(filterFunc)
                    .collect(Collectors.toList());
        }
       model.addAttribute("totalPages", totalPages);
       model.addAttribute("list", pageItems);
       model.addAttribute("page", page);
       model.addAttribute("serverDateTime", searchStartTime.getDateTime());
       model.addAttribute("pageName", "home");
        model.addAttribute("filter", filter);
       return "results";
    }

    @Override
    @PostMapping("/")
    public String getAll(@RequestParam Map<String, String> params){
        // extracting ids from params
        BiFunction <Set<String>,String,List<Long>> getIds =
                (set, word) -> {
                    return
                            params.keySet().stream()
                            .filter(s -> s.contains(word))
                            .map(s -> {
                                int start = s.indexOf(':');
                                return Long.valueOf(s.substring(start + 1));
                            })
                            .collect(Collectors.toList());
                };
        List<Long> removeIds    = getIds.apply(params.keySet(),"remove");
        List<Long> appliedIds   = getIds.apply(params.keySet(),"markApplied");

        jobRepoService.deleteAllWithIds(removeIds);
        jobRepoService.setApplied(appliedIds);

        // making a string of request params
        StringBuilder requestParams = new StringBuilder();
        requestParams.append("page=" + params.get("page"));
        if(params.get("filter")!=null)
            requestParams.append("&filter=" + params.get("filter"));

        return "redirect:/?"+ requestParams;
    }

    @Override
    @GetMapping("/settings")
    public String showSettings(Model model){
        String saveKeywords="error";
        String stopKeywords="error";
        String links="error";
        String resume = "error";
        try{
            var prop = restService.getKeywords();
            saveKeywords = prop.getProperty("saveKeywords");
            stopKeywords = prop.getProperty("stopKeywords");
            links = restService.getLinks().stream().collect(Collectors.joining("\n"));
            resume = restService.getResume();
        } catch (HttpClientErrorException e) {
            throw new RuntimeException(e);
        }
        model.addAttribute("saveKeywords", saveKeywords);
        model.addAttribute("stopKeywords",stopKeywords);
        model.addAttribute("links", links);
        model.addAttribute("resume", resume);
        model.addAttribute("pageName","settings");
        return "settings";
    }

    @Override
    @PostMapping("/settings")
    public String saveSettings(@RequestParam Map<String, String> params){
        try {
            String saveKeywords = params.get("saveKeywords");
            String stopKeywords = params.get("stopKeywords");
            restService.saveKeywords(stopKeywords, saveKeywords);
            restService.saveLinks(params.get("links"));
            restService.saveResume(params.get("resume"));
        } catch (HttpClientErrorException e) {
            return "redirect:/settings?notSaved";
        }
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        return "redirect:/settings?saved=" + time;
    }

    @Override
    @GetMapping("/about")
    public String showAbout(Model model) {
        model.addAttribute("pageName","about");
        return "about";
    }

    @Override
    @GetMapping("/login")
    public String showLogin(Model model) {
        model.addAttribute("pageName","login");
        return "login";
    }
}
