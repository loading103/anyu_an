package com.yunbao.common.download;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.yunbao.common.CommonAppContext;
import com.yunbao.common.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2020/9/22.
 * Describe:
 */
public class TasksManagerDBController {
    public final static String TABLE_NAME = "tasksmanger";
    private final SQLiteDatabase db;

    public TasksManagerDBController() {
        TasksManagerDBOpenHelper openHelper = new TasksManagerDBOpenHelper(CommonAppContext.sInstance);
        db = openHelper.getWritableDatabase();
    }

    public List<TasksManagerModel> getAllTasks() {
        final Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        final List<TasksManagerModel> list = new ArrayList<>();
        try {
            if (!c.moveToLast()) {
                return list;
            }

            do {
                TasksManagerModel model = new TasksManagerModel();
                model.setId(c.getInt(c.getColumnIndex(TasksManagerModel.ID)));
                model.setName(c.getString(c.getColumnIndex(TasksManagerModel.NAME)));
                model.setUrl(c.getString(c.getColumnIndex(TasksManagerModel.URL)));
                model.setPath(c.getString(c.getColumnIndex(TasksManagerModel.PATH)));
                model.setVip(c.getString(c.getColumnIndex(TasksManagerModel.VIP)));
                model.setSize(c.getString(c.getColumnIndex(TasksManagerModel.SIZE)));
                model.setImg(c.getString(c.getColumnIndex(TasksManagerModel.IMG)));
                model.setChecked(c.getString(c.getColumnIndex(TasksManagerModel.CHECKED)));
                model.setEdit(c.getString(c.getColumnIndex(TasksManagerModel.EDIT)));
                model.setFile_name(c.getString(c.getColumnIndex(TasksManagerModel.FILE_NAME)));
                model.setVideo_id(c.getString(c.getColumnIndex(TasksManagerModel.VIDEO_ID)));
                model.setFinish(c.getString(c.getColumnIndex(TasksManagerModel.FINISH)));
                list.add(model);
            } while (c.moveToPrevious());
        } finally {
            if (c != null) {
                c.close();
            }
        }


        return list;
    }

    public TasksManagerModel addTask(final String url, final String path) {
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(path)) {
            return null;
        }

        // have to use FileDownloadUtils.generateId to associate TasksManagerModel with FileDownloader
        final int id = FileDownloadUtils.generateId(url, path);

        TasksManagerModel model = new TasksManagerModel();
        model.setId(id);
        model.setName(CommonAppContext.sInstance.getString(R.string.tasks_manager_demo_name, id));
        model.setUrl(url);
        model.setPath(path);
//        model.setImg(path);
//        model.setSize(path);
//        model.setVip(path);
//        model.setChecked(path);
//        model.setEdit(path);

        final boolean succeed = db.insert(TABLE_NAME, null, model.toContentValues()) != -1;
        return succeed ? model : null;
    }

    public TasksManagerModel addTask(TasksManagerModel tasksManagerModel) {
        if(tasksManagerModel==null){
            return null;
        }

        if (TextUtils.isEmpty(tasksManagerModel.getUrl()) || TextUtils.isEmpty(tasksManagerModel.getPath())) {
            return null;
        }

        // have to use FileDownloadUtils.generateId to associate TasksManagerModel with FileDownloader
        final int id = FileDownloadUtils.generateId(tasksManagerModel.getUrl(), tasksManagerModel.getPath());

        tasksManagerModel.setId(id);

        final boolean succeed = db.insert(TABLE_NAME, null, tasksManagerModel.toContentValues()) != -1;
        return succeed ? tasksManagerModel : null;
    }

    public TasksManagerModel deleteTask(TasksManagerModel tasksManagerModel) {
        if(tasksManagerModel==null){
            return null;
        }

        if (TextUtils.isEmpty(tasksManagerModel.getUrl()) || TextUtils.isEmpty(tasksManagerModel.getPath())) {
            return null;
        }

//        final boolean succeed = db.insert(TABLE_NAME, null, tasksManagerModel.toContentValues()) != -1;
        String[] s=new String[]{tasksManagerModel.getId()+""};
        final boolean succeed = db.delete(TABLE_NAME, "id=?",s) != -1;
        return succeed ? tasksManagerModel : null;
    }
}
