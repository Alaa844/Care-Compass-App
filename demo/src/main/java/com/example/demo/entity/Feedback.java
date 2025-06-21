package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String doctorName;
    private int clinicRating;
    private int serviceRating;
    private String comment;
    private int timeTaken; // New field for user-entered time taken (in minutes)
    private Integer medicineRating; // New field for medicine rating (1-5)

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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