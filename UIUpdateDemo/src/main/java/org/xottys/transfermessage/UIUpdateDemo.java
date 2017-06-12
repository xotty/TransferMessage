/**
 * Description:Android UI更新的四种主要方法演示
 * <br/>Copyright (C), 2017-2018, Steve Chang
 * <br/>This program is protected by copyright laws.
 * <br/>Program Name:Android UI Update DEMO
 * <br/>Date:June，2017
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


public class UIUpdateDemo extends Activity {


    public Handler mainHandler;
    private Button bt;
    private TextView tv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
                    tv.setText(R.string.waitting);     //这个textView是用来演示更新的UI元素
                    new HandlerThread1().start();
                } else {
                    bt.setText("START");
                    tv.setText(R.string.hello);
                }
            }
        });

        mainHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                tv.setText(msg.obj.toString());
                Log.d("UIUpdateDemo", "SendMessage更新UI" + msg.obj.toString());
                new HandlerThread2().start();
            }
        };
    }

    //模拟耗时操作
    private void doSomthing() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*
     *更新UI方法一：Handler SendMessage
     */
    class HandlerThread1 extends Thread {


        @Override
        public void run() {
            doSomthing();
            Message message = new Message();
            message.obj = "Handler SendMessage Update UI->OK";
            message.what = 1;
            mainHandler.sendMessage(message);
            Log.d("UIUpdateDemo", "HandlerThread1处理完毕，发送消息......");
        }
    }

    /*
     *更新UI方法二：Handler Post
     */
    class HandlerThread2 extends Thread {


        @Override
        public void run() {
            doSomthing();
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    tv.setText("Handler POST Update UI->OK");
                    Log.d("UIUpdateDemo", "HandlerThread2处理完毕，post直接更新UI");

                    new UIThread().start();
                }
            });
        };

        /*
         *更新UI方法三：runOnUiThread
         */
        class UIThread extends Thread {

            @Override
            public void run() {
                doSomthing();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv.setText("runOnUiThread Update UI->OK");
                        Log.d("UIUpdateDemo", "UIThread处理完毕，更新UI");

                        new ViewPostThread().start();
                    }
                });
            };

            /*
             *更新UI方法四：View.Post
            */
            class ViewPostThread extends Thread {

                @Override
                public void run() {
                    doSomthing();
                    tv.post(new Runnable() {

                        @Override
                        public void run() {
                            tv.setText("ViewPost Update UI->OK");
                            bt.setText("END");
                            bt.setEnabled(true);
                            Log.d("UIUpdateDemo", "ViewPostThread处理完毕，更新UI");
                        }
                    });
                };
            }
        }
    }
}


