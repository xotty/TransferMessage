/**
 * Description: 用Messenger方式提供对外传递消息的服务
 * <br/>Copyright (C), 2017-2018, Steve Chang
 * <br/>This program is protected by copyright laws.
 * <br/>Program Name:MyMessengerService
 * <br/>Date:June，2017
 *
 * @author xottys@163.com
 * @version 1.0
 */
package org.xottys.IPC;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;


//对AIDL进行了封装，service收到的请求是放在Handler的MessageQueue里面，只能发送消息，不能处理多线程并发请求,
public class MyMessengerService extends Service {
    private static final int RECEIVE_MESSAGE_CODE = 0x0001;
    private static final int SEND_MESSAGE_CODE = 0x0002;
    final private String TAG = "IPCDemo";
    //clientMessenger表示的是客户端的Messenger，可以通过来自于客户端的Message的replyTo属性获得，
    //其内部指向了客户端的ClientHandler实例，可以用clientMessenger向客户端发送消息
    private Messenger clientMessenger = null;

    //serverMessenger是自身的Messenger，其内部指向了ServerHandler的实例
    //客户端可以通过IBinder构建Server端的Messenger，从而向Server发送消息，
    //并由ServerHandler接收并处理来自于客户端的消息
    private Messenger serverMessenger = new Messenger(new ServerHandler());

    //获取Service自身Messenger所对应的IBinder，并将其发送共享给所有客户端
    @Override
    public IBinder onBind(Intent intent) {

        Log.i(TAG, "MyMessengerService ->onBind:");
        return serverMessenger.getBinder();
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "MyMessengerService -> onCreate");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "MyMessengerService -> onDestroy");
        clientMessenger = null;
        super.onDestroy();
    }

    //用ServerHandler接收并处理来自于客户端的消息
    private class ServerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "ServereHandler -> handleMessage");
            if (msg.what == RECEIVE_MESSAGE_CODE) {
                Bundle data = msg.getData();
                if (data != null) {
                    String str = data.getString("msg");
                    Log.i(TAG, "MyMessengerService收到客户端如下信息: " + str);
                }
                //通过Message的replyTo获取到客户端自身的Messenger，Server可以通过它向客户端发送消息
                clientMessenger = msg.replyTo;
                if (clientMessenger != null) {

                    Message msgToClient = Message.obtain();
                    msgToClient.what = SEND_MESSAGE_CODE;

                    //通过Message-Bundle封装要发送的信息
                    Bundle bundle = new Bundle();
                    bundle.putString("server", "你好!客户端，我是Server.");
                    bundle.putString("client", data.getString("msg"));
                    msgToClient.setData(bundle);
                    Log.i(TAG, "MyMessengerService向客户端回信:" + bundle.getString("server"));
                    try {
                        //发送消息给客户端
                        clientMessenger.send(msgToClient);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        Log.e(TAG, "MyMessengerService向客户端发送信息失败: " + e.getMessage());
                    }
                }
            }
        }
    }

}
