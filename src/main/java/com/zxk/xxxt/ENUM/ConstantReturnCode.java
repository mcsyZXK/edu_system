package com.zxk.xxxt.ENUM;

/**
 * 返回状态信息枚举
 */
public enum ConstantReturnCode {

    OK(0,"ok"),
    DATA_SQL_ERROR(1001,"数据库异常"),
    DATA_NULL_ERROR(1001,"空指针异常"),
    DATA_GET_API_ERROR(1001,"获取接⼝数据失败"),
    DATA_INTERNAL_ERROR(1001,"内部错误"),
    DATA_EXIST(1001,"数据已存在"),
    FILE_TYPE_ERROR(1001,"文件类型错误！"),
    PARAM_NULL_ERROR(1001,"参数为空"),
    PARAM_FILE_ERROR(1001,"导入文件有误"),
    PARAM_FILE_INFO_NULL_ERROR(1001,"导入文件数据为空"),
    AUTH_NOT_LOGGED_IN(2001,"未登录或token失效"),

    AUTH_TOKEN_OVERDUE(3001,"短token过期"),
    AUTH_USER_NOT_EXIST(1001,"用户不存在！"),
    AUTH_ERROR(1001,"当前用户没有权限!"),
    AUTH_ACCOUNT_LOCKED(1001,"账户异常，请重新登录!");

    private Integer code;
    private String name;

    private ConstantReturnCode(Integer code, String name){
        this.code=code;
        this.name=name;
    }

    public Integer getCode(){
        return this.code;
    }

    public String getName(){
        return this.name;
    }
}
