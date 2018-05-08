package com.igorronner.irloginbackup.init;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;

public class IRLoginBackup {

    public static IRLoginBackup.Builder startInit(String dbName, String googleClientId) {
        return new IRLoginBackup.Builder(dbName, googleClientId);
    }

    private IRLoginBackup(final IRLoginBackup.Builder builder){
        ConfigUtil.COLOR_ACCENT = builder.colorAccent;
        ConfigUtil.COLOR_PRIMARY = builder.colorPrimary;
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
}