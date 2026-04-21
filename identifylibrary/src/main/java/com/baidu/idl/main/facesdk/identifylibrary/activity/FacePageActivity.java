package com.baidu.idl.main.facesdk.identifylibrary.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.viewpager.widget.ViewPager;

import com.baidu.idl.main.facesdk.identifylibrary.R;
import com.baidu.idl.main.facesdk.identifylibrary.fragment.DevelopFragment;
import com.baidu.idl.main.facesdk.identifylibrary.fragment.IdentifyFragment;
import com.example.datalibrary.fragment.ActivitionPagerAdapter;
import com.example.datalibrary.fragment.BaseFragment;
import com.example.datalibrary.manager.FaceSDKManager;
import com.example.datalibrary.model.LivenessModel;
import com.example.datalibrary.utils.DensityUtils;

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
    protected IdentifyFragment preFragment;
    private ImageView dynamicsBut;
    private boolean isPre = true;
    private boolean isGarden;
    private boolean isCheck = false;
    private PopupWindow mPopupMenu;
    private TextView videoText;
    private TextView pictureText;
    protected boolean isVideo = true;
    private View imageVideo;
    private View imagePicture;
    @Override
    protected void initView() {
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
        fragementList.add(preFragment = new IdentifyFragment());
        fragementList.add(developFragment = new DevelopFragment());
        developFragment.setBaseFragmentListener(this);
//        developFragment.setBaseFragmentListener(this);
        viewPager.setAdapter(new ActivitionPagerAdapter(getSupportFragmentManager() , fragementList));

        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(this);
        dynamicsBut = findViewById(R.id.dynamics_but);
        dynamicsBut.setOnClickListener(this);
        initUserManagePopupWindow();
        super.initView();
    }

    protected void upLoad(List<LivenessModel> models){
        lastModels = models;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                preFragment.upLoad(models);
                developFragment.upLoad(models);
            }
        });
    }
    protected void upLoadBitmap(Bitmap bitmap){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                preFragment.upLoadBitmap(bitmap);
                developFragment.upLoadBitmap(bitmap);
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
                Toast.makeText(this, getResources().getString(R.string.toast_sdk_loading),
                        Toast.LENGTH_LONG).show();
                return;
            }
            finish();
        }else if (id == R.id.preview_text) {

            viewPager.setCurrentItem(0);
        } else if (id == R.id.develop_text) {
            viewPager.setCurrentItem(1);
        }else if (id == R.id.dynamics_but){
            if (!isCheck){
                isCheck = true;
                dynamicsBut.setImageResource(R.mipmap.dynamics_false);
                if (mPopupMenu != null) {
                    int marginRight = DensityUtils.dip2px(FacePageActivity.this, 20);
                    int marginTop = DensityUtils.dip2px(FacePageActivity.this, 56);
                    mPopupMenu.showAtLocation(dynamicsBut, Gravity.END | Gravity.TOP,
                            marginRight, marginTop);
                }
            }else {
                dismissPopupWindow();
            }
        }else if (id == R.id.video_manager){
            if (!isVideo){
                isVideo = true;
                glMantleSurfacView.setVisibility(View.VISIBLE);
                preFragment.setVideoOrPicture(isVideo);
                developFragment.setVideoOrPicture(isVideo);
                videoText.setTextColor(Color.parseColor("#00BFFF"));
                pictureText.setTextColor(Color.parseColor("#FFFFFF"));
                imageVideo.setVisibility(View.VISIBLE);
                imagePicture.setVisibility(View.INVISIBLE);
            }
            dismissPopupWindow();
        }else if (id == R.id.picture_manager){
            if (isVideo){
                isVideo = false;
                glMantleSurfacView.setVisibility(View.GONE);
                preFragment.setVideoOrPicture(isVideo);
                developFragment.setVideoOrPicture(isVideo);
                pictureText.setTextColor(Color.parseColor("#00BFFF"));
                videoText.setTextColor(Color.parseColor("#FFFFFF"));
                imagePicture.setVisibility(View.VISIBLE);
                imageVideo.setVisibility(View.INVISIBLE);
            }
            dismissPopupWindow();
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

    /**
     * 初始化用户管理PopupWindow
     */
    @SuppressLint("MissingInflatedId")
    private void initUserManagePopupWindow() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.popup_dynamics, null);
        mPopupMenu = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupMenu.setFocusable(true);
        mPopupMenu.setOutsideTouchable(true);
        mPopupMenu.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_round));
        mPopupMenu.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                isCheck = false;
                dynamicsBut.setImageResource(R.mipmap.dynamics_true);
            }
        });
        contentView.findViewById(R.id.video_manager).setOnClickListener(this);
        contentView.findViewById(R.id.picture_manager).setOnClickListener(this);
        videoText = contentView.findViewById(R.id.video_text);
        pictureText = contentView.findViewById(R.id.picture_text);
        imageVideo = contentView.findViewById(R.id.image_video);
        imagePicture = contentView.findViewById(R.id.image_picture);
        mPopupMenu.setContentView(contentView);
    }
    private void dismissPopupWindow() {
        if (mPopupMenu != null) {
            mPopupMenu.dismiss();
        }
    }
    protected void upLoadPicture(Bitmap bitmap){
        preFragment.setBitmap(bitmap);
        developFragment.setBitmap(bitmap);
    }
}
