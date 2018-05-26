package cn.njmeter.intelligenthydrant.loginregister.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pkmmte.view.CircularImageView;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;
import cn.njmeter.intelligenthydrant.HydrantApplication;
import cn.njmeter.intelligenthydrant.R;
import cn.njmeter.intelligenthydrant.activity.BaseActivity;
import cn.njmeter.intelligenthydrant.activity.HtmlActivity;
import cn.njmeter.intelligenthydrant.activity.MainActivity;
import cn.njmeter.intelligenthydrant.constant.ApkInfo;
import cn.njmeter.intelligenthydrant.loginregister.bean.ClientUser;
import cn.njmeter.intelligenthydrant.constant.Constants;
import cn.njmeter.intelligenthydrant.network.ExceptionHandle;
import cn.njmeter.intelligenthydrant.network.NetClient;
import cn.njmeter.intelligenthydrant.network.NetworkSubscriber;
import cn.njmeter.intelligenthydrant.utils.ActivityController;
import cn.njmeter.intelligenthydrant.utils.ApkUtils;
import cn.njmeter.intelligenthydrant.utils.CipherUtils;
import cn.njmeter.intelligenthydrant.utils.GsonUtils;
import cn.njmeter.intelligenthydrant.utils.LogUtils;
import cn.njmeter.intelligenthydrant.utils.NetworkUtil;
import cn.njmeter.intelligenthydrant.utils.RegexUtils;
import cn.njmeter.intelligenthydrant.utils.SharedPreferencesUtils;
import cn.njmeter.intelligenthydrant.utils.StatusBarUtil;
import cn.njmeter.intelligenthydrant.utils.ViewUtils;
import cn.smssdk.OnSendMessageHandler;
import cn.smssdk.SMSSDK;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 主账号登录注册
 * Created by LiYuliang on 2017/07/10 0010.
 *
 * @author LiYuliang
 * @version 2017/10/20
 */

public class LoginRegisterActivity extends BaseActivity {

