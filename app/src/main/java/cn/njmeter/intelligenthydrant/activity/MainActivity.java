package cn.njmeter.intelligenthydrant.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.utils.DistanceUtil;
import com.bumptech.glide.Glide;
import com.pkmmte.view.CircularImageView;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import cn.njmeter.intelligenthydrant.HydrantApplication;
import cn.njmeter.intelligenthydrant.R;
import cn.njmeter.intelligenthydrant.bean.HydrantLastData;
import cn.njmeter.intelligenthydrant.bean.WaterMeterLoginResult;
import cn.njmeter.intelligenthydrant.constant.Constants;
import cn.njmeter.intelligenthydrant.constant.NetWork;
import cn.njmeter.intelligenthydrant.constant.ProductType;
import cn.njmeter.intelligenthydrant.loginregister.activity.LoginRegisterActivity;
import cn.njmeter.intelligenthydrant.loginregister.bean.ClientUser;
import cn.njmeter.intelligenthydrant.map.MyOrientationListener;
import cn.njmeter.intelligenthydrant.network.ExceptionHandle;
import cn.njmeter.intelligenthydrant.network.NetClient;
import cn.njmeter.intelligenthydrant.network.NetworkSubscriber;
import cn.njmeter.intelligenthydrant.utils.ActivityController;
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
import cn.njmeter.intelligenthydrant.utils.clusterutil.clustering.ClusterItem;
import cn.njmeter.intelligenthydrant.utils.clusterutil.clustering.ClusterManager;
import cn.njmeter.intelligenthydrant.widget.CommonWarningDialog;
import cn.njmeter.intelligenthydrant.widget.DownLoadDialog;
import cn.njmeter.intelligenthydrant.widget.DownloadProgressBar;
import cn.njmeter.intelligenthydrant.widget.ReDownloadWarningDialog;
import cn.njmeter.intelligenthydrant.widget.UpgradeVersionDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    private Context mContext;
    private String serverHost, httpPort, serviceName, hieId;
    private DrawerLayout drawerLayout;
    private LinearLayout llMain;
    private TextView tvAllHydrant, tvFireHydrant, tvOtherHydrant;
    private ImageView mDingWei, mRefresh, mKefu;
    private RelativeLayout mRefreshAll;
    private TextureMapView mMapView;
    private CircularImageView ivIcon, ivUserIcon;
    private TextView tvNickName, tvCompanyName, tvPosition;
    private Button btnExit;
    private BaiduMap mBaiduMap;
    public LocationClient mlocationClient;
    private MapStatus mapStatus;

    private MyLocationConfiguration.LocationMode mCurrentMode;
    private double currentLatitude, currentLongitude, changeLatitude, changeLongitude;
    private MyOrientationListener myOrientationListener;
    private float mCurrentX;
    private boolean hasPlanRoute = false, isServiceLive = false;
    private PlanNode startNodeStr, endNodeStr;
    private LatLng mCurrentLocation;
    private boolean isFirstLocation = true;

    private BDLocation mBDLocation;
    private static final int HYDRANTTYPE_ALL_HYDRANT = 0;
    private static final int HYDRANTTYPE_FIRE_HYDRANT = 1;
    private static final int HYDRANTTYPE_OTHER_HYDRANT = 2;
    private int CURRENT_HYDRANTTYPE = HYDRANTTYPE_ALL_HYDRANT;
    private ClusterManager<MyItem> mClusterManager;
    private MyItem mCurrentItem;
    private boolean shouldUseLocation = true;

    private BitmapDescriptor dotExpand = BitmapDescriptorFactory.fromResource(R.mipmap.icon_gcoding);

    private String versionType, latestVersionName, versionFileName, latestVersionMD5, latestVersionLog, apkDownloadPath;
    private int myVersionCode, latestVersionCode;
    private DownloadProgressBar downloadProgressBar;
    private TextView tvUpdateLog, tvCompletedSize, tvTotalSize;
    private float apkSize, completedSize;

    private static final int REQUEST_CODE_NOTIFICATION_SETTINGS = 1;
    private static final int REQUEST_CODE_UNLOCK = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        initView();
        initMap();
        initClusterManager();
        requesPemission();
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
    }

    @Override
    protected void setStatusBar() {
        int mColor = getResources().getColor(R.color.colorPrimary);
        StatusBarUtil.setColorForDrawerLayout(this, findViewById(R.id.drawer_layout), mColor);
    }

    private void initView() {
        NavigationView navigation = findViewById(R.id.navigation);
        ViewGroup.LayoutParams params = navigation.getLayoutParams();
        params.width = mWidth * 4 / 5;
        navigation.setLayoutParams(params);

        drawerLayout = findViewById(R.id.drawer_layout);
        llMain = findViewById(R.id.ll_main);
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

        mMapView = findViewById(R.id.bmapview);
        setMyClickable(tvAllHydrant);
        mBaiduMap = mMapView.getMap();

        ivIcon = findViewById(R.id.iv_icon);
        ivUserIcon = findViewById(R.id.iv_userIcon);

        ivIcon.setOnClickListener(onClickListener);

        tvNickName = findViewById(R.id.tv_nickName);
        tvCompanyName = findViewById(R.id.tv_companyName);
        tvPosition = findViewById(R.id.tv_position);

        btnExit = findViewById(R.id.btn_exit);
        btnExit.setOnClickListener(onClickListener);

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
        switch (view.getId()) {
            case R.id.iv_icon:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.tv_allHydrant:
                setMyClickable(tvAllHydrant);
                break;
            case R.id.tv_fireHydrant:
                setMyClickable(tvFireHydrant);
                break;
            case R.id.tv_otherHydrant:
                setMyClickable(tvOtherHydrant);
                break;
            case R.id.dingwei:
                shouldUseLocation = true;
                Go2myLotionAndRefresh();
                break;
            case R.id.refreshAll:
                shouldUseLocation = false;
                refreshData();
                break;
            case R.id.kefu:
                openActivity(ContactUsActivity.class);
                break;
            case R.id.scan_qrcode:
                Go2LoginOrScan();
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

    private void initMap() {
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        mlocationClient = new LocationClient(getApplicationContext());
        mlocationClient.registerLocationListener(new MylocationListener());
        // 隐藏比例尺控件
        //  mMapView.showScaleControl(false);
        //隐藏缩放按钮
        mMapView.showZoomControls(false);
        LocationClientOption option = new LocationClientOption();
        // 打开gps
        option.setOpenGps(true);
        // 设置坐标类型
        option.setCoorType("bd09ll");
        // 设置onReceiveLocation()获取位置的频率
        option.setScanSpan(5000);
        //如想获得具体位置就需要设置为true
        option.setIsNeedAddress(true);
        mlocationClient.setLocOption(option);
        mlocationClient.start();
        mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, null));
        myOrientationListener = new MyOrientationListener(this);
        //通过接口回调来实现实时方向的改变
        myOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                mCurrentX = x;
            }
        });
        myOrientationListener.start();
    }

    private void initClusterManager() {
        // 定义点聚合管理类ClusterManager
        mClusterManager = new ClusterManager<>(this, mBaiduMap);
        // 设置地图监听，当地图状态发生改变时，进行点聚合运算
        mBaiduMap.setOnMapStatusChangeListener(mClusterManager);
        // 设置maker点击时的响应
        mBaiduMap.setOnMarkerClickListener(mClusterManager);

        mClusterManager.setOnClusterClickListener((cluster) -> {
            showToast("有" + cluster.getSize() + "只消火栓");
            return false;
        });
        mClusterManager.setOnClusterItemClickListener((item) -> {
            mCurrentItem = item;
            // 创建一个InfoWindow
            InfoWindow infoWindow = new InfoWindow(dotExpand, mCurrentItem.getPosition(), 0, () -> {
                mBaiduMap.hideInfoWindow();
//                informationHydrant.setVisibility(View.GONE);
            });
            //地图上显示一个InfoWindow
            mBaiduMap.showInfoWindow(infoWindow);
            mBaiduMap.showInfoWindow(infoWindow);
            double distance;
            // 表信息的取出和展示
            String hydrantType = mCurrentItem.getHydrantType();
            String hydrantAddress = mCurrentItem.getHydrantAddress();
            String commitTime = mCurrentItem.getCreateTime();
            int hydrantID = mCurrentItem.getHydrantId();
            LatLng latLng = mCurrentItem.getPosition();
            // 使用百度地图里面的计算的工具类计算出来的距离
            if (mCurrentLocation == null) {
                distance = 0;
            } else {
                distance = DistanceUtil.getDistance(latLng, mCurrentLocation);
            }
            // 规范显示的样式
            String text;
            int oneThousand = 1000;
            if (distance > oneThousand) {
                //保留三位小数
                DecimalFormat decimalFormat = new DecimalFormat("#0.000");
                text = decimalFormat.format(distance / 1000) + "km";
            } else {
                //取整
                DecimalFormat decimalFormat = new DecimalFormat("#");
                text = decimalFormat.format(distance) + "m";
            }
//            tvHydrantID.setText(String.valueOf(hydrantID));
//            tvHydrantType.setText(hydrantType);
//            tvDistance.setText(text);
//            tvCommitTime.setText(commitTime);
//            informationHydrant.setVisibility(View.VISIBLE);
//            hydrantLocation = latLng;
//            hydrantLocationAddress = hydrantAddress;

            return false;
        });
    }

    /**
     * bd地图监听，接收当前位置
     */
    public class MylocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation == null || mBaiduMap == null) {
                return;
            }
            mBDLocation = bdLocation;
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(bdLocation.getRadius())
                    //设定图标方向，此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentX)
                    .latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude()).build();
            if (shouldUseLocation) {
                mBaiduMap.setMyLocationData(locData);
            }
            currentLatitude = bdLocation.getLatitude();
            currentLongitude = bdLocation.getLongitude();
            mCurrentLocation = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            MyLocationManager.getInstance().setCurrentLL(mCurrentLocation);
            MyLocationManager.getInstance().setAddress(bdLocation.getAddrStr());
            startNodeStr = PlanNode.withLocation(mCurrentLocation);
            if (isFirstLocation) {
                isFirstLocation = false;
                moveToPoint(mCurrentLocation);
                changeLatitude = bdLocation.getLatitude();
                changeLongitude = bdLocation.getLongitude();
                if (!isServiceLive) {
//                    addOverLayout(currentLatitude, currentLongitude);
                }
            }
        }
    }

    private void Go2myLotionAndRefresh() {
        moveToPoint(mCurrentLocation);
        refreshData();
    }

    private void refreshData() {
        intiAnimation();
        initAllHydrant();
    }

    /**
     * 请求得到所有的消火栓
     */
    private void initAllHydrant() {
        if (!HydrantApplication.loginSuccess) {
            return;
        }
        ClientUser.Account account = HydrantApplication.getInstance().getAccount();
        serverHost = account.getServer_Host_CS();
        httpPort = account.getHttp_Port_CS();
        serviceName = account.getService_Name_CS();
        hieId = (String) SharedPreferencesUtils.getInstance().getData("hie_id", "");
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
                        List<MyItem> myItemList = new ArrayList<>();
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
                                myItemList.add(new MyItem(latLng, hydrantId, hydrantType, createTime, hydrantAddress));
                            }
                        }
                        addMarkers(myItemList);
                    }
                }
            }
        });
    }

    /**
     * 添加覆盖物
     */
    private void addMarkers(List<MyItem> myItemList) {
        LogUtils.d("List的长度是：" + myItemList.size());
        mClusterManager.clearItems();
        LatLng centerPoint;
        if (myItemList.size() == 0) {
            showToast("没有查询到消火栓");
            centerPoint = mCurrentLocation;
        } else if (myItemList.size() == 1) {
            mClusterManager.addItems(myItemList);
            centerPoint = myItemList.get(0).getPosition();
        } else {
            double longitude = 0;
            double latitude = 0;
            for (MyItem myItem : myItemList) {
                longitude += myItem.getPosition().longitude;
                latitude += myItem.getPosition().latitude;
            }
            mClusterManager.addItems(myItemList);
            centerPoint = new LatLng(latitude / myItemList.size(), longitude / myItemList.size());
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
                .zoom(18)
                .build();
        // 更新状态
        MapStatusUpdate update = MapStatusUpdateFactory.newMapStatus(mapStatus);
        // 更新展示的地图的状态
        mBaiduMap.animateMapStatus(update);
    }

    /**
     * 每个Marker点，包含Marker点坐标以及图标
     */
    public class MyItem implements ClusterItem {

        private final LatLng mPosition;
        private final int hydrantId;
        private final String hydrantType;
        private final String createTime;
        private final String hydrantAddress;

        public MyItem(LatLng latLng, int hydrantId, String hydrantType, String createTime, String hydrantAddress) {
            mPosition = latLng;
            this.hydrantId = hydrantId;
            this.hydrantType = hydrantType;
            this.createTime = createTime;
            this.hydrantAddress = hydrantAddress;
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }

        public int getHydrantId() {
            return hydrantId;
        }

        public String getHydrantType() {
            return hydrantType;
        }

        public String getCreateTime() {
            return createTime;
        }

        public String getHydrantAddress() {
            return hydrantAddress;
        }

        @Override
        public BitmapDescriptor getBitmapDescriptor() {
            return BitmapDescriptorFactory.fromResource(R.mipmap.icon_hydrant_online);
        }

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
    private void requesPemission() {
        List<String> permissionlist = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionlist.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionlist.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionlist.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (!permissionlist.isEmpty()) {
            String[] perssions = permissionlist.toArray(new String[permissionlist.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, perssions, 1);
        } else {
            //   requestionLotion();
        }
    }

    private void login() {
        String userName = (String) SharedPreferencesUtils.getInstance().getData("userName_main", "");
        String passWord = (String) SharedPreferencesUtils.getInstance().getData("passWord_main", "");
        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(passWord)) {
            //如果之前登陆成功过保存了账号密码，则直接调用登录接口在后台登录
            loginMainAccount(userName, passWord);
        }
    }

    /**
     * 登录方法
     *
     * @param userName 用户名
     * @param passWord 密码
     */
    private void loginMainAccount(String userName, String passWord) {
        Map<String, String> params = new HashMap<>(3);
        params.put("loginName", userName);
        params.put("password", passWord);
        params.put("versionCode", String.valueOf(ApkUtils.getVersionCode(mContext)));

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
                    showLoadingDialog(mContext, "登陆中，请稍后", true);
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
                        switch (waterMeterLoginResult.getPrivilege()) {
                            case "管理员":
                                showToast("请勿使用管理员账号！");
                                break;
                            case "普通":
                                //对象中拿到集合
                                WaterMeterLoginResult.Data data = waterMeterLoginResult.getData().get(0);
                                //消火栓信息
                                SharedPreferencesUtils.getInstance().saveData("loginId", data.getLogin_id());
                                SharedPreferencesUtils.getInstance().saveData("hie_id", data.getHie_id());
                                initAllHydrant();
                                break;
                            default:
                                showToast("信息有误");
                                break;
                        }
                    } else {
                        showToast(waterMeterLoginResult.getMsg());
                    }
                }
            }
        });
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
//            showDialogUpdate();
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
                            File file = new File(mContext.getExternalFilesDir("apk"), versionFileName);
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
        File file = new File(mContext.getExternalFilesDir("apk") + File.separator + versionFileName);
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
                            File file = new File(mContext.getExternalFilesDir("apk") + File.separator + versionFileName);
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
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri apkUri = FileProvider.getUriForFile(mContext, "cn.njmeter,njmeter.fileprovider", file);//在AndroidManifest中的android:authorities值
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            } else {
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
                            finish();
                            return;
                        }
                    }
                    //      requestionLotion();
                } else {
                    showToast("Something Happened");
                    finish();
                }
                break;
            default:
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
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
                    showToast(data.getStringExtra("result"));
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
        return super.onKeyDown(keyCode, event);
    }
}
