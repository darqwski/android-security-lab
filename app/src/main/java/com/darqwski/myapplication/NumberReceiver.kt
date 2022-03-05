package com.darqwski.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class NumberReceiver : BroadcastReceiver() {
    private lateinit var userActivityContext: Context

    public fun setUserActivityContext(context: Context){
        userActivityContext = context
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("NumberReceiver", "Received message")
        val number = intent.getIntExtra("NUMBER_EXTRA", 0) ?: 0
        val user = intent.getStringExtra("USER_NAME_EXTRA") ?: ""
        Log.d("Number, user", "$number, $user")

        (userActivityContext as UserActivity).setResult(number, user)
    }
}