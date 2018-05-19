package com.igorronner.loginbackupsample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.igorronner.irloginbackup.init.IRLoginBackup
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.sign

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        signIn.setOnClickListener { IRLoginBackup.openSigIn(this) }


        restoreBackup.setOnClickListener { IRLoginBackup.openRestoreBackup(this) }
        doBackup.setOnClickListener { IRLoginBackup.backup(this) }
        logout.setOnClickListener { IRLoginBackup.logoutDialog(this, true,
                {
                    logout.visibility = View.GONE
                    signIn.visibility = View.VISIBLE
                })
        }


    }

    override fun onResume() {
        super.onResume()
        if (IRLoginBackup.isLogged(this)){
            signIn.visibility = View.GONE
            logout.visibility = View.VISIBLE
        } else{
            signIn.visibility = View.VISIBLE
            logout.visibility = View.GONE
        }
    }
}
