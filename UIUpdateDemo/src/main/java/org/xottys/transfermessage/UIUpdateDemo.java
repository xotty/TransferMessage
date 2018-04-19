/**
 * Description:Android UI更新的四种主要方法演示
 * 1）Handler SendMessage
 * 2）Handler Post
 * 3）runOnUiThread
 * 4）View.Post
 * <br/>Copyright (C), 2017-2018, Steve Chang
 * <br/>This program is protected by copyright laws.
 * <br/>Program Name:Android UI Update DEMO
 * <br/>Date:June，2017
 *
 * @author xottys@163.com
 * @version 1.0
 */
package org.xottys.transfermessage;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.ref.WeakReference;


public class UIUpdateDemo extends Activity {
    private Handler mainHandler;
    private Button bt;
    private TextView tv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt = findViewById(R.id.bt);

        //这个textView是用来演示更新的UI元素的
        tv = findViewById(R.id.tv);

        bt.setBackgroundColor(0xbd292f34);
        bt.setTextColor(0xFFFFFFFF);
        bt.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (bt.getText().equals("START")) {
                    bt.setText("Running......");
                    bt.setEnabled(false);
                    tv.setText(R.string.waitting);
                    new HandlerThread1().start();
                } else {
                    bt.setText("START");
                    tv.setText(R.string.hello);
                }
            }
        });

        mainHandler = new MyHandler(this, tv, new HandlerThread2());
        /*Handler非静态类写法会导致内存泄漏
        mainHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                tv.setText(msg.obj.toString());
                Log.i("UIUpdateDemo", "SendMessage更新UI" + msg.obj.toString());
                new HandlerThread2().start();
            }
        };*/
    }

    //模拟耗时操作
    private void doSomthing() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //可以避免内存泄漏的内部静态类Handler
    private static class MyHandler extends Handler {
        //持有弱引用MainActivity,GC回收时会被回收掉.
        private final WeakReference<Activity> mActivty;
        private TextView tv;
        private Thread thread;

        //需要更新或使用的非静态对象用构造器参数传进来
        public MyHandler(Activity activity, TextView textView, Thread thread) {
            mActivty = new WeakReference<>(activity);
            tv = textView;
            this.thread = thread;
        }

        @Override
        public void handleMessage(Message msg) {
            Activity activity = mActivty.get();
            super.handleMessage(msg);
            if (activity != null) {

                tv.setText(msg.obj.toString());
                Log.i("UIUpdateDemo", "SendMessage更新UI" + msg.obj.toString());
                thread.start();

            }
        }
    }

    //更新UI方法一：Handler SendMessage
    private class HandlerThread1 extends Thread {
        @Override
        public void run() {
            doSomthing();
            Message message = new Message();
            message.obj = "1)Handler SendMessage Update UI->OK\n\n";
            message.what = 1;
            mainHandler.sendMessage(message);
            Log.i("UIUpdateDemo", "HandlerThread1处理完毕，发送消息......");
        }
    }

    //更新UI方法二：Handler Post
    class HandlerThread2 extends Thread {
        @Override
        public void run() {
            doSomthing();
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    tv.append("2)Handler POST Update UI->OK" + "\n\n");
                    Log.i("UIUpdateDemo", "HandlerThread2处理完毕，post直接更新UI");

                    new UIThread().start();
                }
            });
        }

        //更新UI方法三：runOnUiThread
        class UIThread extends Thread {
            @Override
            public void run() {
                doSomthing();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv.append("3)runOnUiThread Update UI->OK" + "\n\n");
                        Log.i("UIUpdateDemo", "UIThread处理完毕，更新UI");

                        new ViewPostThread().start();
                    }
                });
            }

            //更新UI方法四：View.Post
            class ViewPostThread extends Thread {
                @Override
                public void run() {
                    doSomthing();
                    tv.post(new Runnable() {

                        @Override
                        public void run() {
                            tv.append("4)ViewPost Update UI->OK");
                            bt.setText("END");
                            bt.setEnabled(true);
                            Log.i("UIUpdateDemo", "ViewPostThread处理完毕，更新UI");
                        }
                    });
                }
            }
        }
    }
}


