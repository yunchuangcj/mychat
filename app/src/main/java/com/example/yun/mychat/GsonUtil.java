package com.example.yun.mychat;

import com.google.gson.Gson;

/**
 * Created by Yun on 2016/11/17.
 */

public class GsonUtil {
    public static <T> T jsonToBean(String jsonResult, Class<T> clz) {
        Gson gson = new Gson();
        T t = gson.fromJson(jsonResult, clz);
        return t;
    }

    //将一个javaBean生成对应的Json数据
    public static String beanToJson(Object obj) {
        Gson gson = new Gson();
        String json = gson.toJson(obj);
        return json;
    }
}
