package com.igorronner.irloginbackup.init;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.widget.Toast;

import com.igorronner.irloginbackup.R;
import com.igorronner.irloginbackup.preferences.MainPreference;
import com.igorronner.irloginbackup.services.FirebaseAuthService;
import com.igorronner.irloginbackup.services.FirebaseStorageService;
import com.igorronner.irloginbackup.utils.ConnectionUtil;
import com.igorronner.irloginbackup.utils.PermissionsUtils;
import com.igorronner.irloginbackup.views.RestoreBackupActivity;
import com.igorronner.irloginbackup.views.SignInActivity;
import com.igorronner.irloginbackup.views.SignUpActivity;

public class IRLoginBackup {

    public static IRLoginBackup.Builder startInit(String dbName, String googleClientId) {
        return new IRLoginBackup.Builder(dbName, googleClientId);
    }

    private IRLoginBackup(final IRLoginBackup.Builder builder){

        ConfigUtil.LOGO = builder.logo;
        ConfigUtil.DATABASE_NAME = builder.dbName;
        ConfigUtil.GOOGLE_CLIENT_ID = builder.googleClientId;
        ConfigUtil.NODE_NAME = builder.nodeName;
        ConfigUtil.LOGIN_OPTIONAL = builder.loginOptional;

    }

    public static class Builder {
//        @ColorRes private int colorPrimary;
//        @ColorRes private int colorPrimaryDark;
//        @ColorRes private int colorAccent;
        @DrawableRes private int logo;
        private String googleClientId;
        private String dbName;
        private String nodeName;
        private boolean loginOptional;
        private IRLoginBackup IRLoginBackup;

        public Builder(String dbName, String googleClientId){
            this.dbName = dbName;
            this.googleClientId = googleClientId;
        }


        public Builder setLogo(@DrawableRes int logo){
            this.logo = logo;
            return this;
        }

        public Builder setLoginOptional(boolean loginOptional){
            this.loginOptional = loginOptional;
            return this;
        }
        public Builder setNodeName(String nodeName){
            this.nodeName = nodeName;
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

    public interface LogoutListener {
        void onComplete();
    }

    public static void logoutDialog(final Activity context, final boolean keepCurrentActivity, final LogoutListener logoutListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainPreference.setUuid(context, null);
                FirebaseAuthService.getInstance(context).logout(keepCurrentActivity);
                if (logoutListener!=null)
                    logoutListener.onComplete();

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

    public static void showTutorialBackup(final Activity context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        View view = context.getLayoutInflater().inflate(R.layout.dialog_tutorial_backup, null);
        builder.setPositiveButton(R.string.go_it, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainPreference.setShownTutorialBackup(context, true);
                    }
                })
                .setView(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    MainPreference.setShownTutorialBackup(context, true);
                }
            });
        }

        builder.show();
    }

    public static void logoutDialog(final Activity context){
        logoutDialog(context, false, null);
    }


    public static boolean isLogged(Activity context){
        return MainPreference.isLogged(context);
    }

    public static void openRestoreBackup(Activity context){
        context.startActivity(new Intent(context, RestoreBackupActivity.class));
    }

    public static void openSignUp(Activity context){
        context.startActivity(new Intent(context, SignUpActivity.class));
    }

    public static void openSigIn(Activity context){
        context.startActivity(new Intent(context, SignInActivity.class));
    }

}