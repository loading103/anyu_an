package com.yunbao.live.activity;

import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.yunbao.common.adapter.ImChatFacePagerAdapter;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.dialog.ChatFaceDialog;
import com.yunbao.common.event.FollowEvent;
import com.yunbao.common.greendao.GreenDaoUtils;
import com.yunbao.common.greendao.entity.SocketMessageBean;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.interfaces.KeyBoardHeightChangeListener;
import com.yunbao.common.interfaces.OnFaceClickListener;
import com.yunbao.common.utils.ClickUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.views.AbsViewHolder;
import com.yunbao.im.R;
import com.yunbao.im.dialog.ChatMoreDialog;
import com.yunbao.im.interfaces.ChatRoomActionListener;
import com.yunbao.im.utils.ImTextRender;
import com.yunbao.live.adapter.ChatRoomAdapter;
import com.yunbao.live.socket.SocketChatUtil;
import com.yunbao.live.socket.SocketClient;
import com.yunbao.live.utils.NetworkUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by cxf on 2018/10/24.
 */

public class LiveChatRoomDialogViewHolder extends AbsViewHolder implements
        View.OnClickListener, OnFaceClickListener, ChatFaceDialog.ActionListener,
        ChatMoreDialog.ActionListener {

    private InputMethodManager imm;
    private RecyclerView mRecyclerView;
    private EditText mEditText;
    private TextView mTitleView;
    private UserBean mUserBean;
    private String mToUid;
    private ChatRoomActionListener mActionListener;
    private CheckBox mBtnFace;
    private View mFaceView;//表情控件
    private int mFaceViewHeight;//表情控件高度
    private ChatFaceDialog mChatFaceDialog;//表情弹窗
    private boolean mFollowing;
    private Handler mHandler;
    private SocketClient mSocketClient;
    private ChatRoomAdapter  chatRoomAdapter;
    private List<SocketMessageBean> datas;
    private boolean isList;
    private ImageView btnBack;

    public LiveChatRoomDialogViewHolder(Context context, ViewGroup parentView, UserBean userBean, boolean following, SocketClient mSocketClient,boolean isList) {
        super(context, parentView, userBean, following,mSocketClient,isList);
    }

    @Override
    protected void processArguments(Object... args) {
        mUserBean = (UserBean) args[0];
        mFollowing = (Boolean) args[1];
        mSocketClient= (SocketClient) args[2];
        isList= (Boolean) args[3];
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_chat_room_2;
    }

    @Override
    public void init() {
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mTitleView = (TextView) findViewById(R.id.titleView);
        mEditText = (EditText) findViewById(R.id.edit);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendText();
                    return true;
                }
                return false;
            }
        });
        mEditText.setOnClickListener(this);
        btnBack=(ImageView) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        mBtnFace = (CheckBox) findViewById(R.id.btn_face);
        mBtnFace.setOnClickListener(this);
        View btnAdd = findViewById(R.id.btn_add);
        if (btnAdd != null) {
            btnAdd.setOnClickListener(this);
        }
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    return hideSoftInput() || hideFace();
                }
                return false;
            }
        });
        mHandler = new Handler();
        mEditText.requestFocus();
