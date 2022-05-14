package dariusz.cabala.cards.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.OutputStreamWriter
import java.lang.reflect.Type
import java.net.HttpURLConnection
import java.net.URL

class RegisterActivity : AppCompatActivity() {
    private val registerLogin by lazy { findViewById<AppCompatEditText>(R.id.registerLogin) }
    private val registerEmail by lazy { findViewById<AppCompatEditText>(R.id.registerEmail) }
    private val registerPassword by lazy { findViewById<AppCompatEditText>(R.id.registerPassword) }
    private val registerRepeatPassword by lazy { findViewById<AppCompatEditText>(R.id.registerRepeatPassword) }
    private val confirmRegisterButton by lazy { findViewById<MaterialButton>(R.id.confirmRegisterButton) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        confirmRegisterButton.setOnClickListener(View.OnClickListener {
            val login = registerLogin.text.toString()
            val email = registerEmail.text.toString()
            val password = registerPassword.text.toString()
            val repeatPassword = registerRepeatPassword.text.toString()

            val requestBody = HashMap<String, String>()
            requestBody.put("login",login)
            requestBody.put("email",email)
            requestBody.put("password",Utils().md5Hash(password))
            requestBody.put("repeatPassword",Utils().md5Hash(password))
            val gson = Gson()
            val jsonRequestBody = gson.toJson(requestBody)
            val url = URL("https://program-it-yourself.pl/BAM/register/")

            // @TODO dorobić sprawdzenie czy hasła są takie same
            GlobalScope.launch {
                with(url.openConnection()  as HttpURLConnection) {
                    requestMethod = "POST"

                    val wr = OutputStreamWriter(outputStream);
                    wr.write(jsonRequestBody);
                    wr.flush();

                    var result = ""
                    inputStream.bufferedReader().use {
                        it.lines().forEach { line ->
                            result += line
                        }
                    }

                    val gson = Gson()
                    val type : Type = object : TypeToken<HashMap<String, String>>() {}.type

                    val response:HashMap<String, String> = gson.fromJson(result, type)

                    val message = response.get("message")

                    runOnUiThread {
                        Toast.makeText(
                            applicationContext,
                            message,
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    if(message == "User registered correctly"){
                        finish()
                    }
                }
            }
        })
    }
}