package cn.njmeter.intelligenthydrant.utils;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * Gson解析json工具类
 * Created by LiYuliang on 2017/6/5 0005.
 *
 * @author LiYuliang
 * @version 2017/10/27
 */

public final class GsonUtils {

    /**
     * 解析json字符串
     *
     * @param json
     * @param classT
     * @return
     */
    public static <T> T parseJSON(String json, Class<T> classT) {
        T info = null;
        try {
            Gson gson = new Gson();
            info = gson.fromJson(json, classT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    /**
     * 解析json数组
     * <p>
     * Type所在的包：java.lang.reflect
     * TypeToken所在的包：com.google.gson.reflect.TypeToken
     * Type type = new TypeToken<ArrayList<TypeInfo>>(){}.getType();
     * List<TypeInfo> types = GsonUtils.parseJSONArray(jsonArr, type);
     *
     * @param jsonArr
     * @param type
     * @return
     */
    public static <T> T parseJSONArray(String jsonArr, Type type) {
        T infos = null;
        try {
            Gson gson = new Gson();
            infos = gson.fromJson(jsonArr, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return infos;
    }

    public static String convertJSON(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

}
