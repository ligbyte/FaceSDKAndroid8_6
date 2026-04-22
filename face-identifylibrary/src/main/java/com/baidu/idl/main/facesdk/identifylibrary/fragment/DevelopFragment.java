package com.baidu.idl.main.facesdk.identifylibrary.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.idl.main.facesdk.identifylibrary.R;
import com.baidu.idl.main.facesdk.identifylibrary.activity.FaceOneFoOneActivity;
import com.baidu.idl.main.facesdk.model.BDFaceImageInstance;
import com.example.datalibrary.fragment.BaseFragment;
import com.example.datalibrary.model.LivenessModel;
import com.example.datalibrary.utils.BitmapUtils;
import com.example.datalibrary.utils.ToastUtils;
import com.example.datalibrary.threshold.SingleBaseConfig;

import java.util.List;

public class DevelopFragment extends BaseFragment implements View.OnClickListener {
    private ImageView personKaifaIv;
    private TextView tvFeatureTime;
    private TextView tvFeatureSearchTime;
    private TextView tvAllTime;
    private ImageView testImageview;
    private TextView tvUserScore;
    private TextView tvRgbLiveTime;
    private TextView tvRgbLiveScore;
    private TextView tvNirLiveTime;
    private TextView tvNirLiveScore;
    private ImageView hintShowIv;

    private View firstTextTips;
    private View firstCircularTips;
    private int mLiveType;
    private float mRgbLiveScore;
    private float nirLiveScore;

    private View saveCamera;
    public boolean isSaveImage;
    private View spot;
    private View hintShowRl;
    private View developmentAddRl;
    private TextureView irPreviewView;
    private ImageView isNirCheckImage;
    private BaseFragmentListener baseFragmentListener;

    private RelativeLayout testimonyTipsFailRl;
    private TextView testimonyTipsPleaseFailTv;
    private TextView testimonyTipsFailTv;
    private ImageView testimonyTipsFailIv;
    private View pictureGroup;
    private View addPictureBut;
    private ImageView pictureImage;
    private View againAddPicture;
    private View videoImageGroup;
    private boolean isVideo = true;
    public void setBaseFragmentListener(BaseFragmentListener baseFragmentListener) {
        this.baseFragmentListener = baseFragmentListener;
    }

    @Override
    protected Object getContentLayout() {
        return R.layout.fragment_identify_develop;
    }

    @Override
    protected void initView(View contentView) {
        super.initView(contentView);
        // 提示
        testimonyTipsFailRl = contentView.findViewById(R.id.testimony_tips_failRl);
        // 失败提示
        testimonyTipsFailTv = contentView.findViewById(R.id.testimony_tips_failTv);
        testimonyTipsPleaseFailTv = contentView.findViewById(R.id.testimony_tips_please_failTv);

        testimonyTipsFailIv = contentView.findViewById(R.id.testimony_tips_failIv);
        // 特征抽取耗时
        tvFeatureTime = contentView.findViewById(R.id.tv_feature_time);
        // 特征比对耗时
        tvFeatureSearchTime = contentView.findViewById(R.id.tv_feature_search_time);
        // rgb活体得分
        tvRgbLiveScore = contentView.findViewById(R.id.tv_rgb_live_score);
        // nir活体耗时
        tvNirLiveTime = contentView.findViewById(R.id.nir_live_time);
        // nir活体得分
        tvNirLiveScore = contentView.findViewById(R.id.nir_live_score);
        // 总耗时
        tvAllTime = contentView.findViewById(R.id.tv_all_time);
        personKaifaIv = contentView.findViewById(R.id.person_kaifaIv);
        testImageview = contentView.findViewById(R.id.test_rgb_view);
        tvUserScore = contentView.findViewById(R.id.tv_user_score);
        // 活体检测耗时
        tvRgbLiveTime = contentView.findViewById(R.id.tv_rgb_live_time);
        // 活体状态
        mLiveType = SingleBaseConfig.getBaseConfig().getType();
        // 活体阈值
        mRgbLiveScore = SingleBaseConfig.getBaseConfig().getRgbLiveScore();
        // Live 阈值
        nirLiveScore = SingleBaseConfig.getBaseConfig().getNirLiveScore();
        // 图片展示
        hintShowIv = contentView.findViewById(R.id.hint_showIv);
        firstTextTips = contentView.findViewById(R.id.first_text_tips);
        firstCircularTips = contentView.findViewById(R.id.first_circular_tips);
        // 存图按钮
        saveCamera = contentView.findViewById(R.id.save_camera);
        saveCamera.setOnClickListener(this);
        spot = contentView.findViewById(R.id.spot);
        hintShowRl = contentView.findViewById(R.id.hint_showRl);
        developmentAddRl = contentView.findViewById(R.id.development_addRl);
        hintShowRl.setOnClickListener(this);
        developmentAddRl.setOnClickListener(this);
        irPreviewView = contentView.findViewById(R.id.ir_camera_preview_view);
        isNirCheckImage = contentView.findViewById(R.id.is_nir_check_image);
        judgeFirst();
        if (baseFragmentListener != null && mLiveType == 2) {
            baseFragmentListener.onOpenNirCamera(irPreviewView);
        } else {
            contentView.findViewById(R.id.nir_group).setVisibility(View.GONE);
        }

        videoImageGroup = contentView.findViewById(R.id.video_image_group);
        pictureGroup = contentView.findViewById(R.id.picture_group);
        addPictureBut = contentView.findViewById(R.id.add_picture_but);
        addPictureBut.setOnClickListener(this);
        againAddPicture = contentView.findViewById(R.id.again_add_picture);
        againAddPicture.setOnClickListener(this);
        pictureImage = contentView.findViewById(R.id.picture);
    }


