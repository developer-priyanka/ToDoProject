package my.assignment.todoproject;

import android.os.Bundle;
import android.preference.PreferenceActivity;


/**
 * Created by root on 11/25/16.
 */

public class SettingActivity extends PreferenceActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}
