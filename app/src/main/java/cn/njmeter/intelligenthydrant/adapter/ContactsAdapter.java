package cn.njmeter.intelligenthydrant.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.njmeter.intelligenthydrant.R;
import cn.njmeter.intelligenthydrant.bean.Contacts;

/**
 * Created by LiYuliang on 2018/4/11.
 * 软件版本更新日志的适配器
 *
 * @author LiYuliang
 * @version 2018/4/11
 */

public class ContactsAdapter extends RecyclerView.Adapter {

    private List<Contacts> list;

    public ContactsAdapter(List<Contacts> lv) {
        list = lv;
    }
    private OnItemClickListener mListener;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_contacts, viewGroup, false);
        ContactsViewHolder contactsViewHolder = new ContactsViewHolder(view);
        contactsViewHolder.tvName = view.findViewById(R.id.tv_name);
        contactsViewHolder.tvPhoneNumber = view.findViewById(R.id.tv_phoneNumber);
        return contactsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ContactsViewHolder holder = (ContactsViewHolder) viewHolder;
        Contacts contacts = list.get(position);
        holder.tvName.setText(contacts.getName());
        holder.tvPhoneNumber.setText(contacts.getPhoneNumber());
        if (mListener != null) {
            holder.itemView.setOnClickListener((v) -> mListener.onItemClick(v, holder.getLayoutPosition()));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class ContactsViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName, tvPhoneNumber;

        private ContactsViewHolder(View itemView) {
            super(itemView);
        }
    }


    public interface OnItemClickListener {
        /**
         * item点击事件
         *
         * @param view     被点击的item控件
         * @param position 被点击的位置
         */
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

}
