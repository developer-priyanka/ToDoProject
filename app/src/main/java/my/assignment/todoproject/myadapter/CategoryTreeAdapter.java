package my.assignment.todoproject.myadapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;

import my.assignment.todoproject.Model.Category;
import my.assignment.todoproject.Model.Note;
import my.assignment.todoproject.R;
import my.assignment.todoproject.SmartPad;


/**
 * Created by root on 11/15/16.
 */

public class CategoryTreeAdapter extends SimpleCursorTreeAdapter {
    Context context;

    public CategoryTreeAdapter(Context context, Cursor cursor) {
        super(
                context,
                cursor,
                R.layout.category,
                new String[]{Category.COL_NAME},
                new int[]{android.R.id.text1},
                R.layout.category_note,
                new String[]{Note.COL_TITLE},
                new int[]{android.R.id.text1});
        this.context=context;
    }

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {
        Cursor c = Note.list(SmartPad.db, groupCursor.getString(groupCursor.getColumnIndex(Category.COL_ID)));
        return c;

    }

    @Override
    protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {
        super.bindGroupView(view, context, cursor, isExpanded);

        boolean showLock = cursor.getInt(cursor.getColumnIndex(Note.COL_LOCKED)) == 1 ? true : false;

        ((TextView)view.findViewById(android.R.id.text1)).setCompoundDrawablesWithIntrinsicBounds(
                showLock ? R.drawable.folder_locked : R.drawable.folder,
                0,
                0,
                0);
    }

    @Override
    protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
        super.bindChildView(view, context, cursor, isLastChild);

        boolean showLock = getCursor().getInt(cursor.getColumnIndex(Note.COL_LOCKED)) == 1 ? true : false;
        // if group is locked then show lock on a child
        showLock = showLock || cursor.getInt(cursor.getColumnIndex(Note.COL_LOCKED)) == 1 ? true : false;

        ((TextView)view.findViewById(android.R.id.text1)).setCompoundDrawablesWithIntrinsicBounds(
                showLock ? R.drawable.file_locked : R.drawable.file,
                0,
                0,
                0);
    }

}

