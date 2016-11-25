package my.assignment.todoproject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import my.assignment.todoproject.Model.Category;
import my.assignment.todoproject.Model.Note;

import static my.assignment.todoproject.R.id.note_list;
import static my.assignment.todoproject.R.id.spinner;

public class NoteActivity extends AppCompatActivity {
    Spinner spinner;
    ImageButton doneBtn;
    EditText titleEdit,contentEdit;
    SQLiteDatabase db;
    protected Typeface font;
    Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        findViews();

        db=SmartPad.db;
        font = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Semibold.ttf");
        // create instance
        long noteId = getIntent().getLongExtra(Note.COL_ID, 0);
        if (noteId > 0)
            note = new Note(noteId);
        else
            note = new Note();

        Cursor c = Category.list(db);
        startManagingCursor(c);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_spinner_item,
                c,
                new String[]{Category.COL_NAME},
                new int[]{android.R.id.text1});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                note.setCategoryId(id);
                SmartPad.LASTSELECTED_CATEGORYID = id;
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (note.getNoteId() > 0)
            outState.putLong(Note.COL_ID, note.getNoteId());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.containsKey(Note.COL_ID))
            note.setNoteId(savedInstanceState.getLong(Note.COL_ID));
    }

    @Override
    protected void onResume() {
        super.onResume();

        // load data
        if (note.getNoteId() > 0)
            note.load(db);

        // initialize views
        reset();
    }
    protected void findViews() {
        spinner = (Spinner) findViewById(R.id.spinner);
        titleEdit = (EditText) findViewById(R.id.title_et);
        contentEdit = (EditText) findViewById(R.id.content_et);
        doneBtn = (ImageButton) findViewById(R.id.done_btn);


    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.category_btn:
                spinner.performClick();
                break;
            case R.id.additem_btn:
                break;
            case R.id.done_btn:
                finish();
                break;
        }
    }
    protected void reset() {
        SpinnerAdapter adapter = spinner.getAdapter();
        int count = adapter.getCount();
        long categoryId = note.getCategoryId()>0 ? note.getCategoryId() : getDefaultCategoryId();
        for (int i=0; i<count; i++) {
            if (adapter.getItemId(i) == categoryId) {
                spinner.setSelection(i);
                break;
            }
        }

        titleEdit.setText(note.getTitle());
        contentEdit.setText(note.getContent());
    }

    private long getDefaultCategoryId() {
        SpinnerAdapter adapter = spinner.getAdapter();
        int count = adapter.getCount();

        switch(SmartPad.getDefaultCategoryOpt()) {
            case 1:
                for (int i=0; i<count; i++) {
                    if (adapter.getItemId(i) == SmartPad.LASTCREATED_CATEGORYID) {
                        return SmartPad.LASTCREATED_CATEGORYID;
                    }
                }
                break;
            case 2:
                for (int i=0; i<count; i++) {
                    if (adapter.getItemId(i) == SmartPad.LASTSELECTED_CATEGORYID) {
                        return SmartPad.LASTSELECTED_CATEGORYID;
                    }
                }
                break;
        }

        return SmartPad.PUBLIC_CATEGORYID;
    }
    protected void persist() {
        note.setTitle(titleEdit.getText().toString());
        note.setContent(contentEdit.getText().toString());
        note.setNoteId(note.persist(db));
    }

    @Override
    protected void onPause() {
        if (note.getNoteId() > 0 || canSave())
            persist();
        super.onPause();
    }
    boolean canSave() {
        return !TextUtils.isEmpty(titleEdit.getText()) || !TextUtils.isEmpty(contentEdit.getText());
    }
}
