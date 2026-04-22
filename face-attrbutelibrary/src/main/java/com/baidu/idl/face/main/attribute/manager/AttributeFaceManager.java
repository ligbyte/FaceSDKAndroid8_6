package com.baidu.idl.face.main.attribute.manager;

import com.baidu.idl.main.facesdk.FaceInfo;
import com.baidu.idl.main.facesdk.model.BDFaceDetectListConf;
import com.baidu.idl.main.facesdk.model.BDFaceGazeInfo;
import com.baidu.idl.main.facesdk.model.BDFaceImageInstance;
import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon;
import com.example.datalibrary.callback.FaceDetectCallBack;
import com.example.datalibrary.listener.DetectListener;
import com.example.datalibrary.manager.FaceSDKManager;
import com.example.datalibrary.model.BDFaceCheckConfig;
import com.example.datalibrary.model.BDFaceImageConfig;
import com.example.datalibrary.model.LivenessModel;
import com.example.datalibrary.threshold.SingleBaseConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AttributeFaceManager {
    private ExecutorService es = Executors.newSingleThreadExecutor();
    private Future future;

    private static class HolderClass {
        private static final AttributeFaceManager INSTANCE = new AttributeFaceManager();
    }

    public static AttributeFaceManager getInstance() {
        return AttributeFaceManager.HolderClass.INSTANCE;
    }

    /**
     * 检测-活体-特征-人脸检索流程
     *
     * @param bdFaceImageConfig      可见光YUV 数据流
     * @param bdFaceCheckConfig      识别参数
     * @param faceDetectCallBack
     */
    public void onDetectCheck(
            final BDFaceImageConfig bdFaceImageConfig,
            final BDFaceCheckConfig bdFaceCheckConfig,
            final FaceDetectCallBack faceDetectCallBack) {
        if (!FaceSDKManager.initModelSuccess) {
            return;
        }
        ArrayList<LivenessModel> models = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        // 创建检测结果存储数据
        LivenessModel livenessModel = new LivenessModel();
        models.add(livenessModel);
        // 创建检测对象，如果原始数据YUV，转为算法检测的图片BGR
        // TODO: 用户调整旋转角度和是否镜像，手机和开发版需要动态适配
        BDFaceImageInstance rgbInstance = getBdImage(bdFaceImageConfig, bdFaceCheckConfig.darkEnhance);
        livenessModel.setTestBDFaceImageInstanceDuration(System.currentTimeMillis() - startTime);
        onTrack(rgbInstance, livenessModel, new DetectListener() {
            @Override
            public void onDetectSuccess(FaceInfo[] faceInfos, BDFaceImageInstance rgbInstance) {
                // 保存人脸特征点
                livenessModel.setLandmarks(faceInfos[0].landmarks);
                // 保存人脸图片
                livenessModel.setBdFaceImageInstance(rgbInstance.getImage());
                // 调用绘制人脸框接口
                if (faceDetectCallBack != null) {
                    faceDetectCallBack.onFaceDetectDarwCallback(models);
                }

                // 送检识别
                onLivenessCheck(
                        rgbInstance,
                        bdFaceCheckConfig,
                        models,
                        startTime,
                        faceDetectCallBack,
                        faceInfos);
            }

            @Override
            public void onDetectFail() {
                // 流程结束销毁图片，开始下一帧图片检测，否着内存泄露
                if (faceDetectCallBack != null) {
                    faceDetectCallBack.onFaceDetectCallback(null);
                    livenessModel.setBdFaceImageInstance(rgbInstance.getImage());

//                        SaveImageManager.getInstance().saveImage(livenessModel, bdFaceCheckConfig.bdLiveConfig);
                    faceDetectCallBack.onFaceDetectDarwCallback(models);
                    faceDetectCallBack.onTip(0, "未检测到人脸");
                }
                rgbInstance.destory();
            }
        });
    }


    private BDFaceImageInstance getBdImage(BDFaceImageConfig bdFaceImageConfig, boolean darkEnhance) {
        BDFaceImageInstance rgbInstance =
                new BDFaceImageInstance(
                        bdFaceImageConfig.data,
                        bdFaceImageConfig.srcHeight,
                        bdFaceImageConfig.srcWidth,
                        bdFaceImageConfig.bdFaceImageType,
                        bdFaceImageConfig.direction,
                        bdFaceImageConfig.mirror);
        BDFaceImageInstance rgbInstanceOne;
        // 判断暗光恢复
        if (darkEnhance) {
            rgbInstanceOne = FaceSDKManager.getInstance().getFaceModel().getDark().faceDarkEnhance(rgbInstance);
            rgbInstance.destory();
        } else {
            rgbInstanceOne = rgbInstance;
        }
        return rgbInstanceOne;
    }
    private void onTrack(BDFaceImageInstance rgbInstance, LivenessModel livenessModel, DetectListener detectListener) {

        long startDetectTime = System.currentTimeMillis();

        // track
        FaceInfo[] faceInfos = null;
        // 多人采用检测，不能跟踪
        faceInfos = getTrackCheck(rgbInstance);
        // 检测结果判断
        if (faceInfos == null || faceInfos.length == 0) {
            detectListener.onDetectFail();
            return;
        }
        livenessModel.setFaceInfo(faceInfos[0]);

        //  detectListener.onDetectSuccess(faceInfos, rgbInstance);
        detectListener.onDetectSuccess(faceInfos, rgbInstance);
        livenessModel.setRgbDetectDuration(System.currentTimeMillis() - startDetectTime);


    }
    private FaceInfo[] getTrackCheck(BDFaceImageInstance rgbInstance) {

        // 快速检测获取人脸信息，仅用于绘制人脸框，详细人脸数据后续获取
        FaceInfo[] faceInfos =
                FaceSDKManager.getInstance().getFaceModel()
                        .getFaceTrack()
                        .track(
                                BDFaceSDKCommon.DetectType.DETECT_VIS,
                                BDFaceSDKCommon.AlignType.BDFACE_ALIGN_TYPE_RGB_FAST,
                                rgbInstance);
        return faceInfos;
    }
    /**
     * 活体-特征-人脸检索全流程
     *
     * @param rgbInstance            可见光底层送检对象
     * @param models          检测结果数据集合
     * @param startTime              开始检测时间
     * @param faceDetectCallBack
     */
    public void onLivenessCheck(
            final BDFaceImageInstance rgbInstance,
            final BDFaceCheckConfig bdFaceCheckConfig,
            final List<LivenessModel> models,
            final long startTime,
            final FaceDetectCallBack faceDetectCallBack,
            final FaceInfo[] fastFaceInfos) {

        if (future != null && !future.isDone()) {
            // 流程结束销毁图片，开始下一帧图片检测，否着内存泄露
            rgbInstance.destory();
            return;
        }

        future = es.submit(new Runnable() {

            @Override
            public void run() {
                LivenessModel livenessModel = models.get(0);

                BDFaceDetectListConf bdFaceDetectListConfig = new BDFaceDetectListConf();
                bdFaceDetectListConfig.usingQuality = bdFaceDetectListConfig.usingHeadPose
                        = SingleBaseConfig.getBaseConfig().isQualityControl();
                bdFaceDetectListConfig.usingBestImage = SingleBaseConfig.getBaseConfig().isBestImage();
                bdFaceDetectListConfig.usingAttribute = true;
                FaceInfo[] faceInfos = FaceSDKManager.getInstance()
                        .getFaceModel().getFaceDetect()
                        .detect(BDFaceSDKCommon.DetectType.DETECT_VIS,
                                BDFaceSDKCommon.AlignType.BDFACE_ALIGN_TYPE_RGB_ACCURATE,
                                rgbInstance,
                                fastFaceInfos, bdFaceDetectListConfig);
                if (faceInfos == null || faceInfos.length == 0) {
                    livenessModel.setAllDetectDuration(System.currentTimeMillis() - startTime);
                    rgbInstance.destory();
                    if (faceDetectCallBack != null) {
                        faceDetectCallBack.onFaceDetectCallback(models);
                    }
                    return;
                }
                livenessModel.setFaceInfo(faceInfos[0]);
                livenessModel.setLandmarks(faceInfos[0].landmarks);
                // 口罩检测
               float[] scores = FaceSDKManager.getInstance()
                       .getFaceModel().getFaceMoutMask().checkMask(rgbInstance, faceInfos);
                if (scores != null) {
                    livenessModel.setMaskScore(scores[0]);
                }

                // 安全帽检测
                scores = FaceSDKManager.getInstance()
                        .getFaceModel().getFaceSafetyHat().checkHat(rgbInstance, faceInfos);
                if (scores != null) {
                    livenessModel.setSafetyHatScore(scores[0]);
                }
                // 左右眼注意力

                BDFaceGazeInfo bdFaceGazeInfo = FaceSDKManager.getInstance()
                        .getFaceModel().getFaceGaze().gaze(rgbInstance, faceInfos[0].landmarks);
                livenessModel.setBdFaceGazeInfo(bdFaceGazeInfo);
                // 流程结束,记录最终时间
                livenessModel.setAllDetectDuration(System.currentTimeMillis() - startTime);
                // 流程结束销毁图片，开始下一帧图片检测，否着内存泄露
                rgbInstance.destory();
                if (faceDetectCallBack != null) {
                    faceDetectCallBack.onFaceDetectCallback(models);
                }
            }
        });
    }
}
