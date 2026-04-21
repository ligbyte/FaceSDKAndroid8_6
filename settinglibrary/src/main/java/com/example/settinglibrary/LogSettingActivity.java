package com.example.settinglibrary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import com.example.datalibrary.activity.BaseActivity;
import com.example.datalibrary.manager.FaceSDKManager;
import com.example.datalibrary.utils.LogUtils;
import com.example.datalibrary.utils.PWTextUtils;

public class LogSettingActivity extends BaseActivity {
    private Switch swLog;
    private Button tipsLog;
    private View groupLog;
    private TextView tvLog;
    private View groupFunLog;
    private String msgTag = "";
    private int showWidth;
    private int showXLocation;
    private boolean isLog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_setting);
        init();
        initListener();
    }
    private void init(){
        Intent intent = getIntent();
        isLog = intent.getBooleanExtra("isLog" , true);
        swLog = findViewById(R.id.sw_log);
        swLog.setTrackResource(R.drawable.setting_switch_track_selector);
        // log开关
        tipsLog = findViewById(R.id.tips_log);
        tvLog = findViewById(R.id.tv_log);
        groupLog = findViewById(R.id.group_log);
        groupFunLog = findViewById(R.id.group_fun_log);
        if (isLog) {
            swLog.setChecked(true);
        } else {
            swLog.setChecked(false);
        }
    }
    private void initListener(){
        tipsLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msgTag.equals(getString(R.string.cw_log))) {
                    msgTag = "";
                    return;
                }
                msgTag = getString(R.string.cw_log);
                tipsLog.setBackground(getDrawable(R.mipmap.icon_setting_question_hl));
                PWTextUtils.showDescribeText(groupFunLog, tvLog, LogSettingActivity.this,
                        getString(R.string.cw_log), showWidth, showXLocation);
            }
        });
        PWTextUtils.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDismiss() {
                tipsLog.setBackground(getDrawable(R.mipmap.icon_setting_question));
            }
        });

        findViewById(R.id.qc_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLog = false;
                if (swLog.isChecked()) {
                    isLog = true;
                }
                Intent intent = new Intent();
                intent.putExtra("isLog", isLog);
                setResult(Activity.RESULT_OK, intent);
                FaceSDKManager.getInstance().setActiveLog(isLog);
                LogUtils.setIsDebug(isLog);
                finish();
            }
        });
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        showWidth = groupFunLog.getWidth();
        showXLocation = (int) groupLog.getX();
    }
}
