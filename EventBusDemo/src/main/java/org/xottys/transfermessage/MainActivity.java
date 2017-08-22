/**
 * 本例演示了第三方消息传递框架EventBus的用法，它可以在Activity、Thread、Service之间传递消息对象
 * 1）定义消息对象，如MessageEvent.java
 * 2）订阅（注册）消息接收者，register/unregister
 * 3）实现消息接收方法：onMessageEvent(MessageEvent msg)，共有POST、MAIN、BACKGROUND、ASYN四种消息处理模式
 * 4）发送消息：post(MessageEvent msg)
 * 优点是开销小，速度快，代码更简洁以及将发送者和接收者解耦
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
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends Activity {
    private static final String TAG = "EventBusDemo";
    public static Handler handler;
    private Button bt1, bt2, bt3;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt1 = (Button) findViewById(R.id.bt1);
        bt2 = (Button) findViewById(R.id.bt2);
        bt3 = (Button) findViewById(R.id.bt3);

        bt1.setBackgroundColor(0xbd292f34);
        bt1.setTextColor(0xFFFFFFFF);
        bt2.setBackgroundColor(0xbd292f34);
        bt2.setTextColor(0xFFFFFFFF);
        bt3.setBackgroundColor(0xbd292f34);
        bt3.setTextColor(0xFFFFFFFF);
        tv = (TextView) findViewById(R.id.tv);

        //订阅消息
        EventBus.getDefault().register(this);

        //Thread启动和消息传递
        bt1.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                tv.setText("");
                new MyThread().start();
            }
        });

        //Activity启动和消息传递
        bt2.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                tv.setText("");
                Intent intent = new Intent(MainActivity.this, MyActivity.class);   //显式启动Activity
                startActivity(intent);
            }
        });

        //Service启动和消息传递
        bt3.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                tv.setText("");
                Intent intent = new Intent(MainActivity.this, MyService.class);
                startService(intent);
            }
        });

        //收到消息后统一显示
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                tv.append(msg.obj.toString() + "\n");

            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();

        //发布消息
        EventBus.getDefault().post(new MessageEvent("从MainActivity发出的消息"));
    }

    @Override
    public void onStop() {
        super.onStop();
        Intent intent = new Intent(MainActivity.this, MyService.class);
        stopService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解除订阅消息
        EventBus.getDefault().unregister(this);
    }

    //用主线程方式接收处理消息，此时不论消息是在哪个线程中发布出来，它都会在UI线程中执行
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {   //tv.append("onMainActivity:"+event.getMsg()+"\n");
        Message message = Message.obtain();
        message.obj = "onMainActivity:" + event.getMsg();
        handler.sendMessage(message);

        Log.i(TAG, "onMainActivity:" + event.getMsg());
    }

    //线程
    public class MyThread extends Thread {

        @Override
        public void run() {
            //订阅消息
            EventBus.getDefault().register(this);
            //发布消息
            EventBus.getDefault().post(new MessageEvent("从Thread发出的消息"));

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //解除订阅消息
            EventBus.getDefault().unregister(this);
        }

        //用异步方式接收和处理消息，此时无论消息在哪个线程发布，都会创建新的子线程在执行
        @Subscribe(threadMode = ThreadMode.ASYNC)
        public void onMessageEvent(MessageEvent event) {
            Message message = Message.obtain();
            message.obj = "onThread:" + event.getMsg();
            handler.sendMessage(message);

            Log.i(TAG, "onThread:" + event.getMsg());
        }
    }
}
