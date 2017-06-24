/**
 * Description: AIDL文件，用java接口的方式声明了Service中要实现的方法
 * <br/>Copyright (C), 2017-2018, Steve Chang
 * <br/>This program is protected by copyright laws.
 * <br/>Program Name:MyAidlInterface.aidl
 * <br/>Date:June，2017
 *
 * @author xottys@163.com
 * @version 1.0
 */


package org.xottys.IPC;

interface MyAidlInterface {

    int getPid();
    void basicTypes(int anInt, boolean aBoolean, double aDouble, String aString);

}
