package com.SpringProject.controller;

import com.SpringProject.Entity.Project;
import com.SpringProject.service.GenerativeAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "http://localhost:5173")
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
    @GetMapping("/get/projects_by_userID")
    public List<Project> getProjects(@RequestBody Map<String, String> request) {
        Long userId = Long.valueOf(request.get("user_id")); // Extract userId from request
     return generativeAIService.getProjects();

    }

    @GetMapping("/get/projects")
    public List<Project> getProjects() {
        return generativeAIService.getProjects();

    }

    @PutMapping("/update/{taskId}")
    public String updateTaskCompletion(@RequestBody Map<String, Boolean> request, @PathVariable Long taskId) {
        if (!request.containsKey("completed")) {
            return "Error: 'completed' field is missing in request body";
        }
        boolean isCompleted = request.get("completed");
       boolean success = generativeAIService.updateTaskCompletion(taskId, isCompleted);
        if (success) {
            return "Task updated successfully";
        } else {
            return "Error: Task not found or could not be updated";
        }
    }

    @PostMapping("/hint")
    public ResponseEntity<String> getHint(@RequestBody Map<String, String> request){
        String prompt = request.get("prompt");
        if(prompt == null || prompt.isEmpty()){
//            return "Error: 'prompt' field is missing or empty in the request body";
            return ResponseEntity.badRequest().body("Error: 'prompt' field is missing or empty in the request body");
        }
//        String hint = generativeAIService.getHint(prompt);
//
//        return hint;
        return generativeAIService.getHint(prompt);
    }

}