package com.SpringProject.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GenerativeAIService {
    @Value("${google.generative.ai.api.key}")
    private String apiKey;

    public String getAIResponse(String prompt) {
        // Create the request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))));

        // Headers for the API
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // Send POST request to the API
        RestTemplate restTemplate = new RestTemplate();
        String GENERATIVE_AI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;
        ResponseEntity<Map> response = restTemplate.exchange(
                GENERATIVE_AI_URL,
                HttpMethod.POST,
                entity,
                Map.class
        );

        // Extract the response
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map responseBody = response.getBody();
            List<Map> candidates = (List<Map>) responseBody.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map content = (Map) candidates.get(0).get("content");
                List<Map> parts = (List<Map>) content.get("parts");
                if (parts != null && !parts.isEmpty()) {
                    String responseText = parts.get(0).get("text").toString();
                    return convertToJson(formatResponseWithCheckboxes(responseText));
//                    return formatResponseWithCheckboxes(responseText).toString();
                  //  return parts.get(0).get("text").toString();
                }
            }
        }
        return "No response from AI model. API Key: hello " + apiKey;
    }

    private List<Map<String, Object>> formatResponseWithCheckboxes(String responseText) {
        // Split the response by phases or tasks
        String[] phases = responseText.split("\\n\\n"); // Assuming phases/tasks are separated by double line breaks

        List<Map<String, Object>> tasks = new ArrayList<>();
        for (int i = 0; i < phases.length; i++) {
            Map<String, Object> task = new HashMap<>();
            task.put("taskId", i + 1);
            task.put("description", phases[i].trim()); // Trim unnecessary whitespace
            task.put("completed", false); // Default all tasks to "not completed"
            tasks.add(task);
        }

        return tasks; // Return the formatted task list
    }

    private String convertToJson(List<Map<String, Object>> tasks) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(tasks); // Convert the task list to a JSON string
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{\"error\": \"Failed to convert tasks to JSON.\"}";
        }
    }

}

