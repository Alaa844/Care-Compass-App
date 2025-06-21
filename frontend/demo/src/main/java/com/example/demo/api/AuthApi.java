package com.example.demo.api;

import com.example.demo.model.LoginDto;
import com.example.demo.model.LoginResponse;
import com.example.demo.model.UserDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {
    @POST("auth/register")
    Call<String> registerUser(@Body UserDto userDto);

    @POST("/api/auth/login")
    Call<LoginResponse> loginUser(@Body LoginDto loginDto);

}


