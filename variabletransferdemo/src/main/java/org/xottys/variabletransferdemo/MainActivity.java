package org.xottys.variabletransferdemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends Activity {
    public static String pString;
    public static Handler mHandler;
    public static TextView tv;
    private static Button bt;

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
                    tv.setText(R.string.waitting);       //这个textView是用来演示更新的UI元素
                    new Thread() {
                        public void run() {
                            T1 t1 = new T1();
                            t1.m1();
                        }

                    }.start();

                } else {
                    bt.setText("START");
                    tv.setText(R.string.hello);
                }
//t1.m2();
                //T2 t2=new T2("");
            }
        });

        mHandler = new Handler()

        {
            @Override
            public void handleMessage(Message msg) {
                tv.setText(msg.what + ")" + pString);
                Log.d("HandlerDemo", "收到消息---" + msg.what);
                if (msg.what == 5) {
                    bt.setText("END");
                    bt.setEnabled(true);
                }


            }
        };
    }

}
