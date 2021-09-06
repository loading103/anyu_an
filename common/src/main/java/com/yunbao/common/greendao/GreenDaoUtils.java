package com.yunbao.common.greendao;


import android.text.TextUtils;
import android.util.Log;

import com.umeng.commonsdk.debug.E;
import com.yunbao.common.CommonAppConfig;
import com.yunbao.common.Constants;
import com.yunbao.common.greendao.entity.ShopRightDbBean;
import com.yunbao.common.greendao.entity.SocketListBean;
import com.yunbao.common.greendao.entity.SocketMessageBean;
import com.yunbao.common.greendao.gen.ShopRightDbBeanDao;
import com.yunbao.common.greendao.gen.SocketListBeanDao;
import com.yunbao.common.greendao.gen.SocketMessageBeanDao;

import java.util.List;


public class GreenDaoUtils {
    private static final String TAG = "GreenDaoManager";

    private static GreenDaoUtils instance;

    private final  ShopRightDbBeanDao shopRightDbBeanDao;
    private final  SocketListBeanDao  socketListBeanDao;
    private final  SocketMessageBeanDao  socketMessageBeanDao;
    public static GreenDaoUtils getInstance() {
        if (instance == null) {
            synchronized (GreenDaoManager.class) {
                if (instance == null) {
                    instance = new GreenDaoUtils();
                }
            }
        }
        return instance;
    }

    private GreenDaoUtils() {
        shopRightDbBeanDao = GreenDaoManager.getInstance().getSession().getShopRightDbBeanDao();
        socketMessageBeanDao = GreenDaoManager.getInstance().getSession().getSocketMessageBeanDao();
        socketListBeanDao= GreenDaoManager.getInstance().getSession().getSocketListBeanDao();
    }

    //-----------------------------------------------------------我的应用------------------------------------------------------------------------
    /**
     * 插入parent数据
     *
     * @param bean
     * @return
     */
    public void insertShopRightData(ShopRightDbBean bean) {
        try {
            if(bean==null){
                return;
            }
            ShopRightDbBean bean1 = queryShopRightData(bean.getId());
            if(bean1==null){
                shopRightDbBeanDao.insert(bean);
            }else {
                bean.set_id(bean1.get_id());
                shopRightDbBeanDao.update(bean);
            }
        } catch (Exception e) {
            Log.e("--------","我蹦啦");
            return;
        }
    }
    /**
     * 删除全部数据
     *
     */
    public void deleteShopRightData() {
        shopRightDbBeanDao.deleteAll();
    }

    /**
     * 删除一个数据
     */
    public void deleteShopRightData(String  id) {
        if(TextUtils.isEmpty(id)){
            return;
        }
        ShopRightDbBean bean = queryShopRightData(id);
        if(bean!=null){
            shopRightDbBeanDao.delete(bean);
        }
    }
    /**
     * 更新parent数据
     *
     * @param bean
     */
    public void updateShopRightDbBean(ShopRightDbBean bean) {
        shopRightDbBeanDao.update(bean);
    }
    /**
     *根据时间排序）降序
     * @return
     */
    public List<ShopRightDbBean> queryAllShopRightDbBean() {
        try{
            List<ShopRightDbBean> list = shopRightDbBeanDao.queryBuilder()
                    .where(ShopRightDbBeanDao.Properties.UserId.eq(CommonAppConfig.getInstance().getUid()))
                    .orderDesc(ShopRightDbBeanDao.Properties.ClickTime).list();
            return list;
        }catch (Exception e){
            return null;
        }
    }
    /**
     * 根据id查询查询用户列表
     */
    public ShopRightDbBean queryShopRightData(String gameId) {
        try{
            ShopRightDbBean bean = shopRightDbBeanDao.queryBuilder()
                    .where(ShopRightDbBeanDao.Properties.Id.eq(gameId))
                    .where(ShopRightDbBeanDao.Properties.UserId.eq(CommonAppConfig.getInstance().getUid()))
                    .build().unique();
            return bean;
        }catch (Exception e){
            return null;
        }
    }
    //-----------------------------------------------------------私信列表------------------------------------------------------------------------
    /**
     * 插入parent数据
     */
    public void insertSocketListData(SocketListBean bean) {
        if(bean==null){
            return;
        }
        bean.setCurrentUid(CommonAppConfig.getInstance().getUid());
        SocketListBean bean1 = querySocketListBean(bean.getUserId());
        if(bean1==null){
            socketListBeanDao.insert(bean);
        }
    }
    /**
     * 删除parent数据
     *
     * @param id
     */
    public void deleteSocketListData(Long id) {
        socketListBeanDao.deleteByKey(id);
    }
    public void deleteSocketListData(String userId) {
        SocketListBean bean = querySocketListBean(userId);
        if(bean==null){
            return;
        }
        socketListBeanDao.delete(bean);
    }
    public void deleteSocketListData(SocketListBean bean) {
        if(bean==null){
            return;
        }
        socketListBeanDao.delete(bean);
    }
    public void deleteSocketListAllData() {
        socketListBeanDao.deleteAll();
    }
    /**
     * 更新parent数据
     *
     * @param bean
     */
    public void updateSocketListData(SocketListBean bean) {
        SocketListBean bean1 = querySocketListBean(bean.getUserId());
        if(bean1==null){
            socketListBeanDao.insert(bean);
        }else {
            socketListBeanDao.update(bean);
        }
    }
    /**
     * 查询所有的user数据
     *根据时间排序）降序
     * @return
     */
    public List<SocketListBean> queryAllSocketList() {
        try {
            List<SocketListBean> list = socketListBeanDao.queryBuilder().where(SocketListBeanDao.Properties
                    .CurrentUid.eq(CommonAppConfig.getInstance().getUid()))
                    .orderDesc(SocketListBeanDao.Properties.Time).list();
            return list;
        }catch (Exception e){
            return null;
        }
    }
    public List<SocketListBean> queryAllSocketList(int size) {
        try {
            List<SocketListBean> list = socketListBeanDao.queryBuilder()
                    .where(SocketListBeanDao.Properties.CurrentUid.eq(CommonAppConfig.getInstance().getUid()))
                    .limit(size).orderDesc(SocketListBeanDao.Properties.Time).list();
            return list;
        }catch (Exception e){
            return null;
        }
    }
    /**
     * 根据id查询查询用户列表
     */
    public SocketListBean querySocketListBean(String userId) {
        try {
            SocketListBean bean = socketListBeanDao.queryBuilder()
                    .where(SocketListBeanDao.Properties.CurrentUid.eq(CommonAppConfig.getInstance().getUid()))
                    .where(SocketListBeanDao.Properties.UserId.eq(userId)).build().unique();
            return bean;
        }catch (Exception e){
            return null;
        }
    }

