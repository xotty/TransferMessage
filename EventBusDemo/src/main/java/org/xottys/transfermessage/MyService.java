/**
 * 本例演示了EventBus在Service之间传递消息对象，它可以代替Handler、Intent、Broadcast等方式
 * 但是它要求必须Service完全启动后才能收发消息
 * <p>
 * <br/>Copyright (C), 2017-2018, Steve Chang
 * <br/>This program is protected by copyright laws.
 * <br/>Program Name:EventBusDEMO
 * <br/>Date:Aug，2017
 *
 * @author xottys@163.com
 * @version 1.0
 */
package org.xottys.transfermessage;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class MyService extends Service {

    private static final String TAG = "EventBusDemo";


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //订阅消息
        EventBus.getDefault().register(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //解除订阅消息
        EventBus.getDefault().unregister(this);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //发布消息
        EventBus.getDefault().post(new MessageEvent("从Service发出的消息"));

        return START_NOT_STICKY;
    }

    //用后台方式接收和处理消息，
    //此时如果消息是在UI线程中发布的，那么它就会在子线程中运行；如果事件本来就是子线程中发布出来的，它就直接在该子线程中执行
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(MessageEvent event) {
        Message message = Message.obtain();
        message.obj = "onService:" + event.getMsg();
        MainActivity.handler.sendMessage(message);

        Log.i(TAG, "onService:" + event.getMsg());
    }
}
