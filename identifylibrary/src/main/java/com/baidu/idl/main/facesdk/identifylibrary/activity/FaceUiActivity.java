package com.baidu.idl.main.facesdk.identifylibrary.activity;

import android.hardware.Camera;
import android.os.Bundle;
import android.view.TextureView;
import android.view.ViewGroup;

import com.baidu.idl.main.facesdk.identifylibrary.R;
import com.example.datalibrary.activity.BaseActivity;
import com.example.datalibrary.callback.CameraDataCallback;
import com.example.datalibrary.gatecamera.CameraPreviewManager;
import com.example.datalibrary.gl.view.GlMantleSurfacView;
import com.example.datalibrary.model.LivenessModel;
import com.example.datalibrary.view.PreviewTexture;
import com.example.datalibrary.threshold.SingleBaseConfig;

import java.util.List;

public abstract class FaceUiActivity extends BaseActivity  {
    /*图片越大，性能消耗越大，也可以选择640*480， 1280*720*/
    private static final int PREFER_WIDTH = SingleBaseConfig.getBaseConfig().getRgbAndNirWidth();
    private static final int PERFER_HEIGH = SingleBaseConfig.getBaseConfig().getRgbAndNirHeight();


    // RGB+IR 控件
    private PreviewTexture nirPreview;
    private Camera nirCamera;
    protected GlMantleSurfacView glMantleSurfacView;
    private boolean isPause = false;
    protected List<LivenessModel> lastModels;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_face_identify);
        initView();
    }

    /**
     * View
     */
    protected void initView() {
        glMantleSurfacView = findViewById(R.id.camera_textureview);
        glMantleSurfacView.initSurface(SingleBaseConfig.getBaseConfig().getRgbRevert(),
                SingleBaseConfig.getBaseConfig().getMirrorVideoRGB() , SingleBaseConfig.getBaseConfig().isOpenGl());
        CameraPreviewManager.getInstance().startPreview(glMantleSurfacView,
                SingleBaseConfig.getBaseConfig().getRgbVideoDirection() , PREFER_WIDTH, PERFER_HEIGH);
    }
    protected void openNirCamera(TextureView nirView){
        nirPreview = new PreviewTexture(this, nirView);

        if (SingleBaseConfig.getBaseConfig().getRBGCameraId() != -1) {
            nirCamera = Camera.open(Math.abs(SingleBaseConfig.getBaseConfig().getRBGCameraId() - 1));
        } else {
            nirCamera = Camera.open(1);
        }
        ViewGroup.LayoutParams layoutParams = nirView.getLayoutParams();
        int w = layoutParams.width;
        int h = layoutParams.height;
        int cameraRotation = SingleBaseConfig.getBaseConfig().getNirVideoDirection();
        nirCamera.setDisplayOrientation(cameraRotation);
        if (cameraRotation == 90 || cameraRotation == 270) {
            layoutParams.height = w;
            layoutParams.width = h;
            // 旋转90度或者270，需要调整宽高
        } else {
            layoutParams.height = h;
            layoutParams.width = w;
        }
        nirView.setLayoutParams(layoutParams);
        nirPreview.setCamera(nirCamera, PREFER_WIDTH, PERFER_HEIGH);
        initNirFaceConfig(PERFER_HEIGH , PREFER_WIDTH);
        nirCamera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                onNirCameraData(data);
            }
        });
    }
    public void showFrame(List<LivenessModel> models){
        if (models == null){
            return;
        }

        if (isPause){
            glMantleSurfacView.onGlDraw(models , lastModels , SingleBaseConfig.getBaseConfig().getIdThreshold());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTestOpenDebugRegisterFunction();
    }

    private void startTestOpenDebugRegisterFunction() {
        if (SingleBaseConfig.getBaseConfig().getRBGCameraId() != -1) {
            CameraPreviewManager.getInstance().setCameraFacing(SingleBaseConfig.getBaseConfig().getRBGCameraId());
        } else {
            CameraPreviewManager.getInstance().setCameraFacing(CameraPreviewManager.CAMERA_FACING_FRONT);
        }
        int[] cameraSize = CameraPreviewManager.getInstance().initCamera();
        initFaceConfig(cameraSize[1], cameraSize[0]);
        isPause = true;

        CameraPreviewManager.getInstance().setmCameraDataCallback(new CameraDataCallback() {
            @Override
            public void onGetCameraData(byte[] data, Camera camera, int width, int height) {
                onCameraData(data);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPause = false;
        CameraPreviewManager.getInstance().stopPreview();

        if (nirCamera != null) {
            nirCamera.setPreviewCallback(null);
            nirCamera.stopPreview();
            nirCamera.release();
            nirCamera.release();
            nirCamera = null;
        }
    }

    public GlMantleSurfacView getGlMantleSurfacView() {
        return glMantleSurfacView;
    }

    abstract void initFaceConfig(int height , int width);
    abstract void initNirFaceConfig(int height , int width);
    abstract void onNirCameraData(byte[] data);
    abstract void onCameraData(byte[] data);
}
