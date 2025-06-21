package com.example.demo.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackResponseDto {
    private String userName;
    private String doctorName;
    private int doctorRating;
    private int serviceRating;
    private int clinicRating;
    private String comment;
}
