package com.yunbao.common.greendao.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.yunbao.common.greendao.entity.SocketMessageBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "SOCKET_MESSAGE_BEAN".
*/
public class SocketMessageBeanDao extends AbstractDao<SocketMessageBean, Long> {

    public static final String TABLENAME = "SOCKET_MESSAGE_BEAN";

    /**
     * Properties of entity SocketMessageBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property _id = new Property(0, Long.class, "_id", true, "_id");
        public final static Property MsgId = new Property(1, String.class, "msgId", false, "MSG_ID");
        public final static Property Content = new Property(2, String.class, "content", false, "CONTENT");
        public final static Property SendUid = new Property(3, String.class, "sendUid", false, "SEND_UID");
        public final static Property TargetUid = new Property(4, String.class, "targetUid", false, "TARGET_UID");
        public final static Property Time = new Property(5, String.class, "time", false, "TIME");
        public final static Property Type = new Property(6, String.class, "type", false, "TYPE");
        public final static Property Url = new Property(7, String.class, "url", false, "URL");
        public final static Property Avatar = new Property(8, String.class, "avatar", false, "AVATAR");
        public final static Property Nickname = new Property(9, String.class, "nickname", false, "NICKNAME");
        public final static Property CurrentUid = new Property(10, String.class, "currentUid", false, "CURRENT_UID");
        public final static Property UserId = new Property(11, String.class, "userId", false, "USER_ID");
        public final static Property SendState = new Property(12, String.class, "sendState", false, "SEND_STATE");
        public final static Property SendType = new Property(13, String.class, "sendType", false, "SEND_TYPE");
    }


    public SocketMessageBeanDao(DaoConfig config) {
        super(config);
    }
    
    public SocketMessageBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"SOCKET_MESSAGE_BEAN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: _id
                "\"MSG_ID\" TEXT," + // 1: msgId
                "\"CONTENT\" TEXT," + // 2: content
                "\"SEND_UID\" TEXT," + // 3: sendUid
                "\"TARGET_UID\" TEXT," + // 4: targetUid
                "\"TIME\" TEXT," + // 5: time
                "\"TYPE\" TEXT," + // 6: type
                "\"URL\" TEXT," + // 7: url
                "\"AVATAR\" TEXT," + // 8: avatar
                "\"NICKNAME\" TEXT," + // 9: nickname
                "\"CURRENT_UID\" TEXT," + // 10: currentUid
                "\"USER_ID\" TEXT," + // 11: userId
                "\"SEND_STATE\" TEXT," + // 12: sendState
                "\"SEND_TYPE\" TEXT);"); // 13: sendType
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"SOCKET_MESSAGE_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, SocketMessageBean entity) {
        stmt.clearBindings();
 
        Long _id = entity.get_id();
        if (_id != null) {
            stmt.bindLong(1, _id);
        }
 
        String msgId = entity.getMsgId();
        if (msgId != null) {
            stmt.bindString(2, msgId);
        }
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(3, content);
        }
 
        String sendUid = entity.getSendUid();
        if (sendUid != null) {
            stmt.bindString(4, sendUid);
        }
 
        String targetUid = entity.getTargetUid();
        if (targetUid != null) {
            stmt.bindString(5, targetUid);
        }
 
        String time = entity.getTime();
        if (time != null) {
            stmt.bindString(6, time);
        }
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(7, type);
        }
 
        String url = entity.getUrl();
        if (url != null) {
            stmt.bindString(8, url);
        }
 
        String avatar = entity.getAvatar();
        if (avatar != null) {
            stmt.bindString(9, avatar);
        }
 
        String nickname = entity.getNickname();
        if (nickname != null) {
            stmt.bindString(10, nickname);
        }
 
        String currentUid = entity.getCurrentUid();
        if (currentUid != null) {
            stmt.bindString(11, currentUid);
        }
 
        String userId = entity.getUserId();
        if (userId != null) {
            stmt.bindString(12, userId);
        }
 
        String sendState = entity.getSendState();
        if (sendState != null) {
            stmt.bindString(13, sendState);
        }
 
        String sendType = entity.getSendType();
        if (sendType != null) {
            stmt.bindString(14, sendType);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, SocketMessageBean entity) {
        stmt.clearBindings();
 
        Long _id = entity.get_id();
        if (_id != null) {
            stmt.bindLong(1, _id);
        }
 
        String msgId = entity.getMsgId();
        if (msgId != null) {
            stmt.bindString(2, msgId);
        }
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(3, content);
        }
 
        String sendUid = entity.getSendUid();
        if (sendUid != null) {
            stmt.bindString(4, sendUid);
        }
 
        String targetUid = entity.getTargetUid();
        if (targetUid != null) {
            stmt.bindString(5, targetUid);
        }
 
        String time = entity.getTime();
        if (time != null) {
            stmt.bindString(6, time);
        }
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(7, type);
        }
 
        String url = entity.getUrl();
        if (url != null) {
            stmt.bindString(8, url);
        }
 
        String avatar = entity.getAvatar();
        if (avatar != null) {
            stmt.bindString(9, avatar);
        }
 
        String nickname = entity.getNickname();
        if (nickname != null) {
            stmt.bindString(10, nickname);
        }
 
        String currentUid = entity.getCurrentUid();
        if (currentUid != null) {
            stmt.bindString(11, currentUid);
        }
 
        String userId = entity.getUserId();
        if (userId != null) {
            stmt.bindString(12, userId);
        }
 
        String sendState = entity.getSendState();
        if (sendState != null) {
            stmt.bindString(13, sendState);
        }
 
        String sendType = entity.getSendType();
        if (sendType != null) {
            stmt.bindString(14, sendType);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public SocketMessageBean readEntity(Cursor cursor, int offset) {
        SocketMessageBean entity = new SocketMessageBean( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // _id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // msgId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // content
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // sendUid
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // targetUid
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // time
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // type
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // url
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // avatar
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // nickname
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // currentUid
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // userId
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // sendState
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13) // sendType
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, SocketMessageBean entity, int offset) {
        entity.set_id(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setMsgId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setContent(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setSendUid(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setTargetUid(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setTime(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setType(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setUrl(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setAvatar(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setNickname(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setCurrentUid(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setUserId(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setSendState(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setSendType(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(SocketMessageBean entity, long rowId) {
        entity.set_id(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(SocketMessageBean entity) {
        if(entity != null) {
            return entity.get_id();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(SocketMessageBean entity) {
        return entity.get_id() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}