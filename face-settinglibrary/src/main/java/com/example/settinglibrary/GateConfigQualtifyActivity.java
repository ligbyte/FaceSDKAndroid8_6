package com.example.settinglibrary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import com.example.datalibrary.activity.BaseActivity;
import com.example.datalibrary.utils.PWTextUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class GateConfigQualtifyActivity extends BaseActivity {
    private Switch qcQuality;

    private ImageView qcGestureDecrease;
    private EditText qcGestureEtThreshold;
    private ImageView qcGestureIncrease;

    private ImageView qcIlluminiationDecrease;
    private EditText qcIlluminiationEtThreshold;
    private ImageView qcIlluminiationIncrease;

    private ImageView qcBlurDecrease;
    private EditText qcBlurEtThreshold;
    private ImageView qcBlurIncrease;

    private ImageView qcEyeDecrease;
    private EditText qcEyeEtThreshold;
    private ImageView qcEyeIncrease;

    private ImageView qcCheekDecrease;
    private EditText qcCheekEtThreshold;
    private ImageView qcCheekIncrease;

    private ImageView qcNoseDecrease;
    private EditText qcNoseEtThreshold;
    private ImageView qcNoseIncrease;

    private ImageView qcMouseDecrease;
    private EditText qcMouseEtThreshold;
    private ImageView qcMouseIncrease;

    private ImageView qcChinDecrease;
    private EditText qcChinEtThreshold;
    private ImageView qcChinIncrease;

    private LinearLayout qcLinerQuality;

    private float gesture;
    private float illum;
    private float blur;
    private float eye;
    private float cheek;
    private float nose;
    private float mouth;
    private float chinContour;


    private BigDecimal gestureDecimal;
    private BigDecimal blurDecimal;
    private BigDecimal occlusionLeftEyeDecimal;
    private BigDecimal occlusionLeftCheekDecimal;
    private BigDecimal noseDecimal;
    private BigDecimal chinContourDecimal;
    private BigDecimal illuminiationDecimal;

    private BigDecimal obNonmoralValue;
    private BigDecimal gestureNormalValue;

    private ImageView qcSave;

    private RelativeLayout linerGesture;
    private TextView tvGesture;
    private Button cwGesture;

    private View linerIlluminiation;
    private TextView tvIlluminiation;
    private Button cwIlluminiation;

    private View linerBlur;
    private TextView tvBlur;
    private Button cwBlur;

    private LinearLayout linerocclusion;
    private TextView tvocclusion;
    private Button cwocclusion;
    private String msgTag = "";
    private int showWidth;
    private int showXLocation;
    private View qcLinerFirst;
    private boolean qualityControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gate_config_qualtifys);

        init();
    }

    public void init() {
        // 获取Intent对象
        Intent intent = getIntent();
        linerGesture = findViewById(R.id.linergesture);
        qcLinerFirst = findViewById(R.id.qc_LinerFirst);

        qcSave = findViewById(R.id.qc_save);

        tvGesture = findViewById(R.id.tvgesture);
        cwGesture = findViewById(R.id.cwgesture);

        linerIlluminiation = findViewById(R.id.linerilluminiation);
        tvIlluminiation = findViewById(R.id.tvilluminiation);
        cwIlluminiation = findViewById(R.id.cwilluminiation);

        linerBlur = findViewById(R.id.linerblur);
        tvBlur = findViewById(R.id.tvblur);
        cwBlur = findViewById(R.id.cwblur);

        linerocclusion = findViewById(R.id.linerocclusion);
        tvocclusion = findViewById(R.id.tvocclusion);
        cwocclusion = findViewById(R.id.cwocclusion);

        qcQuality = findViewById(R.id.qc_Quality);
        qcLinerQuality = findViewById(R.id.qc_LinerQuality);

        qcGestureDecrease = findViewById(R.id.qc_GestureDecrease);
        qcGestureEtThreshold = findViewById(R.id.qc_GestureEtThreshold);
        qcGestureIncrease = findViewById(R.id.qc_GestureIncrease);

        qcIlluminiationDecrease = findViewById(R.id.qc_IlluminiationDecrease);
        qcIlluminiationEtThreshold = findViewById(R.id.qc_IlluminiationEtThreshold);
        qcIlluminiationIncrease = findViewById(R.id.qc_IlluminiationIncrease);

        qcBlurDecrease = findViewById(R.id.qc_BlurDecrease);
        qcBlurEtThreshold = findViewById(R.id.qc_BlurEtThreshold);
        qcBlurIncrease = findViewById(R.id.qc_BlurIncrease);

        qcEyeDecrease = findViewById(R.id.qc_EyeDecrease);
        qcEyeEtThreshold = findViewById(R.id.qc_EyeEtThreshold);
        qcEyeIncrease = findViewById(R.id.qc_EyeIncrease);

        qcCheekDecrease = findViewById(R.id.qc_CheekDecrease);
        qcCheekEtThreshold = findViewById(R.id.qc_CheekEtThreshold);
        qcCheekIncrease = findViewById(R.id.qc_CheekIncrease);

        qcNoseDecrease = findViewById(R.id.qc_NoseDecrease);
        qcNoseEtThreshold = findViewById(R.id.qc_NoseEtThreshold);
        qcNoseIncrease = findViewById(R.id.qc_NoseIncrease);

        qcMouseDecrease = findViewById(R.id.qc_MouseDecrease);
        qcMouseEtThreshold = findViewById(R.id.qc_MouseEtThreshold);
        qcMouseIncrease = findViewById(R.id.qc_MouseIncrease);

        qcChinDecrease = findViewById(R.id.qc_ChinDecrease);
        qcChinEtThreshold = findViewById(R.id.qc_ChinEtThreshold);
        qcChinIncrease = findViewById(R.id.qc_ChinIncrease);

        gesture = intent.getIntExtra("gesture" , 30);
        illum = intent.getFloatExtra("illum" , 0.8f);
        blur = intent.getFloatExtra("blur" , 0.8f);
        eye = intent.getFloatExtra("eye" , 0.8f);
        cheek = intent.getFloatExtra("cheek" , 0.8f);
        nose = intent.getFloatExtra("nose" , 0.8f);
        mouth = intent.getFloatExtra("mouth" , 0.8f);
        chinContour = intent.getFloatExtra("chinContour" , 1.0f);
        qualityControl = intent.getBooleanExtra("qualityControl" , true);

        qcGestureEtThreshold.setText((int) gesture + "");
        qcIlluminiationEtThreshold.setText(+illum + "");
        qcBlurEtThreshold.setText(blur + "");
        qcEyeEtThreshold.setText(eye + "");
        qcCheekEtThreshold.setText(cheek + "");
        qcNoseEtThreshold.setText(nose + "");
        qcMouseEtThreshold.setText(mouth + "");
        qcChinEtThreshold.setText(chinContour + "");

        obNonmoralValue = new BigDecimal(0.1 + "");
        gestureNormalValue = new BigDecimal(5 + "");

        if (qualityControl) {
            qcQuality.setChecked(true);
            qcLinerQuality.setVisibility(View.VISIBLE);
        } else {
            qcQuality.setChecked(false);
            qcLinerQuality.setVisibility(View.GONE);
        }

        PWTextUtils.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDismiss() {
                cwGesture.setBackground(getDrawable(R.mipmap.icon_setting_question));
                cwBlur.setBackground(getDrawable(R.mipmap.icon_setting_question));
                cwIlluminiation.setBackground(getDrawable(R.mipmap.icon_setting_question));
                cwocclusion.setBackground(getDrawable(R.mipmap.icon_setting_question));
            }
        });

        qcQuality.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    qcQuality.setChecked(true);
                    qcLinerQuality.setVisibility(View.VISIBLE);
                } else {
                    qcQuality.setChecked(false);
                    qcLinerQuality.setVisibility(View.GONE);
                }
                qualityControl = isChecked;
            }
        });
        setClickListener();
        initEdittextStatus();
    }

    private void initEdittextStatus() {

        qcGestureEtThreshold.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Integer.valueOf(s.toString()) == 90) {
                    qcGestureIncrease.setImageResource(R.mipmap.adding_sign_to_gray);
                }
                if (Integer.valueOf(s.toString()) == 0) {
                    qcGestureDecrease.setImageResource(R.mipmap.icon_setting_minus);
                }
                if (Integer.valueOf(s.toString()) > 0 && Integer.valueOf(s.toString()) < 90) {
                    qcGestureIncrease.setImageResource(R.mipmap.icon_setting_add);
                    qcGestureDecrease.setImageResource(R.mipmap.icon_setting_minus);
                }
            }
        });

        if (Integer.valueOf(qcGestureEtThreshold.getText().toString()) == 90) {
            qcGestureIncrease.setImageResource(R.mipmap.adding_sign_to_gray);
        }
        if (Integer.valueOf(qcGestureEtThreshold.getText().toString()) == 0) {
            qcGestureDecrease.setImageResource(R.mipmap.icon_setting_minus);
        }

        qcIlluminiationEtThreshold.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Float.valueOf(s.toString()) == 1) {
                    qcIlluminiationIncrease.setImageResource(R.mipmap.adding_sign_to_gray);
                }
                if (Float.valueOf(s.toString()) == 0) {
                    qcIlluminiationDecrease.setImageResource(R.mipmap.icon_setting_minus);
                }
                if (Float.valueOf(s.toString()) > 0 && Float.valueOf(s.toString()) < 1) {
                    qcIlluminiationIncrease.setImageResource(R.mipmap.icon_setting_add);
                    qcIlluminiationDecrease.setImageResource(R.mipmap.icon_setting_minus );
                }
            }
        });

        if (Float.valueOf(qcIlluminiationEtThreshold.getText().toString()) == 1) {
            qcIlluminiationIncrease.setImageResource(R.mipmap.adding_sign_to_gray);

        }
        if (Float.valueOf(qcIlluminiationEtThreshold.getText().toString()) == 0) {
            qcIlluminiationDecrease.setImageResource(R.mipmap.icon_setting_minus);
        }


        qcBlurEtThreshold.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Float.valueOf(s.toString()) == 1f) {
                    qcBlurIncrease.setImageResource(R.mipmap.adding_sign_to_gray);
                }
                if (Float.valueOf(s.toString()) == 0f) {
                    qcBlurDecrease.setImageResource(R.mipmap.icon_setting_minus);
                }
                if (Float.valueOf(s.toString()) > 0f && Float.valueOf(s.toString()) < 1f) {
                    qcBlurIncrease.setImageResource(R.mipmap.icon_setting_add);
                    qcBlurDecrease.setImageResource(R.mipmap.icon_setting_minus );
                }
            }
        });

        if (Float.valueOf(qcBlurEtThreshold.getText().toString()) == 1f) {
            qcBlurIncrease.setImageResource(R.mipmap.adding_sign_to_gray);

        }
        if (Float.valueOf(qcBlurEtThreshold.getText().toString()) == 0f) {
            qcBlurDecrease.setImageResource(R.mipmap.icon_setting_minus);
        }


        qcEyeEtThreshold.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Float.valueOf(s.toString()) == 1f) {
                    qcEyeIncrease.setImageResource(R.mipmap.adding_sign_to_gray);
                }
                if (Float.valueOf(s.toString()) == 0f) {
                    qcEyeDecrease.setImageResource(R.mipmap.icon_setting_minus);
                }
                if (Float.valueOf(s.toString()) > 0f && Float.valueOf(s.toString()) < 1f) {
                    qcEyeIncrease.setImageResource(R.mipmap.icon_setting_add);
                    qcEyeDecrease.setImageResource(R.mipmap.icon_setting_minus );
                }
            }
        });

        if (Float.valueOf(qcEyeEtThreshold.getText().toString()) == 1f) {
            qcEyeIncrease.setImageResource(R.mipmap.adding_sign_to_gray);

        }
        if (Float.valueOf(qcEyeEtThreshold.getText().toString()) == 0f) {
            qcEyeDecrease.setImageResource(R.mipmap.icon_setting_minus);
        }


        qcCheekEtThreshold.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Float.valueOf(s.toString()) == 1f) {
                    qcCheekIncrease.setImageResource(R.mipmap.adding_sign_to_gray);
                }
                if (Float.valueOf(s.toString()) == 0f) {
                    qcCheekDecrease.setImageResource(R.mipmap.icon_setting_minus);
                }
                if (Float.valueOf(s.toString()) > 0f && Float.valueOf(s.toString()) < 1f) {
                    qcCheekIncrease.setImageResource(R.mipmap.icon_setting_add);
                    qcCheekDecrease.setImageResource(R.mipmap.icon_setting_minus );
                }
            }
        });

        if (Float.valueOf(qcCheekEtThreshold.getText().toString()) == 1f) {
            qcCheekIncrease.setImageResource(R.mipmap.adding_sign_to_gray);

        }
        if (Float.valueOf(qcCheekEtThreshold.getText().toString()) == 0f) {
            qcCheekDecrease.setImageResource(R.mipmap.icon_setting_minus);
        }

        qcNoseEtThreshold.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Float.valueOf(s.toString()) == 1f) {
                    qcNoseIncrease.setImageResource(R.mipmap.adding_sign_to_gray);
                }
                if (Float.valueOf(s.toString()) == 0f) {
                    qcNoseDecrease.setImageResource(R.mipmap.icon_setting_minus);
                }
                if (Float.valueOf(s.toString()) > 0f && Float.valueOf(s.toString()) < 1f) {
                    qcNoseIncrease.setImageResource(R.mipmap.icon_setting_add);
                    qcNoseDecrease.setImageResource(R.mipmap.icon_setting_minus );
                }
            }
        });

        if (Float.valueOf(qcNoseEtThreshold.getText().toString()) == 1f) {
            qcNoseIncrease.setImageResource(R.mipmap.adding_sign_to_gray);

        }
        if (Float.valueOf(qcNoseEtThreshold.getText().toString()) == 0f) {
            qcNoseDecrease.setImageResource(R.mipmap.icon_setting_minus);
        }

        qcMouseEtThreshold.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Float.valueOf(s.toString()) == 1f) {
                    qcMouseIncrease.setImageResource(R.mipmap.adding_sign_to_gray);
                }
                if (Float.valueOf(s.toString()) == 0f) {
                    qcMouseDecrease.setImageResource(R.mipmap.icon_setting_minus);
                }
                if (Float.valueOf(s.toString()) > 0f && Float.valueOf(s.toString()) < 1f) {
                    qcMouseIncrease.setImageResource(R.mipmap.icon_setting_add);
                    qcMouseDecrease.setImageResource(R.mipmap.icon_setting_minus );
                }
            }
        });

        if (Float.valueOf(qcMouseEtThreshold.getText().toString()) == 1f) {
            qcMouseIncrease.setImageResource(R.mipmap.adding_sign_to_gray);

        }
        if (Float.valueOf(qcMouseEtThreshold.getText().toString()) == 0f) {
            qcMouseDecrease.setImageResource(R.mipmap.icon_setting_minus);
        }

        qcChinEtThreshold.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Float.valueOf(s.toString()) == 1f) {
                    qcChinIncrease.setImageResource(R.mipmap.adding_sign_to_gray);
                }
                if (Float.valueOf(s.toString()) == 0f) {
                    qcChinDecrease.setImageResource(R.mipmap.icon_setting_minus);
                }
                if (Float.valueOf(s.toString()) > 0f && Float.valueOf(s.toString()) < 1f) {
                    qcChinIncrease.setImageResource(R.mipmap.icon_setting_add);
                    qcChinDecrease.setImageResource(R.mipmap.icon_setting_minus );
                }
            }
        });

        if (Float.valueOf(qcChinEtThreshold.getText().toString()) == 1f) {
            qcChinIncrease.setImageResource(R.mipmap.adding_sign_to_gray);

        }
        if (Float.valueOf(qcChinEtThreshold.getText().toString()) == 0f) {
            qcChinDecrease.setImageResource(R.mipmap.icon_setting_minus);
        }
    }

    public void setClickListener() {

        qcGestureDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gesture <= 90 && gesture > 0) {
                    gestureDecimal = new BigDecimal(gesture + "");
                    gesture = gestureDecimal.subtract(gestureNormalValue).floatValue();
                    qcGestureEtThreshold.setText((int) gesture + "");
                }
            }
        });

        qcGestureIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gesture < 90 && gesture >= 0) {
                    gestureDecimal = new BigDecimal(gesture + "");
                    gesture = gestureDecimal.add(gestureNormalValue).floatValue();
                    qcGestureEtThreshold.setText((int) gesture + "");
                }
            }
        });

        qcIlluminiationDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (illum > 0f && illum <= 1f) {
                    illuminiationDecimal = new BigDecimal(illum + "");
                    illum = illuminiationDecimal.subtract(obNonmoralValue).floatValue();
                    qcIlluminiationEtThreshold.setText(illum + "");
                }
            }
        });

        qcIlluminiationIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (illum >= 0f && illum < 1f) {
                    illuminiationDecimal = new BigDecimal(illum + "");
                    illum = illuminiationDecimal.add(obNonmoralValue).floatValue();
                    qcIlluminiationEtThreshold.setText(illum + "");
                }
            }
        });

        qcBlurDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (blur > 0f && blur <= 1f) {
                    blurDecimal = new BigDecimal(blur + "");
                    blur = blurDecimal.subtract(obNonmoralValue).floatValue();
                    qcBlurEtThreshold.setText(blur + "");
                }
            }
        });

        qcBlurIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (blur >= 0f && blur < 1f) {
                    blurDecimal = new BigDecimal(blur + "");
                    blur = blurDecimal.add(obNonmoralValue).floatValue();
                    qcBlurEtThreshold.setText(blur + "");
                }
            }
        });

        qcEyeDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eye > 0f && eye <= 1f) {
                    occlusionLeftEyeDecimal = new BigDecimal(eye + "");
                    eye = occlusionLeftEyeDecimal.subtract(obNonmoralValue).floatValue();
                    qcEyeEtThreshold.setText(eye + "");
                }
            }
        });

        qcEyeIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eye >= 0f && eye < 1f) {
                    occlusionLeftEyeDecimal = new BigDecimal(eye + "");
                    eye = occlusionLeftEyeDecimal.add(obNonmoralValue).floatValue();
                    qcEyeEtThreshold.setText(eye + "");
                }
            }
        });

        qcCheekDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cheek > 0f && cheek <= 1f) {
                    occlusionLeftCheekDecimal = new BigDecimal(cheek + "");
                    cheek = occlusionLeftCheekDecimal.subtract(obNonmoralValue).floatValue();
                    qcCheekEtThreshold.setText(cheek + "");
                }
            }
        });

        qcCheekIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cheek >= 0f && cheek < 1f) {
                    occlusionLeftCheekDecimal = new BigDecimal(cheek + "");
                    cheek = occlusionLeftCheekDecimal.add(obNonmoralValue).floatValue();
                    qcCheekEtThreshold.setText(cheek + "");
                }
            }
        });

        qcNoseDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nose > 0f && nose <= 1f) {
                    noseDecimal = new BigDecimal(nose + "");
                    nose = noseDecimal.subtract(obNonmoralValue).floatValue();
                    qcNoseEtThreshold.setText(nose + "");
                }
            }
        });

        qcNoseIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nose >= 0f && nose < 1f) {
                    noseDecimal = new BigDecimal(nose + "");
                    nose = noseDecimal.add(obNonmoralValue).floatValue();
                    qcNoseEtThreshold.setText(nose + "");
                }
            }
        });

        qcMouseDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mouth > 0f && mouth <= 1f) {
                    noseDecimal = new BigDecimal(mouth + "");
                    mouth = noseDecimal.subtract(obNonmoralValue).floatValue();
                    qcMouseEtThreshold.setText(mouth + "");
                }
            }
        });

        qcMouseIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mouth >= 0f && mouth < 1f) {
                    noseDecimal = new BigDecimal(mouth + "");
                    mouth = noseDecimal.add(obNonmoralValue).floatValue();
                    qcMouseEtThreshold.setText(mouth + "");
                }
            }
        });

        qcChinDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chinContour > 0f && chinContour <= 1f) {
                    chinContourDecimal = new BigDecimal(chinContour + "");
                    chinContour = chinContourDecimal.subtract(obNonmoralValue).floatValue();
                    qcChinEtThreshold.setText(chinContour + "");
                }
            }
        });

        qcChinIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chinContour >= 0f && chinContour < 1f) {
                    chinContourDecimal = new BigDecimal(chinContour + "");
                    chinContour = chinContourDecimal.add(obNonmoralValue).floatValue();
                    qcChinEtThreshold.setText(chinContour + "");
                }
            }
        });


        qcSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cwGesture.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                if (msgTag.equals(getString(R.string.cw_gesture))) {
                    msgTag = "";
                    return;
                }
                msgTag = getString(R.string.cw_gesture);
                cwGesture.setBackground(getDrawable(R.mipmap.icon_setting_question_hl));
                PWTextUtils.showDescribeText(linerGesture, tvGesture, GateConfigQualtifyActivity.this,
                        getString(R.string.cw_gesture), showWidth, showXLocation);
            }
        });
        cwIlluminiation.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                if (msgTag.equals(getString(R.string.cw_illuminiation))) {
                    msgTag = "";
                    return;
                }
                msgTag = getString(R.string.cw_illuminiation);
                cwIlluminiation.setBackground(getDrawable(R.mipmap.icon_setting_question_hl));
                PWTextUtils.showDescribeText(linerIlluminiation, tvIlluminiation, GateConfigQualtifyActivity.this,
                        getString(R.string.cw_illuminiation), showWidth, showXLocation);
            }
        });
        cwBlur.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                if (msgTag.equals(getString(R.string.cw_blur))) {
                    msgTag = "";
                    return;
                }
                msgTag = getString(R.string.cw_blur);
                cwBlur.setBackground(getDrawable(R.mipmap.icon_setting_question_hl));
                PWTextUtils.showDescribeText(linerBlur, tvBlur, GateConfigQualtifyActivity.this,
                        getString(R.string.cw_blur), showWidth, showXLocation);
            }
        });
        cwocclusion.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                if (msgTag.equals(getString(R.string.cw_occulusion))) {
                    msgTag = "";
                    return;
                }
                msgTag = getString(R.string.cw_occulusion);
                cwocclusion.setBackground(getDrawable(R.mipmap.icon_setting_question_hl));
                PWTextUtils.showDescribeText(linerocclusion, tvocclusion, GateConfigQualtifyActivity.this,
                        getString(R.string.cw_occulusion), showWidth, showXLocation);
            }
        });
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("gesture", Integer.valueOf(qcGestureEtThreshold.getText().toString()));
        intent.putExtra("illum", Float.valueOf(qcIlluminiationEtThreshold.getText().toString()));
        intent.putExtra("blur", Float.valueOf(qcBlurEtThreshold.getText().toString()));
        intent.putExtra("eye", Float.valueOf(qcEyeEtThreshold.getText().toString()));
        intent.putExtra("cheek", Float.valueOf(qcCheekEtThreshold.getText().toString()));
        intent.putExtra("nose", Float.valueOf(qcNoseEtThreshold.getText().toString()));
        intent.putExtra("mouth", Float.valueOf(qcMouseEtThreshold.getText().toString()));
        intent.putExtra("chinContour", Float.valueOf(qcChinEtThreshold.getText().toString()));
        intent.putExtra("qualityControl", qualityControl);
        // 设置返回码和返回携带的数据
        setResult(Activity.RESULT_OK, intent);
        super.finish();
    }

    public static String roundByScale(float numberValue) {
        // 构造方法的字符格式这里如果小数不足2位,会以0补足.
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        // format 返回的是字符串
        String resultNumber = decimalFormat.format(numberValue);
        return resultNumber;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        showWidth = qcLinerFirst.getWidth();
        showXLocation = (int) linerGesture.getX();
    }
}

