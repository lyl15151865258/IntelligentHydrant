package cn.njmeter.intelligenthydrant.bean;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.model.LatLng;

import cn.njmeter.intelligenthydrant.R;
import cn.njmeter.intelligenthydrant.utils.clusterutil.clustering.ClusterItem;

/**
 * 消火栓聚合点item
 * Created at 2018/5/26 0026 13:27
 *
 * @author LiYuliang
 * @version 1.0
 */

public class HydrantClusterItem implements ClusterItem {

    private final LatLng mPosition;
    private final int hydrantId;
    private final String hydrantType;
    private final String createTime;
    private final String hydrantAddress;

    public HydrantClusterItem(LatLng latLng, int hydrantId, String hydrantType, String createTime, String hydrantAddress) {
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
