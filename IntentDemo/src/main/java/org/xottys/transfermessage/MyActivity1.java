/**
 * 显式启动的Activity，无返回数据
 *
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

public class MyActivity1 extends Activity {
    private static final String TAG = "IntentDemo";
    private TextView tv;
    private MyBindService myBindService;
    private ServiceConnection conn = new ServiceConnection() {
        //每个宿主与service第一次Bind成功时会回调该方法，同一宿主第二次及以后bind同一Sercice时不会再回调本方法，除非unBindService后再次Bind
        @Override
        public void onServiceConnected(ComponentName name
                , IBinder service) {
            Log.i(TAG, "*******MyActivity1 onServiceConnected, Thread："+ Thread.currentThread().getName());
            //获取绑定Service传递过来的IBinder对象，通过这个IBinder对象，实现宿主和Service的交互。
            MyBindService.MyBinder myBinder = (MyBindService.MyBinder) service;

            //通过传递过来的IBinder对象中的方法来获取Service的对象以便调用其公共方法
            myBindService=myBinder.getMyBindService();

            tv.setText("MyBindService已绑定成功!\n\n初始值-->City:" + myBindService.getCity() + "  GDP:" + myBindService.getGDP() + "\n\n");
            Log.i(TAG, "MyBindService初始值-->City:" + myBindService.getCity() + "  GDP:" + myBindService.getGDP());

            //向Service 传递数据的又一种方法，通过MyBinderService中的方法来传递
            myBindService.setCity("深圳(MyBindService方法)");
            myBindService.setGDP(1.97f);

            myBindService.calller="MyActivity1";

            if (myBindService != null) {
                //通过绑定服务传递的Service对象，获取Service暴露出来的数据
                Log.d(TAG, "MyActivity1从MyBindService获取计数值：" + myBindService.getCount());
            } else {

                Log.d(TAG, "还没绑定呢，先绑定,无法从服务端获取数据");
            }

            tv.append("任务值-->City:" + myBindService.getCity() + "  GDP:" + myBindService.getGDP() + "\n\n");
            Log.i(TAG, "MyActivity1模拟任务完成后获取MyBindService值-->City:" + myBindService.getCity() + "  GDP:" + myBindService.getGDP());

        }

        //在与服务的连接意外中断时（例如当服务崩溃或被终止时）调用该方法。当客户端取消绑定时，系统“绝对不会”调用该方法。
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "*******MyActivity1 onServiceDisconnected Thread："+ Thread.currentThread().getName());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my1);
        Log.i(TAG, "*******MyActivity1 -> onCreate, Thread: " + Thread.currentThread().getName());

        Button bt1 = (Button) findViewById(R.id.bt1);
        Button bt2 = (Button) findViewById(R.id.bt2);
        final Button bt_start = (Button) findViewById(R.id.bt_startservice);
        final Button bt_bind = (Button) findViewById(R.id.bt_bindservice);
        tv = (TextView) findViewById(R.id.tv);
        bt1.setBackgroundColor(0xbd292f34);
        bt1.setTextColor(0xFFFFFFFF);
        bt2.setBackgroundColor(0xbd292f34);
        bt2.setTextColor(0xFFFFFFFF);

        //获取和解析 Intent携带的数据
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        float score = intent.getFloatExtra("score", -1f);
        Log.i(TAG, "MyActivity1接收到数据：" + name + ":" + score);
        tv.setText("MainActivity---->MyActivity1\n\n" + "传入数据-->" + name + ":" + score);
        bt1.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                finish();       //结束当前Activity，返回上一级
            }
        });

        bt2.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "*******MyActivity1执行startActivityForResult跳转到MyActivity2");
                //第二种数据设置方法：利用Bundle设置数据
                Bundle bundle = new Bundle();
                bundle.putString("name", "李四");
                bundle.putFloat("score", 82.5f);
                //隐式启动MyActivity2且需要返回结果
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setAction("MyActivity");   //隐式启动
                Log.i("IntentDemo", "MyActivity1准备隐式启动MyActivity2");
                startActivityForResult(intent, 0);//启动新Activity，要求返回结果
            }
        });

        //Service启动和消息传递
        bt_start.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MyActivity1.this, MyStartService.class);

                if (bt_start.getText().equals("Start\n Service")) {
                    Log.i(TAG, "*******MyActivity1执行startService");
                    bt_start.setText("Stop\n Service");
                    intent.putExtra("city", "福州");
                    intent.putExtra("GDP", 1.67f);
                    //启动Service，该方法不能获取Service返回数据
                    startService(intent);
                    tv.setText("MyStartService被MyActivity1启动,详情请查看终端Log输出。");
                } else {
                    Log.i(TAG, "*******MyActivity1执行stoptService");
                    bt_start.setText("Start\n Service");
                    //停止Service
                    stopService(intent);
                    tv.setText("MyStartService被MyActivity1终止！");
                }
            }
        });

        //Service绑定和消息传递演
        bt_bind.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                final Intent intent = new Intent(MyActivity1.this, MyBindService.class);

                if (bt_bind.getText().equals("Bind\n Service")) {
                    bt_bind.setText("Unbind\n Service");
                    tv.setText("MyActivity1绑定MyBindService");

                    //向Service 传递数据的一种方法，只能第一次bindService时使用
                    intent.putExtra("city", "西安");
                    intent.putExtra("GDP", 0.76f);

                    //绑定指定Serivce
                    //flags则是指定绑定时是否自动创建Service:0代表不自动创建、BIND_AUTO_CREATE则代表自动创建。
                    bindService(intent, conn, Service.BIND_AUTO_CREATE);
                    Log.i(TAG, "*******MyActivity1 执行 bindService");
                } else {
                    bt_bind.setText("Bind\n Service");
                    tv.setText("MyActivity1解除绑定MyBindService!\nMyBindService返回值-->" + myBindService.getCount());
                    // 解除绑定Serivce
                    unbindService(conn);
                    Log.i(TAG, "*******MyActivity1 执行 unbindService");
                }
            }
        });
    }


    @Override
    //接收和处理Activty的返回结果
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       if (data!=null) {
           String name = data.getStringExtra("name");
           float score = data.getFloatExtra("score", -1f);
           tv.setText("MyActivity2---->MyActivity1" + "\n\n" + "返回数据-->" + name + ":" + score);
           Log.i("IntentDemo", "MyActivity1收到MyActivity2返回的结果：" + name + ":" + score);
           super.onActivityResult(requestCode, resultCode, data);
       }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "*******MyActivity1 -> onDestroy, Thread: " + Thread.currentThread().getName());
    }
}