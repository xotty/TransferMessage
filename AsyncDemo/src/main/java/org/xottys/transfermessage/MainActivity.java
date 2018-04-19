/**
 * 本例演示了除HandlerThread和IntentService(这两个另有专题论述)以外所有的android异步方法
 * 1）普通线程Thread   2）AsyncTask   3）Executor  4）Loader  5）AsyncQueryHandler
 * <p>
 * <br/>Copyright (C), 2017-2018, Steve Chang
 * <br/>This program is protected by copyright laws.
 * <br/>Program Name:AsyncDEMO
 * <br/>Date:June，2017
 *
 * @author xottys@163.com
 * @version 1.0
 */
package org.xottys.transfermessage;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.lang.Thread.currentThread;

public class MainActivity extends Activity {
    private static final String TAG = "AsyncDemo";
    private final Object obj = new Object();
    private final byte[] lock = new byte[0];
    volatile Integer mSum = 0;
    private Button bt1, bt2, bt3, bt4;
    private TextView tv;
    private ProgressBar progressBar;
    private int count;
    private MyAsyncTask myAsyncTask;
    private Future<Integer> myFuture;
    private FutureTask myFutureTask;
    private ExecutorService myExectorService;
    private MyThread myThread1;
    private Thread myThread2;
    private PipedOutputStream outStream;
    private PipedInputStream inStream;

    private volatile boolean stopFlag = false;    //volatile主要用途之一，线程循环控制变量

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

