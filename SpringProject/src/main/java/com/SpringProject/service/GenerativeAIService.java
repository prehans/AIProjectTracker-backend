package com.SpringProject.service;

import com.SpringProject.Entity.User;
import com.SpringProject.Repository.ProjectRepository;
import com.SpringProject.Repository.TaskRepository;
import com.SpringProject.Entity.Task;
import com.SpringProject.Entity.Project;
import com.SpringProject.Repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class GenerativeAIService {

    @Value("${google.generative.ai.api.key}")
    private String apiKey;

    private final TaskRepository taskRepository;
    private final RestTemplate restTemplate;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public GenerativeAIService(TaskRepository taskRepository , ProjectRepository projectRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.restTemplate = new RestTemplate();
    }
    //////////////////////////////////////////////////////
    /// function to generate the response (task) from AI
    //////////////////////////////////////////////////////

    public String getAIResponse(String prompt, Long userId) {
        // Prepare request body
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(Map.of("text", prompt))
                ))
        );

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // API URL
        String GENERATIVE_AI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;

        ResponseEntity<Map> response = restTemplate.exchange(GENERATIVE_AI_URL, HttpMethod.POST, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map responseBody = response.getBody();
            List<Map> candidates = (List<Map>) responseBody.get("candidates");

            if (candidates != null && !candidates.isEmpty()) {
                Map content = (Map) candidates.get(0).get("content");
                List<Map> parts = (List<Map>) content.get("parts");

                if (parts != null && !parts.isEmpty()) {
                    String responseText = parts.get(0).get("text").toString();
//                    return convertToJson(formatResponseWithCheckboxes(responseText , project_id));
                    return generateProjectWithTasks("New AI Project", userId, responseText);
                }
            }
        }
        return "{\"error\": \"No response from AI model.\"}";
    }

//    private List<Task> formatResponseWithCheckboxes(String responseText , Long projectId) {
//        String[] phases = responseText.split("\\n\\n"); // Assuming double newlines separate tasks
//        List<Task> tasks = new ArrayList<>();
//        // Fetch the project from the database
//        Project project = projectRepository.findById(projectId)
//                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));
//
//        for (int i = 0; i < phases.length; i++) {
//            Task task = new Task();
//            task.setDescription(phases[i].trim());
//            task.setCompleted(false); // Default to false
//            task.setProject(project);
//            taskRepository.save(task); // Save to DB
//            tasks.add(task);
//        }
//        return tasks;
//    }

    public String generateProjectWithTasks(String projectName, Long userId, String aiResponseText) {
        // Step 1: Create a new project
        Project project = createNewProject(projectName, userId);
        if (project == null) {
            return "{\"error\": \"User not found with ID: " + userId + "\"}";
        }

        // Step 2: Create tasks from AI response and assign them to the project
        List<Task> tasks = createTasksForProject(aiResponseText, project);

        // Return the created tasks in JSON format
        return convertToJson(tasks);
    }

    private Project createNewProject(String projectName, Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return null; // User not found
        }

        Project project = new Project();
        project.setProjectName(projectName);
        project.setUser(userOptional.get());

        projectRepository.save(project); // Save project to DB
        return project;
    }

    private List<Task> createTasksForProject(String aiResponseText, Project project) {
        String[] taskDescriptions = aiResponseText.split("\\n\\n"); // Assuming tasks are separated by double newlines
        List<Task> tasks = new ArrayList<>();

        for (String description : taskDescriptions) {
            Task task = new Task();
            task.setDescription(description.trim());
            task.setCompleted(false);
            task.setProject(project);

            taskRepository.save(task); // Save task to DB
            tasks.add(task);
        }
        return tasks;
    }

    private String convertToJson(List<Task> tasks) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
//        objectMapper.registerModule(new JavaTimeModule());

        try {
            return objectMapper.writeValueAsString(tasks);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{\"error\": \"Failed to convert tasks to JSON.\"}";
        }

    }
    /////////////////////////////////////////
    /// function to get all task from project
    /////////////////////////////////////////

    public String getTasksAsJson(Long projectId) {

        if (!taskRepository.existsById(projectId)) {
            return "Project not found with Id " + projectId; // Return an empty list to indicate project not found
        }
        List<Task> tasks = taskRepository.findByProjectId(projectId);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Handles LocalDateTime if used

        try {
            return objectMapper.writeValueAsString(tasks);
        } catch (Exception e) {
//            When an exception occurs, Java generates a "stack trace" that records the method calls in reverse order
//            (last method call first). printStackTrace() prints this trace, helping you find the root cause of an error.
           e.printStackTrace();
            return "{\"error\": \"Failed to convert tasks to JSON.\"}";
        }
    }

    public List<Project> getProjects(){
        List<Project> projects = projectRepository.findAll();
        try{
            return projects;
        }
        catch (Exception e) {
            e.printStackTrace(); // Print the full stack trace
            return Collections.emptyList(); // Return an empty list in case of an error
        }
    }
}
