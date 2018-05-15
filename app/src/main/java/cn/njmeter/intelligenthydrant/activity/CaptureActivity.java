package cn.njmeter.intelligenthydrant.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.io.IOException;
import java.util.Vector;

import cn.njmeter.intelligenthydrant.R;
import cn.njmeter.intelligenthydrant.qrcode.common.ActionUtils;
import cn.njmeter.intelligenthydrant.qrcode.common.QrUtils;
import cn.njmeter.intelligenthydrant.qrcode.zxing.camera.CameraManager;
import cn.njmeter.intelligenthydrant.qrcode.zxing.decoding.CaptureActivityHandler;
import cn.njmeter.intelligenthydrant.qrcode.zxing.decoding.InactivityTimer;
import cn.njmeter.intelligenthydrant.qrcode.zxing.view.ViewfinderView;
import cn.njmeter.intelligenthydrant.utils.ActivityController;
import cn.njmeter.intelligenthydrant.utils.StatusBarUtil;
import cn.njmeter.intelligenthydrant.widget.MyToolbar;

/**
 * Initial the camera
 *
 * @author Ryan.Tang
 */
public class CaptureActivity extends BaseActivity implements Callback {

    private static final int REQUEST_PERMISSION_CAMERA = 1000;
    private static final int REQUEST_PERMISSION_PHOTO = 1001;
    private static final int REQUEST_HYDRANT_NUMBER = 22;

    private CaptureActivity mActivity;

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private boolean flashLightOpen = false;
    private ImageView flashIbtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mActivity = this;
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        CameraManager.init(getApplication());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
            }
        }
        initView();
    }

    @Override
    protected void setStatusBar() {
        int mColor = getResources().getColor(R.color.colorPrimary);
        StatusBarUtil.setColor(this, mColor);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        final AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        if (flashIbtn != null) {
            flashIbtn.setImageResource(R.drawable.scan_qrcode_flash_light_off);
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ActionUtils.PHOTO_REQUEST_GALLERY:
                if (resultCode == RESULT_OK && data != null) {
                    Uri inputUri = data.getData();
                    String path = null;

                    if (URLUtil.isFileUrl(inputUri.toString())) {
                        // 小米手机直接返回的文件路径
                        path = inputUri.getPath();
                    } else {
                        String[] proj = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(inputUri, proj, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                        }
                    }
                    if (!TextUtils.isEmpty(path)) {
                        Result result = QrUtils.decodeImage(path);
                        if (result != null) {
                            handleDecode(result, null);
                        } else {
                            new AlertDialog.Builder(CaptureActivity.this)
                                    .setTitle("提示")
                                    .setMessage("此图片无法识别")
                                    .setPositiveButton("确定", null)
                                    .show();
                        }
                    } else {
                        Toast.makeText(mActivity, "图片路径未找到", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case REQUEST_HYDRANT_NUMBER:
                if (RESULT_OK == resultCode) {
                    Intent intent = new Intent();
                    intent.putExtra("result", data.getStringExtra("result"));
                    setResult(RESULT_OK, intent);
                    ActivityController.finishActivity(this);
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && requestCode == REQUEST_PERMISSION_CAMERA) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // 未获得Camera权限
                new AlertDialog.Builder(mActivity)
                        .setTitle("提示")
                        .setMessage("请在系统设置中为App开启摄像头权限后重试")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityController.finishActivity(mActivity);
                            }
                        })
                        .show();
            }
        } else if (grantResults.length > 0 && requestCode == REQUEST_PERMISSION_PHOTO) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                new AlertDialog.Builder(mActivity)
                        .setTitle("提示")
                        .setMessage("请在系统设置中为App中开启文件权限后重试")
                        .setPositiveButton("确定", null)
                        .show();
            } else {
                ActionUtils.startActivityForGallery(mActivity, ActionUtils.PHOTO_REQUEST_GALLERY);
            }
        }
    }

    /**
     * Handler scan result
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        handleResult(resultString);
    }

    protected void handleResult(String resultString) {
        if (resultString.equals("")) {
            Toast.makeText(CaptureActivity.this, R.string.scan_failed, Toast.LENGTH_SHORT).show();
        } else {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("result", resultString);
            resultIntent.putExtras(bundle);
            this.setResult(RESULT_OK, resultIntent);
        }
        ActivityController.finishActivity(mActivity);
    }

    protected void initView() {
        setContentView(R.layout.qr_camera);

        MyToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.initToolBar(this, toolbar, "扫描二维码", R.drawable.back_white, onClickListener);

        viewfinderView = findViewById(R.id.viewfinder_view);
        flashIbtn = findViewById(R.id.flash_ibtn);

        findViewById(R.id.ll_input).setOnClickListener(onClickListener);
        findViewById(R.id.ll_flashLight).setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = (view) -> {
        switch (view.getId()) {
            case R.id.iv_left:
                ActivityController.finishActivity(this);
                break;
            case R.id.ll_input:
                Intent intent = new Intent(CaptureActivity.this, QRCodeInputActivity.class);
                startActivityForResult(intent, REQUEST_HYDRANT_NUMBER);
                break;
            case R.id.ll_flashLight:
                if (flashLightOpen) {
                    flashIbtn.setImageResource(R.drawable.scan_qrcode_flash_light_off);
                } else {
                    flashIbtn.setImageResource(R.drawable.scan_qrcode_flash_light_on);
                }
                toggleFlashLight();
                break;
            default:
                break;
        }
    };

    protected void setViewfinderView(ViewfinderView view) {
        viewfinderView = view;
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

    /**
     * 设置闪光灯是否打开
     *
     * @param open
     */
    public void setFlashLightOpen(boolean open) {
        if (flashLightOpen == open) return;

        flashLightOpen = !flashLightOpen;
        CameraManager.get().setFlashLight(open);
    }

    /**
     * 当前散光灯是否打开
     *
     * @return
     */
    public boolean isFlashLightOpen() {
        return flashLightOpen;
    }

    /**
     * 打开相册
     */
    public void openGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_PHOTO);
        } else {
            ActionUtils.startActivityForGallery(mActivity, ActionUtils.PHOTO_REQUEST_GALLERY);
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    protected void restartPreview() {
        // 当界面跳转时 handler 可能为null
        if (handler != null) {
            Message restartMessage = Message.obtain();
            restartMessage.what = R.id.restart_preview;
            handler.handleMessage(restartMessage);
        }
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    /**
     * yiwen add
     *
     * @param context
     * @param isOnlyNub
     * @return Intent
     */

    public static Intent getMyIntent(Context context, boolean isOnlyNub) {
        Intent intent = new Intent(context, CaptureActivity.class);
        intent.putExtra("isOnlyNub", isOnlyNub);
        return intent;
    }

}