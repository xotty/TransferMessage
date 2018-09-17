/**
 * 本例演示了Activity与Fragment之间消息传递方式
 * 一、Activity-->Fragment
 * 方式1）myFragment.setArguments(bundle)-->getArguments().getString("PARA");
 * 方式2）MyFragment.newInstance("para")-->getArguments().getString("PARA");
 * 方式3）setInfo("para")-->getInfo()
 * 方式4）fragment.updateInfo("para")
 * 二、Fragment-->Activity
 * 方式1）mListener.onFragmentInteraction("para")-->onFragmentInteraction("para")
 * 方式2）((mActivity)getActivity()).setPara("para")
 * <p>
 * <br/>Copyright (C), 2017-2018, Steve Chang
 * <br/>This program is protected by copyright laws.
 * <br/>Program Name:FragmentsDemo
 * <br/>Date:Sept，2018
 *
 * @author xottys@163.com
 * @version 1.0
 */
package org.xottys.transfermessage;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements Fragment1.OnFragmentInteractionListener {
    public static final int REQUEST_CODE = 0x001;
    private static final String TAG = "FragmentMessage";
    public TextView tv;
    private Fragment1 fragment1;
    private Fragment2 fragment2;
    private FrameLayout fm;
    //需传递的信息字符串存储位置
    private String info;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setFrameBackgroud(int i) {
        if (i == 1)
            fm.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
        else
            fm.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.atv);
        fm = findViewById(R.id.container);

        //通过setArguments/getArguments传递给Fragment1
        fragment1 = Fragment1.newInstance("From MainActivity by Arg");
        /*可选替代方法：
        final Fragment1 fragment1 = new Fragment1();
        Bundle bundle = new Bundle();
        bundle.putString("PARA", "From MainActivity");
        fragment1.setArguments(bundle);*/


        fragment2 = new Fragment2();


        Button toFragment1BT = findViewById(R.id.bt1);
        Button toFragment2BT = findViewById(R.id.bt2);

        //接收Fragment回传的消息
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {

                //从Fragment2返回且清空所有Fragment时显示结果
                if (getSupportFragmentManager().getBackStackEntryCount() == 0 && info.contains("Fragment2")) {
                    tv.append("Fragment传回的消息：" + info + "\n");
                    Log.i(TAG, "onBackStackChanged: " + info);
                //改变Fragment背景色
                } else if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                    if (getSupportFragmentManager().getBackStackEntryAt(0).getName().equals("frag1")) {
                        setFrameBackgroud(1);
                    } else if (getSupportFragmentManager().getBackStackEntryAt(0).getName().equals("frag2")) {
                        setFrameBackgroud(2);
                    }
                }

            }
        });

        //加载Fragment1
        toFragment1BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, fragment1)
                            .show(fragment1)
                            .addToBackStack("frag1")
                            .commit();
                }
            }
        });

        //加载Fragment2
        toFragment2BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {   //通过Activity的属性传递Fragment2
                    setInfo("From MainActivity by Att");
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, fragment2)
                            .show(fragment2)
                            .addToBackStack("frag2")
                            .commit();
                }
            }
        });
    }


    //接收Fragment1回传消息的接口实现
    @Override
    public void onFragmentInteraction(String infoFromFragment) {
        info = infoFromFragment;
        //直接回传给Activity
        if (info.contains("1A"))
            tv.append("Fragment传回的消息：" + info + "\n");
            //通过Activity中转传递给另一个Fragment
        else if (info.contains("1F")) {
            //将info传给fragment2
            fragment2.updateInfo(info);


            if (!fragment2.isAdded()) {
                //设置fragment1为其回传消息目标
                fragment2.setTargetFragment(fragment1, REQUEST_CODE);

                getSupportFragmentManager().beginTransaction()
                        .hide(fragment1)
                        .add(R.id.container, fragment2, "frag2")
                        .addToBackStack("frag2")
                        .commit();
            } else {
                getSupportFragmentManager().popBackStack();

            }
        }
    }
}

