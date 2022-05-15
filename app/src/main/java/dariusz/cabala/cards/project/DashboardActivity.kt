package dariusz.cabala.cards.project

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dariusz.cabala.cards.project.adapters.CreditCardAdapter
import dariusz.cabala.cards.project.model.CreditCard
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.reflect.Type
import java.net.HttpURLConnection
import java.net.URL


class DashboardActivity : AppCompatActivity() {
    private val WRITE_FILE_PERMISSION_REQUEST_CODE = 1
    private val creditCardsRecyclerView by lazy { findViewById<RecyclerView>(R.id.creditCardsRecyclerView) }
    private val logoutButton by lazy { findViewById<MaterialButton>(R.id.logoutButton) }
    private val addCardButton by lazy { findViewById<MaterialButton>(R.id.addCardButton) }
    private lateinit var downloadedCards: Array<CreditCard>
    private var savedApplicationContext = this
    private fun showCards(){
        runOnUiThread {
            val adapter = CreditCardAdapter(downloadedCards, savedApplicationContext)
            creditCardsRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
            creditCardsRecyclerView.adapter = adapter
        }
    }

    private var exportedCardFileContent = ""

    fun getCards(){
        GlobalScope.launch {
            val url = URL("https://program-it-yourself.pl/BAM/cards/")
            with(url.openConnection()  as HttpURLConnection) {
                var result = ""

                setRequestProperty("cookie",RequestSession.sessionCookie)
                inputStream.bufferedReader().use {
                    it.lines().forEach { line ->
                        result += line
                    }
                }

                val type : Type = object : TypeToken<Array<CreditCard>>() {}.type

                val gson = Gson()

                val creditCards: Array<CreditCard> = gson.fromJson(result, type)
                DatabaseUtils.db.creditCardDao().deleteAll()
                for(creditCard in creditCards){
                    DatabaseUtils.db.creditCardDao().insert(creditCard)
                }

                downloadedCards = creditCards

                showCards()
            }
        }
    }
    fun downloadExportedCards(){
        GlobalScope.launch {
            val url = URL("https://program-it-yourself.pl/BAM/export/")
            with(url.openConnection()  as HttpURLConnection) {
                var result = ""

                setRequestProperty("cookie",RequestSession.sessionCookie)
                inputStream.bufferedReader().use {
                    it.lines().forEach { line ->
                        result += line
                    }
                }

                exportedCardFileContent = result

                ActivityCompat.requestPermissions(
                    this@DashboardActivity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    WRITE_FILE_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    private fun logoutUserAndDestroyCredentials(){
        val sharedPrefs = Utils().getEncryptedSharedPreferences(applicationContext)
        sharedPrefs.edit().clear().commit()

        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
    }

    private fun exportCards(){
        downloadExportedCards()
    }
    private fun importCards(){

    }

    private fun showPopup(v: View) {
        PopupMenu(this, v).apply {
            setOnMenuItemClickListener { item ->
                when (item?.itemId) {

                    R.id.menuExport -> {
                        exportCards()
                        true
                    }
                    R.id.menuImport -> {
                        importCards()
                        true
                    }
                    R.id.menuLogout -> {
                        logoutUserAndDestroyCredentials()
                        true
                    }
                    else -> false
                }
            }
            inflate(R.menu.main_menu)
            show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        logoutButton.setOnClickListener {
            showPopup(it);
        }
        addCardButton.setOnClickListener {
            val intent = Intent(applicationContext, CreditCardForm::class.java)
            intent.putExtra("type","add")
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        getCards()
    }

    override fun onResume() {
        super.onResume()
        getCards()
    }

    override fun onBackPressed() {
        return;
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == WRITE_FILE_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                saveFile()
            } else {
                Toast.makeText(applicationContext, "Nie można zapisać pliku bez nadania uprawnień", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun saveFile(){
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!dir.exists()) {
            dir.mkdir()
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "exported-cards.txt")
            put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        val resolver = applicationContext.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
            resolver.openOutputStream(uri).use { output ->
                if (output == null) {
                    return
                }

                output.write(exportedCardFileContent.toByteArray())
                output.flush()
                output.close()
                Toast.makeText(
                    applicationContext,
                    "Plik zapisano w folderze Pobrane\"",
                    Toast.LENGTH_LONG
                ).show()

            }
        }
    }
}