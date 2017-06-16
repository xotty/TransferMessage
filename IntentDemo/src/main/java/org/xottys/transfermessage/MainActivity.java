package org.xottys.transfermessage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
    private Button bt;
    private TextView tv;


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

//                    bt.setText("Running......回调");
//                    bt.setEnabled(false);
//                    tv.setText("           一、回调（CallBack）演示\n\n");
                    Log.d("IntentDemo", "MainActivity准备启动MyActivity1");
                    Intent intent = new Intent(MainActivity.this, MyActivity1.class);   //显式启动Activity
                    //第一种：直接用Intent设置数据
                    intent.putExtra("name", "张三");
                    intent.putExtra("score", 95.5f);

                    startActivity(intent);      //启动新Activity，不要求返回结果

                }
                //BroadcastReceiver演示
                else if (bt.getText().equals("NEXT-1")) {
                    bt.setText("Running......广播");
                    bt.setEnabled(false);

                    tv.append("    二、广播（BroadcastReceiver）演示\n\n");
                    Intent intent = new Intent();
                    intent.setAction("MyReceiver_1");   //设置接受者匹配的Action
                    intent.putExtra("FROM", "Inside");  //封装要发送的消息1
                    intent.putExtra("MSG", "方法一：通过动态注册广播和内部广播接收类收发消息"); //封装要发送的消息2
                    sendBroadcast(intent);              //发送广播，在Activity、Service中可直接使用，否则要加Context

                }
                //Observer演示
                else if (bt.getText().equals("NEXT-2")) {
                    bt.setText("Running......观察者");
                    bt.setEnabled(false);


                }

                //演示结束，回到初始状态
                else {
                    bt.setText("START");

                    tv.setText(R.string.hello);
                }


            }
        });


    }
}
