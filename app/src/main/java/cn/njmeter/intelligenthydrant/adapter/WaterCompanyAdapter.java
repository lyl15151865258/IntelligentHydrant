package cn.njmeter.intelligenthydrant.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.njmeter.intelligenthydrant.R;
import cn.njmeter.intelligenthydrant.bean.WaterMeterLoginResult;

/**
 * 水表平台展示水司的适配器
 * Created by LiYuliang on 2017/10/07 0007.
 *
 * @author LiYuliang
 * @version 2017/11/20
 */

public class WaterCompanyAdapter extends BaseAdapter {

    private List<WaterMeterLoginResult.Data> list;
    private Context context;

    public WaterCompanyAdapter(Context c, List<WaterMeterLoginResult.Data> lv) {
        context = c;
        list = lv;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list_platforms, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        WaterMeterLoginResult.Data data = list.get(position);
        if (TextUtils.isEmpty(data.getSupplier())) {
            viewHolder.tvPlatform.setText("暂无信息");
        } else {
            viewHolder.tvPlatform.setText(data.getSupplier());
        }
        return convertView;
    }


    private class ViewHolder {
        private TextView tvPlatform;

        private ViewHolder(View view) {
            tvPlatform = view.findViewById(R.id.tv_platform);
        }
    }
}
