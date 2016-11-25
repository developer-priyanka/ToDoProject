package my.assignment.todoproject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.zip.Inflater;

import my.assignment.todoproject.Model.Category;
import my.assignment.todoproject.Model.CheckItem;
import my.assignment.todoproject.Model.Note;
import my.assignment.todoproject.R;

public class ChecklistActivity extends AppCompatActivity {
    Spinner spinner;
    ImageButton addItemBtn,doneBtn;
    EditText titleEdit,contentEdit;
    protected Typeface font;
    SQLiteDatabase db;
    protected LinearLayout checklistLL;
    Note note;
    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        findViews();
        long noteId = getIntent().getLongExtra(Note.COL_ID, 0);
        if (noteId > 0)
            note = new Note(noteId);
        else
            note = new Note();
        note.setType(Note.CHECKLIST);
        db=SmartPad.db;
        font = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Semibold.ttf");
        inflater = getLayoutInflater();

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

    public void findViews(){
        spinner = (Spinner) findViewById(R.id.spinner);
        addItemBtn = (ImageButton) findViewById(R.id.additem_btn);
        checklistLL = (LinearLayout) findViewById(R.id.checklist_ll);
        titleEdit = (EditText) findViewById(R.id.title_et);
        contentEdit = (EditText) findViewById(R.id.content_et);
        addItemBtn.setVisibility(View.VISIBLE);


    }
    public void onClick(View v) {
        LinearLayout checkitemLL;
        switch (v.getId()) {
            case R.id.category_btn:
                spinner.performClick();
                break;
            case R.id.done_btn:
                finish();
                break;
            case R.id.additem_btn:
                checkitemLL = (LinearLayout) inflater.inflate(R.layout.activity_check_list, null);
                EditText itemEdit = (EditText) checkitemLL.findViewById(R.id.item_et);
                itemEdit.setTypeface(font);
                checklistLL.addView(checkitemLL);
                break;

            case R.id.deleteitem_btn:
                checkitemLL = (LinearLayout) v.getParent();
                TextView itemId = (TextView) checkitemLL.findViewById(R.id.item_id);
                if (!TextUtils.isEmpty(itemId.getText()))
                    new CheckItem(Long.parseLong(itemId.getText().toString())).delete(db);
                checklistLL.removeView(checkitemLL);
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
        contentEdit.setVisibility(View.GONE);
        checklistLL.removeAllViews();

        if (note.getNoteId() > 0) {
            Cursor c = CheckItem.list(db, String.valueOf(note.getNoteId()));
            if (c.moveToFirst()) {
                LinearLayout checkitemLL;
                TextView itemId;
                CheckBox itemCb;
                EditText itemEdit;

                do {
                    checkitemLL = (LinearLayout) inflater.inflate(R.layout.activity_check_list, null);

                    itemId = (TextView) checkitemLL.findViewById(R.id.item_id);
                    itemCb = (CheckBox) checkitemLL.findViewById(R.id.item_cb);
                    itemEdit = (EditText) checkitemLL.findViewById(R.id.item_et);

                    itemId.setText(c.getString(c.getColumnIndex(CheckItem.COL_ID)));
                    itemCb.setChecked(c.getInt(c.getColumnIndex(CheckItem.COL_STATUS))==1 ? true : false);
                    itemEdit.setText(c.getString(c.getColumnIndex(CheckItem.COL_NAME)));
                    itemEdit.setTypeface(font);

                    checklistLL.addView(checkitemLL);
                } while(c.moveToNext());
            }
            c.close();
        }
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
        // persist check items
        int itemCount = checklistLL.getChildCount();
        LinearLayout checkitemLL;
        TextView itemId;
        CheckBox itemCb;
        EditText itemEdit;
        CheckItem checkitem;
        CharSequence id, edit;
        for (int i=0; i<itemCount; i++) {
            checkitemLL = (LinearLayout) checklistLL.getChildAt(i);
            itemId = (TextView) checkitemLL.findViewById(R.id.item_id);
            itemCb = (CheckBox) checkitemLL.findViewById(R.id.item_cb);
            itemEdit = (EditText) checkitemLL.findViewById(R.id.item_et);

            id = itemId.getText();
            edit = itemEdit.getText();

            if (TextUtils.isEmpty(id) && TextUtils.isEmpty(edit))
                continue;

            if (!TextUtils.isEmpty(id))
                checkitem = new CheckItem(Long.parseLong(id.toString()));
            else
                checkitem = new CheckItem();
            checkitem.setNoteId(note.getNoteId());
            checkitem.setStatus(itemCb.isChecked());
            checkitem.setName(edit.toString());
            checkitem.setCheckitemId(checkitem.persist(db));
        }
    }
    @Override
    protected void onPause() {
        if (note.getNoteId() > 0 || canSave())
            persist();
        super.onPause();
    }

    boolean canSave() {
        return !TextUtils.isEmpty(titleEdit.getText()) || checklistLL.getChildCount() > 0;
    }

}

