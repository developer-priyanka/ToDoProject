package my.assignment.todoproject;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import my.assignment.todoproject.myadapter.Pager;

public class MainActivity extends AppCompatActivity {

    ViewPager vp;
    Pager adapter;
    TabLayout tabLay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        //Adding ToolBar to the Activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setIcon(R.drawable.android);

        //initializing the tab layout
        tabLay = (TabLayout) findViewById(R.id.tablayout);

        //Creating our pager adapter
        vp = (ViewPager) findViewById(R.id.viewPager);
        adapter = new Pager(getSupportFragmentManager());

        //Adding adapter to pager
        vp.setAdapter(adapter);

        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){}

            @Override
            public void onPageSelected(int position) {
                Fragment fragment = adapter.getFragment(position);
                if (fragment != null) {
                    fragment.onResume();
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLay.setupWithViewPager(vp);
        createTabIcon();

    }

    public void createTabIcon() {
        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custome_tab, null);
        tabOne.setText("Browse");
        //tabOne.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.,0,0,0);
        tabLay.getTabAt(0).setCustomView(tabOne);
        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custome_tab, null);
        tabTwo.setText("Manage");
        //tabTwo.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.android_32_black,0,0,0);
        tabLay.getTabAt(1).setCustomView(tabTwo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                Intent intent=new Intent(this,SearchActivity.class);
                startActivity(intent);
                break;


        }
        return super.onOptionsItemSelected(item);
    }
}
