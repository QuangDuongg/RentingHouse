package com.example.swipe.Message;

public class Message {
    private String senderId;
    private String message;
    private long timestamp;
    private String profileImageUrl;  // Thêm thuộc tính để lưu URL avatar

    public Message() {
        // Default constructor required for calls to DataSnapshot.getValue(Message.class)
    }

    public Message(String senderId, String message, long timestamp, String profileImageUrl) {
        this.senderId = senderId;
        this.message = message;
        this.timestamp = timestamp;
        this.profileImageUrl = profileImageUrl;  // Gán giá trị cho profileImageUrl
    }

    public String getSenderId() {
        return senderId;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;  // Getter cho profileImageUrl
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;  // Setter cho profileImageUrl
    }
}
