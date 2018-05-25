package cn.njmeter.intelligenthydrant.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.njmeter.intelligenthydrant.bean.EventMsg;
import cn.njmeter.intelligenthydrant.constant.Constants;
import cn.njmeter.intelligenthydrant.constant.NetWork;

/**
 * Created by LiYuliang on 2017/12/08.
 * socket连接服务
 *
 * @author LiYuliang
 * @version 1.1.0
 */
public class SocketService extends Service {

    public static final String TAG = "SocketService";

    /**
     * 线程池
     */
    public static ExecutorService executorService;

    private Socket socket;
    /**
     * 连接线程
     */
    private Runnable connectThread;

    private SocketBinder socketBinder = new SocketBinder();
    private String ip;
    private String port;
    private String loginId;
    private TimerTask task;

    /**
     * 默认重连
     */
    private boolean isReConnect = true;

    private Handler mHandler = new Handler();
    /**
     * 消息广播
     */
    public static final String MESSAGE_ACTION = "MESSAGE_ACTION";
    /**
     * 发送或接收到消息的时间毫秒值，用于减少心跳包的发送次数
     */
    private long sendTime = 0L;


    @Override
    public IBinder onBind(Intent intent) {
        return socketBinder;
    }


    public class SocketBinder extends Binder {
        /**
         * 返回SocketService 在需要的地方可以通过ServiceConnection获取到SocketService
         *
         * @return SocketService对象
         */
        public SocketService getService() {
            return SocketService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        executorService = new ThreadPoolExecutor(5, 10, 60, TimeUnit.SECONDS,
                new SynchronousQueue<>(), (r) -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //拿到传递过来的ip和端口号
        if (intent != null) {
            ip = intent.getStringExtra("ip");
            port = intent.getStringExtra("port");
            loginId = intent.getStringExtra("loginId");
            //初始化socket
            initSocket();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 初始化Socket
     */
    private void initSocket() {
        if (socket == null && connectThread == null) {
            socket = new Socket();
            connectThread = () -> {
                Log.i(TAG, "创建线程：" + Thread.currentThread().getId());
                try {
                    //超时时间为5秒
                    socket.connect(new InetSocketAddress(ip, Integer.valueOf(port)), 5000);
                    //连接成功的话
                    if (socket.isConnected()) {
                        //发送注册信息成功，则开始Socket通信
                        if (sendMsg(NetWork.ANDROID_LOGIN + loginId + NetWork.ANDROID_END)) {

                            showToast("Socket连接成功");

                            //发送连接成功的消息
                            EventMsg msg = new EventMsg();
                            msg.setTag(Constants.CONNECT_SUCCESS_SOCKET);
                            EventBus.getDefault().post(msg);

                            // 连接成功后，就准备发送心跳包
                            executorService.submit(heartBeatRunnable);

                            InputStream is = socket.getInputStream();
                            byte[] buffer = new byte[1024 * 4];
                            int length;
                            while (!socket.isClosed() && !socket.isInputShutdown()
                                    && isReConnect && ((length = is.read(buffer)) != -1)) {
                                if (length > 0) {
                                    String message = new String(Arrays.copyOf(buffer, length)).trim();
                                    Log.i(TAG, "收到服务器发送来的消息：" + message);
                                    sendTime = System.currentTimeMillis();
                                    Log.i(TAG, "收到消息的时间：" + sendTime);
                                    // 收到服务器过来的消息，就通过EventBus发送出去
                                    EventMsg eventMsg = new EventMsg();
                                    eventMsg.setMsg(message);
                                    eventMsg.setTag(Constants.SHOW_DATA_SOCKET);
                                    EventBus.getDefault().post(eventMsg);
                                }
                            }
                        }
                    } else {
                        showToast("未能连接到服务器");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    EventMsg msg = new EventMsg();
                    msg.setTag(Constants.CONNECT_FAIL_SOCKET);
                    EventBus.getDefault().post(msg);
                    if (e instanceof SocketTimeoutException) {
                        showToast("连接超时，正在重连");
                        releaseSocket();
                    } else if (e instanceof NoRouteToHostException) {
                        showToast("Socket地址不存在，请退出检查");
                    } else if (e instanceof ConnectException) {
                        showToast("网络异常或Socket地址不存在，请检查");
                        try {
                            //等待5秒后再重新请求
                            Thread.sleep(5000);
                            releaseSocket();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            };
            /*启动连接线程*/
            executorService.submit(connectThread);
        }
    }

    /**
     * 因为Toast是要运行在主线程的，所以发送给Activity去显示
     *
     * @param msg 要弹出的文本
     */
    private void showToast(final String msg) {
        EventMsg eventMsg = new EventMsg();
        eventMsg.setTag(Constants.SHOW_TOAST_SOCKET);
        eventMsg.setMsg(msg);
        EventBus.getDefault().post(eventMsg);
    }

    /**
     * 发送心跳包
     */
    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            if (System.currentTimeMillis() - sendTime >= NetWork.HEART_BEAT_RATE) {
                // 心跳包只需发送一个\r\n过去, 以节约数据流量
                // 如果发送失败，就重新初始化一个socket
                Runnable runnable = () -> {
                    boolean isSuccess = sendMsg(NetWork.HEART_BEAT_PACKAGE);
                    if (!isSuccess) {
                        EventMsg msg = new EventMsg();
                        msg.setTag(Constants.CONNECT_FAIL_SOCKET);
                        EventBus.getDefault().post(msg);
                        showToast("连接断开，正在重连");
                        releaseSocket();
                    }
                };
                executorService.submit(runnable);
            }
            mHandler.postDelayed(this, NetWork.HEART_BEAT_RATE);
        }
    };

    public boolean sendMsg(final String msg) {
        Log.i(TAG, "发送的内容为：" + msg);
        //发送成功失败的标记
        boolean isSendSuccess;
        if (null == socket || !socket.isConnected()) {
            isSendSuccess = false;
        } else {
            try {
                if (!socket.isClosed() && !socket.isOutputShutdown()) {
                    OutputStream os = socket.getOutputStream();
                    os.write(msg.getBytes("UTF-8"));
                    os.flush();
                    // 每次发送成功数据，就改一下最后成功发送的时间，节省心跳间隔时间
                    sendTime = System.currentTimeMillis();
                    Log.i(TAG, "发送成功的时间：" + sendTime);
                    isSendSuccess = true;
                } else {
                    isSendSuccess = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                isSendSuccess = false;
            }
        }
        return isSendSuccess;
    }

    /**
     * 释放资源
     */
    private void releaseSocket() {
        mHandler.removeCallbacks(heartBeatRunnable);
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            socket = null;
        }
        if (connectThread != null) {
            connectThread = null;
        }
        //重新初始化socket
        if (isReConnect) {
            initSocket();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isReConnect = false;
        releaseSocket();
        mHandler.removeCallbacksAndMessages(null);
        executorService.shutdown();
    }
}
