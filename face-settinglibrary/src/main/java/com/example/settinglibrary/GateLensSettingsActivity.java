package com.example.settinglibrary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.datalibrary.activity.BaseActivity;

public class GateLensSettingsActivity extends BaseActivity implements View.OnClickListener {

    private TextView configTxSettingQualtify;
    private ImageView qcSave;

    private boolean rgbRevert;
    int rgbDetectDirection;
    int mirrorDetectRGB;
    int nirDetectDirection;
    int mirrorDetectNIR;
    // 摄像头个数
    int rgbVideoDirection;
    int mirrorVideoRGB;
    int nirVideoDirection;
    int mirrorVideoNIR;
    private int rbgCameraId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gate_lens_settings);

        init();
    }

    private void init() {
        // 获取Intent对象
        Intent intent = getIntent();
        rgbRevert = intent.getBooleanExtra("rgbRevert" , false);
        rgbDetectDirection = intent.getIntExtra("rgbDetectDirection" , 0);
        mirrorDetectRGB = intent.getIntExtra("mirrorDetectRGB" , 0);
        nirDetectDirection = intent.getIntExtra("nirDetectDirection" , 0);
        mirrorDetectNIR = intent.getIntExtra("mirrorDetectNIR" , 0);
        rbgCameraId = intent.getIntExtra("rbgCameraId" , -1);
        // 获取传递的值
        rgbVideoDirection = intent.getIntExtra("rgbVideoDirection" , 0);
        mirrorVideoRGB = intent.getIntExtra("mirrorVideoRGB" , 0);
        nirVideoDirection = intent.getIntExtra("nirVideoDirection" , 0);
        mirrorVideoNIR = intent.getIntExtra("mirrorVideoNIR" , 0);
        // 人脸检测角度
        LinearLayout configFaceDetectAngle = findViewById(R.id.configFaceDetectAngle);
        configFaceDetectAngle.setOnClickListener(this);
        // 人脸回显角度
        LinearLayout configDisplayAngle = findViewById(R.id.configDisplayAngle);
        configDisplayAngle.setOnClickListener(this);
        // 镜像设置
        LinearLayout configMirror = findViewById(R.id.configMirror);
        configMirror.setOnClickListener(this);
        configTxSettingQualtify = findViewById(R.id.configTxSettingQualtify);


        qcSave = findViewById(R.id.qc_save);
        qcSave.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (rgbRevert) {
            configTxSettingQualtify.setText(getResources().getString(R.string.status_open));
        } else {
            configTxSettingQualtify.setText(getResources().getString(R.string.btn_close));
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.configFaceDetectAngle) {
            Intent intent = new Intent(this, FaceDetectAngleActivity.class);
            intent.putExtra("rgbDetectDirection" , rgbDetectDirection);
            intent.putExtra("mirrorDetectRGB" , mirrorDetectRGB);
            intent.putExtra("nirDetectDirection" , nirDetectDirection);
            intent.putExtra("mirrorDetectNIR" , mirrorDetectNIR);
            intent.putExtra("rgbVideoDirection" , rgbVideoDirection);
            intent.putExtra("mirrorVideoRGB" , mirrorVideoRGB);
            intent.putExtra("nirVideoDirection" , nirVideoDirection);
            intent.putExtra("mirrorVideoNIR" , mirrorVideoNIR);
            intent.putExtra("rbgCameraId" , rbgCameraId);
            startActivityForResult(intent , 100);
        } else if (id == R.id.configDisplayAngle) {
            Intent intent = new Intent(this, CameraDisplayAngleActivity.class);
            intent.putExtra("rgbVideoDirection" , rgbVideoDirection);
            intent.putExtra("mirrorVideoRGB" , mirrorVideoRGB);
            intent.putExtra("nirVideoDirection" , nirVideoDirection);
            intent.putExtra("mirrorVideoNIR" , mirrorVideoNIR);
            intent.putExtra("rbgCameraId" , rbgCameraId);
            startActivityForResult(intent , 101);
        } else if (id == R.id.configMirror) {
            Intent intent = new Intent(this, MirrorSettingActivity.class);
            intent.putExtra("rgbRevert" , rgbRevert);
            startActivityForResult(intent , 102);
        } else if (id == R.id.qc_save) {
            finish();
        }
    }

    @Override
    public void finish() {

        Intent intent = new Intent();
        intent.putExtra("rgbRevert", rgbRevert);
        intent.putExtra("rgbDetectDirection" , rgbDetectDirection);
        intent.putExtra("mirrorDetectRGB" , mirrorDetectRGB);
        intent.putExtra("nirDetectDirection" , nirDetectDirection);
        intent.putExtra("mirrorDetectNIR" , mirrorDetectNIR);
        intent.putExtra("rgbVideoDirection" , rgbVideoDirection);
        intent.putExtra("mirrorVideoRGB" , mirrorVideoRGB);
        intent.putExtra("nirVideoDirection" , nirVideoDirection);
        intent.putExtra("mirrorVideoNIR" , mirrorVideoNIR);
        intent.putExtra("rbgCameraId" , rbgCameraId);
        // 设置返回码和返回携带的数据
        setResult(Activity.RESULT_OK, intent);
        super.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode != Activity.RESULT_OK){
            return;
        }
        switch (requestCode) {
            case 100: // 返回的结果是来自于Activity B
                rgbDetectDirection = intent.getIntExtra("rgbDetectDirection" , 0);
                mirrorDetectRGB = intent.getIntExtra("mirrorDetectRGB" , 0);
                nirDetectDirection = intent.getIntExtra("nirDetectDirection" , 0);
                mirrorDetectNIR = intent.getIntExtra("mirrorDetectNIR" , 0);
                rbgCameraId = intent.getIntExtra("rbgCameraId" , -1);
                // 获取传递的值
                rgbVideoDirection = intent.getIntExtra("rgbVideoDirection" , 0);
                mirrorDetectRGB = intent.getIntExtra("mirrorDetectRGB" , 0);
                nirVideoDirection = intent.getIntExtra("nirVideoDirection" , 0);
                mirrorDetectNIR = intent.getIntExtra("mirrorDetectNIR" , 0);
                break;
            case 101: // 返回的结果是来自于Activity B
                rbgCameraId = intent.getIntExtra("rbgCameraId" , -1);
                // 获取传递的值
                rgbVideoDirection = intent.getIntExtra("rgbVideoDirection" , 0);
                mirrorVideoRGB = intent.getIntExtra("mirrorVideoRGB" , 0);
                nirVideoDirection = intent.getIntExtra("nirVideoDirection" , 0);
                mirrorVideoNIR = intent.getIntExtra("mirrorVideoNIR" , 0);
                break;
            case 102: // 返回的结果是来自于Activity B
                rgbRevert = intent.getBooleanExtra("rgbRevert" , false);
                break;
        }
    }
}