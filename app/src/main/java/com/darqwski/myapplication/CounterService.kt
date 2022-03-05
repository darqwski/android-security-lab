package com.darqwski.myapplication

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.Nullable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CounterService : Service() {
    private var isDestroyed = false
    private var number = 0;

    private fun sendInitIntent(intent: Intent?){
        val newIntent = Intent("NUMBER_RECEIVER_ACTION")
        newIntent.putExtra("NUMBER_EXTRA", number)
        newIntent.putExtra("USER_NAME_EXTRA", intent!!.getStringExtra("USER_NAME_EXTRA"))

        sendBroadcast(newIntent)
    }

    private fun sendDestroyIntent(intent: Intent?){
        val newIntent = Intent("NUMBER_RECEIVER_ACTION")
        newIntent.putExtra("NUMBER_EXTRA", number)
        newIntent.putExtra("USER_NAME_EXTRA", intent!!.getStringExtra("USER_NAME_EXTRA"))

        sendBroadcast(newIntent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isDestroyed = false
        sendInitIntent(intent)
        GlobalScope.launch {
            while (!isDestroyed) {
                number++;
                Log.d("New number", "$number");
                delay(3000);
            }

            sendDestroyIntent(intent)
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        this.isDestroyed = true
    }

    override fun onBind(intent: Intent?): IBinder? {

        return null
    }

}