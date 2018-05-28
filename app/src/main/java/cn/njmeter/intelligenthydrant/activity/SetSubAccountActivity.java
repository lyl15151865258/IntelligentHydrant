package cn.njmeter.intelligenthydrant.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.HashMap;

import cn.njmeter.intelligenthydrant.HydrantApplication;
import cn.njmeter.intelligenthydrant.R;
import cn.njmeter.intelligenthydrant.loginregister.bean.ClientUser;
import cn.njmeter.intelligenthydrant.bean.SubAccount;
import cn.njmeter.intelligenthydrant.constant.Constants;
import cn.njmeter.intelligenthydrant.network.ExceptionHandle;
import cn.njmeter.intelligenthydrant.network.NetClient;
import cn.njmeter.intelligenthydrant.network.NetworkSubscriber;
import cn.njmeter.intelligenthydrant.utils.ActivityController;
import cn.njmeter.intelligenthydrant.utils.GsonUtils;
import cn.njmeter.intelligenthydrant.utils.NetworkUtil;
import cn.njmeter.intelligenthydrant.utils.StatusBarUtil;
import cn.njmeter.intelligenthydrant.utils.ViewUtils;
import cn.njmeter.intelligenthydrant.widget.MyToolbar;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 设置子账号页面
 * Created by LiYuliang on 2017/7/10 0010.
 *
 * @author LiYuliang
 * @version 2017/10/27
 */

public class SetSubAccountActivity extends BaseActivity {

