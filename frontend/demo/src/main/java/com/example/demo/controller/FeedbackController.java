package com.example.demo.controller;

import com.example.demo.api.FeedbackApi;
import com.example.demo.api.RetrofitClient;
import com.example.demo.model.FeedbackDto;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;

public class FeedbackController {

    @FXML private ComboBox<String> doctorDropdown;
    @FXML private ComboBox<Integer> doctorRating;
    @FXML private ComboBox<Integer> serviceRating;
    @FXML private Spinner<Integer> clinicRatingSpinner;
    @FXML private TextField timeTakenField; // New field
    @FXML private ComboBox<Integer> medicineRating; // New field
    @FXML private TextArea commentBox;
    @FXML private Button backButton;

    @FXML
    public void initialize() {
        clinicRatingSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5));

        serviceRating.getItems().addAll(1, 2, 3, 4, 5);
        doctorRating.getItems().addAll(1, 2, 3, 4, 5);
        medicineRating.getItems().addAll(1, 2, 3, 4, 5); // Initialize medicine rating options

        doctorDropdown.getItems().addAll(
                "Dr. Aisha Al-Mansoori",
                "Dr. Khalid Al-Hakim",
                "Dr. Layla Farouq",
                "Dr. Omar Ibn Salim",
                "Dr. Fatima Zahra",
                "Dr. Tariq Al-Rashid",
                "Dr. Zainab Al-Najjar"
        );
    }

    @FXML
    public void submitFeedback() {
        String doctorName = doctorDropdown.getValue();
        int clinicRating = clinicRatingSpinner.getValue();
        Integer serviceRate = serviceRating.getValue();
        Integer docRate = doctorRating.getValue();
        String comment = commentBox.getText();
        String timeTakenText = timeTakenField.getText();
        Integer medicineRate = medicineRating.getValue();

        // Validation
        if (doctorName == null || serviceRate == null || docRate == null || comment.isEmpty()) {
            showAlert("Validation Error", "Doctor, ratings, and comment are required.");
            return;
        }

        // Validate time taken (must be a positive integer)
        int timeTaken;
        try {
            timeTaken = Integer.parseInt(timeTakenText);
            if (timeTaken < 0) {
                showAlert("Validation Error", "Time taken must be a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Time taken must be a valid number.");
            return;
        }

        // Medicine rating is optional, so it can be null

        FeedbackDto dto = new FeedbackDto(doctorName, clinicRating, serviceRate, comment, timeTaken, medicineRate);

        FeedbackApi api = RetrofitClient.getInstance().create(FeedbackApi.class);
        api.submitFeedback(dto).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                javafx.application.Platform.runLater(() -> {
                    if (response.isSuccessful()) {
                        showAlert("Success", response.body());
                    } else {
                        showAlert("Error", "Submission failed");
                    }
                });
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                javafx.application.Platform.runLater(() -> {
                    showAlert("Error", t.getMessage());
                });
            }
        });
    }

    @FXML
    public void switchToMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/hello-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Care Compass");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to return to main page: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}