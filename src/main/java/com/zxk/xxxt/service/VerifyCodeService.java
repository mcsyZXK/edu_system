package com.zxk.xxxt.service;

public interface VerifyCodeService {
    boolean sendCode(String email);
    boolean verifyCode(String email, String inputCode);
    boolean checkFrequency(String email);
}
