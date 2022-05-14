package dariusz.cabala.cards.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.OutputStreamWriter
import java.lang.reflect.Type
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.math.BigInteger
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {
    private val loginInput by lazy { findViewById<AppCompatEditText>(R.id.loginInput) }
    private val passwordInput by lazy { findViewById<AppCompatEditText>(R.id.passwordInput) }
    private val loginButton by lazy { findViewById<MaterialButton>(R.id.loginButton) }
    private val registerButton by lazy { findViewById<MaterialButton>(R.id.registerButton) }

    override fun onCreate(savedInstanceState: Bundle?) {
        DatabaseUtils.initDatabase(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val login = loginInput.text.toString()
            val password = passwordInput.text.toString();

            val url = URL("https://program-it-yourself.pl/BAM/login/")
            var reqParam = URLEncoder.encode("login", "UTF-8") + "=" + URLEncoder.encode(login, "UTF-8")
            reqParam += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(Utils().md5Hash(password), "UTF-8")

            GlobalScope.launch {
                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = "POST"

                    val wr = OutputStreamWriter(outputStream);
                    wr.write(reqParam);
                    wr.flush();

                    var result = ""
                    inputStream.bufferedReader().use {
                        it.lines().forEach { line ->
                            result += line
                        }
                    }

                    val code = responseCode

                    val type : Type = object : TypeToken<HashMap<String, String>>() {}.type

                    val gson = Gson()
                    val response:HashMap<String, String> = gson.fromJson(result, type)

                    runOnUiThread(Runnable {
                        Toast.makeText(applicationContext, response.get("message"), Toast.LENGTH_LONG).show()
                    })


                    if(code == 200){
                        RequestSession.saveSessionCookie(headerFields)
                        val intent = Intent(applicationContext, DashboardActivity::class.java)
                        startActivity(intent)
                    }
                }
            }

        }
    }
}