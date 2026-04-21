package com.baidu.idl.face.main.fragment;

import android.annotation.SuppressLint;
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
import com.baidu.idl.face.main.utils.NetUtil;
import com.baidu.idl.facesdkdemo.R;
import com.baidu.idl.main.facesdk.callback.Callback;
import com.example.authlibrary.BdFaceAuth;
import com.example.datalibrary.fragment.BaseFragment;

public class BatchFragment extends BaseFragment implements View.OnClickListener{

    private Button accreditUseBtn;
    private TextView accreditUsehiteTv;
    private TextView accreditUseErrorTv;
    private PopupWindow popupWindow;
    int count = 0;
    private BdFaceAuth faceAuth;
    private View view1;
    private long lastClickTime = 0L;
    private static final int FAST_CLICK_DELAY_TIME = 1000;
    @Override
    protected Object getContentLayout() {
        return R.layout.batch_activation_layout;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        // 复制按钮
        faceAuth = new BdFaceAuth();
        accreditUseBtn = view.findViewById(R.id.accredit_useBtn);
        accreditUseBtn.setOnClickListener(this);
        accreditUsehiteTv = view.findViewById(R.id.accredit_usehiteTv);
        accreditUsehiteTv.setOnClickListener(this);
        accreditUseErrorTv = view.findViewById(R.id.accredit_useErrorTv);
        // 点击激活按钮3次无响应弹出popupwindow
        initPopupWindow();
    }
    private void initPopupWindow() {
        SharedPreferences sharedPreferences = getAppActivity().getSharedPreferences("ws",
                getAppActivity().MODE_PRIVATE);
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
    public void dismissWindow() {
        if (popupWindow != null){

            popupWindow.dismiss();
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            // 应用激活遇到问题
            case R.id.accredit_usehiteTv:
                bangListener();
                break;
            // 应用激活
            case R.id.accredit_useBtn:
                if (count == 3) {
                    popupWindow.showAsDropDown(accreditUsehiteTv, -15, 10);
                    count = 0;
                    accreditUsehiteTv.setTextColor(Color.parseColor("#00BAF2"));
                }
                boolean networkConnected = NetUtil.isNetworkConnected(getAppActivity());
                if (networkConnected) {
                    accreditUseErrorTv.setText("");
                    // todo 提示填写官网申请的批量授权的license ID
                    faceAuth.initLicenseBatchLine(getAppActivity(), "", new Callback() {
                        @Override
                        public void onResponse(final int code, final String response) {
                            if (code == 0) {
                                getAppActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        accreditUseErrorTv.setText("");
                                        startActivity(new Intent(getAppActivity(), HomeActivity.class));
                                        finish();
                                    }
                                });
                            } else {
                                getAppActivity().runOnUiThread(new Runnable() {
                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void run() {
                                        if (code == 2) {
                                            accreditUseErrorTv.setText(code + getResources().getString(R.string.not_have_valid_activation_credits));
                                        } else if (code == 11) {
                                            accreditUseErrorTv.setText(code + getResources().getString(R.string.authorization_application_has_expired));
                                        } else {
                                            accreditUseErrorTv.setText(code + ":" + response);
                                        }
                                    }
                                });
                            }
                        }
                    });
                } else {
                    accreditUseErrorTv.setText(getResources().getString(R.string.ensure_network_normal));
                }
                if (System.currentTimeMillis() - lastClickTime < FAST_CLICK_DELAY_TIME) {
                    count++;
                }
                lastClickTime = System.currentTimeMillis();
                break;
        }
    }
}
