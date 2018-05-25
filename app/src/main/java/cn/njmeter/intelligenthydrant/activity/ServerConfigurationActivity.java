package cn.njmeter.intelligenthydrant.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import cn.njmeter.intelligenthydrant.HydrantApplication;
import cn.njmeter.intelligenthydrant.R;
import cn.njmeter.intelligenthydrant.loginregister.bean.ClientUser;
import cn.njmeter.intelligenthydrant.adapter.ServerConfigurationAdapter;
import cn.njmeter.intelligenthydrant.utils.ActivityController;
import cn.njmeter.intelligenthydrant.utils.StatusBarUtil;
import cn.njmeter.intelligenthydrant.widget.MyToolbar;
import cn.njmeter.intelligenthydrant.widget.RecyclerViewDivider;

/**
 * 服务器配置信息
 * Created at 2018/5/14 0014 14:23
 *
 * @author LiYuliang
 * @version 1.0
 */
public class ServerConfigurationActivity extends BaseActivity {

    private EditText etSearchContent;
    private List<ClientUser.Server> serverList = new ArrayList<>();
    private ServerConfigurationAdapter serverConfigurationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_configuration);
        Context mContext = this;
        MyToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.initToolBar(this, toolbar, "选择服务器", R.drawable.back_white, onClickListener);
        etSearchContent = findViewById(R.id.et_searchContent);
        RecyclerView recyclerViewConfiguration = findViewById(R.id.recyclerView_configuration);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewConfiguration.setLayoutManager(linearLayoutManager);
        RecyclerViewDivider horizontalDivider = new RecyclerViewDivider(mContext, LinearLayoutManager.HORIZONTAL, 2, ContextCompat.getColor(mContext, R.color.gray_1));
        recyclerViewConfiguration.addItemDecoration(horizontalDivider);
        serverList.addAll(HydrantApplication.getInstance().serverList);
        serverConfigurationAdapter = new ServerConfigurationAdapter(serverList);
        recyclerViewConfiguration.setAdapter(serverConfigurationAdapter);
        serverConfigurationAdapter.setOnItemClickListener((view, position) -> {
            Intent intent = new Intent();
            intent.putExtra("ServerConfiguration", HydrantApplication.getInstance().serverList.get(position));
            setResult(Activity.RESULT_OK, intent);
            ActivityController.finishActivity(this);
        });
        findViewById(R.id.iv_deleteContent).setOnClickListener(onClickListener);
        etSearchContent.addTextChangedListener(textWatcher);
    }

    @Override
    protected void setStatusBar() {
        int mColor = getResources().getColor(R.color.colorPrimary);
        StatusBarUtil.setColor(this, mColor);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            serverList.clear();
            for (ClientUser.Server server : HydrantApplication.getInstance().serverList) {
                if (server.getServerHost().contains(s.toString()) || server.getServerName().contains(s.toString()) ||
                        server.getServerProjectName().contains(s.toString()) || server.getServerTcpPort().contains(s.toString()) ||
                        server.getServerUdpPort().contains(s.toString()) || server.getServerUrlPort().contains(s.toString()) ||
                        server.getServerWebSocketPort().contains(s.toString())) {
                    serverList.add(server);
                }
            }
            serverConfigurationAdapter.notifyDataSetChanged();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private View.OnClickListener onClickListener = (v) -> {
        switch (v.getId()) {
            case R.id.iv_left:
                ActivityController.finishActivity(this);
                break;
            case R.id.iv_deleteContent:
                etSearchContent.setText("");
                break;
            default:
                break;
        }
    };

}
