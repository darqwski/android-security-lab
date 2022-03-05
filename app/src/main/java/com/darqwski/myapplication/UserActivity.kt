package com.darqwski.myapplication

import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class UserActivity : AppCompatActivity() {
    private val startButton by lazy { findViewById<Button>(R.id.startButton) }
    private val stopButton by lazy { findViewById<Button>(R.id.stopButton) }
    private val resultTextView by lazy { findViewById<TextView>(R.id.serviceCounterResult) }
    private lateinit var receiver: NumberReceiver
    lateinit var userName: String

    override fun onStart() {
        super.onStart()
        val receiver = NumberReceiver()

        receiver.setUserActivityContext(this)
        registerReceiver(receiver, IntentFilter("NUMBER_RECEIVER_ACTION"))
        this.receiver = receiver
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(this.receiver)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        val userName = intent.getStringExtra("USER_NAME_EXTRA")

        this.userName = userName?: ""
        Toast.makeText(this, userName, Toast.LENGTH_LONG).show()

        startButton.setOnClickListener {
            onStartServiceBtnClick()
        }

        stopButton.setOnClickListener {
            onStopServiceBtnClick()
        }
    }

    private fun onStartServiceBtnClick() {
        val intent = Intent(this, CounterService::class.java)

        intent.putExtra("USER_NAME_EXTRA", userName)

        startService(intent)
    }
    private fun onStopServiceBtnClick() {
        val intent = Intent(this, CounterService::class.java)

        intent.putExtra("USER_NAME_EXTRA", userName)

        stopService(intent)
    }

    public fun setResult(counter: Number, userName: String) {
        var resultText = "$userName started service"
        if(counter != 0){
            resultText = "$userName started service at $counter"
        }
        resultTextView.text = resultText
    }
}