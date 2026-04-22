package com.baidu.idl.main.facesdk.registerlibrary.user.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.CallSuper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.idl.main.facesdk.registerlibrary.R;
import com.baidu.idl.main.facesdk.registerlibrary.user.adapter.FaceUserAdapter;
import com.baidu.idl.main.facesdk.registerlibrary.user.manager.ShareManager;
import com.baidu.idl.main.facesdk.registerlibrary.user.manager.UserInfoManager;
import com.baidu.idl.main.facesdk.registerlibrary.user.register.FaceRegisterNewNIRActivity;
import com.baidu.idl.main.facesdk.registerlibrary.user.view.TipDialog;
import com.example.datalibrary.activity.BaseActivity;
import com.example.datalibrary.api.FaceApi;
import com.example.datalibrary.listener.DBLoadListener;
import com.example.datalibrary.listener.OnItemClickListener;
import com.example.datalibrary.listener.OnRemoveListener;
import com.example.datalibrary.manager.FaceSDKManager;
import com.example.datalibrary.model.KeyboardsUtils;
import com.example.datalibrary.model.User;
import com.example.datalibrary.utils.DensityUtils;
import com.example.datalibrary.utils.ToastUtils;
import com.example.datalibrary.threshold.SingleBaseConfig;

import java.util.List;

/**
 * 用户管理页面
 * Created by liujialu on 2020/02/10.
 */

