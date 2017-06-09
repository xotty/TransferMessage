/**
 * Description:Handler的两种主要用途演示 1）子线程向主线程发送消息后更新UI   2）子线程之间消息传递
 * <br/>Copyright (C), 2017-2018, Steve Chang
 * <br/>This program is protected by copyright laws.
 * <br/>Program Name:Android Handler DEMO
 * <br/>Date:June，2017
 *
 * @author xottys@163.com
 * @version 1.0
 */

package org.xottys.transfermessage;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/* Handler传递消息共有下列7种方法，本例只演示了方法1、2和5，其它类似
* 1)sendEmptyMessage(int)
* 2)sendMessage(Message)
* 3)sendMessageAtTime(Message,long)
* 4)sendMessageDelayed(Message,long)
* 5)post(Runnable)
* 6)postAtTime(Runnable,long)
* 7)postDelayed(Runnable long)
*/

/*
* HanlerThread本身是一个带Looper的子线程，它可以和主线程或其它子线程进行消息传递，
* 使用方法是定义与其关联的Handler，然后在handleMessage中接收信息，此时该方法中可以进行耗时操作。
*/
public class HandlerDemo extends Activity {


    private Handler mainHandler, threadHandler1, threadHandler2;
    private HandlerThread handlerThread;
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
                //启动子线程，演示Handler消息传递
                if (bt.getText().equals("START")) {
                    bt.setText("Running......");
                    bt.setEnabled(false);
                    tv.setText(R.string.waitting1);

                    Thread t=new Thread1();
                    t.setName("Thread1");
                    t.start();
                }
                //在主线程中启动HandlerThread的handleMessage方法
                else if (bt.getText().equals("NEXT-1")) {

                    threadHandler2.sendEmptyMessage(1);    //模拟做第一件耗时操作

                    threadHandler2.sendEmptyMessage(2);    //模拟做第二件耗时操作
                }
                //在子线程中启动HandlerThread的handleMessage方法
                else if (bt.getText().equals("NEXT-2")) {

                    Thread t=new Thread3();
                    t.setName("Thread3");
                    t.start();


                }
                //演示结束，回到初始状态
                else {
                    handlerThread.quit();
                    bt.setText("START");
                    tv.setText(R.string.hello);
                }
            }
        });

        //主线程Handler
        mainHandler = new Handler()

        {
            @Override
            public void handleMessage(Message msg) {
                tv.setText(msg.obj.toString());
                Log.d("HandlerDemo", Thread.currentThread().getName()+"收到消息---" + msg.obj);
                switch (msg.what) {
                    case 2: {
                        bt.setText("NEXT-1");
                        bt.setEnabled(true);
                    }
                    break;
                    case 3: {
                        bt.setText("NEXT-2");
                        bt.setEnabled(true);
                    }
                    break;
                    case 4: {
                        bt.setText("END");
                        bt.setEnabled(true);
                    }

                }
            }
        };

        //定义子线程Handler（通过HandleThread定义）
        handlerThread = new HandlerThread("handlerThread");
        handlerThread.start();
        threadHandler2 = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {

                if (msg.what == 1) {
                    //模拟耗时操作一
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } else if (msg.what == 2) {
                    //模拟耗时操作二
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Message message = new Message();
                message.what = 3;
                message.obj = "threadHandler处理完毕，发送消息--" + msg.what;
                mainHandler.sendMessage(message);

                Log.d("HandlerDemo", Thread.currentThread().getName()+"收到消息--" + msg.what);

            }
        };

    }

    /*
    *1）使用Handler SendMessage向主线程发送消息后更新UI
    *2）使用Handler Post直接更新UI
    *3）启动新的子线程（Thread2），接收其用Handler返回的消息，进一步传给主线程以更新UI
     */
    class Thread1 extends Thread {

        @Override
        public void run() {
            //模拟耗时操作，然后用sendMessage方法向主线程发送消息   
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.d("HandlerDemo", e.getMessage());
            }
            Message message = new Message();
            message.obj = "Thread1处理完毕，发送消息......"+message.what;
            message.what = 1;
            mainHandler.sendMessage(message);

            //模拟耗时操作，然后用post方法直接更新UI  
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.d("HandlerDemo", e.getMessage());
            }
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    tv.setText(R.string.waitting2);
                    Log.d("HandlerDemo", "Thread1处理完毕，post直接处理UI");
                }
            });

            //在子线程（Thread1）中自定义Handler（ threadHandler1）
            Looper.prepare();
            threadHandler1 = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    Log.d("HandlerDemo", Thread.currentThread().getName() + "收到消息---" + msg.obj.toString());
                    Message message = new Message();
                    message.what = 2;
                    message.obj = msg.obj;

                    //将收到的消息发送给主线程以便更新UI
                    mainHandler.sendMessage(message);
                }
            };
            //启用新的子线程（Thread2）以便传回消息

            Thread t=new Thread2();
            t.setName("Thread2");
            t.start();
            Looper.loop();
        }

        ;

    }

    /*
    *子线程向子线程发送消息
     */
    class Thread2 extends Thread {
        @Override
        public void run() {
            //模拟耗时操作,然后向子线程（Thread1）发送消息
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.d("HandlerDemo", e.getMessage());
            }
            Log.d("HandlerDemo", Thread.currentThread().getName() + "发送消息......");
            Message message = new Message();
            message.what=0;
            message.obj = "Thread2处理完毕，发送消息......"+message.what;
            threadHandler1.sendMessage(message);
        }

        ;
    }

    /*
 *使用HandlerThread 向子线程发送消息
  */
    class Thread3 extends Thread {

        @Override
        public void run() {
            //模拟耗时操作,然后向子线程（handlerThread，类型为HandlerThread）发送消息
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.d("HandlerDemo", e.getMessage());
            }

            //通过HandleThread定义新的子线程handlerThread
            handlerThread = new HandlerThread("handlerThread");
            handlerThread.start();
            //定义子线程Handler（ threadHandler2）
            threadHandler2 = new Handler(handlerThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    Log.d("HandlerDemo", Thread.currentThread().getName() + "收到消息--" + msg.what);
                    Message message = new Message();
                    message.what = 4;
                    message.obj = "通过Thread3启动的handlerThread处理完毕，发送消息......"+msg.what;
                    mainHandler.sendMessage(message);
                }
            };

            //向子线程（handlerThread）发送消息
            threadHandler2.sendEmptyMessage(0);
        }

        ;
    }
}

