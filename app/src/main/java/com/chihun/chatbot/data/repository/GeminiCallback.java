package com.chihun.chatbot.data.repository;

/**
 * Gemini API 통신 결과를 처리하기 위한 콜백 인터페이스
 */
public interface GeminiCallback {
    /**
     * Gemini 응답 성공 시 호출
     * @param response 응답 텍스트
     */
    void onSuccess(String response);

    /**
     * Gemini 응답 실패 시 호출
     * @param errorMessage 에러 메시지
     */
    void onError(String errorMessage);
}
