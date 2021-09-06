package com.yunbao.live.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.just.agentweb.AgentWeb;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.CoinBean;
import com.yunbao.common.bean.UserBean;
import com.yunbao.common.dialog.AbsDialogFragment;
import com.yunbao.common.event.CoinChangeEvent;
import com.yunbao.common.http.CommonHttpUtil;
import com.yunbao.common.http.HttpCallback;
import com.yunbao.common.interfaces.OnItemClickListener;
import com.yunbao.common.utils.DialogUitl;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.ToastUtil;
import com.yunbao.live.R;
import com.yunbao.live.adapter.CoinAdapter;
import com.yunbao.live.http.LiveHttpUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by cxf on 2020/5/22.
 * 直播间充值
 */
public class LivePayDialog extends AbsDialogFragment implements OnItemClickListener<CoinBean> {


    private Double mBalanceValue;
    private List<CoinBean> list;
    private RecyclerView mRecyclerView;
    private CoinAdapter mAdapter;
    private String mCoinName;
    private CoinBean mCheckedCoinBean;
    private Button btnPay;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_pay;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog2;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String more_web_url = bundle.getString(Constants.MORE_WEB_URL);
//            openUrl(more_web_url);
        }

        btnPay = (Button) findViewById(R.id.btn_pay);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
        mAdapter = new CoinAdapter(mContext, CommonAppConfig.getInstance().getCoinName());
//        mAdapter.setContactView(mTop);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay(mCheckedCoinBean);
            }
        });

        loadPayData();
    }

    /**
     * 充值所需数据
     */
    private void loadPayData() {
        LiveHttpUtil.getBalance(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String coin = obj.getString("coin");
                    if(!TextUtils.isEmpty(coin)){
                        mBalanceValue = Double.parseDouble(coin);
                    }
//                    mBalance.setText(coin);
                    list = JSON.parseArray(obj.getString("rules"), CoinBean.class);
                    if (mAdapter != null) {
                        if(list.size()>0){
                            list.get(0).setCheck(true);
                            mCheckedCoinBean = list.get(0);
                            btnPay.setText(String.format(getResources().getString(R.string.coin_pay),mCheckedCoinBean.getMoney()));
                        }
                        mAdapter.setList(list);
                    }
                }
            }
        });
    }

    /**
     * 检查支付结果
     */
    private void checkPayResult() {
        LiveHttpUtil.getBalance(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String coin = obj.getString("coin");
                    //mBalance.setText(coin);
                    Double balanceValue = Double.parseDouble(coin);
                    if (balanceValue > mBalanceValue) {
                        mBalanceValue = balanceValue;
                        ToastUtil.show(R.string.coin_charge_success);
                        UserBean u = CommonAppConfig.getInstance().getUserBean();
                        if (u != null) {
                            u.setCoin(coin);
                        }
                        EventBus.getDefault().post(new CoinChangeEvent(coin, true));
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
}
