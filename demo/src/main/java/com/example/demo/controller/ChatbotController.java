package com.example.demo.controller;

import com.example.demo.config.CustomRestTemplate;
import com.example.demo.util.ResponseStore;
import com.example.demo.dto.PromptRequestDto;
import com.example.demo.service.DoctorSuggestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.MediaType;



import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.replaceAll;

@RestController
@RequestMapping("/api/chat")
public class ChatbotController {

    private final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private final String API_KEY = "empty now";

    @Autowired
    private DoctorSuggestionService doctorService;



    @PostMapping
    public ResponseEntity<?> getChatbotResponse(@RequestBody PromptRequestDto requestDto) {
        RestTemplate restTemplate = CustomRestTemplate.createRestTemplateWithTimeout();

        // about text
        String userMessage = requestDto.getMessage().toLowerCase();
// Check if user asked about the system
        if (userMessage.contains("who are you") ||
                userMessage.contains("about you") ||
                userMessage.contains("what is Care Compass") ||
                userMessage.contains("what can you do")) {

            String aboutText = """
        👋 Hello! I'm your virtual assistant from Care Compass.
        
        I can:
        - Offer home remedies and self-care suggestions for minor issues.
        - Help you decide when to see a doctor.
        - Suggest the best doctor from our team based on your symptoms.
        
        Care Compass is your go-to clinic for reliable and accessible internal medicine services. We aim to make your healthcare journey easier and more informed.
        
        How can I help you today?
        """;

            return ResponseEntity.ok(aboutText);
        }


        // Prepare request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(API_KEY);

        // Prepare request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "deepseek/deepseek-r1:free");
        requestBody.put("messages", new Object[]{
                Map.of("role", "user", "content", requestDto.getMessage())
        });

        HttpEntity<Map<String, Object>> httpRequest = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(API_URL, httpRequest, Map.class);

            // Extract the content
            Map responseBody = response.getBody();
            Map choice = (Map) ((List) responseBody.get("choices")).get(0);
            Map message = (Map) choice.get("message");

            String responseContent = (String) message.get("content");


//  Get doctor suggestion based on the prompt
            String doctorSuggestion = doctorService.getDoctorRecommendation(requestDto.getMessage());

//  Combine the AI response and  advice
            String finalResponse = responseContent + "\n\n👨‍⚕️ *Care Compass Recommendation:* " + doctorSuggestion;


            return ResponseEntity.ok(finalResponse);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/start")
    public ResponseEntity<?> startChat(@RequestBody PromptRequestDto requestDto) {
        String requestId = java.util.UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();
        System.out.println("Starting chat request with ID: " + requestId + " for message: " + requestDto.getMessage());

        new Thread(() -> {
            try {
                RestTemplate restTemplate = CustomRestTemplate.createRestTemplateWithTimeout();

                String doctorSuggestion = doctorService.getDoctorRecommendation(requestDto.getMessage());
                System.out.println("Doctor suggestion retrieved: " + doctorSuggestion);

                System.out.println("Calling OpenRouter API...");
                String aiResponse = getAIResponse(requestDto.getMessage());
                System.out.println("OpenRouter API response received: " + aiResponse);

                // Clean the AI response by removing Markdown markers
                String cleanedAiResponse = aiResponse
                        .replaceAll("###", "") // Remove #### (Markdown headings)
                        .replaceAll("\\*\\*", ""); // Remove ** (Markdown bold)

                String finalResponse = (cleanedAiResponse == null || cleanedAiResponse.isEmpty() ?
                        "No advice available from AI at this time." : cleanedAiResponse) +
                        "\n\n👨‍⚕️ *Care Compass Recommendation:* " + doctorSuggestion;

                System.out.println("Final response prepared (length: " + finalResponse.length() + " chars)");

                ResponseStore.RESPONSE_MAP.put(requestId, finalResponse);
                System.out.println("Response stored in map for ID: " + requestId +
                        " (Total time: " + (System.currentTimeMillis() - startTime) + " ms)");
            } catch (Exception e) {
                System.err.println("Error in async thread for request ID " + requestId + ": " + e.getMessage());
                e.printStackTrace();
                ResponseStore.RESPONSE_MAP.put(requestId, "Error: " + e.getMessage());
            }
        }).start();

        return ResponseEntity.ok(Map.of("requestId", requestId));
    }

    @GetMapping("/result/{id}")
    public ResponseEntity<?> getResult(@PathVariable("id") String requestId) {
        System.out.println("Polling request received for ID: " + requestId);

        // Defensive check: If the id contains "requestId", attempt to extract the actual ID
        String actualId = requestId;
        if (requestId.startsWith("{\"requestId\":\"") && requestId.endsWith("\"}")) {
            actualId = requestId.substring("{\"requestId\":\"".length(), requestId.length() - 2);
            System.out.println("Extracted actual requestId: " + actualId);
        }

        if (ResponseStore.RESPONSE_MAP.containsKey(actualId)) {
            String response = ResponseStore.RESPONSE_MAP.remove(actualId); // Remove after reading
            System.out.println("Response found for ID " + actualId + ": " + response);
            return ResponseEntity.ok(Map.of("response", response)); // Ensure JSON format
        } else {
            System.out.println("No response found for ID " + actualId + ", returning Processing...");
            return ResponseEntity.status(202).body(Map.of("response", "Processing...")); // Ensure JSON format
        }
    }

    private String getAIResponse(String userPrompt) {
        RestTemplate restTemplate = CustomRestTemplate.createRestTemplateWithTimeout();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(API_KEY);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "deepseek/deepseek-r1:free");
        requestBody.put("messages", new Object[]{
                Map.of("role", "user", "content", userPrompt)
        });

        HttpEntity<Map<String, Object>> httpRequest = new HttpEntity<>(requestBody, headers);

        try {
            System.out.println("Sending request to OpenRouter API for prompt: " + userPrompt);
            ResponseEntity<Map> response = restTemplate.postForEntity(API_URL, httpRequest, Map.class);
            System.out.println("OpenRouter API response status: " + response.getStatusCode());

            Map responseBody = response.getBody();
            Map choice = (Map) ((java.util.List) responseBody.get("choices")).get(0);
            Map message = (Map) choice.get("message");

            String content = (String) message.get("content");
            System.out.println("OpenRouter API response content: " + content);
            return content;

        } catch (Exception e) {
            System.err.println("Error calling OpenRouter API: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
