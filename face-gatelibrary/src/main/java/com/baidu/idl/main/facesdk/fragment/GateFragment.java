package com.baidu.idl.main.facesdk.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.idl.main.facesdk.gatelibrary.R;
import com.example.datalibrary.fragment.BaseFragment;
import com.example.datalibrary.model.LivenessModel;
import com.example.datalibrary.model.User;
import com.example.datalibrary.utils.FileUtils;

import java.util.List;

public class GateFragment extends BaseFragment {
    private RelativeLayout textHuanying;
    private RelativeLayout userNameLayout;
    private ImageView nameImage;
    private TextView nameText;

    @Override
    protected Object getContentLayout() {
        return R.layout.fragment_gate;
    }

    @Override
    protected void initView(View contentView) {
        super.initView(contentView);
        userNameLayout = contentView.findViewById(R.id.user_name_layout);
        textHuanying = contentView.findViewById(R.id.huanying_relative);
        nameImage = contentView.findViewById(R.id.name_image);
        nameText = contentView.findViewById(R.id.name_text);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void upLoad(List<LivenessModel> models) {
        if (models == null || models.size() == 0) {
            textHuanying.setVisibility(View.GONE);
            userNameLayout.setVisibility(View.VISIBLE);
            nameImage.setImageResource(R.mipmap.ic_tips_gate_fail);
            nameText.setTextColor(Color.parseColor("#fec133"));
            nameText.setText(getResources().getString(R.string.recognition_failed_apology));
            return;
        }
        LivenessModel livenessModel = models.get(0);
        User user = livenessModel.getUser();

        if (user == null) {
            textHuanying.setVisibility(View.GONE);
            userNameLayout.setVisibility(View.VISIBLE);
            nameImage.setImageResource(R.mipmap.ic_tips_gate_fail);
            nameText.setTextColor(Color.parseColor("#fec133"));
            nameText.setText(getResources().getString(R.string.recognition_failed_apology));

        } else {
            textHuanying.setVisibility(View.GONE);
            userNameLayout.setVisibility(View.VISIBLE);
            nameImage.setImageResource(R.mipmap.ic_tips_gate_success);
            nameText.setTextColor(Color.parseColor("#0dc6ff"));
            nameText.setText(FileUtils.spotString(user.getUserName()) + " " +
                    getResources().getString(R.string.gate_pass_success_fullname_match));
        }
    }
}
