package org.xottys.transfermessage;

/**
 * 单例其中的成员变量可以作为公共变量用来传递数据
 */

public class Singleton {
    String S_VAR;          //用来传递的变量

    //静态内部类方式构造单例
    private Singleton() {
    }

    ;

    public static final Singleton getInstance() {

        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final Singleton INSTANCE = new Singleton();  //创建实例的地方
    }

}
