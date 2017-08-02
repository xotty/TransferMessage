/**
 * Description:Intent的两种主要用途演示 1）Activity之间传递消息   2）Activity与Service之间传递消息
 * Intent还可以在广播中（BroadcastReceiver）传递消息，详见CBOTransferDemo中相关内容
 * <br/>Copyright (C), 2017-2018, Steve Chang
 * <br/>This program is protected by copyright laws.
 * <br/>Program Name:Intent DEMO
 * <br/>Date:June，2017
 *
 * @author xottys@163.com
 * @version 1.0
 */
package org.xottys.transfermessage;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
    final private String TAG="IntentDemo";
        MyBindService.MyBinder myBinder;
    private Button bt1, bt2, bt3;
    private TextView tv;
    private ServiceConnection conn = new ServiceConnection() {
        //每个宿主与service第一次Bind成功时会回调该方法，同一宿主第二次及以后bind同一Sercice时不会再回调本方法，除非unBindService后再次Bind
        @Override
        public void onServiceConnected(ComponentName name
                , IBinder service) {
            // 获取Service的onBind方法所返回的MyBinder对象，该对象用来和Service通信
            myBinder = (MyBindService.MyBinder) service;

            tv.setText("MyBindService已绑定成功!\n\n初始值1-->City:" + myBinder.getCity() + "  GDP:" + myBinder.getGDP() + "\n\n");
            Log.i(TAG, "--Service Connected--)" + "MyBindService初始值-->City:" + myBinder.getCity() + "  GDP:" + myBinder.getGDP());

            //向Service 传递数据的又一种方法，可以每次bindService 重复使用
            myBinder.setCity("杭州");
            myBinder.setGDP(1.17f);
            tv.append("初始值2-->City:" + myBinder.getCity() + "  GDP:" + myBinder.getGDP() + "\n\n");

            //执行Service的主要任务（模拟）
            myBinder.doSomething();

            tv.append("任务完成后返回值-->City:" + myBinder.getCity() + "  GDP:" + myBinder.getGDP());
            Log.i(TAG, "MyBindService模拟任务完成后返回值-->City:" + myBinder.getCity() + "  GDP:" + myBinder.getGDP());

        }

        // 当该Activity与Service断开连接时回调该方法
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "--Service Disconnected--");
        }
    };

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

        //Activity启动和消息传递
        bt1.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "MainActivity准备启动MyActivity1");

                Intent intent = new Intent(MainActivity.this, MyActivity1.class);   //显式启动Activity
                //第一种数据设置方法：直接用Intent设置数据
                intent.putExtra("name", "张三");
                intent.putExtra("score", 95.5f);

                //启动MyActivity1，不要求返回结果
                startActivity(intent);

            }
        });

        //Service启动和消息传递
        bt2.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyStartService.class);

                if (bt2.getText().equals("Start Service")) {
                    Log.i(TAG, "MainActivity准备启动MyStartService");
                    bt2.setText("Stop Service");
                    intent.putExtra("city", "上海");
                    intent.putExtra("GDP", 2.67f);
                    //启动Service，该方法不能获取Service返回数据
                    startService(intent);
                    tv.setText("MyService启动,详情请查看终端Log输出。");
                } else {
                    bt2.setText("Start Service");
                    //停止Service
                    stopService(intent);
                    tv.setText("MyService终止！");
                }
            }
        });

        //Service绑定和消息传递演
        bt3.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                final Intent intent = new Intent(MainActivity.this, MyBindService.class);

                if (bt3.getText().equals("Bind\n Service")) {
                    Log.i(TAG, "MainActivity准备绑定MyBindService");
                    bt3.setText("Unbind\n Service");
                    tv.setText("");

                    //向Service 传递数据的一种方法，只能第一次bindService时使用
                    intent.putExtra("city", "广州");
                    intent.putExtra("GDP", 2.00f);
                    // 绑定指定Serivce
                    bindService(intent, conn, Service.BIND_AUTO_CREATE);


                } else {
                    bt3.setText("Bind\n Service");
                    tv.setText("MyBindService准备解除绑定!\nMyBindService返回值-->" + myBinder.getCount());
                    // 解除绑定Serivce
                    unbindService(conn);
                }
            }
        });
    }
}
