package com.example.demo.controller;

import com.example.demo.api.AuthApi;
import com.example.demo.api.RetrofitClient;
import com.example.demo.model.UserDto;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import javafx.scene.paint.Color;
import java.io.IOException;


public class RegisterController {

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField email;

    @FXML
    private Label regMessage;

    @FXML
    private PasswordField password;

    private AuthApi authService;

    public RegisterController() {
        // Use RetrofitClient to get the Retrofit instance
        Retrofit retrofit = RetrofitClient.getInstance();
        authService = retrofit.create(AuthApi.class);  // Initialize AuthApi with the Retrofit instance
    }

    @FXML
    private void goLogin(ActionEvent event) {
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/com/example/demo/login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(loginRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void regUser() {
        // Create the UserDto from user input
        UserDto user = new UserDto();
        user.setFirstName(firstName.getText());
        user.setLastName(lastName.getText());
        user.setEmail(email.getText());
        user.setPassword(password.getText());

        // Call the registerUser method from AuthApi
        Call<String> call = authService.registerUser(user);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Platform.runLater(() -> {
                    if (response.isSuccessful()) {
                        regMessage.setTextFill(Color.GREEN);
                        regMessage.setText(response.body());

                        PauseTransition delay = new PauseTransition(Duration.seconds(3));
                        delay.setOnFinished(event -> {
                            try {
                                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/demo/login.fxml"));
                                Stage stage = (Stage) regMessage.getScene().getWindow();
                                Scene scene = new Scene(fxmlLoader.load());
                                stage.setScene(scene);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                        delay.play();

                    } else {
                        regMessage.setTextFill(Color.RED);
                        regMessage.setText("Registration failed, Email already exists.");
                    }
                });
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Platform.runLater(() -> {
                    regMessage.setTextFill(Color.RED);
                    regMessage.setText("Error: " + t.getMessage());
                });
            }
        });
    }
}

