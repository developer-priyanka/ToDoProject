package my.assignment.todoproject.Model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import my.assignment.todoproject.SmartPad;

/**
 * Created by root on 11/13/16.
 */

public class DbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "smartpad.db";
    public static final int DB_VERSION = 1;

    public DbHelper(Context context) {

        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Category.getSql());
        db.execSQL(Note.getSql());
        db.execSQL(CheckItem.getSql());
        populateData(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + Category.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Note.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CheckItem.TABLE_NAME);
        onCreate(db);

    }

    private void populateData(SQLiteDatabase db) {

        // Public
        Category category = new Category();
        category.setName("Public");
        long categoryId = category.insert(db);
        SmartPad.PUBLIC_CATEGORYID = categoryId;

        Note note = new Note();
        note.setCategoryId(categoryId);
        note.setTitle("Read me");
        note.setType(Note.BASIC);
        note.setContent("This app allows you to create notes and checklists quickly and easily. \n\nAdditionally, you may create folders to organize your notes.you can search notes. Change default settings in the Settings page. \n\nRemember to long press an item to see more options.");
        note.insert(db);

        // Personal
        category.reset();
        category.setName("Personal");
        category.setLocked(false);
        categoryId = category.insert(db);

        note.reset();
        note.setCategoryId(categoryId);
        note.setTitle("To do");
        note.setType(Note.CHECKLIST);
        long noteId = note.insert(db);
        CheckItem ci = new CheckItem();
        ci.setNoteId(noteId);
        ci.setName("check Settings page");
        ci.insert(db);
        ci.reset();
        ci.setNoteId(noteId);
        ci.setName("Rate this app on Google Play");
        ci.insert(db);
        ci.reset();
        ci.setNoteId(noteId);
        ci.setName("Visit www.acadgild.com");
        ci.insert(db);

    }
}
