package cn.njmeter.intelligenthydrant.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.utils.DistanceUtil;
import com.bumptech.glide.Glide;
import com.pkmmte.view.CircularImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;
import cn.njmeter.intelligenthydrant.BuildConfig;
import cn.njmeter.intelligenthydrant.HydrantApplication;
import cn.njmeter.intelligenthydrant.R;
import cn.njmeter.intelligenthydrant.bean.EventMsg;
import cn.njmeter.intelligenthydrant.bean.HydrantClusterItem;
import cn.njmeter.intelligenthydrant.bean.HydrantLastData;
import cn.njmeter.intelligenthydrant.bean.SocketConstant;
import cn.njmeter.intelligenthydrant.bean.WaterMeterLoginResult;
import cn.njmeter.intelligenthydrant.constant.ApkInfo;
import cn.njmeter.intelligenthydrant.constant.Constants;
import cn.njmeter.intelligenthydrant.constant.NetWork;
import cn.njmeter.intelligenthydrant.constant.ProductType;
import cn.njmeter.intelligenthydrant.loginregister.activity.LoginRegisterActivity;
import cn.njmeter.intelligenthydrant.loginregister.bean.ClientUser;
import cn.njmeter.intelligenthydrant.map.MyOrientationListener;
import cn.njmeter.intelligenthydrant.network.ExceptionHandle;
import cn.njmeter.intelligenthydrant.network.NetClient;
import cn.njmeter.intelligenthydrant.network.NetworkSubscriber;
import cn.njmeter.intelligenthydrant.network.bean.SocketPackage;
import cn.njmeter.intelligenthydrant.service.LocationService;
import cn.njmeter.intelligenthydrant.service.SocketService;
import cn.njmeter.intelligenthydrant.utils.ActivityController;
import cn.njmeter.intelligenthydrant.utils.AnalysisUtils;
import cn.njmeter.intelligenthydrant.utils.ApkUtils;
import cn.njmeter.intelligenthydrant.utils.CipherUtils;
import cn.njmeter.intelligenthydrant.utils.FileUtil;
import cn.njmeter.intelligenthydrant.utils.GsonUtils;
import cn.njmeter.intelligenthydrant.utils.LogUtils;
import cn.njmeter.intelligenthydrant.utils.MathUtils;
import cn.njmeter.intelligenthydrant.utils.MyLocationManager;
import cn.njmeter.intelligenthydrant.utils.NetworkUtil;
import cn.njmeter.intelligenthydrant.utils.NotificationsUtils;
import cn.njmeter.intelligenthydrant.utils.SharedPreferencesUtils;
import cn.njmeter.intelligenthydrant.utils.StatusBarUtil;
import cn.njmeter.intelligenthydrant.utils.StringUtils;
import cn.njmeter.intelligenthydrant.utils.TimeUtils;
import cn.njmeter.intelligenthydrant.utils.clusterutil.clustering.ClusterManager;
import cn.njmeter.intelligenthydrant.widget.dialog.CommonWarningDialog;
import cn.njmeter.intelligenthydrant.widget.dialog.DownLoadDialog;
import cn.njmeter.intelligenthydrant.widget.DownloadProgressBar;
import cn.njmeter.intelligenthydrant.widget.dialog.ReDownloadWarningDialog;
import cn.njmeter.intelligenthydrant.widget.dialog.UpgradeVersionDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    private Context mContext;
    private String serverHost, httpPort, serviceName, hieId, loginId;
    private DrawerLayout drawerLayout;
    private LinearLayout llMain, popupPOIDetails, llHydrantUsingStatus;
    private TextView tvAllHydrant, tvFireHydrant, tvOtherHydrant;
    private TextView tvHydrantType, tvHydrantId, tvHydrantStatus, tvHydrantDistance, tvHydrantAddress;
    private ImageView mDingWei, mRefresh, mKefu;
    private RelativeLayout mRefreshAll;
    private TextureMapView mMapView;
    private CircularImageView ivIcon, ivUserIcon;
    private TextView tvNickName, tvCompanyName, tvPosition;
    private Button btnExit;
    private BaiduMap mBaiduMap;
    private MapStatus mapStatus;

    private View btnBg;

    private List<HydrantClusterItem> hydrantClusterItemList;

    private MyLocationConfiguration.LocationMode mCurrentMode;
    private double currentLatitude, currentLongitude, changeLatitude, changeLongitude;
    private MyOrientationListener myOrientationListener;
    private float mCurrentX;
    private boolean hasPlanRoute = false, isServiceLive = false;
    private PlanNode startNodeStr, endNodeStr;
    private LatLng mCurrentLocation;
    private boolean isFirstLocation = true;

    private BDLocation mBDLocation;
    private GeoCoder mGeoCoder;
    private static final int HYDRANT_TYPE_ALL_HYDRANT = 0, HYDRANT_TYPE_FIRE_HYDRANT = 1, HYDRANT_TYPE_OTHER_HYDRANT = 2;
    private int CURRENT_HYDRANTTYPE = HYDRANT_TYPE_ALL_HYDRANT;

    private LocationService locationService;
    private ClusterManager<HydrantClusterItem> mClusterManager;
    private HydrantClusterItem mCurrentItem;
    private boolean shouldUseLocation = false;

    /**
     * 判断是否需要检查版本更新
     */
    public static boolean shouldCheckVersion = true;

    private String versionType, latestVersionName, versionFileName, latestVersionMD5, latestVersionLog, apkDownloadPath;
    private int myVersionCode, latestVersionCode;
    private DownloadProgressBar downloadProgressBar;
    private TextView tvUpdateLog, tvCompletedSize, tvTotalSize;
    private float apkSize, completedSize;

    private static final int REQUEST_CODE_CHOOSE_COMPANY = 1000, REQUEST_CODE_NOTIFICATION_SETTINGS = 1001, REQUEST_CODE_UNLOCK = 1002, REQUEST_CODE_ZOOM = 1003;

    /**
     * 线程池
     */
    public ExecutorService executorService;

    private ServiceConnection serviceConnection;
    public SocketService socketService;

    /**
     * Socket连接状态标记，用于发送消息时的判断
     */
    private boolean isConnectSuccess = false;

    private boolean mapLoadedFinish = false;
    private boolean hydrantAccountCorrect = false;

    private boolean isAdminAccount = true;

    private WaterMeterLoginResult loginResult;

    private LoginReceiver loginReceiver;

    private TextView tvOpenTime, tvConsumption, tvUsingStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hydrantClusterItemList = new ArrayList<>();

        mContext = this;
        initView();
        // 初始化百度地图
        initMapView();
        // 初始化定位相关
        initLocation();
        // 地理编码的初始化相关
        initGeoCoder();
        // 初始化点聚合管理
        initClusterManager();
        requestPermission();
        login();

        if (!NotificationsUtils.isNotificationEnabled(mContext)) {
            CommonWarningDialog commonWarningDialog = new CommonWarningDialog(mContext, getString(R.string.notification_open_notification));
            commonWarningDialog.setCancelable(false);
            commonWarningDialog.setOnDialogClickListener(new CommonWarningDialog.OnDialogClickListener() {
                @Override
                public void onOKClick() {
                    // 根据isOpened结果，判断是否需要提醒用户跳转AppInfo页面，去打开App通知权限
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, REQUEST_CODE_NOTIFICATION_SETTINGS);
                }

                @Override
                public void onCancelClick() {
                    showToast("未授予通知权限，部分功能不可使用");
                }
            });
            commonWarningDialog.show();
        }

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        executorService = new ThreadPoolExecutor(1, Runtime.getRuntime().availableProcessors(), 60, TimeUnit.SECONDS,
                new SynchronousQueue<>(), (r) -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        });

        loginReceiver = new LoginReceiver();
        registerReceiver(loginReceiver, new IntentFilter("login"));

        // 开启方向监听
        initOrientation();
    }

    @Override
    protected void setStatusBar() {
        int mColor = getResources().getColor(R.color.colorPrimary);
        StatusBarUtil.setColorForDrawerLayout(this, findViewById(R.id.drawer_layout), mColor);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    public class LoginReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            hydrantClusterItemList.clear();
            hidePoiDetails();
            shouldUseLocation = false;
            mClusterManager.clearItems();
            mBaiduMap.clear();
            initMapView();
            hieId = "";
            loginId = "";
            login();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initPage();
        mMapView.onResume();
        locationService.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        locationService.stop();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //关闭定位
        mBaiduMap.setMyLocationEnabled(false);
        //停止方向传感器
        myOrientationListener.stop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mBaiduMap.setMyLocationEnabled(true);
        myOrientationListener.start();
    }

    private void initView() {
        NavigationView navigation = findViewById(R.id.navigation);
        ViewGroup.LayoutParams params = navigation.getLayoutParams();
        params.width = mWidth * 4 / 5;
        navigation.setLayoutParams(params);

        drawerLayout = findViewById(R.id.drawer_layout);
        llMain = findViewById(R.id.ll_main);
        popupPOIDetails = findViewById(R.id.popupPOIDetails);
        tvAllHydrant = findViewById(R.id.tv_allHydrant);
        tvFireHydrant = findViewById(R.id.tv_fireHydrant);
        tvOtherHydrant = findViewById(R.id.tv_otherHydrant);
        mDingWei = findViewById(R.id.dingwei);
        mRefresh = findViewById(R.id.refresh);
        mRefreshAll = findViewById(R.id.refreshAll);
        mKefu = findViewById(R.id.kefu);

        tvAllHydrant.setOnClickListener(onClickListener);
        tvFireHydrant.setOnClickListener(onClickListener);
        tvOtherHydrant.setOnClickListener(onClickListener);
        mDingWei.setOnClickListener(onClickListener);
        mRefreshAll.setOnClickListener(onClickListener);
        mKefu.setOnClickListener(onClickListener);
        findViewById(R.id.scan_qrcode).setOnClickListener(onClickListener);

        tvHydrantType = findViewById(R.id.tvHydrantType);
        tvHydrantId = findViewById(R.id.tvHydrantId);
        tvHydrantStatus = findViewById(R.id.tvHydrantStatus);
        tvHydrantDistance = findViewById(R.id.tvHydrantDistance);
        tvHydrantAddress = findViewById(R.id.tvHydrantAddress);
        findViewById(R.id.btnNavigation).setOnClickListener(onClickListener);
        findViewById(R.id.btnOpen).setOnClickListener(onClickListener);
        findViewById(R.id.btnClose).setOnClickListener(onClickListener);

        mMapView = findViewById(R.id.bmapview);
        setMyClickable(tvAllHydrant);
        mBaiduMap = mMapView.getMap();

        ivIcon = findViewById(R.id.iv_icon);
        ivUserIcon = findViewById(R.id.iv_userIcon);

        ivIcon.setOnClickListener(onClickListener);

        tvNickName = findViewById(R.id.tv_nickName);
        tvCompanyName = findViewById(R.id.tv_companyName);
        tvPosition = findViewById(R.id.tv_position);

        btnBg = findViewById(R.id.btnBg);
        btnBg.setOnClickListener(onClickListener);

        (findViewById(R.id.ll_login)).setOnClickListener(onClickListener);
        (findViewById(R.id.ll_setMainAccount)).setOnClickListener(onClickListener);
        (findViewById(R.id.ll_setSubAccount)).setOnClickListener(onClickListener);
        (findViewById(R.id.ll_bindAccounts)).setOnClickListener(onClickListener);
        (findViewById(R.id.llMapZoomLevel)).setOnClickListener(onClickListener);
        (findViewById(R.id.llNavigationApp)).setOnClickListener(onClickListener);
        (findViewById(R.id.ll_update)).setOnClickListener(onClickListener);
        (findViewById(R.id.ll_version)).setOnClickListener(onClickListener);
        (findViewById(R.id.ll_share)).setOnClickListener(onClickListener);

        (findViewById(R.id.ivSearch)).setOnClickListener(onClickListener);
        (findViewById(R.id.ivMessage)).setOnClickListener(onClickListener);

        btnExit = findViewById(R.id.btn_exit);
        btnExit.setOnClickListener(onClickListener);

        llHydrantUsingStatus = findViewById(R.id.llHydrantUsingStatus);
        tvOpenTime = findViewById(R.id.tvOpenTime);
        tvConsumption = findViewById(R.id.tvConsumption);
        tvUsingStatus = findViewById(R.id.tvUsingStatus);

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                llMain.scrollTo(-(int) (navigation.getWidth() * slideOffset), 0);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    /**
     * 收到EventBus发来的消息并处理
     *
     * @param msg 消息对象
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessage(EventMsg msg) {
        if (msg.getTag().equals(Constants.CONNECT_SUCCESS_SOCKET)) {
            //接收到这个消息说明连接成功
            isConnectSuccess = true;
            bindSocketService();
        }
        if (msg.getTag().equals(Constants.CONNECT_FAIL_SOCKET)) {
            //接收到这个消息说明连接失败或者中断了
            isConnectSuccess = false;
            bindSocketService();
        }
        if (msg.getTag().equals(Constants.SHOW_TOAST_SOCKET)) {
            //接收到这个消息说明需要显示一个Toast
            showToast(msg.getMsg());
        }
        if (msg.getTag().equals(Constants.SHOW_DATA_SOCKET)) {
            //接收到这个消息说明需要处理数据
            //对消息的分包与解析处理
            List<String> messageList = separateMessage(msg.getMsg());
            if (messageList != null && messageList.size() != 0) {
                LogUtils.d(SocketService.TAG, "本次共获取" + messageList.size() + "条数据");
                for (int i = 0; i < messageList.size(); i++) {
                    LogUtils.d(SocketService.TAG, "分包后的数据，第" + i + "条：" + messageList.get(i));
                }
                //循环读取返回的消息列表并进行解析
                for (int i = 0; i < messageList.size(); i++) {
                    String message = messageList.get(i);
                    String meterId = message.substring(4, 12);
                    //判断收到的信息是否属于选中的消火栓
                    if (meterId.equals(String.valueOf(mCurrentItem.getHydrantId()))) {
                        //对连包进行处理
                        switch (message.length()) {
                            //长度42，开关阀门锁返回的信息
                            case 42:
                                if ("A4".equals(message.substring(18, 20))) {
                                    parserLockState(message);
                                }
                                break;
                            //长度68，正在使用信息
                            case 68:
                                if ("A1".equals(message.substring(18, 20))) {
                                    parserCurrentUseState(message);
                                }
                                break;
                            default:
                                break;
                        }
                        if (popupPOIDetails.getVisibility() == View.VISIBLE) {
                            llHydrantUsingStatus.setVisibility(View.VISIBLE);
                        }
                    }
                }
            } else {
                //非消火栓返回的数据
                String text;
                if (SocketConstant.DEVICE_IS_NOT_EXIST.equals(msg.getMsg())) {
                    text = "不存在该消火栓";
                } else if (SocketConstant.DEVICE_IS_OFFLINE.equals(msg.getMsg())) {
                    text = "该消火栓不在线";
                } else {
                    text = msg.getMsg();
                }
                showToast(text);
            }
        }
    }

    /**
     * Socket收到的信息分包
     * 2017-08-26 重新写分包规则，同BluetoothAnalysisUtils里面的数据解析规则
     *
     * @param message 收到的信息
     * @return 返回信息列表
     */
    private List<String> separateMessage(String message) {
        //创建一个消息集合
        List<String> messageList = new ArrayList<>();
        try {
            //判断指令头“68”的位置
            int positionHeadInstruction = message.indexOf("68");
            if (positionHeadInstruction >= 0) {
                //指令头
                String headInstruction = message.substring(positionHeadInstruction, positionHeadInstruction + 2);
                //产品类型（68后面两位）
                String productType = message.substring(positionHeadInstruction + 2, positionHeadInstruction + 4);
                //指令总长度
                String lengthTx;
                //指令具体内容
                String data;
                //智能消火栓
                if ("68".equals(headInstruction) && productType.equals(ProductType.HYDRANT_STRING)) {
                    lengthTx = message.substring(positionHeadInstruction + 20, positionHeadInstruction + 20 + 2);
                    if (message.substring(positionHeadInstruction + (AnalysisUtils.HexS2ToInt(lengthTx) + 13) * 2 - 2, positionHeadInstruction + (AnalysisUtils.HexS2ToInt(lengthTx) + 13) * 2).equals("16")) {
                        data = message.substring(positionHeadInstruction, positionHeadInstruction + (AnalysisUtils.HexS2ToInt(lengthTx) + 13) * 2);
                        messageList.add(data);
                    }
                }
            } else {
                LogUtils.d(SocketService.TAG, "不带6859的数据，非消火栓返回数据");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messageList;
    }


    private void bindSocketService() {
        /*通过binder拿到service*/
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                SocketService.SocketBinder binder = (SocketService.SocketBinder) iBinder;
                socketService = binder.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        Intent intent = new Intent(mContext, SocketService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    /**
     * 解析开关阀门锁信息
     *
     * @param message 开关阀门锁信息
     */
    private void parserLockState(String message) {
        //收到下位机发送的开关锁返回信息
        String meterId = message.substring(4, 12);
        if (message.substring(36, 38).endsWith("00")) {
            tvOpenTime.setText(TimeUtils.getCurrentTime());
            tvUsingStatus.setText("开始使用");
            showToast("消火栓" + meterId + "阀门锁开启");
        } else if (message.substring(36, 38).endsWith("01")) {
            tvUsingStatus.setText("结束使用");
            showToast("消火栓" + meterId + "阀门锁关闭");
        }
    }

    /**
     * 解析正在使用信息
     *
     * @param message 正在使用信息
     */
    private void parserCurrentUseState(String message) {
        //收到下位机发送的正在使用信息
        String meterId = message.substring(4, 12);
        String currentTime = message.substring(58, 60) + message.substring(56, 58) + "-" + message.substring(54, 56) + "-" + message.substring(52, 54) + "     " + message.substring(50, 52) + ":" + message.substring(48, 50) + ":" + message.substring(46, 48);
        String lockDevice = message.substring(26, 28);
        String currentUserID = message.substring(28, 36);
        String currentAmount = StringUtils.changeCode(message.substring(36, 44));
        String currentAmountUnit = message.substring(44, 46);
        String currentUseState = message.substring(60, 62);
        BigDecimal bigDecimal = new BigDecimal((Double.valueOf(currentAmount) * AnalysisUtils.getFlowMultiple(currentAmountUnit)));
        //保留三位小数
        double waterConsumption = bigDecimal.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();

        String usingStatus = "";
        switch (currentUseState) {
            case "00":
                usingStatus = "开始使用";
                break;
            case "01":
                usingStatus = "正在用水";
                break;
            case "02":
                usingStatus = "结束使用";
                break;
            default:
                break;
        }

        tvUsingStatus.setText(usingStatus);
        tvConsumption.setText(String.format(getString(R.string.exampleConsumption), String.valueOf(waterConsumption)));

//        String alarm = message.substring(62, 64);
//        int m = Integer.valueOf(alarm, 16);
//        if ((m & 0x02) == 0x02) {
//            tvAlarmNotCloseValve.setText("阀门未关紧");
//            refreshSocketText(TimeUtils.getCurrentTimeWithSpace() + "消火栓" + meterId + "阀门未关紧，请关紧阀门" + "\n");
//        } else {
//            tvAlarmNotCloseValve.setText("阀门已关紧");
//        }
//        if ((m & 0x04) == 0x04) {
//            tvAlarmLeakageWithoutUser.setText("漏水中");
//        } else {
//            tvAlarmLeakageWithoutUser.setText("未漏水");
//        }
    }

    /**
     * 初始化页面（根据是否已登录）
     */
    private void initPage() {
        if (!HydrantApplication.loginSuccess) {
            tvNickName.setText("暂无昵称");
            tvCompanyName.setText("未登录");
            tvPosition.setText("（点击此处登录）");
            btnExit.setVisibility(View.GONE);
        } else {
            String photoPath = "http://" + NetWork.SERVER_HOST_MAIN + ":" + NetWork.SERVER_PORT_MAIN + "/" + HydrantApplication.getInstance().getAccount().getHead_Portrait_URL().replace("\\", "/");
            // 加载头像
            Glide.with(this).load(photoPath)
                    .error(R.drawable.user_icon)
                    .placeholder(R.drawable.user_icon)
                    .dontAnimate()
                    .into(ivUserIcon);
            Glide.with(this).load(photoPath)
                    .error(R.drawable.user_icon)
                    .placeholder(R.drawable.user_icon)
                    .dontAnimate()
                    .into(ivIcon);
            SharedPreferencesUtils.getInstance().saveData("userIconPath", photoPath);
            String nickName = HydrantApplication.getInstance().getAccount().getNickName();
            String companyName = HydrantApplication.getInstance().getAccount().getName_Company();
            String userPosition = HydrantApplication.getInstance().getAccount().getPosition_Company();
            tvNickName.setText(nickName.equals(Constants.EMPTY) ? "未设置昵称" : nickName);
            tvCompanyName.setText(companyName.equals(Constants.EMPTY) ? "暂无单位名称" : companyName);
            tvPosition.setText(userPosition.equals(Constants.EMPTY) ? "暂无职务" : userPosition);
            btnExit.setVisibility(View.VISIBLE);
        }
    }

    private View.OnClickListener onClickListener = (view) -> {
        ClientUser.Account account = HydrantApplication.getInstance().getAccount();
        switch (view.getId()) {
            case R.id.ivSearch:
                showToast("搜索功能暂未开放");
                break;
            case R.id.ivMessage:
                showToast("消息功能暂未开放");
                break;
            case R.id.btnBg:
                hidePoiDetails();
                break;
            case R.id.btnNavigation:
                // 导航到消火栓
                showToast("导航功能暂未开放");
                break;
            case R.id.btnOpen:
                // 打开消火栓阀门锁
                if (checkAccount()) {
                    operateHydrant("开阀门锁", "0011112408901F" + loginId + "F000");
                }
                break;
            case R.id.btnClose:
                // 关闭消火栓阀门锁
                if (checkAccount()) {
                    operateHydrant("关阀门锁", "0011112408901F" + loginId + "0F00");
                }
                break;
            case R.id.iv_icon:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.tv_allHydrant:
                hidePoiDetails();
                setMyClickable(tvAllHydrant);
                break;
            case R.id.tv_fireHydrant:
                hidePoiDetails();
                setMyClickable(tvFireHydrant);
                break;
            case R.id.tv_otherHydrant:
                hidePoiDetails();
                setMyClickable(tvOtherHydrant);
                break;
            case R.id.dingwei:
                hidePoiDetails();
                shouldUseLocation = true;
                moveToPoint(mCurrentLocation);
                break;
            case R.id.refreshAll:
                hidePoiDetails();
                shouldUseLocation = false;
                if (checkAccount()) {
                    refreshData();
                }
                break;
            case R.id.kefu:
                hidePoiDetails();
                showToast("故障上报功能暂未开放");
                break;
            case R.id.scan_qrcode:
                hidePoiDetails();
                Go2LoginOrScan();
                break;
            case R.id.ll_login:
                //如果是未登陆状态则跳转到登录页面
                if (account == null) {
                    openActivity(LoginRegisterActivity.class);
                }
                break;
            case R.id.ll_setMainAccount:
                if (account == null) {
                    openActivity(LoginRegisterActivity.class);
                } else {
                    openActivity(SetMainAccountActivity.class);
                }
                break;
            case R.id.ll_setSubAccount:
                if (account == null) {
                    openActivity(LoginRegisterActivity.class);
                } else {
                    openActivity(SetSubAccountActivity.class);
                }
                break;
            case R.id.ll_bindAccounts:
                openActivity(SocialAccountsActivity.class);
                break;
            case R.id.llMapZoomLevel:
                startActivityForResult(new Intent(this, MapZoomLevelActivity.class), REQUEST_CODE_ZOOM);
                break;
            case R.id.llNavigationApp:
                showToast("导航功能暂未开放");
                break;
            case R.id.ll_update:
                if (account == null) {
                    showToast("请登陆后检查版本更新");
                } else {
                    if (account.getStable_Update() == 0) {
                        showToast("已禁用所有更新");
                    } else {
                        LogUtils.d("version", "myVersionCode" + myVersionCode + ",latestVersionCode" + latestVersionCode);
                        if (myVersionCode < latestVersionCode) {
                            showDialogUpdate();
                        } else {
                            showToast("已经是最新的版本");
                        }
                    }
                }
                break;
            case R.id.ll_version:
                openActivity(VersionsActivity.class);
                break;
            case R.id.ll_share:
                showToast("分享功能暂未开放");
                break;
            case R.id.btn_exit:
                SharedPreferencesUtils.getInstance().clearData("passWord_main");
                SharedPreferencesUtils.getInstance().clearData("account");
                HydrantApplication.loginSuccess = false;
                openActivity(LoginRegisterActivity.class);
                ActivityController.finishActivity(this);
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                break;
            default:
                break;
        }
    };

    private void initMapView() {
        mapStatus = new MapStatus.Builder()
                // 缩放级别3--21：默认的是12
                .zoom(17)
                // 俯仰的角度
                .overlook(0)
                // 旋转的角度
                .rotate(0)
                .build();
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        // 隐藏比例尺控件
        mMapView.showScaleControl(false);
        // 隐藏缩放按钮
        mMapView.showZoomControls(false);

        mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(mCurrentMode, true, null));

        //监听加载完成
        mBaiduMap.setOnMapLoadedCallback(onMapLoadedCallback);
        // 激活定位图层
        mBaiduMap.setMyLocationEnabled(true);
        MapStatusUpdate update = MapStatusUpdateFactory.newMapStatus(mapStatus);
        // 更新展示的地图的状态
        mBaiduMap.animateMapStatus(update);
    }

    private BaiduMap.OnMapLoadedCallback onMapLoadedCallback = () -> {
        mapLoadedFinish = true;
        initAllHydrant();
    };

    /**
     * 开启方向监听
     */
    private void initOrientation() {
        myOrientationListener = new MyOrientationListener(this);
        //通过接口回调来实现实时方向的改变
        myOrientationListener.setOnOrientationListener((x) -> {
            mCurrentX = x;
        });
        myOrientationListener.start();
    }

    /**
     * 初始化定位相关
     */
    private void initLocation() {
        locationService = ((HydrantApplication) getApplication()).locationService;
        locationService.registerListener(bdAbstractLocationListener);
    }

    /**
     * 地理编码的初始化相关
     */
    private void initGeoCoder() {
        if (mGeoCoder == null) {
            mGeoCoder = GeoCoder.newInstance();
        }
        mGeoCoder.setOnGetGeoCodeResultListener(mGeoCoderResultListener);
    }

    /**
     * 地理编码的监听
     */
    private OnGetGeoCoderResultListener mGeoCoderResultListener = new OnGetGeoCoderResultListener() {
        // 得到地理编码的结果：地址-->经纬度
        @Override
        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

        }

        // 得到反向地理编码的结果：经纬度-->地址
        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
            tvHydrantAddress.setText(reverseGeoCodeResult.getAddress());
        }
    };

    /**
     * 定位监听
     */
    private BDAbstractLocationListener bdAbstractLocationListener = new BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation == null || mBaiduMap == null) {
                return;
            }
            mBDLocation = bdLocation;
            currentLatitude = bdLocation.getLatitude();
            currentLongitude = bdLocation.getLongitude();
            mCurrentLocation = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            MyLocationManager.getInstance().setCurrentLL(mCurrentLocation);
            MyLocationManager.getInstance().setAddress(bdLocation.getAddrStr());
            startNodeStr = PlanNode.withLocation(mCurrentLocation);
            if (isFirstLocation) {
                isFirstLocation = false;
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(0f)
                        //设定图标方向，此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(mCurrentX)
                        .latitude(currentLatitude)
                        .longitude(currentLongitude).build();
                mBaiduMap.setMyLocationData(locData);
            }
            if (shouldUseLocation) {
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(0f)
                        //设定图标方向，此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(mCurrentX)
                        .latitude(currentLatitude)
                        .longitude(currentLongitude).build();
                mBaiduMap.setMyLocationData(locData);
            }
        }
    };

    private void initClusterManager() {
        // 定义点聚合管理类ClusterManager
        mClusterManager = new ClusterManager<>(this, mBaiduMap);
        // 设置地图监听，当地图状态发生改变时，进行点聚合运算
        mBaiduMap.setOnMapStatusChangeListener(mClusterManager);

        mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                shouldUseLocation = false;
            }
        });
        // 设置maker点击时的响应
        mBaiduMap.setOnMarkerClickListener(mClusterManager);

        mClusterManager.setOnClusterClickListener((cluster) -> {
            showToast("有" + cluster.getSize() + "只消火栓");
            return false;
        });
        mClusterManager.setOnClusterItemClickListener((item) -> {
            mCurrentItem = item;
            llHydrantUsingStatus.setVisibility(View.GONE);
            showPoiDetails();
            return false;
        });
    }

    private void showPoiDetails() {
        shouldUseLocation = false;

        double distance;
        LatLng latLng = mCurrentItem.getPosition();
        // 使用百度地图里面的计算的工具类计算出来的距离
        if (mCurrentLocation == null) {
            distance = 0;
        } else {
            distance = DistanceUtil.getDistance(latLng, mCurrentLocation);
        }
        // 规范显示的样式
        String distanceText;
        int oneThousand = 1000;
        if (distance > oneThousand) {
            //保留三位小数
            DecimalFormat decimalFormat = new DecimalFormat("#0.000");
            distanceText = decimalFormat.format(distance / 1000) + "km";
        } else {
            //取整
            DecimalFormat decimalFormat = new DecimalFormat("#");
            distanceText = decimalFormat.format(distance) + "m";
        }

        tvHydrantType.setText(mCurrentItem.getHydrantType());
        tvHydrantId.setText(String.valueOf(mCurrentItem.getHydrantId()));
        tvHydrantStatus.setText(mCurrentItem.getHydrantStatus());
        tvHydrantDistance.setText(distanceText);
        // 如果手动录入过消火栓地址，则采用手动录入的，否则采用地理编码反解坐标得到地址
        if (TextUtils.isEmpty(mCurrentItem.getHydrantAddress().trim())) {
            mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(mCurrentItem.getPosition()));
        } else {
            tvHydrantAddress.setText(mCurrentItem.getHydrantAddress());
        }

        if (popupPOIDetails.getVisibility() == View.GONE) {
            btnBg.setVisibility(View.VISIBLE);
            popupPOIDetails.setVisibility(View.VISIBLE);
            popupPOIDetails.setAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_top_show));
        }

        moveToPoint(latLng);
    }

    private void hidePoiDetails() {
        if (popupPOIDetails.getVisibility() == View.VISIBLE) {
            btnBg.setVisibility(View.GONE);
            popupPOIDetails.setAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_top_hide));
            popupPOIDetails.setVisibility(View.GONE);
        }
    }

    private void refreshData() {
        intiAnimation();
        initAllHydrant();
    }

    private boolean checkAccount() {
        if (!HydrantApplication.loginSuccess) {
            openActivity(LoginRegisterActivity.class);
            return false;
        }
        if (!hydrantAccountCorrect) {
            showToast("请检查消火栓账号");
            return false;
        }
        if (isAdminAccount) {
            Intent intent = new Intent(MainActivity.this, ChooseCompanyActivity.class);
            intent.putExtra(getString(R.string.waterCompanyName), GsonUtils.convertJSON(loginResult));
            startActivityForResult(intent, REQUEST_CODE_CHOOSE_COMPANY);
            return false;
        }
        if (!mapLoadedFinish) {
            return false;
        }
        return true;
    }

    /**
     * 请求得到所有的消火栓
     */
    private void initAllHydrant() {
        if (!HydrantApplication.loginSuccess) {
            return;
        }
        if (!hydrantAccountCorrect) {
            return;
        }
        if (isAdminAccount) {
            if (mapLoadedFinish) {
                Intent intent = new Intent(MainActivity.this, ChooseCompanyActivity.class);
                intent.putExtra(getString(R.string.waterCompanyName), GsonUtils.convertJSON(loginResult));
                startActivityForResult(intent, REQUEST_CODE_CHOOSE_COMPANY);
            }
            return;
        }
        if (!mapLoadedFinish) {
            return;
        }

        hydrantClusterItemList.clear();
        hidePoiDetails();
        shouldUseLocation = false;
        mClusterManager.clearItems();
        mBaiduMap.clear();
        initMapView();

        ClientUser.Account account = HydrantApplication.getInstance().getAccount();
        serverHost = account.getServer_Host_CS();
        httpPort = account.getHttp_Port_CS();
        serviceName = account.getService_Name_CS();
        HashMap<String, String> params = new HashMap<>(2);
        params.put("hieId", hieId);
        params.put("meterid", "");
        Observable<HydrantLastData> allHydrantRootObservable = NetClient.getInstances(NetClient.getBaseUrl(serverHost, httpPort, serviceName)).getNjMeterApi().searchHydrantLastData(params);
        allHydrantRootObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new NetworkSubscriber<HydrantLastData>(mContext, getClass().getSimpleName()) {

            @Override
            public void onStart() {
                super.onStart();
                //接下来可以检查网络连接等操作
                if (!NetworkUtil.isNetworkAvailable(mContext)) {
                    showToast("当前网络不可用，请检查网络");
                    if (!isUnsubscribed()) {
                        unsubscribe();
                    }
                }
            }

            @Override
            public void onError(ExceptionHandle.ResponseThrowable responseThrowable) {
                cancelDialog();
                showToast(responseThrowable.message);
            }

            @Override
            public void onNext(HydrantLastData hydrantLastData) {
                cancelDialog();
                if (hydrantLastData == null) {
                    showToast("请求失败，返回值异常");
                } else {
                    List<HydrantLastData.Data> hydrantList = hydrantLastData.getData();
                    String result = hydrantLastData.getResult();
                    if (Constants.SUCCESS.equals(result)) {
                        hydrantClusterItemList.clear();
                        for (int i = 0; i < hydrantList.size(); i++) {
                            //不显示没有坐标信息的消火栓
                            if (0 != hydrantList.get(i).getLat() && 0 != hydrantList.get(i).getLng()) {
                                HydrantLastData.Data hydrant = hydrantList.get(i);
                                LatLng latLng = new LatLng(hydrant.getLat(), hydrant.getLng());
                                int hydrantId = Integer.parseInt(hydrant.getMeter_id());
                                String hydrantType;
                                switch (hydrant.getProduct_use_code()) {
                                    case ProductType.HYDRANT_I:
                                        hydrantType = "I型智能消火栓";
                                        break;
                                    case ProductType.HYDRANT_II:
                                        hydrantType = "II型智能消火栓";
                                        break;
                                    case ProductType.HYDRANT_III:
                                        hydrantType = "III型智能消火栓";
                                        break;
                                    case ProductType.HYDRANT_FLOWMETER:
                                        hydrantType = "消火栓流量计";
                                        break;
                                    default:
                                        hydrantType = "未知设备";
                                        break;
                                }
                                String createTime = hydrant.getCreate_time();
                                String hydrantAddress = hydrant.getAddress();
                                StringBuilder hydrantStatus = new StringBuilder();
                                if (hydrant.getMeter_status().contains("无水")) {
                                    hydrantStatus.append("无水");
                                }
                                if ("00".equals(hydrant.getRemark3()) || "01".equals(hydrant.getRemark3())) {
                                    if (TextUtils.isEmpty(hydrantStatus.toString())) {
                                        hydrantStatus.append("，");
                                    }
                                    hydrantStatus.append("有人使用");
                                }
                                if (TextUtils.isEmpty(hydrantStatus.toString())) {
                                    hydrantStatus.append("状态正常");
                                }
                                boolean online = hydrant.isOnline();
                                hydrantClusterItemList.add(new HydrantClusterItem(latLng, hydrantId, hydrantType, createTime, hydrantStatus.toString(), hydrantAddress, online));
                            }
                        }
                        addMarkers(hydrantClusterItemList);
                    }
                }
            }
        });
    }

    /**
     * 添加覆盖物
     */
    private void addMarkers(List<HydrantClusterItem> hydrantClusterItemList) {
        LogUtils.d("List的长度是：" + hydrantClusterItemList.size());
        mClusterManager.clearItems();
        LatLng centerPoint;
        if (hydrantClusterItemList.size() == 0) {
            showToast("没有查询到消火栓");
            centerPoint = mCurrentLocation;
        } else if (hydrantClusterItemList.size() == 1) {
            mClusterManager.addItems(hydrantClusterItemList);
            centerPoint = hydrantClusterItemList.get(0).getPosition();
        } else {
            double longitude = 0;
            double latitude = 0;
            for (HydrantClusterItem hydrantClusterItem : hydrantClusterItemList) {
                longitude += hydrantClusterItem.getPosition().longitude;
                latitude += hydrantClusterItem.getPosition().latitude;
            }
            mClusterManager.addItems(hydrantClusterItemList);
            centerPoint = new LatLng(latitude / hydrantClusterItemList.size(), longitude / hydrantClusterItemList.size());
        }
        moveToPoint(centerPoint);
    }

    /**
     * 移动到指定的地方
     *
     * @param latLng 指定的经纬度
     */
    public void moveToPoint(LatLng latLng) {
        // 地图状态的设置：设置到定位的地方
        mapStatus = new MapStatus.Builder()
                .target(latLng)
                .rotate(0)
                .overlook(0)
                .zoom((int) SharedPreferencesUtils.getInstance().getData("ZoomLevel", 15))
                .build();
        // 更新状态
        MapStatusUpdate update = MapStatusUpdateFactory.newMapStatus(mapStatus);
        // 更新展示的地图的状态
        mBaiduMap.animateMapStatus(update);
    }

    /**
     * 刷新动画
     */
    private void intiAnimation() {
        Animation rotationAnimation;
        rotationAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotationAnimation.setFillAfter(true);
        rotationAnimation.setDuration(500);
        rotationAnimation.setRepeatCount(2);

        mRefresh.startAnimation(rotationAnimation);

        rotationAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 设置当前选中的消火栓类型
     *
     * @param tv 对应消火栓类型的TextView
     */
    private void setMyClickable(TextView tv) {
        tvAllHydrant.setClickable(true);
        tvFireHydrant.setClickable(true);
        tvOtherHydrant.setClickable(true);
        tvAllHydrant.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        tvFireHydrant.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        tvOtherHydrant.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        tv.setClickable(false);
        tv.setBackground(getResources().getDrawable(R.drawable.top_tab_select));
        CURRENT_HYDRANTTYPE = Integer.parseInt((String) tv.getTag());
    }

    /**
     * 权限申请
     */
    private void requestPermission() {
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (!permissionList.isEmpty()) {
            String[] perssions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, perssions, 1);
        } else {
            //   requestionLotion();
        }
    }

    private void login() {
        if (HydrantApplication.loginSuccess) {
            ClientUser.Account account = HydrantApplication.getInstance().getAccount();
            loginHydrant(account.getUser_Name_CS(), account.getPass_Word_CS());
        } else {
            String userName = (String) SharedPreferencesUtils.getInstance().getData("userName_main", "");
            String passWord = (String) SharedPreferencesUtils.getInstance().getData("passWord_main", "");
            if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(passWord)) {
                //如果之前登陆成功过保存了账号密码，则直接调用登录接口在后台登录
                loginMainAccount(userName, passWord);
            }
        }
    }

    /**
     * 登录方法
     *
     * @param userName 用户名
     * @param passWord 密码
     */
    private void loginMainAccount(String userName, String passWord) {
        Map<String, String> params = new HashMap<>(4);
        params.put("loginName", userName);
        params.put("password", passWord);
        params.put("versionCode", String.valueOf(ApkUtils.getVersionCode(mContext)));
        params.put("apkTypeId", ApkInfo.APK_TYPE_ID_HYDRANT);

        Observable<String> clientUserCall = NetClient.getInstances(NetClient.BASE_URL_PROJECT).getNjMeterApi().loginMainAccount(params);
        clientUserCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new NetworkSubscriber<String>(mContext, getClass().getSimpleName()) {

            @Override
            public void onStart() {
                super.onStart();
                //接下来可以检查网络连接等操作
                if (!NetworkUtil.isNetworkAvailable(mContext)) {
                    showToast("当前网络不可用，请检查网络");
                    if (!isUnsubscribed()) {
                        unsubscribe();
                    }
                } else {
                    showLoadingDialog(mContext, "登陆中", true);
                }
            }

            @Override
            public void onError(ExceptionHandle.ResponseThrowable responseThrowable) {
                cancelDialog();
                showToast("" + responseThrowable.message);
            }

            @Override
            public void onNext(String s) {
                cancelDialog();
                LogUtils.d("retrofit", s);
                try {
                    CipherUtils des = new CipherUtils(NetClient.SECRET_KRY);
                    String result = des.decrypt(s);
                    ClientUser clientUser = GsonUtils.parseJSON((result), ClientUser.class);
                    String mark = clientUser.getResult();
                    String message = clientUser.getMsg();
                    LogUtils.d("retrofit", GsonUtils.convertJSON(clientUser));
                    switch (mark) {
                        case Constants.SUCCESS:
                            SharedPreferencesUtils.getInstance().saveData("account", GsonUtils.convertJSON(clientUser));
                            HydrantApplication.loginSuccess = true;
                            LogUtils.d("登陆成功");
                            HydrantApplication.getInstance().setAccount(clientUser.getAccount());
                            HydrantApplication.getInstance().setVersion(clientUser.getVersion());
                            HydrantApplication.getInstance().setVersion2(clientUser.getVersion2());
                            HydrantApplication.getInstance().serverList = clientUser.getServer();
                            initPage();
                            checkNewVersion();
                            //注册极光推送别名
                            JPushInterface.setAlias(mContext.getApplicationContext(), 0, String.valueOf(HydrantApplication.getInstance().getAccount().getLoginId()));
                            loginHydrant(clientUser.getAccount().getUser_Name_CS(), clientUser.getAccount().getPass_Word_CS());
                            break;
                        default:
                            showToast("登陆失败，" + message);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loginHydrant(String mUsername, String mPassword) {
        ClientUser.Account account = HydrantApplication.getInstance().getAccount();
        serverHost = account.getServer_Host_CS();
        httpPort = account.getHttp_Port_CS();
        serviceName = account.getService_Name_CS();
        Map<String, String> energyManagerParams = new HashMap<>(3);
        energyManagerParams.put("loginName", mUsername);
        energyManagerParams.put("password", mPassword);
        energyManagerParams.put("type", "xhs");
        Observable<WaterMeterLoginResult> waterMeterLoginResultObservable = NetClient.getInstances(NetClient.getBaseUrl(serverHost, httpPort, serviceName)).getNjMeterApi().loginWaterMeter(energyManagerParams);
        waterMeterLoginResultObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new NetworkSubscriber<WaterMeterLoginResult>(mContext, getClass().getSimpleName()) {

            @Override
            public void onStart() {
                super.onStart();
                //接下来可以检查网络连接等操作
                if (!NetworkUtil.isNetworkAvailable(mContext)) {
                    showToast("当前网络不可用，请检查网络");
                    if (!isUnsubscribed()) {
                        unsubscribe();
                    }
                }
            }

            @Override
            public void onError(ExceptionHandle.ResponseThrowable responseThrowable) {
                cancelDialog();
                showToast(responseThrowable.message);
            }

            @Override
            public void onNext(WaterMeterLoginResult waterMeterLoginResult) {
                cancelDialog();
                if (waterMeterLoginResult == null) {
                    showToast("请求失败，返回值异常");
                } else {
                    String result = waterMeterLoginResult.getResult();
                    if (result.equals(Constants.SUCCESS)) {
                        Intent intent;
                        hydrantAccountCorrect = true;
                        switch (waterMeterLoginResult.getPrivilege()) {
                            case "管理员":
                                isAdminAccount = true;
                                loginResult = waterMeterLoginResult;
                                if (mapLoadedFinish) {
                                    intent = new Intent(MainActivity.this, ChooseCompanyActivity.class);
                                    intent.putExtra(getString(R.string.waterCompanyName), GsonUtils.convertJSON(loginResult));
                                    startActivityForResult(intent, REQUEST_CODE_CHOOSE_COMPANY);
                                }
                                break;
                            case "普通":
                                //对象中拿到集合
                                WaterMeterLoginResult.Data data = waterMeterLoginResult.getData().get(0);
                                //消火栓信息
                                loginId = data.getLogin_id();
                                hieId = data.getHie_id();
                                isAdminAccount = false;
                                initAllHydrant();
                                String socketPort = HydrantApplication.getInstance().getAccount().getSocket_Port_CS();
                                String loginId = (String) SharedPreferencesUtils.getInstance().getData("loginId", "");
                                //启动service
                                intent = new Intent(mContext, SocketService.class);
                                intent.putExtra("ip", serverHost);
                                intent.putExtra("port", socketPort);
                                intent.putExtra("loginId", loginId);
                                startService(intent);
                                break;
                            default:
                                showToast("信息有误");
                                break;
                        }
                    } else {
                        isAdminAccount = false;
                        showToast(waterMeterLoginResult.getMsg());
                    }
                }
            }
        });
    }

    /**
     * 发送控制消火栓指令的公共方法
     *
     * @param operateType 指令中文说明（用于打印在文本框）
     * @param command     指令具体内容
     */
    private void operateHydrant(String operateType, String command) {
        String meterId = String.valueOf(mCurrentItem.getHydrantId());
        //用于计算校验码
        String number = "6859" + meterId + command;
        //校验码
        String checkCode = AnalysisUtils.getCSSum(number, 0);
        String tx = number + checkCode + "16";
        LogUtils.d(SocketService.TAG, "发送的指令为：" + tx);

        SocketPackage socketPackage = new SocketPackage();
        socketPackage.setUserId(loginId);
        socketPackage.setDeviceType(ProductType.HYDRANT_INT);
        socketPackage.setSingleOpt(1);
        socketPackage.setMeterId(meterId);
        socketPackage.setUseCmdCode(false);
        socketPackage.setCmdContent(tx);
        if (isConnectSuccess) {
            Runnable runnable = () -> {
                socketService.sendMsg(NetWork.ANDROID_CMD + GsonUtils.convertJSON(socketPackage) + NetWork.ANDROID_END);
            };
            executorService.submit(runnable);
            showToast(operateType + "指令已发送");
        } else {
            showToast("通信服务未建立，发送失败");
        }
    }

    /**
     * 检查是否有新版本
     */
    private void checkNewVersion() {
        String versionUrl = "";
        myVersionCode = ApkUtils.getVersionCode(mContext);
        //正式版、预览版本更新检查
        ClientUser.Version version = HydrantApplication.getInstance().getVersion();
        ClientUser.Version2 version2 = HydrantApplication.getInstance().getVersion2();
        ClientUser.Account account = HydrantApplication.getInstance().getAccount();
        //是否接收正式版更新
        int stableUpdate = account.getStable_Update();
        //是否接收预览版更新
        int betaUpdate = account.getBeta_Update();
        if (stableUpdate == 1 && betaUpdate == 1) {
            //如果接收预览版更新（前提是接收正式版更新）
            if (version2 == null) {
                //如果预览版不存在，则用正式版代替
                if (version != null) {
                    //如果正式版存在，则采用正式版的值
                    versionType = getString(R.string.versionType_stable);
                    latestVersionCode = version.getVersionCode();
                    latestVersionName = version.getVersionName();
                    versionFileName = version.getVersionFileName();
                    latestVersionMD5 = version.getMd5Value();
                    latestVersionLog = version.getVersionLog();
                    versionUrl = version.getVersionUrl();
                }
            } else {
                //如果预览版存在
                if (version == null) {
                    //如果正式版不存在，直接使用预览版的值
                    versionType = getString(R.string.versionType_preview);
                    latestVersionCode = version2.getVersionCode();
                    latestVersionName = version2.getVersionName();
                    versionFileName = version2.getVersionFileName();
                    latestVersionMD5 = version2.getMd5Value();
                    latestVersionLog = version2.getVersionLog();
                    versionUrl = version2.getVersionUrl();
                } else {
                    //如果正式版存在，比较正式版与预览版的版本号大小
                    if (version2.getVersionCode() > version.getVersionCode()) {
                        //如果预览版版本号比正式版版本号大,则使用预览版的值
                        versionType = getString(R.string.versionType_preview);
                        latestVersionCode = version2.getVersionCode();
                        latestVersionName = version2.getVersionName();
                        versionFileName = version2.getVersionFileName();
                        latestVersionMD5 = version2.getMd5Value();
                        latestVersionLog = version2.getVersionLog();
                        versionUrl = version2.getVersionUrl();
                    } else {
                        //如果正式版版本号比预览版版本号大,则使用正式版的值
                        versionType = getString(R.string.versionType_stable);
                        latestVersionCode = version.getVersionCode();
                        latestVersionName = version.getVersionName();
                        versionFileName = version.getVersionFileName();
                        latestVersionMD5 = version.getMd5Value();
                        latestVersionLog = version.getVersionLog();
                        versionUrl = version.getVersionUrl();
                    }
                }
            }
        } else if (stableUpdate == 1 && betaUpdate == 0) {
            //如果只接收正式版更新，不接收预览版更新
            if (version != null) {
                //如果正式版存在
                versionType = getString(R.string.versionType_stable);
                latestVersionCode = version.getVersionCode();
                latestVersionName = version.getVersionName();
                versionFileName = version.getVersionFileName();
                latestVersionMD5 = version.getMd5Value();
                latestVersionLog = version.getVersionLog();
                versionUrl = version.getVersionUrl();
            }
        }
        apkDownloadPath = versionUrl.replace("\\", "/");
        if (myVersionCode < latestVersionCode) {
            showDialogUpdate();
        }
    }

    /**
     * 提示版本更新的对话框
     */
    private void showDialogUpdate() {
        UpgradeVersionDialog upgradeVersionDialog = new UpgradeVersionDialog(mContext);
        upgradeVersionDialog.setCancelable(false);
        ((TextView) upgradeVersionDialog.findViewById(R.id.tv_versionLog)).setText(latestVersionLog);
        ((TextView) upgradeVersionDialog.findViewById(R.id.tv_currentVersion)).setText(ApkUtils.getVersionName(mContext));
        ((TextView) upgradeVersionDialog.findViewById(R.id.tv_latestVersion)).setText(latestVersionName);
        ((TextView) upgradeVersionDialog.findViewById(R.id.title_name)).setText(String.format(getString(R.string.update_version), latestVersionName + versionType));
        upgradeVersionDialog.setOnDialogClickListener(new UpgradeVersionDialog.OnDialogClickListener() {
            @Override
            public void onOKClick() {
                if (isDownloaded()) {
                    ReDownloadWarningDialog reDownloadWarningDialog = new ReDownloadWarningDialog(mContext, getString(R.string.warning_redownload));
                    reDownloadWarningDialog.setCancelable(false);
                    reDownloadWarningDialog.setOnDialogClickListener(new ReDownloadWarningDialog.OnDialogClickListener() {
                        @Override
                        public void onOKClick() {
                            //直接安装
                            File file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), versionFileName);
                            installApk(file);
                        }

                        @Override
                        public void onCancelClick() {
                            //下载最新的版本程序
                            downloadApk();
                        }
                    });
                    reDownloadWarningDialog.show();
                } else {
                    //下载最新的版本程序
                    downloadApk();
                }
            }

            @Override
            public void onCancelClick() {
                upgradeVersionDialog.dismiss();
            }
        });
        upgradeVersionDialog.show();
    }

    /**
     * 判断是否已经下载过该文件
     *
     * @return boolean
     */
    private boolean isDownloaded() {
        File file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + versionFileName);
        LogUtils.d("MD5", file.getPath());
        return file.isFile() && latestVersionMD5.equals(FileUtil.getFileMD5(file));
    }

    /**
     * 下载新版本程序
     */
    private void downloadApk() {
        if (apkDownloadPath.equals(Constants.EMPTY)) {
            showToast("下载路径有误，请联系客服");
        } else {
            DownLoadDialog progressDialog = new DownLoadDialog(mContext);
            downloadProgressBar = progressDialog.findViewById(R.id.progressbar_download);
            tvUpdateLog = progressDialog.findViewById(R.id.tv_updateLog);
            tvUpdateLog.setText(latestVersionLog);
            tvCompletedSize = progressDialog.findViewById(R.id.tv_completedSize);
            tvTotalSize = progressDialog.findViewById(R.id.tv_totalSize);
            progressDialog.setCancelable(false);
            progressDialog.show();
            NetClient.downloadFileProgress(apkDownloadPath, (currentBytes, contentLength, done) -> {
                //获取到文件的大小
                apkSize = MathUtils.formatFloat((float) contentLength / 1024f / 1024f, 2);
                tvTotalSize.setText(String.format(getString(R.string.file_size_m), String.valueOf(apkSize)));
                //已完成大小
                completedSize = MathUtils.formatFloat((float) currentBytes / 1024f / 1024f, 2);
                tvCompletedSize.setText(String.format(getString(R.string.file_size_m), String.valueOf(completedSize)));
                downloadProgressBar.setProgress(MathUtils.formatFloat(completedSize / apkSize * 100, 1));
                if (done) {
                    progressDialog.dismiss();
                }
            }, new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    //处理下载文件
                    if (response.body() != null) {
                        try {
                            InputStream is = response.body().byteStream();
                            //定义下载后文件的路径和名字，例如：/apk/JiangSuMetter_1.0.1.apk
                            File file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + versionFileName);
                            FileOutputStream fos = new FileOutputStream(file);
                            BufferedInputStream bis = new BufferedInputStream(is);
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = bis.read(buffer)) != -1) {
                                fos.write(buffer, 0, len);
                            }
                            fos.close();
                            bis.close();
                            is.close();
                            installApk(file);
                        } catch (Exception e) {
                            e.printStackTrace();
                            showToast("下载出错，" + e.getMessage() + "，请联系管理员");
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                    progressDialog.dismiss();
                    showToast("下载出错，" + t.getMessage() + "，请联系管理员");
                }
            });
        }
    }

    /**
     * 安装apk
     *
     * @param file 需要安装的apk
     */
    private void installApk(File file) {
        //先验证文件的正确性和完整性（通过MD5值）
        if (file.isFile() && latestVersionMD5.equals(FileUtil.getFileMD5(file))) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri apkUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".fileProvider", file);//在AndroidManifest中的android:authorities值
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            } else {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            }
            startActivity(intent);
        } else {
            showToast("文件异常，无法安装");
        }
    }

    private void Go2LoginOrScan() {
        Intent intent = new Intent(this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE_UNLOCK);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result == PackageManager.PERMISSION_DENIED) {
                            showToast("请同意所申请权限");
                            ActivityController.finishActivity(this);
                            return;
                        }
                    }
                    //      requestionLotion();
                } else {
                    showToast("程序异常");
                    ActivityController.finishActivity(this);
                }
                break;
            default:
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_CHOOSE_COMPANY:
                if (RESULT_OK == resultCode) {
                    hieId = data.getStringExtra("hieId");
                    loginId = data.getStringExtra("loginId");
                    Intent intent = new Intent(mContext, SocketService.class);
                    intent.putExtra("ip", serverHost);
                    String socketPort = HydrantApplication.getInstance().getAccount().getSocket_Port_CS();
                    intent.putExtra("port", socketPort);
                    intent.putExtra("loginId", loginId);
                    startService(intent);
                    isAdminAccount = false;
                    hydrantAccountCorrect = true;
                    initAllHydrant();
                }
                break;
            case REQUEST_CODE_NOTIFICATION_SETTINGS:
                if (NotificationsUtils.isNotificationEnabled(mContext)) {
                    //通知权限已打开
                    showToast("已开启了通知权限");
                } else {
                    //通知权限没有打开
                    showToast("未开启通知权限，部分功能受限");
                }
                break;
            case REQUEST_CODE_UNLOCK:
                if (RESULT_OK == resultCode) {
                    String hydrantId = data.getStringExtra("HydrantId");
                    boolean hasHydrant = false;
                    for (HydrantClusterItem hydrantClusterItem : hydrantClusterItemList) {
                        if (String.valueOf(hydrantClusterItem.getHydrantId()).equals(hydrantId)) {
                            mCurrentItem = hydrantClusterItem;
                            showPoiDetails();
                            hasHydrant = true;
                            break;
                        }
                    }
                    if (!hasHydrant) {
                        LogUtils.d("没有找到该消火栓");
                        showToast("没有找到该消火栓");
                    }
                }
                break;
            case REQUEST_CODE_ZOOM:
                if (RESULT_OK == resultCode && mBaiduMap.getMapStatus().zoom != (int) SharedPreferencesUtils.getInstance().getData("ZoomLevel", 15)) {
                    // 中心不变，地图缩放
                    moveToPoint(mBaiduMap.getMapStatus().target);
                }
                break;
            default:
                break;
        }
    }

    //点击两次返回退出程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
                return false;
            }
        }
        if (btnBg.getVisibility() == View.VISIBLE) {
            btnBg.performClick();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(mContext, SocketService.class);
        stopService(intent);
        if (serviceConnection != null) {
            unbindService(serviceConnection);
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        executorService.shutdown();
        mMapView.onDestroy();
        mGeoCoder.destroy();
        locationService.unregisterListener(bdAbstractLocationListener);
        if (loginReceiver != null) {
            try {
                unregisterReceiver(loginReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
