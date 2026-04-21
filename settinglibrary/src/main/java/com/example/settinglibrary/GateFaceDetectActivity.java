package com.example.settinglibrary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.example.datalibrary.activity.BaseActivity;
import com.example.datalibrary.utils.PWTextUtils;

import java.math.BigDecimal;

public class GateFaceDetectActivity extends BaseActivity implements View.OnClickListener {

    int zero = 0;
    int ten = 10;
    private static final int ONE = 1;
    private static final int TWO = 2;
    private static final int THREE = 3;

    private static final int HUNDERED = 100;

    private int activeModel;
    private ImageView qcSave;
    private LinearLayout rgbandnirLlMixture;
    private LinearLayout rgbandnirMixture;
    private int cameraLightThreshold;
    private EditText thRgbandnirLiveEtThreshold;
    private EditText thLiveEtThreshold;
    private EditText thIDEtThreshold;
    private EditText mixtureIDEtThreshold;
    private float liveScoreThreshold;
    private float idScoreThreshold;
    private float rgbAndNirScoreThreshold;
    private RadioGroup flsMixtureType;
    private LinearLayout flRepresent;

    private int showWidth;
    private int showXLocation;
    private RadioButton mixtureZero;
    private RadioButton mixtureOne;
    private RadioButton mixtureTwo;
    private Button cwCameratype;

