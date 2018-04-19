/**Callback的两种主要用法演示 1）与Callback设置同步定义回调方法   2）单独定义回调方法
 * BroadcastReceiver的两种主要用法演示  1）内部类接收消息   2）自定义外部类接收消息
 * Observer用法演示
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
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

public class MainActivity extends Activity implements CallbackInterface {
    private Button bt;
    private TextView tv;
    private CallbackClass mCallback;
    private MyBroadcastReceiver1 mRreceiver;
    private MyObservable myObservable;
    private MyObserver myObserver;
    private MyContentObserver myContentObserver;

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

                    bt.setText("Running......回调");
                    bt.setEnabled(false);
                    tv.setText("           一、回调（CallBack）演示\n\n");

                    //Callback演示
                    mCallback = new CallbackClass();
                    mCallback.setCallbackInterface(new CallbackInterface() {
                        //方式一：具体实现回调方法
                        @Override
                        public void callbackMethod(String str) {

                            System.out.println("方式一，与Callback设置同步定义回调方法，收到的数据为-->" + str);
                            tv.append("方式一，同步定义回调方法，收到数据:\n" + str + "\n");
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
                //BroadcastReceiver演示
                else if (bt.getText().equals("NEXT-1")) {
                    bt.setText("Running......广播");
                    bt.setEnabled(false);

                    tv.append("    二、广播（BroadcastReceiver）演示\n\n");
                    Intent intent = new Intent();
                    intent.setAction("MyReceiver_1");   //设置接受者匹配的Action
                    intent.putExtra("FROM", "Inside");  //封装要发送的消息1
                    intent.putExtra("MSG", "方法一：通过动态注册广播和内部广播接收类收发消息"); //封装要发送的消息2
                    sendBroadcast(intent);              //发送广播，在Activity、Service中可直接使用，否则要加Context

                }
                //Observer演示
                else if (bt.getText().equals("NEXT-2")) {
                    bt.setText("Running......观察者");
                    bt.setEnabled(false);

                    tv.append("         三、观察者（Observer）演示\n\n");
                    myObservable.setData("第一次改变");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            myObservable.setData("第二次改变");
                        }
                    }, 3000);

                    //使用ContentObserver监听短信发出
                   myContentObserver=new MyContentObserver(new Handler());
                    getContentResolver().registerContentObserver(
                            Uri.parse("content://sms"), true,
                            myContentObserver);
                }

                //演示结束，回到初始状态
                else {
                    bt.setText("START");

                    tv.setText(R.string.hello);
                    getContentResolver().unregisterContentObserver(myContentObserver);
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


        //观察者模式启动准备
        myObservable = new MyObservable();
        myObserver = new MyObserver();
        myObservable.addObserver(myObserver);     //添加观察者

    }

    @Override
    protected void onDestroy()

    {
        super.onDestroy();

        unregisterReceiver(mRreceiver);           //及时解除广播注册
        myObservable.deleteObserver(myObserver);  //及时解除观察者注册

    }

    //方式二：具体实现回调方法
    @Override
    public void callbackMethod(String str) {
        System.out.println("方式二，回调方法开始处理，收到的数据为-->" + str);
        tv.append("方式二，单独定义回调方法，收到数据:\n" + str + "\n");
        tv.append(getResources().getString(R.string.mline) + "\n");
        bt.setText("NEXT-1");
        bt.setEnabled(true);
        //下面是具体处理程序......
    }


    //内部类方式定义广播接收者，此处演示的是动态注册；
    //若改为静态注册（XML中注册），该类必须是static的
    class MyBroadcastReceiver1 extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String from = intent.getStringExtra("FROM");    //解析收到的消息1
            String msg = intent.getStringExtra("MSG");      //解析收到的消息2
            tv.append(from + "：" + msg + "\n");
            Log.i("CBOTransferDemo", "MyBroadcastReceiver1收到-->" + from + "：" + msg);

            //如果收到的是内部类发出的广播，则继续调用外部广播类进行发送广播演示
            if (from.equals("Inside")) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //调用外部类再次发送广播
                        new MyBroadcastSend(getApplicationContext()).mSendBroadcast("方法二：调用外部类发送广播");
                    }
                }, 3000);
            }
            //如果收到的是外部类发出的广播，结束则广播演示，修改UI为其它演示操作做好准备
            else {
                tv.append(getResources().getString(R.string.mline) + "\n");
                bt.setText("NEXT-2");
                bt.setEnabled(true);
            }

        }
    }

    //在被观察的数据发生变化时update()方法会被自动调用
    private class MyObserver implements Observer {
        @Override
        public void update(Observable observable, Object obj) {
            String msg = (String) obj;
            Log.i("CBOTransferDemo", "MyObserver发现变化-->" + msg);
            tv.append("MyObserver发现变化-->" + msg + "\n");
            if (msg.equals("第二次改变")) {
                bt.setText("END");
                bt.setEnabled(true);
            }
        }
    }

    // 提供自定义的ContentObserver监听器类
    private final class MyContentObserver extends ContentObserver {
      //private Handler mHandler;
        public MyContentObserver(Handler handler) {
            super(handler);
        //  mHandler=handler;
        }

        @Override
        public void onChange(boolean selfChange) {

            Log.i("CBOTransferDemo", "MyContenObserver发现变化!");
            tv.append("MyContenObserver发现变化!\n");

            //变化信息也可以通过这里的mHandler传到主线程中去处理,例如：
            //mHandler.obtainMessage(1，sb.toString()).sendToTarget();
        }
    }
}