package com.example.settinglibrary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;

import com.example.datalibrary.activity.BaseActivity;
import com.example.datalibrary.utils.PWTextUtils;


/**
 * 镜像调节页面
 * Created by v_liujialu01 on 2019/6/17.
 */

public class MirrorSettingActivity extends BaseActivity implements View.OnClickListener {
    private Switch mSwitchMirrorRgb;
    private Switch mSwitchMirrorNir;
    private Switch switchDetectFrame;
    private ImageView mButtonMirrorSave;
    private int zero = 0;
    private int one = 1;
    public static final int CANCLE = 404;

    private LinearLayout linerDetectMirror;
    private TextView tvDetectMirror;
    private Button cwDetectMirror;

    private LinearLayout linerCameraDisplayMirror;
    private TextView tvCameraDisplayMirror;
    private Button cwCameraDisplayMirror;
    private String msgTag = "";

    //    private LinearLayout linerBarMirror;
    private LinearLayout mirrorRepresent;
    private int showWidth;
    private int showXLocation;
    private boolean rgbRevert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gate_mirror_setting);

//        linerBarMirror = findViewById(R.id.linerbarmirror);
//        setBarColor();
//        setLightStatusBarColor(this);
//        setBarLayout(linerBarMirror);

        initView();
        initData();
    }

    private void initView() {
        Intent intent = getIntent();
        rgbRevert = intent.getBooleanExtra("rgbRevert" , false);
        mirrorRepresent = findViewById(R.id.mirrorRepresent);

        mSwitchMirrorRgb = findViewById(R.id.switch_mirror_rgb);
        mSwitchMirrorNir = findViewById(R.id.switch_mirror_nir);
        switchDetectFrame = findViewById(R.id.switch_detect_frame);
        mButtonMirrorSave = findViewById(R.id.button_mirror_save);

        linerDetectMirror = findViewById(R.id.linerdetectmirror);
        tvDetectMirror = findViewById(R.id.tvdetectmirror);
        cwDetectMirror = findViewById(R.id.cwdetectmirror);

        linerCameraDisplayMirror = findViewById(R.id.linercameradisplaymirror);
        tvCameraDisplayMirror = findViewById(R.id.tvcameradisplaymirror);
        cwCameraDisplayMirror = findViewById(R.id.cwcameradisplaymirror);

        mButtonMirrorSave.setOnClickListener(this);

        PWTextUtils.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDismiss() {
                cwDetectMirror.setBackground(getDrawable(R.mipmap.icon_setting_question));
                cwCameraDisplayMirror.setBackground(getDrawable(R.mipmap.icon_setting_question));
            }
        });

        cwDetectMirror.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                if (msgTag.equals(getString(R.string.cw_detectframe))) {
                    msgTag = "";
                    return;
                }
                msgTag = getString(R.string.cw_detectframe);
                cwDetectMirror.setBackground(getDrawable(R.mipmap.icon_setting_question_hl));
                PWTextUtils.showDescribeText(linerDetectMirror, tvDetectMirror, MirrorSettingActivity.this,
                        getString(R.string.cw_detectframe), showWidth, showXLocation);
            }
        });

        cwCameraDisplayMirror.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                if (msgTag.equals(getString(R.string.cw_cameradisplay))) {
                    msgTag = "";
                    return;
                }
                msgTag = getString(R.string.cw_cameradisplay);
                cwCameraDisplayMirror.setBackground(getDrawable(R.mipmap.icon_setting_question_hl));
                PWTextUtils.showDescribeText(linerCameraDisplayMirror,
                        tvCameraDisplayMirror, MirrorSettingActivity.this,
                        getString(R.string.cw_cameradisplay), showWidth, showXLocation);
            }
        });

    }

    private void initData() {

        if (rgbRevert) {
            switchDetectFrame.setChecked(true);
        } else {
            switchDetectFrame.setChecked(false);
        }

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.button_mirror_save) {
            if (switchDetectFrame.isChecked()) {
                rgbRevert = true;
            } else {
                rgbRevert = false;
            }
            finish();
        }
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("rgbRevert", rgbRevert);
        // 设置返回码和返回携带的数据
        setResult(Activity.RESULT_OK, intent);
        super.finish();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        showWidth = mirrorRepresent.getWidth();
        showXLocation = (int) mirrorRepresent.getX();
    }
}
