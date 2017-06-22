/**
 * Description: 跨进程消息传递（IPC）演示，1）Messenger   2）AIDL   3）ContentProvider
 * <br/>Copyright (C), 2017-2018, Steve Chang
 * <br/>This program is protected by copyright laws.
 * <br/>Program Name:IPCDemo MainActivity
 * <br/>Date:June，2017
 *
 * @author xottys@163.com
 * @version 1.0
 */
package org.xottys.transfermessage;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.xottys.IPC.MyAidlInterface;

public class MainActivity extends Activity {
    private static final int SEND_MESSAGE_CODE = 0x0001;
    private static final int RECEIVE_MESSAGE_CODE = 0x0002;
    final private String TAG = "IPCDemo";
    private Button bt1, bt2, bt3;
    private TextView tv;
    private boolean isBound = false;

    //serverMessenger内部指向了MyMessengerService的ServerHandler实例,可以向Server发送消息
    private Messenger serverMessenger = null;

    //clientMessenger是客户端自身的Messenger，内部指向了ClientHandler的实例
    //MyMessengeService可以通过Message的replyTo得到clientMessenger，从而Server端可以向客户端发送消息，
    //并由ClientHandler接收并处理来自于Server的消息
    private Messenger clientMessenger = new Messenger(new ClientHandler());

    //MyAidlInterface的实例，用来调用其中的方法实现
    private MyAidlInterface remoteService;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            //客户端与Service建立连接
            Log.i(TAG, "客户端onServiceConnected");
            //Messenger方式演示
            if (name.getClassName().contains("MyMessengerService")) {
                tv.setText("MyMessengerService已启动......\n\n");
                //通过从MyMessengerService的onBind方法中返回的IBinder初始化了一个指向Server端的Messenger
                serverMessenger = new Messenger(binder);

                isBound = true;

                Message msg = Message.obtain();
                msg.what = SEND_MESSAGE_CODE;

                //此处跨进程Message通信不能将msg.obj设置为non-Parcelable的对象，应该使用Bundle
                //msg.obj = "你好，MyService，我是客户端"，这样是不行的;
                Bundle data = new Bundle();
                data.putString("msg", "你好！我是客户端。");
                msg.setData(data);

                //需要将Message的replyTo设置为客户端的clientMessenger，以便Server可以通过它向客户端发送消息
                msg.replyTo = clientMessenger;

                try {
                    //向Server端发送消息
                    serverMessenger.send(msg);

                    Log.i(TAG, "客户端向MyMessengerService发送信息-->" + data.get("msg"));
                    tv.append("Client-->Server:" + data.getString("msg") + "\n");

                } catch (RemoteException e) {
                    e.printStackTrace();
                    Log.i(TAG, "客户端向MyMessengerService发送消息失败: " + e.getMessage());
                }
            }
            //AIDL方式演示
            if (name.getClassName().contains("MyAidlService")) {
                tv.setText("MyAidlService已启动......\n\n");
                //通过从MyAidlService的onBind方法中返回的IBinder初始化了一个指向Server端的MyAidlInterface实例
                remoteService = MyAidlInterface.Stub.asInterface(binder);
                try {
                    //调用Server端的方法
                    int pid = remoteService.getPid();

                    tv.append("启动了MyAidlService的getPid，返回结果：" + pid + "\n");
                    Log.d(TAG, "MyAidlService的getPid的返回结果: " + pid);

                    //调用Server端的方法
                    remoteService.basicTypes(12, true, 12.3, "This is MyAidlServiceDemo ");

                    tv.append("启动了MyAidlService的basicTypes");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //客户端与Service失去连接
            serverMessenger = null;
            isBound = false;
            Log.i(TAG, "客户端 onServiceDisconnected");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        //MyMessengerService启动和消息传递
        bt1.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                if (bt1.getText().equals("Start\n Messenger")) {

                    bt1.setText("Stop\n Messenger");
                    bt2.setEnabled(false);
                    Log.i(TAG, "MainActivity准备启动MyMessengerService");
                    Intent intent = new Intent();
                    intent.setAction("action.Messenger_SERVICE");
                    //隐式的Intent进行转化，从而可以用来启动的Service
                    PackageManager pm = getPackageManager();
                    ResolveInfo info = pm.resolveService(intent, 0);
                    if (info != null) {
                        String packageName = info.serviceInfo.packageName;
                        String serviceNmae = info.serviceInfo.name;
                        ComponentName componentName = new ComponentName(packageName, serviceNmae);
                        intent.setComponent(componentName);
                        //启动远程 Service
                        bindService(intent, conn, BIND_AUTO_CREATE);
                    }
                } else {
                    bt1.setText("Start\n Messenger");
                    bt2.setEnabled(true);
                    //解除Service绑定
                    unbindService(conn);
                    tv.setText("MyMessengerService解除绑定");
                }

            }
        });

        //MyAidlService启动和消息传递
        bt2.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                if (bt2.getText().equals("Start\n AIDL")) {
                    bt2.setText("Stop\n AIDL");
                    bt1.setEnabled(false);
                    tv.setText("MyAidlService已启动,详情请查看终端Log输出。\n");
                    Log.i(TAG, "MainActivity准备启动MyAidlService");
                    Intent intent = new Intent();
                    intent.setAction("action.Aidl_SERVICE");
                    //隐式的Intent进行转化，从而可以用来启动的Service
                    PackageManager pm = getPackageManager();
                    ResolveInfo info = pm.resolveService(intent, 0);
                    if (info != null) {
                        String packageName = info.serviceInfo.packageName;
                        String serviceNmae = info.serviceInfo.name;
                        ComponentName componentName = new ComponentName(packageName, serviceNmae);
                        intent.setComponent(componentName);
                        //启动远程 Service
                        bindService(intent, conn, BIND_AUTO_CREATE);
                    }

                } else {
                    bt2.setText("Start\n AIDL");
                    bt1.setEnabled(true);
                    //解除Service绑定
                    unbindService(conn);
                    tv.setText("MyAidlService解除绑定");
                }
            }
        });

        //Service绑定和消息传递演
        bt3.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

//                final Intent intent = new Intent(MainActivity.this, MyBindService.class);
//
//                if (bt3.getText().equals("Bind\n Service")) {
//                    Log.i(TAG, "MainActivity准备绑定MyBindService");
//                    bt3.setText("Unbind\n Service");
//
//                    //向Service 传递数据的一种方法，只能第一次bindService时使用
//                    intent.putExtra("city", "广州");
//                    intent.putExtra("GDP", 2.00f);
//                    // 绑定指定Serivce
//                    bindService(intent, conn, Service.BIND_AUTO_CREATE);
//                } else {
//                    bt3.setText("Bind\n Service");
//                    tv.setText("MyBindService准备解除绑定!\nMyBindService返回值-->" + myBinder.getCount());
//                    // 解除绑定Serivce
//                    unbindService(conn);
//                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }

    //客户端用ClientHandler接收并处理来自于Server的消息
    private class ClientHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "ClientHandler -> handleMessage");
            if (msg.what == RECEIVE_MESSAGE_CODE) {
                Bundle data = msg.getData();
                if (data != null) {
                    String str1 = data.getString("server");
                    String str2 = data.getString("client");
                    tv.append("Server-->Client:" + str1 + "\n");
                    tv.append("Server刚才收到Client发来的消息:" + str2 + "\n");
                    Log.i(TAG, "客户端收到的消息MyMessengerService: " + str1 + str2);
                }
            }
        }
    }
}
