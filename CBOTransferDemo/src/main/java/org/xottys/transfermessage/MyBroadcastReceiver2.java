package org.xottys.transfermessage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


/**
 * 在独立类中自定义广播接收者，该接收者用静态方法（XML）注册
 */

public class MyBroadcastReceiver2 extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String msg = intent.getStringExtra("MSG");  //解析收到的消息
        Log.d("CBOTransferDemo", "MyBroadcastReceiver2收到-->" + msg);
    }
}
