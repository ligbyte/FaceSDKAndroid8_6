package com.baidu.idl.main.facesdk.identifylibrary.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.baidu.idl.main.facesdk.FaceInfo;
import com.baidu.idl.main.facesdk.identifylibrary.R;
import com.baidu.idl.main.facesdk.identifylibrary.utils.FaceUtils;
import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon;
import com.example.datalibrary.callback.FaceDetectCallBack;
import com.example.datalibrary.manager.FaceSDKManager;
import com.example.datalibrary.manager.SaveImageManager;
import com.example.datalibrary.model.BDFaceCheckConfig;
import com.example.datalibrary.model.BDFaceImageConfig;
import com.example.datalibrary.model.BDLiveConfig;
import com.example.datalibrary.model.LivenessModel;
import com.example.datalibrary.utils.FaceOnDrawTexturViewUtil;
import com.example.datalibrary.utils.ImageUtils;
import com.example.datalibrary.utils.ToastUtils;
import com.example.datalibrary.view.BdFaceRectView;
import com.example.datalibrary.threshold.SingleBaseConfig;

import java.io.FileNotFoundException;
import java.util.List;

public class FaceOneFoOneActivity extends FacePageActivity implements View.OnClickListener {


    private BDFaceImageConfig bdFaceImageConfig;
    private BDFaceImageConfig bdNirFaceImageConfig;
    private BDFaceCheckConfig bdFaceCheckConfig;
    private BDLiveConfig bdLiveConfig;
    private byte[] pictureFeature;
    public static final int PICK_PHOTO_FRIST = 100;
    public static final int PICK_PHOTO_ADD = 101;
    private Bitmap rgbBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FaceSDKManager.getInstance().initDataBases(this);
        initFaceCheck();
    }

    // 初始化rgb数据
    @Override
    protected void initFaceConfig(int height, int width) {
        bdFaceImageConfig = new BDFaceImageConfig(height, width,
                SingleBaseConfig.getBaseConfig().getRgbDetectDirection(),
                SingleBaseConfig.getBaseConfig().getMirrorDetectRGB(),
                BDFaceSDKCommon.BDFaceImageType.BDFACE_IMAGE_TYPE_YUV_NV21);
    }

    // 初始化nir数据
    @Override
    void initNirFaceConfig(int height, int width) {
        bdNirFaceImageConfig = new BDFaceImageConfig(height, width,
                SingleBaseConfig.getBaseConfig().getNirDetectDirection(),
                SingleBaseConfig.getBaseConfig().getMirrorDetectNIR(),
                BDFaceSDKCommon.BDFaceImageType.BDFACE_IMAGE_TYPE_YUV_NV21);
    }

    // 相关配置参数
    private void initFaceCheck() {
        bdFaceCheckConfig = FaceUtils.getInstance().getBDFaceCheckConfig();
        bdLiveConfig = FaceUtils.getInstance().getBDLiveConfig();
    }

    @Override
    void onNirCameraData(byte[] data) {
        bdNirFaceImageConfig.setData(data);
    }

    @Override
    void onCameraData(byte[] data) {
        if (!isVideo) {
            return;
        }
        bdFaceImageConfig.setData(data);

        FaceSDKManager.getInstance().onDetectCheck(bdFaceImageConfig, bdNirFaceImageConfig, null,
                bdFaceCheckConfig, new FaceDetectCallBack() {
                    @Override
                    public void onFaceDetectCallback(final List<LivenessModel> models) {
                        if (isVideo) {
                            upLoad(models);
                            if (developFragment.isSaveImage) {
                                SaveImageManager.getInstance().saveImage(models, bdLiveConfig);
                            }
                        }
                    }

                    @Override
                    public void onTip(int code, final String msg) {

                    }

                    @Override
                    public void onFaceDetectDarwCallback(List<LivenessModel> models) {
                        // 人脸框显示
                        showFrame(models);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null || data.getData() == null) {
            return;
        }
        if (requestCode == PICK_PHOTO_FRIST) {
            Uri uri1 = ImageUtils.geturi(data, this);
            try {
                final Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri1));
                if (bitmap == null) {
                    bdFaceCheckConfig.setSecondFeature(null);
                    return;
                }
                byte[] secondFeature = new byte[512];
                // 提取特征值
                float ret = FaceSDKManager.getInstance().personDetect(bitmap, secondFeature,
                        FaceUtils.getInstance().getBDFaceCheckConfig(), this);

                // 判断质量检测，针对模糊度、遮挡、角度
                if (ret == 128) {
                    bdFaceCheckConfig.setSecondFeature(secondFeature);
                    ToastUtils.toast(this, getResources().getString(R.string.toast_feature_extraction_success));
                    upLoadBitmap(bitmap);
                    if (!isVideo && rgbBitmap != null) {
                        onBitmapDetect();
                    }
                } else {
                    bdFaceCheckConfig.setSecondFeature(null);
                    if (!bitmap.isRecycled()) {
                        bitmap.recycle();
                    }
                    upLoadBitmap(null);
                    ToastUtils.toast(this, getResources().getString(R.string.toast_feature_extraction_fail));
                }
            } catch (FileNotFoundException e) {
                bdFaceCheckConfig.setSecondFeature(null);
            }
        } else if (requestCode == PICK_PHOTO_ADD) {

            Uri uri1 = ImageUtils.geturi(data, this);
            try {
                rgbBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri1));
                if (rgbBitmap == null) {
                    pictureFeature = null;
                    upLoadPicture(null);
                    return;
                }
                upLoadPicture(rgbBitmap);
                if (bdFaceCheckConfig.secondFeature != null) {
                    onBitmapDetect();
                }
            } catch (FileNotFoundException e) {
                upLoadPicture(null);
                rgbBitmap = null;
                pictureFeature = null;
            }
        }
    }

    private void onBitmapDetect() {
        FaceSDKManager.getInstance().onLiveCheck(new BDFaceImageConfig(rgbBitmap), null , null ,
                bdFaceCheckConfig, new FaceDetectCallBack() {
                    @Override
                    public void onFaceDetectCallback(final List<LivenessModel> models) {
                        if (!isVideo) {
                            drawFace(models);
                            upLoad(models);

                        }
                    }

                    @Override
                    public void onTip(int code, final String msg) {

                    }

                    @Override
                    public void onFaceDetectDarwCallback(List<LivenessModel> models) {
                    }
                });
    }
    private void drawFace(List<LivenessModel> models){
        // 更新图片人脸框
        if (models == null || models.size() == 0) {
            return;
        }

        if (rgbBitmap == null || rgbBitmap.isRecycled()) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                for (LivenessModel model : models) {
                    FaceInfo faceInfo = model.getFaceInfo();
                    float wd = (float) rgbBitmap.getWidth() / glMantleSurfacView.previewWidth;
                    float hd = (float) rgbBitmap.getHeight() / glMantleSurfacView.previewHeight;
                    Bitmap bitmap = Bitmap.createBitmap(rgbBitmap.getWidth(), rgbBitmap.getHeight(), Bitmap.Config.ARGB_8888);

                    // 创建一个Canvas，并将其与Bitmap关联
                    Canvas canvas = new Canvas(bitmap);
                    // 设置绘制颜色
                    Paint paint = new Paint();
                    canvas.drawBitmap(rgbBitmap, 0, 0, paint);
                    if (model.getScore() > SingleBaseConfig.getBaseConfig().getIdThreshold()) {
                        paint.setColor(Color.parseColor("#00baf2"));
                    } else {
                        paint.setColor(Color.parseColor("#FECD33"));
                    }

                    FaceOnDrawTexturViewUtil.drawFace(getApplicationContext() , paint, new RectF(faceInfo.centerX - faceInfo.width / 2,
                                    faceInfo.centerY - faceInfo.height / 2,
                                    faceInfo.centerX + faceInfo.width / 2,
                                    faceInfo.centerY + faceInfo.height / 2), canvas, BdFaceRectView.LINE_WIDTH * wd,
                            BdFaceRectView.STROKE_WIDTH * hd, BdFaceRectView.FACIAL_AREA);

                    upLoadPicture(bitmap);
                }
            }
        });

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}