package com.baidu.idl.face.main.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ReplacementTransformationMethod;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baidu.idl.face.main.activity.start.HomeActivity;
import com.baidu.idl.face.main.activity.start.StartSettingActivity;
import com.baidu.idl.face.main.utils.NetUtil;
import com.baidu.idl.facesdkdemo.R;
import com.baidu.idl.main.facesdk.callback.Callback;
import com.example.authlibrary.BdFaceAuth;
import com.example.datalibrary.fragment.BaseFragment;


public class OnlineFragment extends BaseFragment implements View.OnClickListener {
    private BdFaceAuth bdFaceAuth;

    private Button accreditOnBtn;
    private long lastClickTime = 0L;
    private static final int FAST_CLICK_DELAY_TIME = 1000;
    int count = 0;
    private PopupWindow popupWindow;
    private View view1;
    private TextView accreditOnhiteTv;
    private EditText activityEtOne;
    private EditText activityEtTwo;
    private EditText activityEtThree;
    private EditText activityEtFour;
    // 拼接后的激活码
    private String end;

    private View activityOneView;
    private View activityTwoView;
    private View activityThreeView;
    private View activityFourView;
    private TextView accreditOnhintTv;

    @Override
    protected Object getContentLayout() {
        return R.layout.online_activation_layout;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        // 复制按钮
        bdFaceAuth = new BdFaceAuth();
        accreditOnBtn = view.findViewById(R.id.accredit_onBtn);
        accreditOnBtn.setOnClickListener(this);
        accreditOnhiteTv = view.findViewById(R.id.accredit_onhiteTv);
        accreditOnhiteTv.setOnClickListener(this);
        // 输入序列码
        activityEtOne = view.findViewById(R.id.activity_et_one);
        activityEtTwo = view.findViewById(R.id.activity_et_two);
        activityEtThree = view.findViewById(R.id.activity_et_three);
        activityEtFour = view.findViewById(R.id.activity_et_four);
        activityEtTwo.setFocusable(false);
        activityEtTwo.setFocusableInTouchMode(false);
        activityEtTwo.requestFocus();

        activityEtThree.setFocusable(false);
        activityEtThree.setFocusableInTouchMode(false);
        activityEtThree.requestFocus();

        activityEtFour.setFocusable(false);
        activityEtFour.setFocusableInTouchMode(false);
        activityEtFour.requestFocus();

        activityOneView = view.findViewById(R.id.activity_one_view);
        activityTwoView = view.findViewById(R.id.activity_two_view);
        activityThreeView = view.findViewById(R.id.activity_three_view);
        activityFourView = view.findViewById(R.id.activity_four_view);
        accreditOnhintTv = view.findViewById(R.id.accredit_onhintTv);
        initActivation();


        // 点击激活按钮3次无响应弹出popupwindow
        initPopupWindow();
    }
    // 激活
    private void initActivation() {
        activityEtOne.setTransformationMethod(new AllCapTransformationMethod(true));
        activityEtTwo.setTransformationMethod(new AllCapTransformationMethod(true));
        activityEtThree.setTransformationMethod(new AllCapTransformationMethod(true));
        activityEtFour.setTransformationMethod(new AllCapTransformationMethod(true));
        activityEtOne.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (activityEtOne.length() == 0) {
                    activityOneView.setBackgroundColor(Color.parseColor("#666666"));
                } else if (activityEtOne.length() == 4) {
                    activityOneView.setBackgroundColor(Color.parseColor("#666666"));
                    activityEtTwo.setFocusable(true);
                    activityEtTwo.setFocusableInTouchMode(true);
                    activityEtTwo.requestFocus();
                    activityEtTwo.setText(activityEtTwo.getText().toString().trim() + " ");
                    activityEtTwo.setSelection(activityEtTwo.getText().length());
                } else if (activityEtOne.length() < 4) {
                    activityOneView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }
            }
        });
        activityEtTwo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (activityEtTwo.length() == 0) {
                    activityEtOne.setFocusable(true);
                    activityEtOne.setFocusableInTouchMode(true);
                    activityEtOne.requestFocus();
                    activityTwoView.setBackgroundColor(Color.parseColor("#666666"));
                } else if (activityEtTwo.getText().toString().trim().length() == 4) {
                    activityTwoView.setBackgroundColor(Color.parseColor("#666666"));
                    activityEtThree.setFocusable(true);
                    activityEtThree.setFocusableInTouchMode(true);
                    activityEtThree.requestFocus();
                    activityEtThree.setText(activityEtThree.getText().toString().trim() + " ");
                    activityEtThree.setSelection(activityEtThree.getText().length());
                } else if (activityEtTwo.getText().toString().trim().length() < 4) {
                    activityTwoView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }


            }
        });
        activityEtThree.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (activityEtThree.length() == 0) {
                    activityEtTwo.setFocusable(true);
                    activityEtTwo.setFocusableInTouchMode(true);
                    activityEtTwo.requestFocus();
                    activityThreeView.setBackgroundColor(Color.parseColor("#666666"));

                } else if (activityEtThree.getText().toString().trim().length() == 4) {
                    activityThreeView.setBackgroundColor(Color.parseColor("#666666"));
                    activityEtFour.setFocusable(true);
                    activityEtFour.setFocusableInTouchMode(true);
                    activityEtFour.requestFocus();
                    activityEtFour.setText(activityEtFour.getText().toString().trim() + " ");
                    activityEtFour.setSelection(activityEtFour.getText().length());
                } else if (activityEtThree.getText().toString().trim().length() < 4) {
                    activityThreeView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }
            }
        });
        activityEtFour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (activityEtFour.length() == 0) {
                    activityEtThree.setFocusable(true);
                    activityEtThree.setFocusableInTouchMode(true);
                    activityEtThree.requestFocus();
                    activityFourView.setBackgroundColor(Color.parseColor("#666666"));
                } else if (activityEtFour.getText().toString().trim().length() == 4) {
                    activityFourView.setBackgroundColor(Color.parseColor("#666666"));
                    accreditOnBtn.setEnabled(true);
                    accreditOnBtn.setBackground(getResources().getDrawable(R.mipmap.btn_main_normal));
                    accreditOnBtn.setTextColor(Color.parseColor("#FFFFFF"));
                } else if (activityEtFour.getText().toString().trim().length() < 4) {
                    activityFourView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    accreditOnBtn.setBackground(getResources().getDrawable(R.mipmap.btn_all_d));
                    accreditOnBtn.setEnabled(false);
                    accreditOnBtn.setTextColor(Color.parseColor("#666666"));
                }
            }
        });
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

    @Override
    public void dismissWindow() {
        if (popupWindow != null){
            popupWindow.dismiss();
        }
    }
    private void bangListener() {
        Intent intent = new Intent(getAppActivity(), StartSettingActivity.class);
        startActivity(intent);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            // 在线激活遇到问题
            case R.id.accredit_onhiteTv:
                bangListener();
                break;
            // 在线激活按钮
            case R.id.accredit_onBtn:
                if (System.currentTimeMillis() - lastClickTime < FAST_CLICK_DELAY_TIME) {
                    count++;
                }
                lastClickTime = System.currentTimeMillis();
                if (count == 3) {
                    popupWindow.showAsDropDown(accreditOnhiteTv, -15, 10);
                    count = 0;
//                    initHandler();
                    accreditOnhiteTv.setTextColor(Color.parseColor("#00BAF2"));
                }
                if (activityEtOne.getText().toString().trim().length() == 4 &&
                        activityEtTwo.getText().toString().trim().length() == 4
                        && activityEtThree.getText().toString().trim().length() == 4
                        && activityEtFour.getText().toString().trim().length() == 4) {
                    String etOne = activityEtOne.getText().toString().trim();
                    String etTwo = activityEtTwo.getText().toString().trim();
                    String etThree = activityEtThree.getText().toString().trim();
                    String etFour = activityEtFour.getText().toString().trim();
                    end = etOne + "-" + etTwo + "-" + etThree + "-" + etFour;


                }
                boolean onNetworkConnected = NetUtil.isNetworkConnected(getAppActivity());
                if (onNetworkConnected) {
                    bdFaceAuth.initLicenseOnLine(getAppActivity(), end, new Callback() {
                        @Override
                        public void onResponse(final int code, final String response) {
                            if (code == 0) {
                                getAppActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        accreditOnhintTv.setText("");
                                        startActivity(new Intent(getAppActivity(), HomeActivity.class));
                                        finish();
                                    }
                                });
                            } else {
                                getAppActivity().runOnUiThread(new Runnable() {
                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void run() {
                                        accreditOnhintTv.setText("错误 : " + code + "  " + response);
                                        initShake();
                                    }
                                });
                            }
                        }
                    });
                } else {
                    accreditOnhintTv.setText(getResources().getString(R.string.ensure_network_normal));

                    initShake();
                }
                break;
        }
    }
    // 为组件设置一个抖动效果
    private void initShake() {
        Animation shake = AnimationUtils.loadAnimation(getAppActivity().getApplicationContext(),
                R.anim.shake);
        activityEtOne.startAnimation(shake);
        activityEtTwo.startAnimation(shake);
        activityEtThree.startAnimation(shake);
        activityEtFour.startAnimation(shake);
        // 改变view的颜色
        activityOneView.setBackgroundColor(Color.parseColor("#F34B56"));
        activityTwoView.setBackgroundColor(Color.parseColor("#FF0033"));
        activityThreeView.setBackgroundColor(Color.parseColor("#FF0033"));
        activityFourView.setBackgroundColor(Color.parseColor("#FF0033"));

    }


    public static class AllCapTransformationMethod extends ReplacementTransformationMethod {

        private char[] lower = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
                'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        private char[] upper = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
                'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        private boolean allUpper = false;

        public AllCapTransformationMethod(boolean needUpper) {
            this.allUpper = needUpper;
        }

        @Override
        protected char[] getOriginal() {
            if (allUpper) {
                return lower;
            } else {
                return upper;
            }
        }

        @Override
        protected char[] getReplacement() {
            if (allUpper) {
                return upper;
            } else {
                return lower;
            }
        }
    }

}
