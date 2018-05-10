package com.igorronner.irloginbackup.init;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.widget.Toast;

import com.igorronner.irloginbackup.R;
import com.igorronner.irloginbackup.preferences.FirebasePreference;
import com.igorronner.irloginbackup.services.FirebaseAuthService;
import com.igorronner.irloginbackup.services.FirebaseStorageService;
import com.igorronner.irloginbackup.utils.ConnectionUtil;
import com.igorronner.irloginbackup.utils.PermissionsUtils;
import com.igorronner.irloginbackup.views.BaseActivity;

public class IRLoginBackup {

    public static IRLoginBackup.Builder startInit(String dbName, String googleClientId) {
        return new IRLoginBackup.Builder(dbName, googleClientId);
    }

    private IRLoginBackup(final IRLoginBackup.Builder builder){
        if (builder.colorAccent>0)
            ConfigUtil.COLOR_ACCENT = builder.colorAccent;
        if (builder.colorPrimary>0)
            ConfigUtil.COLOR_PRIMARY = builder.colorPrimary;
        if (builder.colorPrimaryDark>0)
            ConfigUtil.COLOR_PRIMARY_DARK = builder.colorPrimaryDark;

        ConfigUtil.LOGO = builder.logo;
        ConfigUtil.DATABASE_NAME = builder.dbName;
        ConfigUtil.GOOGLE_CLIENT_ID = builder.googleClientId;
    }

    public static class Builder {
        @ColorRes private int colorPrimary;
        @ColorRes private int colorPrimaryDark;
        @ColorRes private int colorAccent;
        @DrawableRes private int logo;
        private String googleClientId;
        private String dbName;
        private IRLoginBackup IRLoginBackup;

        public Builder(String dbName, String googleClientId){
            this.dbName = dbName;
            this.googleClientId = googleClientId;
        }


        public Builder setColorPrimary(@ColorRes int colorPrimary){
            this.colorPrimary = colorPrimary;
            return this;
        }

        public Builder setColorPrimaryDark(@ColorRes int colorPrimaryDark){
            this.colorPrimaryDark = colorPrimaryDark;
            return this;
        }

        public Builder setColorAccent(@ColorRes int colorAccent){
            this.colorAccent = colorAccent;
            return this;
        }

        public Builder setLogo(@DrawableRes int logo){
            this.logo = logo;
            return this;
        }


        public IRLoginBackup build(){
            this.IRLoginBackup = new IRLoginBackup(this);
            return this.IRLoginBackup;
        }

    }

    public static void backup(final Activity context){
        if (!ConnectionUtil.isConnected(context)){
            Toast.makeText(context, R.string.need_internet, Toast.LENGTH_SHORT).show();
            return;
        }

        if (PermissionsUtils.isStoragePermissionGranted(context)) {
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(context.getString(R.string.loading));
            progressDialog.show();
            FirebaseStorageService
                    .getInstance(context)
                    .setUploadServiceListener(new FirebaseStorageService.UploadServiceListener() {
                        @Override
                        public void onUploadComplete() {
                            progressDialog.cancel();
                            Toast.makeText(context, R.string.backup_success, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError() {
                            Toast.makeText(context, R.string.error_backup, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .uploadBackupWithNotification();
        }

    }

    public static void logoutDialog(final Activity context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebasePreference.setUuid(context, null);
                FirebaseAuthService.getInstance(context).logout();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setTitle(R.string.are_you_sure_logout);

        builder.show();
    }

    public static boolean isLogged(Activity context){
       return FirebasePreference.isLogged(context);
    }
}