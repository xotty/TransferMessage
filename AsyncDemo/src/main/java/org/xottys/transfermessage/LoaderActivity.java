/**
 * Loader主要用来在Activity和Fragment中异步加载UI数据，本例演示了
 * 1）用CursorLoader异步加载显示用户通讯录姓名到ListView中的过程
 * 2）用AsyncQueryHandler对通讯录进行异步增删改查操作（CRUD）
 * 自定义Loader的用法在Activity中基本一样，另外需要自己写一个MyLoader以实现非数据库数据的加载
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
import android.app.LoaderManager;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class LoaderActivity extends Activity {

    // 这是我们想获取的联系人中一行的部分列数据
    static final String[] CONTACTS_SUMMARY_PROJECTION = new String[]{
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME
    };
    static final Uri uri = ContactsContract.Contacts.CONTENT_URI;
    MyQueryHandler myQueryHandler;
    private Button bt1, bt2;
    private ListView listview;
    private TextView tv;
    // 这是用于显示列表数据的Adapter
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);
        bt1 = (Button) findViewById(R.id.bt1);
        bt2 = (Button) findViewById(R.id.bt2);

        listview = (ListView) findViewById(R.id.lv);
        tv = (TextView) findViewById(R.id.tv);

        adapter = new ArrayAdapter<String>
                (LoaderActivity.this, R.layout.array_item, new ArrayList<String>());
        listview.setAdapter(adapter);

        bt1.setBackgroundColor(0xbd292f34);
        bt1.setTextColor(0xFFFFFFFF);
        bt2.setBackgroundColor(0xbd292f34);
        bt2.setTextColor(0xFFFFFFFF);

        //初始化Loader,指定Callback所在的类
        Bundle bd = new Bundle();
        bd.putString("Para1", "Name");
        bd.putInt("Para2", 2);
        getLoaderManager().initLoader(0, bd, new DataLoaderCallback());
        Log.i(TAG, "Init Loader......");
        //返回上一级
        bt1.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        //启动AsyncQueryHandler
        bt2.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //添加数据
                if (bt2.getText().equals("AsyncQuery\nHandler(Insert)")) {

                    tv.setText("AsyncQueryHandler开始......\n");
                    Log.i(TAG, "AsyncQueryHandler开始......");
                    //先清空通讯录列表中的中原有数据
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                    //给通讯录添加1条新数据，
                    myQueryHandler = new MyQueryHandler(getContentResolver());
                    ContentValues values = new ContentValues();
                    Uri rawContactUri = Uri.parse("content://com.android.contacts/raw_contacts");
                    long rawContactId = ContentUris.parseId(getContentResolver().insert(rawContactUri, values));
                    values.clear();
                    values.put(ContactsContract.Contacts.Data.RAW_CONTACT_ID, rawContactId);
                    values.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
                    values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                            "Steve Chang");
                    //添加通讯录记录，新加记录的姓名是"Steve Chang"
                    myQueryHandler.startInsert(1, null, ContactsContract.Data.CONTENT_URI, values);


                    bt2.setText("AsyncQuery\nHandler(Update)");
                    bt2.setBackgroundColor(0xFFD7D7D7);
                    bt2.setTextColor(0xbd292f34);
                    bt1.setTextColor(0xFFA0A0A0);
                    bt1.setEnabled(false);
                }
                //修改数据
                else if (bt2.getText().equals("AsyncQuery\nHandler(Update)")) {
                    Uri uri = Uri.parse("content://com.android.contacts/data");
                    ContentValues values = new ContentValues();
                    values.put("data1", "Mercy Zhang");
                    String whereClause = "mimetype=? and data1=?";

                    //修改通讯录，将姓名"Steve Chang"改为"Mercy Zhang"，
                    myQueryHandler.startUpdate(2, null, uri, values, whereClause, new String[]{"vnd.android.cursor.item/name", "Steve Chang"});

                    bt2.setText("AsyncQuery\nHandler(Delete)");

                }
                //删除数据
                else if (bt2.getText().equals("AsyncQuery\nHandler(Delete)")) {

                    Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
                    String whereClause = "DISPLAY_NAME = ?";
                    //删除通讯录记录，将姓名为"Mercy Zhang"的记录删除
                    myQueryHandler.startDelete(4, null, uri, whereClause, new String[]{"Mercy Zhang"});

                    bt2.setText("AsyncQuery\nHandler(Insert)");
                    bt1.setBackgroundColor(0xbd292f34);
                    bt1.setTextColor(0xFFFFFFFF);
                    bt1.setEnabled(true);
                }

            }
        });
    }

    // LoaderCallbacks
    private class DataLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            Log.i(TAG, "onCreateLoader收到传入参数args: " + args.getString("Para1") + "和" + args.getInt("Para2"));
            // 当一个新的loader需被创建时调用，设置URI，URI指向的是系统通讯录
            // URI若是自定义的，就要提供相应的Provider和数据库的Query操作即可
            // Uri uri = ContactsContract.Contacts.CONTENT_URI;


            //创建并返回一个Loader，系统会启动这个Loader，执行其中loadInBackground方法
            //这里是系统自带的CursorLoader，其loadInBackground功能是执行ContentResolver.query方法，并返回查询结果
            return new CursorLoader(LoaderActivity.this, uri,
                    CONTACTS_SUMMARY_PROJECTION, null, null,
                    ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
            //如果用自定义Loader，则改为：return new MyLoader(Context)
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

            //查询返回结果以cursor形式提供，将其转换为所需要显示的数据，放入ArrayAdapter
            adapter.clear();
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (name != null)
                    adapter.add(name);
                else
                    Log.i(TAG, "Null Exception: Name Null");
            }

            //用准备好的数据的adapter刷新UI显示
            adapter.notifyDataSetChanged();
            tv.append("onLoadFinished,获取的数据总数为：" + adapter.getCount() + "\n");
            Log.i(TAG, "onLoadFinished,获取的数据总数为：" + adapter.getCount());

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            //释放相关资源,Adapter数据清空
            adapter.clear();
            tv.append("onLoaderReset,adapter数据总数为：" + adapter.getCount() + "\n");
            Log.i(TAG, "onLoaderReset,adapter数据总数为：" + adapter.getCount());
        }
    }

    //通过内部封装的Handler，借助ContentProvider来异步操作数据库，提供CRUD四种操作及其每种操作完成后的结果回调
    private class MyQueryHandler extends AsyncQueryHandler {
        public MyQueryHandler(ContentResolver contentResolver) {
            super(contentResolver);
        }

        //StartQuery完成时执行此方法
        @Override
        protected void onQueryComplete(int token, Object obj, Cursor cursor) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                adapter.add(name);
            }
            adapter.notifyDataSetChanged();
            Log.i(TAG, "onQueryComplete");
        }

        //StartInsert完成时执行此方法
        @Override
        protected void onInsertComplete(int token, Object cookie, Uri uri) {

            myQueryHandler.startQuery(0, null, uri, CONTACTS_SUMMARY_PROJECTION, null, null, null);
            Log.i(TAG, "onInsertComplete, 新增一条记录:Steve Chang.");
            tv.append("新增一条记录:Steve Chang.\n");

        }

        //StartUpdate完成时执行此方法
        @Override
        protected void onUpdateComplete(int token, Object cookie, int result) {

            myQueryHandler.startQuery(0, null, uri, CONTACTS_SUMMARY_PROJECTION, null, null, null);
            tv.append("修改：Steve Chang->Mercy Zhang.\n");
            Log.i(TAG, "onUpdateComplete,修改一条记录.");

        }

        //StartDelete完成时执行此方法
        @Override
        protected void onDeleteComplete(int token, Object cookie, int result) {
            if (token == 4) {
                Uri uri = Uri.parse("content://com.android.contacts/data");
                String whereClause = "mimetype=? and Data.DATA1 =?";
                myQueryHandler.startDelete(5, null, uri, whereClause, new String[]{"vnd.android.cursor.item/name", "Mercy Zhang"});
            } else if (token == 5)

            {
                myQueryHandler.startQuery(0, null, uri, CONTACTS_SUMMARY_PROJECTION, null, null, null);

                tv.append("删除:Mercy Zhang.\n");
                Log.i(TAG, "onDeleteComplete,删除一条记录.");
            }
        }
    }
}