        //Thread启动和消息传递，Thread除了可以用标准的变量传递方法
        //（详见VariableTransferDemo）外，还可以用下列2）3）两种方法：
        //1）Thread三种构造方法
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
        //2）共享内存方式的数据传递，主要用Volatile修饰符（如本例中的stopFlag）
        //3）PipedOutputStream／PipedInputStream方式传递数据
        //4）Synchonized，wait／notify的简单用法
        bt1.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //通过wait/notify机制同步两个线程的操作
                if (bt1.getText().equals("Start\n Thread")) {
                    bt1.setText("Interrupt\n Thread");
                    Log.i(TAG, "Start Thread");

                    //线程定义方法一
                    myThread1 = new MyThread();
                    myThread1.setName("myThread1");

                    //线程定义方法二
                    MyRunnable myRunnable = new MyRunnable();
                    myThread2 = new Thread(myRunnable);
                    myThread2.setName("myThread2");

                    //将两个线程的PipedOutputStream／PipedInputStream进行关联
                    try {
                        myThread1.getOutStream().connect(myRunnable.getInStream());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    myThread1.start();
                    myThread2.start();

                    tv.setText("线程启动，开始执行\n");
                    bt1.setBackgroundColor(0xFFD7D7D7);
                    bt1.setTextColor(0xbd292f34);

                    bt2.setTextColor(0xFFA0A0A0);
                    bt3.setTextColor(0xFFA0A0A0);
                    bt4.setTextColor(0xFFA0A0A0);
                    bt2.setBackgroundColor(0xbd292f34);
                    bt3.setBackgroundColor(0xbd292f34);
                    bt4.setBackgroundColor(0xbd292f34);
                    bt2.setEnabled(false);
                    bt3.setEnabled(false);
                    bt4.setEnabled(false);
                    progressBar.setProgress(0);
                    //中断线程运行
                } else if (bt1.getText().equals("Interrupt\n Thread")) {
                    myThread1.interrupt();
                    myThread2.interrupt();
                    tv.append("\n线程中断,当前计算结果：" + mSum + "\n");
                    bt1.setText("Callable\n Thread");

                    //通过synchronized机制同步两个线程的操作
                } else if (bt1.getText().equals("Callable\n Thread")) {
                    //匿名线程启动，线程名称为"myThread",它与后面用FutureTask启动的线程执行的是同一个用Sychonized加锁的方法，二者互斥
                    new Thread("myThread") {
                        @Override
                        public void run() {
                            int sum = doSomething("myThread");
                        }
                    }.start();

                    tv.append("\n\nCallable线程启动");

                    //线程定义方法三，FutureTask是为了弥补Thread的不足而设计的，多用于耗时的计算
                    //它可以让程序员准确地知道线程什么时候执行完成并获得到线程执行完成后返回的结果
                    MyCallable myCallable = new MyCallable();
                    //用Callable方式，定义和启动线程
                    FutureTask<Integer> myFutureTask = new FutureTask<>(myCallable);
                    new Thread(myFutureTask).start();

                    //接收线程运算后的结果,此部分将阻塞UI线程
                    try {
                        Integer result = myFutureTask.get();   //在所有的线程没有执行完成之后这里是不会执行的
                        System.out.println("Executor result:" + result);
                        tv.append("\nCallable线程结束，计算结果：" + result + "\n");
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

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

        //AsyncTask启动和消息传递
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
                    bt2.setBackgroundColor(0xFFD7D7D7);
                    bt2.setTextColor(0xbd292f34);

                    bt1.setTextColor(0xFFA0A0A0);
                    bt3.setTextColor(0xFFA0A0A0);
                    bt4.setTextColor(0xFFA0A0A0);
                    bt1.setBackgroundColor(0xbd292f34);
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

        //Executor启动和消息传递
        bt3.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                mSum = null;

                if (bt3.getText().equals("Start\n Executor")) {
                    MyCallable myCallable = new MyCallable();
                    myExectorService = Executors.newCachedThreadPool();

                    //启动异步线程，可以是Callable，也可以是Runnable
                    myFuture = myExectorService.submit(myCallable);
                    //另外两种启动Executor的方式如下：
                    //myFuture = myExectorService.submit(myRunnable,sum);
                    //myExectorService.execute(myRunnable);

                    try {
                        //关闭线程池，不再接收新任务
                        myExectorService.shutdown();

                        // 在指定的时间内所有的任务都结束的时候，返回true，反之返回false
                        if (!myExectorService.awaitTermination(2000, TimeUnit.MILLISECONDS)) {

                            myExectorService.shutdownNow();
                        }
                    } catch (InterruptedException e) {
                        //向所有执行中的线程发出interrupted以中止线程的运行
                        myExectorService.shutdownNow();
                    }

                    //继续做其它任务
                    Log.i(TAG, "任务开始于" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date()));
                    bt3.setText("CANCEL");
                    tv.setText("ExectorService.submit(myCallable)任务开始于：" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date()) + "\n");

                    new Thread() {
                        @Override
                        public void run() {
                            //获取线程运行结果，该部分代码会被阻塞，直到异步线程返回结果或被意外终止
                            try {
                                mSum = myFuture.get(10000, TimeUnit.MILLISECONDS);
                                Log.i(TAG, currentThread().getName() + "任务结束于" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date()) + "result=" + mSum);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tv.append("ExectorService.submit(myCallable)任务完成于：" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date()) + "---result=" + mSum + "\n");
                                    }
                                });

                            } catch (InterruptedException e) {
                                Log.e(TAG, currentThread().getName() + "任务终止于" + new SimpleDateFormat("yyyy/MM/dH:mm:ss", Locale.getDefault()).format(new Date()));

                            } catch (CancellationException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        bt3.setText("NEXT");
                                        tv.append("ExectorService.submit(myCallable)任务取消于" + new SimpleDateFormat("yyyy/MM/dH:mm:ss", Locale.getDefault()).format(new Date()) + "---result=" + mSum + "\n");
                                    }
                                });
                                Log.e(TAG, currentThread().getName() + "任务取消于" + new SimpleDateFormat("yyyy/MM/dH:mm:ss", Locale.getDefault()).format(new Date()) + "---result=" + mSum);

                            } catch (TimeoutException e) {
                                Log.e(TAG, "TAG, currentThread().getName()" + "获取结果超时。");

                            } catch (Exception e) {
                                Log.e(TAG, currentThread().getName() + "出现意外错误于" + new SimpleDateFormat("yyyy/MM/dH:mm:ss", Locale.getDefault()).format(new Date()));
                                e.printStackTrace();
                            }

                            Log.i(TAG, "取得结果后才能继续执行");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    bt3.setText("NEXT");
                                }
                            });
                        }
                    }.start();

                } else if (bt3.getText().equals("NEXT")) {
                    MyRunnable myRunnable = new MyRunnable();
                    myFutureTask = new FutureTask<Integer>(myRunnable, mSum) {
                        @Override
                        protected void done() {
                            try {
                                mSum = (Integer) myFutureTask.get();
                                Log.i(TAG, currentThread().getName() + "任务完成于" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date()) + "result=" + mSum);
                                bt3.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        bt3.setText("Start\n Executor");
                                        tv.append("ExectorService.excute(myFutureTask)任务完成于：" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date()) + "--result=" + mSum + "\n");
                                        bt1.setTextColor(0xFFFFFFFF);
                                        bt2.setTextColor(0xFFFFFFFF);
                                        bt4.setTextColor(0xFFFFFFFF);
                                        bt1.setEnabled(true);
                                        bt2.setEnabled(true);
                                        bt4.setEnabled(true);
                                    }
                                });
                            } catch (InterruptedException e) {
                                Log.e(TAG, currentThread().getName() + "任务终止于" + new SimpleDateFormat("yyyy/MM/dH:mm:ss", Locale.getDefault()).format(new Date()));

                            } catch (CancellationException e) {
                                Log.e(TAG, currentThread().getName() + "任务取消于" + new SimpleDateFormat("yyyy/MM/dH:mm:ss", Locale.getDefault()).format(new Date()) + "--result=" + mSum);
                                bt3.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        bt3.setText("Start\n Executor");
                                        tv.append("ExectorService.excute(myFutureTask)任务取消于：" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date()) + "--result=" + mSum + "\n");
                                        bt1.setTextColor(0xFFFFFFFF);
                                        bt2.setTextColor(0xFFFFFFFF);
                                        bt4.setTextColor(0xFFFFFFFF);
                                        bt1.setEnabled(true);
                                        bt2.setEnabled(true);
                                        bt4.setEnabled(true);
                                    }
                                });

                            } catch (Exception e) {
                                Log.e(TAG, currentThread().getName() + "出现意外错误于" + new SimpleDateFormat("yyyy/MM/dH:mm:ss", Locale.getDefault()).format(new Date()));
                                e.printStackTrace();
                            }
                        }
                    };
                    myExectorService = Executors.newFixedThreadPool(4);

                    //用FutureTask启动异步线程
                    myExectorService.execute(myFutureTask);
                    //另外两种FutureTask启动方法如下：
                    //myExectorService.submit(myFutureTask);
                    //new Thread(myFutureTask).start();

                    myExectorService.shutdown();

                    bt3.setText("Cancel");
                    tv.append(getResources().getString(R.string.mline) + "\n");
                    tv.append("ExectorService.excute(myFutureTask)任务开始于：" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date()) + "\n");
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

        //Loader启动和加载数据
        bt4.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoaderActivity.class);   //显式启动Activity
                startActivity(intent);

                bt1.setBackgroundColor(0xbd292f34);
                bt2.setBackgroundColor(0xbd292f34);
                bt3.setBackgroundColor(0xbd292f34);
                bt1.setTextColor(0xFFFFFFFF);
                bt2.setTextColor(0xFFFFFFFF);
                bt3.setTextColor(0xFFFFFFFF);
                progressBar.setProgress(0);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ----");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ----");
    }

    //模拟任务，从1加到100，并逐行打印
    //参数name用来区别是否需要加wait／notify和进行UI更新机制
    private  int doSomething(String name) {
        //全程锁，演示两个线程分别调用它时实际是分先后执行的
        synchronized (lock) {
            int sum = 0;
            //因要传递整数，故用DataOutputStream封装流
            DataOutputStream dos = new DataOutputStream(outStream);
            for (int i = 1; i <= 100; i++) {
                if (!Thread.currentThread().isInterrupted()) {
                    count = i;
                    sum += i;
                    mSum = sum;

                    if (name.equals("Thread")) {
                        try {
                            //通过PipedOutputSream发送计算结果
                            dos.writeInt(sum);
                            //刷新流缓冲区
                            outStream.flush();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (name.equals("Thread") || name.equals("Callable")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setProgress(count);
                                tv.append(".");
                            }
                        });
                    }
                    Log.i(TAG, currentThread().getName() + "------" + count);
                    //局部锁，演示两个线程处于互相交替执行状态
                    if (name.equals("Thread")) {
                        synchronized (obj) {
                            //一步计算完成，唤醒打印本步计算结果
                            obj.notifyAll();
                        }
                        synchronized (lock) {
                            try {
                                //等待本步结果打印后被唤醒
                                lock.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                } else
                    break;
            }

            stopFlag = true;   //设置打印结果线程结束的标志
            synchronized (obj) {
                //最后唤醒打印进程结束
                obj.notify();
            }
            //关闭输出流
            if (name.equals("Thread")) {
                try {
                    outStream.close();
                    Log.i(TAG, "PipedOutputSream Cloesd ");
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
            }
            return sum;
        }
    }


    //线程定义方法一
    public class MyThread extends Thread {

        public MyThread() {
            outStream = new PipedOutputStream();
        }

        public PipedOutputStream getOutStream() {
            return outStream;

        }

        @Override
        public void run() {
            //这个等待用来保证MyRunnable先执行以到达wait
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            doSomething("Thread");
        }
    }

    //线程定义方法二,功能是打印doSomthing每一步的计算结果
    public class MyRunnable implements Runnable {


        public MyRunnable() {
            inStream = new PipedInputStream();
        }

        public PipedInputStream getInStream() {
            return inStream;
        }

        @Override
        public void run() {
            if (!Thread.currentThread().isInterrupted()) {
                stopFlag = false;
                //因要接收整数，故用DataInputStream封装流
                DataInputStream myDataInputStream = new DataInputStream(inStream);
                do {
                    try {
                        synchronized (obj) {
                            //等待一步计算完成后被唤醒
                            obj.wait();
                            //为避免在输出流结束后输入流仍然执行读取操作而抛出异常，加此判断条件
                            if (!stopFlag) {
                                //从PipedInputStream中获取计算解结果
                                int xsum = myDataInputStream.readInt();
                                Log.i(TAG, currentThread().getName() + "---Sum=" + xsum);
                            }
                        }
                        synchronized (lock) {   //一步计算结果已打印完成，唤醒继续计算下一步
                            lock.notify();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        stopFlag = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                } while (!stopFlag);
                try {
                    inStream.close();
                    Log.i(TAG, "PipedInputStream Cloesd ");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv.append("\n线程执行结束，计算结果：" + mSum);
                        bt1.setText("Callable\n Thread");
                    }
                });
            }
        }
    }


    //线程定义方法三，可以有返回结果，可以抛出异常
    class MyCallable implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            int sum = 0;
            sum = doSomething("Callable");
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
            Thread myThread = new Thread("myThread") {
                @Override
                public void run() {
                    doSomething("Callable");
                }
            };
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
            if (!isCancelled()) {
                Log.i(TAG, "onProgressUpdate called----" + progresses[0] + "%");
                progressBar.setProgress(progresses[0]);
                tv.setText("AsyncTask准备开始......\n\nLoading..." + progresses[0] + "%");
            }
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

