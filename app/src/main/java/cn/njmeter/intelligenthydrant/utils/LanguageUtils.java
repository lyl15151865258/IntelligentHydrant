package cn.njmeter.intelligenthydrant.utils;

/**
 * 软件语言设置工具
 * Created by LiYuliang on 2018/1/11.
 *
 * @author LiYuliang
 * @version 2018/1/11
 */

public class LanguageUtils {

    public static void setLanguageLocal(String language) {
        SharedPreferencesUtils.getInstance().saveData("language", language);
    }

    public static String getLanguageLocal() {
        return (String) SharedPreferencesUtils.getInstance().getData("language", "");
    }
}
