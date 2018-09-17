/**
 * 本例演示了Fragment与Fragment之间消息传递方式之1)和4)
 * 方式1）myFragment.setArguments(bundle)-->getArguments().getString("PARA");
 * 方式2）MyFragment.newInstance("para")-->getArguments().getString("PARA");
 * 方式3) mListener.onFragmentInteraction("para")-->fragment.updateData("para")
 * 方式4）fragment.setTargetFragment()/onActivityResult()---> getTargetFragment().onActivityResult()
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

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class Fragment2 extends Fragment {
    public static final int RESPONSE_CODE = 0x010;
    private static final String TAG = "FragmentMessage";
    private Fragment1 fragment1;

    //需传递的信息字符串存储位置
    private String info;

    //可对外使用的消息传递方法
    public void updateInfo(String para) {
        info = para;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //直接从Activity的属性传来的消息
        info = ((MainActivity) context).getInfo();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //从Activtiy直跳转来时为进一步跳转到fragment2作准备
        if (info.contains("Activity")) {
            fragment1 = new Fragment1();
            //为fragment1准备数据
            Bundle bundle = new Bundle();
            bundle.putString("PARA", "From Fragment2F");
            fragment1.setArguments(bundle);
           /*可选替代方法
           fragment1 = Fragment1.newInstance("From Fragment2");*/
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle data) {
        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        TextView tv = view.findViewById(R.id.tv);
        Button ToActivityBT = view.findViewById(R.id.bt1);
        Button ToFragment1BT = view.findViewById(R.id.bt2);

        ToFragment1BT.setText("To Fragment1");
        tv.setText("Fragment 2");
        tv.setTextColor(Color.BLACK);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(20);
        ((MainActivity) getActivity()).setFrameBackgroud(2);
        //返回MainActivity
        ToActivityBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置Activity 的回传消息
                ((MainActivity) getActivity()).setInfo("From Fragment2A");
                //清空fragment栈
                getFragmentManager().popBackStack(null, 1);
            }
        });

        //返回fragment1
        ToFragment1BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //从fragment1过来时用intent返回消息
                if (getTargetFragment() != null) {
                    Intent intent = new Intent();
                    intent.putExtra("PARA", "From Fragment2F");
                    getTargetFragment().onActivityResult(Fragment1.REQUEST_CODE, RESPONSE_CODE, intent);
                    getFragmentManager().popBackStack();
                }
                //从Activity过来时用setArguments传递消息
                else {
                    getFragmentManager().beginTransaction()
                            .hide(Fragment2.this)
                            .add(R.id.container, fragment1, "frag1")
                            .addToBackStack("frag1")
                            .commit();
                }
            }
        });
        return view;
    }


    //显示传递来的消息
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (info.contains("Fragment"))
            ((MainActivity) getActivity()).tv.append("Fragment传来的消息：" + info + "\n");
        else
            ((MainActivity) getActivity()).tv.setText("Activity传来的消息：" + info + "\n");
    }
}
