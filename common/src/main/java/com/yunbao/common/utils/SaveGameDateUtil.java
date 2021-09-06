package com.yunbao.common.utils;

import android.util.Log;

import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.bean.ShopRightBean;
import com.yunbao.common.greendao.GreenDaoUtils;
import com.yunbao.common.greendao.entity.ShopRightDbBean;
import com.yunbao.common.greendao.gen.ShopRightDbBeanDao;

import java.util.ArrayList;
import java.util.List;

/**
 * @author：jianxin 创建时间：2020/9/23
 *     private String id;
 *     private String name;
 *     private String pic;
 *     private String show_style;
 *     private String jump_url;
 *     private String jump_type;
 *     private String is_king;
 *     private String parent_id;
 *     private String userId;
 *     private long clickTime;
 */
public class SaveGameDateUtil {
    public  static void saveClickGame(ShopRightBean bean){
        if(bean==null){
            return;
        }
        ShopRightDbBean  dbBean=new ShopRightDbBean();
        dbBean.setId(bean.getId());
        dbBean.setName(bean.getName());
        dbBean.setPic(bean.getPic());
        dbBean.setShow_style(bean.getShow_style());
        dbBean.setJump_type(bean.getJump_type());
        dbBean.setJump_url(bean.getJump_url());
        dbBean.setIs_king(bean.getIs_king());
        dbBean.setParent_id(bean.getParent_id());
        dbBean.setUserId(CommonAppConfig.getInstance().getUid());
        dbBean.setClickTime(System.currentTimeMillis());
        GreenDaoUtils.getInstance().insertShopRightData(dbBean);
    }
    public  static List<ShopRightBean> getSaveGameList(List<ShopRightBean> rightdatas){
        try {
            List<ShopRightDbBean> list = GreenDaoUtils.getInstance().queryAllShopRightDbBean();
            if(rightdatas!=null && rightdatas.size()>0){
                if(rightdatas.get(0).getName().equals("我的应用")) {
                    rightdatas.remove(0);
                }
            }
            List<ShopRightDbBean> list1=new ArrayList<>();
            List<ShopRightDbBean> list2=new ArrayList<>();
            if(list!=null && list.size()>0){
                if(list.size()>6){
                    list1 .addAll(list.subList(0, 6));
                    list2.addAll(list.subList(6,list.size()));
                    for (int i = 0; i < list2.size(); i++) {
                        GreenDaoUtils.getInstance().deleteShopRightData(list2.get(i).getId());
                    }
                }else {
                    list1.addAll(list);
                }
                ShopRightBean bean=new ShopRightBean();
                bean.setName("我的应用");
                bean.setChildren(list1);
                if(rightdatas!=null){
                    rightdatas.add(0,bean);
                }
            }
            return rightdatas;
        }catch (Exception e){
            return rightdatas;
        }
    }
}
