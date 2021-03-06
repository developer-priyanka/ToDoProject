package my.assignment.todoproject;

import android.app.Application;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import my.assignment.todoproject.Model.DbHelper;

/**
 * Created by root on 11/15/16.
 */

public class SmartPad extends Application {

        public static DbHelper dbHelper;
        public static SQLiteDatabase db;
        public static SharedPreferences sp;

        public static final String HIDE_LOCKED = "hideLocked";
        public static final String PASSWORD = "password";
        public static final String LAST_AUTH = "lastAuth";
        public static final String DEFAULT_SORT = "defaultSort";
        public static final String DEFAULT_CATEGORY_OPT = "defaultCategoryOpt";
        public static final String TIME_OPTION = "timeOpt";

        public static long PUBLIC_CATEGORYID = 1;
        public static long LASTCREATED_CATEGORYID;
        public static long LASTSELECTED_CATEGORYID;

        @Override
        public void onCreate() {
            super.onCreate();
            Log.i("SmartPad","Oncreate");


            PreferenceManager.setDefaultValues(this, R.xml.settings, false);
                    sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

            dbHelper = new DbHelper(this);
            db = dbHelper.getWritableDatabase();
        }


        public static int getDefaultCategoryOpt() {
            return Integer.parseInt(sp.getString(DEFAULT_CATEGORY_OPT, "0"));
        }

        public static boolean showLocked() {
            return !sp.getBoolean(HIDE_LOCKED, false);
        }

        public static boolean isAuth() {
            return TextUtils.isEmpty(sp.getString(PASSWORD, null)) ||
                    (System.currentTimeMillis()-sp.getLong(LAST_AUTH, 0)) < 5*60*1000;
        }

        public static void clearAuth() {
            sp.edit().remove(LAST_AUTH).commit();
        }

        public static boolean doAuth(String str) {
            String pass = sp.getString(PASSWORD, null);
            boolean isAuth = TextUtils.isEmpty(pass) || pass.equals(str);
            if (isAuth)
                sp.edit().putLong(LAST_AUTH, System.currentTimeMillis()).commit();
            return isAuth;
        }

        public static int getDefaultSort() {
            return Integer.parseInt(sp.getString(DEFAULT_SORT, "0"));
        }

        public static int getTimeOption() {
            return Integer.parseInt(sp.getString(TIME_OPTION, "0"));
        }

    }


