package org.xottys.transfermessage;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.currentThread;

public class MainActivity extends Activity {
    private static final String TAG = "AsyncDemo";
    Integer sum = 2,mSum;
    private Button bt1, bt2, bt3, bt4;
    private TextView tv;
    private ProgressBar progressBar;
    private int count;
    private MyAsyncTask myAsyncTask;
    private Future<Integer> myFuture;
    private FutureTask myFutureTask;
    private ExecutorService myExectorService;

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

                    //线程定义方法一
                    Thread myThread1 = new MyThread();
                    myThread1.start();

                    //线程定义方法二
                    Thread myThread2 = new Thread(new MyRunnable());
                    myThread2.start();

                    //线程定义方法三，FutureTask是为了弥补Thread的不足而设计的，多用于耗时的计算
                    // 它可以让程序员准确地知道线程什么时候执行完成并获得到线程执行完成后返回的结果
                    MyCallable myCallable = new MyCallable();
                    //用Callable方式，定义和启动线程
                    FutureTask<Integer> myFutureTask = new FutureTask<>(myCallable);
                    new Thread(myFutureTask).start();

                    while (!myFutureTask.isDone()) {
                        try {
                            Thread.sleep(50);
                            System.out.println("任务还未完成，请等待......");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    //接收线程运算后的结果
                    try {
                        Integer result = myFutureTask.get();   //在所有的线程没有执行完成之后这里是不会执行的
                        System.out.println("Executor result:" + result);
                        tv.setText("Executor result:" + result);
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    bt1.setText("Stop\n Thread");
                    bt2.setTextColor(0xFFA0A0A0);
                    bt3.setTextColor(0xFFA0A0A0);
                    bt4.setTextColor(0xFFA0A0A0);
                    bt3.setBackgroundColor(0xbd292f34);
                    bt4.setBackgroundColor(0xbd292f34);
                    bt2.setEnabled(false);
                    bt3.setEnabled(false);
                    bt4.setEnabled(false);
                    progressBar.setProgress(0);
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
                    progressBar.setProgress(0);
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
                mSum=null;

                if (bt3.getText().equals("Start\n Executor")) {
                    MyCallable myCallable = new MyCallable();
                    myExectorService = Executors.newCachedThreadPool();

                    myFuture = myExectorService.submit(myCallable);
                    //另外两种启动Executor的方式如下：
                    //Future<Integer> myFuture = executor.submit(myRunnable,sum);
                    //executor.execute(myRunnable);

                    myExectorService.shutdown();    //关闭线程池，不再接收新任务

                    //继续做其它任务
                    Log.i(TAG, "任务开始于" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
                    bt3.setText("CANCEL");
                    tv.setText("ExectorService.submit(myCallable)任务开始于："+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date())+"\n");

                    new Thread() {
                        @Override
                        public void run() {
                            //获取线程运行结果，该部分代码会被阻塞，直到线程返回结果或被意外终止
                            try {
                                  mSum = myFuture.get(10000, TimeUnit.MILLISECONDS);
                                Log.i(TAG, currentThread().getName() + "任务结束于" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) + "result=" + mSum);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tv.append("ExectorService.submit(myCallable)任务完成于：" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) + "---result=" + mSum + "\n");
                                    }});

                            } catch (InterruptedException e) {
                                Log.e(TAG, currentThread().getName() + "任务终止于" + new SimpleDateFormat("yyyy/MM/dH:mm:ss").format(new Date()));

                            } catch (CancellationException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        bt3.setText("NEXT");
                                        tv.append("ExectorService.submit(myCallable)任务取消于" + new SimpleDateFormat("yyyy/MM/dH:mm:ss").format(new Date())+"---result=" + mSum +"\n");
                                    }
                                });
                                Log.e(TAG, currentThread().getName() + "任务取消于" + new SimpleDateFormat("yyyy/MM/dH:mm:ss").format(new Date())+"---result=" + mSum );

                            } catch (Exception e) {
                                Log.e(TAG, currentThread().getName() + "出现意外错误于" + new SimpleDateFormat("yyyy/MM/dH:mm:ss").format(new Date()));
                                e.printStackTrace();
                            }

                            Log.i(TAG, "取得结果后才能继续执行");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    bt3.setText("NEXT");
                                }
                            });
                        }}.start();

                } else if (bt3.getText().equals("NEXT")) {

                    MyRunnable myRunnable = new MyRunnable();
                    myFutureTask = new FutureTask<Integer>(myRunnable, sum) {
                        @Override
                        protected void done() {
                            try {
                                mSum = (Integer) myFutureTask.get();
                                Log.i(TAG, currentThread().getName() + "任务完成于" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()) + "result=" + mSum);
                                bt3.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        bt3.setText("Start\n Executor");
                                        tv.append("ExectorService.excute(myFutureTask)任务完成于："+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date())+ "--result=" + mSum+"\n");
                                        bt1.setTextColor(0xFFFFFFFF);
                                        bt2.setTextColor(0xFFFFFFFF);
                                        bt4.setTextColor(0xFFFFFFFF);
                                        bt1.setEnabled(true);
                                        bt2.setEnabled(true);
                                        bt4.setEnabled(true);
                                    }
                                });
                            } catch (InterruptedException e) {
                                Log.e(TAG, currentThread().getName() + "任务终止于" + new SimpleDateFormat("yyyy/MM/dH:mm:ss").format(new Date()));

                            } catch (CancellationException e) {
                                Log.e(TAG, currentThread().getName() + "任务取消于" + new SimpleDateFormat("yyyy/MM/dH:mm:ss").format(new Date())+ "--result=" + mSum);
                                bt3.post(new Runnable() {
                                    @Override
                                    public void run() {
                                bt3.setText("Start\n Executor");
                                tv.append("ExectorService.excute(myFutureTask)任务取消于："+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date())+ "--result=" + mSum+"\n");
                                        bt1.setTextColor(0xFFFFFFFF);
                                        bt2.setTextColor(0xFFFFFFFF);
                                        bt4.setTextColor(0xFFFFFFFF);
                                        bt1.setEnabled(true);
                                        bt2.setEnabled(true);
                                        bt4.setEnabled(true);
                                    }
                                });

                            } catch (Exception e) {
                                Log.e(TAG, currentThread().getName() + "出现意外错误于" + new SimpleDateFormat("yyyy/MM/dH:mm:ss").format(new Date()));
                                e.printStackTrace();
                            }
                        }
                    };
                    myExectorService = Executors.newFixedThreadPool(4);
                    myExectorService.execute(myFutureTask);
                    myExectorService.shutdown();
                    bt3.setText("Cancel");
                    tv.append(getResources().getString(R.string.mline)+"\n");
                    tv.append("ExectorService.excute(myFutureTask)任务开始于："+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date())+"\n");
                } else if (bt3.getText().equals("CANCEL")) {
                    myFuture.cancel(true);
                } else if (bt3.getText().equals("Cancel")) {
                    myFutureTask.cancel(true);
                }

                bt3.setBackgroundColor(0xFFD7D7D7);
                bt3.setTextColor(0xbd292f34);

                bt1.setTextColor(0xFFA0A0A0);
                bt2.setTextColor(0xFFA0A0A0);
                bt4.setTextColor(0xFFA0A0A0);
                bt1.setBackgroundColor(0xbd292f34);
                bt2.setBackgroundColor(0xbd292f34);
                bt4.setBackgroundColor(0xbd292f34);
                bt1.setBackgroundColor(0xbd292f34);
                bt1.setEnabled(false);
                bt2.setEnabled(false);
                bt4.setEnabled(false);
                progressBar.setProgress(0);
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

    private int doSomething() {
        int sum = 0;
        for (int i = 1; i <= 100; i++) {
            count = i;
            sum += i;
            System.out.println(currentThread().getName() + "------" + count);
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("Interrupted");

                break;
            }

        }
        return sum;
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

    //线程定义方法三，可以有返回结果，可以抛出异常，可以中途干预
    class MyCallable implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            int sum = doSomething();
            return sum;
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
            Log.i(TAG, "doInBackground called，收到参数：" + params[0]);
            //为演示目的,另外开一线程完成具体工作
            Thread myThread = new MyThread();
            myThread.start();

            int mcount = 0;
            while (count <= 100) {
                //cancel后立即退出循环
                if (isCancelled()) {
                    myThread.interrupt();
                    break;
                }

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
