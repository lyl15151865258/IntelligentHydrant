package cn.njmeter.intelligenthydrant;

import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.baidu.mapapi.SDKInitializer;

import java.util.ArrayList;
import java.util.List;

import cn.njmeter.intelligenthydrant.loginregister.bean.ClientUser;
import cn.njmeter.intelligenthydrant.service.LocationService;
import cn.njmeter.intelligenthydrant.sqlite.DbHelper;
import cn.njmeter.intelligenthydrant.utils.CrashHandler;
import cn.njmeter.intelligenthydrant.utils.GsonUtils;
import cn.njmeter.intelligenthydrant.utils.LogUtils;
import cn.njmeter.intelligenthydrant.utils.MyLifecycleHandler;
import cn.njmeter.intelligenthydrant.utils.SharedPreferencesUtils;

/**
 * Application类
 * Created by Li Yuliang on 2017/03/01.
 *
 * @author LiYuliang
 * @version 2018/02/24
 */

public class HydrantApplication extends MultiDexApplication {

    private static HydrantApplication instance;

    private Context mContext;
    /**
     * 全局数据库操作类
     */
    private DbHelper mDbHelper;
    private ClientUser.Account account;
    private ClientUser.Version version;
    private ClientUser.Version2 version2;
    public List<ClientUser.Server> serverList;
    public LocationService locationService;
    public static boolean loginSuccess;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d("StartTime", "Application启动");
        mContext = getApplicationContext();
        instance = this;
        loginSuccess = false;
        // 捕捉异常
        CrashHandler.getInstance().init(this);
        //初始化SharedPreferences工具
        SharedPreferencesUtils.init(this);
        //百度地图初始化
        SDKInitializer.initialize(this);
        locationService = new LocationService(getApplicationContext());
        serverList = new ArrayList<>();
        //注册Activity生命周期回调
        registerActivityLifecycleCallbacks(new MyLifecycleHandler());

        // android 7.0系统解决拍照的问题
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();
        }
        LogUtils.d("StartTime", "Application启动完毕");
    }

    /**
     * 单例模式中获取唯一的MyApplication实例
     *
     * @return application实例
     */
    public static HydrantApplication getInstance() {
        if (instance == null) {
            instance = new HydrantApplication();
        }
        return instance;
    }

    public ClientUser.Account getAccount() {
        if (account != null) {
            return account;
        } else if (loginSuccess) {
            String account = (String) SharedPreferencesUtils.getInstance().getData("account", "");
            if (TextUtils.isEmpty(account)) {
                return null;
            } else {
                ClientUser clientUser = GsonUtils.parseJSON(account, ClientUser.class);
                return clientUser.getAccount();
            }
        } else {
            return null;
        }
    }

    public ClientUser.Version getVersion() {
        if (version != null) {
            return version;
        } else if (loginSuccess) {
            String account = (String) SharedPreferencesUtils.getInstance().getData("account", "");
            if (TextUtils.isEmpty(account)) {
                return null;
            } else {
                ClientUser clientUser = GsonUtils.parseJSON(account, ClientUser.class);
                return clientUser.getVersion();
            }
        } else {
            return null;
        }
    }

    public ClientUser.Version2 getVersion2() {
        if (version2 != null) {
            return version2;
        } else if (loginSuccess) {
            String account = (String) SharedPreferencesUtils.getInstance().getData("account", "");
            if (TextUtils.isEmpty(account)) {
                return null;
            } else {
                ClientUser clientUser = GsonUtils.parseJSON(account, ClientUser.class);
                return clientUser.getVersion2();
            }
        } else {
            return null;
        }
    }

    public void setAccount(ClientUser.Account account) {
        this.account = account;
    }

    public void setVersion(ClientUser.Version version) {
        this.version = version;
    }

    public void setVersion2(ClientUser.Version2 version2) {
        this.version2 = version2;
    }

    public DbHelper getmDbHelper() {
        if (mDbHelper == null) {
            mDbHelper = new DbHelper(mContext);
            mDbHelper.getDBHelper();
            mDbHelper.open();
        }
        return mDbHelper;
    }

    public void setmDbHelper(DbHelper dbHelper) {
        mDbHelper = dbHelper;
    }

}
