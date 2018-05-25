package cn.njmeter.intelligenthydrant.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.njmeter.intelligenthydrant.R;
import cn.njmeter.intelligenthydrant.loginregister.bean.ClientUser;

/**
 * 服务器配置信息显示适配器
 * Created at 2018/5/14 0014 14:38
 *
 * @author LiYuliang
 * @version 1.0
 */

public class ServerConfigurationAdapter extends RecyclerView.Adapter {

    private List<ClientUser.Server> list;
    private OnItemClickListener mItemClickListener;

    public ServerConfigurationAdapter(List<ClientUser.Server> lv) {
        list = lv;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_server_configuration, viewGroup, false);
        ListViewHolder listViewHolder = new ListViewHolder(view);
        listViewHolder.tvNumber = view.findViewById(R.id.tv_number);
        listViewHolder.tvServerName = view.findViewById(R.id.tv_serverName);
        listViewHolder.tvServerHost = view.findViewById(R.id.tv_serverHost);
        listViewHolder.tvHttpPort = view.findViewById(R.id.tv_httpPort);
        listViewHolder.tvSocketPort = view.findViewById(R.id.tv_socketPort);
        listViewHolder.tvWebSocketPort = view.findViewById(R.id.tv_webSocketPort);
        listViewHolder.tvProjectName = view.findViewById(R.id.tv_projectName);
        return listViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ListViewHolder holder = (ListViewHolder) viewHolder;
        ClientUser.Server data = list.get(position);
        holder.tvNumber.setText(String.valueOf(position + 1));
        holder.tvServerName.setText(data.getServerName());
        holder.tvServerHost.setText(data.getServerHost());
        holder.tvHttpPort.setText(data.getServerUrlPort());
        holder.tvSocketPort.setText(data.getServerTcpPort());
        holder.tvWebSocketPort.setText(data.getServerWebSocketPort());
        holder.tvProjectName.setText(data.getServerProjectName());
        if (mItemClickListener != null) {
            viewHolder.itemView.setOnClickListener((v) -> mItemClickListener.onItemClick(holder.itemView, holder.getLayoutPosition()));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class ListViewHolder extends RecyclerView.ViewHolder {

        private TextView tvNumber,tvServerName, tvServerHost, tvHttpPort, tvSocketPort, tvWebSocketPort, tvProjectName;

        private ListViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mItemClickListener = onItemClickListener;
    }

}
