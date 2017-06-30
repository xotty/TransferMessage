/**
 * 自定义Loader必须重写下列父类方法，本例是一个基本模版
 *1）loadInBackground()
 *2）cancelLoadInBackground
 *3）onStartLoading
 *4）onStopLoading
 *5）onReset
 *6）onCanceled
 *7）deliverResult
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

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.CancellationSignal;
import android.os.OperationCanceledException;

import java.util.ArrayList;
import java.util.List;

//本类只是为了说明问题，并没有在Demo中启用
//自定义Loader通常是AsyncTaskLoader的子类，List可以是任意数据类型
public class MyLoader extends AsyncTaskLoader<List<String>> {

    CancellationSignal mCancellationSignal;

    private List<String> mData;
    //构造器，至少要有一个Context参数
    public MyLoader(Context context) {
        super(context);
    }

    //通常在这里执行数据获取的任务
    @Override
    public List<String> loadInBackground() {
        synchronized (this) {
            if (isLoadInBackgroundCanceled()) {
                throw new OperationCanceledException();
            }
            mCancellationSignal = new CancellationSignal();
        }
        if (mData == null) {
            mData = new ArrayList<String>();
        }
        try {
            //模拟耗时操作
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            // 模拟读数据

        } finally {
            synchronized (this) {
                mCancellationSignal = null;
            }
        }

        return mData;
    }

    //取消后台数据获取
    @Override
    public void cancelLoadInBackground() {
        super.cancelLoadInBackground();

        synchronized (this) {
            if (mCancellationSignal != null) {
                mCancellationSignal.cancel();
            }
        }
    }

   /**
     * 获取到新数据后向客户端发送
     */
    @Override
    public void deliverResult(List<String> data) {
        if (isReset()) {
            // An async query came in while the loader is stopped.  We
            // don't need the result.
            if (data != null) {
                onReleaseResources(data);
            }
        }
        List<String> olds = mData;
        mData = data;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(data);
        }

        if (olds != null) {
            onReleaseResources(olds);
        }
    }

    /**
     * 启动Loader
     */
    @Override
    protected void onStartLoading() {

        if (mData != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mData);
        }
        if (takeContentChanged() || mData == null) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }

    /**
     * 停止Loader
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();

    }

    /**
     * 取消Loader
     */
    @Override
    public void onCanceled(List<String> data) {
        super.onCanceled(data);

        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(data);
    }

    /**
     * 重置Loader
     */
    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (mData != null) {
            onReleaseResources(mData);
            mData = null;
        }

    }

    /**
     * Helper function to take care of releasing resources associated
     * with an actively loaded data set.
     */
    protected void onReleaseResources(List<String> data) {
        // For a simple List<> there is nothing to do.  For something
        // like a Cursor, we would close it here.
        mData = null;
    }
}
