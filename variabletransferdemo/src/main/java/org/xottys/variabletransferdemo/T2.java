package org.xottys.variabletransferdemo;

/**
 * Created by changqing on 2017/6/9.
 */

public class T2 {
    public String para2;
    private String p;
    private String p2;


    public T2(String p) {
        this.p = p;
        MainActivity.pString = this.p;
        System.out.println(this.p);

    }

    public String getP2() {
        return p2;
    }

    public void setP2(String p2) {
        this.p2 = p2;
    }

    public void m2_1()

    {
        MainActivity.pString = para2;
        System.out.println(para2);
        MainActivity.mHandler.sendEmptyMessage(1);

        waitAmoment();
        MainActivity.pString = p2;
        System.out.println(p2);
        MainActivity.mHandler.sendEmptyMessage(2);
        waitAmoment();
        MainActivity.pString = T1.para1;
        System.out.println(T1.para1);
        MainActivity.mHandler.sendEmptyMessage(3);
        waitAmoment();
        System.out.println(GVariable.G_VAR);
        MainActivity.pString = GVariable.G_VAR;
        MainActivity.mHandler.sendEmptyMessage(4);
    }

    public void m2_2(String p2) {   //waitAmoment();
        MainActivity.pString = p2;
        System.out.println(p2);
        MainActivity.mHandler.sendEmptyMessage(5);
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
