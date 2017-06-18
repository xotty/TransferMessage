package org.xottys.transfermessage;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 单独handler thread线程来处理异步任务，不支持BindService，会自动退出（不需要显式退出）
 */
public class MyIntentService extends IntentService {
    private static final String ACTION_FOO = "org.xottys.transfermessage.action.FOO";
    private static final String ACTION_BAZ = "org.xottys.transfermessage.action.BAZ";
    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "org.xottys.transfermessage.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "org.xottys.transfermessage.extra.PARAM2";
    final private String TAG = "IntentDemo";

    public MyIntentService() {

        super("myIntentService");           // 设置子线程名称
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

    //主要处理任务都放在这儿，这是一个单独的线程
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
                Log.d(TAG, "{" + Thread.currentThread().getName() + "}MyIntentService Started! City:" + city + "  GDP:" + GDP);
            }
        }
    }

    //因为IntentService在所有请求处理完成后会自动停止，所以这里重写了onDestroy()方法
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "{" + Thread.currentThread().getName() + "}onDestroy：MyIntentService Stoped！");

    }


    private void handleActionFoo(String param1, Float param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void handleActionBaz(String param1, Float param2) {

        Log.d(TAG, "{" + Thread.currentThread().getName() + "}MyIntentService！" + "City:" + param1 + "  GDP:" + param2);

    }
}
