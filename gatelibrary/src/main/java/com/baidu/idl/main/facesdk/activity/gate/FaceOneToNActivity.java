package com.baidu.idl.main.facesdk.activity.gate;

import android.os.Bundle;

import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon;
import com.baidu.idl.main.facesdk.utils.FaceUtils;
import com.example.datalibrary.callback.FaceDetectCallBack;
import com.example.datalibrary.manager.FaceSDKManager;
import com.example.datalibrary.manager.SaveImageManager;
import com.example.datalibrary.model.BDFaceCheckConfig;
import com.example.datalibrary.model.BDFaceImageConfig;
import com.example.datalibrary.model.BDLiveConfig;
import com.example.datalibrary.model.LivenessModel;
import com.example.datalibrary.threshold.SingleBaseConfig;

import java.util.List;


public class FaceOneToNActivity extends FacePageActivity {
    private BDFaceImageConfig bdFaceImageConfig;
    private BDFaceImageConfig bdNirFaceImageConfig;
    private BDFaceCheckConfig bdFaceCheckConfig;
    private BDLiveConfig bdLiveConfig;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FaceSDKManager.getInstance().initDataBases(this);
        initFaceCheck();
    }
    // 初始化rgb数据
    @Override
    protected void initFaceConfig(int height , int width){
        bdFaceImageConfig = new BDFaceImageConfig(height , width ,
                SingleBaseConfig.getBaseConfig().getRgbDetectDirection(),
                SingleBaseConfig.getBaseConfig().getMirrorDetectRGB() ,
                BDFaceSDKCommon.BDFaceImageType.BDFACE_IMAGE_TYPE_YUV_NV21);
    }
    // 初始化nir数据
    @Override
    void initNirFaceConfig(int height, int width) {
        bdNirFaceImageConfig = new BDFaceImageConfig(height , width ,
                SingleBaseConfig.getBaseConfig().getNirDetectDirection(),
                SingleBaseConfig.getBaseConfig().getMirrorDetectNIR() ,
                BDFaceSDKCommon.BDFaceImageType.BDFACE_IMAGE_TYPE_YUV_NV21);
    }

    // 相关配置参数
    private void initFaceCheck(){
        bdFaceCheckConfig = FaceUtils.getInstance().getBDFaceCheckConfig();
        bdLiveConfig = FaceUtils.getInstance().getBDLiveConfig();
    }

    @Override
    void onNirCameraData(byte[] data) {
        bdNirFaceImageConfig.setData(data);
    }

    @Override
    void onCameraData(byte[] data) {
        bdFaceImageConfig.setData(data);
        FaceSDKManager.getInstance().onDetectCheck(bdFaceImageConfig, bdNirFaceImageConfig, null,
                bdFaceCheckConfig, new FaceDetectCallBack() {
                    @Override
                    public void onFaceDetectCallback(List<LivenessModel> livenessModel) {
                        upLoad(livenessModel);
                        if (developFragment.isSaveImage) {
                            SaveImageManager.getInstance().saveImage(livenessModel , bdLiveConfig);
                        }
                    }

                    @Override
                    public void onTip(int code, String msg) {
                    }

                    @Override
                    public void onFaceDetectDarwCallback(List<LivenessModel> models) {
                        // 绘制人脸框
                        showFrame(models);

                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
