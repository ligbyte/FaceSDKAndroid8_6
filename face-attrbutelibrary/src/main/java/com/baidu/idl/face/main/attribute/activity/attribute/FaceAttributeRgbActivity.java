package com.baidu.idl.face.main.attribute.activity.attribute;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.idl.face.main.attribute.manager.AttributeFaceManager;
import com.baidu.idl.face.main.attribute.utils.FaceUtils;
import com.baidu.idl.main.facesdk.FaceInfo;
import com.baidu.idl.main.facesdk.attrbutelibrary.R;
import com.baidu.idl.main.facesdk.model.BDFaceGazeInfo;
import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon;
import com.example.datalibrary.activity.BaseActivity;
import com.example.datalibrary.callback.CameraDataCallback;
import com.example.datalibrary.callback.FaceDetectCallBack;
import com.example.datalibrary.gatecamera.CameraPreviewManager;
import com.example.datalibrary.gl.view.GlMantleSurfacView;
import com.example.datalibrary.manager.FaceSDKManager;
import com.example.datalibrary.model.BDFaceCheckConfig;
import com.example.datalibrary.model.BDFaceImageConfig;
import com.example.datalibrary.model.LivenessModel;
import com.example.datalibrary.utils.FaceOnDrawTexturViewUtil;
import com.example.datalibrary.threshold.SingleBaseConfig;

import java.util.List;

public class FaceAttributeRgbActivity extends BaseActivity {
    private GlMantleSurfacView glMantleSurfacView;
    /*RGB摄像头图像宽和高*/
    private static final int RGB_WIDTH = SingleBaseConfig.getBaseConfig().getRgbAndNirWidth();
    private static final int RGB_HEIGHT = SingleBaseConfig.getBaseConfig().getRgbAndNirHeight();
    private RelativeLayout showAtrMessage;
    private TextView atrDetectTime;
    private TextView atrToalTime;
    private TextView atrSex;
    private TextView atrAge;
    private TextView atrAccessory;
    private TextView atrEmotion;
    private TextView atrMask;
    private RelativeLayout atrRlDisplay;
    private RelativeLayout atrLinerTime;
    private TextView previewText;
    private TextView developText;
    private TextView homeBaiduTv;
    private ImageView previewView;
    private ImageView developView;
    private BDFaceImageConfig bdFaceImageConfig;
    private BDFaceCheckConfig bdFaceCheckConfig;
    private TextView leftEye;
    private TextView rightEye;

    private void initFaceCheck() {
        bdFaceCheckConfig = FaceUtils.getInstance().getBDFaceCheckConfig();
    }

