package com.example.demo.controller;

import com.example.demo.model.PromptRequestDto;
import com.example.demo.api.ChatBotApi;
import com.example.demo.api.RetrofitClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

public class ChatBotController {

    @FXML private VBox chatBox;
    @FXML private TextField userInput;
    @FXML private Button sendButton;
    @FXML private ScrollPane scrollPane;
    @FXML private Button bookButton;
    @FXML private VBox bookingOptions;
    @FXML private ComboBox<String> doctorComboBox;
    @FXML private Button confirmBookingButton;
    @FXML private Button backButton;

    private final ChatBotApi chatbotApi = RetrofitClient.getInstance().create(ChatBotApi.class);

    @FXML
    public void initialize() {
        // Populate the doctor ComboBox with the same doctors as in FeedbackController
        doctorComboBox.getItems().addAll(
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
    public void sendMessage() {
        String userText = userInput.getText();
        if (userText.isEmpty()) return;

        // Add user message to chat
        Label userMessage = new Label("You: " + userText);
        chatBox.getChildren().add(userMessage);

        // Show "thinking..." message
        Label botThinking = new Label("Care Compass is analyzing...");
        Platform.runLater(() -> chatBox.getChildren().add(botThinking));

        userInput.clear();

        PromptRequestDto prompt = new PromptRequestDto(userText);

        // Check if the message qualifies for a hardcoded response
        String userTextLower = userText.toLowerCase();
        boolean isHardcodedResponse = userTextLower.contains("who are you") ||
                userTextLower.contains("about you") ||
                userTextLower.contains("what is Care Compass") ||
                userTextLower.contains("what can you do");

        ChatBotApi api = RetrofitClient.getInstance().create(ChatBotApi.class);

        if (isHardcodedResponse) {
            // Call the synchronous /api/chat endpoint for hardcoded responses
            Call<String> chatCall = api.getChatbotResponse(prompt);
            chatCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Platform.runLater(() -> {
                        chatBox.getChildren().remove(botThinking);
                        if (response.isSuccessful()) {
                            String botReply = response.body();
                            chatBox.getChildren().add(new Label("Bot: " + botReply));
                        } else {
                            chatBox.getChildren().add(new Label("Bot: Failed to get response. Error: " + response.code()));
                        }
                        scrollPane.setVvalue(1.0);
                    });
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Platform.runLater(() -> {
                        chatBox.getChildren().remove(botThinking);
                        chatBox.getChildren().add(new Label("Bot: Request failed. " + t.getMessage()));
                        scrollPane.setVvalue(1.0);
                    });
                }
            });
        } else {
            // Use polling for non-hardcoded responses (e.g., DeepSeek API calls)
            Call<Map<String, String>> startCall = api.startChat(prompt);
            startCall.enqueue(new Callback<Map<String, String>>() {
                @Override
                public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                    if (response.isSuccessful()) {
                        // Extract the requestId from the JSON response
                        Map<String, String> responseBody = response.body();
                        String requestId = responseBody.get("requestId");
                        if (requestId != null) {
                            pollForResult(api, requestId, botThinking, 60);
                        } else {
                            Platform.runLater(() -> {
                                chatBox.getChildren().remove(botThinking);
                                chatBox.getChildren().add(new Label("Bot: Failed to get requestId from response."));
                            });
                        }
                    } else {
                        Platform.runLater(() -> {
                            chatBox.getChildren().remove(botThinking);
                            chatBox.getChildren().add(new Label("Bot: Failed to send message. Error: " + response.code()));
                        });
                    }
                }

                @Override
                public void onFailure(Call<Map<String, String>> call, Throwable t) {
                    Platform.runLater(() -> {
                        chatBox.getChildren().remove(botThinking);
                        chatBox.getChildren().add(new Label("Bot: Request failed. " + t.getMessage()));
                    });
                }
            });
        }
    }

    private void pollForResult(ChatBotApi api, String requestId, Label botThinking, int maxAttempts) {
        new Thread(() -> {
            int attempts = 0;
            while (attempts < maxAttempts) {
                try {
                    Thread.sleep(1000); // Poll every 1 second
                    Call<Map<String, String>> statusCall = api.getChatStatus(requestId);
                    Response<Map<String, String>> statusResponse = statusCall.execute();

                    if (statusResponse.isSuccessful()) {
                        Map<String, String> responseBody = statusResponse.body();
                        String botReply = responseBody.get("response");
                        if (botReply != null && !botReply.equals("Processing...")) {
                            final String trimmedReply = botReply.trim();
                            Platform.runLater(() -> {
                                chatBox.getChildren().remove(botThinking);
                                Label responseLabel = new Label("Bot: " + trimmedReply);
                                responseLabel.setWrapText(true);
                                chatBox.getChildren().add(responseLabel);
                                scrollPane.setVvalue(1.0);
                            });
                            return;
                        }
                    } else {
                        Platform.runLater(() -> {
                            chatBox.getChildren().remove(botThinking);
                            Label errorLabel = new Label("Bot: Error - " + statusResponse.code());
                            errorLabel.setWrapText(true);
                            chatBox.getChildren().add(errorLabel);
                        });
                        return;
                    }
                    attempts++;
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        chatBox.getChildren().remove(botThinking);
                        Label errorLabel = new Label("Bot: Polling failed. " + e.getMessage());
                        errorLabel.setWrapText(true);
                        chatBox.getChildren().add(errorLabel);
                    });
                    return;
                }
            }
            Platform.runLater(() -> {
                chatBox.getChildren().remove(botThinking);
                Label timeoutLabel = new Label("Bot: Request timed out. Please try again.");
                timeoutLabel.setWrapText(true);
                chatBox.getChildren().add(timeoutLabel);
            });
        }).start();
    }

    private void addMessage(String message) {
        Label label = new Label(message);
        label.setWrapText(true);
        chatBox.getChildren().add(label);
        scrollPane.setVvalue(1.0);
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

    @FXML
    public void showBookingOptions() {
        bookingOptions.setVisible(true);
        bookingOptions.setManaged(true);
        bookButton.setVisible(false);
        bookButton.setManaged(false);
    }

    @FXML
    public void confirmBooking() {
        String selectedDoctor = doctorComboBox.getValue();
        if (selectedDoctor == null) {
            showAlert("Error", "Please select a doctor.");
            return;
        }

        // Generate a random time between 9 AM and 5 PM
        Random random = new Random();
        int hour = 9 + random.nextInt(8); // 9 AM to 4 PM (so we can add minutes and stay within 5 PM)
        int minute = random.nextInt(60);
        String time = String.format("%02d:%02d %s", hour > 12 ? hour - 12 : hour, minute, hour >= 12 ? "PM" : "AM");

        // Show confirmation alert
        showAlert("Booking Confirmed", "You are scheduled to meet " + selectedDoctor + " at " + time + ".");

        // Hide booking options and show the book button again
        bookingOptions.setVisible(false);
        bookingOptions.setManaged(false);
        bookButton.setVisible(true);
        bookButton.setManaged(true);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}