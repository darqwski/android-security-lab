package com.darqwski.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class UserActivity : AppCompatActivity() {
    private val startButton by lazy { findViewById<Button>(R.id.startButton) }
    private val stopButton by lazy { findViewById<Button>(R.id.stopButton) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        val userName = intent.getStringExtra("USER_NAME_EXTRA")

        Toast.makeText(this, userName, Toast.LENGTH_LONG).show()

        startButton.setOnClickListener(View.OnClickListener {
            onStartServiceBtnClick()
        })

        stopButton.setOnClickListener(View.OnClickListener {
            onStopServiceBtnClick()
        })
    }

    private fun onStartServiceBtnClick() {
        val intent = Intent(this, CounterService::class.java)
        startService(intent)
    }
    private fun onStopServiceBtnClick() {
        val intent = Intent(this, CounterService::class.java)
        stopService(intent)
    }
}