package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DoctorSuggestionService {

    private final Map<String, String> symptomDoctorMap = new HashMap<>();

    public DoctorSuggestionService() {
        symptomDoctorMap.put("cough", "Dr. Aisha Al-Mansoori (Internal Medicine)");
        symptomDoctorMap.put("fever", "Dr. Khalid Al-Hakim (General Physician)");
        symptomDoctorMap.put("sore throat", "Dr. Layla Farouq (ENT Specialist)");
        symptomDoctorMap.put("headache", "Dr. Omar Ibn Salim (Neurologist)");
        symptomDoctorMap.put("chest pain", "Dr. Fatima Zahra (Cardiologist)");
        symptomDoctorMap.put("skin rash", "Dr. Tariq Al-Rashid (Dermatologist)");
        symptomDoctorMap.put("stomach pain", "Dr. Zainab Al-Najjar (Gastroenterologist)");
    }

    public String getDoctorRecommendation(String userMessage) {
        String lowerMsg = userMessage.toLowerCase();
        for (String keyword : symptomDoctorMap.keySet()) {
            if (lowerMsg.contains(keyword)) {
                return "\n\n👨‍⚕️ You might want to see " + symptomDoctorMap.get(keyword) + " at Care Compass.";
            }
        }
        return ""; // No match
    }
}
