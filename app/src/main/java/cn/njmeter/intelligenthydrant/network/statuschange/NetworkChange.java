package cn.njmeter.intelligenthydrant.network.statuschange;

import java.util.Observable;

/**
 * Created by ：Luo
 * <p>  被观察者类
 * Created Time ：2017/8/30
 */

public class NetworkChange extends Observable {

    private static NetworkChange instance = null;

    public static NetworkChange getInstance() {
        if (null == instance) {
            instance = new NetworkChange();
        }
        return instance;
    }

    //通知观察者数据改变
    public void notifyDataChange(NetworkState net) {
        //被观察者怎么通知观察者数据有改变了呢？？这里的两个方法是关键。
        setChanged();
        notifyObservers(net);
    }

}
