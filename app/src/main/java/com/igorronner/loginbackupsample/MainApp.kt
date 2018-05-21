package com.igorronner.loginbackupsample

import android.app.Application
import com.igorronner.irloginbackup.init.IRLoginBackup


class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()
        //Just for create db file
        DatabaseOpenHelper(this)

        IRLoginBackup.startInit(DatabaseOpenHelper.DATABASE_NAME, resources.getString(R.string.default_web_client_id))
                .setLogo(R.mipmap.ic_launcher)
                .setLoginOptional(true)
                .build()


    }


}