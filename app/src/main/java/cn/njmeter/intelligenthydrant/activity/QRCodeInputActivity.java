package cn.njmeter.intelligenthydrant.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.jungly.gridpasswordview.GridPasswordView;

import cn.njmeter.intelligenthydrant.R;
import cn.njmeter.intelligenthydrant.qrcode.zxing.camera.CameraManager;
import cn.njmeter.intelligenthydrant.utils.StatusBarUtil;
import cn.njmeter.intelligenthydrant.widget.MyToolbar;

public class QRCodeInputActivity extends BaseActivity {

    public static boolean unlockSuccess = false;
    private ImageView mIvFlash;
    private Button mBtQuery;
    private GridPasswordView mPasswordView;
    private String carNub;
    private boolean flashLightOpen = false;
    private boolean isOnlyNub = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_input);
        CameraManager.init(getApplication());
        Intent intent = getIntent();
        if (intent != null) {
            isOnlyNub = intent.getBooleanExtra("isOnlyNub", false);
        }
        initView();
    }

    @Override
    protected void setStatusBar() {
        int mColor = getResources().getColor(R.color.colorPrimary);
        StatusBarUtil.setColor(this, mColor);
    }

    private void initView() {

        MyToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.initToolBar(this, toolbar, "输入消火栓编号", R.drawable.back_white, onClickListener);
        mIvFlash = findViewById(R.id.id_iv_flash);
        mBtQuery = findViewById(R.id.id_bt_query);
        mIvFlash.setOnClickListener(onClickListener);
        mBtQuery.setOnClickListener(onClickListener);
        mPasswordView = findViewById(R.id.pswView);
        mPasswordView.setPasswordVisibility(true);
        mPasswordView.setOnPasswordChangedListener(new GridPasswordView.OnPasswordChangedListener() {
            @Override
            public void onTextChanged(String psw) {
                mBtQuery.setBackgroundResource(R.color.smssdk_gray);
                mBtQuery.setClickable(false);
            }

            @Override
            public void onInputFinish(String psw) {
                carNub = psw;
                mBtQuery.setBackgroundResource(R.color.red);
                mBtQuery.setClickable(true);
            }
        });
    }


    /**
     * 切换散光灯状态
     */
    public void toggleFlashLight() {
        if (flashLightOpen) {
            setFlashLightOpen(false);
        } else {
            setFlashLightOpen(true);
        }
    }

    private void setFlashLightOpen(boolean open) {
        if (flashLightOpen == open)
            return;

        flashLightOpen = !flashLightOpen;
        CameraManager.get().setFlashLight(open);
    }

    private View.OnClickListener onClickListener = (view) -> {
        switch (view.getId()) {
            case R.id.id_iv_flash:
                flash();
                break;
            case R.id.id_bt_query:
                queryUnlock();
                break;
        }
    };

    public static Intent getMyIntent(Context context, boolean isOnlyNub) {
        Intent intent = new Intent(context, QRCodeInputActivity.class);
        intent.putExtra("isOnlyNub", isOnlyNub);
        return intent;
    }

    private void queryUnlock() {
        if (TextUtils.isEmpty(mPasswordView.getPassWord().toString())) {
            showToast("请输入消火栓编号");
            return;
        }
        if (mPasswordView.getPassWord().toString().length() != 8) {
            showToast("请输入完整的智能消火栓号码");
            return;
        }

        if (isOnlyNub) {
            Intent intent = new Intent();
            intent.putExtra("result", mPasswordView.getPassWord().toString());
            setResult(RESULT_OK, intent);
            finish();
        } else {
            unlockSuccess = true;
            startActivity(new Intent(QRCodeInputActivity.this, MainActivity.class));
            finish();
        }

    }

    private void flash() {
        if (flashLightOpen) {
            mIvFlash.setImageResource(R.drawable.scan_qrcode_flash_light_off);
        } else {
            mIvFlash.setImageResource(R.drawable.scan_qrcode_flash_light_on);
        }
        toggleFlashLight();
    }

    @Override
    protected void onPause() {
        super.onPause();
        CameraManager.get().closeDriver();
    }

    public void onDestroy() {
        super.onDestroy();
        unlockSuccess = false;
    }

}
