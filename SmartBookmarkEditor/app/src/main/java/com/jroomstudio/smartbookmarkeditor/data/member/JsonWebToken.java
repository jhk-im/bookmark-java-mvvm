package com.jroomstudio.smartbookmarkeditor.data.member;

import com.google.gson.annotations.SerializedName;

public class JsonWebToken {
    @SerializedName("message")
    private String message;
    @SerializedName("jwt")
    private String jwt;
    @SerializedName("email")
    private String email;
    @SerializedName("expireAt")
    private String expireAt;
    @SerializedName("name")
    private String name;

    // 생성자
    public JsonWebToken(String inputMessage, String inputJwt, String inputEmail, String inputExpireAt, String inputName){
        this.message = inputMessage;
        this.jwt = inputJwt;
        this.email = inputEmail;
        this.expireAt = inputExpireAt;
        this.name = inputName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(String expireAt) {
        this.expireAt = expireAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