    private void initFaceConfig(int height, int width) {
        bdFaceImageConfig = new BDFaceImageConfig(height, width,
                SingleBaseConfig.getBaseConfig().getRgbDetectDirection(),
                SingleBaseConfig.getBaseConfig().getMirrorDetectRGB(),
                BDFaceSDKCommon.BDFaceImageType.BDFACE_IMAGE_TYPE_YUV_NV21);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_rgb_attribute);
        initFaceCheck();
        SingleBaseConfig.getBaseConfig().setAttribute(true);
        init();
    }

    public void init() {
        homeBaiduTv = findViewById(R.id.home_baiduTv);
        previewView = findViewById(R.id.preview_view);
        developView = findViewById(R.id.develop_view);
        leftEye = findViewById(R.id.left_eye);
        rightEye = findViewById(R.id.right_eye);
        TextureView mDrawDetectFaceView = findViewById(R.id.draw_detect_face_view);
        showAtrMessage = findViewById(R.id.showAtrMessage);
        mDrawDetectFaceView.setOpaque(false);
        mDrawDetectFaceView.setKeepScreenOn(true);
        if (SingleBaseConfig.getBaseConfig().getRgbRevert()) {
            mDrawDetectFaceView.setRotationY(180);
        }
        ImageView btnBack = findViewById(R.id.btn_back);
        glMantleSurfacView = findViewById(R.id.camera_textureview);
        atrDetectTime = findViewById(R.id.atrDetectTime);
        atrToalTime = findViewById(R.id.atrToalTime);
        atrSex = findViewById(R.id.atrSex);
        atrAge = findViewById(R.id.atrAge);
        atrAccessory = findViewById(R.id.atrAccessory);
        atrEmotion = findViewById(R.id.atrEmotion);
        atrMask = findViewById(R.id.atrMask);
        atrRlDisplay = findViewById(R.id.atrRlDisplay);
        atrLinerTime = findViewById(R.id.atrLinerTime);
        previewText = findViewById(R.id.preview_text);
        developText = findViewById(R.id.develop_text);
        previewText.setTextColor(Color.parseColor("#FFFFFF"));

        previewText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeBaiduTv.setVisibility(View.VISIBLE);
                atrRlDisplay.setVisibility(View.GONE);
                atrLinerTime.setVisibility(View.GONE);
                previewText.setTextColor(Color.parseColor("#FFFFFF"));
                developText.setTextColor(Color.parseColor("#d3d3d3"));
                previewView.setVisibility(View.VISIBLE);
                developView.setVisibility(View.GONE);
            }
        });

        developText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeBaiduTv.setVisibility(View.GONE);
                atrRlDisplay.setVisibility(View.VISIBLE);
                atrLinerTime.setVisibility(View.VISIBLE);
                developText.setTextColor(Color.parseColor("#FFFFFF"));
                previewText.setTextColor(Color.parseColor("#d3d3d3"));
                previewView.setVisibility(View.GONE);
                developView.setVisibility(View.VISIBLE);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!FaceSDKManager.initModelSuccess) {
                    Toast.makeText(FaceAttributeRgbActivity.this, getResources().getString(R.string.toast_sdk_loading_models),
                            Toast.LENGTH_LONG).show();
                    return;
                }
                finish();
            }
        });

        glMantleSurfacView.initSurface(SingleBaseConfig.getBaseConfig().getRgbRevert(),
                SingleBaseConfig.getBaseConfig().getMirrorVideoRGB(), SingleBaseConfig.getBaseConfig().isOpenGl());
        CameraPreviewManager.getInstance().startPreview(glMantleSurfacView,
                SingleBaseConfig.getBaseConfig().getRgbVideoDirection(), RGB_WIDTH, RGB_HEIGHT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraPreview();
    }


    /**
     * 摄像头图像预览
     */
    private void startCameraPreview() {
        // 设置前置摄像头
        // CameraPreviewManager.getInstance().setCameraFacing(CameraPreviewManager.CAMERA_FACING_FRONT);
        // 设置后置摄像头
        // CameraPreviewManager.getInstance().setCameraFacing(CameraPreviewManager.CAMERA_FACING_BACK);
        // 设置USB摄像头
        if (SingleBaseConfig.getBaseConfig().getRBGCameraId() != -1) {
            CameraPreviewManager.getInstance().setCameraFacing(SingleBaseConfig.getBaseConfig().getRBGCameraId());
        } else {
            CameraPreviewManager.getInstance().setCameraFacing(0);
        }
        int[] cameraSize = CameraPreviewManager.getInstance().initCamera();
        initFaceConfig(cameraSize[1], cameraSize[0]);
        CameraPreviewManager.getInstance().setmCameraDataCallback(new CameraDataCallback() {
            @Override
            public void onGetCameraData(byte[] data, Camera camera, int width, int height) {
                bdFaceImageConfig.setData(data);
                dealRgb();
            }
        });
    }


    private void dealRgb() {
        AttributeFaceManager.getInstance().onDetectCheck(bdFaceImageConfig, bdFaceCheckConfig, new FaceDetectCallBack() {
            @Override
            public void onFaceDetectCallback(List<LivenessModel> models) {
                // 检测结果输出
                    showAtrDetailMessage(models/*.getFaceInfo(), livenessModel.getMaskScore(),
                            livenessModel.getSafetyHatScore()*/);
                showResult(models);
            }

            @Override
            public void onTip(int code, String msg) {

            }

            @Override
            public void onFaceDetectDarwCallback(List<LivenessModel> models) {
                showFrame(models);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CameraPreviewManager.getInstance().stopPreview();
    }


    /**
     * 绘制人脸框
     */
    private void showFrame(final List<LivenessModel> models) {
        if (models == null || models.size() == 0) {
            return;
        }
        LivenessModel model = models.get(0);
        glMantleSurfacView.onGlDraw(models , null , 0);

        FaceInfo faceInfo = model.getFaceInfo();
        if (faceInfo == null ){
            return;
        }
        RectF rectF = new RectF();

        rectF.set(FaceOnDrawTexturViewUtil.getFaceRectTwo(faceInfo));
        // 检测图片的坐标和显示的坐标不一样，需要转换。
        FaceOnDrawTexturViewUtil.mapFromOriginalRect(rectF,
                glMantleSurfacView, model.getBdFaceImageInstance());
        if (faceInfo.width > faceInfo.height) {
            if (!SingleBaseConfig.getBaseConfig().getRgbRevert()) {
                if (rectF.centerY() < glMantleSurfacView.getHeight() * 0.6) {
                    showAtrMessage.setTranslationX(rectF.centerX());
                    showAtrMessage.setTranslationY(rectF.centerY() + rectF.height() / 2);
                } else {
                    showAtrMessage.setTranslationX(rectF.centerX());
                    showAtrMessage.setTranslationY(rectF.centerY() -
                            rectF.width() - showAtrMessage.getHeight());
                }
            } else {
                if (rectF.centerY() < glMantleSurfacView.getHeight() * 0.6) {
                    showAtrMessage.setTranslationX(glMantleSurfacView.getWidth() - rectF.centerX());
                    showAtrMessage.setTranslationY(rectF.centerY() + rectF.width() / 2);
                } else {
                    showAtrMessage.setTranslationX(glMantleSurfacView.getWidth()
                            - rectF.centerX());
                    showAtrMessage.setTranslationY(rectF.centerY() -
                            rectF.width() - showAtrMessage.getHeight());
                }
            }
        } else {
            if (!SingleBaseConfig.getBaseConfig().getRgbRevert()) {
                if (rectF.centerY() < glMantleSurfacView.getHeight() * 0.6) {
                    showAtrMessage.setTranslationX(rectF.centerX());
                    showAtrMessage.setTranslationY(rectF.centerY() + rectF.width() / 2);
                } else {
                    showAtrMessage.setTranslationX(rectF.centerX());
                    showAtrMessage.setTranslationY(rectF.centerY() -
                            rectF.width() - showAtrMessage.getHeight());
                }
            } else {
                if (rectF.centerY() < glMantleSurfacView.getHeight() * 0.6) {
                    showAtrMessage.setTranslationX(glMantleSurfacView.getWidth() - rectF.centerX());
                    showAtrMessage.setTranslationY(rectF.centerY() + rectF.width() / 2);
                } else {
                    showAtrMessage.setTranslationX(glMantleSurfacView.getWidth() - rectF.centerX());
                    showAtrMessage.setTranslationY(rectF.centerY() -
                            rectF.width() - showAtrMessage.getHeight());
                }
            }
        }
    }

    public void showResult(final List<LivenessModel> models) {
        runOnUiThread(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                if (models != null && models.size() > 0) {
                    LivenessModel livenessModel = models.get(0);
                    atrDetectTime.setText(getResources().getString(R.string.attr_detect_time) + livenessModel.getRgbDetectDuration() + "ms");
                    atrToalTime.setText(getResources().getString(R.string.attr_total_time) + livenessModel.getAllDetectDuration() + "ms");
                } else {
                    atrDetectTime.setText(getResources().getString(R.string.attr_detect_time) + 0 + "ms");
                    atrToalTime.setText(getResources().getString(R.string.attr_total_time) + 0 + "ms");
                }
            }
        });
    }

    public void showAtrDetailMessage(final List<LivenessModel> models) {
            runOnUiThread(new Runnable() {
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {
                    if (models == null || models.size() == 0){
                        showAtrMessage.setVisibility(View.GONE);
                        return;
                    }
                    LivenessModel livenessModel = models.get(0);
                    showAtrMessage.setVisibility(View.VISIBLE);
                    FaceInfo faceInfo = livenessModel.getFaceInfo();
                    String sex = faceInfo.gender == BDFaceSDKCommon.BDFaceGender.BDFACE_GENDER_FEMALE ? getResources().getString(R.string.gender_female) :
                            faceInfo.gender == BDFaceSDKCommon.BDFaceGender.BDFACE_GENDER_MALE ?
                                    getResources().getString(R.string.gender_male) : getResources().getString(R.string.gender_baby);

                    atrSex.setText(getResources().getString(R.string.attr_sex) + sex);
                    atrAge.setText(getResources().getString(R.string.attr_age) + faceInfo.age + getResources().getString(R.string.attr_unit_year));
                    if (faceInfo.glasses == BDFaceSDKCommon.BDFaceGlasses.BDFACE_NO_GLASSES) {
                        atrAccessory.setText(getResources().getString(R.string.attr_glasses_no));
                    } else {
                        atrAccessory.setText(getResources().getString(R.string.attr_glasses_yes));
                    }
//                    atrAccessory.setText("眼镜：" + accessory);
                    atrEmotion.setText(getResources().getString(R.string.attr_safety_helmet) +
                            (livenessModel.getSafetyHatScore() > 0.8 ?
                                    getResources().getString(R.string.yes) : getResources().getString(R.string.no)));
                    if (livenessModel.getMaskScore() > 0.9) {
                        atrMask.setText(getResources().getString(R.string.attr_mask) + getResources().getString(R.string.yes));
                    } else {
                        atrMask.setText(getResources().getString(R.string.attr_mask) + getResources().getString(R.string.no));
                    }
                    BDFaceGazeInfo faceGazeInfo = livenessModel.getBdFaceGazeInfo();
                    leftEye.setText(getResources().getString(R.string.attr_left_eye) + identifyLeftGaze(faceGazeInfo));
                    rightEye.setText(getResources().getString(R.string.attr_right_eye) + identifyRightGaze(faceGazeInfo));
                }
            });
    }
    public String identifyLeftGaze(BDFaceGazeInfo bdFaceGazeInfo) {
        String result = "";
        if (bdFaceGazeInfo != null) {
            if (bdFaceGazeInfo.leftEyeGaze ==
                    BDFaceSDKCommon.BDFaceGazeDirection.BDFACE_GACE_DIRECTION_DOWN
            ) {
                result = getResources().getString(R.string.gaze_direction_down);
            }
            if (bdFaceGazeInfo.leftEyeGaze ==
                    BDFaceSDKCommon.BDFaceGazeDirection.BDFACE_GACE_DIRECTION_LEFT
            ) {
                result = getResources().getString(R.string.gaze_direction_left);
            }
            if (bdFaceGazeInfo.leftEyeGaze ==
                    BDFaceSDKCommon.BDFaceGazeDirection.BDFACE_GACE_DIRECTION_FRONT
            ) {
                result = getResources().getString(R.string.gaze_direction_front);
            }
            if (bdFaceGazeInfo.leftEyeGaze ==
                    BDFaceSDKCommon.BDFaceGazeDirection.BDFACE_GACE_DIRECTION_RIGHT
            ) {
                result = getResources().getString(R.string.gaze_direction_right);
            }
            if (bdFaceGazeInfo.leftEyeGaze ==
                    BDFaceSDKCommon.BDFaceGazeDirection.BDFACE_GACE_DIRECTION_UP
            ) {
                result = getResources().getString(R.string.gaze_direction_up);
            }
            if (bdFaceGazeInfo.leftEyeGaze ==
                    BDFaceSDKCommon.BDFaceGazeDirection.BDFACE_GACE_DIRECTION_EYE_CLOSE
            ) {
                result = getResources().getString(R.string.gaze_eye_close);
            }
        }
        return result;
    }

    public String identifyRightGaze(BDFaceGazeInfo bdFaceGazeInfo) {
        String result = "";
        if (bdFaceGazeInfo != null) {
            if (bdFaceGazeInfo.rightEyeGaze ==
                    BDFaceSDKCommon.BDFaceGazeDirection.BDFACE_GACE_DIRECTION_DOWN
            ) {
                result = getResources().getString(R.string.gaze_direction_down);
            }
            if (bdFaceGazeInfo.rightEyeGaze ==
                    BDFaceSDKCommon.BDFaceGazeDirection.BDFACE_GACE_DIRECTION_LEFT
            ) {
                result = getResources().getString(R.string.gaze_direction_left);
            }
            if (bdFaceGazeInfo.rightEyeGaze ==
                    BDFaceSDKCommon.BDFaceGazeDirection.BDFACE_GACE_DIRECTION_FRONT
            ) {
                result = getResources().getString(R.string.gaze_direction_front);
            }
            if (bdFaceGazeInfo.rightEyeGaze ==
                    BDFaceSDKCommon.BDFaceGazeDirection.BDFACE_GACE_DIRECTION_RIGHT
            ) {
                result = getResources().getString(R.string.gaze_direction_right);
            }
            if (bdFaceGazeInfo.rightEyeGaze ==
                    BDFaceSDKCommon.BDFaceGazeDirection.BDFACE_GACE_DIRECTION_UP
            ) {
                result = getResources().getString(R.string.gaze_direction_up);
            }
            if (bdFaceGazeInfo.rightEyeGaze ==
                    BDFaceSDKCommon.BDFaceGazeDirection.BDFACE_GACE_DIRECTION_EYE_CLOSE
            ) {
                result = getResources().getString(R.string.gaze_eye_close);
            }
        }
        return result;
    }
}
