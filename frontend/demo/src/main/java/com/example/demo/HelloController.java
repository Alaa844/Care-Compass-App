package com.example.demo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {
    @FXML
    private Button problemButton; // For switching to chatbot
    @FXML
    private Button feedbackButton; // For switching to feedback
    @FXML
    private Button backToLoginButton; // For switching to login

    @FXML
    public void switchToChatbot() {
        try {
            // Load the chatbot FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/chatbot.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) problemButton.getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("ChatBot");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Optionally show an alert to the user
        }
    }

    @FXML
    public void switchToFeedback() {
        try {
            // Load the feedback FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/feedback.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) feedbackButton.getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Feedback");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Optionally show an alert to the user
        }
    }

    @FXML
    public void switchToLogin() {
        try {
            // Load the login FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo/login.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) backToLoginButton.getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Optionally show an alert to the user
        }
    }
}