package com.igorronner.irloginbackup.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class FirebasePreference {

    private static final String USER_ID = "uuid";

    private static FirebasePreference instance;

    public static synchronized FirebasePreference getInstance(){
        if(instance==null)
            instance = new FirebasePreference();

        return instance;
    }

    private FirebasePreference(){

    }

    public void setUuid(Context context, String firebaseUserId){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(USER_ID, firebaseUserId);
        editor.commit();
    }
    public String getUserId(Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(USER_ID, null);
    }

}