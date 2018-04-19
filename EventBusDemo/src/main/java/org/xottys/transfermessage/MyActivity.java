/**
 * 本例演示了EventBus在Activity之间传递消息对象，它可以代替Handler、Intent、Broadcast等方式
 * 但是它要求必须Activity完全启动后才能收发消息
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

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MyActivity extends Activity {
    private static final String TAG = "EventBusDemo";
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_activity);
        Button bt1 = (Button) findViewById(R.id.bt1);
        Button bt2 = (Button) findViewById(R.id.bt2);
        tv = (TextView) findViewById(R.id.tv);

        bt1.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //结束本Activity
                finish();
            }
        });

        bt2.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //发送消息
                EventBus.getDefault().post(new MessageEvent("从MyActivity发出的消息"));
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        //订阅消息
        EventBus.getDefault().register(this);

    }

    @Override
    public void onStop() {
        super.onStop();
        //解除订阅消息
        EventBus.getDefault().unregister(this);

    }

    //用缺省方式接收和处理消息，此时消息在哪个线程发布，它就会在这个线程中运行，也就是说发布事件和接收事件线程在同一个线程
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onMessageEvent(MessageEvent event) {
        tv.setText(event.getMsg());

        Message message = Message.obtain();
        message.obj = "onMyActivity:" + event.getMsg();
        MainActivity.handler.sendMessage(message);
        Log.i(TAG, "onMyActivity:" + event.getMsg());
    }
}