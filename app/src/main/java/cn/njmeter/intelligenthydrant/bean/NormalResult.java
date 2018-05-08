package cn.njmeter.intelligenthydrant.bean;

/**
 * 通用请求结果实体类
 * Created by LiYuliang on 2017/10/30.
 *
 * @author LiYuliang
 * @version 2017/10/30
 */

public class NormalResult {

    private String result;
    private String message;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
