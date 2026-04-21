package com.baidu.idl.main.facesdk.activity.gate;

import android.content.Intent;
import android.graphics.Color;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.viewpager.widget.ViewPager;

import com.baidu.idl.main.facesdk.fragment.AttendanceFragment;
import com.baidu.idl.main.facesdk.fragment.DevelopFragment;
import com.baidu.idl.main.facesdk.fragment.GateFragment;
import com.baidu.idl.main.facesdk.fragment.PaymentFragment;
import com.baidu.idl.main.facesdk.gatelibrary.R;
import com.example.datalibrary.fragment.ActivitionPagerAdapter;
import com.example.datalibrary.fragment.BaseFragment;
import com.example.datalibrary.manager.FaceSDKManager;
import com.example.datalibrary.model.LivenessModel;

import java.util.ArrayList;
import java.util.List;

public abstract class FacePageActivity extends FaceUiActivity implements View.OnClickListener ,
        ViewPager.OnPageChangeListener , BaseFragment.BaseFragmentListener {

    private TextView preText;
    private TextView deveLop;
    private View preView;
    private View developView;
    private ViewPager viewPager;
    private ArrayList<BaseFragment> fragementList;
    protected DevelopFragment developFragment;
    protected BaseFragment preFragment;
    private boolean isPre = true;
    private boolean isGarden;
    @Override
    protected void initView() {
        super.initView();
        Intent intent = getIntent();
        int state = intent.getIntExtra("state" , 0 );
        isGarden = intent.getBooleanExtra("isGarden" , false);
        // 返回
        ImageView mButReturn = findViewById(R.id.btn_back);
        mButReturn.setOnClickListener(this);
        // 预览模式
        preText = findViewById(R.id.preview_text);
        preText.setOnClickListener(this);
        preText.setTextColor(Color.parseColor("#ffffff"));
        preView = findViewById(R.id.preview_view);
        // 开发模式
        deveLop = findViewById(R.id.develop_text);
        deveLop.setOnClickListener(this);
        developView = findViewById(R.id.develop_view);
        developView.setVisibility(View.GONE);
        // ui
        viewPager = findViewById(R.id.pager);

        fragementList = new ArrayList<BaseFragment>();
        if (state == 0){
            fragementList.add(preFragment = new GateFragment());
        }else if (state == 1){
            fragementList.add(preFragment = new AttendanceFragment());
        }else {
            fragementList.add(preFragment = new PaymentFragment());
            glMantleSurfacView.setDraw(true);
        }
        fragementList.add(developFragment = new DevelopFragment());
        developFragment.setBaseFragmentListener(this);
        viewPager.setAdapter(new ActivitionPagerAdapter(getSupportFragmentManager() , fragementList));

        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(this);
    }

    protected void upLoad(List<LivenessModel> models){
        lastModels = models;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isPre){
                    if (isGarden){
                        glMantleSurfacView.setDraw(true);
                    }
                    preFragment.upLoad(models);
                }else {
                    developFragment.upLoad(models);
                    glMantleSurfacView.setDraw(false);
                }
            }
        });
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0){
            toPre();
            isPre = true;
        }else {
            toDevelop();
            isPre = false;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId(); // 返回
        if (id == R.id.btn_back) {
            if (!FaceSDKManager.initModelSuccess) {
                Toast.makeText(this, getResources().getString(R.string.toast_sdk_loading_models),
                        Toast.LENGTH_LONG).show();
                return;
            }
            finish();
        }else if (id == R.id.preview_text) {

            viewPager.setCurrentItem(0);
        } else if (id == R.id.develop_text) {
            viewPager.setCurrentItem(1);
        }
    }

    @Override
    public void onOpenNirCamera(TextureView nirView) {
        openNirCamera(nirView);
    }

    private void toPre(){

        deveLop.setTextColor(Color.parseColor("#a9a9a9"));
        preText.setTextColor(Color.parseColor("#ffffff"));
        preView.setVisibility(View.VISIBLE);
        developView.setVisibility(View.GONE);
    }
    private void toDevelop(){

        deveLop.setTextColor(Color.parseColor("#ffffff"));
        preText.setTextColor(Color.parseColor("#a9a9a9"));
        preView.setVisibility(View.GONE);
        developView.setVisibility(View.VISIBLE);
    }
}