    //-----------------------------------------------------------私信消息------------------------------------------------------------------------
    /**
     * 插入数据
     *
     */
    public void insertSocketMessageData(SocketMessageBean bean) {
        SocketMessageBean data = querySocketMessageDataAccordFieldFiger(bean.getMsgId());
        if(data==null){
            bean.setCurrentUid(CommonAppConfig.getInstance().getUid());
            socketMessageBeanDao.insert(bean);
        }
    }
    /**
     * 删除children数据
     */
    public void deleteSocketMessageData(Long id) {
        socketMessageBeanDao.deleteByKey(id);
    }
    public void deleteSocketMessageData(SocketMessageBean bean) {
        socketMessageBeanDao.delete(bean);
    }
    /**
     * 删除某个userId下对应的所有message
     */
    public void deleteSocketMessageAccordField(String userId) {
        List<SocketMessageBean> beans = querySocketMessageDataAccordField(userId);
        if (beans == null) {
            return;
        }
        for (SocketMessageBean bean: beans) {
            deleteSocketMessageData(bean);
        }
    }
    /**
     * 更新children数据
     */
    public void updateSocketMessageData(SocketMessageBean bean) {
        SocketMessageBean bean1 = querySocketMessageDataAccordFieldFiger(bean.getMsgId());
        if(bean1==null){
            socketMessageBeanDao.insert(bean);
        }else {
            socketMessageBeanDao.update(bean);
        }
    }

    /**
     * 查询某个userId下对应的所有message
     * @return
     */
    public List<SocketMessageBean> querySocketMessageDataAccordField(String userId) {
        try {
            List<SocketMessageBean> list = socketMessageBeanDao.queryBuilder()
                    .where(SocketMessageBeanDao.Properties.UserId.eq(userId))
                    .where(SocketMessageBeanDao.Properties.CurrentUid.eq(CommonAppConfig.getInstance().getUid()))
                    .orderAsc(SocketMessageBeanDao.Properties.Time)
                    .list();
            return list;
        } catch (Exception e) {
            return null;
        }
    }

    public List<SocketMessageBean> querySocketMessageDataAccordField(String userId,int offset,int pageSize) {
        try {
            List<SocketMessageBean> list = socketMessageBeanDao.queryBuilder()
                    .where(SocketMessageBeanDao.Properties.UserId.eq(userId))
                    .where(SocketMessageBeanDao.Properties.CurrentUid.eq(CommonAppConfig.getInstance().getUid()))
                    .orderDesc(SocketMessageBeanDao.Properties.Time)
                    .offset(offset).limit(pageSize)
                    .list();
            return list;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取某个消息（根据消息Id）
     */
    public SocketMessageBean querySocketMessageDataAccordFieldFiger(String msgId) {
        try {
            SocketMessageBean list = socketMessageBeanDao.queryBuilder()
                    .where(SocketMessageBeanDao.Properties.MsgId.eq(msgId))
                    .where(SocketMessageBeanDao.Properties.CurrentUid.eq(CommonAppConfig.getInstance().getUid()))
                    .unique();
            return list;
        }catch (Exception e){
            return null;
        }
    }

    /**
     * 获取某个人的消息列表（根据人的userId）
     */
    public List<SocketMessageBean> querySocketMessageorderAscrdField(String userId) {
        try {
            List<SocketMessageBean> list = socketMessageBeanDao.queryBuilder()
                    .where(SocketMessageBeanDao.Properties.UserId.eq(userId))
                    .where(SocketMessageBeanDao.Properties.CurrentUid.eq(CommonAppConfig.getInstance().getUid()))
                    .orderAsc(SocketMessageBeanDao.Properties.Time)
                    .list();
            return list;
        } catch (Exception e) {
            return null;
        }
    }

}
