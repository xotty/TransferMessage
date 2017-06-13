package org.xottys.transfermessage;

/**
 * 单例其中的成员变量可以作为公共变量用来传递数据
 */

public class Singleton {
    private static final Singleton myInstance = new Singleton();
    String S_VAR;          //用来传递的变量;

    //构造单例
    private Singleton() {
    }

    public static final Singleton getInstance() {
        return myInstance;
    }


}
