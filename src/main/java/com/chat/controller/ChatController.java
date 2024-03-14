package com.chat.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
public
class ChatController {
    @Value(("${openai.api.url}"))
    private String APIURL;

    @Value("${openai.api.key}")
    private String APIKEY;

    @Value("${openai.api.host}")
    private String HOSTNAME;

    @PostMapping("/ask")
    @ResponseBody
    public ResponseEntity<String> askApi(@RequestBody Map<String, String> requestBody) {

        // Create a RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        // Create request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-RapidAPI-Key", APIKEY);
        headers.set("X-RapidAPI-Host", HOSTNAME);

        // Create HTTP entity with headers and body
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            // Make the HTTP POST request
            ResponseEntity<String> responseEntity = restTemplate.exchange(APIURL, HttpMethod.POST, requestEntity, String.class);
            return ResponseEntity.ok(responseEntity.getBody());
        } catch (HttpClientErrorException e) {
            // Handle HTTP client errors
            HttpStatusCode statusCode = e.getStatusCode();
            String responseBody = e.getResponseBodyAsString();
            return ResponseEntity.status(statusCode).body(responseBody);
        } catch (Exception e) {
            // Handle other exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error occurred.");
        }
    }
}

