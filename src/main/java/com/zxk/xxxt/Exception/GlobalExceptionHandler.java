package com.zxk.xxxt.Exception;

import com.alibaba.fastjson.JSONObject;
import com.zxk.xxxt.ENUM.ConstantReturnCode;
import com.zxk.xxxt.Utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;
import org.apache.commons.lang3.StringUtils;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@ControllerAdvice
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = SQLException.class)
    @ResponseBody
    public com.alibaba.fastjson.JSONObject handle(SQLException e) {

        e.printStackTrace();
        // 存储error日志
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        log.error(sw.toString());

        return JsonUtils.fail(ConstantReturnCode.DATA_SQL_ERROR.getCode(), ConstantReturnCode.DATA_SQL_ERROR.getName());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public JSONObject jsonParamsException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        List errorList = new ArrayList();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            String msg = String.format("%s%s；", fieldError.getField(), fieldError.getDefaultMessage());
            errorList.add(msg);
        }
        return JsonUtils.fail(ConstantReturnCode.DATA_INTERNAL_ERROR.getCode(), StringUtils.join(errorList,";"));
    }

    @ExceptionHandler(value = NullPointerException.class)
    @ResponseBody
    public com.alibaba.fastjson.JSONObject handle(NullPointerException e) {

        e.printStackTrace();
        // 存储error日志
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        log.error(sw.toString());

        return JsonUtils.fail(ConstantReturnCode.DATA_NULL_ERROR.getCode(), ConstantReturnCode.DATA_NULL_ERROR.getName());
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public com.alibaba.fastjson.JSONObject haandle(Exception e) {

        e.printStackTrace();
        // 存储error日志
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        log.error(sw.toString());

        return JsonUtils.fail(ConstantReturnCode.DATA_INTERNAL_ERROR.getCode(), ConstantReturnCode.DATA_INTERNAL_ERROR.getName());
    }

    /**
     * 内部错误
     * @param e
     * @return
     */
    @ExceptionHandler(value = CommonException.class)
    @ResponseBody
    public com.alibaba.fastjson.JSONObject haandleCommonException(CommonException e) {

        e.printStackTrace();
        // 存储error日志
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        log.error(sw.toString());

        return JsonUtils.fail(ConstantReturnCode.DATA_INTERNAL_ERROR.getCode(), ConstantReturnCode.DATA_INTERNAL_ERROR.getName());
    }

    /**
     * 没有权限错误
     * @param e
     * @return
     */
    @ExceptionHandler(value = AccessDeniedException.class)
    @ResponseBody
    public com.alibaba.fastjson.JSONObject haandleCommonException(AccessDeniedException e) {

        e.printStackTrace();
        // 存储error日志
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        log.error(sw.toString());

        return JsonUtils.fail(ConstantReturnCode.AUTH_ERROR.getCode(), ConstantReturnCode.AUTH_ERROR.getName());
    }

    /**
     * 没有权限错误
     * @param e
     * @return
     */
    @ExceptionHandler(value = ServiceException.class)
    @ResponseBody
    public com.alibaba.fastjson.JSONObject haandleServiceException(ServiceException e) {

        e.printStackTrace();
        // 存储error日志
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        log.error(sw.toString());

        return JsonUtils.fail(e.getMessage());
    }

}
