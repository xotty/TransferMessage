/**MyAidlInterface.aidl的实现文件，向外提供AIDL文件中声明的方法服务，Activity与Service通讯的方式之一，主要用于跨进程通讯
 *
 * <br/>Copyright (C), 2017-2018, Steve Chang
 * <br/>This program is protected by copyright laws.
 * <br/>Program Name:MyAidlService
 * <br/>Date:June，2017
 *
 * @author xottys@163.com
 * @version 1.0
 */

package org.xottys.IPC;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;

//AIDL文件的实现类，可以处理多线程访问的请求
public class MyAidlService extends Service {
    final private String TAG = "IPCDemo";

    //实现AIDL接口中的方法
    private final MyAidlInterface.Stub mBinder = new MyAidlInterface.Stub() {
        @Override
        public int getPid() {

            int pid = Process.myPid();
            System.out.println("MyAidlService getPid被调用：" + pid);
            return pid;
        }

        @Override
        public void basicTypes(int anInt, boolean aBoolean, double aDouble, String aString) {

            System.out.println("MyAidlService basicTypes被调用： aDouble: " + aDouble + " anInt: " + anInt + " aBoolean " + aBoolean + " aString " + aString);
        }
    };

    //返回实现接口方法的Binder实例
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "MyAidlService -> onCreate");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "MyAidlService -> onDestroy");
        super.onDestroy();
    }
}
