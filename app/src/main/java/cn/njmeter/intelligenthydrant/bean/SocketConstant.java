package cn.njmeter.intelligenthydrant.bean;

/**
 * 智慧水务平台Socket通信固定值
 * Created by LiYuliang on 2018/03/30.
 *
 * @author LiYuliang
 * @version 2018/03/30
 */

public class SocketConstant {
    /**
     * 安卓登陆标记
     */
    public static final String ANDROID_LOGIN = "*#ANDROIDLOGIN:";
    /**
     * 安卓指令开始标记
     */
    public static final String ANDROID_CMD = "*#ADNROIDCMD:";
    /**
     * 安卓数据结束
     */
    public static final String ANDROID_END = "END#*";
    /**
     * 设备不存在
     */
    public static final String DEVICE_IS_NOT_EXIST = "0";
    /**
     * 设备不在线
     */
    public static final String DEVICE_IS_OFFLINE = "1";
}
