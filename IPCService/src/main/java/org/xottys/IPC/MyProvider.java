/**
 * ContentProvider演示
 * <br/>Copyright (C), 2017-2018, Steve Chang
 * <br/>This program is protected by copyright laws.
 * <br/>Program Name:MyProider
 * <br/>Date:June，2017
 *
 * @author xottys@163.com
 * @version 1.0
 */

package org.xottys.IPC;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

public class MyProvider extends ContentProvider {

    // 第一次创建该ContentProvider时调用该方法
    @Override
    public boolean onCreate()
    {
        System.out.println("===onCreate方法被调用===");
        return true;
    }

    // 该方法的返回值代表了该ContentProvider所提供数据的MIME类型
    @Override
    public String getType(@NonNull Uri uri)
    {
        System.out.println("===getType方法被调用===");
        return null;
    }

    // 实现查询方法，该方法应该返回查询得到的Cursor
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String where,
                        String[] whereArgs, String sortOrder)
    {
        System.out.println(uri + "===query方法被调用===");
        System.out.println("where参数为：" + where);
        return null;
    }

    // 实现插入的方法，该方法应该新插入的记录的Uri
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values)
    {
        System.out.println(uri + "===insert方法被调用===");
        System.out.println("values参数为：" + values);
        return null;
    }

    // 实现删除方法，该方法应该返回被删除的记录条数
    @Override
    public int delete(@NonNull Uri uri, String where, String[] whereArgs)
    {
        System.out.println(uri + "===delete方法被调用===");
        System.out.println("where参数为：" + where);
        return 1;
    }

    // 实现删除方法，该方法应该返回被更新的记录条数
    @Override
    public int update(@NonNull Uri uri, ContentValues values, String where,
                      String[] whereArgs)
    {
        System.out.println(uri + "===update方法被调用===");
        System.out.println("where参数为："
                + where + ",values参数为：" + values);
        return 2;
    }
}
