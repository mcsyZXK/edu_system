package com.zxk.xxxt.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Component
public class EmailUtil {
    @Autowired
    private JavaMailSender mailSender;

    /**
     * 生成6位随机验证码
     * @return
     */
    public String generateCode(){
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 6; i++){
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
    /**
     * 发送验证码邮件
     */
    @Transactional
    public boolean sendVerificationCode(String toEmail, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            String fromEmail = "1124127770@qq.COM";
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("留言板系统 - 邮箱验证码");
            message.setText(String.format(
                    "【留言板系统】\n\n" +
                            "您的验证码是：%s\n" +
                            "有效期5分钟，请及时使用。\n" +
                            "请勿泄露给他人。\n\n" +
                            "系统邮件，请勿回复。",
                    code
            ));
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
