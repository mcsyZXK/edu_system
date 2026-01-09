package com.zxk.xxxt.Utils;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;

public class JsonUtils {

    public static JSONObject success(){
        JSONObject map = new JSONObject();
        map.put("errcode",0);
        map.put("errmsg","成功");
        map.put("data","");
        return map;
    }

    public static JSONObject success(Object data){
        JSONObject map = new JSONObject();
        map.put("errcode",0);
        map.put("errmsg","成功");
        map.put("data",data);
        return map;
    }

    public static JSONObject success(Object data,String msg){
        JSONObject map = new JSONObject();
        map.put("errcode",0);
        map.put("errmsg",msg);
        map.put("data",data);
        return map;
    }

    public static JSONObject fail(Integer code, String msg){
        JSONObject map = new JSONObject();
        map.put("errcode",code);
        map.put("errmsg",msg);
        map.put("data","");
        return map;
    }

    public static JSONObject fail(String msg){
        JSONObject map = new JSONObject();
        map.put("errcode",1001);
        map.put("errmsg",msg);
        map.put("data","");
        return map;
    }

    public static JSONObject successPage(IPage iPage){
        JSONObject map = new JSONObject();
        map.put("errcode",0);
        map.put("errmsg","成功");
        map.put("list",iPage.getRecords());
        map.put("total",iPage.getTotal());
        map.put("size",iPage.getSize());
        return map;
    }

    public static JSONObject success(JSONObject map,IPage iPage){
        map.put("errcode",0);
        map.put("errmsg","成功");
        map.put("list",iPage.getRecords());
        map.put("total",iPage.getTotal());
        map.put("size",iPage.getSize());
        return map;
    }

    public static JSONObject token(Object data,String msg,Integer code){
        JSONObject map = new JSONObject();
        map.put("errcode",code);
        map.put("errmsg",msg);
        map.put("data",data);
        return map;
    }
}
