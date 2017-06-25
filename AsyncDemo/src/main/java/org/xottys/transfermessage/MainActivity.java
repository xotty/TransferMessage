package org.xottys.transfermessage;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import static java.lang.Thread.currentThread;

public class MainActivity extends Activity {
    private static final String TAG = "AsyncDemo";


    private Button bt1, bt2, bt3, bt4;
    private TextView tv;
    private ProgressBar progressBar;
    private int count;
    private  MyAsyncTask myAsyncTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt1 = (Button) findViewById(R.id.bt1);
        bt2 = (Button) findViewById(R.id.bt2);
        bt3 = (Button) findViewById(R.id.bt3);
        bt4 = (Button) findViewById(R.id.bt4);

        bt1.setBackgroundColor(0xbd292f34);
        bt1.setTextColor(0xFFFFFFFF);
        bt2.setBackgroundColor(0xbd292f34);
        bt2.setTextColor(0xFFFFFFFF);
        bt3.setBackgroundColor(0xbd292f34);
        bt3.setTextColor(0xFFFFFFFF);
        bt4.setBackgroundColor(0xbd292f34);
        bt4.setTextColor(0xFFFFFFFF);
        tv = (TextView) findViewById(R.id.tv);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);


        // 获取系统的ContentResolver对象
        //contentResolver = getContentResolver();

        //MyMessengerService启动和消息传递
        bt1.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (bt1.getText().equals("Start\n Thread")) {
                    Log.i(TAG, "Thread");
//                    Intent intent = new Intent();
//                    intent.setAction("action.Messenger_SERVICE");
//                    //隐式的Intent进行转化，从而可以用来启动的Service
//                    PackageManager pm = getPackageManager();
//                    ResolveInfo info = pm.resolveService(intent, 0);
//                    if (info != null) {
//                        String packageName = info.serviceInfo.packageName;
//                        String serviceNmae = info.serviceInfo.name;
//                        ComponentName componentName = new ComponentName(packageName, serviceNmae);
//                        intent.setComponent(componentName);
//                        //启动远程 Service
//                        bindService(intent, conn, BIND_AUTO_CREATE);
//                    }
                    //线程定义方法一
                    Thread myThread1 = new MyThread();

                    myThread1.start();

                    //线程定义方法二
                    Thread myThread2 = new Thread(new MyRunnable());

                    myThread2.start();

                    bt1.setText("Stop\n Thread");
                    bt2.setTextColor(0xFFA0A0A0);
                    bt3.setTextColor(0xFFA0A0A0);
                    bt4.setTextColor(0xFFA0A0A0);
                    bt3.setBackgroundColor(0xbd292f34);
                    bt4.setBackgroundColor(0xbd292f34);
                    bt2.setEnabled(false);
                    bt3.setEnabled(false);
                    bt4.setEnabled(false);
                } else {

                    //解除Service绑定
                    //  unbindService(conn);
                    tv.setText("MyMessengerService解除绑定");

                    bt1.setText("Start\n Thread");
                    bt2.setTextColor(0xFFFFFFFF);
                    bt3.setTextColor(0xFFFFFFFF);
                    bt4.setTextColor(0xFFFFFFFF);
                    bt2.setEnabled(true);
                    bt3.setEnabled(true);
                    bt4.setEnabled(true);
                }
            }
        });

        //MyAidlService启动和消息传递
        bt2.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                if (bt2.getText().equals("Start\n AsyncTask")) {
                    Log.i(TAG, "Start AsyncTask");
                    count = 0;
                    //一个AsyncTask只能使用一次，要再次使用须再new一个任务，否则要报异常
                    myAsyncTask = new MyAsyncTask();
                    //启动AsyncTask，默认是串行执行。若想并行执行，启动方式须改为：
                    //myAsyncTask.executeOnExecutor(THREAD_POOL_EXECUTOR，"myPara")
                    myAsyncTask.execute("myPara");

                    bt2.setText("Stop\n AsyncTask");
                    bt1.setTextColor(0xFFA0A0A0);
                    bt3.setTextColor(0xFFA0A0A0);
                    bt4.setTextColor(0xFFA0A0A0);
                    bt3.setBackgroundColor(0xbd292f34);
                    bt4.setBackgroundColor(0xbd292f34);
                    bt1.setEnabled(false);
                    bt3.setEnabled(false);
                    bt4.setEnabled(false);
                } else {
                    //将AsyncTask的isCancelled()设置为"true"
                    myAsyncTask.cancel(true);

                    bt2.setText("Start\n AsyncTask");
                    bt1.setTextColor(0xFFFFFFFF);
                    bt3.setTextColor(0xFFFFFFFF);
                    bt4.setTextColor(0xFFFFFFFF);
                    bt1.setEnabled(true);
                    bt3.setEnabled(true);
                    bt4.setEnabled(true);
                }
            }
        });

        //跳转到另一个进程的Activity且可以获取返回值
        bt3.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent();
                ComponentName componentName = new ComponentName("org.xottys.IPC", "org.xottys.IPC.MainActivity");
                intent.setComponent(componentName);
                //另外一种Intent设置方法
                //Intent intent =  new Intent("MYACTION", Uri.parse("info://111"));

                startActivityForResult(intent, 0);

                bt3.setBackgroundColor(0xFFD7D7D7);
                bt3.setTextColor(0xbd292f34);
                bt4.setBackgroundColor(0xbd292f34);
                bt4.setTextColor(0xFFFFFFFF);
            }
        });

        //操作ContentProvider数据
        bt4.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {


                bt4.setBackgroundColor(0xFFD7D7D7);
                bt4.setTextColor(0xbd292f34);
                bt3.setBackgroundColor(0xbd292f34);
                bt3.setTextColor(0xFFFFFFFF);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ---");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ----");
    }

    private void doSomething() {
        for (int i = 1; i <= 100; i++) {
            count = i;
            System.out.println(currentThread().getName() + "------" + count);
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }

        }
    }


    //线程定义方法一
    public class MyThread extends Thread {

        @Override
        public void run() {
            doSomething();
        }
    }

    //线程定义方法二
    public class MyRunnable implements Runnable {
        @Override
        public void run() {
            doSomething();
        }

    }

    //执行异步任务,可以在任务的前中后给UI主线程发送消息
    class MyAsyncTask extends AsyncTask<String, Integer, Integer> {

        //UI线程中运行,用于在执行后台任务前做一些准备工作
        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute called");
            tv.setText("AsyncTask准备开始......\n\n");
        }

        //工作线程中执行后台任务,不可在此方法内修改UI
        @Override
        protected Integer doInBackground(String... params) {
            Log.i(TAG, "doInBackground called，收到参数："+params[0]);
            //为演示目的,另外开一线程完成具体工作
            Thread myThread = new MyThread();
            myThread.start();

            int mcount = 0;
            while (count <= 100) {
                //cancel后立即退出循环
                if (isCancelled())
                {   myThread.interrupt();
                    break;}

                //count改变时发布进度信息
                if (count != mcount) {
                    //发布进度,启动onProgressUpdate方法
                    publishProgress(count);
                    mcount = count;
                }

                //结束时退出循环
                if (count == 100) {
                    publishProgress(count);
                    break;
                }
            }
            //返回异步任务结果
            return count;
        }

        //UI线程中运行,用于更新进度信息
        @Override
        protected void onProgressUpdate(Integer... progresses) {
            Log.i(TAG, "onProgressUpdate called----" + progresses[0] + "%");
            progressBar.setProgress(progresses[0]);
            tv.setText("AsyncTask准备开始......\n\nLoading..." + progresses[0] + "%");
        }

        //UI线程中运行,onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(Integer result) {
            Log.i(TAG, "onPostExecute called");
            tv.append("\n\n后台任务完成：" + result.toString() + "\n");

        }

        //UI线程中运行,用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {
            Log.i(TAG, "onCancelled() called");
            tv.append("\n\nAsyncTask取消了！\n");
        }
    }
}
