package com.baidu.idl.main.facesdk.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.idl.main.facesdk.gatelibrary.R;
import com.baidu.idl.main.facesdk.model.BDFaceImageInstance;
import com.example.datalibrary.api.FaceApi;
import com.example.datalibrary.fragment.BaseFragment;
import com.example.datalibrary.model.LivenessModel;
import com.example.datalibrary.model.User;
import com.example.datalibrary.utils.BitmapUtils;
import com.example.datalibrary.utils.FileUtils;
import com.example.datalibrary.utils.ToastUtils;
import com.example.datalibrary.threshold.SingleBaseConfig;

import java.util.List;

public class DevelopFragment extends BaseFragment implements View.OnClickListener {
    private View firstTextTips;
    private View firstCircularTips;
    private ImageView mFaceDetectImageView;
    private RelativeLayout layoutCompareStatus;
    private ImageView isCheckImage;
    private TextView mTvDetect;
    private TextView mTvLive;
    private TextView mTvLiveScore;
    private TextView mTvFeature;
    private TextView mTvAll;
    private TextView mTvAllTime;
    private TextView textCompareStatus;
    private View saveCamera;
    private TextView mNum;
    private View spot;
    private ImageView isNirCheckImage;
    private TextView mTvIr;
    private TextView mTvIrScore;
    private float mRgbLiveScore;
    private int mLiveType;
    public boolean isSaveImage;
    private float nirLiveScore;
    private TextureView irPreviewView;
    private BaseFragmentListener baseFragmentListener;

    @Override
    protected Object getContentLayout() {
        return R.layout.fragment_develop;
    }

    @SuppressLint("StringFormatMatches")
    @Override
    protected void initView(View contentView) {
        super.initView(contentView);
        firstTextTips = contentView.findViewById(R.id.first_text_tips);
        firstCircularTips = contentView.findViewById(R.id.first_circular_tips);
        mFaceDetectImageView = contentView.findViewById(R.id.face_detect_image_view);
        layoutCompareStatus = contentView.findViewById(R.id.layout_compare_status);
        isCheckImage = contentView.findViewById(R.id.is_check_image);
        // 检测耗时
        mTvDetect = contentView.findViewById(R.id.tv_detect_time);
        // RGB活体
        mTvLive = contentView.findViewById(R.id.tv_rgb_live_time);
        mTvLiveScore = contentView.findViewById(R.id.tv_rgb_live_score);
        // 特征提取
        mTvFeature = contentView.findViewById(R.id.tv_feature_time);
        // 检索
        mTvAll = contentView.findViewById(R.id.tv_feature_search_time);
        // 总耗时
        mTvAllTime = contentView.findViewById(R.id.tv_all_time);
        textCompareStatus = contentView.findViewById(R.id.text_compare_status);
        // 活体阈值
        mRgbLiveScore = SingleBaseConfig.getBaseConfig().getRgbLiveScore();
        // Live 阈值
        nirLiveScore = SingleBaseConfig.getBaseConfig().getNirLiveScore();
        // 活体状态
        mLiveType = SingleBaseConfig.getBaseConfig().getType();

        // Ir活体
        mTvIr = contentView.findViewById(R.id.tv_nir_live_time);
        mTvIrScore = contentView.findViewById(R.id.tv_nir_live_score);

        isNirCheckImage = contentView.findViewById(R.id.is_nir_check_image);;
                // 存图按钮
        saveCamera = contentView.findViewById(R.id.save_camera);
        saveCamera.setOnClickListener(this);
        // 存在底库的数量
        mNum = contentView.findViewById(R.id.tv_num);
        mNum.setText(String.format(getResources().getString(R.string.toast_face_database), FaceApi.getInstance().getmUserNum()));
        spot = contentView.findViewById(R.id.spot);
        irPreviewView = contentView.findViewById(R.id.ir_camera_preview_view);
        judgeFirst();
        if (baseFragmentListener != null && mLiveType == 2){
            baseFragmentListener.onOpenNirCamera(irPreviewView);
        }else {
            contentView.findViewById(R.id.nir_group).setVisibility(View.GONE);
        }
    }

