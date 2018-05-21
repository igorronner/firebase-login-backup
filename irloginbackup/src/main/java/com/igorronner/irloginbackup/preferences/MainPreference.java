package com.igorronner.irloginbackup.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MainPreference {

    private static final String USER_ID = "uuid";
    private static final String SHOWN_TUTORIAL_BACKUP = "shown_tutorial_backup";

    public static boolean isLogged(Context context){
        return getUserId(context) != null;
    }

    public static void setShownTutorialBackup(Context context, boolean shownTutorialBackup){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean(SHOWN_TUTORIAL_BACKUP, shownTutorialBackup);
        editor.commit();
    }

    public static void setUuid(Context context, String firebaseUserId){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(USER_ID, firebaseUserId);
        editor.commit();
    }

    public static String getUserId(Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(USER_ID, null);
    }

    public static boolean alreadyShownTutorialBackup(Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getBoolean(SHOWN_TUTORIAL_BACKUP, false);
    }
}