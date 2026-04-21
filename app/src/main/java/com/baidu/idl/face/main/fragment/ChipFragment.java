package com.baidu.idl.face.main.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baidu.idl.face.main.activity.start.HomeActivity;
import com.baidu.idl.face.main.activity.start.StartSettingActivity;
import com.baidu.idl.facesdkdemo.R;
import com.baidu.idl.main.facesdk.FaceAuth;
import com.baidu.idl.main.facesdk.callback.Callback;
import com.example.datalibrary.fragment.BaseFragment;

public class ChipFragment extends BaseFragment implements View.OnClickListener{
    private Button chipUseBtn;
    private View view1;
    private PopupWindow popupWindow;
    private TextView chipUsehiteTv;
    private TextView chipUseErrorTv;
    private View loadView;
    private FaceAuth faceAuth;
    private long lastClickTime = 0L;
    private static final int FAST_CLICK_DELAY_TIME = 1000;
    int count = 0;
    @Override
    protected Object getContentLayout() {
        return R.layout.chip_activation_layout;
    }
    @Override
    public void dismissWindow() {
        if (popupWindow != null){

            popupWindow.dismiss();
        }
    }
    @Override
    protected void initView(View view) {
        super.initView(view);
        // 复制按钮
        faceAuth = new FaceAuth();
        chipUseBtn = view.findViewById(R.id.chip_useBtn);
        chipUseBtn.setOnClickListener(this);
        chipUsehiteTv = view.findViewById(R.id.chip_usehiteTv);
        chipUsehiteTv.setOnClickListener(this);
        chipUseErrorTv = view.findViewById(R.id.chip_useErrorTv);
        loadView = view.findViewById(R.id.load_view);
        initPopupWindow();
    }
    private void initPopupWindow() {
        SharedPreferences sharedPreferences = getAppActivity().
                getSharedPreferences("ws", getAppActivity().MODE_PRIVATE);
        boolean accredit = sharedPreferences.getBoolean("accredit", false);
        if (accredit == false) {
            // 以view将view_layout中的布局和activity_main布局进行桥接
            view1 = View.inflate(getAppActivity(), R.layout.layout_popup_hint, null);
            popupWindow = new PopupWindow(view1,
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            // 点击框外可以使得popupwindow消失
            popupWindow.setFocusable(false);
            popupWindow.setTouchable(true);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.setOutsideTouchable(false);

            TextView hintHelpTv = view1.findViewById(R.id.hint_helpTv);
            hintHelpTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bangListener();
                }
            });
        }
    }
    private void bangListener() {
        Intent intent = new Intent(getAppActivity(), StartSettingActivity.class);
        startActivity(intent);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.chip_usehiteTv:
                bangListener();
                break;
            case R.id.chip_useBtn:
                if (count == 3) {
                    popupWindow.showAsDropDown(chipUsehiteTv, -15, 10);
                    count = 0;
                    chipUsehiteTv.setTextColor(Color.parseColor("#00BAF2"));
                }
                chipUseErrorTv.setText("");
                loadView.setVisibility(View.VISIBLE);
                faceAuth.initLicenseAuthChip(getAppActivity(), new Callback() {
                    @Override
                    public void onResponse(final int code, final String response) {
                        getAppActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadView.setVisibility(View.GONE);
                                if (code == 0){


                                    SharedPreferences sharedPreferences =
                                            getAppActivity().getSharedPreferences("share", 0);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("isAuthChip", true);
                                    editor.commit();
                                    chipUseErrorTv.setText("");
                                    startActivity(new Intent(getAppActivity(), HomeActivity.class));
                                    finish();
                                }else {

                                    chipUseErrorTv.setText(response);
                                }

                            }
                        });
                    }
                });
                if (System.currentTimeMillis() - lastClickTime < FAST_CLICK_DELAY_TIME) {
                    count++;
                }
                lastClickTime = System.currentTimeMillis();
                break;
        }
    }


}
