package com.example.settinglibrary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;

import com.example.datalibrary.activity.BaseActivity;
import com.example.datalibrary.utils.PWTextUtils;
import com.example.datalibrary.utils.PreferencesManager;

import java.math.BigDecimal;
import java.text.DecimalFormat;


/**
 * author : shangrong
 * date : two019/five/two7 six:four8 PM
 * description :活体检测模式
 */
public class FaceLivinessTypeActivity extends BaseActivity implements View.OnClickListener {

    private int type;
    private View rgbAndNirView;

   /* 1:rgb活体*/
    private static final int ONE = 1;
    /* 2:rgb+nir活体*/
    private static final int TWO = 2;
    /* 3:rgb+depth活体*/
    private static final int THREE = 3;
    /* 4:rgb+nir+depth活体*/
    private static final int FOUR = 4;

    private Button cwLivetype;
    private Button cwRgb;
    private Button cwRgbAndNir;

    private LinearLayout linerLiveTpye;
    private TextView tvLivType;

    private CheckBox flsRgbLive;
    private CheckBox flsRgbAndNirLive;
    private String msgTag = "";
    private int showWidth;
    private int showXLocation;
    private LinearLayout flRepresent;
    private View rgbView;
    private Switch qcLiving;
    private LinearLayout qcLinerLiving;
    private ImageView qcGestureDecrease;
    private EditText qcGestureEtThreshold;
    private ImageView qcGestureIncrease;
    private int framesThreshold;

    private int ten = 10;
    private int zero = 0;
    // RGB活体阀值
    private ImageView thRgbLiveDecrease;
    private ImageView thRgbLiveIncrease;
    private EditText thRgbLiveEtThreshold;

    // NIR活体阀值
    private ImageView thNirLiveDecrease;
    private ImageView thNirLiveIncrease;
    private EditText thNirLiveEtThreshold;


    private float rgbLiveScore;
    private float nirLiveScore;

    private BigDecimal rgbDecimal;
    private BigDecimal nirDecimal;
    private BigDecimal depthDecimal;
    private BigDecimal nonmoralValue;
    private static final float TEMPLE_VALUE = 0.05f;
    private Button cwLiveThrehold;
    private LinearLayout linerLiveThreshold;
    private TextView tvLive;


