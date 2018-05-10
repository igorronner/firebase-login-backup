package com.igorronner.irloginbackup.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class FirebasePreference {

    private static final String USER_ID = "uuid";

    public static boolean isLogged(Context context){
        return getUserId(context) != null;
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

}