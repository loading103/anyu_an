package com.yunbao.main.event;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2020/8/18.
 * Describe:
 */
public class MyPhoneState extends BroadcastReceiver{


    public TelephonyManager tm;

    public MyPhoneState(){

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if("android.intent.action.PHONE_STATE".equals(intent.getAction())) {
            //获取电话管理者
            tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            int state = tm.getCallState();
            //获取电话号码
            String number = intent.getStringExtra("incoming_number");

            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.i("test", "有电话进来了" + number);
                    EventBus.getDefault().post(new CallEvent(0));
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.i("test", "通话中" + number);
                    EventBus.getDefault().post(new CallEvent(1));
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.i("test", "通话结束了" + number);
                    EventBus.getDefault().post(new CallEvent(2));
                    break;
            }
        }
    }
}