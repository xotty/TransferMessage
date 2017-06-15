package org.xottys.transfermessage;

import android.content.Intent;
import android.util.Log;

/**
 * 该类扮演封装后对外提供服务的角色（如API），利用回调方法留下使用者介入的接口
 * 在完成任务过程中，需要使用者介入时通过接口对象调用回调方法，并通过方法参数传递消息
 */

class CallbackClass {

    private CallbackInterface mCallbackInterface;

    //方式1 先设置回调接口对象 ，然后用该接口对象调用回调方法
    public void setCallbackInterface(CallbackInterface mCallbackInterface) {
        this.mCallbackInterface = mCallbackInterface;
    }

    //该方法完成一系列任务，其中回调方法在需要的时候被调用，并传值出去，以便其它程序使用和处理
    public void doSomthing() {

        Log.d("CBOTransferDemo", "调用回调方法....");
        this.mCallbackInterface.callbackMethod("方法一：先设置CallBack再调用回调方法");
    }

    //方式2 直接把回调接口对象当参数传入,然后用该接口对象调用回调方法
    public void doSomthing(CallbackInterface mCallbackInterface) {
        Log.d("CBOTransferDemo", "调用回调方法....");
        Intent intent = new Intent();
        intent.setAction("MyReceiver_1");
        intent.putExtra("MSG", "方式一：通过动态注册广播和内部广播接收类收发消息");
        mCallbackInterface.callbackMethod("方法二：直接把CallBack接口当参数传入回调方法");

    }
}
