package my.assignment.todoproject;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import my.assignment.todoproject.Model.Category;
import my.assignment.todoproject.Model.Note;
import my.assignment.todoproject.myadapter.CustomeAdapter;


public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private SearchView searchView;
    private MenuItem searchMenuItem;
    private ListView listView;
    private SQLiteDatabase db;
    private ArrayList<Note>noteArrayList=new ArrayList<Note>();
    private CustomeAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_search);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.holo_orange_dark)));
        db=SmartPad.db;
        noteArrayList=Note.getNotes(db);
        listView=(ListView)findViewById(R.id.listView);
        adapter=new CustomeAdapter(this,noteArrayList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Note note=(Note)adapter.getItem(i);
                openNote(note.getNoteId(),getApplicationContext());
                searchMenuItem.collapseActionView();

                //Toast.makeText(getApplicationContext(),"noteid"+" "+note.getNoteId(),Toast.LENGTH_LONG).show();

            }
        });
        


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search,menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.search);
        searchView =
               (SearchView) searchMenuItem.getActionView();
        searchView.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
        searchMenuItem.expandActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        //searchView.setSubmitButtonEnabled(true);
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(this);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.search:

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }
    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        adapter.filter(text);
        return false;
    }



    public void  openNote(long id, Context ctx) {
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
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }


}
