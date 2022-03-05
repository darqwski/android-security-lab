package com.darqwski.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NumberReceiver : BroadcastReceiver() {
    private lateinit var userActivityContext: Context

    public fun setUserActivityContext(context: Context){
        userActivityContext = context
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("NumberReceiver", "Received message")
        val number = intent.getIntExtra("NUMBER_EXTRA", 0)
        val user = intent.getStringExtra("USER_NAME_EXTRA") ?: ""
        Log.d("Number, user", "$number, $user")

        (userActivityContext as UserActivity).setResult(number, user)

        if(number != 0) {
           GlobalScope.launch {
               DatabaseUtils.db.userDao().insert(
                   User(userName = user, number = number)
               )

               val rows = DatabaseUtils.db.userDao().getAll()

               Log.d("Number of records", "${rows.size}")
           }
        }
    }
}