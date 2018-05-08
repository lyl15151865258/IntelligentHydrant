package cn.njmeter.intelligenthydrant.bean;

/**
 * 通讯方式实体类
 * Created by LiYuliang on 2018/04/11.
 *
 * @author LiYuliang
 * @version 2018/04/11
 */

public class Contacts {

    private String name;

    private String phoneNumber;

    public Contacts(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
