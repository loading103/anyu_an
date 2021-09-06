package com.yunbao.common.utils;

import com.yunbao.common.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cxf on 2018/7/11.
 */

public class FaceUtil {

    private static final Map<String, Integer> FACE_MAP;

    private static final List<String> FACE_LIST;

    static {
        FACE_MAP = new LinkedHashMap<>();
        FACE_MAP.put("[微笑]", R.mipmap.emoji_00);
        FACE_MAP.put("[撇嘴]", R.mipmap.emoji_01);
        FACE_MAP.put("[色]", R.mipmap.emoji_02);
        FACE_MAP.put("[发呆]", R.mipmap.emoji_03);
        FACE_MAP.put("[得意]", R.mipmap.emoji_04);
        FACE_MAP.put("[流泪]", R.mipmap.emoji_05);
        FACE_MAP.put("[害羞]", R.mipmap.emoji_06);
        FACE_MAP.put("[闭嘴]", R.mipmap.emoji_07);
        FACE_MAP.put("[睡]", R.mipmap.emoji_08);
        FACE_MAP.put("[大哭]", R.mipmap.emoji_09);
        FACE_MAP.put("[尴尬]", R.mipmap.emoji_10);
        FACE_MAP.put("[发怒]", R.mipmap.emoji_11);
        FACE_MAP.put("[调皮]", R.mipmap.emoji_12);
        FACE_MAP.put("[呲牙]", R.mipmap.emoji_13);
        FACE_MAP.put("[惊讶]", R.mipmap.emoji_14);
        FACE_MAP.put("[难过]", R.mipmap.emoji_15);
        FACE_MAP.put("[酷]", R.mipmap.emoji_16);
        FACE_MAP.put("[冷汗]", R.mipmap.emoji_17);
        FACE_MAP.put("[抓狂]", R.mipmap.emoji_18);
        FACE_MAP.put("[吐]", R.mipmap.emoji_19);
        FACE_MAP.put("[偷笑]", R.mipmap.emoji_20);
        FACE_MAP.put("[愉快]", R.mipmap.emoji_21);
        FACE_MAP.put("[白眼]", R.mipmap.emoji_22);
        FACE_MAP.put("[傲慢]", R.mipmap.emoji_23);
        FACE_MAP.put("[饥饿]", R.mipmap.emoji_24);
        FACE_MAP.put("[困]", R.mipmap.emoji_25);
        FACE_MAP.put("[惊恐]", R.mipmap.emoji_26);
        FACE_MAP.put("[流汗]", R.mipmap.emoji_27);
        FACE_MAP.put("[憨笑]", R.mipmap.emoji_28);
        FACE_MAP.put("[悠闲]", R.mipmap.emoji_29);
        FACE_MAP.put("[奋斗]", R.mipmap.emoji_30);
        FACE_MAP.put("[咒骂]", R.mipmap.emoji_31);
        FACE_MAP.put("[疑问]", R.mipmap.emoji_32);
        FACE_MAP.put("[嘘]", R.mipmap.emoji_33);
        FACE_MAP.put("[晕]", R.mipmap.emoji_34);
        FACE_MAP.put("[疯了]", R.mipmap.emoji_35);
        FACE_MAP.put("[衰]", R.mipmap.emoji_36);
        FACE_MAP.put("[骷髅]", R.mipmap.emoji_37);
        FACE_MAP.put("[敲打]", R.mipmap.emoji_38);
        FACE_MAP.put("[再见]", R.mipmap.emoji_39);
        FACE_MAP.put("[擦汗]", R.mipmap.emoji_40);
        FACE_MAP.put("[抠鼻]", R.mipmap.emoji_41);
        FACE_MAP.put("[鼓掌]", R.mipmap.emoji_42);
        FACE_MAP.put("[糗大了]", R.mipmap.emoji_43);
        FACE_MAP.put("[坏笑]", R.mipmap.emoji_44);
        FACE_MAP.put("[左哼哼]", R.mipmap.emoji_45);
        FACE_MAP.put("[右哼哼]", R.mipmap.emoji_46);
        FACE_MAP.put("[哈欠]", R.mipmap.emoji_47);
        FACE_MAP.put("[鄙视]", R.mipmap.emoji_48);
        FACE_MAP.put("[委屈]", R.mipmap.emoji_49);
        FACE_MAP.put("[快哭]", R.mipmap.emoji_50);
        FACE_MAP.put("[阴险]", R.mipmap.emoji_51);
        FACE_MAP.put("[亲亲]", R.mipmap.emoji_52);
        FACE_MAP.put("[吓]", R.mipmap.emoji_53);
        FACE_MAP.put("[可怜]", R.mipmap.emoji_54);
        FACE_MAP.put("[菜刀]", R.mipmap.emoji_55);
        FACE_MAP.put("[西瓜]", R.mipmap.emoji_56);
        FACE_MAP.put("[啤酒]", R.mipmap.emoji_57);
        FACE_MAP.put("[篮球]", R.mipmap.emoji_58);
        FACE_MAP.put("[乒乓]", R.mipmap.emoji_59);

        FACE_LIST = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : FACE_MAP.entrySet()) {
            FACE_LIST.add(entry.getKey());
        }
    }

    public static List<String> getFaceList() {
        return FACE_LIST;
    }

    public static Integer getFaceImageRes(String key) {
        return FACE_MAP.get(key);
    }
}
