package org.xottys.variabletransferdemo;

/**
 * Created by changqing on 2017/6/9.
 */

public class T1 {


    public static String para1;
    //public Handler handler;

    public void m1() {
        GVariable.G_VAR = "模拟全局变量传值：T1->T2";
        para1 = "静态变量传值：T1->T2";
        T2 t2 = new T2("构造器传值：T1->T2");
        t2.para2 = "公共变量传值：T1->T2";
        t2.setP2("属性传值：T1->T2");
        t2.m2_1();
        // MainActivity.mHandler.sendEmptyMessage(0);
        //   m2();
//        new Thread() {
//            public void run() {
//               m2();
//            }
//
//        }.start();


        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        t2.m2_2("方法参数传值：T1->T2");


    }

//    public void m2()
//    {   T2 t2 = new T2("构造器传值：T1->T2");
//        t2.m2_2("方法参数传值：T1->T2");
//         MainActivity.mHandler.sendEmptyMessage(2);
//    }
}