public class UserManagerActivity extends BaseActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener, OnItemClickListener,
        TipDialog.OnTipDialogClickListener, OnRemoveListener {
    private static final String TAG = UserManagerActivity.class.getName();

    // view
    private RecyclerView mRecyclerUserManager;
    private ImageView mImageIconSearch;        // title中的搜索按钮
    private RelativeLayout mRelativeStandard; // title中的常规布局
    private LinearLayout mLinearSearch;       // title中的搜索布局
    private ImageView mImageMenu;             // title中的菜单
    private boolean isCheck = false;            // title菜单点击状态
    private TextView mTextCancel;             // title中的取消按钮
    private RelativeLayout mRelativeEmpty;   // 暂无内容
    private TextView mTextEmpty;
    private EditText mEditTitleSearch;

    private PopupWindow mPopupMenu;
    private RelativeLayout mRelativeDelete;   // 删除布局
    private CheckBox mCheckAll;               // 全选
    private TextView mTextDelete;             // 删除
    private TipDialog mTipDialog;

    // pop
    private RelativeLayout mPopRelativeImport;

    private Context mContext;
    private UserListListener mUserListListener;
    private FaceUserAdapter mFaceUserAdapter;
    private List<User> mUserInfoList;
    private int mSelectCount;                // 选中的个数
    private boolean mIsLongClick;           // 是否是长按
    private int mLiveType;

    private ProgressBar progressBar;
    private TextView progressText;
    private View progressGroup;
    private boolean isDBLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        FaceSDKManager.getInstance().initDataBases(this);
        setContentView(R.layout.activity_user_manager);
        initView();
        initData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // 读取数据库，获取用户信息
        UserInfoManager.getInstance().getUserListInfo(null, mUserListListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 数据变化，更新内存
//        FaceSDKManager.getInstance().initDatabases();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void initView() {
        mRecyclerUserManager = findViewById(R.id.recycler_user_manager);
        mRecyclerUserManager.setOnClickListener(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerUserManager.setLayoutManager(layoutManager);
        // title相关的控件
        mRelativeStandard = findViewById(R.id.relative_standard);
        mLinearSearch = findViewById(R.id.linear_title_search);
        mImageIconSearch = findViewById(R.id.image_icon_research);
        mImageIconSearch.setOnClickListener(this);
        mImageMenu = findViewById(R.id.image_menu);
        mImageMenu.setOnClickListener(this);
        mTextCancel = findViewById(R.id.text_cancel);
        mTextCancel.setOnClickListener(this);

        mRelativeEmpty = findViewById(R.id.relative_empty);
        mTextEmpty = findViewById(R.id.text_empty);
        // 删除相关的控件
        mRelativeDelete = findViewById(R.id.relative_botton_delete);
        mRelativeDelete.setOnClickListener(this);
        mCheckAll = findViewById(R.id.check_all);
        mCheckAll.setOnCheckedChangeListener(this);
        mTextDelete = findViewById(R.id.text_delete);
        mTextDelete.setOnClickListener(this);
        // title中的搜索框取消按钮
        Button btnTitleCancel = findViewById(R.id.btn_title_cancel);
        btnTitleCancel.setOnClickListener(this);
        ImageView imageBack = findViewById(R.id.image_back);
        imageBack.setOnClickListener(this);
        ImageView imageInputDelete = findViewById(R.id.image_input_delete);
        imageInputDelete.setOnClickListener(this);
        // 初始化PopupWindow
        initPopupWindow();
        mTipDialog = new TipDialog(mContext);
        mTipDialog.setOnTipDialogClickListener(this);

        mFaceUserAdapter = new FaceUserAdapter();
        mRecyclerUserManager.setAdapter(mFaceUserAdapter);
        mFaceUserAdapter.setItemClickListener(this);
        mFaceUserAdapter.setOnRemoveListener(this);

        progressBar = findViewById(R.id.progress_bar);
        progressText = findViewById(R.id.progress_text);
        progressGroup = findViewById(R.id.progress_group);

        // title中的搜索框
        mEditTitleSearch = findViewById(R.id.edit_title_search);
        // 搜索框输入监听事件
        mEditTitleSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mUserListListener != null && s != null) {
                    // 读取数据库，获取用户信息
                    UserInfoManager.getInstance().getUserListInfo(s.toString(), mUserListListener);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 初始化PopupWindow
     */
    private void initPopupWindow() {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.userpopup_menu, null);
        mPopupMenu = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupMenu.setFocusable(true);
        mPopupMenu.setOutsideTouchable(true);
        mPopupMenu.setBackgroundDrawable(getResources().getDrawable(R.drawable.menu_round));

        RelativeLayout relativeRegister = contentView.findViewById(R.id.relative_register);
        mPopRelativeImport = contentView.findViewById(R.id.relative_import);
        RelativeLayout relativeDelete = contentView.findViewById(R.id.relative_delete);

        relativeRegister.setOnClickListener(this);
        mPopRelativeImport.setOnClickListener(this);
        relativeDelete.setOnClickListener(this);
        mPopupMenu.setContentView(contentView);
    }

    private void initData() {
        mLiveType = SingleBaseConfig.getBaseConfig().getType();
        mUserListListener = new UserListListener();
        // 读取数据库，获取用户信息
        UserInfoManager.getInstance().getUserListInfo(null, mUserListListener);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_title_cancel) {  // 取消按钮
            mEditTitleSearch.setText("");
            mLinearSearch.setVisibility(View.GONE);
            mRelativeStandard.setVisibility(View.VISIBLE);
            UserInfoManager.getInstance().getUserListInfo(null, mUserListListener);
        } else if (id == R.id.image_icon_research) {  // 搜索按钮
            if (!isDBLoad){
                return;
            }
            mLinearSearch.setVisibility(View.VISIBLE);
            mRelativeStandard.setVisibility(View.GONE);
        } else if (id == R.id.image_back) {
            if (FaceApi.getInstance().isDelete){
                mTipDialog = new TipDialog(mContext);
                mTipDialog.setOnTipDialogClickListener(new TipDialog.OnTipDialogClickListener() {
                    @Override
                    public void onCancel() {
                        mTipDialog.dismiss();
                    }

                    @Override
                    public void onConfirm(String tipType) {
                        mTipDialog.dismiss();

                        UserInfoManager.getInstance().release();
                        // 返回
                        finish();
                    }
                });
                mTipDialog.show();
                mTipDialog.setTextMessage(getResources().getString(R.string.dialog_return_message));
                mTipDialog.setTextConfirm(getResources().getString(R.string.dialog_return_confirm));
                mTipDialog.setTextCancel(getResources().getString(R.string.dialog_return_cancel));
                mTipDialog.setTextTitle(getResources().getString(R.string.dialog_return_title));
            } else {

                UserInfoManager.getInstance().release();
                // 返回
                finish();
            }
        } else if (id == R.id.image_menu) {
            if (!isDBLoad){
                return;
            }
            if (!isCheck) {
                isCheck = true;
                mImageMenu.setImageResource(R.mipmap.icon_titlebar_menu_hl);
                showPopupWindow(mImageMenu);
            }
        } else if (id == R.id.relative_register) {      // 进入注册页面
            if (!isDBLoad){
                return;
            }
            dismissPopupWindow();
            startActivity(new Intent(UserManagerActivity.this, FaceRegisterNewNIRActivity.class));
        } else if (id == R.id.relative_import) {        // 进入批量导入页面
            if (!isDBLoad){
                return;
            }
            dismissPopupWindow();
            Intent intent2 = new Intent(mContext, BatchImportActivity.class);
            startActivity(intent2);
        } else if (id == R.id.relative_delete) {       // 显示删除UI
            if (!isDBLoad){
                return;
            }
            dismissPopupWindow();
            updateDeleteUI(true);
        } else if (id == R.id.text_cancel) {           // 批量删除取消
            if (!isDBLoad){
                return;
            }
            updateDeleteUI(false);
        } else if (id == R.id.text_delete) {           // 批量删除
            if (!isDBLoad){
                return;
            }
            if (mSelectCount == 0) {
                ToastUtils.toast(getApplicationContext(), getResources().getString(R.string.tips_select_users_to_be_deleted));
                return;
            }
            mTipDialog.show();
            mTipDialog.setTextTitle(getResources().getString(R.string.dialog_delete_title));
            mTipDialog.setTextMessage(getResources().getString(R.string.dialog_delete_clear_message));
            mTipDialog.setTextConfirm(getResources().getString(R.string.button_delete) + "(" + mSelectCount + ")");
            mTipDialog.setCancelable(false);
        } else if (id == R.id.image_input_delete) {
            mEditTitleSearch.setText("");
        } else if (id == R.id.recycler_user_manager) {
            KeyboardsUtils.hintKeyBoards(v);
        } else if (id == R.id.relative_botton_delete) {
            Log.e(TAG, "relative_botton_delete");
        }
    }

    /**
     * 全选多选框的监听事件
     */
    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!isDBLoad){
            mCheckAll.setChecked(true);
            return;
        }
        if (isChecked) {      // 全选
            mSelectCount = mUserInfoList.size();
            for (int i = 0; i < mUserInfoList.size(); i++) {
                mUserInfoList.get(i).setChecked(true);
                mTextDelete.setText(getResources().getString(R.string.button_delete) + "(" +mSelectCount + ")");
                mTextDelete.setTextColor(Color.parseColor("#F34B56"));
            }
        } else {             // 取消全选
            mSelectCount = 0;
            for (int i = 0; i < mUserInfoList.size(); i++) {
                mUserInfoList.get(i).setChecked(false);
                mTextDelete.setText(getResources().getString(R.string.button_delete) + "(" + mSelectCount + ")");
                mTextDelete.setTextColor(Color.parseColor("#666666"));
            }
        }
        mFaceUserAdapter.notifyDataSetChanged();
    }

    // 用于adapter的item点击事件
    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void onItemClick(View view, int position) {
        if (!isDBLoad){
            return;
        }
        if (mRelativeDelete.getVisibility() != View.VISIBLE) {
            return;
        }
        // 如果当前item未选中，则选中
        if (!mUserInfoList.get(position).isChecked()) {
            mUserInfoList.get(position).setChecked(true);
            mSelectCount++;
            mTextDelete.setText(getResources().getString(R.string.button_delete) + "(" + mSelectCount + ")");
            mTextDelete.setTextColor(Color.parseColor("#F34B56"));
        } else {
            // 如果当前item已经选中，则取消选中
            mUserInfoList.get(position).setChecked(false);
            mSelectCount--;
            mTextDelete.setText(getResources().getString(R.string.button_delete) + "(" + mSelectCount + ")");
            if (mSelectCount == 0) {
                mTextDelete.setTextColor(Color.parseColor("#666666"));
            }
        }
        mFaceUserAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRemove(int position) {
        mUserInfoList.get(position).setChecked(true);
        mSelectCount = 1;
        mIsLongClick = true;
        mTipDialog.show();
        mTipDialog.setTextTitle(getResources().getString(R.string.dialog_delete_title));
        mTipDialog.setTextMessage(getResources().getString(R.string.dialog_delete_clear_message));
        mTipDialog.setTextConfirm(getResources().getString(R.string.button_delete) );
        mTipDialog.setCancelable(false);
    }

    // 对话框“取消”按钮
    @Override
    public void onCancel() {
        if (mIsLongClick) {
            resetDeleteData();
            mIsLongClick = false;
        }
        mFaceUserAdapter.notifyDataSetChanged();
        if (mTipDialog != null) {
            mTipDialog.dismiss();
        }
    }
    private DBLoadListener loadListener = new DBLoadListener() {
        @Override
        public void onStart(final int successCount) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressGroup.setVisibility(View.VISIBLE);
                    /*    if (successCount < 50 && successCount !=0){
                            loadProgress(10);
                        }*/
                    }
                });
        }

        @Override
        public void onLoad(final int finishCount, final int successCount, final float progress) {
//            if (successCount > 50 || successCount == 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress((int) (progress * 100));
                        progressText.setText(finishCount + "/" + successCount);
                    }
                });
