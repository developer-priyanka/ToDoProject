package my.assignment.todoproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;

import my.assignment.todoproject.Model.Category;
import my.assignment.todoproject.Model.Note;

/**
 * Created by root on 11/11/16.
 */

public class BrowseFragment extends Fragment {
    private ListView noteList;
    private TextView emptytxt;
    private ImageButton newBtn,sortBtn;
    private AlertDialog newBtnDialog;
    private AlertDialog sortBtnDialog;
    private SQLiteDatabase db;
    private final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
    private final Calendar cal = Calendar.getInstance();

    View rootView;

    private DialogInterface.OnClickListener dialogListener=new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            if (dialogInterface == newBtnDialog) {
                Intent intent = new Intent();
                switch (i) {
                    case 0:
                        intent.setClass(getContext(), NoteActivity.class);
                        break;
                    case 1:
                        intent.setClass(getContext(), ChecklistActivity.class);
                        break;

                }
                startActivity(intent);

            } else if (dialogInterface == sortBtnDialog) {
                Cursor c = Note.list(db, null, getOrderBy(i));
                getActivity().startManagingCursor(c);
                ((SimpleCursorAdapter)noteList.getAdapter()).changeCursor(c);

            }

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         rootView=inflater.inflate(R.layout.fragment_browse,container,false);
        findViews(rootView);
        db=SmartPad.db;

        newBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newBtnDialog.show();

            }
        });
        sortBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortBtnDialog.show();

            }
        });

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());

        builder.setTitle("Choose an Option");
        builder.setItems(R.array.new_note_arr, dialogListener);

        newBtnDialog = builder.create();

        builder.setTitle("Sort by");
        builder.setItems(R.array.sort_options_arr, dialogListener);
        sortBtnDialog = builder.create();

        registerForContextMenu(noteList);

        noteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                openNote(l, getContext());

            }
        });


        return rootView;
    }

    private String getOrderBy(int which) {
        String orderBy = null;
        switch (which) {
            case 0:
                orderBy = Note.COL_MODIFIEDTIME + " DESC";
                break;
            case 1:
                orderBy = Note.COL_TITLE + " COLLATE NOCASE ASC";
                break;
            case 2:
                orderBy = Note.COL_CREATEDTIME + " ASC";
                break;
        }
        return orderBy;
    }

    @Override
    public void onResume() {
        super.onResume();


        Cursor c = Note.list(db, null, getOrderBy(SmartPad.getDefaultSort()));
        Log.i("BrowseFragment OnResume",c.getCount()+"");
        getActivity().startManagingCursor(c);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                getContext(),
                R.layout.raw_note,
                c,
                new String[]{Note.COL_TITLE,
                        Note.COL_TYPE,
                        SmartPad.getTimeOption()==0 ? Note.COL_MODIFIEDTIME : Note.COL_CREATEDTIME},
                new int[]{android.R.id.text1, R.id.icon, android.R.id.text2});

        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                switch (view.getId()) {
                    case R.id.icon:
                        String type = cursor.getString(columnIndex);
                        int icon = 0;

                        if (Note.BASIC.equals(type))
                            icon = R.drawable.document;
                        else if (Note.CHECKLIST.equals(type))
                            icon = R.drawable.checklist;

                        ((ImageView)view).setImageResource(icon);
                        return true;

                    case android.R.id.text2:
                        cal.setTimeInMillis(cursor.getLong(columnIndex));
                        ((TextView)view).setText(df.format(cal.getTime()));
                        return true;
                }

                return false;
            }
        });

        noteList.setAdapter(adapter);
        if(c.moveToFirst())
            emptytxt.setVisibility(View.INVISIBLE);

    }


    private void findViews(View view) {
        noteList = (ListView) view.findViewById(R.id.note_list);
        newBtn = (ImageButton) view.findViewById(R.id.new_btn);
        sortBtn = (ImageButton) view.findViewById(R.id.sort_btn);
        emptytxt=(TextView)view.findViewById(android.R.id.empty);
    }

    @Override
    public void onCreateContextMenu(final ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId() == R.id.note_list) {
            menu.setHeaderTitle("Choose an Option");
            menu.setHeaderIcon(R.drawable.ic_dialog_menu_generic);
            String[] options = {"Edit", "Delete"};

            for (String option : options) {
                menu.add(option);
            }

            for (int i = 0, n = menu.size(); i < n; i++) {
                menu.getItem(i).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        String menuname = item.getTitle().toString();
                        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

                        switch (menuname) {
                            case "Edit":
                                openNote(info.id, getContext());
                                Toast.makeText(getView().getContext(), "Edit Note"+info.id, Toast.LENGTH_SHORT).show();
                                break;
                            case "Delete":
                                Toast.makeText(getView().getContext(), "Remove Note", Toast.LENGTH_SHORT).show();
                                Note note = new Note(info.id);
                                note.delete(db);
                                SimpleCursorAdapter adapter = (SimpleCursorAdapter) noteList.getAdapter();
                                adapter.getCursor().requery();
                                adapter.notifyDataSetChanged();
                                break;

                            default:
                                Toast.makeText(getView().getContext(), "invalid option!", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return false;
                    }
                });
               // SimpleCursorAdapter adapter = (SimpleCursorAdapter) noteList.getAdapter();
                //adapter.getCursor().requery();
                //adapter.notifyDataSetChanged();

            }
        }
    }

    public BrowseFragment() {
        super();
    }

    void  openNote(long id, Context ctx) {
        Note note = new Note(id);
        note.load(SmartPad.db);

        Class claz = null;
        String type = note.getType();
        if (Note.BASIC.equals(type)) {
            claz = NoteActivity.class;
        } else if (Note.CHECKLIST.equals(type)) {
            claz = ChecklistActivity.class;
        }

        boolean isLocked = note.isLocked();
        if (!isLocked) {
            Category category = new Category(note.getCategoryId());
            category.load(SmartPad.db);
            isLocked = category.isLocked();
        }

        Intent intent = new Intent();
        // authenticate
        if (isLocked && !SmartPad.isAuth()) {
          //  intent.setClass(ctx, AuthActivity.class);
            intent.putExtra("class", claz);
        } else {
            intent.setClass(ctx, claz);
        }
        intent.putExtra(Note.COL_ID, id);
        ctx.startActivity(intent);
    }


}
