package cn.njmeter.intelligenthydrant.network.statuschange;

/**
 * Created by ：Luo
 * <p>  Network数据类
 * Created Time ：2017/8/30
 */

public class NetworkState {

    private boolean wifi;
    private boolean mobile;
    private boolean connected;

    public NetworkState() {
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public boolean isWifi() {
        return wifi;
    }

    public void setWifi(boolean wifi) {
        this.wifi = wifi;
    }

    public boolean isMobile() {
        return mobile;
    }

    public void setMobile(boolean mobile) {
        this.mobile = mobile;
    }

}
