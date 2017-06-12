package org.xottys.transfermessage;

/**
 * 此类负责接收数据，然后传递给UI线程
 */

public class T2 {
    public String para2;
    private String p;
    private String p2;

    //构造器传值
    public T2(String p) {
        this.p = p;
    }

    public String getP2() {
        return p2;
    }

    public void setP2(String p2) {
        this.p2 = p2;
    }

    public void m2_1()

    {

        //公共成员变量传值
        MainActivity.pString = para2;
        MainActivity.mHandler.sendEmptyMessage(1);

        waitAmoment();

        //属性传值（setter／getter）
        MainActivity.pString = p2;
        MainActivity.mHandler.sendEmptyMessage(2);

        waitAmoment();

        //显示构造器传值
        MainActivity.pString = this.p;
        MainActivity.mHandler.sendEmptyMessage(3);
    }

    //方法参数传值
    public void m2_2(String p2) {
        MainActivity.pString = p2;
        MainActivity.mHandler.sendEmptyMessage(4);
    }

    public void m2_3()

    {   //静态变量传值
        MainActivity.pString = T1.static_para;
        MainActivity.mHandler.sendEmptyMessage(5);

        waitAmoment();

        //模拟全局变量传值（静态）
        MainActivity.pString = GVariable.G_VAR;
        MainActivity.mHandler.sendEmptyMessage(6);

        waitAmoment();

        //Application全局传值
        MyApplication myApp = MyApplication.getMyApplication();
        MainActivity.pString = myApp.APP_PARA;
        MainActivity.mHandler.sendEmptyMessage(7);

        waitAmoment();

        //单例传值
        MainActivity.pString = Singleton.getInstance().S_VAR;
        MainActivity.mHandler.sendEmptyMessage(8);
    }

    //延时3s
    private void waitAmoment() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
