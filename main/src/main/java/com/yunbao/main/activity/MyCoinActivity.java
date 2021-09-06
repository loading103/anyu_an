package com.yunbao.main.activity;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.activity.AbsActivity;
import com.yunbao.common.activity.WebViewActivity;
import com.yunbao.common.bean.CoinBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.event.CoinChangeEvent;
import com.yunbao.common.http.CommonHttpConsts;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.pay.PayCallback;
import com.yunbao.common.pay.ali.AliPayBuilder;
import com.yunbao.common.pay.wx.WxPayBuilder;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.L;
import com.yunbao.common.utils.RouteUtil;
import com.yunbao.common.utils.SpUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.main.R;
import com.yunbao.main.adapter.CoinAdapter;
import com.yunbao.main.http.MainHttpConsts;
import com.yunbao.main.http.MainHttpUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by cxf on 2018/10/23.
 * 我的钻石
 */
@Route(path = RouteUtil.PATH_COIN)
public class MyCoinActivity extends AbsActivity implements OnItemClickListener<CoinBean> {

    private View mTop;
    private TextView mBalance;
    private Double mBalanceValue;
    private RecyclerView mRecyclerView;
    private CoinAdapter mAdapter;
    private String mCoinName;
    private CoinBean mCheckedCoinBean;
    private String mAliPartner;// 支付宝商户ID
    private String mAliSellerId; // 支付宝商户收款账号
    private String mAliPrivateKey; // 支付宝商户私钥，pkcs8格式
    private String mWxAppID;//微信AppID
    private boolean mFirstLoad = true;
    private SparseArray<String> mSparseArray;
    private Button btnPay;
    private List<CoinBean> list;
    private TextView tvXy;
    private String mKingReg;
    private TextView tvPay;
    private TextView tvMoney;
    private LinearLayout llMoney;
    private TextView tvCoin;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_coin;
    }

    @Override
    protected void main() {
        mSparseArray = new SparseArray<>();
//        mCoinName = CommonAppConfig.getInstance().getCoinName();

        mCoinName =getIntent().getStringExtra("name");
        setTitle( mCoinName);
        mTop = findViewById(R.id.top);
        TextView mTvFinish = findViewById(R.id.tv_finish);
        btnPay = findViewById(R.id.btn_pay);
        mTvFinish.setVisibility(View.GONE);
        mBalance = (TextView) findViewById(R.id.balance);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        tvXy = (TextView) findViewById(R.id.tv_xy);
        tvPay = (TextView) findViewById(R.id.tv_pay);
        tvCoin = (TextView) findViewById(R.id.tv_coin);
        tvMoney = (TextView) findViewById(R.id.tv_money);
        llMoney = (LinearLayout) findViewById(R.id.ll_money);


        tvCoin.setText(mCoinName);
        tvPay.setText(mCoinName+"充值");

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
        mAdapter = new CoinAdapter(mContext, mCoinName);
        mAdapter.setContactView(mTop);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay(mCheckedCoinBean);
            }
        });
        initXy();
        showCharge(false);
