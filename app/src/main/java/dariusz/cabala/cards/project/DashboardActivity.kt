package dariusz.cabala.cards.project

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
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
import java.io.*
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
   private fun exportCards(){
        GlobalScope.launch {
            val url = URL("https://program-it-yourself.pl/BAM/export/")
            with(url.openConnection()  as HttpURLConnection) {
                var result = ""
                requestMethod = "POST"
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

    private fun importCards(){
        val dir = getFilesDir()
        if (!dir.exists()) {
            dir.mkdir()
        }

        val destination = File(dir, "exported-cards.txt")

        val fileBoundary = "*******12347890*******"
        GlobalScope.launch {
            val url = URL("https://program-it-yourself.pl/BAM/import/")
            with(url.openConnection()  as HttpURLConnection) {
                var result = ""
                requestMethod = "POST"
                setRequestProperty("cookie",RequestSession.sessionCookie)
                setRequestProperty("Connection", "Keep-Alive");
                setRequestProperty("Cache-Control", "no-cache");
                setRequestProperty("Content-Type", "multipart/form-data;boundary=$fileBoundary");

                var fileContent = ""

                destination.inputStream().bufferedReader().use {
                    it.lines().forEach { line ->
                        fileContent += line
                    }
                }

                val dataOutputStream = DataOutputStream(outputStream);
                dataOutputStream.writeBytes("--$fileBoundary\r\n")
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"file\"\r\n");
                dataOutputStream.writeBytes("\r\n");
                dataOutputStream.writeBytes(fileContent)
                dataOutputStream.writeBytes("\r\n");
                dataOutputStream.writeBytes("--$fileBoundary\r\n")

                dataOutputStream.flush();

                inputStream.bufferedReader().use {
                    it.lines().forEach { line ->
                        result += line
                    }
                }

                runOnUiThread {
                    Toast.makeText(applicationContext, "Karty zaimportowano pomyślnie", Toast.LENGTH_LONG).show()
                }
            }
        }

    }

    private fun logoutUserAndDestroyCredentials(){
        val sharedPrefs = Utils().getEncryptedSharedPreferences(applicationContext)
        sharedPrefs.edit().clear().commit()

        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
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
        val dir = getFilesDir()
        if (!dir.exists()) {
            dir.mkdir()
        }
        val destination = File(dir, "exported-cards.txt")

        try {
            destination.createNewFile()
            val fos = OutputStreamWriter(openFileOutput("exported-cards.txt", MODE_PRIVATE))
            fos.write(exportedCardFileContent.toCharArray())
            fos.flush()
            fos.close()
            Toast.makeText(applicationContext, "Plik zapisano pomyślnie", Toast.LENGTH_LONG).show()

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}