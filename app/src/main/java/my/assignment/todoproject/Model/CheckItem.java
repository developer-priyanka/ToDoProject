package my.assignment.todoproject.Model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by root on 11/13/16.
 */

public class CheckItem {

    public static final String TABLE_NAME = "checkitem";
    public static final String COL_ID = "_id";
    public static final String COL_NOTEID = "note_id";
    public static final String COL_NAME = "name";
    public static final String COL_STATUS = "status";
    public static final String CREATE_TABLE="CREATE TABLE "+TABLE_NAME+" ("+COL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+COL_NOTEID+" INTEGER NOT NULL,"+COL_NAME+" TEXT NOT NULL,"+COL_STATUS+" TEXT NOT NULL)";

    static String getSql() {
        return CREATE_TABLE;
    }

    long insert(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        cv.put(COL_NOTEID, noteId);
        cv.put(COL_NAME, name==null ? "" : name);
        cv.put(COL_STATUS, status ? 1 : 0);

        return db.insert(TABLE_NAME, null, cv);
    }

    boolean update(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        cv.put(COL_ID, checkitemId);
        if (noteId > 0)
            cv.put(COL_NOTEID, noteId);
        if (name != null)
            cv.put(COL_NAME, name);
        if (status != null)
            cv.put(COL_STATUS, status ? 1 : 0);

        return db.update(TABLE_NAME, cv, COL_ID+" = ?", new String[]{String.valueOf(checkitemId)})
                == 1 ? true : false;
    }

    public boolean load(SQLiteDatabase db) {
        Cursor cursor = db.query(TABLE_NAME, null, COL_ID+" = ?", new String[]{String.valueOf(checkitemId)}, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                reset();
                checkitemId = cursor.getLong(cursor.getColumnIndex(COL_ID));
                noteId = cursor.getLong(cursor.getColumnIndex(COL_NOTEID));
                name = cursor.getString(cursor.getColumnIndex(COL_NAME));
                status = cursor.getInt(cursor.getColumnIndex(COL_STATUS)) == 1 ? true : false;
                return true;
            }
            return false;
        } finally {
            cursor.close();
        }
    }

    public static Cursor list(SQLiteDatabase db, String noteId) {
        if (noteId != null)
            return db.query(TABLE_NAME, null, COL_NOTEID+" = ?", new String[]{noteId}, null, null, COL_ID+" ASC");
        else
            return null;
    }

    public boolean delete(SQLiteDatabase db) {
        return db.delete(TABLE_NAME, COL_ID+" = ?", new String[]{String.valueOf(checkitemId)})
                == 1 ? true : false;
    }



   /// -------------------------pojo for checkitem-----------

    private long noteId;
    private String name;
    private Boolean status = Boolean.FALSE;
    private long checkitemId;

    public void reset() {
        checkitemId = 0;
        noteId = 0;
        name = null;
        status = Boolean.FALSE;
    }

    public long getCheckitemId() {
        return checkitemId;
    }

    public void setCheckitemId(long checkitemId) {
        this.checkitemId = checkitemId;
    }

    public long getNoteId() {
        return noteId;
    }
    public void setNoteId(long noteId) {
        this.noteId = noteId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Boolean getStatus() {
        return status;
    }
    public void setStatus(Boolean status) {
        this.status = status;
    }

    public CheckItem() {}

    public CheckItem(long id) {
        this.checkitemId = id;
    }

    public long persist(SQLiteDatabase db) {
        if (checkitemId > 0)
            return update(db) ? checkitemId : 0;
        else
            return insert(db);
    }

}
