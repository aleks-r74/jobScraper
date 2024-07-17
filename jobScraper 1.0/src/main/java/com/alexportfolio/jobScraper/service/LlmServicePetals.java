package com.alexportfolio.jobScraper.service;


import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Optional;

@Component
public class LlmServicePetals implements LlmService{
    private String model = "petals-team/StableBeluga2";
    private String endpoint = "https://chat.petals.dev/api/v1/generate";
    RestTemplate restTemplate;

    public LlmServicePetals(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<String> callLLM(List<String> prompt) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        StringBuilder promptStrb = new StringBuilder();

        for(String str: prompt)
            promptStrb.append(str + "\n");

        formData.add("model", model);
        formData.add("max_new_tokens", "500");
        formData.add("inputs", promptStrb.toString());

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);

        ResponseEntity<ResponseObject> response = restTemplate.exchange(
                endpoint,
                HttpMethod.POST,
                requestEntity,
                ResponseObject.class
        );
        return Optional.of(response.getBody().getOutputs().replace("\\n","\n"));
    }

    @Override
    public String getName() {
        return "Petals";
    }
}
class ResponseObject {
    boolean ok;
    String outputs;

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public String getOutputs() {
        return outputs;
    }

    public void setOutputs(String outputs) {
        this.outputs = outputs;
    }

    public ResponseObject() {
    }
}