package com.chihun.chatbot.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.chihun.chatbot.data.model.ChatMessage;
import com.chihun.chatbot.data.model.MessageType;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.*;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GeminiRepository {

    private static final String TAG = "GeminiRepository";

    private final GeminiApiService apiService;
    private final Gson gson = new Gson();
    private final String apiKey = ;

    public GeminiRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://generativelanguage.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(GeminiApiService.class);
    }

    public void sendMessage(String prompt, MessageType type, GeminiCallback callback) {
        if (type == MessageType.COMMAND) {
            callback.onError("Temi 명령은 Gemini API를 호출하지 않습니다.");
            return;
        }

        Log.d(TAG, "\uD83D\uDCE4 sendMessage 호출됨");
        Log.d(TAG, "입력 메시지: " + prompt);

        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);

        Map<String, Object> userPart = new HashMap<>();
        userPart.put("role", "user");
        userPart.put("parts", Collections.singletonList(part));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", Collections.singletonList(userPart));

        Log.d(TAG, "\uD83C\uDF10 API 요청 준비 완료: " + requestBody.toString());

        String modelId = "models/gemini-1.5-pro:generateContent";

        apiService.sendPrompt(modelId, apiKey, requestBody).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Object>> call, @NonNull Response<Map<String, Object>> response) {
                Log.d(TAG, "✅ onResponse 호출됨");
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.body().get("candidates");
                        if (candidates != null && !candidates.isEmpty()) {
                            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                            if (parts != null && !parts.isEmpty()) {
                                String text = (String) parts.get(0).get("text");

                                // ✅ ChatMessage 생성 및 JSON 변환
                                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
                                ChatMessage chatMessage = new ChatMessage(text, false, timestamp, MessageType.TEXT);

                                // role 필드 추가 포함된 구조로 Map 생성
                                Map<String, Object> jsonMap = new LinkedHashMap<>();
                                jsonMap.put("role", "bot");
                                jsonMap.put("message", chatMessage.getMessage());
                                jsonMap.put("type", chatMessage.getMessageType().name());
                                jsonMap.put("timestamp", chatMessage.getTimestamp());

                                String json = gson.toJson(jsonMap);
                                Log.d(TAG, "✅ Gemini 응답 JSON: " + json);
                                callback.onSuccess(json);
                                return;
                            }
                        }
                        callback.onError("응답 파싱 실패");
                    } catch (Exception e) {
                        callback.onError("예외 발생: " + e.getMessage());
                    }
                } else {
                    Log.e(TAG, "❌ API 호출 실패 - 상태 코드: " + response.code());
                    callback.onError("API 호출 실패: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Object>> call, @NonNull Throwable t) {
                Log.e(TAG, "❌ 네트워크 오류: " + t.getMessage());
                callback.onError("네트워크 오류: " + t.getMessage());
            }
        });
    }

    public interface GeminiCallback {
        void onSuccess(String json);
        void onError(String errorMessage);
    }
}