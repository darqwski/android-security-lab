package dariusz.cabala.cards.project

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.OutputStreamWriter
import java.lang.reflect.Type
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder


class MainActivity : AppCompatActivity() {
    private val loginInput by lazy { findViewById<AppCompatEditText>(R.id.loginInput) }
    private val passwordInput by lazy { findViewById<AppCompatEditText>(R.id.passwordInput) }
    private val loginButton by lazy { findViewById<MaterialButton>(R.id.loginButton) }
    private val registerButton by lazy { findViewById<MaterialButton>(R.id.registerButton) }
    private val sharedPreferences by lazy { Utils().getEncryptedSharedPreferences(applicationContext) }


    private fun isCredentialsSaved(): Boolean {
        val login = sharedPreferences.getString("login", null)
        val password = sharedPreferences.getString("password", null)

        if(login != null && password != null){
            return true
        }
        return false
    }

    private fun loginUser(login: String, password: String){
        val url = URL("https://program-it-yourself.pl/BAM/login/")
        var reqParam = URLEncoder.encode("login", "UTF-8") + "=" + URLEncoder.encode(login, "UTF-8")
        reqParam += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8")

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


                if(response.get("message") == "Login successful"){
                    RequestSession.saveSessionCookie(headerFields)
                    if(!isCredentialsSaved()){
                        //Próbowałem dodać tutaj AlertDialog, jednak nie chciał się wogóle pokazywać, stąd decyzja o kolejnym activity
                        val intent = Intent(applicationContext, AskForCredentialsActivity::class.java)
                        intent.putExtra("login",login)
                        intent.putExtra("password",password)
                        startActivity(intent)
                    } else {
                        val intent = Intent(applicationContext, DashboardActivity::class.java)
                        startActivity(intent)
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(applicationContext, response.get("message"), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

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

            loginUser(login, Utils().md5Hash(password))
        }

        if(isCredentialsSaved()){
            val login = sharedPreferences.getString("login", null)!!
            val password = sharedPreferences.getString("password", null)!!

            loginUser(login, password)
        }
    }

    override fun onBackPressed() {
        return;
    }
}