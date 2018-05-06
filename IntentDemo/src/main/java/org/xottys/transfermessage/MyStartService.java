/**
 * 仅用Start方法启动的Service,一旦启动，服务即可在后台无限期运行，即使启动服务的组件已被销毁也不受影响，需要显式Stop才能停止
 * 只能通过Intent传入数据，不能将结果返回给调用方，生命周期为：
 * 1）onCreate
 * 2）onStartCommand
 *    onStartCommand
 *    .......
 * 3）onDestory
 *
 * 通常在onStartCommand中处理各种事物
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
import android.os.IBinder;
import android.util.Log;

public class MyStartService extends Service {

    final private String TAG = "IntentDemo";


    @Override
    //必须实现的方法，用Start启动时，即认为不允许绑定，应返回null
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "*******MyStartService -> onBind, Thread: " + Thread.currentThread().getName());
        return null;
    }

    //首次创建服务时，系统将调用此方法来执行一次性设置程序（在调用 onStartCommand() 或 onBind() 之前）
    //如果服务已在运行，则不会调用此方法。该方法只被调用一次
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "*******MyStartService -> onCreate, Thread: " + Thread.currentThread().getName());

    }

    @Override
    //每次通过startService()方法启动Service时该方法都会被回调，Bind启动时无需实现此方法
    public int onStartCommand(Intent intent, int flags, int startId) {
        //flags参数的含义
        //START_FLAG_REDELIVERY：强制onStartCommand方法的返回值为START_REDELIVER_INTENT
        //START_FLAG_RETRY：当onStartCommand调用后一直没有返回值时，会尝试重新去调用onStartCommand()
        Log.i(TAG, "*******MyStartService -> onStartCommand, startId: " + startId + ", Thread: " + Thread.currentThread().getName());

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

        //START_STICKY：当Service因内存不足而被系统kill后，一段时间后内存再次空闲时，系统将会尝试重新创建此Service
        //其中传递的Intent将是null，除非有挂起的Intent
        //START_NOT_STICKY：当Service因内存不足而被系统kill后，即使系统内存再次空闲时，系统也不会尝试重新创建此Service
        //START_REDELIVER_INTENT：当Service因内存不足而被系统kill后，一段时间后内存再次空闲时，则会重建服务，
        //并通过传递给服务的最后一个 Intent 调用 onStartCommand()，任何挂起 Intent均依次传递。
        return START_NOT_STICKY;
    }

    //服务销毁时的回调，通过调用 stopSelf() 或 stopService() 来停止服务
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "*******MyStartService -> onDestroy, Thread: " + Thread.currentThread().getName());
    }
}