//        if(!isList){
//            btnBack.setRotation(-90);
//        }
    }

    /**
     * 初始化表情控件
     */
    private View initFaceView() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.view_chat_face, null);
        v.measure(0, 0);
        mFaceViewHeight = v.getMeasuredHeight();
        v.findViewById(R.id.btn_send).setOnClickListener(this);
        final RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.radio_group);
        ViewPager viewPager = (ViewPager) v.findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(10);
        ImChatFacePagerAdapter adapter = new ImChatFacePagerAdapter(mContext, this);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ((RadioButton) radioGroup.getChildAt(position)).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        for (int i = 0, pageCount = adapter.getCount(); i < pageCount; i++) {
            RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.view_chat_indicator, radioGroup, false);
            radioButton.setId(i + 10000);
            if (i == 0) {
                radioButton.setChecked(true);
            }
            radioGroup.addView(radioButton);
        }
        return v;
    }


    public void loadData() {
        if (mUserBean == null) {
            return;
        }
        mToUid = mUserBean.getId();
        if (TextUtils.isEmpty(mToUid)) {
            return;
        }
        mTitleView.setText(mUserBean.getUserNiceName());
        datas = GreenDaoUtils.getInstance().querySocketMessageDataAccordField(mToUid);
        if(datas==null || datas.size()==0){
            return;
        }
        chatRoomAdapter =new ChatRoomAdapter(mContext,datas,mUserBean);
        mRecyclerView.setAdapter(chatRoomAdapter);
        mRecyclerView.scrollToPosition(datas.size()-1);
    }


    public void setActionListener(ChatRoomActionListener actionListener) {
        mActionListener = actionListener;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_back) {
            back();

        } else if (i == R.id.btn_send) {
            sendText();

        } else if (i == R.id.btn_face) {
            faceClick();

        } else if (i == R.id.edit) {
            clickInput();

        }
    }


    /**
     * 关注
     */
    private void follow() {
        CommonHttpUtil.setAttention(mToUid, null);
    }

    /**
     * 返回
     */
    public void back() {
        if (hideFace() || hideSoftInput()) {
            return;
        }
        if (mActionListener != null) {
            mActionListener.onCloseClick();
        }
    }

    /**
     * 点击输入框
     */
    private void clickInput() {
        hideFace();
    }


    /**
     * 显示软键盘
     */
    private void showSoftInput() {
        if (!((KeyBoardHeightChangeListener) mContext).isSoftInputShowed() && imm != null && mEditText != null) {
            imm.showSoftInput(mEditText, InputMethodManager.SHOW_FORCED);
            mEditText.requestFocus();
        }
    }

    /**
     * 隐藏键盘
     */
    private boolean hideSoftInput() {
        if (((KeyBoardHeightChangeListener) mContext).isSoftInputShowed() && imm != null && mEditText != null) {
            imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
            return true;
        }
        return false;
    }

    /**
     * 点击表情按钮
     */
    private void faceClick() {
        if (mBtnFace.isChecked()) {
            hideSoftInput();
            if (mHandler != null) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showFace();
                    }
                }, 200);
            }
        } else {
            hideFace();
            showSoftInput();
        }
    }

    /**
     * 显示表情弹窗
     */
    private void showFace() {
        if (mChatFaceDialog != null && mChatFaceDialog.isShowing()) {
            return;
        }
        if (mFaceView == null) {
            mFaceView = initFaceView();
        }
        if (mActionListener != null) {
            mActionListener.onPopupWindowChanged(mFaceViewHeight);
        }
        mChatFaceDialog = new ChatFaceDialog(mParentView, mFaceView, false, this);
        mChatFaceDialog.show();
    }

    /**
     * 隐藏表情弹窗
     */
    private boolean hideFace() {
        if (mChatFaceDialog != null) {
            mChatFaceDialog.dismiss();
            mChatFaceDialog = null;
            return true;
        }
        return false;
    }

    /**
     * 表情弹窗消失的回调
     */
    @Override
    public void onFaceDialogDismiss() {
        if (mActionListener != null) {
            mActionListener.onPopupWindowChanged(0);
        }
        mChatFaceDialog = null;
        if (mBtnFace != null) {
            mBtnFace.setChecked(false);
        }
    }


    /**
     * 更多弹窗消失的回调
     */
    @Override
    public void onMoreDialogDismiss() {
        if (mActionListener != null) {
            mActionListener.onPopupWindowChanged(0);
        }
    }

    /**
     * 点击表情图标按钮
     */
    @Override
    public void onFaceClick(String str, int faceImageRes) {
        if (mEditText != null) {
            Editable editable = mEditText.getText();
            editable.insert(mEditText.getSelectionStart(), ImTextRender.getFaceImageSpan(str, faceImageRes));
        }
    }

    /**
     * 点击表情删除按钮
     */
    @Override
    public void onFaceDeleteClick() {
        if (mEditText != null) {
            int selection = mEditText.getSelectionStart();
            String text = mEditText.getText().toString();
            if (selection > 0) {
                String text2 = text.substring(selection - 1, selection);
                if ("]".equals(text2)) {
                    int start = text.lastIndexOf("[", selection);
                    if (start >= 0) {
                        mEditText.getText().delete(start, selection);
                    } else {
                        mEditText.getText().delete(selection - 1, selection);
                    }
                } else {
                    mEditText.getText().delete(selection - 1, selection);
                }
            }
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
        if (mChatFaceDialog != null) {
            mChatFaceDialog.dismiss();
        }
        mChatFaceDialog = null;
    }


    /**
     * 发送文本信息
     */
    public void sendText() {
        if (!ClickUtil.canClick()) {
            return;
        }

        String content = mEditText.getText().toString().trim();
        if(!NetworkUtils.isNetWorkAvailable(mContext)){
            ToastUtil.show(R.string.load_failure);
            return;
        }
        if (TextUtils.isEmpty(content)) {
            ToastUtil.show(R.string.content_empty);
            return;
        }
        SocketChatUtil.sendPrivateMsg(mSocketClient,content,mToUid, mUserBean);

    }

    /**
     * 接收到私信消息
     */
    public void onReceivePrivateLetterMsg(SocketMessageBean bean) {
        if(!bean.getSendUid().equals(mUserBean.getId())){
            return;
        }
        inSertMessageBean(bean,true);
    }


    /**
     * 接收到发送失败的回执
     */
    public void onReceiveSendMsgFail(SocketMessageBean bean, String retmsg) {
        if(!bean.getTargetUid().equals(mUserBean.getId())){
            return;
        }
        inSertMessageBean(bean,false);
//        Toast.makeText(mContext, retmsg, Toast.LENGTH_SHORT).show();
        ToastUtil.show(retmsg);
        if(mEditText!=null){
            mEditText.getText().clear();
        }
    }
    /**
     * 接收到发送成功的回执
     */
    public void onReceiveSendMsgSuccess(SocketMessageBean bean) {
        if(!bean.getTargetUid().equals(mUserBean.getId())){
            return;
        }
        inSertMessageBean(bean,true);
        if(mEditText!=null){
            mEditText.getText().clear();
        }
    }

    /**
     * 收到回执成功和接收消息为true
     */
    private void inSertMessageBean(SocketMessageBean bean,boolean success) {
        if(datas==null){
            datas=new ArrayList<>();
        }
        bean.setSendState(success?"1":"2");
        datas.add(bean);
        if(chatRoomAdapter==null){
            chatRoomAdapter =new ChatRoomAdapter(mContext,datas,mUserBean);
            mRecyclerView.setAdapter(chatRoomAdapter);
        }else {
            chatRoomAdapter.notifyDataSetChanged();
        }
        if(mRecyclerView!=null){
            mRecyclerView.scrollToPosition(datas.size()-1);
        }
    }

    public void scrollToBottom() {
        if(mRecyclerView!=null && datas!=null && datas.size()>1){
            mRecyclerView.scrollToPosition(datas.size()-1);
        }
    }
}
