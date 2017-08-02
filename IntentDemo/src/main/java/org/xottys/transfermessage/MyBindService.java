package org.xottys.transfermessage;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * 仅用Bind方法启动的Service,随着Activity的停止而能停止
 */
public class MyBindService extends Service {
    final private String TAG = "IntentDemo";

    private int count;
    private String city;
    private float GDP;
    private boolean quit;

    //定义onBinder方法所返回的对象
    private MyBinder myBinder;

    //Service第一次被绑定时会回调该方法且只会被回调一次，多次调用bindService()方法并不会导致多次回调该方法
    @Override
    public IBinder onBind(Intent intent) {
        myBinder = new MyBinder();  // 返回IBinder对象

        city = intent.getStringExtra("city");
        GDP = intent.getFloatExtra("GDP", -1f);
        Log.i(TAG, "{" + Thread.currentThread().getName() + "} MyBindService Binded! City:" + city + "  GDP:" + GDP);

        return myBinder;
    }

    //Service被创建时回调该方法，即第一次bindSrvice时才会回调该方法，整个生命周期中只会被回调一次
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "MyBindService is Created");
        // 启动一条线程、动态地修改count状态值
        new Thread() {
            @Override
            public void run() {
                while (!quit) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                    count++;
                }
            }
        }.start();
    }

    //最后一个与Service连接的宿主与Service的断开时会调该方法，通常通过宿主Activity退出或显式调用unbindService()方法来启动
    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "MyBindService is Unbinded");

        return true;   //onRebind()被调用的前提条件
    }

    //如果服务一直在后台（通常是用StartService启动后再Bind），将已经Bind的Service全部unBind后再次调用bindService()时，会回调本方法
    @Override
    public void onRebind(Intent intent) {
        Log.i(TAG, "MyBindService is Rebinded");

    }

    //Service被关闭之前回调该方法。
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.quit = true;
        Log.i(TAG, "MyBindService is Destroyed");
    }

    // Service 要完成的任务通常放在这里
    public class MyBinder extends Binder {

        //获取本Service的实例
        public MyBindService getTcpService() {
            return MyBindService.this;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String cty) {
            city = cty;
        }

        public float getGDP() {
            return GDP;
        }

        public void setGDP(float gdp) {
            GDP = gdp;
        }

        public int getCount() {
            // 获取Service的运行状态：count
            return count;
        }

        public void doSomething() {
            Log.i(TAG, "MyBindService的数据通常在这里设置 City:" + city + "  GDP:" + GDP);

            //模拟修改数据
            city = "深圳";
            GDP = 1.93f;
        }

        //可以用Service实例调用的方法
        public void doAnotherThing() {
            Log.i(TAG, "doAnotherThing");
        }
    }
}
