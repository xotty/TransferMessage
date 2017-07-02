package org.xottys.transfermessage;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 单独handler thread线程来处理异步任务，不支持BindService，会自动退出（不需要显式退出）
 * 多次启动IntentService时实例也只有一个，每次启动的任务都会进入消息队列中，直到全部任务完成才会自动终止
 */
public class MyIntentService extends IntentService {
    private static final String ACTION_FOO = "org.xottys.transfermessage.action.FOO";
    private static final String ACTION_BAZ = "org.xottys.transfermessage.action.BAZ";

    private static final String EXTRA_PARAM1 = "org.xottys.transfermessage.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "org.xottys.transfermessage.extra.PARAM2";
    final private String TAG = "IntentDemo";

    public MyIntentService() {

        super("myIntentService");      // 设置子线程名称
        setIntentRedelivery(true);     //该service在onHandleIntent返回之前死掉则会自动重新启动并投递intent
    }


    public static void startActionFoo(Context context, String param1, Float param2) {
        Intent intent = new Intent(context, MyIntentService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    public static void startActionBaz(Context context, String param1, Float param2) {
        Intent intent = new Intent(context, MyIntentService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    //主要处理任务都放在这儿，这是一个单独的异步处理方法，可以处理耗时工作
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final Float param2 = intent.getFloatExtra(EXTRA_PARAM2, -1f);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final Float param2 = intent.getFloatExtra(EXTRA_PARAM2, -1f);
                handleActionBaz(param1, param2);
            } else {
                String city = intent.getStringExtra("city");
                float GDP = intent.getFloatExtra("GDP", -1f);
                Log.i(TAG, "{" + Thread.currentThread().getName() + "}MyIntentService Started! City:" + city + "  GDP:" + GDP);
            }
        }
    }

    //因为IntentService在所有请求处理完成后会自动停止，所以这里重写了onDestroy()方法
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "{" + Thread.currentThread().getName() + "}onDestroy：MyIntentService Stoped！");

    }


    private void handleActionFoo(String param1, Float param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void handleActionBaz(String param1, Float param2) {

        Log.i(TAG, "{" + Thread.currentThread().getName() + "}MyIntentService！" + "City:" + param1 + "  GDP:" + param2);

    }
}
