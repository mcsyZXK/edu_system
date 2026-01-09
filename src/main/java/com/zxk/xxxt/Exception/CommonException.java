package com.zxk.xxxt.Exception;

/**
 * @author zwj
 * @create 2022-04-18-11:43
 */
public class CommonException extends RuntimeException{

    private String msg;

    public CommonException(String msg) {
        this.msg = msg;
    }
}
