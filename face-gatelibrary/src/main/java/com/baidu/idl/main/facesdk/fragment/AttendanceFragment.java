package com.baidu.idl.main.facesdk.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.idl.main.facesdk.gatelibrary.R;
import com.example.datalibrary.fragment.BaseFragment;
import com.example.datalibrary.model.LivenessModel;
import com.example.datalibrary.model.User;
import com.example.datalibrary.utils.FileUtils;
import com.example.datalibrary.utils.TimeUtils;

import android.os.Handler;
import java.util.Date;
import java.util.List;

public class AttendanceFragment extends BaseFragment {
    private TextView attendanceTime;
    private TextView attendanceDate;
    private RelativeLayout textHuanying;
    private ViewGroup userLayout;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @SuppressLint("SetTextI18n")
        @Override
        public void run() {
            Date date = new Date();
            attendanceTime.setText(TimeUtils.getTimeShort(date));
            attendanceDate.setText(TimeUtils.getStringDateShort(date) + " "
                    + TimeUtils.getWeek(date));
            initData();
        }
    };

    @Override
    protected void initView(View contentView) {
        super.initView(contentView);
        attendanceTime = contentView.findViewById(R.id.attendance_time);
        attendanceDate = contentView.findViewById(R.id.attendance_date);
        textHuanying = contentView.findViewById(R.id.huanying_relative);
        userLayout = contentView.findViewById(R.id.user_layout);
    }
    private void initData(){
        handler.postDelayed(runnable , 1000);
    }

    @Override
    protected Object getContentLayout() {
        return R.layout.fragment_attendancelibrary;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void upLoad(List<LivenessModel> models) {
        super.upLoad(models);
        if (models == null || models.size() == 0) {
            textHuanying.setVisibility(View.VISIBLE);
            userLayout.removeAllViews();
            return;
        }
        textHuanying.setVisibility(View.GONE);
        userLayout.removeAllViews();
        for (int i = 0, k = models.size(); i < k; i++) {
            checkMultiIdentifyResult(models.get(i), k);
        }
    }


    @SuppressLint("SetTextI18n")
    private void checkMultiIdentifyResult(final LivenessModel livenessModel, int size) {
        User user = livenessModel.getUser();
        if (user == null){
            return;
        }
        View userView = LayoutInflater.from(getAppActivity()).inflate(R.layout.item_user_layout, userLayout, false);
        ImageView nameImage = userView.findViewById(R.id.detect_reg_image_item);
        TextView nameText = userView.findViewById(R.id.name_text);
        TextView attendanceTimeText = userView.findViewById(R.id.attendance_time_text);

        String absolutePath = FileUtils.getBatchImportSuccessDirectory()
                + "/" + user.getImageName();
        Bitmap bitmap = BitmapFactory.decodeFile(absolutePath);
        nameImage.setImageBitmap(bitmap);
        nameText.setTextColor(Color.parseColor("#00BAF2"));
        nameText.setText(FileUtils.spotString(user.getUserName()) + " " + getResources().getString(R.string.message_attendance_success));
        attendanceTimeText.setText(getResources().getString(R.string.label_attendance_time) + TimeUtils.getTimeShort(new Date()));
        userLayout.addView(userView);
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (handler != null && runnable != null){
            handler.removeCallbacks(runnable);
            runnable = null;
            handler = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null && runnable != null){
            handler.removeCallbacks(runnable);
            runnable = null;
            handler = null;
        }
    }
}
