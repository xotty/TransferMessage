package org.xottys.transfermessage;

import android.util.Log;

/**
 * 在相关方法完成任务过程中，通过接口对象调用接口定义的回调方法。并通过方法参数传递消息
 */

public class A {

    private CallbackInterface mCallbackInterface;

    //方式1 先设置CallBack ，再调用方法
    public void setCallback(CallbackInterface mCallbackInterface) {
        this.mCallbackInterface = mCallbackInterface;
    }

    //该方法完成一系列任务，其中回调方法在需要的时候被调用，并传值出去，以便其它程序使用和处理
    public void doSomthing() {

        Log.d("CBOTransferDemo", "调用回调方法....");
        this.mCallbackInterface.callbackMethod("方法一：先设置CallBack再调用回调方法");

    }

    //方式2 直接把CallBack接口接口 当参数传入,在需要使用的时候直接传入,然后调用接口方法就可以了
    public void doSomthing(CallbackInterface mCallbackInterface) {
        Log.d("CBOTransferDemo", "调用回调方法....");
        mCallbackInterface.callbackMethod("方法二：直接把CallBack接口当参数传入回调方法");

    }
}
