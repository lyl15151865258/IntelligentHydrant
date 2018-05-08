package cn.njmeter.intelligenthydrant.loginregister.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;

import cn.njmeter.intelligenthydrant.HydrantApplication;
import cn.njmeter.intelligenthydrant.R;
import cn.njmeter.intelligenthydrant.activity.BaseActivity;
import cn.njmeter.intelligenthydrant.bean.NormalResult;
import cn.njmeter.intelligenthydrant.constant.Constants;
import cn.njmeter.intelligenthydrant.network.ExceptionHandle;
import cn.njmeter.intelligenthydrant.network.NetClient;
import cn.njmeter.intelligenthydrant.network.NetworkSubscriber;
import cn.njmeter.intelligenthydrant.utils.ActivityController;
import cn.njmeter.intelligenthydrant.utils.NetworkUtil;
import cn.njmeter.intelligenthydrant.utils.SharedPreferencesUtils;
import cn.njmeter.intelligenthydrant.utils.StatusBarUtil;
import cn.njmeter.intelligenthydrant.utils.ViewUtils;
import cn.njmeter.intelligenthydrant.widget.MyToolbar;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 更改主账号密码
 * Created by LiYuliang on 2017/07/10 0010.
 *
 * @author LiYuliang
 * @version 2017/10/20
 */

public class ChangePasswordActivity extends BaseActivity {

    private Context mContext;
    private int loginId;
    private EditText etOldPassword, etNewPassword1, etNewPassword2;
    private ImageView ivShowOldPassword, ivShowNewPassword1, ivShowNewPassword2;
    private Button btnModify;
    private String password;
    private Boolean isInvisibleOldPassword, isInvisibleNewPassword1, isInvisibleNewPassword2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        mContext = this;
        loginId = HydrantApplication.getInstance().getAccount().getLoginId();
        MyToolbar toolbar = findViewById(R.id.set_password_toolbar);
        toolbar.initToolBar(this, toolbar, getString(R.string.ChangePassword), R.drawable.back_white, onClickListener);
        btnModify = findViewById(R.id.btn_modify);
        btnModify.setOnClickListener(onClickListener);
        etOldPassword = findViewById(R.id.et_oldPassword);
        etNewPassword1 = findViewById(R.id.et_newPassword1);
        etNewPassword2 = findViewById(R.id.et_newPassword2);
        etOldPassword.addTextChangedListener(textWatcher);
        etNewPassword1.addTextChangedListener(textWatcher);
        etNewPassword2.addTextChangedListener(textWatcher);
        ivShowOldPassword = findViewById(R.id.iv_showOldPassword);
        ivShowNewPassword1 = findViewById(R.id.iv_showNewPassword1);
        ivShowNewPassword2 = findViewById(R.id.iv_showNewPassword2);
        ivShowOldPassword.setOnClickListener(onClickListener);
        ivShowNewPassword1.setOnClickListener(onClickListener);
        ivShowNewPassword2.setOnClickListener(onClickListener);
        findViewById(R.id.tv_forgetPassword_login).setOnClickListener(onClickListener);
        isInvisibleOldPassword = true;
        isInvisibleNewPassword1 = true;
        isInvisibleNewPassword2 = true;
    }

    @Override
    protected void setStatusBar() {
        int mColor = getResources().getColor(R.color.colorPrimary);
        StatusBarUtil.setColor(this, mColor);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (TextUtils.isEmpty(etOldPassword.getText().toString()) || TextUtils.isEmpty(etNewPassword1.getText().toString()) ||
                    TextUtils.isEmpty(etNewPassword2.getText().toString())) {
                btnModify.setEnabled(false);
            } else {
                btnModify.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private View.OnClickListener onClickListener = (v) -> {
        switch (v.getId()) {
            case R.id.iv_left:
                ActivityController.finishActivity(ChangePasswordActivity.this);
                break;
            case R.id.btn_modify:
                String oldPassword = etOldPassword.getText().toString().trim();
                String newPassword1 = etNewPassword1.getText().toString().trim();
                String newPassword2 = etNewPassword2.getText().toString().trim();
                int passwordLength = 6;
                if (TextUtils.isEmpty(oldPassword)) {
                    showToast("请输入旧密码");
                } else if (TextUtils.isEmpty(newPassword1)) {
                    showToast("请输入新密码");
                } else if (TextUtils.isEmpty(newPassword2)) {
                    showToast("请再次输入新密码");
                } else if (!newPassword1.equals(newPassword2)) {
                    showToast("两次输入的密码不一致");
                } else if (newPassword1.length() < passwordLength) {
                    showToast("新密码长度小于6位");
                } else {
                    password = newPassword2;
                    modifyPassword(oldPassword, password);
                }
                break;
            case R.id.iv_showOldPassword:
                ViewUtils.changePasswordState(isInvisibleOldPassword, etOldPassword, ivShowOldPassword);
                isInvisibleOldPassword = !isInvisibleOldPassword;
                break;
            case R.id.iv_showNewPassword1:
                ViewUtils.changePasswordState(isInvisibleNewPassword1, etNewPassword1, ivShowNewPassword1);
                isInvisibleNewPassword1 = !isInvisibleNewPassword1;
                break;
            case R.id.iv_showNewPassword2:
                ViewUtils.changePasswordState(isInvisibleNewPassword2, etNewPassword2, ivShowNewPassword2);
                isInvisibleNewPassword2 = !isInvisibleNewPassword2;
                break;
            case R.id.tv_forgetPassword_login:
                //忘记密码
                openActivity(RetrievePasswordActivity.class);
                ActivityController.finishActivity(this);
                break;
            default:
                break;
        }
    };

    /**
     * 修改密码的方法
     *
     * @param oldPassword 原密码
     * @param newPassword 新密码
     */
    private void modifyPassword(String oldPassword, String newPassword) {
        Map<String, String> params = new HashMap<>(3);
        params.put("loginId", String.valueOf(loginId));
        params.put("oldpassword", oldPassword);
        params.put("newpassword", newPassword);
        Observable<NormalResult> normalResultObservable = NetClient.getInstances(NetClient.BASE_URL_PROJECT).getNjMeterApi().updatePassword(params);
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
                    showLoadingDialog(mContext, "更新密码中，请稍后", true);
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
                            SharedPreferencesUtils.getInstance().saveData("passWord_main", password);
                            showToast("密码更新成功");
                            Intent intent = new Intent(ChangePasswordActivity.this, LoginRegisterActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            ActivityController.finishActivity(ChangePasswordActivity.this);
                            break;
                        case Constants.FAIL:
                            showToast("密码更新失败，" + message);
                            break;
                        default:
                            showToast("密码更新失败");
                            break;
                    }
                }
            }
        });
    }
}
