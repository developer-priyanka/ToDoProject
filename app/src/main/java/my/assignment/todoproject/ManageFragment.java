package my.assignment.todoproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;
import android.widget.Toast;

import my.assignment.todoproject.Model.Category;
import my.assignment.todoproject.Model.DbHelper;
import my.assignment.todoproject.Model.Note;
import my.assignment.todoproject.SettingActivity;
import my.assignment.todoproject.myadapter.CategoryTreeAdapter;


/**
 * Created by root on 11/11/16.
 */

public class ManageFragment extends Fragment {

    private EditText categoryNameEdit;
    private ImageButton newBtn,settingsBtn;
    private android.app.AlertDialog categoryDialog;
    ExpandableListView expandableListView;
    private Category category;
    private TextView emptytxt;
    private SQLiteDatabase db;
    private  View rootView=null;
    private Note note;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         rootView=inflater.inflate(R.layout.fragment_manage,container,false);
        findViews(rootView);
        db=SmartPad.db;
        registerForContextMenu(expandableListView);

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                getActivity().openContextMenu(v);
                return true;
            }
        });
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                int childCount = expandableListView.getExpandableListAdapter().getChildrenCount(groupPosition);
                if (childCount == 0)
                    Toast.makeText(getContext(), "Empty Folder", Toast.LENGTH_LONG).show();
                return false;
            }
        });

        category=new Category();
        categoryNameEdit = new EditText(getContext());
        categoryNameEdit.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        categoryDialog = new android.app.AlertDialog.Builder(getContext())
                .setTitle("New Folder")
                .setView(categoryNameEdit)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (!TextUtils.isEmpty(categoryNameEdit.getText())) {
                            category.setName(categoryNameEdit.getText().toString());
                            //long i=category.insert(db);

                            SmartPad.LASTCREATED_CATEGORYID = category.persist(db);

                            SimpleCursorTreeAdapter adapter = (SimpleCursorTreeAdapter)expandableListView.getExpandableListAdapter();
                            adapter.getCursor().requery();
                            adapter.notifyDataSetChanged();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .create();
        newBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryDialog.show();
            }
        });
        settingsBtn.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View view) {
                                               Intent intent = new Intent(getContext(),SettingActivity.class);
                                               startActivity(intent);

                                           }
                                       });
                Cursor c = Category.list(db);

        getActivity().startManagingCursor(c);
        SimpleCursorTreeAdapter adapter = new CategoryTreeAdapter(getContext(), c);
        expandableListView.setAdapter(adapter);
        if(c.moveToFirst())
            emptytxt.setVisibility(View.GONE);

        note=new Note();
        return rootView;
    }
    private void findViews(View view) {
        newBtn = (ImageButton) view.findViewById(R.id.new_btn);
        settingsBtn = (ImageButton) view.findViewById(R.id.settings_btn);
        expandableListView=(ExpandableListView)view.findViewById(android.R.id.list);
        emptytxt=(TextView)view.findViewById(android.R.id.empty);
    }

    @Override
    public void onResume() {
        super.onResume();
        Cursor c = Category.list(db);
        Log.i("ManageFragment OnResume",c.getCount()+"");
        getActivity().startManagingCursor(c);
        SimpleCursorTreeAdapter adapter = new CategoryTreeAdapter(getContext(), c);
        expandableListView.setAdapter(adapter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == android.R.id.list) {
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
                        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();
                        int type = ExpandableListView.getPackedPositionType(info.packedPosition);
                        if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                            note.reset();
                            note.setNoteId(info.id);

                        } else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                            category.reset();
                            category.setId(info.id);
                        }
                        switch (menuname) {
                            case "Edit":

                                Toast.makeText(getView().getContext(), "Edit Category" + info.id, Toast.LENGTH_SHORT).show();
                                if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                                   new  BrowseFragment().openNote(info.id, getContext());

                                } else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                                    category.load(db);
                                    categoryNameEdit.setText(category.getName());
                                    categoryDialog.show();
                                }
                                break;

                            case "Delete":
                                Toast.makeText(getView().getContext(), "Remove Category", Toast.LENGTH_SHORT).show();
                                if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                                    note.delete(db);

                                } else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                                    category.delete(db);
                                }
                                SimpleCursorTreeAdapter adapter = (SimpleCursorTreeAdapter) expandableListView.getExpandableListAdapter();
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
               // SimpleCursorTreeAdapter adapter = (SimpleCursorTreeAdapter) expandableListView.getExpandableListAdapter();
               // adapter.getCursor().requery();
                //adapter.notifyDataSetChanged();
            }
        }
    }

}
