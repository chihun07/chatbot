package com.chihun.chatbot.temi;

public class TemiCommandManager {

    // Temi 명령인지 여부를 판별하는 메서드
    public static boolean isTemiCommand(String text) {
        // 예: 특정 키워드로 판단
        return text != null && (text.toLowerCase().contains("go to") || text.toLowerCase().contains("move to"));
    }

    // Temi 명령을 처리하고 응답 텍스트를 반환하는 메서드
    public static String handleTemiCommand(String command) {
        // 실제 Temi 이동 명령 처리 로직은 추후 구현
        // 현재는 더미 응답으로 처리
        return "[Temi] 이동 명령을 수행했습니다: " + command;
    }
}
