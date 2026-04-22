package com.example.settinglibrary;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.idl.main.facesdk.FaceAuth;
import com.baidu.idl.main.facesdk.license.BDFaceLicenseAuthInfo;
import com.example.datalibrary.activity.BaseActivity;
import com.example.datalibrary.manager.FaceSDKManager;
import com.example.datalibrary.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;


public class VersionMessageActivity extends BaseActivity {
    private TextView sdkVersion;
    private TextView systemVersion;
    private TextView activateStatus;
    private TextView activateType;
    private TextView activateData;
    private ImageView buttonVersionSave;
    private FaceAuth faceAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_versionmsg);

        init();
    }

    private void showAuthText(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        BDFaceLicenseAuthInfo bdFaceLicenseAuthInfo = faceAuth.getAuthInfo(this);
        long expireTime = bdFaceLicenseAuthInfo.expireTime * 1000L;
        long curTime = System.currentTimeMillis();
        long gap = expireTime - curTime;
        boolean isForever = false;
        long tenYeasTime = 315360000;
        // 假设大于10年，显示永久
        if (gap / 1000 > tenYeasTime){
            isForever = true;
        }
        if (isForever){
            activateData.setText(getString(R.string.auth_for_ever));
        }
        else {
            Date dateLong = new Date(bdFaceLicenseAuthInfo.expireTime * 1000L);
            String dateTime = simpleDateFormat.format(dateLong);
            activateData.setText(dateTime);
        }
    }

    public void init() {
        faceAuth = new FaceAuth();
        buttonVersionSave = findViewById(R.id.button_version_save);
        sdkVersion = findViewById(R.id.sdkversion);
        systemVersion = findViewById(R.id.systemversion);
        activateStatus = findViewById(R.id.activatestatus);
        activateType = findViewById(R.id.activatetype);
        activateData = findViewById(R.id.activatedata);

        sdkVersion.setText(Utils.getVersionName(this));
        systemVersion.setText(android.os.Build.VERSION.RELEASE);
        if (FaceSDKManager.initStatus != FaceSDKManager.SDK_MODEL_LOAD_SUCCESS) {
            activateStatus.setText(getResources().getString(R.string.status_not_activated));
        } else {
            activateStatus.setText(getResources().getString(R.string.status_activated));
        }

        showAuthText();
        buttonVersionSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