    private TextView rgbThresholdTv;
    private TextView nirThresholdTv;
    private ImageView thNirLiveDecreaseAshDisposal;
    private ImageView thNirLiveIncreaseAshDisposal;
    // rgb和nir摄像头宽
    private int rgbAndNirWidth;
    // rgb和nir摄像头高
    private int rgbAndNirHeight;
    // depth摄像头宽
    private int depthWidth;
    // depth摄像头高
    private int depthHeight;
    // 是否开启活体检测开关
    private boolean livingControl;
    private int cameraType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gate_facelivinesstype);
        init();
    }

    public void init() {
        // 获取Intent对象
        Intent intent = getIntent();
        framesThreshold = intent.getIntExtra("framesThreshold" , 3);
        rgbLiveScore = intent.getFloatExtra("rgbLiveScore" , 0.8f);
        nirLiveScore = intent.getFloatExtra("nirLiveScore" , 0.8f);
        type = intent.getIntExtra("type" , 1);
        cameraType = intent.getIntExtra("cameraType" , 0);
        livingControl = intent.getBooleanExtra("livingControl" , true);
        rgbAndNirWidth = intent.getIntExtra("rgbAndNirWidth" , 640);
        rgbAndNirHeight = intent.getIntExtra("rgbAndNirHeight" , 480);
        depthWidth = intent.getIntExtra("depthWidth" , 640);
        depthHeight = intent.getIntExtra("depthHeight" , 400);
        flRepresent = findViewById(R.id.flRepresent);
        rgbView = findViewById(R.id.rgbView);
        linerLiveTpye = findViewById(R.id.linerlivetpye);
        tvLivType = findViewById(R.id.tvlivetype);

        cwLivetype = findViewById(R.id.cw_livetype);
        cwLivetype.setOnClickListener(this);
        cwRgb = findViewById(R.id.cw_rgb);
        cwRgb.setOnClickListener(this);
        cwRgbAndNir = findViewById(R.id.cw_rgbandnir);
        cwRgbAndNir.setOnClickListener(this);

        flsRgbLive = findViewById(R.id.fls_rgb_live);
        flsRgbAndNirLive = findViewById(R.id.fls_rgbandnir_live);
        // 返回
        ImageView flsSave = findViewById(R.id.fls_save);
        flsSave.setOnClickListener(this);
        // 活体检测开关
        qcLiving = findViewById(R.id.qc_Living);
        qcLinerLiving = findViewById(R.id.qc_LinerLiving);

        rgbAndNirView = findViewById(R.id.rgbAndNirView);
        // 帧数阈值
        qcGestureDecrease = findViewById(R.id.qc_GestureDecrease);
        qcGestureDecrease.setOnClickListener(this);
        qcGestureEtThreshold = findViewById(R.id.qc_GestureEtThreshold);
        qcGestureIncrease = findViewById(R.id.qc_GestureIncrease);
        qcGestureIncrease.setOnClickListener(this);
        // rgb活体
        thRgbLiveDecrease = findViewById(R.id.th_RgbLiveDecrease);
        thRgbLiveDecrease.setOnClickListener(this);
        thRgbLiveIncrease = findViewById(R.id.th_RgbLiveIncrease);
        thRgbLiveIncrease.setOnClickListener(this);
        thRgbLiveEtThreshold = findViewById(R.id.th_RgbLiveEtThreshold);
        // nir活体
        thNirLiveDecrease = findViewById(R.id.th_NirLiveDecrease);
        thNirLiveDecrease.setOnClickListener(this);
        thNirLiveIncrease = findViewById(R.id.th_NirLiveIncrease);
        thNirLiveIncrease.setOnClickListener(this);
        thNirLiveEtThreshold = findViewById(R.id.th_NirLiveEtThreshold);

        cwLiveThrehold = findViewById(R.id.cw_livethrehold);
        cwLiveThrehold.setOnClickListener(this);
        linerLiveThreshold = findViewById(R.id.linerlivethreshold);
        tvLive = findViewById(R.id.tvlive);

        nonmoralValue = new BigDecimal(TEMPLE_VALUE + "");

        rgbThresholdTv = findViewById(R.id.rgb_thresholdTv);
        nirThresholdTv = findViewById(R.id.nir_thresholdTv);
        thNirLiveDecreaseAshDisposal = findViewById(R.id.th_NirLiveDecrease_Ash_disposal);
        thNirLiveIncreaseAshDisposal = findViewById(R.id.th_NirLiveIncrease_Ash_disposal);

        PWTextUtils.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDismiss() {
                cwLivetype.setBackground(getDrawable(R.mipmap.icon_setting_question));
                cwRgb.setBackground(getDrawable(R.mipmap.icon_setting_question));
                cwRgbAndNir.setBackground(getDrawable(R.mipmap.icon_setting_question));
                cwLiveThrehold.setBackground(getDrawable(R.mipmap.icon_setting_question));
            }
        });

        qcLiving.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (type == 1) {
                        flsRgbLive.setChecked(true);
                    } else if (type == 2) {
                        flsRgbAndNirLive.setChecked(true);
                    } else {
                        type = 1;
                        flsRgbLive.setChecked(true);
                    }
                    qcLiving.setChecked(true);
                    livingControl = true;
                    qcLinerLiving.setVisibility(View.VISIBLE);
                } else {
                    qcLiving.setChecked(false);
                    livingControl = false;
                    qcLinerLiving.setVisibility(View.INVISIBLE);
                    justify();
                }
            }
        });

        flsRgbLive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    // 镜头类型
                    flsRgbLive.setChecked(true);
                    flsRgbLive.setEnabled(false);
                    flsRgbAndNirLive.setChecked(false);
                    type = ONE;
                    // nir 置灰
                    nirThresholdTv.setTextColor(getResources().getColor(R.color.hui_color));
                    thNirLiveDecrease.setVisibility(View.GONE);
                    thNirLiveEtThreshold.setTextColor(getResources().getColor(R.color.hui_color));
                    thNirLiveIncrease.setVisibility(View.GONE);
                    thNirLiveDecreaseAshDisposal.setVisibility(View.VISIBLE);
                    thNirLiveIncreaseAshDisposal.setVisibility(View.VISIBLE);
                    justify();

                } else {
                    flsRgbLive.setEnabled(true);
                }
            }
        });
        flsRgbAndNirLive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    flsRgbAndNirLive.setChecked(true);
                    flsRgbAndNirLive.setEnabled(false);
                    flsRgbLive.setChecked(false);
                    type = TWO;

                    nirThresholdTv.setTextColor(getResources().getColor(R.color.white));
                    thNirLiveDecrease.setVisibility(View.VISIBLE);
                    thNirLiveEtThreshold.setTextColor(getResources().getColor(R.color.white));
                    thNirLiveIncrease.setVisibility(View.VISIBLE);
                    thNirLiveDecreaseAshDisposal.setVisibility(View.GONE);
                    thNirLiveIncreaseAshDisposal.setVisibility(View.GONE);
                    justify();


                } else {
                    flsRgbAndNirLive.setEnabled(true);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

//        if (gateChangeLensBtn.getVisibility() == View.VISIBLE) {
            // 镜头类型
            PreferencesManager.getInstance(this.getApplicationContext())
                    .setRgbDepth(cameraType);

//        }

//        if (gateChangeLensBtnTwo.getVisibility() == View.VISIBLE) {
//            // 镜头类型
//            int cameraType = SingleBaseConfig.getBaseConfig().getCameraType();
            PreferencesManager.getInstance(this.getApplicationContext())
                    .setRgbNirDepth(cameraType);


        if (livingControl) {
            qcLiving.setChecked(true);
            qcLinerLiving.setVisibility(View.VISIBLE);
        } else {
            qcLiving.setChecked(false);
            qcLinerLiving.setVisibility(View.INVISIBLE);
        }

        qcGestureEtThreshold.setText(framesThreshold + "");
        thRgbLiveEtThreshold.setText(roundByScale(rgbLiveScore));
        thNirLiveEtThreshold.setText(roundByScale(nirLiveScore));


        if (type == ONE) {
            flsRgbLive.setChecked(true);
            flsRgbAndNirLive.setChecked(false);
        }
        if (type == TWO) {
            flsRgbAndNirLive.setChecked(true);
            flsRgbLive.setChecked(false);
        }
        if (type == THREE) {
            flsRgbLive.setChecked(false);
            flsRgbAndNirLive.setChecked(false);
        }
        if (type == FOUR) {
            flsRgbLive.setChecked(false);
            flsRgbAndNirLive.setChecked(false);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        showWidth = flRepresent.getWidth();
        showXLocation = (int) flRepresent.getLeft();
    }

    @SuppressLint("NewApi")
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.fls_save) {
            if (qcLiving.isChecked()) {
                livingControl = true;
            } else {
                livingControl = false;
                type = zero;
            }
            framesThreshold = Integer.valueOf(qcGestureEtThreshold.getText().toString());
            rgbLiveScore = Float.parseFloat(thRgbLiveEtThreshold.getText().toString());
            nirLiveScore = Float.parseFloat(thNirLiveEtThreshold.getText().toString());

            justify();
            finish();
        } else if (id == R.id.cw_livetype) {
            if (msgTag.equals(getString(R.string.cw_livedetecttype))) {
                msgTag = "";
                return;
            }
            msgTag = getString(R.string.cw_livedetecttype);
            cwLivetype.setBackground(getDrawable(R.mipmap.icon_setting_question_hl));
            PWTextUtils.showDescribeText(linerLiveTpye, tvLivType, FaceLivinessTypeActivity.this,
                    getString(R.string.cw_livedetecttype)
                    , showWidth, showXLocation);
        } else if (id == R.id.cw_rgb) {
            if (msgTag.equals(getString(R.string.cw_rgblive))) {
                msgTag = "";
                return;
            }
            msgTag = getString(R.string.cw_rgblive);
            cwRgb.setBackground(getDrawable(R.mipmap.icon_setting_question_hl));
            PWTextUtils.showDescribeText(rgbView, rgbView, FaceLivinessTypeActivity.this,
                    getString(R.string.cw_rgblive)
                    , showWidth, 0);
        } else if (id == R.id.cw_rgbandnir) {
            if (msgTag.equals(getString(R.string.cw_rgbandnir))) {
                msgTag = "";
                return;
            }
            msgTag = getString(R.string.cw_rgbandnir);
            cwRgbAndNir.setBackground(getDrawable(R.mipmap.icon_setting_question_hl));
            PWTextUtils.showDescribeText(rgbAndNirView, rgbAndNirView,
                    FaceLivinessTypeActivity.this, getString(R.string.cw_rgbandnir)
                    , showWidth, 0);
        }else if (id == R.id.qc_GestureDecrease) {
            if (framesThreshold > ONE && framesThreshold <= ten) {
                framesThreshold = framesThreshold - 1;
                qcGestureEtThreshold.setText(framesThreshold + "");
            }
            // 加
        } else if (id == R.id.qc_GestureIncrease) {
            if (framesThreshold >= ONE && framesThreshold < ten) {
                framesThreshold = framesThreshold + 1;
                qcGestureEtThreshold.setText(framesThreshold + "");
            }
        } else if (id == R.id.th_RgbLiveDecrease) {
            if (rgbLiveScore > zero && rgbLiveScore <= ONE) {
                rgbDecimal = new BigDecimal(rgbLiveScore + "");
                rgbLiveScore = rgbDecimal.subtract(nonmoralValue).floatValue();
                thRgbLiveEtThreshold.setText(roundByScale(rgbLiveScore));
            }
        } else if (id == R.id.th_RgbLiveIncrease) {
            if (rgbLiveScore >= zero && rgbLiveScore < ONE) {
                rgbDecimal = new BigDecimal(rgbLiveScore + "");
                rgbLiveScore = rgbDecimal.add(nonmoralValue).floatValue();
                thRgbLiveEtThreshold.setText(roundByScale(rgbLiveScore));
            }
        } else if (id == R.id.th_NirLiveDecrease) {
            if (nirLiveScore > zero && nirLiveScore <= ONE) {
                nirDecimal = new BigDecimal(nirLiveScore + "");
                nirLiveScore = nirDecimal.subtract(nonmoralValue).floatValue();
                thNirLiveEtThreshold.setText(roundByScale(nirLiveScore));
            }
        } else if (id == R.id.th_NirLiveIncrease) {
            if (nirLiveScore >= zero && nirLiveScore < ONE) {
                nirDecimal = new BigDecimal(nirLiveScore + "");
                nirLiveScore = nirDecimal.add(nonmoralValue).floatValue();
                thNirLiveEtThreshold.setText(roundByScale(nirLiveScore));
            }
        } else if (id == R.id.cw_livethrehold) {
            if (msgTag.equals(getString(R.string.cw_livethrehold))) {
                msgTag = "";
                return;
            }
            msgTag = getString(R.string.cw_livethrehold);
            cwLiveThrehold.setBackground(getDrawable(R.mipmap.icon_setting_question_hl));
            PWTextUtils.showDescribeText(linerLiveThreshold, tvLive, FaceLivinessTypeActivity.this,
                    getString(R.string.cw_livethrehold), showWidth, showXLocation);
        }
    }

    @Override
    public void finish() {

        Intent intent = new Intent();
        intent.putExtra("framesThreshold", framesThreshold);
        intent.putExtra("rgbLiveScore", rgbLiveScore);
        intent.putExtra("nirLiveScore", nirLiveScore);
        intent.putExtra("type", type);
        intent.putExtra("cameraType", cameraType);
        intent.putExtra("rgbAndNirWidth", rgbAndNirWidth);
        intent.putExtra("rgbAndNirHeight", rgbAndNirHeight);
        intent.putExtra("depthWidth", depthWidth);
        intent.putExtra("depthHeight", depthHeight);
        intent.putExtra("livingControl", livingControl);
        // 设置返回码和返回携带的数据
        setResult(Activity.RESULT_OK, intent);
        super.finish();
    }

    public void justify() {
        if (type == ONE) {
            type = ONE;
            rgbAndNirWidth = 640;
            rgbAndNirHeight = 480;
        }
        if (type == TWO) {
            type = TWO;
            rgbAndNirWidth = 640;
            rgbAndNirHeight = 480;
        }
        if (type == THREE) {
            type = THREE;
        }
        if (type == FOUR) {
            type = FOUR;
        }
        if (type == zero) {
            type = 0;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK){
            return;
        }
        switch (requestCode) {
            case 100: // 返回的结果是来自于Activity B
                type = data.getIntExtra("type" , 0);
                cameraType = data.getIntExtra("cameraType" , 0);
                rgbAndNirWidth = data.getIntExtra("rgbAndNirWidth" , 640);
                rgbAndNirHeight = data.getIntExtra("rgbAndNirHeight" , 480);
                depthWidth = data.getIntExtra("depthWidth" , 640);
                depthHeight = data.getIntExtra("depthHeight" , 400);
                break;
        }
    }

    public static String roundByScale(float numberValue) {
        // 构造方法的字符格式这里如果小数不足2位,会以0补足.
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        // format 返回的是字符串
        String resultNumber = decimalFormat.format(numberValue);
        return resultNumber;
    }
}
