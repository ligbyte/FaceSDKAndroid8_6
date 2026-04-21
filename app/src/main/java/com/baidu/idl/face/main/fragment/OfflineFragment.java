package com.baidu.idl.face.main.fragment;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.idl.face.main.activity.start.HomeActivity;
import com.baidu.idl.face.main.activity.start.StartSettingActivity;
import com.baidu.idl.face.main.utils.ToastUtils;
import com.baidu.idl.facesdkdemo.R;
import com.baidu.idl.main.facesdk.callback.Callback;
import com.example.authlibrary.BdFaceAuth;
import com.example.datalibrary.fragment.BaseFragment;

public class OfflineFragment extends BaseFragment implements View.OnClickListener{
    private TextView accreditDeviceTv;
    private BdFaceAuth bdFaceAuth;
    private Button accreditOffBtn;
    private TextView accreditHintTv;
    private TextView accreditOffhiteTv;
    @Override
    protected Object getContentLayout() {
        return R.layout.offline_activation_layout;
    }
    @Override
    protected void initView(View view){
        // 激活失败提示
        accreditHintTv =  view.findViewById(R.id.accredit_hintTv);

        // 复制按钮
        bdFaceAuth = new BdFaceAuth();
        // 复制序列码
        accreditDeviceTv = view.findViewById(R.id.accredit_deviceTv);
        String a = bdFaceAuth.getDeviceId(getAppActivity());
        accreditDeviceTv.setText(a);
        accreditOffBtn = view.findViewById(R.id.accredit_offBtn);
        accreditOffBtn.setOnClickListener(this);
        accreditOffhiteTv = view.findViewById(R.id.accredit_offhiteTv);
        accreditOffhiteTv.setOnClickListener(this);

        // 长按点击复制
        accreditDeviceTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager)
                        getAppActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                clipboardManager.setText(accreditDeviceTv.getText());

                ToastUtils.toast(getAppActivity(), "deviceID 复制成功");
                return false;
            }
        });
    }

    private void bangListener() {
        Intent intent = new Intent(getAppActivity(), StartSettingActivity.class);
        startActivity(intent);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            // 离线激活遇到问题
            case R.id.accredit_offhiteTv:
                bangListener();
                break;
            // 离线激活
            case R.id.accredit_offBtn:
                bdFaceAuth.initLicenseOffLine(getAppActivity(), new Callback() {
                    @Override
                    public void onResponse(int code, String response) {

                        getAppActivity().runOnUiThread(new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() {

                                if (code == 0) {

                                    accreditHintTv.setText("");
                                    startActivity(new Intent(getAppActivity(), HomeActivity.class));
                                }else {

                                    accreditHintTv.setText(code + " " + response);
                                }
                            }
                        });
                    }
                });
                break;
        }
    }
}