    private String msgTag = "";
    private TextView tvThreshold;
    private TextView thLiveTvThreshold;
    private TextView thIDTvThreshold;
    private TextView mixtureIDTvThreshold;
    private ImageView mixtureIDDecreaseAshDisposal;
    private ImageView mixtureIDIncreaseAshDisposal;
    private ImageView thIDDecreaseAshDisposal;
    private ImageView thIDIncreaseAshDisposal;
    private ImageView thLiveDecreaseAshDisposal;
    private ImageView thLiveIncreaseAshDisposal;
    private ImageView thLiveDecrease;
    private ImageView thLiveIncrease;
    private ImageView thIDDecrease;
    private ImageView thIDIncrease;
    private ImageView mixtureIDDecrease;
    private ImageView mixtureIDIncrease;
    private View linerreCognizeThrehold;
    private BigDecimal faceThresholdDecimal;
    private BigDecimal levelValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gate_face_detect);
        init();
    }

    private void init() {
        // 获取Intent对象
        Intent intent = getIntent();
        levelValue = new BigDecimal(0.05  + "");
        // 模型
        activeModel = intent.getIntExtra("activeModel" , 1);

        liveScoreThreshold = intent.getFloatExtra("liveScoreThreshold" , 80f);
        idScoreThreshold = intent.getFloatExtra("idScoreThreshold" , 80f);
        rgbAndNirScoreThreshold = intent.getFloatExtra("rgbAndNirScoreThreshold" , 80f);
        // 模态切换光线阈值
        cameraLightThreshold = intent.getIntExtra("cameraLightThreshold" , 50);

        // 模型和模态
        flsMixtureType = findViewById(R.id.fls_mixture_type);
        flsMixtureType.setOnCheckedChangeListener(liveType);
        mixtureZero = findViewById(R.id.mixture_zero);
        mixtureOne = findViewById(R.id.mixture_one);
        mixtureTwo = findViewById(R.id.mixture_two);

        // RGB/NIR模态切换条件
        rgbandnirLlMixture = findViewById(R.id.rgbandnir_ll_mixture);
        rgbandnirMixture = findViewById(R.id.rgbandnir_mixture);
        ImageView thRgbandnirLiveDecrease = findViewById(R.id.th_rgbandnir_LiveDecrease);
        thRgbandnirLiveDecrease.setOnClickListener(this);
        thRgbandnirLiveEtThreshold = findViewById(R.id.th_rgbandnir_LiveEtThreshold);
        ImageView thRgbandnirLiveIncrease = findViewById(R.id.th_rgbandnir_LiveIncrease);
        thRgbandnirLiveIncrease.setOnClickListener(this);

        // 识别阈值
        // 生活照模型
        thLiveDecrease = findViewById(R.id.th_LiveDecrease);
        thLiveDecrease.setOnClickListener(this);
        thLiveEtThreshold = findViewById(R.id.th_LiveEtThreshold);
        thLiveIncrease = findViewById(R.id.th_LiveIncrease);
        thLiveIncrease.setOnClickListener(this);
        // 证件照模型
        thIDDecrease = findViewById(R.id.th_IDDecrease);
        thIDDecrease.setOnClickListener(this);
        thIDEtThreshold = findViewById(R.id.th_IDEtThreshold);
        thIDIncrease = findViewById(R.id.th_IDIncrease);
        thIDIncrease.setOnClickListener(this);
        // RGB+NIR混合模态阈值
        mixtureIDDecrease = findViewById(R.id.mixture_IDDecrease);
        mixtureIDDecrease.setOnClickListener(this);
        mixtureIDEtThreshold = findViewById(R.id.mixture_IDEtThreshold);
        mixtureIDIncrease = findViewById(R.id.mixture_IDIncrease);
        mixtureIDIncrease.setOnClickListener(this);

        qcSave = findViewById(R.id.qc_save);
        qcSave.setOnClickListener(this);
        flRepresent = findViewById(R.id.flRepresent);

        linerreCognizeThrehold = findViewById(R.id.linerrecognizethrehold);
        cwCameratype = findViewById(R.id.cw_cameratype);
        cwCameratype.setOnClickListener(this);
        tvThreshold = findViewById(R.id.tvthreshold);

        PWTextUtils.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDismiss() {
                cwCameratype.setBackground(getDrawable(R.mipmap.icon_setting_question));
            }
        });

        if (activeModel == 1) {
            mixtureZero.setChecked(true);
        }
        if (activeModel == 2) {
            mixtureOne.setChecked(true);
        }
        if (activeModel == 3) {
            mixtureTwo.setChecked(true);
        }

        thLiveTvThreshold = findViewById(R.id.th_LiveTvThreshold);
        thIDTvThreshold = findViewById(R.id.th_IDTvThreshold);
        mixtureIDTvThreshold = findViewById(R.id.mixture_IDTvThreshold);
        mixtureIDDecreaseAshDisposal = findViewById(R.id.mixture_IDDecrease_Ash_disposal);
        mixtureIDIncreaseAshDisposal = findViewById(R.id.mixture_IDIncrease_Ash_disposal);
        thIDDecreaseAshDisposal = findViewById(R.id.th_IDDecrease_Ash_disposal);
        thIDIncreaseAshDisposal = findViewById(R.id.th_IDIncrease_Ash_disposal);
        thLiveDecreaseAshDisposal = findViewById(R.id.th_LiveDecrease_Ash_disposal);
        thLiveIncreaseAshDisposal = findViewById(R.id.th_LiveIncrease_Ash_disposal);

        mixtureZero.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mixtureZero.setChecked(true);
                    mixtureOne.setChecked(false);
                    mixtureTwo.setChecked(false);
                    activeModel = 1;

                    mixtureZero.setTextColor(getResources().getColor(R.color.white));
                    mixtureOne.setTextColor(getResources().getColor(R.color.activition_color));
                    mixtureTwo.setTextColor(getResources().getColor(R.color.activition_color));

                    thLiveTvThreshold.setTextColor(getResources().getColor(R.color.white));
                    thIDTvThreshold.setTextColor(getResources().getColor(R.color.activition_color));
                    mixtureIDTvThreshold.setTextColor(getResources().getColor(R.color.activition_color));
                    thLiveDecreaseAshDisposal.setVisibility(View.GONE);
                    thLiveIncreaseAshDisposal.setVisibility(View.GONE);
                    thLiveDecrease.setVisibility(View.VISIBLE);
                    thLiveIncrease.setVisibility(View.VISIBLE);
                    thIDDecreaseAshDisposal.setVisibility(View.VISIBLE);
                    thIDIncreaseAshDisposal.setVisibility(View.VISIBLE);
                    thIDDecrease.setVisibility(View.GONE);
                    thIDIncrease.setVisibility(View.GONE);
                    mixtureIDDecreaseAshDisposal.setVisibility(View.VISIBLE);
                    mixtureIDIncreaseAshDisposal.setVisibility(View.VISIBLE);
                    mixtureIDDecrease.setVisibility(View.GONE);
                    mixtureIDIncrease.setVisibility(View.GONE);

                    thLiveEtThreshold.setTextColor(getResources().getColor(R.color.white));
                    thIDEtThreshold.setTextColor(getResources().getColor(R.color.activition_color));
                    mixtureIDEtThreshold.setTextColor(getResources().getColor(R.color.activition_color));

                }
            }
        });

        mixtureOne.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mixtureZero.setChecked(false);
                    mixtureOne.setChecked(true);
                    mixtureTwo.setChecked(false);
                    activeModel = 2;

                    mixtureZero.setTextColor(getResources().getColor(R.color.activition_color));
                    mixtureOne.setTextColor(getResources().getColor(R.color.white));
                    mixtureTwo.setTextColor(getResources().getColor(R.color.activition_color));

                    thLiveTvThreshold.setTextColor(getResources().getColor(R.color.activition_color));
                    thIDTvThreshold.setTextColor(getResources().getColor(R.color.white));
                    mixtureIDTvThreshold.setTextColor(getResources().getColor(R.color.activition_color));
                    thLiveDecreaseAshDisposal.setVisibility(View.VISIBLE);
                    thLiveIncreaseAshDisposal.setVisibility(View.VISIBLE);
                    thLiveDecrease.setVisibility(View.GONE);
                    thLiveIncrease.setVisibility(View.GONE);
                    thIDDecreaseAshDisposal.setVisibility(View.GONE);
                    thIDIncreaseAshDisposal.setVisibility(View.GONE);
                    thIDDecrease.setVisibility(View.VISIBLE);
                    thIDIncrease.setVisibility(View.VISIBLE);
                    mixtureIDDecreaseAshDisposal.setVisibility(View.VISIBLE);
                    mixtureIDIncreaseAshDisposal.setVisibility(View.VISIBLE);
                    mixtureIDDecrease.setVisibility(View.GONE);
                    mixtureIDIncrease.setVisibility(View.GONE);

                    thLiveEtThreshold.setTextColor(getResources().getColor(R.color.activition_color));
                    thIDEtThreshold.setTextColor(getResources().getColor(R.color.white));
                    mixtureIDEtThreshold.setTextColor(getResources().getColor(R.color.activition_color));

                }
            }
        });

        mixtureTwo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    rgbandnirLlMixture.setVisibility(View.GONE);
                    rgbandnirMixture.setVisibility(View.GONE);

                    mixtureZero.setChecked(false);
                    mixtureOne.setChecked(false);
                    mixtureTwo.setChecked(true);
                    activeModel = 3;

                    mixtureZero.setTextColor(getResources().getColor(R.color.activition_color));
                    mixtureOne.setTextColor(getResources().getColor(R.color.activition_color));
                    mixtureTwo.setTextColor(getResources().getColor(R.color.white));

                    thLiveTvThreshold.setTextColor(getResources().getColor(R.color.activition_color));
                    thIDTvThreshold.setTextColor(getResources().getColor(R.color.activition_color));
                    mixtureIDTvThreshold.setTextColor(getResources().getColor(R.color.white));
                    thLiveDecreaseAshDisposal.setVisibility(View.VISIBLE);
                    thLiveIncreaseAshDisposal.setVisibility(View.VISIBLE);
                    thLiveDecrease.setVisibility(View.GONE);
                    thLiveIncrease.setVisibility(View.GONE);
                    thIDDecreaseAshDisposal.setVisibility(View.VISIBLE);
                    thIDIncreaseAshDisposal.setVisibility(View.VISIBLE);
                    thIDDecrease.setVisibility(View.GONE);
                    thIDIncrease.setVisibility(View.GONE);
                    mixtureIDDecreaseAshDisposal.setVisibility(View.GONE);
                    mixtureIDIncreaseAshDisposal.setVisibility(View.GONE);
                    mixtureIDDecrease.setVisibility(View.VISIBLE);
                    mixtureIDIncrease.setVisibility(View.VISIBLE);

                    thLiveEtThreshold.setTextColor(getResources().getColor(R.color.activition_color));
                    thIDEtThreshold.setTextColor(getResources().getColor(R.color.activition_color));
                    mixtureIDEtThreshold.setTextColor(getResources().getColor(R.color.white));

                } else {
                    rgbandnirLlMixture.setVisibility(View.GONE);
                    rgbandnirMixture.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (activeModel == ONE) {
            mixtureZero.setChecked(true);

            mixtureZero.setTextColor(getResources().getColor(R.color.white));
            mixtureOne.setTextColor(getResources().getColor(R.color.activition_color));
            mixtureTwo.setTextColor(getResources().getColor(R.color.activition_color));

            thLiveTvThreshold.setTextColor(getResources().getColor(R.color.white));
            thIDTvThreshold.setTextColor(getResources().getColor(R.color.activition_color));
            mixtureIDTvThreshold.setTextColor(getResources().getColor(R.color.activition_color));
            thLiveDecreaseAshDisposal.setVisibility(View.GONE);
            thLiveIncreaseAshDisposal.setVisibility(View.GONE);
            thLiveDecrease.setVisibility(View.VISIBLE);
            thLiveIncrease.setVisibility(View.VISIBLE);
            thIDDecreaseAshDisposal.setVisibility(View.VISIBLE);
            thIDIncreaseAshDisposal.setVisibility(View.VISIBLE);
            thIDDecrease.setVisibility(View.GONE);
            thIDIncrease.setVisibility(View.GONE);
            mixtureIDDecreaseAshDisposal.setVisibility(View.VISIBLE);
            mixtureIDIncreaseAshDisposal.setVisibility(View.VISIBLE);
            mixtureIDDecrease.setVisibility(View.GONE);
            mixtureIDIncrease.setVisibility(View.GONE);


            thLiveEtThreshold.setTextColor(getResources().getColor(R.color.white));
            thIDEtThreshold.setTextColor(getResources().getColor(R.color.activition_color));
            mixtureIDEtThreshold.setTextColor(getResources().getColor(R.color.activition_color));
        }
        if (activeModel == TWO) {
            mixtureOne.setChecked(true);

            mixtureZero.setTextColor(getResources().getColor(R.color.activition_color));
            mixtureOne.setTextColor(getResources().getColor(R.color.white));
            mixtureTwo.setTextColor(getResources().getColor(R.color.activition_color));

            thLiveTvThreshold.setTextColor(getResources().getColor(R.color.activition_color));
            thIDTvThreshold.setTextColor(getResources().getColor(R.color.white));
            mixtureIDTvThreshold.setTextColor(getResources().getColor(R.color.activition_color));
            thLiveDecreaseAshDisposal.setVisibility(View.VISIBLE);
            thLiveIncreaseAshDisposal.setVisibility(View.VISIBLE);
            thLiveDecrease.setVisibility(View.GONE);
            thLiveIncrease.setVisibility(View.GONE);
            thIDDecreaseAshDisposal.setVisibility(View.GONE);
            thIDIncreaseAshDisposal.setVisibility(View.GONE);
            thIDDecrease.setVisibility(View.VISIBLE);
            thIDIncrease.setVisibility(View.VISIBLE);
            mixtureIDDecreaseAshDisposal.setVisibility(View.VISIBLE);
            mixtureIDIncreaseAshDisposal.setVisibility(View.VISIBLE);
            mixtureIDDecrease.setVisibility(View.GONE);
            mixtureIDIncrease.setVisibility(View.GONE);

            thLiveEtThreshold.setTextColor(getResources().getColor(R.color.activition_color));
            thIDEtThreshold.setTextColor(getResources().getColor(R.color.white));
            mixtureIDEtThreshold.setTextColor(getResources().getColor(R.color.activition_color));
        }
        if (activeModel == THREE) {
            mixtureTwo.setChecked(true);

            mixtureZero.setTextColor(getResources().getColor(R.color.activition_color));
            mixtureOne.setTextColor(getResources().getColor(R.color.activition_color));
            mixtureTwo.setTextColor(getResources().getColor(R.color.white));

            thLiveTvThreshold.setTextColor(getResources().getColor(R.color.activition_color));
            thIDTvThreshold.setTextColor(getResources().getColor(R.color.activition_color));
            mixtureIDTvThreshold.setTextColor(getResources().getColor(R.color.white));
            thLiveDecreaseAshDisposal.setVisibility(View.VISIBLE);
            thLiveIncreaseAshDisposal.setVisibility(View.VISIBLE);
            thLiveDecrease.setVisibility(View.GONE);
            thLiveIncrease.setVisibility(View.GONE);
            thIDDecreaseAshDisposal.setVisibility(View.VISIBLE);
            thIDIncreaseAshDisposal.setVisibility(View.VISIBLE);
            thIDDecrease.setVisibility(View.GONE);
            thIDIncrease.setVisibility(View.GONE);
            mixtureIDDecreaseAshDisposal.setVisibility(View.GONE);
            mixtureIDIncreaseAshDisposal.setVisibility(View.GONE);
            mixtureIDDecrease.setVisibility(View.VISIBLE);
            mixtureIDIncrease.setVisibility(View.VISIBLE);

            thLiveEtThreshold.setTextColor(getResources().getColor(R.color.activition_color));
            thIDEtThreshold.setTextColor(getResources().getColor(R.color.activition_color));
            mixtureIDEtThreshold.setTextColor(getResources().getColor(R.color.white));
        }

        thRgbandnirLiveEtThreshold.setText(cameraLightThreshold + "");
        thLiveEtThreshold.setText(liveScoreThreshold + "");
        thIDEtThreshold.setText(idScoreThreshold + "");
        mixtureIDEtThreshold.setText(rgbAndNirScoreThreshold + "");

        if (mixtureTwo.isChecked()) {
            rgbandnirLlMixture.setVisibility(View.GONE);
            rgbandnirMixture.setVisibility(View.GONE);
        } else {
            rgbandnirLlMixture.setVisibility(View.GONE);
            rgbandnirMixture.setVisibility(View.GONE);
        }
    }

    public RadioGroup.OnCheckedChangeListener liveType = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int checkedRadioButtonId = group.getCheckedRadioButtonId();
            if (checkedRadioButtonId == R.id.mixture_zero) {
                activeModel = ONE;
            } else if (checkedRadioButtonId == R.id.mixture_one) {
                activeModel = TWO;
            } else if (checkedRadioButtonId == R.id.mixture_two) {
                activeModel = THREE;
            }
        }
    };

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("liveScoreThreshold", liveScoreThreshold);
        intent.putExtra("cameraLightThreshold", cameraLightThreshold);
        intent.putExtra("rgbAndNirScoreThreshold", rgbAndNirScoreThreshold);
        intent.putExtra("idScoreThreshold", idScoreThreshold);
        intent.putExtra("activeModel", activeModel);
        // 设置返回码和返回携带的数据
        setResult(Activity.RESULT_OK, intent);
        super.finish();
    }

    @SuppressLint("NewApi")
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.qc_save) {
            liveScoreThreshold = Float.valueOf(thLiveEtThreshold.getText().toString());
            cameraLightThreshold = Integer.valueOf(thRgbandnirLiveEtThreshold.getText().toString());
            rgbAndNirScoreThreshold = Float.valueOf(mixtureIDEtThreshold.getText().toString());
            idScoreThreshold = Float.valueOf(thIDEtThreshold.getText().toString());

            if (activeModel == ONE) {
                activeModel = ONE;
            }
            if (activeModel == TWO) {
                activeModel = TWO;
            }
            if (activeModel == THREE) {
                activeModel = THREE;
            }
            finish();
        } else if (id == R.id.th_rgbandnir_LiveDecrease) {
            if (cameraLightThreshold > zero && cameraLightThreshold <= 255) {
                cameraLightThreshold = cameraLightThreshold - 5;
                thRgbandnirLiveEtThreshold.setText(cameraLightThreshold + "");
            }
        } else if (id == R.id.th_rgbandnir_LiveIncrease) {
            if (cameraLightThreshold >= zero && cameraLightThreshold < 255) {
                cameraLightThreshold = cameraLightThreshold + 5;
                thRgbandnirLiveEtThreshold.setText(cameraLightThreshold + "");
            }
        } else if (id == R.id.th_LiveDecrease) {
            if (liveScoreThreshold > zero && liveScoreThreshold <= HUNDERED) {
                faceThresholdDecimal = new BigDecimal(liveScoreThreshold + "");
                liveScoreThreshold = faceThresholdDecimal.subtract(levelValue).floatValue();
                thLiveEtThreshold.setText(liveScoreThreshold + "");
            }
        } else if (id == R.id.th_LiveIncrease) {
            if (liveScoreThreshold >= zero && liveScoreThreshold < HUNDERED) {
                faceThresholdDecimal = new BigDecimal(liveScoreThreshold + "");
                liveScoreThreshold = faceThresholdDecimal.add(levelValue).floatValue();
                thLiveEtThreshold.setText(liveScoreThreshold + "");
            }
        } else if (id == R.id.th_IDDecrease) {
            if (idScoreThreshold > zero && idScoreThreshold <= HUNDERED) {
                faceThresholdDecimal = new BigDecimal(idScoreThreshold + "");
                idScoreThreshold = faceThresholdDecimal.subtract(levelValue).floatValue();
                thIDEtThreshold.setText(idScoreThreshold + "");
            }
        } else if (id == R.id.th_IDIncrease) {
            if (idScoreThreshold >= zero && idScoreThreshold < HUNDERED) {
                faceThresholdDecimal = new BigDecimal(idScoreThreshold + "");
                idScoreThreshold = faceThresholdDecimal.add(levelValue).floatValue();
                thIDEtThreshold.setText(idScoreThreshold + "");
            }
        } else if (id == R.id.mixture_IDDecrease) {
            if (rgbAndNirScoreThreshold > zero && rgbAndNirScoreThreshold <= HUNDERED) {
                faceThresholdDecimal = new BigDecimal(rgbAndNirScoreThreshold + "");
                rgbAndNirScoreThreshold = faceThresholdDecimal.subtract(levelValue).floatValue();
                mixtureIDEtThreshold.setText(rgbAndNirScoreThreshold + "");
            }
        } else if (id == R.id.mixture_IDIncrease) {
            if (rgbAndNirScoreThreshold >= zero && rgbAndNirScoreThreshold < HUNDERED) {
                faceThresholdDecimal = new BigDecimal(rgbAndNirScoreThreshold + "");
                rgbAndNirScoreThreshold = faceThresholdDecimal.add(levelValue).floatValue();
                mixtureIDEtThreshold.setText(rgbAndNirScoreThreshold + "");
            }
        } else if (id == R.id.cw_cameratype) {
            if (msgTag.equals(getString(R.string.cw_recognizethrehold))) {
                msgTag = "";
                return;
            }
            msgTag = getString(R.string.cw_recognizethrehold);
            cwCameratype.setBackground(getDrawable(R.mipmap.icon_setting_question_hl));
            PWTextUtils.showDescribeText(linerreCognizeThrehold, tvThreshold,
                    GateFaceDetectActivity.this, getString(R.string.cw_recognizethrehold), showWidth, showXLocation);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        showWidth = flsMixtureType.getWidth();
        showXLocation = (int) flRepresent.getLeft();
    }


}