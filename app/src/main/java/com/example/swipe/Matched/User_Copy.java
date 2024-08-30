package com.example.swipe.Matched;

public class User_Copy {
    private String userId;
    private String username;
    private String email;
    private String profileImageUrl;
    private String dob; // date of birth
    private String gender;
    private String role; // tenant or host

    public User_Copy(String userId, String username, String email, String profileImageUrl, String dob, String gender, String role) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.dob = dob;
        this.gender = gender;
        this.role = role;
    }

    public User_Copy(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    // Getters and setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
