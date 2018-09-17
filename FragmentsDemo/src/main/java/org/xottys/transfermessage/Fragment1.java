/**
 * 本例演示了Fragment与Fragment之间消息传递方式之3)
 * 方式1）myFragment.setArguments(bundle)-->getArguments().getString("PARA");
 * 方式2）MyFragment.newInstance("para")-->getArguments().getString("PARA");
 * 方式3) mListener.onFragmentInteraction("para")-->fragment.updateInfo("para")
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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class Fragment1 extends Fragment {
    public static final int REQUEST_CODE = 0x001;
    private static final String TAG = "FragmentMessage";
    //需传递的信息字符串存储位置
    static private String info;

    //回传消息给Activity的接口
    private OnFragmentInteractionListener mListener;

    public Fragment1() {
    }

    //可传递消息的静态构造方法
    public static Fragment1 newInstance(String param) {
        Fragment1 fragment = new Fragment1();
        Bundle args = new Bundle();
        args.putString("PARA", param);
        fragment.setArguments(args);
        return fragment;
    }

    //去往Activity的消息传递接口
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String para);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            //设置接口为绑定的Activity
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //从Bundle中获取传入的消息
        if (getArguments() != null) {
            info = getArguments().getString("PARA");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle data) {
        View view = inflater.inflate(R.layout.fragment_blank, container, false);

        //这里定义Fragment要显示的内容
        TextView tv = view.findViewById(R.id.tv);
        tv.setText("Fragment1");
        tv.setTextColor(Color.BLACK);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(20);

        Button ToActivityBT = view.findViewById(R.id.bt1);
        Button ToFragment2BT = view.findViewById(R.id.bt2);
        ToFragment2BT.setText("To Fragment2");
        ((MainActivity) getActivity()).setFrameBackgroud(1);
        //返回Actvity时用接口传递消息
        ToActivityBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //从Activity跳转过来时
                if (info.contains("Activity"))
                    getFragmentManager().popBackStack();
                //从Fragment跳转过来时
                else
                    getFragmentManager().popBackStack(null, 1);

                //用接口传递消息给Activity
                mListener.onFragmentInteraction("From Fragment1A");

            }
        });

        //跳转fragment2时用接口过渡传递消息
        ToFragment2BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //用接口传递消息给Activity
                mListener.onFragmentInteraction("From Fragment1F");

            }
        });

        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //显示从Activity传来的消息
        if (info.contains("Activity"))
            ((MainActivity) getActivity()).tv.setText("Activity传来的消息：" + info + "\n");
        //显示从Fragment传来的消息
        else
            ((MainActivity) getActivity()).tv.append("Fragment传来的消息：" + info + "\n");
    }

    //接收Fragment回传的消息
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (intent != null) {
            info = intent.getStringExtra("PARA");
            ((MainActivity) getActivity()).tv.append("Fragment传来的消息：" + info + "\n");
        }
    }
}
