package com.igorronner.loginbackupsample

import android.app.Application
import android.content.Intent
import com.igorronner.irloginbackup.init.IRLoginBackup
import com.igorronner.irloginbackup.preferences.FirebasePreference
import com.igorronner.irloginbackup.views.SignUpActivity


class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()
        //Just for create db file
        DatabaseOpenHelper(this)

        IRLoginBackup.startInit(DatabaseOpenHelper.DATABASE_NAME, resources.getString(R.string.default_web_client_id))
//                .setColorPrimary(R.color.colorPrimary)
//                .setColorPrimaryDark(R.color.colorPrimaryDark)
//                .setColorAccent(R.color.colorAccent)
                .setLogo(R.mipmap.ic_launcher)
                .build()


    }


}