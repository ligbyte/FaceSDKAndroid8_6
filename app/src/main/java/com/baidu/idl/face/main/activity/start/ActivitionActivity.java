package com.baidu.idl.face.main.activity.start;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.baidu.idl.face.main.activity.BaseActivity;
import com.example.datalibrary.fragment.BaseFragment;
import com.baidu.idl.face.main.fragment.BatchFragment;
import com.baidu.idl.face.main.fragment.ChipFragment;
import com.example.datalibrary.fragment.ActivitionPagerAdapter;
import com.baidu.idl.face.main.fragment.OfflineFragment;
import com.baidu.idl.face.main.fragment.OnlineFragment;
import com.baidu.idl.facesdkdemo.R;

import java.util.ArrayList;

public class ActivitionActivity extends BaseActivity implements View.OnClickListener {

    public static Context mContext;
    private TextView accreditOffTv;
//    private ImageView accredit_offView;
    private TextView accreditOnTv;
//    private ImageView accredit_onView;
    private TextView accreditUseTv;
    private TextView chipUseTv;
//    private ImageView accredit_useView;
    RelativeLayout rel;
    private ViewPager viewPager;
    private ArrayList<BaseFragment> fragementList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activition);
        mContext = this;
        initView();
    }


    private void initView() {
        viewPager = findViewById(R.id.pager);
        // 离线激活
        accreditOffTv = findViewById(R.id.accredit_offTv);
        accreditOffTv.setOnClickListener(this);
//        accredit_offView = findViewById(R.id.accredit_offView);

        // 在线激活
        accreditOnTv = findViewById(R.id.accredit_onTv);
        accreditOnTv.setOnClickListener(this);
        // 应用激活
        accreditUseTv = findViewById(R.id.accredit_useTv);
        accreditUseTv.setOnClickListener(this);
        // 批量激活
        chipUseTv = findViewById(R.id.chip_use_tv);
        chipUseTv.setOnClickListener(this);

        rel = findViewById(R.id.parentView);
        fragementList = new ArrayList<BaseFragment>();
        fragementList.add(new OfflineFragment());
        fragementList.add(new OnlineFragment());
        fragementList.add(new BatchFragment());
        fragementList.add(new ChipFragment());

        viewPager.setAdapter(new ActivitionPagerAdapter(getSupportFragmentManager() , fragementList));

        viewPager.setOffscreenPageLimit(4);

    }
    private void dismissWindow(){
        if (fragementList != null){
            for (int i = 0 , k = fragementList.size(); i < k;i++){
                fragementList.get(i).dismissWindow();
            }
        }
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 离线激活
            case R.id.accredit_offTv:
                accreditOffTv.setTextColor(getResources().getColor(R.color.white));
                accreditOnTv.setTextColor(Color.parseColor("#808080"));
                accreditUseTv.setTextColor(Color.parseColor("#808080"));
                chipUseTv.setTextColor(Color.parseColor("#808080"));
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                viewPager.setCurrentItem(0);
                dismissWindow();
                break;
            // 在线激活
            case R.id.accredit_onTv:
                accreditOffTv.setTextColor(Color.parseColor("#808080"));
                accreditOnTv.setTextColor(getResources().getColor(R.color.white));
                accreditUseTv.setTextColor(Color.parseColor("#808080"));
                chipUseTv.setTextColor(Color.parseColor("#808080"));
                viewPager.setCurrentItem(1);
                dismissWindow();
                break;
            // 应用激活
            case R.id.accredit_useTv:
                accreditOffTv.setTextColor(Color.parseColor("#808080"));
                accreditOnTv.setTextColor(Color.parseColor("#808080"));
                accreditUseTv.setTextColor(getResources().getColor(R.color.white));
                chipUseTv.setTextColor(Color.parseColor("#808080"));
                InputMethodManager imm1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm1.showSoftInput(view, InputMethodManager.SHOW_FORCED);
                imm1.hideSoftInputFromWindow(view.getWindowToken(), 0);
                viewPager.setCurrentItem(2);
                dismissWindow();
                break;
            case R.id.chip_use_tv:
                accreditOffTv.setTextColor(Color.parseColor("#808080"));
                accreditOnTv.setTextColor(Color.parseColor("#808080"));
                accreditUseTv.setTextColor(Color.parseColor("#808080"));
                chipUseTv.setTextColor(getResources().getColor(R.color.white));
                viewPager.setCurrentItem(3);
                dismissWindow();
                break;

        }
    }


    /**
     * 点击空白区域隐藏键盘.
     *
     * @param event the event
     * @return true, if successful
     */
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            if (ActivitionTwoctivity.this.getCurrentFocus() != null) {
//                if (ActivitionTwoctivity.this.getCurrentFocus().getWindowToken() != null) {
//                    imm.hideSoftInputFromWindow(ActivitionTwoctivity.this.getCurrentFocus()
//                            .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//                }
//            }
//        }
//        return super.onTouchEvent(event);
//    }

/*
    // 转大写
    */
}
