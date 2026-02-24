package com.nomadtrack.nomadtrackserver.model.dto;

public class LoginResponseDto {

    private String accessToken;
    private String tokenType;
    private long expiresInSeconds;

    public LoginResponseDto(String accessToken, long expiresInSeconds) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
        this.expiresInSeconds = expiresInSeconds;
    }

    public String getAccessToken() { return accessToken; }
    public String getTokenType() { return tokenType; }
    public long getExpiresInSeconds() { return expiresInSeconds; }
}