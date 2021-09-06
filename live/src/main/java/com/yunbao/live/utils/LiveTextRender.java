package com.yunbao.live.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.Constants;
import com.yunbao.common.bean.LevelBean;
import com.yunbao.common.custom.VerticalImageSpan;
import com.yunbao.common.glide.ImgLoader;
import com.yunbao.common.utils.DpUtil;
import com.yunbao.common.utils.StringUtil;
import com.yunbao.common.utils.WordUtil;
import com.yunbao.live.R;
import com.yunbao.live.bean.LiveChatBean;

/**
 * Created by cxf on 2018/10/11.
 */

public class LiveTextRender {

    private static StyleSpan sBoldSpan;
    private static StyleSpan sNormalSpan;
    private static ForegroundColorSpan sWhiteColorSpan;
    private static ForegroundColorSpan sGlobalColorSpan;
    private static AbsoluteSizeSpan sFontSizeSpan;
    private static AbsoluteSizeSpan sFontSizeSpan2;
    private static AbsoluteSizeSpan sFontSizeSpan3;

    static {
        sBoldSpan = new StyleSpan(Typeface.BOLD);
        sNormalSpan = new StyleSpan(Typeface.NORMAL);
        sWhiteColorSpan = new ForegroundColorSpan(0xffffffff);
        sGlobalColorSpan = new ForegroundColorSpan(0xffffdd00);
        sFontSizeSpan = new AbsoluteSizeSpan(17, true);
        sFontSizeSpan2 = new AbsoluteSizeSpan(13, true);
        sFontSizeSpan3 = new AbsoluteSizeSpan(14, true);
    }


