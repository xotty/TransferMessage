/**
 * 通过T1类->T2类传递数据，演示类与类之间消息传递方式之"变量传递"的八种方法演示，
 * 1）公共成员变量传值
 * 2）属性传值（setter／getter）
 * 3）构造器传值
 * 4）方法参数传值
 * 5）静态变量传值
 * 6）模拟全局变量传值（静态变量）--GVariable
 * 7）Application全局传值--MyApplication
 * 8）单例传值--Sigleton
 * <p>
 * <br/>Copyright (C), 2017-2018, Steve Chang
 * <br/>This program is protected by copyright laws.
 * <br/>Program Name:Android UI Update DEMO
 * <br/>Date:June，2017
 *
 * @author xottys@163.com
 * @version 1.0
 */
package org.xottys.transfermessage;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.ref.WeakReference;


public class MainActivity extends Activity {
    public static String pString;    //用于在UI中显示传递结果

    public static Handler mHandler;
    private  TextView tv;
    private  Button bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt = findViewById(R.id.bt);
        tv = findViewById(R.id.tv);
        bt.setBackgroundColor(0xbd292f34);
        bt.setTextColor(0xFFFFFFFF);
        bt.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (bt.getText().equals("START")) {
                    bt.setText("Running......");
                    bt.setEnabled(false);

                    new Thread() {
                        public void run() {
                            T1 t1 = new T1();
                            //启动T1类中的数据传递
                            t1.m1();
                        }
                    }.start();

                } else {
                    bt.setText("START");
                    tv.setText(R.string.hello);
                }
            }
        });

        mHandler = new MyHandler(this,tv,bt);

    }

    //内部静态类Handler可以避免内存泄漏
    private  static class MyHandler extends Handler{
        //持有弱引用HandlerActivity,GC回收时会被回收掉.
        private final WeakReference<Activity> mActivty;
        private TextView tv;
        private Button bt;
        public MyHandler(Activity activity,TextView textView,Button button){
            mActivty =new WeakReference<>(activity);
            tv=textView;
            bt=button;
        }
        @Override
        public void handleMessage(Message msg) {

            Activity activity = mActivty.get();
            super.handleMessage(msg);
            if (activity != null) {

                if (msg.what == 1)
                    tv.setText(msg.what + ")" + pString + "\n");
                else
                    tv.append(msg.what + ")" + pString + "\n");

                Log.i("VariableTransferDemo", "收到传递值---" + msg.what + ")" + pString);
                if (msg.what == 7) {
                    bt.setText("END");
                    bt.setEnabled(true);
                }

                T1 t22=new T1();
                t22.m2();
            }
        }
    }
}
