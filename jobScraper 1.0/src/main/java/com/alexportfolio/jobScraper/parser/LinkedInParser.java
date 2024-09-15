package com.alexportfolio.jobScraper.parser;

import com.alexportfolio.jobScraper.JobScraperApplication;
import com.alexportfolio.jobScraper.entity.JobCard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

/*
    This class is not a Bean
    If the browser is closed during parsing, throws exception
 */
public class LinkedInParser extends ParserUtils {
    private static final Logger logger = LoggerFactory.getLogger(LinkedInParser.class);

    int afterLoginDelay;

    public LinkedInParser(int afterLoginDelay) throws IOException {
        super();
        this.afterLoginDelay = afterLoginDelay;

    }
    public void login(String username, String password){
        // try to restore cookies, session, etc
        loadBrowserState();
        // loggin in as usual
        driver.get(loginUrl);

        if(driver.getCurrentUrl().equals(loginUrl)){
            var login = getElement(xpaths.get("login"), WaitType.CLICKABLE);
            login.ifPresent(element->typeText(element, username));

            var passw = getElement(xpaths.get("password"), WaitType.CLICKABLE);
            passw.ifPresent(element->typeText(element, password));

            var loginBtn = getElement(xpaths.get("login_btn"), WaitType.CLICKABLE);
            loginBtn.ifPresent(WebElement::click);
            sleep(afterLoginDelay>5?afterLoginDelay*1000:5000);
            saveBrowserState();
        }

    }
    public Set<JobCard> parseJobCards(List<String> urls){
        Set<JobCard> tmp = new HashSet<>();
        for(String url: urls){
            tmp.addAll(parseJobCards(url));
        }
        return tmp;
    }
    public Set<JobCard> parseJobCards(String url) {
        Set<JobCard> uniqueJobCardSet = new HashSet<>();
        var filteringKeywords = getFilteringKeywords();
        // 1. Open the page
        driver.get(url);
        // 2. Check if jobs are found
        Optional<WebElement> jobsFound = getElement(xpaths.get("jobs_found"), WaitType.PRESENCE);
        if(jobsFound.isEmpty()) return Set.of();

        // scroll the page, get cards from the page, go to the next page, repeat
        boolean flag = true;

        int currentPage = 0;
        Optional<List<WebElement>> cards = Optional.empty();
        while (flag) {
            scrollPage(xpaths.get("job_card"));
            cards = getElements(xpaths.get("job_card"));
            if (cards.isEmpty())  return Set.of(); //if cards not found, return empty list

            // re-try wrapper
            ReTryLoop: for(int i=0;i<3; i++)
                try{
                    // 3. Take all job titles from this page in a loop
                    CardLoop: for (WebElement card : cards.get()) {
                        WebElement cardUrl = card.findElement(By.xpath(xpaths.get("card_url")));
                        String jobUrl = cardUrl.getAttribute("href");
                        String jobTitle = cardUrl.getAttribute("aria-label").strip();
                        String comapnyName = card.findElement(By.xpath(xpaths.get("company_name"))).getText().strip();

                        // filter out not job links
                        if(!jobUrl.contains("www.linkedin.com/jobs/view"))
                            continue CardLoop;
                        boolean saveFlag = filteringKeywords.get("save").size() > 0 ? false : true;
                        // filter out by save-stop keywords
                        for(String saveKeyword: filteringKeywords.get("save"))
                            if(jobTitle.toLowerCase().contains(saveKeyword))
                                saveFlag = true;
                        for(String stopKeyword: filteringKeywords.get("stop"))
                            if(jobTitle.toLowerCase().contains(stopKeyword))
                                saveFlag = false;
                        if(!saveFlag) continue CardLoop;
                        //here we should add titles
                        uniqueJobCardSet.add(new JobCard(jobTitle, comapnyName, null, jobUrl, null,  null, null ));
                    }
                    break ReTryLoop;
                } catch(RuntimeException e){
                    cards = getElements(xpaths.get("job_card"));
                }
            // Go to the next page
            var pages = getElements(xpaths.get("pages"));
            if (pages.isPresent()) {
                if ((currentPage < pages.get().size() - 1)) {
                    currentPage++;
                    // if next page link has digits, click on it
                    if (!pages.get().get(currentPage).getText().replaceAll("[^0-9]+", "").isBlank()) {
                        pages.get().get(currentPage).click();
                        continue;
                    }
                }
            }
            flag = false;
        }
        return uniqueJobCardSet;
    }

    // accepts set of jobcards and fills parses description for each card
    public List<JobCard> fillDescription(List<JobCard> jobCards){
        // fill job description, add "parsed" note on success
        if(jobCards == null)
            throw new IllegalArgumentException("No JobCard provided for parsing.");
        var timeStamp = LocalDateTime.now();
        var iterator = jobCards.iterator();
        while(iterator.hasNext()){
            var card = iterator.next();
            driver.get(card.getUrl());
            getElement(xpaths.get("see_more"), WaitType.CLICKABLE).ifPresent(WebElement::click);
            var element = getElement(xpaths.get("job_description"), WaitType.PRESENCE);
            if(element.isPresent() && !element.get().getText().isBlank()) {
                String text = element.get().getText().strip();
                if(text.length()>4096) text = text.substring(0,4096);
                card.setDescription(text);
                card.setTimeStamp(timeStamp);
            } else {
                //if job description wasn't found, we remove this card
                iterator.remove();
                logger.debug("empty job description or can't get the element");
            }
        }
       return jobCards;
    }



    public void destroy() {
        driver.quit();
        driver.close();
    }
}