    private EditText etPhoneNumberLogin, etPassWordLogin, etPhoneNumberRegister, etSmsCode, etPassWordRegister, etConfirmRegister;
    private Button btnRegister;
    private TextView tvGetConfirmCode;
    private RelativeLayout rlLogin;
    private LinearLayout llRegister;
    private CircularImageView ivUserIcon;
    private Context context;
    private MyHandler myHandler = new MyHandler(this);
    /**
     * 短信验证码重复发送倒计时
     */
    private int i = 30;
    /**
     * 线程池
     */
    public static ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        context = this;
        executorService = new ThreadPoolExecutor(1, Runtime.getRuntime().availableProcessors(), 60, TimeUnit.SECONDS,
                new SynchronousQueue<>(), (r) -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        });
        // 创建EventHandler对象
        String userName = (String) SharedPreferencesUtils.getInstance().getData("userName_main", Constants.EMPTY);
        String passWord = (String) SharedPreferencesUtils.getInstance().getData("passWord_main", Constants.EMPTY);
        ivUserIcon = findViewById(R.id.iv_userIcon);
        etPhoneNumberLogin = findViewById(R.id.et_phoneNumber_login);
        etPassWordLogin = findViewById(R.id.et_passWord_login);
        etPhoneNumberLogin.setText(userName);
        etPassWordLogin.setText(passWord);
        ViewUtils.setCharSequence(etPhoneNumberLogin);
        ViewUtils.setCharSequence(etPassWordLogin);
        etPassWordLogin.setOnEditorActionListener(onEditorActionListener);
        findViewById(R.id.tv_showRegister).setOnClickListener(onClickListener);
        findViewById(R.id.tv_forgetPassword_login).setOnClickListener(onClickListener);
        findViewById(R.id.btn_Login).setOnClickListener(onClickListener);
        etPhoneNumberRegister = findViewById(R.id.et_phoneNumber_register);
        etPassWordRegister = findViewById(R.id.et_passWord_register);
        etConfirmRegister = findViewById(R.id.et_confirm_register);
        etSmsCode = findViewById(R.id.et_sms_code);
        tvGetConfirmCode = findViewById(R.id.tv_getConfirmCode);
        btnRegister = findViewById(R.id.btn_register);
        rlLogin = findViewById(R.id.rl_login);
        llRegister = findViewById(R.id.ll_register);
        tvGetConfirmCode.setOnClickListener(onClickListener);
        btnRegister.setOnClickListener(onClickListener);
        TextView tvLogin = findViewById(R.id.tv_showLogin);
        TextView tvForgetPassword = findViewById(R.id.tv_forgetPassword_register);
        tvLogin.setOnClickListener(onClickListener);
        tvForgetPassword.setOnClickListener(onClickListener);
        CheckBox checkboxAgree = findViewById(R.id.checkbox_agree);
        checkboxAgree.setOnCheckedChangeListener(onCheckedChangeListener);
        TextView tvRegistrationProtocol = findViewById(R.id.tv_registration_protocol);
        setTextStyle(tvRegistrationProtocol);
        tvRegistrationProtocol.setOnClickListener(onClickListener);
        (findViewById(R.id.ll_login_by_qq)).setOnClickListener(onClickListener);
        (findViewById(R.id.ll_login_by_wechat)).setOnClickListener(onClickListener);
        tvRegistrationProtocol.setOnClickListener(onClickListener);
        //展示头像
        showUserIcon(((String) SharedPreferencesUtils.getInstance().getData("userIconPath", "")).replace("\\", "/"));
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setTranslucentForImageView(this, findViewById(R.id.toolbar));
    }

    private TextView.OnEditorActionListener onEditorActionListener = (textView, actionId, keyEvent) -> {
        boolean isGo = actionId == EditorInfo.IME_ACTION_GO || (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER);
        if (isGo) {
            login();
        }
        return false;
    };

    /**
     * 加载头像
     *
     * @param photoPath 头像路径
     */
    private void showUserIcon(String photoPath) {
        LogUtils.d(NetClient.TAG_POST, "图片路径：" + photoPath);
        if (photoPath != null) {
            Glide.with(this).load(photoPath)
                    .error(R.drawable.user_icon)
                    .placeholder(R.drawable.user_icon)
                    .dontAnimate()
                    .into(ivUserIcon);
        }
    }

    /**
     * 设置字体格式
     *
     * @param textView 文本控件
     */
    private void setTextStyle(TextView textView) {
        String text = textView.getText().toString();
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        ForegroundColorSpan blueSpan = new ForegroundColorSpan(Color.BLUE);
        UnderlineSpan lineSpan = new UnderlineSpan();
        int start = text.indexOf(getString(R.string.RegistrationProtocol));
        int end = start + getString(R.string.RegistrationProtocol).length();
        //下划线
        builder.setSpan(lineSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //字体颜色
        builder.setSpan(blueSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(builder);
    }

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                btnRegister.setEnabled(true);
            } else {
                btnRegister.setEnabled(false);
            }
        }
    };

    /**
     * 是否接收短信的验证
     */
    private OnSendMessageHandler onSendMessageHandler = (s, s1) -> {
        //此方法在发送验证短信前被调用，传入参数为接收者号码,返回true表示此号码无须实际接收短信
        return false;
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_forgetPassword_login:
                    //登录页面的忘记密码
                case R.id.tv_forgetPassword_register:
                    //注册页面的忘记密码
                    openActivity(RetrievePasswordActivity.class);
                    break;
                case R.id.btn_Login:
                    login();
                    break;
                case R.id.ll_login_by_qq:
//                    authorization(SHARE_MEDIA.QQ);
                    showToast("暂未开放QQ登陆");
                    break;
                case R.id.ll_login_by_wechat:
//                    authorization(SHARE_MEDIA.WEIXIN);
                    showToast("暂未开放微信登陆");
                    break;
                case R.id.tv_showRegister:
                    //切换到注册页面
                    rlLogin.setVisibility(View.GONE);
                    llRegister.setVisibility(View.VISIBLE);
                    break;
                case R.id.tv_getConfirmCode:
                    //获取短信验证码
                    String phoneNumber = etPhoneNumberRegister.getText().toString().trim();
                    if (TextUtils.isEmpty(phoneNumber)) {
                        showToast("请输入手机号");
                        return;
                    }
                    if (!RegexUtils.checkMobile(phoneNumber)) {
                        showToast("请输入正确的手机号");
                        return;
                    }
                    SMSSDK.getVerificationCode("86", phoneNumber, onSendMessageHandler);
                    tvGetConfirmCode.setClickable(false);
                    tvGetConfirmCode.setText(String.format(getString(R.string.resend_info_time), i));
                    Runnable runnable = () -> {
                        for (; i > 0; i--) {
                            Message msg7 = myHandler.obtainMessage();
                            msg7.arg1 = 7;
                            myHandler.sendMessage(msg7);
                            if (i <= 0) {
                                break;
                            }
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        Message msg8 = myHandler.obtainMessage();
                        msg8.arg1 = 8;
                        myHandler.sendMessage(msg8);
                    };
                    executorService.submit(runnable);
                    break;
                case R.id.tv_showLogin:
                    //切换到登录页面
                    rlLogin.setVisibility(View.VISIBLE);
                    llRegister.setVisibility(View.GONE);
                    break;
                case R.id.tv_registration_protocol:
                    //打开用户注册协议
                    Intent intent = new Intent(LoginRegisterActivity.this, HtmlActivity.class);
                    String url = "file:////android_asset/html/RegisterProtocols/index.html";
                    intent.putExtra("title", getString(R.string.RegistrationProtocol));
                    intent.putExtra("URL", url);
                    startActivity(intent);
                    break;
                case R.id.btn_register:
                    //点击注册按钮的逻辑
                    String userNameRegister = etPhoneNumberRegister.getText().toString().trim();
                    String smsCodeRegister = etSmsCode.getText().toString().trim();
                    String passWordRegister = etPassWordRegister.getText().toString().trim();
                    String confirm = etConfirmRegister.getText().toString().trim();
                    if (TextUtils.isEmpty(userNameRegister)) {
                        showToast("请输入手机号");
                        return;
                    }
                    if (!RegexUtils.checkMobile(userNameRegister)) {
                        showToast("请输入正确的手机号");
                        return;
                    }
                    if (TextUtils.isEmpty(smsCodeRegister)) {
                        showToast("请输入短信验证码");
                        return;
                    }
                    if (TextUtils.isEmpty(passWordRegister)) {
                        showToast("请输入密码");
                        return;
                    }
                    int passwordLength = 6;
                    if (passWordRegister.length() < passwordLength) {
                        showToast("密码长度小于6位");
                        return;
                    }
                    if (TextUtils.isEmpty(confirm)) {
                        showToast("请再次输入密码");
                        return;
                    }
                    if (passWordRegister.equals(confirm)) {
                        //校验手机验证码
                        confirmCode();
                    } else {
                        showToast("两次输入的密码不一致");
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 验证手机验证码
     */
    private void confirmCode() {
        String phoneNumber = etPhoneNumberRegister.getText().toString();
        String smsCode = etSmsCode.getText().toString();
        SMSSDK.submitVerificationCode("86", phoneNumber, smsCode);
    }

    /**
     * 主账号登录
     */
    private void login() {
        LogUtils.d("login", "LoginRegisterActivity登陆方法");
        String userName = etPhoneNumberLogin.getText().toString().trim();
        String passWord = etPassWordLogin.getText().toString().trim();
        if (TextUtils.isEmpty(userName)) {
            showToast("请输入用户名");
            return;
        }
        if (TextUtils.isEmpty(passWord)) {
            showToast("请输入密码");
            return;
        }
        Map<String, String> params = new HashMap<>(4);
        params.put("loginName", userName);
        params.put("password", passWord);
        params.put("versionCode", String.valueOf(ApkUtils.getVersionCode(context)));
        params.put("apkTypeId", ApkInfo.APK_TYPE_ID_HYDRANT);
        Observable<String> clientUserObservable = NetClient.getInstances(NetClient.BASE_URL_PROJECT).getNjMeterApi().loginMainAccount(params);
        clientUserObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new NetworkSubscriber<String>(context, getClass().getSimpleName()) {

            @Override
            public void onStart() {
                super.onStart();
                //接下来可以检查网络连接等操作
                if (!NetworkUtil.isNetworkAvailable(context)) {
                    showToast("当前网络不可用，请检查网络");
                    if (!isUnsubscribed()) {
                        unsubscribe();
                    }
                } else {
                    showLoadingDialog(context, "登陆中", true);
                }
            }

            @Override
            public void onError(ExceptionHandle.ResponseThrowable responseThrowable) {
                cancelDialog();
                showToast("" + responseThrowable.message);
            }

            @Override
            public void onNext(String s) {
                cancelDialog();
                try {
                    CipherUtils des = new CipherUtils(NetClient.SECRET_KRY);
                    String result = des.decrypt(s);
                    ClientUser clientUser = GsonUtils.parseJSON((result), ClientUser.class);
                    String mark = clientUser.getResult();
                    String message = clientUser.getMsg();
                    switch (mark) {
                        case Constants.SUCCESS:
                            SharedPreferencesUtils.getInstance().saveData("account", GsonUtils.convertJSON(clientUser));
                            HydrantApplication.loginSuccess = true;
                            LogUtils.d("登陆成功");
                            //保存用户名密码
                            String phoneNumber = etPhoneNumberLogin.getText().toString();
                            String passWord = etPassWordLogin.getText().toString();
                            SharedPreferencesUtils.getInstance().saveData("userName_main", phoneNumber);
                            SharedPreferencesUtils.getInstance().saveData("passWord_main", passWord);
                            HydrantApplication.getInstance().setAccount(clientUser.getAccount());
                            HydrantApplication.getInstance().setVersion(clientUser.getVersion());
                            HydrantApplication.getInstance().setVersion2(clientUser.getVersion2());
                            HydrantApplication.getInstance().serverList = clientUser.getServer();
                            //注册极光推送别名
                            JPushInterface.setAlias(getApplicationContext(), 0, String.valueOf(HydrantApplication.getInstance().getAccount().getLoginId()));
                            openActivity(MainActivity.class);
                            ActivityController.finishActivity(LoginRegisterActivity.this);
                            break;
                        case Constants.FAIL:
                            showToast("登陆失败，" + message);
                            break;
                        default:
                            showToast("登陆失败");
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 主账号注册
     */
    private void register() {
        String userName = etPhoneNumberRegister.getText().toString().trim();
        String passWord = etPassWordRegister.getText().toString().trim();
        if (TextUtils.isEmpty(userName)) {
            showToast("请输入用户名");
            return;
        }
        if (TextUtils.isEmpty(passWord)) {
            showToast("请输入密码");
            return;
        }
        Map<String, String> params = new HashMap<>(2);
        params.put("loginName", userName);
        params.put("password", passWord);
        Observable<ClientUser> clientUserCall = NetClient.getInstances(NetClient.BASE_URL_PROJECT).getNjMeterApi().registerMainAccount(params);
        clientUserCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new NetworkSubscriber<ClientUser>(context, getClass().getSimpleName()) {

            @Override
            public void onStart() {
                super.onStart();
                //接下来可以检查网络连接等操作
                if (!NetworkUtil.isNetworkAvailable(context)) {
                    showToast("当前网络不可用，请检查网络");
                    if (!isUnsubscribed()) {
                        unsubscribe();
                    }
                } else {
                    showLoadingDialog(context, "注册中", true);
                }
            }

            @Override
            public void onError(ExceptionHandle.ResponseThrowable responseThrowable) {
                cancelDialog();
                showToast("" + responseThrowable.message);
            }

            @Override
            public void onNext(ClientUser clientUser) {
                cancelDialog();
                if (clientUser == null) {
                    showToast("注册失败，返回值异常");
                } else {
                    String mark = clientUser.getResult();
                    String message = clientUser.getMsg();
                    switch (mark) {
                        case Constants.SUCCESS:
                            //保存用户名密码
                            String phoneNumber = etPhoneNumberRegister.getText().toString();
                            String passWord = etPassWordRegister.getText().toString();
                            SharedPreferencesUtils.getInstance().saveData("userName_main", phoneNumber);
                            SharedPreferencesUtils.getInstance().saveData("passWord_main", passWord);
                            HydrantApplication.getInstance().setAccount(clientUser.getAccount());
                            HydrantApplication.getInstance().setVersion(clientUser.getVersion());
                            HydrantApplication.getInstance().setVersion2(clientUser.getVersion2());
                            HydrantApplication.getInstance().serverList = clientUser.getServer();
                            showToast("注册成功");
                            //切换到登录页面
                            rlLogin.setVisibility(View.VISIBLE);
                            llRegister.setVisibility(View.GONE);
                            etPhoneNumberLogin.setText(phoneNumber);
                            etPassWordLogin.setText(passWord);
                            break;
                        case Constants.FAIL:
                            showToast("注册失败，" + message);
                            break;
                        default:
                            showToast("注册失败");
                            break;
                    }
                }
            }
        });
    }

    private static class MyHandler extends Handler {
        WeakReference<LoginRegisterActivity> mActivity;

        MyHandler(LoginRegisterActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final LoginRegisterActivity theActivity = mActivity.get();
            switch (msg.arg1) {
                case 7:
                    theActivity.tvGetConfirmCode.setText(String.format(theActivity.getString(R.string.resend_info_time), theActivity.i));
                    break;
                case 8:
                    theActivity.tvGetConfirmCode.setText("获取验证码");
                    theActivity.tvGetConfirmCode.setClickable(true);
                    theActivity.i = 30;
                    break;
                case 9:
                    int event = msg.getData().getInt("event");
                    int result9 = msg.getData().getInt("result");
                    Object data = msg.obj;
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        if (result9 == SMSSDK.RESULT_COMPLETE) {
                            //验证码验证成功
                            //由于找回密码页面也有短信监听，所以需要先判断栈顶的Activity是否是当前Activity对象
                            AppCompatActivity currentActivity = (AppCompatActivity) ActivityController.getInstance().getCurrentActivity();
                            if (currentActivity instanceof LoginRegisterActivity) {
                                theActivity.register();
                            }
                        } else if (result9 == SMSSDK.RESULT_ERROR) {
                            theActivity.showToast("验证码输入错误");
                        }
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        if (result9 == SMSSDK.RESULT_COMPLETE) {
                            theActivity.showToast("验证码已经发送");
                        } else if (result9 == SMSSDK.RESULT_ERROR) {
                            theActivity.showToast("验证码发送失败");
                        }
                    } else {
                        ((Throwable) data).printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myHandler.removeCallbacksAndMessages(null);
        executorService.shutdown();
    }
}
