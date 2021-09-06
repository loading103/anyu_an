package com.yunbao.common.greendao;


import android.content.Context;

import com.yunbao.common.greendao.gen.DaoMaster;
import com.yunbao.common.greendao.gen.DaoSession;


public class GreenDaoManager {
    private static GreenDaoManager mInstance;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    static Context context;
    public GreenDaoManager(Context context) {
        this.context=context;
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(context, "notes-db", null);
        DaoMaster mDaoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();
    }

    public  static void init(Context context){
        if(mInstance == null) {
            mInstance = new GreenDaoManager(context);
        }
    }

    public static GreenDaoManager getInstance() {
        return mInstance;
    }

    public DaoMaster getMaster() {
        return mDaoMaster;
    }

    public DaoSession getSession() {
        return mDaoSession;
    }

    public DaoSession getNewSession() {
        mDaoSession = mDaoMaster.newSession();
        return mDaoSession;
    }
}
