package org.xottys.transfermessage;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * 仅用Start方法启动的Service,需要显式Stop才能停止
 */
public class MyStartService extends Service {

    final private String TAG = "IntentDemo";


    @Override
    //必须实现的方法
    public IBinder onBind(Intent intent) {
        return null;    //不允许绑定时应返回null
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: MyStartService Created!");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "{" + Thread.currentThread().getName() + "}onDestroy: MyStartService Stoped!");

    }

    @Override
    //多次调用startService()方法并不会导致多次创建服务，但会导致多次调用onStartCommand()方法。
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStart: MyStartService Started!");
        //获取和解析传入的消息
        String city = intent.getStringExtra("city");
        float GDP = intent.getFloatExtra("GDP", -1f);
        Log.i(TAG, "{" + Thread.currentThread().getName() + "} MyStartService Started! City:" + city + "  GDP:" + GDP);
        Log.i(TAG, "MyStartService准备启动MyIntentService");

        //启动IntentService，Android 5.0之后google出于安全的角度禁止了隐式声明Intent来启动Service
        Intent mIntent = new Intent(this, MyIntentService.class);
        mIntent.putExtra("city", "北京");
        mIntent.putExtra("GDP", 2.45f);
        startService(mIntent);
        MyIntentService.startActionBaz(MyStartService.this, "天津", 1.78f);

        return START_NOT_STICKY;
    }
}
