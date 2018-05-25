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
import cn.njmeter.intelligenthydrant.utils.ViewUtils;
import cn.njmeter.intelligenthydrant.widget.MyToolbar;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 设置公司信息
 * Created by LiYuliang on 2017/07/10 0010.
 *
 * @author LiYuliang
 * @version 2017/10/27
 */

public class SetCompanyActivity extends BaseActivity {

    private Context mContext;
    private int loginId;
    private EditText etCompanyName, etPosition;
    private String companyName, userPosition;
    private Button btnModify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_company);
        loginId = HydrantApplication.getInstance().getAccount().getLoginId();
        mContext = this;
        MyToolbar toolbar = findViewById(R.id.set_company_toolbar);
        toolbar.initToolBar(this, toolbar, getString(R.string.UnitsAndPositions), R.drawable.back_white, onClickListener);
        btnModify = findViewById(R.id.btn_modify);
        btnModify.setOnClickListener(onClickListener);
        etCompanyName = findViewById(R.id.et_companyName);
        etPosition = findViewById(R.id.et_position);
        etCompanyName.addTextChangedListener(textWatcher);
        etPosition.addTextChangedListener(textWatcher);
        etCompanyName.setText(HydrantApplication.getInstance().getAccount().getName_Company());
        etPosition.setText(HydrantApplication.getInstance().getAccount().getPosition_Company());
        ViewUtils.setCharSequence(etCompanyName);
        ViewUtils.setCharSequence(etPosition);
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
            if (TextUtils.isEmpty(etCompanyName.getText().toString()) || TextUtils.isEmpty(etPosition.getText().toString())) {
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
                ActivityController.finishActivity(this);
                break;
            case R.id.btn_modify:
                modifyCompany();
                break;
            default:
                break;
        }
    };

    /**
     * 更新单位信息
     */
    private void modifyCompany() {
        companyName = etCompanyName.getText().toString().trim();
        userPosition = etPosition.getText().toString().trim();
        if (TextUtils.isEmpty(companyName)) {
            showToast("请填写单位名称");
        } else if (TextUtils.isEmpty(userPosition)) {
            showToast("请填写您的职务");
        } else {
            Map<String, String> params = new HashMap<>(3);
            params.put("loginId", String.valueOf(loginId));
            params.put("name_Company", companyName);
            params.put("position_Company", userPosition);
            Observable<NormalResult> normalResultObservable = NetClient.getInstances(NetClient.BASE_URL_PROJECT).getNjMeterApi().updateCompany(params);
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
                                showToast("单位信息修改成功");
                                //更新本地单位和职务信息
                                HydrantApplication.getInstance().getAccount().setName_Company(companyName);
                                HydrantApplication.getInstance().getAccount().setPosition_Company(userPosition);
                                ActivityController.finishActivity(SetCompanyActivity.this);
                                break;
                            case Constants.FAIL:
                                showToast("单位信息修改失败，" + message);
                                break;
                            default:
                                showToast("单位信息修改失败");
                                break;
                        }
                    }
                }
            });
        }
    }
}
