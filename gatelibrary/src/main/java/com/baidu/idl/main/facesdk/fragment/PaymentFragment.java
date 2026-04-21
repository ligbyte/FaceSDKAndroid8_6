package com.baidu.idl.main.facesdk.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.baidu.idl.main.facesdk.activity.gate.FaceUiActivity;
import com.baidu.idl.main.facesdk.gatelibrary.R;
import com.example.datalibrary.fragment.BaseFragment;
import com.example.datalibrary.gl.view.GlMantleSurfacView;
import com.example.datalibrary.model.LivenessModel;
import com.example.datalibrary.utils.FaceOnDrawTexturViewUtil;
import com.example.datalibrary.utils.FileUtils;
import com.example.datalibrary.threshold.SingleBaseConfig;

import java.util.List;
import java.util.Objects;

public class PaymentFragment extends BaseFragment {
    private TextView preToastText;
    private ImageView progressBarView;
    // 包含适配屏幕后后的人脸的x坐标，y坐标，和width
    private float[] pointXY = new float[4];
    private GlMantleSurfacView glMantleSurfacView;
    private boolean count = false;
    private ImageView isMaskImage;

    private ImageView isCheckImageView;
    private TextView detectRegTxt;
    private RelativeLayout progressLayout;
    private RelativeLayout payHintRl;
    private float mRgbLiveScore;
    private float nirLiveScore;
    private ImageView detectRegImageItem;
    private int mLiveType;
    private boolean isTime = true;
    private long searshTime;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            return false;
        }
    });

    @Override
    protected Object getContentLayout() {
        return R.layout.fragment_payment;
    }

    @Override
    protected void initView(View contentView) {
        super.initView(contentView);
        preToastText = contentView.findViewById(R.id.pre_toast_text);
        progressBarView = contentView.findViewById(R.id.progress_bar_view);
        isMaskImage = contentView.findViewById(R.id.is_mask_image);

        isCheckImageView = contentView.findViewById(R.id.is_check_image_view);
        detectRegTxt = contentView.findViewById(R.id.detect_reg_txt);
        progressLayout = contentView.findViewById(R.id.progress_layout);
        payHintRl = contentView.findViewById(R.id.pay_hintRl);
        detectRegImageItem = contentView.findViewById(R.id.detect_reg_image_item);
        // 活体阈值
        mRgbLiveScore = SingleBaseConfig.getBaseConfig().getRgbLiveScore();
        // Live 阈值
        nirLiveScore = SingleBaseConfig.getBaseConfig().getNirLiveScore();
        // 活体状态
        mLiveType = SingleBaseConfig.getBaseConfig().getType();
        glMantleSurfacView = ((FaceUiActivity) Objects.requireNonNull(getActivity())).getGlMantleSurfacView();
    }

    @Override
    public void upLoad(List<LivenessModel> models) {
        super.upLoad(models);
        if (count){
            return;
        }
        if (models == null){
            preToastText.setText("");
            if (isTime) {
                isTime = false;
                searshTime = System.currentTimeMillis();
            }
            long endSearchTime = System.currentTimeMillis() - searshTime;
            if (endSearchTime < 5000) {
                preToastText.setTextColor(Color.parseColor("#FFFFFF"));
                preToastText.setText(R.string.face_within_frame);
                progressBarView.setImageResource(R.mipmap.ic_loading_grey);
            } else {
                payHint(null);
            }
            return;
        }
        isTime = true;
        LivenessModel livenessModel = models.get(0);
        pointXY[0] = livenessModel.getFaceInfo().centerX;
        pointXY[1] = livenessModel.getFaceInfo().centerY;
        pointXY[2] = livenessModel.getFaceInfo().width;
        pointXY[3] = livenessModel.getFaceInfo().width;
        FaceOnDrawTexturViewUtil.converttPointXY(pointXY, glMantleSurfacView,
                livenessModel.getBdFaceImageInstance(), livenessModel.getFaceInfo().width);
        float leftLimitX = glMantleSurfacView.circleX - glMantleSurfacView.circleRadius;
        float rightLimitX = glMantleSurfacView.circleX + glMantleSurfacView.circleRadius;
        float topLimitY = glMantleSurfacView.circleY - glMantleSurfacView.circleRadius;
        float bottomLimitY = glMantleSurfacView.circleY + glMantleSurfacView.circleRadius;
        if (pointXY[0] - pointXY[2] / 2 < leftLimitX
                || pointXY[0] + pointXY[2] / 2 > rightLimitX
                || pointXY[1] - pointXY[3] / 2 < topLimitY
                || pointXY[1] + pointXY[3] / 2 > bottomLimitY) {
            preToastText.setText(R.string.face_within_frame);
            progressBarView.setImageResource(R.mipmap.ic_loading_grey);
            return;
        }
        preToastText.setText(getResources().getString(R.string.recognizing));
        progressBarView.setImageResource(R.mipmap.ic_loading_blue);
        payHint(livenessModel);
    }

    @SuppressLint("SetTextI18n")
    private void payHint(final LivenessModel livenessModel) {
        if (livenessModel == null) {
            isMaskImage.setImageResource(R.mipmap.ic_mask_fail);
            isCheckImageView.setImageResource(R.mipmap.ic_icon_fail_sweat);
            detectRegTxt.setTextColor(Color.parseColor("#FECD33"));
            detectRegTxt.setText(getResources().getString(R.string.recognition_timeout));
            progressLayout.setVisibility(View.GONE);
            payHintRl.setVisibility(View.VISIBLE);
            return;
        }
        if (livenessModel.getUser() != null) {
            // todo: 成功展示kk
            progressLayout.setVisibility(View.GONE);
            payHintRl.setVisibility(View.VISIBLE);
            String absolutePath = FileUtils.getBatchImportSuccessDirectory()
                    + "/" + livenessModel.getUser().getImageName();
            Bitmap userBitmap = BitmapFactory.decodeFile(absolutePath);
            detectRegImageItem.setImageBitmap(userBitmap);
            isMaskImage.setImageResource(R.mipmap.ic_mask_success);
            isCheckImageView.setImageResource(R.mipmap.ic_icon_success_star);
            detectRegTxt.setTextColor(Color.parseColor("#00BAF2"));
            detectRegTxt.setText(FileUtils.spotString(livenessModel.getUser().getUserName()) + " " + getResources().getString(R.string.recognition_success));
            count = true;
        } else {
            if (livenessModel.isQualityCheck()) {
                preToastText.setText(getResources().getString(R.string.toast_face_alignment));
                return;
            }
            if (mLiveType == 1) {
                float rgbLivenessScore = livenessModel.getRgbLivenessScore();
                if (rgbLivenessScore < mRgbLiveScore) {
                    preToastText.setText(getResources().getString(R.string.toast_liveness_failed));
                    return;
                }
            }else if (mLiveType == 2) {
                float rgbLivenessScore = livenessModel.getRgbLivenessScore();
                float nirLivenessScore = livenessModel.getIrLivenessScore();
                if (nirLivenessScore < nirLiveScore || rgbLivenessScore < mRgbLiveScore) {
                    preToastText.setText(getResources().getString(R.string.toast_liveness_failed));
                    return;
                }
            }
            preToastText.setText(getResources().getString(R.string.no_recognize_info));
        }

    }

}
