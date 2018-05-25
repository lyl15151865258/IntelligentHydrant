package cn.njmeter.intelligenthydrant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jungly.gridpasswordview.GridPasswordView;
import com.jungly.gridpasswordview.PasswordType;

import java.io.IOException;

import cn.njmeter.intelligenthydrant.R;
import cn.njmeter.intelligenthydrant.qrcode.zxing.camera.CameraManager;
import cn.njmeter.intelligenthydrant.utils.ActivityController;
import cn.njmeter.intelligenthydrant.utils.StatusBarUtil;
import cn.njmeter.intelligenthydrant.widget.MyToolbar;

public class QRCodeInputActivity extends BaseActivity {

    public static boolean unlockSuccess = false;
    private ImageView mIvFlash;
    private TextView tvWarning;
    private Button mBtQuery;
    private GridPasswordView mPasswordView;
    private boolean flashLightOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_input);
        CameraManager.init(getApplication());
        try {
            CameraManager.get().openDriver(null);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
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
        mIvFlash = findViewById(R.id.iv_flash);
        tvWarning = findViewById(R.id.tv_warning);
        mBtQuery = findViewById(R.id.id_bt_query);
        mIvFlash.setOnClickListener(onClickListener);
        mBtQuery.setOnClickListener(onClickListener);
        mPasswordView = findViewById(R.id.pswView);
        mPasswordView.setPasswordVisibility(true);
        mPasswordView.setPasswordType(PasswordType.NUMBER);
        mPasswordView.setOnPasswordChangedListener(new GridPasswordView.OnPasswordChangedListener() {
            @Override
            public void onTextChanged(String psw) {
                mBtQuery.setBackgroundResource(R.color.smssdk_gray);
                mBtQuery.setClickable(false);
                tvWarning.setText("请确认您输入了正确的智能消火栓编号");
                tvWarning.setTextColor(getResources().getColor(R.color.white));
            }

            @Override
            public void onInputFinish(String psw) {
                mBtQuery.setBackgroundResource(R.color.red);
                mBtQuery.setClickable(true);
                tvWarning.setText("若因输错编码造成的他人用水，将由您自行承担责任");
                tvWarning.setTextColor(getResources().getColor(R.color.red_400));
            }
        });
    }


    /**
     * 切换闪光灯状态
     */
    public void toggleFlashLight() {
        if (flashLightOpen) {
            setFlashLightOpen(false);
        } else {
            setFlashLightOpen(true);
        }
    }

    private void setFlashLightOpen(boolean open) {
        if (flashLightOpen == open) return;

        flashLightOpen = !flashLightOpen;
        CameraManager.get().setFlashLight(open);
    }

    private View.OnClickListener onClickListener = (view) -> {
        switch (view.getId()) {
            case R.id.iv_flash:
                flash();
                break;
            case R.id.id_bt_query:
                queryUnlock();
                break;
        }
    };

    private void queryUnlock() {
        if (TextUtils.isEmpty(mPasswordView.getPassWord())) {
            showToast("请输入消火栓编号");
            return;
        }
        if (mPasswordView.getPassWord().length() != 8) {
            showToast("请输入完整的消火栓编号");
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("result", mPasswordView.getPassWord());
        setResult(RESULT_OK, intent);
        ActivityController.finishActivity(QRCodeInputActivity.this);
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
