package com.baidu.idl.face.main.activity.start;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.idl.face.main.activity.BaseActivity;
import com.baidu.idl.face.main.attribute.activity.attribute.FaceAttributeRgbActivity;
import com.baidu.idl.facesdkdemo.R;
import com.baidu.idl.main.facesdk.activity.gate.FaceOneToNActivity;
import com.baidu.idl.main.facesdk.identifylibrary.activity.FaceOneFoOneActivity;
import com.baidu.idl.main.facesdk.registerlibrary.user.activity.UserManagerActivity;
import com.baidu.idl.main.facesdk.registerlibrary.user.register.FaceRegisterNewNIRActivity;
import com.baidu.idl.main.facesdk.utils.FaceUtils;
import com.baidu.idl.main.facesdk.utils.StreamUtil;
import com.example.datalibrary.api.FaceApi;
import com.example.datalibrary.listener.DBLoadListener;
import com.example.datalibrary.listener.SdkInitListener;
import com.example.datalibrary.manager.FaceSDKManager;
import com.example.datalibrary.model.User;
import com.example.datalibrary.utils.DensityUtils;
import com.example.datalibrary.utils.ToastUtils;
import com.example.datalibrary.view.PreviewTexture;
import com.example.settinglibrary.SettingActivity;
import com.example.datalibrary.threshold.SingleBaseConfig;
import com.example.settinglibrary.utils.ConfigUtils;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class HomeActivity extends BaseActivity implements View.OnClickListener, FaceRecyclerAdapter.FaceRecyclerOnclickListener {
    private Context mContext;
    private final Handler mHandler = new Handler();
    private PopupWindow popupWindow;
    private View view1;
    private RelativeLayout layout_home;
    private PopupWindow mPopupMenu;
    private PopupWindow mPopupMenuFirst;
    private ImageView home_menuImg;
    private boolean isCheck = false;
    private boolean isClickActivity = false;
    private static final int PREFER_WIDTH = 640;
    private static final int PREFER_HEIGHT = 480;
    private PreviewTexture[] previewTextures;
    private Camera[] mCamera;
    private TextureView checkRBGTexture;
    private TextureView checkNIRTexture;
    private ProgressBar progressBar;
    private TextView progressText;
    private View progressGroup;
    private boolean isDBLoad;
    private Future future;
    private final int[] items = new int[]{R.string.home_gate, R.string.home_check, R.string.home_pay, R.string.home_person, R.string.home_attribute};

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mContext = this;
        initView();

        initRGBCheck();
        initListener();
        SharedPreferences sharedPreferences = this.getSharedPreferences("share", MODE_PRIVATE);
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (isFirstRun) {
            mHandler.postDelayed(mRunnable, 500);
            editor.putBoolean("isFirstRun", false);
            editor.commit();
        }
        initUserManagePopupWindow();
    }

    private void initListener() {
        if (FaceSDKManager.initStatus != FaceSDKManager.SDK_MODEL_LOAD_SUCCESS) {
            FaceSDKManager.getInstance().initModel(mContext,
                    FaceUtils.getInstance().getBDFaceSDKConfig(), new SdkInitListener() {
                        @Override
                        public void initStart() {
                        }

                        @Override
                        public void initLicenseSuccess() {
                        }

                        @Override
                        public void initLicenseFail(int errorCode, String msg) {
                        }

                        @Override
                        public void initModelSuccess() {
                            initDBApi();
                            FaceSDKManager.initModelSuccess = true;
                            ToastUtils.toast(mContext, getResources().getString(R.string.toast_model_loaded_success));
                        }

                        @Override
                        public void initModelFail(int errorCode, String msg) {
                            FaceSDKManager.initModelSuccess = false;
                            if (errorCode != -12) {
                                ToastUtils.toast(mContext, getResources().getString(R.string.toast_model_loaded_failed));
                            }
                        }
                    });
        }else {
            initDBApi();
            FaceSDKManager.initModelSuccess = true;
        }
    }

    private void initDBApi() {

        if (future != null && !future.isDone()) {
            future.cancel(true);
        }
        isDBLoad = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressGroup.setVisibility(View.VISIBLE);
            }
        });
        future = Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                FaceApi.getInstance().init(new DBLoadListener() {

                    @Override
                    public void onStart(int successCount) {
                        if (successCount < 5000 && successCount != 0) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadProgress(10);
                                }
                            });
                        }
                    }

                    @Override
                    public void onLoad(final int finishCount, final int successCount, final float progress) {
                        if (successCount > 5000 || successCount == 0) {
                            runOnUiThread(new Runnable() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void run() {
                                    progressBar.setProgress((int) (progress * 100));
                                    progressText.setText(((int) (progress * 100)) + "%");
                                }
                            });
                        }
                    }

                    @Override
                    public void onComplete(final List<User> users, final int successCount) {
//                        FileUtils.saveDBList(HomeActivity.this, users);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                FaceApi.getInstance().setUsers(users);
                                if (successCount > 5000 || successCount == 0) {
                                    progressGroup.setVisibility(View.GONE);
                                    isDBLoad = true;
                                }
                            }
                        });
                    }

                    @Override
                    public void onFail(final int finishCount, final int successCount, final List<User> users) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                FaceApi.getInstance().setUsers(users);
                                progressGroup.setVisibility(View.GONE);
                                ToastUtils.toast(HomeActivity.this,
                                        getResources().getString(R.string.toast_face_db_load_failed) + successCount +
                                                getResources().getString(R.string.toast_face_db_load_data_count) + finishCount +
                                                getResources().getString(R.string.toast_face_db_load_data_unit));
                                isDBLoad = true;
                            }
                        });
                    }
                }, mContext);
            }
        });
    }

    private void loadProgress(float i) {
        new Handler().postDelayed(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                progressBar.setProgress((int) ((i / 5000f) * 100));
                progressText.setText(((int) ((i / 5000f) * 100)) + "%");
                if (i < 5000) {
                    loadProgress(i + 100);
                } else {
                    progressGroup.setVisibility(View.GONE);
                    isDBLoad = true;
                }
            }
        }, 10);
    }

    private void initFirstPopupWindowTip() {
        home_menuImg.setImageResource(R.mipmap.icon_titlebar_menu_first);
        @SuppressLint("InflateParams") View contentView = LayoutInflater.from(mContext).inflate(R.layout.popup_menu_home_first, null);
        mPopupMenuFirst = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupMenuFirst.setFocusable(true);
        mPopupMenuFirst.setOutsideTouchable(true);
        mPopupMenuFirst.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_round_frist));

        mPopupMenuFirst.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                isCheck = false;
                home_menuImg.setImageResource(R.mipmap.icon_titlebar_menu);
            }
        });
        mPopupMenuFirst.setContentView(contentView);

        if (mPopupMenuFirst != null && home_menuImg != null) {
            int marginRight = DensityUtils.dip2px(mContext, 20);
            int marginTop = DensityUtils.dip2px(mContext, 56);
            mPopupMenuFirst.showAtLocation(home_menuImg, Gravity.END | Gravity.TOP,
                    marginRight, marginTop);
        }
    }

    @Override
    protected void onResume() {
        isClickActivity = false;
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        release(0);
        release(1);

        if (future != null && !future.isDone()) {
            future.cancel(true);
        }
    }

    private void initRGBCheck() {
        if (isSetCameraId()) {
            return;
        }
        int mCameraNum = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, cameraInfo);
        }
        if (mCameraNum > 1) {
            try {
                mCamera = new Camera[mCameraNum];
                previewTextures = new PreviewTexture[mCameraNum];
                mCamera[0] = Camera.open(0);
                previewTextures[0] = new PreviewTexture(this, checkRBGTexture);
                previewTextures[0].setCamera(mCamera[0], PREFER_WIDTH, PREFER_HEIGHT);
                mCamera[0].setPreviewCallback(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera) {
                        int check = StreamUtil.checkNirRgb(data, PREFER_WIDTH, PREFER_HEIGHT);
                        if (check == 1) {
                            setRgbCameraId(0);
                        }
                        release(0);
                    }
                });
            } catch (Exception ignored) {
            }
            try {
                mCamera[1] = Camera.open(1);
                previewTextures[1] = new PreviewTexture(this, checkNIRTexture);
                previewTextures[1].setCamera(mCamera[1], PREFER_WIDTH, PREFER_HEIGHT);
                mCamera[1].setPreviewCallback(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera) {
                        int check = StreamUtil.checkNirRgb(data, PREFER_WIDTH, PREFER_HEIGHT);
                        if (check == 1) {
                            setRgbCameraId(1);
                        }
                        release(1);
                    }
                });
            } catch (Exception ignored) {
            }
        } else {
            setRgbCameraId(0);
        }
    }

    private void setRgbCameraId(int index) {
        SingleBaseConfig.getBaseConfig().setRBGCameraId(index);
        ConfigUtils.modityJson(getApplicationContext());

    }

    private boolean isSetCameraId() {
        return SingleBaseConfig.getBaseConfig().getRBGCameraId() != -1;
    }

    private void release(int id) {
        if (mCamera != null && mCamera[id] != null) {
            mCamera[id].setPreviewCallback(null);
            mCamera[id].stopPreview();
            previewTextures[id].release();
            mCamera[id].release();
            mCamera[id] = null;
        }
    }

    private final Runnable mRunnable = new Runnable() {
        public void run() {
            // 弹出PopupWindow的具体代码
            initPopupWindow();
            initFirstPopupWindowTip();
        }
    };

    private void initPopupWindow() {

        view1 = View.inflate(mContext, R.layout.layout_popup, null);
        popupWindow = new PopupWindow(view1, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        // 点击框外可以使得popupwindow消失
        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(layout_home, Gravity.CENTER, 0, 0);
        initHandler();
    }

    /**
     * 初始化用户管理PopupWindow
     */
    private void initUserManagePopupWindow() {
        @SuppressLint("InflateParams") View contentView = LayoutInflater.from(mContext).inflate(R.layout.popup_menu_home, null);
        mPopupMenu = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupMenu.setFocusable(true);
        mPopupMenu.setOutsideTouchable(true);
        mPopupMenu.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_round));

        RelativeLayout relativeRegister = contentView.findViewById(R.id.relative_register);
        RelativeLayout mPopRelativeManager = contentView.findViewById(R.id.relative_manager);
        relativeRegister.setOnClickListener(this);
        mPopRelativeManager.setOnClickListener(this);
        mPopupMenu.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                isCheck = false;
                home_menuImg.setImageResource(R.mipmap.icon_titlebar_menu);
            }
        });
        // 设置
        contentView.findViewById(R.id.relative_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(HomeActivity.this, SettingActivity.class));
                dismissPopupWindow();
            }
        });
        mPopupMenu.setContentView(contentView);
    }

    private void showPopupWindow(ImageView imageView) {
        if (mPopupMenu != null && imageView != null) {
            int marginRight = DensityUtils.dip2px(mContext, 20);
            int marginTop = DensityUtils.dip2px(mContext, 56);
            mPopupMenu.showAtLocation(imageView, Gravity.END | Gravity.TOP,
                    marginRight, marginTop);
        }
    }

    private void dismissPopupWindow() {
        if (mPopupMenu != null) {
            mPopupMenu.dismiss();
        }
    }

    private void initHandler() {
        new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                // 实现页面跳转
                popupWindow.dismiss();
                return false;
            }
        }).sendEmptyMessageDelayed(0, 3000);
    }

    private void initView() {
        layout_home = findViewById(R.id.layout_home);
        ImageView home_settingImg = findViewById(R.id.home_settingImg);
        home_settingImg.setOnClickListener(this);
        home_menuImg = findViewById(R.id.home_menuImg);
        home_menuImg.setOnClickListener(this);
        checkRBGTexture = findViewById(R.id.check_rgb_texture);
        checkNIRTexture = findViewById(R.id.check_nir_texture);
        progressBar = findViewById(R.id.progress_bar);
        progressText = findViewById(R.id.progress_text);
        progressGroup = findViewById(R.id.progress_group);
        RecyclerView recyclerView = findViewById(R.id.faceRecycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        FaceRecyclerAdapter faceRecyclerAdapter = new FaceRecyclerAdapter(items);
        recyclerView.setAdapter(faceRecyclerAdapter);
        faceRecyclerAdapter.setFaceRecyclerOnclickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        if (!FaceSDKManager.initModelSuccess) {
            ToastUtils.toast(this, getResources().getString(R.string.toast_model_not_initialized));
            return;
        }
        if (!isDBLoad) {
            return;
        }
        int mLiveType;
        switch (view.getId()) {
            case R.id.home_menuImg:
                if (!isCheck) {
                    isCheck = true;
                    home_menuImg.setImageResource(R.mipmap.icon_titlebar_menu_hl);
                    showPopupWindow(home_menuImg);
                } else {
                    dismissPopupWindow();
                }
                break;
            case R.id.relative_register: // 人脸注册
                if (isClickActivity) {
                    return;
                }
                isClickActivity = true;
                dismissPopupWindow();
                startActivity(new Intent(HomeActivity.this, FaceRegisterNewNIRActivity.class));
                break;
            case R.id.relative_manager: // 人脸库管理
                if (isClickActivity) {
                    return;
                }
                isClickActivity = true;
                dismissPopupWindow();
                startActivity(new Intent(HomeActivity.this, UserManagerActivity.class));
                break;
        }
    }

    @Override
    public void onClick(int position) {
        if (!FaceSDKManager.initModelSuccess) {
            ToastUtils.toast(this, getResources().getString(R.string.toast_model_not_initialized));
            return;
        }
        if (!isDBLoad) {
            return;
        }
        if (position == 0 || position == 1 || position == 2) {
            if (isClickActivity) {
                return;
            }
            isClickActivity = true;
            // 闸机模块
            Intent intent = new Intent(HomeActivity.this, FaceOneToNActivity.class);
            intent.putExtra("state", position);
            intent.putExtra("isGarden", position == 2);
            startActivity(intent);
        } else if (position == 3) {
            if (isClickActivity) {
                return;
            }
            isClickActivity = true;
            // 人证核验
            Intent intent = new Intent(HomeActivity.this, FaceOneFoOneActivity.class);
            intent.putExtra("state", position);
            startActivity(intent);
        }else if (position == 4){

            if (isClickActivity) {
                return;
            }
            isClickActivity = true;
            // 属性模式
            Intent intent = new Intent(HomeActivity.this, FaceAttributeRgbActivity.class);
            startActivity(intent);
        }
    }

}
