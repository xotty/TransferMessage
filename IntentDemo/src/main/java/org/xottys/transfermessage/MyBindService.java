/**
 * 仅用Bind方法启动的Service,仅当与另一个应用组件绑定时，该Service才会运行。 多个组件可以同时绑定到该服务，
 * 但全部宿主(如Activity)解除绑定后，该服务即会被销毁；允许组件与服务进行交互、发送请求、获取结果、调用Service中的方法。
 * 1)onCreate
 * 2)onBind
 * 3)onServiceConnected
 *   onServiceConnected
 *   .......
 * 4)onUnBind(全部绑定解除时才调用，全部宿主销毁会自动解除绑定)
 * 5)onDestroy
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

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MyBindService extends Service {
    final private String TAG = "IntentDemo";
    public String calller;
    private int count;
    private String city;
    private float GDP;
    private boolean quit;

    //可以用Service实例调用的方法
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

    private Callback callback;

    //把Binder类的对象返回给客户端
    //Service第一次被绑定时会回调该方法且只会被回调一次，多次调用bindService()方法并不会导致多次回调该方法
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "*******MyBindService -> onBind, Thread: " + Thread.currentThread().getName());

        //生成所返回的对象
        MyBinder myBinder = new MyBinder();

        //这里接收传值只在第一个绑定时有效，因此并不常用
        city = intent.getStringExtra("city");
        GDP = intent.getFloatExtra("GDP", -1f);
        Log.i(TAG, "MyBindService在onBind中收到数据： City:" + city + "  GDP:" + GDP);

        return myBinder;
    }

    //Service被创建时回调该方法，即第一次bindSrvice时才会回调该方法，整个生命周期中只会被回调一次
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "*******MyBindService -> onCreate, Thread: " + Thread.currentThread().getName());

        // 启动一条线程、动态地修改count状态值
        new Thread() {
            @Override
            public void run() {
                while (!quit) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    count++;
                }
            }
        }.start();
    }

    //最后一个与Service连接的宿主与Service的断开时会调该方法，通常通过宿主Activity退出或显式调用unbindService()方法来启动
    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "*******MyBindService -> onUnbind, from:" + intent.getStringExtra("from"));
        return true;   //true：满足条件时onRebind()被调用  false：满足条件时onBind()被调用
    }

    //如果服务一直在后台（通常是用StartService启动后再Bind），将已经Bind的Service全部unBind后再次调用bindService()时，会回调本方法
    @Override
    public void onRebind(Intent intent) {
        Log.i(TAG, "*******MyBindService -> onRebind, from:" + intent.getStringExtra("from"));

    }

    //BindService中该方法可选，如果用Start方式启动则有用，否则可以省略
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "*******MyBindService -> onStartCommand, startId: " + startId + ", Thread: " + Thread.currentThread().getName());
        //获取和解析传入的消息
        city = intent.getStringExtra("city");
        GDP = intent.getFloatExtra("GDP", -1f);
        Log.i(TAG, "MyBindService在onStartCommand中获取传入的值：City:" + city + "  GDP:" + GDP);

        return START_NOT_STICKY;
    }

    //Service被关闭之前回调该方法。
    @Override
    public void onDestroy() {

        this.quit = true;
        if (callback != null) {
                callback.onStopService(calller);
                        }
        Log.i(TAG, "*******MyBindService -> onDestroy, Thread: " + Thread.currentThread().getName());
        super.onDestroy();
    }

    //用于返回给客户端即Activity使用，提供数据交换的接口
    //Service 要完成的任务通常放在这里
    public class MyBinder extends Binder {

        //返回当前对象MyBindService,这样我们就可在客户端调用Service的公共方法了
        public MyBindService getMyBindService() {
            return MyBindService.this;
        }


        public void doSomething(String mcity, float gdp) {
            Log.i(TAG, "MyBindService设置数据： City:" + mcity + "  GDP:" + gdp);
            //模拟修改数据
            city = mcity;
            GDP = gdp;
        }
    }

    //可以用Service实例调用的方法
    public int getCount() {
        // 获取Service的运行状态：count
        return count;
    }


    public void setCallback(Callback callback) {
          this.callback = callback;
    }

    static public interface Callback {
      void onStopService(String data);
  }

}
