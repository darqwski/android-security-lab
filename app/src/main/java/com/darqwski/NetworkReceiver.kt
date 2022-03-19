package com.darqwski

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log


class NetworkReceiver :BroadcastReceiver() {
    override fun onReceive(p0: Context?, intent: Intent?) {
        val bundle: Bundle = intent?.getExtras()!!
        for (key in bundle.keySet()) {
            Log.d("Network changed!!", key + " : " + if (bundle[key] != null) bundle[key] else "NULL")
        }
    }
}