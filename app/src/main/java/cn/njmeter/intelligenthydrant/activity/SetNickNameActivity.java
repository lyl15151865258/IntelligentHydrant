package cn.njmeter.intelligenthydrant.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

import cn.njmeter.intelligenthydrant.HydrantApplication;
import cn.njmeter.intelligenthydrant.R;
import cn.njmeter.intelligenthydrant.bean.NormalResult;
import cn.njmeter.intelligenthydrant.constant.Constants;
import cn.njmeter.intelligenthydrant.network.ExceptionHandle;
import cn.njmeter.intelligenthydrant.network.NetClient;
import cn.njmeter.intelligenthydrant.network.NetworkSubscriber;
import cn.njmeter.intelligenthydrant.utils.ActivityController;
import cn.njmeter.intelligenthydrant.utils.NetworkUtil;
import cn.njmeter.intelligenthydrant.utils.StatusBarUtil;
import cn.njmeter.intelligenthydrant.widget.MyToolbar;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 设置主账号昵称
 * Created by LiYuliang on 2017/7/10 0010.
 *
 * @author LiYuliang
 * @version 2017/10/20
 */

public class SetNickNameActivity extends BaseActivity {

    private Context mContext;
    private EditText etNickName;
    private int loginId;
    private String password;
    private String nickName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_nick_name);
        mContext = this;
        loginId = HydrantApplication.getInstance().getAccount().getLoginId();
        password = HydrantApplication.getInstance().getAccount().getPassword();
        MyToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.initToolBar(this, toolbar, getString(R.string.ChangeNickname), R.drawable.back_white, onClickListener);
        Button btnModify = findViewById(R.id.btn_modify);
        btnModify.setOnClickListener(onClickListener);
        etNickName = findViewById(R.id.et_nickName);
        etNickName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence.toString())) {
                    btnModify.setEnabled(false);
                } else {
                    btnModify.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        etNickName.setText(HydrantApplication.getInstance().getAccount().getNickName());
    }

    @Override
    protected void setStatusBar() {
        int mColor = getResources().getColor(R.color.colorPrimary);
        StatusBarUtil.setColor(this, mColor);
    }

    private View.OnClickListener onClickListener = (v) -> {
        switch (v.getId()) {
            case R.id.iv_left:
                ActivityController.finishActivity(this);
                break;
            case R.id.btn_modify:
                modifyNickName();
                break;
            default:
                break;
        }
    };

    /**
     * 保存昵称
     */
    private void modifyNickName() {
        String key = "NickName";
        nickName = etNickName.getText().toString().trim();
        if (TextUtils.isEmpty(nickName)) {
            showToast("请输入昵称");
        } else {
            Map<String, String> params = new HashMap<>(4);
            params.put("key", key);
            params.put("value", nickName);
            params.put("loginId", String.valueOf(loginId));
            params.put("password", password);
            Observable<NormalResult> normalResultObservable = NetClient.getInstances(NetClient.BASE_URL_PROJECT).getNjMeterApi().modifyNickName(params);
            normalResultObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new NetworkSubscriber<NormalResult>(mContext, getClass().getSimpleName()) {

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
                public void onNext(NormalResult normalResult) {
                    cancelDialog();
                    if (normalResult == null) {
                        showToast("更新失败，返回值异常");
                    } else {
                        String result = normalResult.getResult();
                        String message = normalResult.getMessage();
                        switch (result) {
                            case Constants.SUCCESS:
                                HydrantApplication.getInstance().getAccount().setNickName(nickName);
                                showToast("更新昵称成功");
                                ActivityController.finishActivity(SetNickNameActivity.this);
                                break;
                            case Constants.FAIL:
                                showToast("更新昵称失败," + message);
                                break;
                            default:
                                showToast("更新昵称失败");
                                break;
                        }
                    }
                }
            });
        }
    }
}
