package com.zxk.xxxt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zxk.xxxt.POJO.VerifyCode;
import com.zxk.xxxt.Utils.EmailUtil;
import com.zxk.xxxt.mapper.VerifyCodeMapper;
import com.zxk.xxxt.service.VerifyCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VerifyCodeServiceImpl implements VerifyCodeService {
        @Autowired
        private VerifyCodeMapper verifyCodeMapper;
        @Autowired
        private EmailUtil emailUtil;

        /**
         * 发送验证码
         */
        public boolean sendCode(String email) {
            String code = emailUtil.generateCode();
            boolean b = emailUtil.sendVerificationCode(email,code);
            if(!b){
                return false;
            }
            VerifyCode verifyCode = new VerifyCode();
            verifyCode.setCode(code);
            verifyCode.setEmail(email);
            int insert = verifyCodeMapper.insertSelective(verifyCode);

            return insert > 0;
        }
        /**
         * 验证验证码
         */
        public boolean verifyCode(String email, String inputCode) {
            // 查询5分钟内所有未过期的验证码
            QueryWrapper<VerifyCode> wrapper = new QueryWrapper<>();
            wrapper.eq("email", email)
                    .apply("created_at > DATE_SUB(NOW(), INTERVAL 5 MINUTE)")
                    .orderByDesc("created_at");
            List<VerifyCode> codes = verifyCodeMapper.selectList(wrapper);
            // 检查输入是否匹配任何一个
            for (VerifyCode code : codes) {
                if (code.getCode().equals(inputCode) && !code.isExpired()) {
                    return true;
                }
            }
            return false;
        }
        /**
         * 检查发送频率
         */
        public boolean checkFrequency(String email) {
            LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
            int count = verifyCodeMapper.countByEmailAfterTime(email,oneMinuteAgo);
            return count < 3; // 1分钟内最多发送3次
        }

    }

