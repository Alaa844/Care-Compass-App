package com.example.demo.api;



import com.example.demo.model.PromptRequestDto;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.Map;

public interface ChatBotApi {
    @POST("chat")
    Call<String> getChatbotResponse(@Body PromptRequestDto request);

    @POST("/api/chat/start")
    Call<Map<String, String>> startChat(@Body PromptRequestDto prompt); // Changed to return Map<String, String>

    @GET("/api/chat/result/{id}")
    Call<Map<String, String>> getChatStatus(@Path("id") String id);
}
