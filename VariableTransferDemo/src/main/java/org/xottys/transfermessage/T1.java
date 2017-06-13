package org.xottys.transfermessage;

/**
 * 此类负责构造和传递数据
 */

public class T1 {

    public static String static_para;

    public void m1() {


        T2 t2 = new T2("构造器传值：T1->T2");
        t2.para2 = "公共变量传值：T1->T2";
        t2.setP2("属性传值：T1->T2");

        t2.m2_1();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        t2.m2_2("方法参数传值：T1->T2");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        static_para = "静态变量传值：T1->T2";
        GVariable.G_VAR = "模拟全局变量传值：T1->T2";
        MyApplication myApp = MyApplication.getMyApplication();
        myApp.APP_PARA = "Application全局传值：T1->T2";
        Singleton.getInstance().S_VAR = "单例传值：T1->T2";
        t2.m2_3();
    }


}