    public void upLoadBitmap(Bitmap bitmap) {
        testimonyTipsFailRl.setVisibility(View.GONE);
        if (bitmap == null) {
            developmentAddRl.setVisibility(View.VISIBLE);
            hintShowRl.setVisibility(View.GONE);
        } else {
            hintShowIv.setImageBitmap(bitmap);
            developmentAddRl.setVisibility(View.GONE);
            hintShowRl.setVisibility(View.VISIBLE);
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
            // 阈值
            personKaifaIv.setVisibility(View.GONE);
            // 显示默认图片
            testImageview.setImageResource(R.mipmap.ic_image_video);
            testimonyTipsFailRl.setVisibility(View.GONE);
            // 默认值为0
            tvUserScore.setText(String.format(getResources().getString(R.string.similarity_score), 0));
            tvRgbLiveTime.setText(String.format(getResources().getString(R.string.rgb_liveness_duration), 0));
            tvRgbLiveScore.setText(String.format(getResources().getString(R.string.rgb_liveness_score), 0));
            tvNirLiveTime.setText(String.format(getResources().getString(R.string.nir_liveness_duration), 0));
            tvNirLiveScore.setText(String.format(getResources().getString(R.string.nir_liveness_score), 0));
            tvFeatureTime.setText(String.format(getResources().getString(R.string.feature_extract_duration), 0));
            tvFeatureSearchTime.setText(String.format(getResources().getString(R.string.feature_compare_duration), 0));
            tvAllTime.setText(String.format(getResources().getString(R.string.total_duration), 0));
            return;
        }
        LivenessModel model = models.get(0);
        testimonyTipsFailRl.setVisibility(View.VISIBLE);
        float score = model.getScore();
        // rgb回显图赋值显示
        BDFaceImageInstance image = model.getBdFaceImageInstance();
        if (image != null) {
            testImageview.setImageBitmap(BitmapUtils.getInstaceBmp(image));
        }
        tvUserScore.setText(String.format(getResources().getString(R.string.similarity_score), score));
        tvRgbLiveTime.setText(String.format(getResources().getString(R.string.rgb_liveness_duration), model.getRgbLivenessDuration()));
        tvRgbLiveScore.setText(String.format(getResources().getString(R.string.rgb_liveness_score), model.getRgbLivenessScore()));
        tvNirLiveTime.setText(String.format(getResources().getString(R.string.nir_liveness_duration), model.getIrLivenessDuration()));
        tvNirLiveScore.setText(String.format(getResources().getString(R.string.nir_liveness_score), model.getIrLivenessScore()));
        tvFeatureTime.setText(String.format(getResources().getString(R.string.feature_extract_duration), model.getFeatureDuration()));
        tvFeatureSearchTime.setText(String.format(getResources().getString(R.string.feature_compare_duration), model.getCheckDuration()));
        tvAllTime.setText(String.format(getResources().getString(R.string.total_duration), model.getAllDetectDuration()));

        if (model.isQualityCheck()) {
            personKaifaIv.setVisibility(View.VISIBLE);
            personKaifaIv.setImageResource(R.mipmap.ic_icon_develop_fail);
            setTips(getResources().getString(R.string.compare_failed) , getResources().getString(R.string.face_camera_please) , true);
            return;
        }
        boolean isLive = true;
        // 活体阈值判断显示
        if (mLiveType == 1){
            float rgbLivenessScore = model.getRgbLivenessScore();
            if (rgbLivenessScore < mRgbLiveScore) {
                personKaifaIv.setVisibility(View.VISIBLE);
                personKaifaIv.setImageResource(R.mipmap.ic_icon_develop_fail);
                setTips(getResources().getString(R.string.compare_failed) , getResources().getString(R.string.liveness_failed) , true);
                isLive = false;
            } else {
                personKaifaIv.setVisibility(View.VISIBLE);
                personKaifaIv.setImageResource(R.mipmap.ic_icon_develop_success);
            }
        }
        float rgbLivenessScore = model.getRgbLivenessScore();
        float nirLivenessScore = model.getIrLivenessScore();
        if (mLiveType == 2){

            if (nirLivenessScore < nirLiveScore) {
                isNirCheckImage.setVisibility(View.VISIBLE);
                isNirCheckImage.setImageResource(R.mipmap.ic_icon_develop_fail);
                isLive = false;
            } else {
                isNirCheckImage.setVisibility(View.VISIBLE);
                isNirCheckImage.setImageResource(R.mipmap.ic_icon_develop_success);
            }
            if (rgbLivenessScore < mRgbLiveScore) {
                personKaifaIv.setVisibility(View.VISIBLE);
                personKaifaIv.setImageResource(R.mipmap.ic_icon_develop_fail);
                isLive = false;
            } else {
                personKaifaIv.setVisibility(View.VISIBLE);
                personKaifaIv.setImageResource(R.mipmap.ic_icon_develop_success);
            }
        }

        if (!isLive){
            setTips(getResources().getString(R.string.compare_failed) , getResources().getString(R.string.liveness_failed) , true);
            tvFeatureTime.setText(String.format(getResources().getString(R.string.feature_extract_duration), model.getFeatureDuration()));
            tvFeatureSearchTime.setText(String.format(getResources().getString(R.string.feature_compare_duration),
                    model.getCheckDuration()));

            tvAllTime.setText(String.format(getResources().getString(R.string.total_duration), model.getAllDetectDuration()));
            return;
        }
        if (score > SingleBaseConfig.getBaseConfig().getIdThreshold()) {
            setTips(getResources().getString(R.string.compare_success) , getResources().getString(R.string.similarity_score_with_value) + score, false);
        } else {
            setTips(getResources().getString(R.string.compare_failed) , getResources().getString(R.string.recognition_failed) + score, true);
        }
        tvFeatureTime.setText(String.format(getResources().getString(R.string.feature_extract_duration), model.getFeatureDuration()));
        tvFeatureSearchTime.setText(String.format(getResources().getString(R.string.feature_compare_duration),
                model.getCheckDuration()));

        tvAllTime.setText(String.format(getResources().getString(R.string.total_duration), model.getAllDetectDuration()));
    }