    private Context mContext;
    private EditText etServerHostEnergyManager, etHttpPortEnergyManager, etSocketPortEnergyManager, etWebSocketPortEnergyManager,
            etServiceNameEnergyManager, etUserNameEnergyManager, etPassWordEnergyManager;
    private ImageView ivInvisibleEnergyManager;
    private boolean isInvisibleEtPassWordEnergyManager = true;
    public static final int REQUEST_CODE_WATER_METER = 1, REQUEST_CODE_CONSTANT_FLOW_VALVE = 2, REQUEST_CODE_METER_READING = 3, REQUEST_CODE_IRRIGATION = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_subaccount);
        mContext = this;
        initView();
    }

    @Override
    protected void setStatusBar() {
        int mColor = getResources().getColor(R.color.colorPrimary);
        StatusBarUtil.setColor(this, mColor);
    }

    private void initView() {
        MyToolbar toolbar = findViewById(R.id.main_toolbar);
        toolbar.initToolBar(this, toolbar, getString(R.string.HydrantAccount), R.drawable.back_white, R.drawable.save_selector, onClickListener);
        //限制域名（或服务器IP地址）输入框的输入内容
        String digits = "abcdefghijklmnopqrstuvwxyz0123456789.";

        //智慧水务平台账号
        etServerHostEnergyManager = findViewById(R.id.et_serverHost_energyManager);
        etHttpPortEnergyManager = findViewById(R.id.et_httpPort_energyManager);
        etSocketPortEnergyManager = findViewById(R.id.et_socketPort_energyManager);
        etWebSocketPortEnergyManager = findViewById(R.id.et_webSocketPort_energyManager);
        etServiceNameEnergyManager = findViewById(R.id.et_serviceName_energyManager);
        etUserNameEnergyManager = findViewById(R.id.et_userName_energyManager);
        etPassWordEnergyManager = findViewById(R.id.et_passWord_energyManager);
        etServerHostEnergyManager.setKeyListener(DigitsKeyListener.getInstance(digits));

        etServerHostEnergyManager.setText(HydrantApplication.getInstance().getAccount().getServer_Host_CS());
        etHttpPortEnergyManager.setText(HydrantApplication.getInstance().getAccount().getHttp_Port_CS());
        etSocketPortEnergyManager.setText(HydrantApplication.getInstance().getAccount().getSocket_Port_CS());
        etServiceNameEnergyManager.setText(HydrantApplication.getInstance().getAccount().getService_Name_CS());
        etUserNameEnergyManager.setText(HydrantApplication.getInstance().getAccount().getUser_Name_CS());
        etPassWordEnergyManager.setText(HydrantApplication.getInstance().getAccount().getPass_Word_CS());

        ViewUtils.setCharSequence(etServerHostEnergyManager);
        ViewUtils.setCharSequence(etHttpPortEnergyManager);
        ViewUtils.setCharSequence(etSocketPortEnergyManager);
        ViewUtils.setCharSequence(etServiceNameEnergyManager);
        ViewUtils.setCharSequence(etUserNameEnergyManager);
        ViewUtils.setCharSequence(etPassWordEnergyManager);

        ivInvisibleEnergyManager = findViewById(R.id.iv_invisible_energyManager);

        findViewById(R.id.iv_deleteIP_energyManager).setOnClickListener(onClickListener);
        findViewById(R.id.iv_deleteHttpPort_energyManager).setOnClickListener(onClickListener);
        findViewById(R.id.iv_deleteSocketPort_energyManager).setOnClickListener(onClickListener);
        findViewById(R.id.iv_deleteWebSocketPort_energyManager).setOnClickListener(onClickListener);
        findViewById(R.id.iv_deleteServiceName_energyManager).setOnClickListener(onClickListener);
        findViewById(R.id.iv_deleteName_energyManager).setOnClickListener(onClickListener);
        ivInvisibleEnergyManager.setOnClickListener(onClickListener);
        findViewById(R.id.iv_configuration_energyManager).setOnClickListener(onClickListener);

    }

    private View.OnClickListener onClickListener = (v) -> {
        Intent intent = new Intent(this, ServerConfigurationActivity.class);
        switch (v.getId()) {
            case R.id.iv_left:
                ActivityController.finishActivity(this);
                break;
            case R.id.iv_right:
                saveInformation();
                break;
            case R.id.iv_deleteIP_energyManager:
                etServerHostEnergyManager.setText("");
                break;
            case R.id.iv_deleteHttpPort_energyManager:
                etHttpPortEnergyManager.setText("");
                break;
            case R.id.iv_deleteSocketPort_energyManager:
                etSocketPortEnergyManager.setText("");
                break;
            case R.id.iv_deleteWebSocketPort_energyManager:
                etWebSocketPortEnergyManager.setText("");
                break;
            case R.id.iv_deleteServiceName_energyManager:
                etServiceNameEnergyManager.setText("");
                break;
            case R.id.iv_deleteName_energyManager:
                etUserNameEnergyManager.setText("");
                break;
            case R.id.iv_invisible_energyManager:
                ViewUtils.changePasswordState(isInvisibleEtPassWordEnergyManager, etPassWordEnergyManager, ivInvisibleEnergyManager);
                isInvisibleEtPassWordEnergyManager = !isInvisibleEtPassWordEnergyManager;
                break;
            case R.id.iv_configuration_energyManager:
                startActivityForResult(intent, REQUEST_CODE_WATER_METER);
                break;
            default:
                break;
        }
    };

    /**
     * 保存子账号信息
     */
    private void saveInformation() {

        SubAccount subAccount = new SubAccount();
        subAccount.setLoginId(HydrantApplication.getInstance().getAccount().getLoginId());
        //智慧水务平台账号信息
        subAccount.setServer_Host_CS(etServerHostEnergyManager.getText().toString());
        subAccount.setHttp_Port_CS(etHttpPortEnergyManager.getText().toString());
        subAccount.setSocket_Port_CS(etSocketPortEnergyManager.getText().toString());
        subAccount.setService_Name_CS(etServiceNameEnergyManager.getText().toString());
        subAccount.setUser_Name_CS(etUserNameEnergyManager.getText().toString());
        subAccount.setPass_Word_CS(etPassWordEnergyManager.getText().toString());

        ClientUser.Account account = HydrantApplication.getInstance().getAccount();
        account.setLoginId(HydrantApplication.getInstance().getAccount().getLoginId());
        //智慧水务平台账号信息
        account.setServer_Host_CS(etServerHostEnergyManager.getText().toString());
        account.setHttp_Port_CS(etHttpPortEnergyManager.getText().toString());
        account.setSocket_Port_CS(etSocketPortEnergyManager.getText().toString());
        account.setService_Name_CS(etServiceNameEnergyManager.getText().toString());
        account.setUser_Name_CS(etUserNameEnergyManager.getText().toString());
        account.setPass_Word_CS(etPassWordEnergyManager.getText().toString());

        updateSubAccount(GsonUtils.convertJSON(account));
    }

    /**
     * 更新子账号信息
     *
     * @param details 子账号信息
     */
    private void updateSubAccount(String details) {
        HashMap<String, String> params = new HashMap<>(1);
        params.put("details", details);
        Observable<ClientUser> clientUserObservable = NetClient.getInstances(NetClient.BASE_URL_PROJECT).getNjMeterApi().updateSubAccount(params);
        clientUserObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new NetworkSubscriber<ClientUser>(mContext, getClass().getSimpleName()) {

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
                    showLoadingDialog(mContext, "更新中", true);
                }
            }

            @Override
            public void onError(ExceptionHandle.ResponseThrowable responseThrowable) {
                cancelDialog();
                showToast(responseThrowable.message);
            }

            @Override
            public void onNext(ClientUser clientUser) {
                cancelDialog();
                if (clientUser == null) {
                    showToast("更新失败，返回值异常");
                } else {
                    String mark = clientUser.getResult();
                    String message = clientUser.getMsg();
                    switch (mark) {
                        case Constants.SUCCESS:
                            //保存用户名密码
                            HydrantApplication.getInstance().setAccount(clientUser.getAccount());
                            HydrantApplication.getInstance().setVersion(clientUser.getVersion());
                            showToast("账号信息更新成功");
                            Intent intent = new Intent();
                            intent.setAction("login");
                            sendBroadcast(intent);
                            ActivityController.finishActivity(SetSubAccountActivity.this);
                            break;
                        case Constants.FAIL:
                            showToast("账号信息更新失败，" + message);
                            break;
                        default:
                            showToast("账号信息更新失败");
                            break;
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode) {
            ClientUser.Server server = (ClientUser.Server) data.getSerializableExtra("ServerConfiguration");
            switch (requestCode) {
                case REQUEST_CODE_WATER_METER:
                    etServerHostEnergyManager.setText(server.getServerHost());
                    etHttpPortEnergyManager.setText(server.getServerUrlPort());
                    etSocketPortEnergyManager.setText(server.getServerTcpPort());
                    etWebSocketPortEnergyManager.setText(server.getServerWebSocketPort());
                    etServiceNameEnergyManager.setText(server.getServerProjectName());
                    break;
                default:
                    break;
            }
        }
    }
}