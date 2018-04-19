package org.xottys.IPC;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final String TAG = "IPC";
    private TextView tvx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button bt1 = (Button) findViewById(R.id.bt1);
        Button bt2 = (Button) findViewById(R.id.bt2);
        tvx = (TextView) findViewById(R.id.tv);

        Log.i(TAG, "IPC MainActivity已启动");

        tvx.setText("Hello,IPC Service" );

        //返回Activity数据
        bt1.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //返回数据封装在Inten中传递
                Intent intent = new Intent();
                intent.putExtra("name", "王五");
                intent.putExtra("score", 70.5f);
                //返回数据封装完成
                setResult(1, intent);
                finish();
            }
        });

        //发送跨进程广播
        bt2.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                //封装广播消息
                Bundle bundle = new Bundle();
                bundle.putString("msg", "来自IPC Sevice的问候！");
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setAction("ACTION_IPCDemo");
                //发送广播
                sendBroadcast(intent);

                Log.i(TAG, "IPC 发送广播："+bundle.getString("msg"));
                tvx.setText("IPC 发送广播:"+bundle.getString("msg"));
                new Handler().postDelayed(new Runnable() {

                    public void run() {

                        finish();

                    }

                }, 2000);

            }
        });
    }
}

