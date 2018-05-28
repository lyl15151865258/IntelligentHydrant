package cn.njmeter.intelligenthydrant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.njmeter.intelligenthydrant.R;
import cn.njmeter.intelligenthydrant.widget.flowtaglayout.OnInitSelectedPosition;

/**
 * 流式标签选择器
 * Created by LiYuliang on 2017/07/10.
 *
 * @author LiYuliang
 * @version 2017/11/15
 */

public class TagAdapter<T> extends BaseAdapter implements OnInitSelectedPosition {

    private final Context mContext;
    private final List<T> mDataList;

    public TagAdapter(Context context) {
        this.mContext = context;
        mDataList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.tag_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        T t = mDataList.get(position);
        if (t instanceof String) {
            viewHolder.tvTag.setText((String) t);
        }
        return convertView;
    }

    public void onlyAddAll(List<T> datas) {
        mDataList.addAll(datas);
        notifyDataSetChanged();
    }

    public void clearAndAddAll(List<T> datas) {
        mDataList.clear();
        onlyAddAll(datas);
    }

    @Override
    public boolean isSelectedPosition(int position) {
        return false;
    }

    private class ViewHolder {

        private TextView tvTag;

        private ViewHolder(View view) {
            tvTag = view.findViewById(R.id.tv_tag);
        }
    }
}
