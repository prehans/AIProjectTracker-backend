package com.SpringProject.controller;

import com.SpringProject.Entity.Project;
import com.SpringProject.service.GenerativeAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AIController {
    @Autowired
    private GenerativeAIService generativeAIService;

    @PostMapping("/generate")
    public String generateResponse(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        if (prompt == null || prompt.isEmpty()) {
            return "Error: 'prompt' field is missing or empty in the request body";
        }
        Long userId = Long.valueOf(request.get("user_id")); // Extract userId from request
        String aiResponse = generativeAIService.getAIResponse(prompt, userId);

        return aiResponse;
    }
    @GetMapping("/get/{projectId}")
    public String getTasks(@PathVariable Long projectId) {

            return generativeAIService.getTasksAsJson(projectId);

    }
    @GetMapping("/get/projects")
    public List<Project> getProjects() {

     return generativeAIService.getProjects();

    }
}