//            }
        }

        @Override
        public void onComplete(List<User> features, final int successCount) {
        }

        @Override
        public void onFail(int finishCount, int successCount, List<User> features) {

        }
    };

    // 对话框“删除”按钮
    @Override
    public void onConfirm(String tipType) {
        if (mTipDialog != null) {
            mTipDialog.dismiss();
        }
        if (mSelectCount != 0) {
            isDBLoad = false;
            UserInfoManager.getInstance().deleteUserListInfo(mUserInfoList,
                    mEditTitleSearch.getText().toString() , mUserListListener,
                    loadListener);
        } else {
            updateDeleteUI(false);
        }
        if (mSelectCount == mUserInfoList.size()) {
            // 设置数据库状态
            ShareManager.getInstance(mContext).setDBState(false);
        }
    }

    // 更新删除相关的UI
    private void updateDeleteUI(boolean isShowDeleteUI) {
        if (isShowDeleteUI) {
            mRelativeDelete.setVisibility(View.VISIBLE);
            mImageMenu.setVisibility(View.GONE);
            mImageIconSearch.setVisibility(View.GONE);
            mTextCancel.setVisibility(View.VISIBLE);
            // 列表显示复选框
            mFaceUserAdapter.setShowCheckBox(true);
            mFaceUserAdapter.notifyDataSetChanged();
        } else {
            mRelativeDelete.setVisibility(View.GONE);
            mImageMenu.setVisibility(View.VISIBLE);
            mImageIconSearch.setVisibility(View.VISIBLE);
            mTextCancel.setVisibility(View.GONE);
            // 列表隐藏复选框
            mFaceUserAdapter.setShowCheckBox(false);
            mFaceUserAdapter.notifyDataSetChanged();
            if (mUserInfoList != null) {
                for (int i = 0; i < mUserInfoList.size(); i++) {
                    mUserInfoList.get(i).setChecked(false);
                }
            }
            mCheckAll.setChecked(false);
            mSelectCount = 0;
            mTextDelete.setText(getResources().getString(R.string.button_delete));
        }
    }

    private void showPopupWindow(ImageView imageView) {
        if (mPopupMenu != null && imageView != null) {
//            if (mUserInfoList == null || mUserInfoList.size() == 0) {
//                mPopRelativeImport.setBackgroundColor(Color.parseColor("#777777"));
//            } else {
//                mPopRelativeImport.setBackgroundColor(Color.parseColor("#666666"));
//                mPopRelativeImport.setBackground(this.getResources()
//                        .getDrawable(R.drawable.button_selector_homemenu_item2));
//            }
            int marginRight = DensityUtils.dip2px(mContext, 20);
            int marginTop = DensityUtils.dip2px(mContext, 56);

            mPopupMenu.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    isCheck = false;
                    mImageMenu.setImageResource(R.mipmap.icon_titlebar_menu);
                }
            });

            mPopupMenu.showAtLocation(imageView, Gravity.END | Gravity.TOP,
                    marginRight, marginTop);
        }
    }

    private void dismissPopupWindow() {
        if (mPopupMenu != null) {
            mPopupMenu.dismiss();
        }
    }

    private void resetDeleteData() {
        mSelectCount = 0;
        for (int i = 0; i < mUserInfoList.size(); i++) {
            mUserInfoList.get(i).setChecked(false);
        }
        mTextDelete.setText(getResources().getString(R.string.button_delete));
    }

    // 用于返回读取用户的结果
    private class UserListListener extends UserInfoManager.UserInfoListener {
        // 读取用户列表成功
        @Override
        public void userListQuerySuccess(final String userName, final List<User> listUserInfo) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressGroup.setVisibility(View.GONE);
                    isDBLoad = true;
                    mUserInfoList = listUserInfo;
                    if (listUserInfo == null || listUserInfo.size() == 0) {
                        mFaceUserAdapter.setDataList(listUserInfo);
                        mFaceUserAdapter.notifyDataSetChanged();
                        mRelativeEmpty.setVisibility(View.VISIBLE);
                        mRecyclerUserManager.setVisibility(View.GONE);
                        // 显示无内容判断
                        if (userName == null) {
                            mTextEmpty.setText(getResources().getString(R.string.empty_hint_nocontent));
                            updateDeleteUI(false);
                        } else {
                            mTextEmpty.setText(getResources().getString(R.string.empty_hint_noresults));
                            mRelativeDelete.setVisibility(View.GONE);
                        }
                        return;
                    }

                    // 恢复默认状态
                    resetDeleteData();
                    mRelativeEmpty.setVisibility(View.GONE);
                    mRecyclerUserManager.setVisibility(View.VISIBLE);
                    if (userName == null || userName.length() == 0) {
                        updateDeleteUI(false);
                    } else {
                        updateDeleteUI(true);
                    }
                    mFaceUserAdapter.setDataList(listUserInfo);
                    mFaceUserAdapter.notifyDataSetChanged();


                }
            });
        }

        // 读取用户列表失败
        @Override
        public void userListQueryFailure(final String message) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mContext == null) {
                        return;
                    }
                    ToastUtils.toast(mContext, message);
                }
            });
        }

        // 删除用户列表成功
        @Override
        public void userListDeleteSuccess() {
            UserInfoManager.getInstance().getUserListInfo(null, mUserListListener);
        }

        // 删除用户列表失败
        @Override
        public void userListDeleteFailure(final String message) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mContext == null) {
                        return;
                    }
                    ToastUtils.toast(mContext, message);
                }
            });
        }
    }

    /**
     * 点击非编辑区域收起键盘
     * 获取点击事件
     */
    @CallSuper
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (KeyboardsUtils.isShouldHideKeyBord(view, ev)) {
                KeyboardsUtils.hintKeyBoards(view);
            }
        }
        return super.dispatchTouchEvent(ev);
    }




    private void judgeCameraType(int cameraType, Class<?> depthCls) {
        switch (cameraType) {
            case 1: { // pro
                startActivity(new Intent(mContext, depthCls));
                break;
            }

            case 2: { // atlas
                startActivity(new Intent(mContext, depthCls));
                break;
            }

            default:
                startActivity(new Intent(mContext, depthCls));
                break;
        }
    }
}
