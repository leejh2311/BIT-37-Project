package com.bit37.team6.register;

public class RegisterDTO {
    private String userId;
    private String userPw;

    public RegisterDTO() {
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
