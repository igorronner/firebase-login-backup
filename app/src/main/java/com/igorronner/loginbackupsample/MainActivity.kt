package com.igorronner.loginbackupsample

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.igorronner.irloginbackup.preferences.FirebasePreference
import com.igorronner.irloginbackup.views.SignUpActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (FirebasePreference.getInstance().getUserId(this) == null) {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}
