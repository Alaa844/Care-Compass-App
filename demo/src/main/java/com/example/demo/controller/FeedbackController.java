package com.example.demo.controller;

import com.example.demo.entity.Feedback;
import com.example.demo.dto.FeedbackDto;
import com.example.demo.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin
public class FeedbackController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @PostMapping
    public String submitFeedback(@RequestBody FeedbackDto dto) {
        Feedback feedback = new Feedback();
        feedback.setDoctorName(dto.getDoctorName());
        feedback.setClinicRating(dto.getClinicRating());
        feedback.setServiceRating(dto.getServiceRating());
        feedback.setComment(dto.getComment());
        feedback.setTimeTaken(dto.getTimeTaken());
        feedback.setMedicineRating(dto.getMedicineRating()); 

        feedbackRepository.save(feedback);
        return "Feedback submitted successfully";
    }
}