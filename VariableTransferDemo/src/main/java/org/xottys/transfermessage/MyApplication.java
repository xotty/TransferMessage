package org.xottys.transfermessage;

import android.app.Application;

/**
 * 利用Application的单例特性，其中的成员变量可以作为公共变量用来传递数据
 */

public class MyApplication extends Application {
    //用来传递的变量
    String APP_PARA;

    //用于在普通类（非Activity、Service）中获得MyAppliction实例（系统单例）
    private static MyApplication myApplication;

    public
    static MyApplication getMyApplication() {
        return myApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
    }
}