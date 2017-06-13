package org.xottys.transfermessage;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements CallbackInterface {
    private Button bt;
    private TextView tv;
    private A a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt = (Button) findViewById(R.id.bt);
        tv = (TextView) findViewById(R.id.tv);
        bt.setBackgroundColor(0xbd292f34);
        bt.setTextColor(0xFFFFFFFF);

        bt.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (bt.getText().equals("START")) {
                    bt.setText("Running......");
                    bt.setEnabled(false);
                    tv.setText(R.string.waitting);

                    a = new A();
                    //方式一：启动回调方法调用
                    a.setCallback(new CallbackInterface() {
                        @Override
                        public void callbackMethod(String str) {
                            System.out.println("方式一，与Callback设置同步定义回调方法，收到的数据为-->" + str);
                            tv.setText("方式一，同步定义回调方法，收到数据:\n" + str);
                            //下面是具体处理程序......
                        }
                    });
                    a.doSomthing();

                    //方式二：启动回调方法调用
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            a.doSomthing(MainActivity.this);
                        }

                    }, 3000);

                }
                //在主线程中启动HandlerThread的handleMessage方法
                else if (bt.getText().equals("NEXT-1")) {

                    // threadHandler2.sendEmptyMessage(1);    //模拟做第一件耗时操作

                    //threadHandler2.sendEmptyMessage(2);    //模拟做第二件耗时操作
                }
                //在子线程中启动HandlerThread的handleMessage方法
                else if (bt.getText().equals("NEXT-2")) {

//                    Thread t = new Thread3();
//                    t.setName("Thread3");
//                    t.start();


                }
                //演示结束，回到初始状态
                else {

                    bt.setText("START");
                    tv.setText(R.string.hello);
                }


            }
        });
    }

    //具体实现回调方法
    @Override
    public void callbackMethod(String str) {
        System.out.println("方式二，回调方法开始处理，收到的数据为-->" + str);
        tv.setText("方式二，单独定义回调方法，收到数据:\n" + str);
        bt.setText("END");
        bt.setEnabled(true);
        //下面是具体处理程序......
    }

}