    private void setTips(String title , String name , boolean isFail){

        testimonyTipsFailTv.setText(title);
        testimonyTipsPleaseFailTv.setText(name);
        if (isFail){
            testimonyTipsFailTv.setTextColor(
                    Color.parseColor("#FFFEC133"));
            testimonyTipsFailIv.setImageResource(R.mipmap.tips_fail);
        }else {
            testimonyTipsFailTv.setTextColor(
                    Color.parseColor("#FF00BAF2"));
            testimonyTipsFailIv.setImageResource(R.mipmap.tips_success);
        }
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
        } else if (id == R.id.hint_showRl || id == R.id.development_addRl) {

            Intent intent4 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            getAppActivity().startActivityForResult(intent4, FaceOneFoOneActivity.PICK_PHOTO_FRIST);
        }else if (id == R.id.add_picture_but){
            getIdentifyPicture();
        }else if (id == R.id.again_add_picture){
            getIdentifyPicture();

        }
    }


    void getIdentifyPicture() {
        Intent intent4 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        getAppActivity().startActivityForResult(intent4, FaceOneFoOneActivity.PICK_PHOTO_ADD);
    }

    public void setVideoOrPicture(boolean isVideo){
        testimonyTipsFailRl.setVisibility(View.GONE);
        this.isVideo = isVideo;
        if (isVideo){
            videoImageGroup.setVisibility(View.VISIBLE);
            pictureGroup.setVisibility(View.GONE);
        }else {
            videoImageGroup.setVisibility(View.GONE);
            pictureGroup.setVisibility(View.VISIBLE);
        }
    }
    public void setBitmap(Bitmap bitmap){

        testimonyTipsFailRl.setVisibility(View.GONE);
        if (bitmap == null){
            addPictureBut.setVisibility(View.VISIBLE);
            pictureImage.setVisibility(View.GONE);
            againAddPicture.setVisibility(View.GONE);
        }else {
            addPictureBut.setVisibility(View.GONE);
            pictureImage.setVisibility(View.VISIBLE);
            againAddPicture.setVisibility(View.VISIBLE);
            pictureImage.setImageBitmap(bitmap);
        }
    }

}
