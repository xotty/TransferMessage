/**
 * 此类负责构造和传递数据
 */

package org.xottys.transfermessage;

import android.util.Log;

class T1 {

    public static String static_para;

    public void m1() {
        //给数据赋初值
        T2 t2 = new T2("构造器传值：T1->T2");
        t2.para2 = "公共变量传值：T1->T2";
        t2.setP2("属性传值：T1->T2");

        //启动数据传递
        t2.m2_1();
        //模拟等待
        t2.waitAmoment();
        //启动数据传递
        t2.m2_2("方法参数传值：T1->T2");
        //模拟等待
        t2.waitAmoment();

        //给数据赋初值
        static_para = "静态变量传值：T1->T2";
        GVariable.G_VAR = "模拟全局变量传值：T1->T2";
        MyApplication myApp = MyApplication.getMyApplication();
        myApp.APP_PARA = "Application全局传值：T1->T2";
        Singleton.getInstance().S_VAR = "单例传值：T1->T2";
        //启动数据传递
        t2.m2_3();
    }
    public void m2() {
        Log.i("GT", "m2: "+static_para);
    }

}
