package com.example.swipe.Message;

public class User {
    private String userId;
    private String username;
    private String email;
    private String profileImageUrl;  // Thêm thuộc tính này để lưu trữ URL ảnh đại diện

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String userId, String username, String email, String profileImageUrl) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.profileImageUrl = profileImageUrl;  // Gán giá trị cho profileImageUrl
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;  // Getter cho profileImageUrl
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;  // Setter cho profileImageUrl
    }
}
