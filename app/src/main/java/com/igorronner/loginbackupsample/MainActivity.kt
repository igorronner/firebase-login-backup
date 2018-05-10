package com.igorronner.loginbackupsample

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.igorronner.irloginbackup.init.IRLoginBackup
import com.igorronner.irloginbackup.preferences.FirebasePreference
import com.igorronner.irloginbackup.views.RestoreBackupActivity
import com.igorronner.irloginbackup.views.SignUpActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!IRLoginBackup.isLogged(this))
            startActivity(Intent(this, SignUpActivity::class.java))

        restoreBackup.setOnClickListener { startActivity(Intent(this, RestoreBackupActivity::class.java)) }

        doBackup.setOnClickListener { IRLoginBackup.backup(this) }

        logout.setOnClickListener { IRLoginBackup.logoutDialog(this) }
    }
}
