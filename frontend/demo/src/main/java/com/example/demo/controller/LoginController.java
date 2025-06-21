package com.example.demo.controller;

import com.example.demo.model.LoginResponse;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.util.Duration;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.example.demo.api.AuthApi;
import com.example.demo.api.RetrofitClient;
import com.example.demo.model.LoginDto;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import javafx.scene.paint.Color;

import java.io.IOException;


public class LoginController {

    @FXML
    private TextField email;

    @FXML
    private PasswordField password;

    @FXML
    private Label loginMessage;

    private final AuthApi authService;

    public LoginController() {
        authService = RetrofitClient.getInstance().create(AuthApi.class);
    }

    @FXML
    private void goReg(ActionEvent event) {
        try {
            Parent regRoot = FXMLLoader.load(getClass().getResource("/com/example/demo/register.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(regRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void loginUser() {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(email.getText());
        loginDto.setPassword(password.getText());

        Call<LoginResponse> call = authService.loginUser(loginDto);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Platform.runLater(() -> {
                    if (response.isSuccessful()) {
                        LoginResponse loginResponse = response.body();


                        loginMessage.setTextFill(Color.GREEN);
                        loginMessage.setText("Login successful");

                        PauseTransition delay = new PauseTransition(Duration.seconds(3));
                        delay.setOnFinished(event -> {
                            try {
                                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/demo/hello-view.fxml"));
                                Parent root = fxmlLoader.load();
                                Stage stage = (Stage) loginMessage.getScene().getWindow();
                                stage.setScene(new Scene(root));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                        delay.play();
                    } else {
                        loginMessage.setTextFill(Color.RED);
                        loginMessage.setText("Invalid email or password");
                    }
                });
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Platform.runLater(() -> {
                    loginMessage.setTextFill(Color.RED);
                    loginMessage.setText("Login failed. Try again later.");
                });
            }
        });
    }

}

