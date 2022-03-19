package com.darqwski

import android.Manifest
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    private val receiver:NetworkReceiver = NetworkReceiver()
    private val grantPermissionButton by lazy { findViewById<Button>(R.id.permission) }
    private val READ_CONTACTS_PERMISSION_REQUEST_CODE = 13423123;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val url = URL("https://jsonplaceholder.typicode.com/posts")
        GlobalScope.launch {
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "GET" // optional default is GET
                var allLines = ""
                inputStream.bufferedReader().use {
                    it.lines().forEach { line ->
                        allLines += line
                    }
                }

                runOnUiThread {
                    findViewById<TextView>(R.id.downloadedJson).text = allLines
                }
            }
        }


        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo;
        Log.d("Initial network info", "Is connected: ${networkInfo?.isConnected}")
        Log.d("Initial network info", "Type: ${networkInfo?.type} ${ConnectivityManager.TYPE_WIFI}")

        registerReceiver(this.receiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))


        grantPermissionButton.setOnClickListener {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                READ_CONTACTS_PERMISSION_REQUEST_CODE
            )
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_CONTACTS_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED
            ) {
                val cursor = contentResolver.query(
                    ContactsContract.Contacts.CONTENT_URI,
                    null,
                    null,
                    null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
                )
                var i = 0
                while (cursor!!.moveToNext()) {
                    i++
                    val contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                    val displayName =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    Log.d("Contact", "Contact ${contactId} ${displayName}")
                }
                Toast.makeText(this, "Kontaków jest $i", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, "Nie ma permissionów", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(this.receiver)
    }
}