//        setTextViewStyles(tvPay);
    }

    /**
     * 设置textview 的颜色渐变
     * @param text
     */

    public void setTextViewStyles(TextView text){
        LinearGradient mLinearGradient =new LinearGradient(0, 0, text.getMeasuredWidth(),  0, Color.parseColor("#B739FF"), Color.parseColor("#FF2BE9"), Shader.TileMode.CLAMP);
        text.getPaint().setShader(mLinearGradient);
        text.invalidate();
    }

    /**
     * 协议
     */
    private void initXy() {
        SpannableStringBuilder builder=new SpannableStringBuilder(getString(R.string.string_privacy));
        ClickableSpan clickSpanPrivacy=new ClickableSpan() {
            @Override
            public void onClick( View widget) {

                WebViewActivity.forward(mContext, SpUtil.getInstance().getStringValue(SpUtil.FAST_URL)+"/index.php?g=portal&m=page&a=index&id=6");
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                //点击事件去掉下划线
                ds.setUnderlineText(false);
            }
        };
        ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary));
        builder.setSpan(colorSpan1,6,14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(clickSpanPrivacy,6,14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvXy.setText(builder);
        tvXy.setHighlightColor(Color.TRANSPARENT);
        tvXy.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mFirstLoad) {
            mFirstLoad = false;
            loadData();
        } else {
            checkPayResult();
        }
    }

    private void loadData() {
        MainHttpUtil.getBalance(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String coin = obj.getString("coin");
                    if(!TextUtils.isEmpty(coin)){
                        mBalanceValue = Double.parseDouble(coin);
                    }
                    mBalance.setText(coin);
                    list = JSON.parseArray(obj.getString("rules"), CoinBean.class);
                    if (mAdapter != null) {
                        if(list.size()>0){
                            list.get(0).setCheck(true);
                            mCheckedCoinBean = list.get(0);
                            btnPay.setText(String.format(getResources().getString(R.string.coin_pay),mCheckedCoinBean.getMoney()));
                        }
                        mAdapter.setList(list);
                    }

                    mAliPartner = obj.getString("aliapp_partner");
                    mAliSellerId = obj.getString("aliapp_seller_id");
                    mAliPrivateKey = obj.getString("aliapp_key_android");
                    mWxAppID = obj.getString("wx_appid");
                    boolean aliPayEnable = obj.getIntValue("aliapp_switch") == 1;//支付宝是否开启
                    boolean wxPayEnable = obj.getIntValue("wx_switch") == 1;//微信支付是否开启
                    if (aliPayEnable) {
                        mSparseArray.put(Constants.PAY_TYPE_ALI, WordUtil.getString(R.string.coin_pay_ali));
                    }
                    if (wxPayEnable) {
                        mSparseArray.put(Constants.PAY_TYPE_WX, WordUtil.getString(R.string.coin_pay_wx));
                    }

                    mKingReg = obj.getString("king_res");
                    if(!mKingReg.equals("1")){//隐藏充值
                        showCharge(false);
                    }else {
                        showCharge(true);
                    }

                    JSONObject king = obj.getJSONObject("king");
                    if(king.getString("king_res").equals("1")){
                        llMoney.setVisibility(View.VISIBLE);
//                        tvMoney.setText(String.format("我的余额：￥%1$s元", king.getString("balance")));
                        tvMoney.setText("￥"+king.getString("balance"));
                    }else {
                        llMoney.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void showCharge(boolean isSHow) {
        tvPay.setVisibility(isSHow==true?View.VISIBLE:View.INVISIBLE);
        tvXy.setVisibility(isSHow==true?View.VISIBLE:View.INVISIBLE);
        mRecyclerView.setVisibility(isSHow==true?View.VISIBLE:View.INVISIBLE);
        btnPay.setVisibility(isSHow==true?View.VISIBLE:View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        MainHttpUtil.cancel(MainHttpConsts.GET_BALANCE);
        CommonHttpUtil.cancel(CommonHttpConsts.GET_ALI_ORDER);
        CommonHttpUtil.cancel(CommonHttpConsts.GET_WX_ORDER);
        super.onDestroy();
    }

    @Override
    public void onItemClick(CoinBean bean, int position) {
        mCheckedCoinBean = bean;
        for (int i = 0; i < list.size(); i++) {
            if(i==position){
                list.get(i).setCheck(true);
            }else {
                list.get(i).setCheck(false);
            }
        }
        mAdapter.setList(list);
        btnPay.setText(String.format(getResources().getString(R.string.coin_pay),bean.getMoney()));
    }

    private DialogUitl.StringArrayDialogCallback mArrayDialogCallback = new DialogUitl.StringArrayDialogCallback() {
        @Override
        public void onItemClick(String text, int tag) {
            switch (tag) {
                case Constants.PAY_TYPE_ALI://支付宝支付
                    aliPay();
                    break;
                case Constants.PAY_TYPE_WX://微信支付
                    wxPay();
                    break;
            }
        }
    };

    private void aliPay() {
        if (!CommonAppConfig.isAppExist(Constants.PACKAGE_NAME_ALI)) {
            ToastUtil.show(R.string.coin_ali_not_install);
            return;
        }
        if (TextUtils.isEmpty(mAliPartner) || TextUtils.isEmpty(mAliSellerId) || TextUtils.isEmpty(mAliPrivateKey)) {
            ToastUtil.show(Constants.PAY_ALI_NOT_ENABLE);
            return;
        }
        AliPayBuilder builder = new AliPayBuilder(this, mAliPartner, mAliSellerId, mAliPrivateKey);
        builder.setCoinName(mCoinName);
        builder.setCoinBean(mCheckedCoinBean);
        builder.setPayCallback(mPayCallback);
        builder.pay();
    }

    private void wxPay() {
        if (!CommonAppConfig.isAppExist(Constants.PACKAGE_NAME_WX)) {
            ToastUtil.show(R.string.coin_wx_not_install);
            return;
        }
        if (TextUtils.isEmpty(mWxAppID)) {
            ToastUtil.show(Constants.PAY_WX_NOT_ENABLE);
            return;
        }
//        WxPayBuilder builder = new WxPayBuilder(mContext, mWxAppID);
//        builder.setCoinBean(mCheckedCoinBean);
//        builder.setPayCallback(mPayCallback);
//        builder.pay();
    }

    PayCallback mPayCallback = new PayCallback() {
        @Override
        public void onSuccess() {
            // checkPayResult();
        }

        @Override
        public void onFailed() {
            //ToastUtil.show(R.string.coin_charge_failed);
        }
    };

    /**
     * 检查支付结果
     */
    private void checkPayResult() {
        MainHttpUtil.getBalance(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String coin = obj.getString("coin");
                    mBalance.setText(coin);
                    Double balanceValue = Double.parseDouble(coin);
                    if (balanceValue > mBalanceValue) {
                        mBalanceValue = balanceValue;
                        ToastUtil.show(R.string.coin_charge_success);
                        UserBean u = CommonAppConfig.getInstance().getUserBean();
                        if (u != null) {
                            u.setCoin(coin);
                        }
                        EventBus.getDefault().post(new CoinChangeEvent(coin, true));
                        loadData();
                    }
                }
            }
        });
    }

    /**
     * 从服务器端获取订单号,即下单
     * @param mBean
     */
    public void pay(CoinBean mBean) {
        if (mBean == null) {
            return;
        }
        CommonHttpUtil.doPay(mBean.getMoney(), mBean.getId(), mBean.getCoin(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    checkPayResult();
                }else {
                    ToastUtil.show(msg);
                }
            }

            @Override
            public boolean showLoadingDialog() {
                return true;
            }

            @Override
            public Dialog createLoadingDialog() {
                return DialogUitl.loadingNewDialog(mContext);
            }


        });
    }


}
