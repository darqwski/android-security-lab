package com.darqwski.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.room.Room
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {
    private val loginButton by lazy { findViewById<Button>(R.id.loginButton) }
    private val loginEditText by lazy { findViewById<EditText>(R.id.loginEditText) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loginButton.setOnClickListener(View.OnClickListener {
            val userName = loginEditText.text.toString()
            val intent = Intent(this, UserActivity::class.java)
            intent.putExtra("USER_NAME_EXTRA", userName)
            startActivity(intent)
        })

    }
}