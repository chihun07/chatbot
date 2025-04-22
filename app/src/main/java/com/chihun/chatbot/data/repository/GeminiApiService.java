package com.chihun.chatbot.data.repository;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GeminiApiService {

    @POST("v1beta/{model}")
    Call<Map<String, Object>> sendPrompt(
            @Path(value = "model", encoded = true) String model,
            @Query("key") String apiKey,
            @Body Map<String, Object> requestBody
    );
}
