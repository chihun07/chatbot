package com.chihun.chatbot.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.chihun.chatbot.data.model.ChatMessage;
import com.chihun.chatbot.data.model.MessageType;
import com.chihun.chatbot.data.repository.GeminiRepository;
import com.chihun.chatbot.temi.TemiCommandManager;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatViewModel extends ViewModel {

    private final GeminiRepository geminiRepository = new GeminiRepository();
    private final Gson gson = new Gson();

    private final MutableLiveData<List<ChatMessage>> messagesLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> jsonResponseLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public LiveData<List<ChatMessage>> getMessagesLiveData() {
        return messagesLiveData;
    }

    public LiveData<String> getJsonResponseLiveData() {
        return jsonResponseLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void sendJsonInput(String jsonInput) {
        try {
            ChatMessage message = gson.fromJson(jsonInput, ChatMessage.class);
            if (message == null || message.getMessage() == null) {
                errorLiveData.setValue("잘못된 JSON 메시지입니다.");
                return;
            }

            addMessage(message);

            if (message.getMessageType() == MessageType.COMMAND) {
                String result = TemiCommandManager.handleTemiCommand(message.getMessage());
                ChatMessage response = new ChatMessage(result, false, getCurrentTimestamp(), MessageType.TEXT);
                emitJsonResponse(response);
                addMessage(response);
                return;
            }

            isLoading.setValue(true);

            geminiRepository.sendMessage(message.getMessage(), MessageType.TEXT, new GeminiRepository.GeminiCallback() {
                @Override
                public void onSuccess(String response) {
                    String responseTimestamp = getCurrentTimestamp();
                    ChatMessage botMessage = new ChatMessage(response, false, responseTimestamp, MessageType.TEXT);
                    emitJsonResponse(botMessage);
                    addMessage(botMessage);
                    isLoading.postValue(false);
                }

                @Override
                public void onError(String errorMessage) {
                    String responseTimestamp = getCurrentTimestamp();
                    ChatMessage errorMsg = new ChatMessage("[에러] " + errorMessage, false, responseTimestamp, MessageType.TEXT);
                    emitJsonResponse(errorMsg);
                    addMessage(errorMsg);
                    isLoading.postValue(false);
                }
            });

        } catch (Exception e) {
            errorLiveData.setValue("JSON 파싱 오류: " + e.getMessage());
        }
    }

    private void addMessage(ChatMessage message) {
        List<ChatMessage> currentMessages = messagesLiveData.getValue();
        if (currentMessages == null) currentMessages = new ArrayList<>();
        List<ChatMessage> updatedList = new ArrayList<>(currentMessages);
        updatedList.add(message);
        messagesLiveData.setValue(updatedList);
    }

    private void emitJsonResponse(ChatMessage message) {
        jsonResponseLiveData.setValue(gson.toJson(message));
    }

    private String getCurrentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }
    public void sendUserMessage(String text) {
        String timestamp = getCurrentTimestamp();

        // 텍스트 기반으로 Temi 명령 여부 판단
        MessageType type = TemiCommandManager.isTemiCommand(text) ? MessageType.COMMAND : MessageType.TEXT;

        ChatMessage userMessage = new ChatMessage(text, true, timestamp, type);
        addMessage(userMessage);

        if (type == MessageType.COMMAND) {
            // 👉 Temi 명령 처리
            String response = TemiCommandManager.handleTemiCommand(text);
            ChatMessage botMessage = new ChatMessage(response, false, getCurrentTimestamp(), MessageType.TEXT);
            addMessage(botMessage);
            emitJsonResponse(botMessage);
            return;
        }

        // 일반 Gemini 처리
        isLoading.setValue(true);

        geminiRepository.sendMessage(text, MessageType.TEXT, new GeminiRepository.GeminiCallback() {
            @Override
            public void onSuccess(String jsonResponse) {
                try {
                    ChatMessage botMessage = gson.fromJson(jsonResponse, ChatMessage.class);
                    if (botMessage != null) {
                        if (botMessage.getTimestamp() == null) {
                            botMessage.setTimestamp(getCurrentTimestamp());
                        }
                        botMessage.setUser(false);
                        addMessage(botMessage);
                        emitJsonResponse(botMessage);
                    } else {
                        errorLiveData.setValue("응답 파싱 실패: null 객체");
                    }
                } catch (Exception e) {
                    errorLiveData.setValue("JSON 파싱 오류: " + e.getMessage());
                } finally {
                    isLoading.postValue(false);
                }
            }

            @Override
            public void onError(String errorMessage) {
                ChatMessage errorMsg = new ChatMessage("[에러] " + errorMessage, false, getCurrentTimestamp(), MessageType.TEXT);
                addMessage(errorMsg);
                isLoading.postValue(false);
            }
        });
    }

}
