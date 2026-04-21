package com.baidu.idl.face.main.activity.start;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.baidu.idl.face.main.activity.BaseActivity;
import com.baidu.idl.face.main.activity.FaceSDKManager;
import com.baidu.idl.facesdkdemo.R;
import com.example.datalibrary.listener.SdkInitListener;
import com.example.datalibrary.threshold.SingleBaseConfig;
import com.example.datalibrary.utils.LogUtils;
import com.example.settinglibrary.utils.ConfigUtils;

import java.util.Timer;
import java.util.TimerTask;

public class StartActivity extends BaseActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mContext = this;
        boolean isConfigExit = ConfigUtils.isConfigExit(this);
        boolean isInitConfig = ConfigUtils.initConfig(getApplicationContext());
        if (isConfigExit && isInitConfig) {
            Toast.makeText(StartActivity.this, "初始配置加载成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(StartActivity.this, "初始配置失败,将重置文件内容为默认配置", Toast.LENGTH_SHORT).show();
//            ConfigUtils.modityJson();
        }
        LogUtils.setIsDebug(SingleBaseConfig.getBaseConfig().isLog());
        initLicense();
    }

    private void initLicense() {
        FaceSDKManager.getInstance().init(mContext, new SdkInitListener() {
            @Override
            public void initStart() {

            }

            public void initLicenseSuccess() {

                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        /**
                         *要执行的操作
                         */
                        startActivity(new Intent(mContext, HomeActivity.class));
                        finish();
                    }
                };
                Timer timer = new Timer();
                timer.schedule(task, 2000);
            }

            @Override
            public void initLicenseFail(int errorCode, String msg) {
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        /**
                         *要执行的操作
                         */
                        startActivity(new Intent(mContext, ActivitionActivity.class));
                        finish();
                    }
                };
                Timer timer = new Timer();
                timer.schedule(task, 2000);
            }

            @Override
            public void initModelSuccess() {
            }

            @Override
            public void initModelFail(int errorCode, String msg) {

            }
        });
    }
}
