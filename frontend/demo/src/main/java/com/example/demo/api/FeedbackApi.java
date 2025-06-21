package com.example.demo.api;

import com.example.demo.model.FeedbackDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface FeedbackApi {
    @POST("/api/feedback")
    Call<String> submitFeedback(@Body FeedbackDto feedbackDto);
}