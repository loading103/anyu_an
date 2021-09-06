package com.yunbao.common.download;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2020/9/22.
 * Describe:
 */
public class TasksManagerDBOpenHelper extends SQLiteOpenHelper {
    public final static String DATABASE_NAME = "tasksmanager.db";
    public final static int DATABASE_VERSION = 2;

    public TasksManagerDBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TasksManagerDBController.TABLE_NAME
                + String.format(
                "("
                        + "%s INTEGER PRIMARY KEY, " // id, download id
                        + "%s VARCHAR, " // name
                        + "%s VARCHAR, " // url
                        + "%s VARCHAR, " // path
                        + "%s VARCHAR, " // img
                        + "%s VARCHAR, " // vip
                        + "%s VARCHAR, " // size
                        + "%s VARCHAR, " // edit
                        + "%s VARCHAR, " // check
                        + "%s VARCHAR, " // file_name
                        + "%s VARCHAR, " // video_id
                        + "%s VARCHAR, " // file_name
                        + "%s VARCHAR, " // DOWN_CHECKED
                        + "%s VARCHAR " // finishzz
                        + ")"
                , TasksManagerModel.ID
                , TasksManagerModel.NAME
                , TasksManagerModel.URL
                , TasksManagerModel.PATH
                , TasksManagerModel.IMG
                , TasksManagerModel.VIP
                , TasksManagerModel.SIZE
                , TasksManagerModel.EDIT
                , TasksManagerModel.CHECKED
                , TasksManagerModel.FILE_NAME
                , TasksManagerModel.VIDEO_ID
                , TasksManagerModel.DOWN_CHECKED
                , TasksManagerModel.DOWN_EDIT
                , TasksManagerModel.FINISH
        ));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1 && newVersion == 2) {
            db.delete(TasksManagerDBController.TABLE_NAME, null, null);
        }
    }
}


