package com.chihun.chatbot.data.model;

import java.io.Serializable;

public class ChatMessage implements Serializable {

    private String message;
    private boolean isUser;
    private String timestamp;
    private MessageType messageType;  // ✅ 외부 enum

    public ChatMessage() {}

    public ChatMessage(String message, boolean isUser, String timestamp, MessageType messageType) {
        this.message = message;
        this.isUser = isUser;
        this.timestamp = timestamp;
        this.messageType = messageType;
    }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public boolean isUser() { return isUser; }

    public void setUser(boolean user) { isUser = user; }

    public String getTimestamp() { return timestamp; }

    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public MessageType getMessageType() { return messageType; }

    public void setMessageType(MessageType messageType) { this.messageType = messageType; }

    public String getText() { return getMessage(); }

    public MessageType getType() { return getMessageType(); }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "message='" + message + '\'' +
                ", isUser=" + isUser +
                ", timestamp='" + timestamp + '\'' +
                ", messageType=" + messageType +
                '}';
    }
}
