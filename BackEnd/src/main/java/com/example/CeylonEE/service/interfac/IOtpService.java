package com.example.CeylonEE.service.interfac;

public interface IOtpService {

    String generateOtp();
    void sendOtpEmail(String email, String otp);
    void saveOtp(String email, String otp);
    boolean verifyOtp(String email, String otp);
    void clearOtp(String email);
}
