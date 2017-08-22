/**
 * 跨进程消息传递（IPC）演示，1）Messenger   2）AIDL   3）Activity跳转
 *                        4）BroadcastReceiver    5）ContentProvider
 * 上述通信方式在本地进程中也都是可用的。
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
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
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

import static android.os.Process.myPid;


public class MainActivity extends Activity  {

    private static final int SEND_MESSAGE_CODE = 0x0001;
    private static final int RECEIVE_MESSAGE_CODE = 0x0002;
    private static final String TAG = "IPCDemo";

    static Activity instance;
    private static Button bt1, bt2, bt3, bt4;
    private static TextView tv;

    private ContentResolver contentResolver;
    private Uri uri = Uri.parse("content://org.xottys.IPC.MyProvider/");


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

                    tv.append("启动了MyAidlService的getPid：" + pid + "\n");
                    Log.i(TAG, "MyAidlService的getPid的返回结果: " + pid);
                    Log.i(TAG, "当前进程的PID是：" + myPid());

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

            Log.i(TAG, "客户端 onServiceDisconnected");
        }
    };

    static Activity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_main);
        bt1 = (Button) findViewById(R.id.bt1);
        bt2 = (Button) findViewById(R.id.bt2);
        bt3 = (Button) findViewById(R.id.bt3);
        bt4 = (Button) findViewById(R.id.bt4);

        bt1.setBackgroundColor(0xbd292f34);
        bt1.setTextColor(0xFFFFFFFF);
        bt2.setBackgroundColor(0xbd292f34);
        bt2.setTextColor(0xFFFFFFFF);
        bt3.setBackgroundColor(0xbd292f34);
        bt3.setTextColor(0xFFFFFFFF);
        bt4.setBackgroundColor(0xbd292f34);
        bt4.setTextColor(0xFFFFFFFF);
        tv = (TextView) findViewById(R.id.tv);
        instance = this;

        // 获取系统的ContentResolver对象
        contentResolver = getContentResolver();

        //MyMessengerService启动和消息传递
        bt1.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (bt1.getText().equals("Start\n Messenger")) {
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
                    bt1.setText("Stop\n Messenger");
                    bt2.setTextColor(0xFFA0A0A0);
                    bt3.setTextColor(0xFFA0A0A0);
                    bt4.setTextColor(0xFFA0A0A0);
                    bt3.setBackgroundColor(0xbd292f34);
                    bt4.setBackgroundColor(0xbd292f34);
                    bt2.setEnabled(false);
                    bt3.setEnabled(false);
                    bt4.setEnabled(false);
                } else {

                    //解除Service绑定
                    unbindService(conn);
                    tv.setText("MyMessengerService解除绑定");

                    bt1.setText("Start\n Messenger");
                    bt2.setTextColor(0xFFFFFFFF);
                    bt3.setTextColor(0xFFFFFFFF);
                    bt4.setTextColor(0xFFFFFFFF);
                    bt2.setEnabled(true);
                    bt3.setEnabled(true);
                    bt4.setEnabled(true);
                }
            }
        });

        //MyAidlService启动和消息传递
        bt2.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                if (bt2.getText().equals("Start\n AIDL")) {

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
                    bt2.setText("Stop\n AIDL");
                    bt1.setTextColor(0xFFA0A0A0);
                    bt3.setTextColor(0xFFA0A0A0);
                    bt4.setTextColor(0xFFA0A0A0);
                    bt3.setBackgroundColor(0xbd292f34);
                    bt4.setBackgroundColor(0xbd292f34);
                    bt1.setEnabled(false);
                    bt3.setEnabled(false);
                    bt4.setEnabled(false);
                } else {
                    //解除Service绑定
                    unbindService(conn);
                    tv.setText("MyAidlService解除绑定");

                    bt2.setText("Start\n AIDL");
                    bt1.setTextColor(0xFFFFFFFF);
                    bt3.setTextColor(0xFFFFFFFF);
                    bt4.setTextColor(0xFFFFFFFF);
                    bt1.setEnabled(true);
                    bt3.setEnabled(true);
                    bt4.setEnabled(true);
                }
            }
        });

        //跳转到另一个进程的Activity且可以获取返回值
        bt3.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent();
                ComponentName componentName = new ComponentName("org.xottys.IPC", "org.xottys.IPC.MainActivity");
                intent.setComponent(componentName);
                //另外一种Intent设置方法
                //Intent intent =  new Intent("MYACTION", Uri.parse("info://111"));

                startActivityForResult(intent, 0);

                bt3.setBackgroundColor(0xFFD7D7D7);
                bt3.setTextColor(0xbd292f34);
                bt4.setBackgroundColor(0xbd292f34);
                bt4.setTextColor(0xFFFFFFFF);
            }
        });

        //操作ContentProvider数据
        bt4.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                query(v);
                insert(v);
                update(v);
                delete(v);

                bt4.setBackgroundColor(0xFFD7D7D7);
                bt4.setTextColor(0xbd292f34);
                bt3.setBackgroundColor(0xbd292f34);
                bt3.setTextColor(0xFFFFFFFF);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ---");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ----");
    }

    //ContentResolver的CRUD操作方法
    public void query(View source) {
        // 调用ContentResolver的query()方法。
        // 实际返回的是该Uri对应的ContentProvider的query()的返回值
        Cursor c = contentResolver.query(uri, null
                , "query_where", null, null);
        tv.setText("远程ContentProvide返回的Cursor为：" + c + "\n");
    }

    public void insert(View source) {
        ContentValues values = new ContentValues();
        values.put("name", "fkjava");
        // 调用ContentResolver的insert()方法。
        // 实际返回的是该Uri对应的ContentProvider的insert()的返回值
        Uri newUri = contentResolver.insert(uri, values);
        tv.append("远程ContentProvide插入记录的Uri为：" + newUri + "\n");

    }

    public void update(View source) {
        ContentValues values = new ContentValues();
        values.put("name", "fkjava");
        // 调用ContentResolver的update()方法。
        // 实际返回的是该Uri对应的ContentProvider的update()的返回值
        int count = contentResolver.update(uri, values
                , "update_where", null);
        tv.append("远程ContentProvide更新记录数为：" + count + "\n");
    }

    public void delete(View source) {
        // 调用ContentResolver的delete()方法。
        // 实际返回的是该Uri对应的ContentProvider的delete()的返回值
        int count = contentResolver.delete(uri
                , "delete_where", null);
        tv.append("远程ContentProvide删除记录数为：" + count + "\n");
    }


    @Override
    //接收和处理远程Activty的返回结果
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            String name = data.getStringExtra("name");
            float score = data.getFloatExtra("score", -1f);
            tv.setText("IPC Service---->IPC Client" + "\n\n" + "返回数据-->" + name + ":" + score);
            Log.i(TAG, "IPC Client收到IPC Service返回的结果：" + name + ":" + score);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    //接收和处理远程广播内容
    public static class IPCReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context content, final Intent intent) {

            String msg = intent.getStringExtra("msg");
            Log.i(TAG, "收到远程广播内容：" + msg + tv.getText());
            tv.setText("收到远程广播内容：" + msg);
        }

    }

    //Messenger客户端用ClientHandler接收并处理来自于Server的消息
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
