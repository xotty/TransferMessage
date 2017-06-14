package org.xottys.transfermessage;

import android.content.Context;
import android.content.Intent;

/**
 * Created by changqing on 2017/6/13.
 */

public class MyBroadcastSend {
    private Context context;    //广播发送方法一定要用Context来调用，如果该广播作用域是全应用的就用ApplicationContext

    public MyBroadcastSend(Context context) {
        this.context = context;
    }

    //自定义广播发送
    public void mSendBroadcast(String str)

    {
        Intent intent = new Intent();
        intent.setAction("MyReceiver_2");      //一个广播发送只能设置一个接收者匹配的Action
        intent.putExtra("FROM", "Outside");    //封装要发送的消息1
        intent.putExtra("MSG", str);           //封装要发送的消息2
        context.sendBroadcast(intent);         //发送广播
    }
}
