/**
 * 隐式启动的Activity，有返回数据
 * <p>
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
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MyActivity2 extends Activity {
    private static final String TAG = "IntentDemo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my2);
        Button bt1 = (Button) findViewById(R.id.bt1);
        Button bt2 = (Button) findViewById(R.id.bt2);
        Button bt3 = (Button) findViewById(R.id.bt3);
        TextView tv = (TextView) findViewById(R.id.tv);
        bt1.setBackgroundColor(0xbd292f34);
        bt1.setTextColor(0xFFFFFFFF);
        bt3.setBackgroundColor(0xbd292f34);
        bt3.setTextColor(0xFFFFFFFF);
        bt2.setBackgroundColor(0xbd292f34);
        bt2.setTextColor(0xFFFFFFFF);

        //获取和解析上级Activity的Intent/Budle传递过来的数据
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String name = bundle.getString("name");
            float score = bundle.getFloat("score", -1f);
            Log.i("IntentDemo", "MyActivity2收到MyActivity2传入的数据：" + name + ":" + score);

            tv.setText("MyActivity1---->MyActivity2\n\n" + "传入数据-->" + name + ":" + score);
        }
        //用Action和Data组合调用系统电话拨号界面
        bt1.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL);
                String data = "tel:10086";
                Uri uri = Uri.parse(data);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        //返回上级MyActivity1
        bt2.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //返回数据也封装在Inten中传递
                Intent intent = new Intent();
                intent.putExtra("name", "王五");
                intent.putExtra("score", 70.5f);
                //返回数据封装完成
                setResult(RESULT_OK, intent);

                Log.i(TAG, "MyActivity2准备返回MyActivity1");

                finish();            //结束当前Activity，返回上一级
            }
        });

        //越级直接返回MainActivity
        bt3.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MyActivity2.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Log.i(TAG, "MyActivity2准备返回MainActivity");

                startActivity(intent);
            }
        });
    }

}
