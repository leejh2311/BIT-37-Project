package com.bit37.team6.login;

public class LoginDTO {
    private String userId;
    private String userPw;

    public LoginDTO() {
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserPw() {
        return userPw;
    }
    public void setUserPw(String userPw) {
        this.userPw = userPw;
    }
}