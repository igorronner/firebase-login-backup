package com.igorronner.loginbackupsample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.igorronner.irloginbackup.init.IRLoginBackup
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!IRLoginBackup.isLogged(this))
            IRLoginBackup.openSignUp(this)

        restoreBackup.setOnClickListener { IRLoginBackup.openRestoreBackup(this) }
        doBackup.setOnClickListener { IRLoginBackup.backup(this) }
        logout.setOnClickListener { IRLoginBackup.logoutDialog(this) }

    }
}
