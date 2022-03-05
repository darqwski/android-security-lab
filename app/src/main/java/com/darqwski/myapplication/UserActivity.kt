package com.darqwski.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class UserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        val userName = intent.getStringExtra("USER_NAME_EXTRA")

        Toast.makeText(this, userName, Toast.LENGTH_LONG).show()
    }
}