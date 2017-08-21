/**
 * Handler的两种主要用途演示 1）子线程向主线程发送消息后更新UI   2）子线程之间消息传递
 * <br/>Copyright (C), 2017-2018, Steve Chang
 * <br/>This program is protected by copyright laws.
 * <br/>Program Name:Android Handler DEMO
 * <br/>Date:June，2017
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


    private static Handler mainHandler;
    private static Handler threadHandler1;
    private static Handler threadHandler2;
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
                    tv.setText(R.string.waitting);

                    Thread t = new Thread1();
                    t.setName("Thread1");
                    t.start();
                }

                //在主线程中启动HandlerThread的handleMessage方法
                else if (bt.getText().equals("NEXT-1")) {
                    bt.setText("Running......");
                    bt.setEnabled(false);
                    threadHandler2.sendEmptyMessage(1);    //模拟第一件耗时操作

                    threadHandler2.sendEmptyMessage(2);    //模拟第二件耗时操作
                }

                //在子线程中启动HandlerThread的handleMessage方法
                else if (bt.getText().equals("NEXT-2")) {
                    bt.setText("Running......");
                    bt.setEnabled(false);
                    Thread t = new Thread3();
                    t.setName("Thread3");
                    t.start();


                }
                //演示结束，回到初始状态
                else {

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
                if (msg.what == 1)
                    tv.setText(msg.obj.toString());
                else
                    tv.append(msg.obj.toString());

                Log.i("HandlerDemo", Thread.currentThread().getName() + "收到消息---" + msg.obj);
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
                        handlerThread.quit();
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
                Message message = new Message();

                if (msg.what == 1) {
                    //模拟耗时操作一
                    doSomthing();
                    message.what = 0;

                } else if (msg.what == 2) {
                    //模拟耗时操作二
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    message.what = 3;
                }

                message.obj = "3." + msg.what + ")通过主线程启动的handlerThread处理完毕，发送消息--" + msg.what + "\n";
                mainHandler.sendMessage(message);

                Log.i("HandlerDemo", Thread.currentThread().getName() + "收到消息--" + msg.what);

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
    *1）使用Handler SendMessage向主线程发送消息后更新UI
    *2）使用Handler Post直接更新UI
    *3）启动新的子线程（Thread2），接收其用Handler返回的消息，进一步传给主线程以更新UI
     */
    private class Thread1 extends Thread {

        @Override
        public void run() {
            //模拟耗时操作，然后用sendMessage方法向主线程发送消息   
            doSomthing();
            Message message = new Message();
            message.obj = "1.1)Thread1处理完毕，发送消息......" + message.what + "\n";
            message.what = 1;
            mainHandler.sendMessage(message);

            //模拟耗时操作，然后用post方法直接更新UI  
            doSomthing();
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    tv.append("1.2)Thread1处理完毕，post直接处理UI" + "\n\n");
                    Log.i("HandlerDemo", "Thread1处理完毕，post直接处理UI");
                }
            });

            //在子线程（Thread1）中自定义Handler（ threadHandler1）
            Looper.prepare();
            threadHandler1 = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    Log.i("HandlerDemo", Thread.currentThread().getName() + "收到消息---" + msg.obj.toString());
                    Message message = new Message();
                    message.what = 2;
                    message.obj = msg.obj + "\n";

                    //将收到的消息发送给主线程以便更新UI
                    mainHandler.sendMessage(message);
                }
            };
            //启用新的子线程（Thread2）以便传回消息

            Thread t = new Thread2();
            t.setName("Thread2");
            t.start();
            Looper.loop();
        }


    }

    /*
    *子线程向子线程发送消息
     */
    private class Thread2 extends Thread {
        @Override
        public void run() {
            //模拟耗时操作,然后向子线程（Thread1）发送消息
            doSomthing();
            Log.i("HandlerDemo", Thread.currentThread().getName() + "发送消息......");
            Message message = new Message();
            message.what = 0;
            message.obj = "2)Thread2处理完毕，发送消息......" + message.what + "\n";
            threadHandler1.sendMessage(message);
        }
    }

    /*
 *使用HandlerThread 向子线程发送消息
  */
    private class Thread3 extends Thread {

        @Override
        public void run() {
            //模拟耗时操作,然后向子线程（handlerThread，类型为HandlerThread）发送消息
            doSomthing();

            //通过HandleThread定义新的子线程handlerThread
            handlerThread = new HandlerThread("handlerThread");
            handlerThread.start();
            //定义子线程Handler（ threadHandler3）
            Handler threadHandler3 = new Handler(handlerThread.getLooper()) {
                //这里是handlerThread真正完成的工作
                @Override
                public void handleMessage(Message msg) {
                    Log.i("HandlerDemo", Thread.currentThread().getName() + "收到消息--" + msg.what);
                    Message message = new Message();
                    message.what = 4;
                    message.obj = "\n4)通过Thread3启动的handlerThread处理完毕，收到消息......" + msg.what + "\n";
                    doSomthing();
                    mainHandler.sendMessage(message);
                }
            };

            //启动handlerThread
            threadHandler3.sendEmptyMessage(0);
        }

    }
}

