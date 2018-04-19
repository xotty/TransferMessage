/**
 * 显式启动的Activity，无返回数据
 *
 * <p>
 * <br/>Copyright (C), 2017-2018, Steve Chang
 * <br/>This program is protected by copyright laws.
 * <br/>Program Name:Intent DEMO
 * <br/>Date:June，2017
 *
 * @author xottys@163.com
 * @version 1.0
 */
package org.xottys.transfermessage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MyActivity1 extends Activity {
    private static final String TAG = "IntentDemo";
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my1);
        Button bt1 = (Button) findViewById(R.id.bt1);
        Button bt2 = (Button) findViewById(R.id.bt2);
        tv = (TextView) findViewById(R.id.tv);
        bt1.setBackgroundColor(0xbd292f34);
        bt1.setTextColor(0xFFFFFFFF);
        bt2.setBackgroundColor(0xbd292f34);
        bt2.setTextColor(0xFFFFFFFF);

        //获取和解析 Intent携带的数据
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        float score = intent.getFloatExtra("score", -1f);
        Log.i(TAG, "MyActivity1已启动，接收数据：" + name + ":" + score);
        tv.setText("MainActivity---->MyActivity1\n\n" + "传入数据-->" + name + ":" + score);
        bt1.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                finish();       //结束当前Activity，返回上一级
            }
        });

        bt2.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //第二种数据设置方法：利用Bundle设置数据
                Bundle bundle = new Bundle();
                bundle.putString("name", "李四");
                bundle.putFloat("score", 82.5f);
                //隐式启动MyActivity2且需要返回结果
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setAction("MyActivity");   //隐式启动
                Log.i("IntentDemo", "MyActivity1准备隐式启动MyActivity2");
                startActivityForResult(intent, 0);//启动新Activity，要求返回结果
            }
        });
    }

    @Override
    //接收和处理Activty的返回结果
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       if (data!=null) {
           String name = data.getStringExtra("name");
           float score = data.getFloatExtra("score", -1f);
           tv.setText("MyActivity2---->MyActivity1" + "\n\n" + "返回数据-->" + name + ":" + score);
           Log.i("IntentDemo", "MyActivity1收到MyActivity2返回的结果：" + name + ":" + score);
           super.onActivityResult(requestCode, resultCode, data);
       }
    }

}