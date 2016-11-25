package my.assignment.todoproject.Model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import my.assignment.todoproject.SmartPad;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 11/13/16.
 */

public class Note {
    public static final String TABLE_NAME = "note";
    public static final String COL_ID = "_id";
    public static final String COL_CREATEDTIME = "created_time";
    public static final String COL_MODIFIEDTIME = "modified_time";
    public static final String COL_LOCKED = "locked";
    public static final String COL_CATEGORYID = "category_id";
    public static final String COL_TITLE = "title";
    public static final String COL_CONTENT = "content";
    public static final String COL_TYPE = "type";

    public static final String BASIC = "basic";
    public static final String CHECKLIST = "checklist";
    //public static final String SNAPSHOT = "snapshot";

    public static final String CREATE_TABLE="CREATE TABLE "+TABLE_NAME+" ("+COL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+COL_CREATEDTIME+" INTEGER NOT NULL,"+COL_MODIFIEDTIME+" INTEGER NOT NULL,"+COL_LOCKED+" INTEGER NOT NULL,"+COL_CATEGORYID+" INTEGER NOT NULL,"+COL_TITLE+" TEXT NOT NULL,"+COL_CONTENT+" TEXT NOT NULL,"+COL_TYPE+" TEXT NOT NULL)";

    static String getSql() {
        return CREATE_TABLE;
    }

    public long insert(SQLiteDatabase db){
        ContentValues cv = new ContentValues();
        long now = System.currentTimeMillis();
        cv.put(COL_CREATEDTIME, now);
        cv.put(COL_MODIFIEDTIME, now);
        cv.put(COL_LOCKED, (isLocked!=null && isLocked) ? 1 : 0);
        cv.put(COL_CATEGORYID, categoryId);
        cv.put(COL_TITLE, title==null ? "" : title);
        cv.put(COL_CONTENT, content==null ? "" : content);
        cv.put(COL_TYPE, type==null ? BASIC : type);
       ;
        return db.insert(TABLE_NAME,null,cv);

    }
    boolean update(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        cv.put(COL_ID, noteId);
        cv.put(COL_MODIFIEDTIME, System.currentTimeMillis());
        if (categoryId > 0)
            cv.put(COL_CATEGORYID, categoryId);
        if (title != null)
            cv.put(COL_TITLE, title);
        if (content != null)
            cv.put(COL_CONTENT, content);
        if (type != null)
            cv.put(COL_TYPE, type);

        return db.update(TABLE_NAME, cv, COL_ID+" = ?", new String[]{String.valueOf(noteId)})
                == 1 ? true : false;
    }

    public boolean load(SQLiteDatabase db) {
        Cursor cursor = db.query(TABLE_NAME, null, COL_ID+" = ?", new String[]{String.valueOf(noteId)}, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                reset();
                noteId = cursor.getLong(cursor.getColumnIndex(COL_ID));
                createdTime = cursor.getLong(cursor.getColumnIndex(COL_CREATEDTIME));
                modifiedTime = cursor.getLong(cursor.getColumnIndex(COL_MODIFIEDTIME));
                categoryId = cursor.getLong(cursor.getColumnIndex(COL_CATEGORYID));
                title = cursor.getString(cursor.getColumnIndex(COL_TITLE));
                content = cursor.getString(cursor.getColumnIndex(COL_CONTENT));
                type = cursor.getString(cursor.getColumnIndex(COL_TYPE));
                return true;
            }
            return false;
        } finally {
            cursor.close();
        }
    }



    public static Cursor list(SQLiteDatabase db, String... args) {
        String categoryId = args!=null ? args[0] : null;

        String[] columns = {COL_ID, COL_LOCKED, COL_TITLE, COL_TYPE, COL_MODIFIEDTIME, COL_CREATEDTIME};
        String selection = "1 = 1";
        selection += !SmartPad.showLocked() ? " AND "+COL_LOCKED+" <> 1" : "";
        selection += categoryId!=null ? " AND "+COL_CATEGORYID+" = "+categoryId : "";
        String orderBy = (args!=null && args.length>1) ? args[1] :
                categoryId!=null ? COL_CREATEDTIME+" ASC" : COL_MODIFIEDTIME+" DESC";

        return db.query(TABLE_NAME, columns, selection, null, null, null, orderBy);

    }

    public static ArrayList<Note> getNotes(SQLiteDatabase db){
        ArrayList<Note> noteArrayList=new ArrayList<>();
        String[] columns = {COL_ID, COL_LOCKED, COL_TITLE, COL_TYPE, COL_MODIFIEDTIME, COL_CREATEDTIME};
        Cursor c=db.query(TABLE_NAME,columns,null,null,null,null,null);
        if(c.moveToFirst()){
            do{

                noteArrayList.add(new Note(c.getLong(c.getColumnIndex(COL_ID)),c.getString(c.getColumnIndex(COL_TITLE)),c.getLong(c.getColumnIndex(COL_CREATEDTIME))));
            }while (c.moveToNext());
        }
        return noteArrayList;
    }

    public boolean delete(SQLiteDatabase db) {
        boolean status = false;
        String[] whereArgs = new String[]{String.valueOf(noteId)};

        db.beginTransaction();
        try {
            db.delete(CheckItem.TABLE_NAME, CheckItem.COL_NOTEID+" = ?", whereArgs);
            status = db.delete(TABLE_NAME, COL_ID+" = ?", whereArgs)
                    == 1 ? true : false;
            db.setTransactionSuccessful();
        } catch (Exception e) {
        } finally {
            db.endTransaction();
        }
        return status;
    }

    public long persist(SQLiteDatabase db) {
        if (noteId > 0)
            return update(db) ? noteId : 0;
        else
            return insert(db);
    }



//------------------pojo for note------------------------
    private long categoryId;
    private String title;
    private String content;
    private String type;
    private List<CheckItem> checklist;
    private long createdTime;
    private long modifiedTime;
    private long noteId;
    Boolean isLocked=null;


    public void reset() {
        noteId=0;
        categoryId = 0;
        title = null;
        content = null;
        type = null;
        checklist = null;
        createdTime = 0;
        modifiedTime = 0;
        isLocked=null;
    }
    public Note(){

    }

    public Note(long id){
        noteId=id;
    }

    public Note(long id,String title,long createdTime){
        this.noteId=id;
        this.title=title;
        this.createdTime=createdTime;
    }

    public Boolean isLocked() {

        return isLocked != null ? isLocked : false;
    }

    public void setLocked(Boolean locked) {
        isLocked = locked;
    }


    public long getNoteId() {
        return noteId;
    }

    public void setNoteId(long noteId) {
        this.noteId = noteId;
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

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<CheckItem> getChecklist() {
        return checklist;
    }

    public void setChecklist(List<CheckItem> checklist) {
        this.checklist = checklist;
    }
}
