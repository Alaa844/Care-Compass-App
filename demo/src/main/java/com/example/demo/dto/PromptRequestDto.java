package com.example.demo.dto;

public class PromptRequestDto {
    private String message;

    public PromptRequestDto() {
    }

    public PromptRequestDto(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
