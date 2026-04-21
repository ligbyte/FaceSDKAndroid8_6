package com.baidu.idl.main.facesdk.identifylibrary.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.idl.main.facesdk.identifylibrary.R;
import com.baidu.idl.main.facesdk.identifylibrary.activity.FaceOneFoOneActivity;
import com.example.datalibrary.fragment.BaseFragment;
import com.example.datalibrary.model.LivenessModel;
import com.example.datalibrary.threshold.SingleBaseConfig;

import java.util.List;

public class IdentifyFragment extends BaseFragment implements View.OnClickListener {
    private RelativeLayout testimonyTipsFailRl;
    private TextView testimonyTipsPleaseFailTv;
    private TextView testimonyTipsFailTv;
    private ImageView testimonyTipsFailIv;
    private int mLiveType;
    private float mRgbLiveScore;
    private float nirLiveScore;
    private View firstAddBut;
    private View testimonyShowRl;
    private ImageView testimonyShowImg;
    private View pictureGroup;
    private View addPictureBut;
    private ImageView pictureImage;
    private View againAddPicture;
    private boolean isVideo = true;

    @Override
    protected Object getContentLayout() {
        return R.layout.fragment_identify;
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
        // 活体状态
        mLiveType = SingleBaseConfig.getBaseConfig().getType();
        // 活体阈值
        mRgbLiveScore = SingleBaseConfig.getBaseConfig().getRgbLiveScore();
        // Live 阈值
        nirLiveScore = SingleBaseConfig.getBaseConfig().getNirLiveScore();
        firstAddBut = contentView.findViewById(R.id.first_add_but);
        testimonyShowRl = contentView.findViewById(R.id.testimony_showRl);
        testimonyShowImg = contentView.findViewById(R.id.testimony_showImg);
        firstAddBut.setOnClickListener(this);
        testimonyShowRl.setOnClickListener(this);
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
            firstAddBut.setVisibility(View.VISIBLE);
            testimonyShowRl.setVisibility(View.GONE);
        } else {
            firstAddBut.setVisibility(View.GONE);
            testimonyShowRl.setVisibility(View.VISIBLE);
            testimonyShowImg.setImageBitmap(bitmap);
        }
    }

    @Override
    public void upLoad(List<LivenessModel> models) {

        if (models == null || models.size() == 0) {
            // 提示隐藏
            testimonyTipsFailRl.setVisibility(View.GONE);
            return;
        }
        LivenessModel model = models.get(0);
        testimonyTipsFailRl.setVisibility(View.VISIBLE);
        if (model.isQualityCheck()) {
            testimonyTipsFailTv.setText(getResources().getString(R.string.compare_failed));
            testimonyTipsFailTv.setTextColor(
                    Color.parseColor("#FFFEC133"));
            testimonyTipsPleaseFailTv.setText(getResources().getString(R.string.face_camera_please));
            testimonyTipsFailIv.setImageResource(R.mipmap.tips_fail);
            return;
        }

        float score = model.getScore();
        if (mLiveType == 0) {
            if (score > SingleBaseConfig.getBaseConfig().getIdThreshold()) {
                testimonyTipsFailTv.setText(getResources().getString(R.string.compare_success_ok));
                testimonyTipsFailTv.setTextColor(
                        Color.parseColor("#FF00BAF2"));
                testimonyTipsPleaseFailTv.setText(getResources().getString(R.string.recognition_success));
                testimonyTipsFailIv.setImageResource(R.mipmap.tips_success);
            } else {
                testimonyTipsFailTv.setText(getResources().getString(R.string.compare_failed));
                testimonyTipsFailTv.setTextColor(
                        Color.parseColor("#FFFEC133"));
                testimonyTipsPleaseFailTv.setText(getResources().getString(R.string.recognition_failed));
                testimonyTipsFailIv.setImageResource(R.mipmap.tips_fail);
            }
            return;
        }
        if (mLiveType == 1 || !isVideo) {

            // 活体阈值判断显示
            if (model.getRgbLivenessScore() < mRgbLiveScore) {
                testimonyTipsFailTv.setText(getResources().getString(R.string.compare_failed));
                testimonyTipsFailTv.setTextColor(
                        Color.parseColor("#FFFEC133"));
                testimonyTipsPleaseFailTv.setText(getResources().getString(R.string.liveness_failed));
                testimonyTipsFailIv.setImageResource(R.mipmap.tips_fail);
            } else {
                if (score > SingleBaseConfig.getBaseConfig()
                        .getIdThreshold()) {
                    testimonyTipsFailTv.setText(getResources().getString(R.string.compare_success));
                    testimonyTipsFailTv.setTextColor(
                            Color.parseColor("#FF00BAF2"));
                    testimonyTipsPleaseFailTv.setText(getResources().getString(R.string.recognition_success));
                    testimonyTipsFailIv.setImageResource(
                            R.mipmap.tips_success);
                } else {
                    testimonyTipsFailTv.setText(getResources().getString(R.string.compare_failed));
                    testimonyTipsFailTv.setTextColor(
                            Color.parseColor("#FFFEC133"));
                    testimonyTipsPleaseFailTv.setText(getResources().getString(R.string.recognition_failed));
                    testimonyTipsFailIv.setImageResource(
                            R.mipmap.tips_fail);
                }
            }
            return;
        }

        if (model.getRgbLivenessScore() < mRgbLiveScore || model.getIrLivenessScore() < nirLiveScore) {
            testimonyTipsFailTv.setText(getResources().getString(R.string.compare_failed));
            testimonyTipsFailTv.setTextColor(
                    Color.parseColor("#FFFEC133"));
            testimonyTipsPleaseFailTv.setText(getResources().getString(R.string.liveness_failed));
            testimonyTipsFailIv.setImageResource(R.mipmap.tips_fail);
        } else {
            if (score > SingleBaseConfig.getBaseConfig()
                    .getIdThreshold()) {
                testimonyTipsFailTv.setText(getResources().getString(R.string.compare_success));
                testimonyTipsFailTv.setTextColor(
                        Color.parseColor("#FF00BAF2"));
                testimonyTipsPleaseFailTv.setText(getResources().getString(R.string.recognition_success));
                testimonyTipsFailIv.setImageResource(
                        R.mipmap.tips_success);
            } else {
                testimonyTipsFailTv.setText(getResources().getString(R.string.compare_failed));
                testimonyTipsFailTv.setTextColor(
                        Color.parseColor("#FFFEC133"));
                testimonyTipsPleaseFailTv.setText(getResources().getString(R.string.recognition_failed));
                testimonyTipsFailIv.setImageResource(
                        R.mipmap.tips_fail);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.first_add_but || id == R.id.testimony_showRl) {

            Intent intent4 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            getAppActivity().startActivityForResult(intent4, FaceOneFoOneActivity.PICK_PHOTO_FRIST);
        } else if (id == R.id.add_picture_but) {
            getIdentifyPicture();
        } else if (id == R.id.again_add_picture) {
            getIdentifyPicture();

        }
    }

    void getIdentifyPicture() {
        Intent intent4 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        getAppActivity().startActivityForResult(intent4, FaceOneFoOneActivity.PICK_PHOTO_ADD);
    }

    public void setVideoOrPicture(boolean isVideo) {
        testimonyTipsFailRl.setVisibility(View.GONE);
        this.isVideo = isVideo;
        if (isVideo) {
            pictureGroup.setVisibility(View.GONE);
        } else {
            pictureGroup.setVisibility(View.VISIBLE);
        }
    }

    public void setBitmap(Bitmap bitmap) {
        testimonyTipsFailRl.setVisibility(View.GONE);

        if (bitmap == null) {
            addPictureBut.setVisibility(View.VISIBLE);
            pictureImage.setVisibility(View.GONE);
            againAddPicture.setVisibility(View.GONE);
        } else {
            addPictureBut.setVisibility(View.GONE);
            pictureImage.setVisibility(View.VISIBLE);
            againAddPicture.setVisibility(View.VISIBLE);
            pictureImage.setImageBitmap(bitmap);
        }
    }


}
