/**
 * Intent的两种主要用途演示
 * 1）Activity之间传递消息   2）Activity与Service之间传递消息
 * Intent还可以在广播中（BroadcastReceiver）传递消息，详见CBOTransferDemo中相关内容
 * <p>
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
    final private String TAG = "IntentDemo";
    private MyBindService myBindService;
    private TextView tv;
    boolean isServiceStop;
    private ServiceConnection conn = new ServiceConnection() {
        //每个宿主与service第一次Bind成功时会回调该方法，同一宿主第二次及以后bind同一Sercice时不会再回调本方法，除非unBindService后再次Bind
        @Override
        public void onServiceConnected(ComponentName name
                , IBinder service) {
            isServiceStop=false;
            Log.i(TAG, "*******MainActivity onServiceConnected, Thread：" + Thread.currentThread().getName());
            //获取绑定Service传递过来的IBinder对象，通过这个IBinder对象，实现宿主和Service的交互。
            MyBindService.MyBinder myBinder = (MyBindService.MyBinder) service;

            //通过传递过来的IBinder对象中的方法来获取Service的对象以便调用其公共方法
            myBindService = myBinder.getMyBindService();

            tv.setText("MyBindService已绑定成功!\n\n初始值-->City:" + myBindService.getCity() + "  GDP:" + myBindService.getGDP() + "\n\n");
            Log.i(TAG, "MainActivity获取MyBindService初始值-->City:" + myBindService.getCity() + "  GDP:" + myBindService.getGDP());

            myBindService.calller="MainActivity";

            //向Service传递数据的常用方法之一，通过MyBinder中的方法来传递，可以每次BindService时重复使用
            myBinder.doSomething("杭州(MyBinder方法)", 1.17f);
            Log.i(TAG, "MainActivity设置MyBindService值-->City:杭州  GDP：1。17");


            if (myBindService != null) {
                //通过绑定服务传递的Service对象，获取Service暴露出来的数据
                Log.d(TAG, "MainActivity从MyBindService获取计数值：" + myBindService.getCount());
            } else {

                Log.d(TAG, "还没绑定呢，先绑定,无法从服务端获取数据");
            }

            tv.append("任务值-->City:" + myBindService.getCity() + "  GDP:" + myBindService.getGDP());
            Log.i(TAG, "MainActivity模拟任务完成后获取MyBindService值-->City:" + myBindService.getCity() + "  GDP:" + myBindService.getGDP());

            //从Service获取数据的又一种方法：回调,只适用于BindService
            myBindService.setCallback(new MyBindService.Callback() {
                @Override
                public void onStopService(String data) {
                    isServiceStop=true;
                    tv.append("\nMyStartService被MainActivity终止！");
                    Log.i(TAG, "onStopService: "+data);
                }
            });

        }

        //在与服务的连接意外中断时（例如当服务崩溃或被终止时）调用该方法。当客户端取消绑定时，系统“绝对不会”调用该方法。
        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceStop=true;
            Log.i(TAG, "*******MainActivity onServiceDisconnected Thread：" + Thread.currentThread().getName());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "*******MainActivity -> onCreate, Thread: " + Thread.currentThread().getName());
        final Button bt1 = (Button) findViewById(R.id.bt1);
        final Button bt2 = (Button) findViewById(R.id.bt2);
        final Button bt3 = (Button) findViewById(R.id.bt3);
        final Button bt4 = (Button) findViewById(R.id.bt4);

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
                Log.i(TAG, "*******MainActivity执行startActivity跳转到MyActivity1");

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

                if (bt2.getText().equals("Start\n Service")) {
                    Log.i(TAG, "*******MainActivity执行startService");
                    bt2.setText("Stop\n Service");
                    intent.putExtra("city", "上海");
                    intent.putExtra("GDP", 2.67f);
                    //启动Service，该方法不能获取Service返回数据
                    startService(intent);
                    tv.setText("MainActivity启动MyStartService,详情请查看终端Log输出。");
                } else {
                    Log.i(TAG, "*******MainActivity执行stoptService");
                    bt2.setText("Start\n Service");
                    //停止Service
                    stopService(intent);

                }
            }
        });

        //Service绑定和消息传递演
        bt3.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                final Intent intent = new Intent(MainActivity.this, MyBindService.class);

                if (bt3.getText().equals("Bind\n Service")) {
                    bt3.setText("Unbind\n Service");
                    tv.setText("MainActivity绑定MyBindService");

                    //向Service 传递数据的一种方法，只能第一次bindService时使用
                    intent.putExtra("city", "广州(Bind Intent)");
                    intent.putExtra("GDP", 2.00f);

                    //绑定指定Serivce
                    //flags则是指定绑定时是否自动创建Service:0代表不自动创建、BIND_AUTO_CREATE则代表自动创建。
                    bindService(intent, conn, Service.BIND_AUTO_CREATE);
                    Log.i(TAG, "*******MainActivity 执行 bindService");
                } else {
                    bt3.setText("Bind\n Service");
                    tv.setText("MainActivity解除绑定MyBindService!\nMyBindService返回值-->" + myBindService.getCount());
                    // 解除绑定Serivce
                    unbindService(conn);
                    Log.i(TAG, "*******MainActivity 执行 unbindService");
                }
            }
        });


        //MyBindService被Start方式启动后将一直存在，直到所有宿主Unbind然后又被显式Stop，
        //在这之前任何宿主可以再次Bind，此时MyBindService的onRebind或onBind会被调用（取决于onUnbind的返回值）
        //startService和bindService先后顺序无论怎样，效果是一样的，
        bt4.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyBindService.class);

                if (bt4.getText().equals("Rebind\n Start")) {
                    Log.i(TAG, "*******MainActivity执行startService---MyBindService");
                    bt4.setText("Rebind\n Stop");

                    intent.putExtra("city", "南京(Start Intent)");
                    intent.putExtra("GDP", 1.87f);

                    //启动Service，该方法不能获取Service返回数据
                    startService(intent);
                    tv.setText("MainActivity启动MyBindService,详情请查看终端Log输出。");
                } else {
                    Log.i(TAG, "*******MainActivity执行stoptService---MyBindService");
                    bt4.setText("Rebind\n Start");
                    //停止Service
                    stopService(intent);
                    tv.setText("MainActivity企图停止MyBindService！");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "*******MainActivity -> onDestroy, Thread: " + Thread.currentThread().getName());
    }
}
