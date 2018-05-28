package cn.njmeter.intelligenthydrant.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;

import cn.njmeter.intelligenthydrant.HydrantApplication;
import cn.njmeter.intelligenthydrant.R;
import cn.njmeter.intelligenthydrant.service.LocationService;
import cn.njmeter.intelligenthydrant.utils.ActivityController;
import cn.njmeter.intelligenthydrant.utils.SharedPreferencesUtils;
import cn.njmeter.intelligenthydrant.utils.StatusBarUtil;
import cn.njmeter.intelligenthydrant.widget.MyToolbar;

/**
 * 地图缩放级别设置页面
 * Created at 2018/5/28 0028 15:05
 *
 * @author LiYuliang
 * @version 1.0
 */

public class MapZoomLevelActivity extends BaseActivity {

    private TextView tvLevel;
    private TextureMapView zoomMap;
    private BaiduMap mBaiduMap;
    private MapStatus mapStatus;
    private LocationService locationService;
    private boolean mapLoadedFinish = false;
    private LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_zoom_level);
        MyToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.initToolBar(this, toolbar, "设置地图默认缩放级别", R.drawable.back_white, onClickListener);
        tvLevel = findViewById(R.id.tvLevel);
        tvLevel.setText(String.valueOf((int) SharedPreferencesUtils.getInstance().getData("ZoomLevel", 15)));
        zoomMap = findViewById(R.id.zoomMap);
        findViewById(R.id.ivReduceLevel).setOnClickListener(onClickListener);
        findViewById(R.id.ivIncreaseLevel).setOnClickListener(onClickListener);
        tvLevel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SharedPreferencesUtils.getInstance().saveData("ZoomLevel", Integer.valueOf(tvLevel.getText().toString()));
                if (mapLoadedFinish) {
                    // 地图状态的设置：设置到定位的地方
                    mapStatus = new MapStatus.Builder()
                            .target(latLng)
                            .rotate(0)
                            .overlook(0)
                            .zoom(Integer.valueOf(tvLevel.getText().toString()))
                            .build();
                    // 更新状态
                    MapStatusUpdate update = MapStatusUpdateFactory.newMapStatus(mapStatus);
                    // 更新展示的地图的状态
                    mBaiduMap.animateMapStatus(update);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        initMapView();
        initLocation();
    }

    @Override
    protected void setStatusBar() {
        int mColor = getResources().getColor(R.color.colorPrimary);
        StatusBarUtil.setColor(this, mColor);
    }

    private View.OnClickListener onClickListener = (v) -> {
        int level = Integer.valueOf(tvLevel.getText().toString());
        switch (v.getId()) {
            case R.id.iv_left:
                setResult(RESULT_OK);
                ActivityController.finishActivity(this);
                break;
            case R.id.ivReduceLevel:
                if (level > 3) {
                    tvLevel.setText(String.valueOf(level - 1));
                } else {
                    showToast("已到达最小级别");
                }
                break;
            case R.id.ivIncreaseLevel:
                if (level < 21) {
                    tvLevel.setText(String.valueOf(level + 1));
                } else {
                    showToast("已到达最大级别");
                }
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
        mBaiduMap = zoomMap.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        // 显示比例尺控件
        zoomMap.showScaleControl(true);
        // 隐藏缩放按钮
        zoomMap.showZoomControls(false);

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
    };

    /**
     * 初始化定位相关
     */
    private void initLocation() {
        locationService = ((HydrantApplication) getApplication()).locationService;
        locationService.registerListener(bdAbstractLocationListener);
    }

    /**
     * 定位监听
     */
    private BDAbstractLocationListener bdAbstractLocationListener = new BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation == null || mBaiduMap == null) {
                return;
            }
            latLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            moveToPoint(latLng);
        }
    };

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

    @Override
    protected void onResume() {
        super.onResume();
        zoomMap.onResume();
        locationService.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        zoomMap.onPause();
        locationService.stop();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //关闭定位
        mBaiduMap.setMyLocationEnabled(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        zoomMap.onDestroy();
        locationService.unregisterListener(bdAbstractLocationListener);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        setResult(RESULT_OK);
        return super.onKeyDown(keyCode, event);
    }
}
