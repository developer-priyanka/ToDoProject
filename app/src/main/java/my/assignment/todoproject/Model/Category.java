package my.assignment.todoproject.Model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import static java.sql.Types.INTEGER;

/**
 * Created by root on 11/13/16.
 */

public class Category {
    public static final String TABLE_NAME = "category";
    public static final String COL_ID = "_id";
    public static final String COL_CREATEDTIME = "created_time";
    public static final String COL_MODIFIEDTIME = "modified_time";
    public static final String COL_LOCKED = "locked";
    public static final String COL_NAME = "name";
    public static final String CREATE_TABLE="CREATE TABLE "+TABLE_NAME+" ("+COL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+COL_CREATEDTIME+" INTEGER NOT NULL,"+COL_MODIFIEDTIME+" INTEGER NOT NULL,"+COL_LOCKED+" INTEGER NOT NULL,"+COL_NAME+" TEXT NOT NULL)";

    static String getSql() {
        return CREATE_TABLE;
    }

    public long insert(SQLiteDatabase db){
        ContentValues cv = new ContentValues();
        long now = System.currentTimeMillis();
        cv.put(COL_CREATEDTIME, now);
        cv.put(COL_MODIFIEDTIME, now);
        cv.put(COL_LOCKED, (isLocked != null && isLocked) ? 1 : 0);
        if (name != null)
            cv.put(COL_NAME, name);
        return db.insert(TABLE_NAME,null,cv);

    }


    boolean update(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        cv.put(COL_ID, id);
        cv.put(COL_MODIFIEDTIME, System.currentTimeMillis());
        if (isLocked != null)
            cv.put(COL_LOCKED, isLocked ? 1 : 0);
        cv.put(COL_NAME, name==null ? "" : name);
        return db.update(TABLE_NAME, cv, COL_ID+" = ?", new String[]{String.valueOf(id)})
                == 1 ? true : false;
    }

    public boolean load(SQLiteDatabase db) {
        Cursor cursor = db.query(TABLE_NAME, null, COL_ID+" = ?", new String[]{String.valueOf(id)}, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                reset();
                id = cursor.getLong(cursor.getColumnIndex(COL_ID));
                createdTime = cursor.getLong(cursor.getColumnIndex(COL_CREATEDTIME));
                modifiedTime = cursor.getLong(cursor.getColumnIndex(COL_MODIFIEDTIME));
                isLocked = cursor.getInt(cursor.getColumnIndex(COL_LOCKED)) == 1 ? true : false;
                name = cursor.getString(cursor.getColumnIndex(COL_NAME));
                return true;
            }
            return false;
        } finally {
            cursor.close();
        }
    }

    public static Cursor list(SQLiteDatabase db) {
        String[] columns = {COL_ID, COL_LOCKED, COL_NAME};
       // String selection = !SmartPad.showLocked() ? COL_LOCKED+" <> 1" : null;
        String selection = null;

        return db.query(TABLE_NAME, columns, selection, null, null, null, COL_CREATEDTIME+" ASC");

    }


    public long persist(SQLiteDatabase db) {
        if (id > 0)
            return update(db) ? id : 0;
        else
            return insert(db);
    }


    public boolean delete(SQLiteDatabase db) {
        boolean status = false;
        String[] whereArgs = new String[]{String.valueOf(id)};
        String whereClause =
                CheckItem.COL_NOTEID + " IN (SELECT "+ Note.COL_ID + " FROM "+ Note.TABLE_NAME+ " WHERE "+Note.COL_CATEGORYID+ " = ?)";



        db.beginTransaction();
        try {
            db.delete(CheckItem.TABLE_NAME, whereClause, whereArgs);
            db.delete(Note.TABLE_NAME, Note.COL_CATEGORYID+" = ?", whereArgs);
            status = db.delete(TABLE_NAME, COL_ID+" = ?", whereArgs)
                    == 1 ? true : false;
            db.setTransactionSuccessful();
        } catch (Exception e) {
        } finally {
            db.endTransaction();
        }
        return status;
    }


    //------------pojo for category------------------------
     Boolean isLocked=null;
     String name=null;
     long createdTime;
     long modifiedTime;
     long id;

    public void reset() {
        id = 0;
        createdTime = 0;
        modifiedTime = 0;
        isLocked = null;
    }


    public Category() {}

    public Category(long id) {
        this.id = id;
    }
    public Category(String name,Boolean check){
        this.name=name;
        isLocked=check;
    }


    public Boolean isLocked() {
        return isLocked!=null ? isLocked : false;
    }

    public void setLocked(Boolean locked) {
        isLocked = locked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public long getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(long modifiedTime) {
        this.modifiedTime = modifiedTime;
    }
}