    private void judgeFirst() {
        SharedPreferences sharedPreferences = getAppActivity().getSharedPreferences("share",
                Context.MODE_PRIVATE);
        boolean isFirstRun = sharedPreferences.getBoolean("isGateFirstSave", true);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (isFirstRun) {
            setFirstView(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setFirstView(View.GONE);
                }
            }, 3000);
            editor.putBoolean("isGateFirstSave", false);
            editor.commit();
        }
    }

    private void setFirstView(int visibility) {
        firstTextTips.setVisibility(visibility);
        firstCircularTips.setVisibility(visibility);

    }

    @SuppressLint("StringFormatMatches")
    @Override
    public void upLoad(List<LivenessModel> models) {
        if (models == null || models.size() == 0) {
            layoutCompareStatus.setVisibility(View.GONE);
            isCheckImage.setVisibility(View.GONE);
            mFaceDetectImageView.setImageResource(R.mipmap.ic_image_video);
            mTvDetect.setText(String.format(getResources().getString(R.string.format_detect_time), 0));
            mTvLive.setText(String.format(getResources().getString(R.string.format_rgb_live_time), 0));
            mTvLiveScore.setText(String.format(String.format(getResources().getString(R.string.format_rgb_live_score), 0)));
            mTvFeature.setText(String.format(getResources().getString(R.string.format_feature_time), 0));
            mTvAll.setText(String.format(getResources().getString(R.string.format_feature_search_time), 0));
            mTvAllTime.setText(String.format(getResources().getString(R.string.format_total_time), 0));
            mTvIr.setText(String.format(getResources().getString(R.string.format_nir_live_time), 0));
            mTvIrScore.setText(String.format(getResources().getString(R.string.format_nir_live_score), 0));
            return;
        }
        LivenessModel livenessModel = models.get(0);
        BDFaceImageInstance image = livenessModel.getBdFaceImageInstance();
        if (image != null) {
            mFaceDetectImageView.setImageBitmap(BitmapUtils.getInstaceBmp(image));
            image.destory();
        }
        if (livenessModel.isQualityCheck()) {
            isCheckImage.setVisibility(View.VISIBLE);
            isCheckImage.setImageResource(R.mipmap.ic_icon_develop_fail);
            layoutCompareStatus.setVisibility(View.VISIBLE);
            textCompareStatus.setTextColor(Color.parseColor("#FFFEC133"));
//                            textCompareStatus.setMaxEms(6);
            textCompareStatus.setText(getResources().getString(R.string.toast_face_alignment));
            setContent(livenessModel);
            return;
        }

        if (mLiveType == 1) {

            float rgbLivenessScore = livenessModel.getRgbLivenessScore();
            if (rgbLivenessScore < mRgbLiveScore) {
                isCheckImage.setVisibility(View.VISIBLE);
                isCheckImage.setImageResource(R.mipmap.ic_icon_develop_fail);
                layoutCompareStatus.setVisibility(View.VISIBLE);
                textCompareStatus.setTextColor(Color.parseColor("#FFFEC133"));
                textCompareStatus.setText(getResources().getString(R.string.toast_liveness_failed));
                setContent(livenessModel);
                return;
            }
            isCheckImage.setVisibility(View.VISIBLE);
            isCheckImage.setImageResource(R.mipmap.ic_icon_develop_success);
        }else if (mLiveType == 2) {
            float rgbLivenessScore = livenessModel.getRgbLivenessScore();
            float nirLivenessScore = livenessModel.getIrLivenessScore();
            boolean isLive = true;
            if (nirLivenessScore < nirLiveScore) {
                isNirCheckImage.setVisibility(View.VISIBLE);
                isNirCheckImage.setImageResource(R.mipmap.ic_icon_develop_fail);
                isLive = false;
            } else {
                isNirCheckImage.setVisibility(View.VISIBLE);
                isNirCheckImage.setImageResource(R.mipmap.ic_icon_develop_success);
            }
            if (rgbLivenessScore < mRgbLiveScore) {
                isCheckImage.setVisibility(View.VISIBLE);
                isCheckImage.setImageResource(R.mipmap.ic_icon_develop_fail);
                isLive = false;
            } else {
                isCheckImage.setVisibility(View.VISIBLE);
                isCheckImage.setImageResource(R.mipmap.ic_icon_develop_success);

            }
            if (!isLive){
                layoutCompareStatus.setVisibility(View.VISIBLE);
                textCompareStatus.setTextColor(Color.parseColor("#FFFEC133"));
                textCompareStatus.setText(getResources().getString(R.string.toast_liveness_failed));
                setContent(livenessModel);
                return;
            }

        }
        User user = livenessModel.getUser();

        if (user == null) {
            layoutCompareStatus.setVisibility(View.VISIBLE);
            textCompareStatus.setTextColor(Color.parseColor("#FFFEC133"));
            textCompareStatus.setText(getResources().getString(R.string.toast_recognition_failed));
        } else {
            layoutCompareStatus.setVisibility(View.VISIBLE);
            textCompareStatus.setTextColor(Color.parseColor("#FF00BAF2"));
            textCompareStatus.setText(FileUtils.spotString(user.getUserName()));
        }
        setContent(livenessModel);
    }

    @SuppressLint("StringFormatMatches")
    private void setContent(LivenessModel livenessModel) {

        mTvDetect.setText(String.format(getResources().getString(R.string.format_detect_time), livenessModel.getRgbDetectDuration()));
        mTvLive.setText(String.format(getResources().getString(R.string.format_rgb_live_time), livenessModel.getRgbLivenessDuration()));
        mTvLiveScore.setText(String.format(getResources().getString(R.string.format_rgb_live_score), livenessModel.getRgbLivenessScore()));
        mTvFeature.setText(String.format(getResources().getString(R.string.format_feature_time), livenessModel.getFeatureDuration()));
        mTvAll.setText(String.format(getResources().getString(R.string.format_feature_search_time), livenessModel.getCheckDuration()));
        mTvAllTime.setText(String.format(getResources().getString(R.string.format_total_time), livenessModel.getAllDetectDuration()));
        mTvIr.setText(String.format(getResources().getString(R.string.format_nir_live_time), livenessModel.getIrLivenessDuration()));
        mTvIrScore.setText(String.format(getResources().getString(R.string.format_nir_live_score), livenessModel.getIrLivenessScore()));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.save_camera) {
            isSaveImage = !isSaveImage;
            if (isSaveImage) {
                ToastUtils.toast(getAppActivity(), getResources().getString(R.string.toast_save_image_enabled));
                spot.setVisibility(View.VISIBLE);
            } else {
                spot.setVisibility(View.GONE);
            }
        }
    }

    public void setBaseFragmentListener(BaseFragmentListener baseFragmentListener) {
        this.baseFragmentListener = baseFragmentListener;
    }

}