    /**
     * 生成前缀
     */
    private static SpannableStringBuilder createPrefix(Drawable levelDrawable, LiveChatBean bean) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        int index = 0;
        if (levelDrawable != null) {
            builder.append("  ");
            levelDrawable.setBounds(0, 0, DpUtil.dp2px(28), DpUtil.dp2px(14));
            builder.setSpan(new VerticalImageSpan(levelDrawable), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            index = 2;
        }
        if (bean.getVipType() != 0) {//vip图标
//            TextView textView=new TextView(CommonAppContext.sInstance);
//            textView.setText("VIP"+bean.getVipType());
//            textView.setBackground(CommonAppContext.sInstance.getResources().getDrawable(R.mipmap.icon_chat_vip));
//            textView.setTextColor(CommonAppContext.sInstance.getResources().getColor(R.color.white));
//            textView.setGravity(Gravity.CENTER);
            Drawable vipDrawable;
            if(bean.getVip_is_king()!=null&&bean.getVip_is_king().equals("1")){
//                 vipDrawable=ConvertUtils.bitmap2Drawable(ConvertUtils.view2Bitmap(textView));
                vipDrawable = getVipDrawble(bean.getVipType(),true);
                if (vipDrawable != null) {
                    builder.append("  ");
                    vipDrawable.setBounds(0, 0, DpUtil.dp2px(14), DpUtil.dp2px(14));
                    builder.setSpan(new VerticalImageSpan(vipDrawable), index, index + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    index += 2;
                }
            }else {
                vipDrawable = ContextCompat.getDrawable(CommonAppContext.sInstance, R.mipmap.icon_live_chat_vip);
                if (vipDrawable != null) {
                    builder.append("  ");
                    vipDrawable.setBounds(0, 0, DpUtil.dp2px(28), DpUtil.dp2px(14));
                    builder.setSpan(new VerticalImageSpan(vipDrawable), index, index + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    index += 2;
                }
            }
        }
        if (bean.isManager()) {//直播间管理员图标
            Drawable drawable = ContextCompat.getDrawable(CommonAppContext.sInstance, R.mipmap.icon_live_chat_m);
            if (drawable != null) {
                builder.append("  ");
                drawable.setBounds(0, 0, DpUtil.dp2px(14), DpUtil.dp2px(14));
                builder.setSpan(new VerticalImageSpan(drawable), index, index + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                index += 2;
            }
        }
        if (bean.getGuardType() != Constants.GUARD_TYPE_NONE) {//守护图标
            Drawable drawable = ContextCompat.getDrawable(CommonAppContext.sInstance,
                    bean.getGuardType() == Constants.GUARD_TYPE_YEAR ? R.mipmap.icon_live_chat_guard_2 : R.mipmap.icon_live_chat_guard_1);
            if (drawable != null) {
                builder.append("  ");
                drawable.setBounds(0, 0, DpUtil.dp2px(14), DpUtil.dp2px(14));
                builder.setSpan(new VerticalImageSpan(drawable), index, index + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                index += 2;
            }
        }
        if (!TextUtils.isEmpty(bean.getLiangName())) {//靓号图标
            Drawable drawable = ContextCompat.getDrawable(CommonAppContext.sInstance, R.mipmap.icon_live_chat_liang);
            if (drawable != null) {
                builder.append("  ");
                drawable.setBounds(0, 0, DpUtil.dp2px(18), DpUtil.dp2px(14));
                builder.setSpan(new VerticalImageSpan(drawable), index, index + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return builder;
    }

    /**
     * 产品规定，进场消息不允许添加管理员图标,
     * 产品规定，进场消息不允许添加靓号图标
     * 所以 我只能复制一份上面的代码。。。。
     */
    public static SpannableStringBuilder createPrefix2(Drawable levelDrawable, LiveChatBean bean,boolean issmall) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        int index = 0;
        if (levelDrawable != null) {
            builder.append("  ");
            levelDrawable.setBounds(0, 0, DpUtil.dp2px(28), DpUtil.dp2px(14));
            builder.setSpan(new VerticalImageSpan(levelDrawable), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            index = 2;
        }
        if (bean.getVipType() != 0) {//vip图标

//            TextView textView=new TextView(CommonAppContext.sInstance);
//            textView.setText("VIP"+bean.getVipType());
//            textView.setBackground(CommonAppContext.sInstance.getResources().getDrawable(R.mipmap.icon_chat_vip));
//            textView.setTextColor(CommonAppContext.sInstance.getResources().getColor(R.color.white));
//            textView.setGravity(Gravity.CENTER);
            Drawable vipDrawable;
            if(bean.getVip_is_king()!=null&&bean.getVip_is_king().equals("1")){
//                vipDrawable=ConvertUtils.bitmap2Drawable(ConvertUtils.view2Bitmap(textView));
                vipDrawable = getVipDrawble(bean.getVipType(),issmall);
                if (vipDrawable != null) {
                    builder.append("  ");
                    if(issmall){
                        vipDrawable.setBounds(0, 0, DpUtil.dp2px(14), DpUtil.dp2px(14));
                    }else {
                        vipDrawable.setBounds(0, 0, DpUtil.dp2px(35), DpUtil.dp2px(14));
                    }
                    builder.setSpan(new VerticalImageSpan(vipDrawable), index, index + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    index += 2;
                }
//                vipDrawable = ContextCompat.getDrawable(CommonAppContext.sInstance, R.mipmap.icon_live_chat_vip);
            }else {
                vipDrawable = ContextCompat.getDrawable(CommonAppContext.sInstance, R.mipmap.icon_live_chat_vip);
                if (vipDrawable != null) {
                    builder.append("  ");
                    vipDrawable.setBounds(0, 0, DpUtil.dp2px(28), DpUtil.dp2px(14));
                    builder.setSpan(new VerticalImageSpan(vipDrawable), index, index + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    index += 2;
                }
            }
        }
        if (bean.getGuardType() != Constants.GUARD_TYPE_NONE) {//守护图标
            Drawable drawable = ContextCompat.getDrawable(CommonAppContext.sInstance,
                    bean.getGuardType() == Constants.GUARD_TYPE_YEAR ? R.mipmap.icon_live_chat_guard_2 : R.mipmap.icon_live_chat_guard_1);
            if (drawable != null) {
                builder.append("  ");
                drawable.setBounds(0, 0, DpUtil.dp2px(14), DpUtil.dp2px(14));
                builder.setSpan(new VerticalImageSpan(drawable), index, index + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return builder;
    }



    public static void render(Context context, final TextView textView, final LiveChatBean bean) {
        final LevelBean levelBean = CommonAppConfig.getInstance().getLevel(bean.getLevel());
        if (levelBean == null) {
            return;
        }
        ImgLoader.displayDrawable(CommonAppContext.sInstance,levelBean.getThumb(), new ImgLoader.DrawableCallback() {
            @Override
            public void onLoadSuccess(Drawable drawable) {
                if (textView != null) {
                    SpannableStringBuilder builder = createPrefix(drawable, bean);
                    int color = 0;
                    if (bean.isAnchor()) {
                        color = 0xffffdd00;
                    } else {
                        color = Color.parseColor(levelBean.getColor());
                    }
                    switch (bean.getType()) {
                        case LiveChatBean.GIFT:
                            builder = renderGift(color, builder, bean);
                            break;
                        default:
                            builder = renderChat(color, builder, bean);
                            break;
                    }
                    textView.setText(builder);
                }
            }

            @Override
            public void onLoadFailed() {
                if (textView != null) {
                    SpannableStringBuilder builder = createPrefix(null, bean);
                    int color = 0;
                    if (bean.isAnchor()) {
                        color = 0xffffdd00;
                    } else {
                        color = Color.parseColor(levelBean.getColor());
                    }
                    switch (bean.getType()) {
                        case LiveChatBean.GIFT:
                            builder = renderGift(color, builder, bean);
                            break;
                        default:
                            builder = renderChat(color, builder, bean);
                            break;
                    }
                    textView.setText(builder);
                }
            }
        });
    }

    /**
     * 渲染普通聊天消息
     */
    private static SpannableStringBuilder renderChat(int color, SpannableStringBuilder builder, LiveChatBean bean) {
        int length = builder.length();
        String name = bean.getUserNiceName();
        if (bean.getType() != LiveChatBean.ENTER_ROOM) {//产品规定，进场消息不允许加冒号
            name += "：";
        }
        builder.append(name);
        builder.setSpan(new ForegroundColorSpan(color), length, length + name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(bean.getContent());
        if (bean.getType() == LiveChatBean.LIGHT) {
            Drawable heartDrawable = ContextCompat.getDrawable(CommonAppContext.sInstance, LiveIconUtil.getLiveLightIcon(bean.getHeart()));
            if (heartDrawable != null) {
                builder.append(" ");
                heartDrawable.setBounds(0, 0, DpUtil.dp2px(16), DpUtil.dp2px(16));
                length = builder.length();
                builder.setSpan(new VerticalImageSpan(heartDrawable), length - 1, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return builder;
    }

    /**
     * 渲染送礼物消息
     */
    private static SpannableStringBuilder renderGift(int color, SpannableStringBuilder builder, LiveChatBean bean) {
        int length = builder.length();
        String name = bean.getUserNiceName() + "：";
        builder.append(name);
        builder.setSpan(new ForegroundColorSpan(color), length, length + name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        length = builder.length();
        String content = bean.getContent();
        builder.append(content);
        builder.setSpan(sGlobalColorSpan, length, length + content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }


    /**
     * 渲染用户进入房间消息
     */
    public static void renderEnterRoom(Context context,final TextView textView, final LiveChatBean bean) {
        final LevelBean levelBean = CommonAppConfig.getInstance().getLevel(bean.getLevel());
        if (levelBean == null) {
            return;
        }
        ImgLoader.displayDrawable(context,levelBean.getThumb(), new ImgLoader.DrawableCallback() {
            @Override
            public void onLoadSuccess(Drawable drawable) {
                if (textView != null) {
                    SpannableStringBuilder builder = createPrefix2(drawable, bean,true);
                    int start = builder.length();
                    String name = bean.getUserNiceName() + " ";
                    builder.append(name);
                    int end = start + name.length();
                    builder.setSpan(sWhiteColorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.setSpan(sFontSizeSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.setSpan(sBoldSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.append(bean.getContent());
                    textView.setText(builder);
                }
            }

            @Override
            public void onLoadFailed() {
                if (textView != null) {
                    SpannableStringBuilder builder = createPrefix2(null, bean,true);
                    int start = builder.length();
                    String name = bean.getUserNiceName() + " ";
                    builder.append(name);
                    int end = start + name.length();
                    builder.setSpan(sWhiteColorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.setSpan(sFontSizeSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.setSpan(sBoldSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.append(bean.getContent());
                    textView.setText(builder);
                }
            }

        });
    }

    public static SpannableStringBuilder renderGiftInfo2(String giftName) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String s1 = WordUtil.getString(R.string.live_send_gift_1);
        String content = s1 + " " + giftName;
        int index1 = s1.length();
        builder.append(content);
        builder.setSpan(sGlobalColorSpan, index1, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    public static SpannableStringBuilder renderGiftInfo(int giftCount, String giftName) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String s1 = WordUtil.getString(R.string.live_send_gift_1);
        String s2 = WordUtil.getString(R.string.live_send_gift_2) + giftName;
        String content = s1 + giftCount + s2;
        int index1 = s1.length();
        int index2 = index1 + String.valueOf(giftCount).length();
        builder.append(content);
        builder.setSpan(sFontSizeSpan3, index1, index2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(sGlobalColorSpan, index1, index2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }


    public static SpannableStringBuilder renderGiftCount(int count) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String s = String.valueOf(count);
        builder.append(s);
        for (int i = 0, length = s.length(); i < length; i++) {
            String c = String.valueOf(s.charAt(i));
            if (StringUtil.isInt(c)) {
                int icon = LiveIconUtil.getGiftCountIcon(Integer.parseInt(c));
                Drawable drawable = ContextCompat.getDrawable(CommonAppContext.sInstance, icon);
                if (drawable != null) {
                    drawable.setBounds(0, 0, DpUtil.dp2px(24), DpUtil.dp2px(32));
                    builder.setSpan(new ImageSpan(drawable), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return builder;
    }

    /**
     * 渲染直播间用户弹窗数据
     */
    public static CharSequence renderLiveUserDialogData(long num) {
        if (num < 10000) {
            return String.valueOf(num);
        }
        SpannableStringBuilder builder = new SpannableStringBuilder();
        //有时在想，海外项目的时候这个"万"怎么翻译？？？而且英语中也没有"万"这个单位啊。。
        String wan = " " + WordUtil.getString(R.string.num_wan);
        String s = StringUtil.toWan2(num) + wan;
        builder.append(s);
        int index2 = s.length();
        int index1 = index2 - wan.length();
        builder.setSpan(sNormalSpan, index1, index2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(sFontSizeSpan2, index1, index2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    /**
     * 渲染直播间用户弹窗数据
     */
    public static CharSequence renderLiveUserDialogData2(double num) {
        if (num < 10000) {
            return String.valueOf(num);
        }
        SpannableStringBuilder builder = new SpannableStringBuilder();
        //有时在想，海外项目的时候这个"万"怎么翻译？？？而且英语中也没有"万"这个单位啊。。
        String wan = " " + WordUtil.getString(R.string.num_wan);
        String s = StringUtil.toWan4(num) + wan;
        builder.append(s);
        int index2 = s.length();
        int index1 = index2 - wan.length();
        builder.setSpan(sNormalSpan, index1, index2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(sFontSizeSpan2, index1, index2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return builder;
    }


    private static Drawable getVipDrawble(int vipType,boolean small) {
        Drawable drawable =null;
        if(vipType>35){
            drawable = ContextCompat.getDrawable(CommonAppContext.sInstance, small ? R.mipmap.vip_35s:R.mipmap.vip_35l);
            return drawable;
        }
        switch (vipType){
            case 1:
                drawable = ContextCompat.getDrawable(CommonAppContext.sInstance, small ? R.mipmap.vip_1s:R.mipmap.vip_1l);
                break;
            case 2:
                drawable = ContextCompat.getDrawable(CommonAppContext.sInstance, small ? R.mipmap.vip_2s:R.mipmap.vip_2l);
                break;
            case 3:
                drawable = ContextCompat.getDrawable(CommonAppContext.sInstance,small ? R.mipmap.vip_3s:R.mipmap.vip_3l);
                break;
            case 4:
                drawable = ContextCompat.getDrawable(CommonAppContext.sInstance, small ? R.mipmap.vip_4s:R.mipmap.vip_4l);
                break;
            case 5:
                drawable = ContextCompat.getDrawable(CommonAppContext.sInstance, small ? R.mipmap.vip_5s:R.mipmap.vip_5l);
                break;
            case 6:
                drawable = ContextCompat.getDrawable(CommonAppContext.sInstance, small ? R.mipmap.vip_6s:R.mipmap.vip_6l);
                break;
            case 7:
                drawable = ContextCompat.getDrawable(CommonAppContext.sInstance, small ? R.mipmap.vip_7s:R.mipmap.vip_7l);
                break;
            case 8:
                drawable = ContextCompat.getDrawable(CommonAppContext.sInstance, small ? R.mipmap.vip_8s:R.mipmap.vip_8l);
                break;
            case 9:
                drawable = ContextCompat.getDrawable(CommonAppContext.sInstance, small ? R.mipmap.vip_9s:R.mipmap.vip_9l);
                break;
            case 10:
                drawable = ContextCompat.getDrawable(CommonAppContext.sInstance,small ? R.mipmap.vip_10s:R.mipmap.vip_10l);
                break;
            case 11:
                drawable = ContextCompat.getDrawable(CommonAppContext.sInstance, small ? R.mipmap.vip_11s:R.mipmap.vip_11l);
                break;
            case 12:
                drawable = ContextCompat.getDrawable(CommonAppContext.sInstance, small ? R.mipmap.vip_12s:R.mipmap.vip_12l);
                break;
            case 13:
                drawable = ContextCompat.getDrawable(CommonAppContext.sInstance,small ? R.mipmap.vip_13s:R.mipmap.vip_13l);
                break;
            case 14:
                drawable = ContextCompat.getDrawable(CommonAppContext.sInstance,small ? R.mipmap.vip_14s:R.mipmap.vip_14l);
                break;
            case 15:
                drawable = ContextCompat.getDrawable(CommonAppContext.sInstance, small ? R.mipmap.vip_15s:R.mipmap.vip_15l);
                break;
            case 16:
                drawable = ContextCompat.getDrawable(CommonAppContext.sInstance, small ? R.mipmap.vip_16s:R.mipmap.vip_16l);
                break;
            case 17:
                drawable = ContextCompat.getDrawable(CommonAppContext.sInstance, small ? R.mipmap.vip_17s:R.mipmap.vip_17l);
                break;
            case 18:
                drawable = ContextCompat.getDrawable(CommonAppContext.sInstance, small ? R.mipmap.vip_18s:R.mipmap.vip_18l);
                break;
            case 19:
                drawable = ContextCompat.getDrawable(CommonAppContext.sInstance, small ? R.mipmap.vip_19s:R.mipmap.vip_19l);
                break;
            case 20:
                drawable = ContextCompat.getDrawable(CommonAppContext.sInstance, small ? R.mipmap.vip_20s:R.mipmap.vip_20l);
                break;
            case 21:
                drawable = ContextCompat.getDrawable(CommonAppContext.sInstance, small ? R.mipmap.vip_21s:R.mipmap.vip_21l);
                break;
            case 22:
                drawable = ContextCompat.getDrawable(CommonAppContext.sInstance, small ? R.mipmap.vip_22s:R.mipmap.vip_22l);
                break;
            case 23:
                drawable = ContextCompat.getDrawable(CommonAppContext.sInstance, small ? R.mipmap.vip_23s:R.mipmap.vip_23l);
                break;
            case 24:
                drawable = ContextCompat.getDrawable(CommonAppContext.sInstance, small ? R.mipmap.vip_24s:R.mipmap.vip_24l);
                break;
            case 25:
                drawable = ContextCompat.getDrawable(CommonAppContext.sInstance, small ? R.mipmap.vip_25s:R.mipmap.vip_25l);
                break;
            case 26:
                drawable = ContextCompat.getDrawable(CommonAppContext.sInstance, small ? R.mipmap.vip_26s:R.mipmap.vip_26l);
                break;
            case 27:
                drawable = ContextCompat.getDrawable(CommonAppContext.sInstance, small ? R.mipmap.vip_27s:R.mipmap.vip_27l);
                break;
            case 28:
                drawable = ContextCompat.getDrawable(CommonAppContext.sInstance, small ? R.mipmap.vip_28s:R.mipmap.vip_28l);
                break;
            case 29:
                drawable = ContextCompat.getDrawable(CommonAppContext.sInstance, small ? R.mipmap.vip_29s:R.mipmap.vip_29l);
                break;
            case 30:
                drawable = ContextCompat.getDrawable(CommonAppContext.sInstance, small ? R.mipmap.vip_30s:R.mipmap.vip_30l);
                break;
            case 31:
                drawable = ContextCompat.getDrawable(CommonAppContext.sInstance, small ? R.mipmap.vip_31s:R.mipmap.vip_31l);
                break;
            case 32:
                drawable = ContextCompat.getDrawable(CommonAppContext.sInstance, small ? R.mipmap.vip_32s:R.mipmap.vip_32l);
                break;
            case 33:
                drawable = ContextCompat.getDrawable(CommonAppContext.sInstance, small ? R.mipmap.vip_33s:R.mipmap.vip_33l);
                break;
            case 34:
                drawable = ContextCompat.getDrawable(CommonAppContext.sInstance, small ? R.mipmap.vip_34s:R.mipmap.vip_34l);
                break;
            case 35:
                drawable = ContextCompat.getDrawable(CommonAppContext.sInstance, small ? R.mipmap.vip_35s:R.mipmap.vip_35l);
                break;


        }
        return  drawable;
    }
}
