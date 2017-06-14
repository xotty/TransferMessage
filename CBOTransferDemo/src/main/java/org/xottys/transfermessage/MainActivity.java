/**
 * Description:
 * Callback的两种主要用法演示 1）与Callback设置同步定义回调方法   2）单独定义回调方法
 * BroadcastReceiver的两种主要用法演示  1）内部类接收消息   2）自定义外部类接收消息
 * <p>
 * <br/>Copyright (C), 2017-2018, Steve Chang
 * <br/>This program is protected by copyright laws.
 * <br/>Program Name:Android CBOTransferMessage DEMO
 * <br/>Date:June，2017
 *
 * @author xottys@163.com
 * @version 1.0
 */
package org.xottys.transfermessage;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements CallbackInterface {
    private Button bt;
    private TextView tv;
    private CallbackClass mCallback;
    private MyBroadcastReceiver1 mRreceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt = (Button) findViewById(R.id.bt);
        tv = (TextView) findViewById(R.id.tv);
        bt.setBackgroundColor(0xbd292f34);
        bt.setTextColor(0xFFFFFFFF);


        bt.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (bt.getText().equals("START")) {
                    bt.setText("Running......");
                    bt.setEnabled(false);
                    tv.setText(R.string.waitting);

                    mCallback = new CallbackClass();
                    mCallback.setCallbackInterface(new CallbackInterface() {
                        //方式一：具体实现回调方法
                        @Override
                        public void callbackMethod(String str) {
                            System.out.println("方式一，与Callback设置同步定义回调方法，收到的数据为-->" + str);
                            tv.setText("方式一，同步定义回调方法，收到数据:\n" + str);
                            //下面是具体处理程序......
                        }
                    });
                    //方式一：启动回调方法调用
                    mCallback.doSomthing();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //方式二：启动回调方法调用
                            mCallback.doSomthing(MainActivity.this);
                        }
                    }, 3000);

                }
                //在主线程中启动HandlerThread的handleMessage方法
                else if (bt.getText().equals("NEXT-1")) {

                    Intent intent = new Intent();
                    intent.setAction("MyReceiver_1");   //设置接受者匹配的Action
                    intent.putExtra("FROM", "Inside");  //封装要发送的消息1
                    intent.putExtra("MSG", "方法一：通过动态注册广播和内部广播接收类收发消息"); //封装要发送的消息2
                    sendBroadcast(intent);              //发送广播，在Activity、Service中可直接使用，否则要加Context

                }
                //在子线程中启动HandlerThread的handleMessage方法
                else if (bt.getText().equals("NEXT-2")) {

                    unregisterReceiver(mRreceiver);           //不用时及时解除广播注册
//                    Thread t = new Thread3();
//                    t.setName("Thread3");
//                    t.start();


                }
                //演示结束，回到初始状态
                else {

                    bt.setText("START");
                    tv.setText(R.string.hello);
                }


            }
        });
        //动态注册广播接收者（代码实现）
        mRreceiver = new MyBroadcastReceiver1();

        //一个广播接收者可以设置多个匹配的Action，即可接收多个发送者的消息
        IntentFilter filter = new IntentFilter();
        filter.addAction("MyReceiver_1");
        filter.addAction("MyReceiver_2");
        filter.addAction("android.intent.action.BATTERY_CHANGED_ACTION");  //系统消息

        registerReceiver(mRreceiver, filter);

    }

    //方式二：具体实现回调方法
    @Override
    public void callbackMethod(String str) {
        System.out.println("方式二，回调方法开始处理，收到的数据为-->" + str);
        tv.setText("方式二，单独定义回调方法，收到数据:\n" + str);
        bt.setText("NEXT-1");
        bt.setEnabled(true);
        //下面是具体处理程序......
    }

    //延时3s
    private void waitAmoment() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //内部类方式定义广播接收者，此时若用静态注册时（XML中注册），该内部类必须是static的
    class MyBroadcastReceiver1 extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String from = intent.getStringExtra("FROM");    //解析收到的消息1
            String msg = intent.getStringExtra("MSG");      //解析收到的消息2
            tv.setText(from + "：" + msg);
            Log.d("CBOTransferDemo", "MyBroadcastReceiver1收到-->" + from + "：" + msg);

            if (from.equals("Inside")) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //调用外部类再次发送广播
                        new MyBroadcastSend(getApplicationContext()).mSendBroadcast("方法二：调用外部类发送广播");
                    }
                }, 3000);
            } else {
                bt.setText("END");
                bt.setEnabled(true);
            }

        }
    }

}
