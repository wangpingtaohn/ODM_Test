package com.quectel.agingtest.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

public class GsonUtils {
    private static final Gson filterNullGson;
    private static final Gson nullableGson;
    static {
        nullableGson = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .serializeNulls()
                .setDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
                .create();
        filterNullGson = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .setDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
                .create();
    }

    protected GsonUtils() {
    }

    /**
     * 根据对象返回json   不过滤空值字段
     */
    public static String toJsonWithNullField(Object obj){
        return nullableGson.toJson(obj);
    }

    /**
     * 根据对象返回json  过滤空值字段
     */
    public static String toJsonFilterNullField(Object obj){
        return filterNullGson.toJson(obj);
    }

    /**
     * 将json转化为对应的实体对象
     * new TypeToken<HashMap<String, Object>>(){}.getType()
     */
    public static <T>  T fromJson(String json, Type type){
        return nullableGson.fromJson(json, type);
    }

    /**
     * 将对象值赋值给目标对象
     * @param source 源对象
     * @param <T> 目标对象类型
     * @return 目标对象实例
     */
    public static <T> T convert(Object source, Class<T> clz){
        String json = GsonUtils.toJsonFilterNullField(source);
        return GsonUtils.fromJson(json, clz);
    }
}

