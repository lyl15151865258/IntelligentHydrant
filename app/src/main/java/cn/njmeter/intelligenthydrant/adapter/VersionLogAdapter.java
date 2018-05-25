package cn.njmeter.intelligenthydrant.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import cn.njmeter.intelligenthydrant.HydrantApplication;
import cn.njmeter.intelligenthydrant.R;
import cn.njmeter.intelligenthydrant.activity.VersionsActivity;
import cn.njmeter.intelligenthydrant.bean.Version;
import cn.njmeter.intelligenthydrant.loginregister.bean.ClientUser;
import cn.njmeter.intelligenthydrant.constant.ApkInfo;
import cn.njmeter.intelligenthydrant.utils.ApkUtils;

/**
 * Created by LiYuliang on 2017/11/08 0008.
 * 软件版本更新日志的适配器
 *
 * @author LiYuliang
 * @version 2017/11/08
 */

public class VersionLogAdapter extends RecyclerView.Adapter {

    private ButtonInterface buttonInterface;
    private VersionsActivity versionsActivity;
    private List<Version> list;

    public VersionLogAdapter(VersionsActivity versionsActivity, List<Version> lv) {
        this.versionsActivity = versionsActivity;
        list = lv;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_version, viewGroup, false);
        VersionLogViewHolder versionLogViewHolder = new VersionLogViewHolder(view);
        versionLogViewHolder.tvVersionName = view.findViewById(R.id.tv_versionName);
        versionLogViewHolder.tvVersionTime = view.findViewById(R.id.tv_versionTime);
        versionLogViewHolder.tvVersionLog = view.findViewById(R.id.tv_versionLog);
        versionLogViewHolder.btnDownloadApk = view.findViewById(R.id.btn_downloadApk);
        return versionLogViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        VersionLogViewHolder holder = (VersionLogViewHolder) viewHolder;
        Version version = list.get(position);
        String versionType;
        switch (version.getVersionType()) {
            case ApkInfo.VERSION_STABLE:
                versionType = "正式版";
                break;
            case ApkInfo.VERSION_BETA:
                versionType = "预览版";
                break;
            default:
                versionType = "未知版";
                break;
        }
        holder.tvVersionName.setText(String.format(versionsActivity.getString(R.string.update_version2), version.getVersionName(), versionType));
        holder.tvVersionTime.setText(version.getCreateTime());
        holder.tvVersionLog.setText(version.getVersionLog());
        if (version.getVersionCode() > ApkUtils.getVersionCode(versionsActivity)) {
            ClientUser.Account account = HydrantApplication.getInstance().getAccount();
            if (account == null) {
                //如果用户未登陆，隐藏下载按钮
                holder.btnDownloadApk.setVisibility(View.GONE);
            } else {
                //是否接收正式版更新
                int stableUpdate = account.getStable_Update();
                //是否接收预览版更新
                int betaUpdate = account.getBeta_Update();
                if (stableUpdate == 1 && betaUpdate == 1) {
                    //如果接收正式版和预览版更新，则全部显示下载按钮
                    holder.btnDownloadApk.setVisibility(View.VISIBLE);
                    holder.btnDownloadApk.setOnClickListener((v) -> {
                        if (buttonInterface != null) {
                            //接口实例化后的对象，调用重写后的方法
                            buttonInterface.onclick(v, position);
                        }
                    });
                } else if (stableUpdate == 1 && betaUpdate == 0) {
                    //如果接收正式版不接收预览版更新，则根据版本类别显示和隐藏下载按钮
                    if (version.getVersionType().equals(ApkInfo.VERSION_STABLE)) {
                        //如果是正式版，则显示下载按钮
                        holder.btnDownloadApk.setVisibility(View.VISIBLE);
                        holder.btnDownloadApk.setOnClickListener((v) -> {
                            if (buttonInterface != null) {
                                //接口实例化后的对象，调用重写后的方法
                                buttonInterface.onclick(v, position);
                            }
                        });
                    } else if (version.getVersionType().equals(ApkInfo.VERSION_BETA)) {
                        //如果是预览版，则隐藏下载按钮
                        holder.btnDownloadApk.setVisibility(View.GONE);
                    }
                } else if (stableUpdate == 0) {
                    //如果不接收更新，则隐藏下载按钮
                    holder.btnDownloadApk.setVisibility(View.GONE);
                }
            }
        } else {
            //如果版本号小于等于当前软件版本号则隐藏下载按钮
            holder.btnDownloadApk.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class VersionLogViewHolder extends RecyclerView.ViewHolder {

        private TextView tvVersionName, tvVersionTime, tvVersionLog;
        private Button btnDownloadApk;

        private VersionLogViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * 按钮点击事件需要的方法
     */
    public void buttonSetOnclick(ButtonInterface buttonInterface) {
        this.buttonInterface = buttonInterface;
    }

    /**
     * 按钮点击事件对应的接口
     */
    public interface ButtonInterface {
        /**
         * 点击事件
         *
         * @param view     被点击的控件
         * @param position 点击的位置
         */
        void onclick(View view, int position);
    }
}
