package com.example.demo.dto;

public class FeedbackDto {
    private String doctorName;
    private int clinicRating;
    private int serviceRating;
    private String comment;
    private int timeTaken; // New field
    private Integer medicineRating; // New field

    // Constructor
    public FeedbackDto(String doctorName, int clinicRating, int serviceRating, String comment, int timeTaken, Integer medicineRating) {
        this.doctorName = doctorName;
        this.clinicRating = clinicRating;
        this.serviceRating = serviceRating;
        this.comment = comment;
        this.timeTaken = timeTaken;
        this.medicineRating = medicineRating;
    }

    // Getters and setters
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public int getClinicRating() { return clinicRating; }
    public void setClinicRating(int clinicRating) { this.clinicRating = clinicRating; }

    public int getServiceRating() { return serviceRating; }
    public void setServiceRating(int serviceRating) { this.serviceRating = serviceRating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public int getTimeTaken() { return timeTaken; }
    public void setTimeTaken(int timeTaken) { this.timeTaken = timeTaken; }

    public Integer getMedicineRating() { return medicineRating; }
    public void setMedicineRating(Integer medicineRating) { this.medicineRating = medicineRating; }
}