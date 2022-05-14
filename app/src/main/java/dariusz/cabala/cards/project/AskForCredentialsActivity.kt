package dariusz.cabala.cards.project

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.android.material.button.MaterialButton

class AskForCredentialsActivity : AppCompatActivity() {
    private val saveCredentialButton by lazy { findViewById<MaterialButton>(R.id.saveCredentialButton) }
    private val cancelSavingCredentialButton by lazy { findViewById<MaterialButton>(R.id.cancelSavingCredentialButton) }
    private val sharedPreferences by lazy { getEncryptedSharedPreferences() }

    private fun getEncryptedSharedPreferences(): SharedPreferences {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        return EncryptedSharedPreferences.create(
            Utils().sharedPreferencesName,
            masterKeyAlias,
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private fun saveCredentials(){
        val login = intent.getStringExtra("login")
        val password = intent.getStringExtra("password")

        val editor = sharedPreferences.edit()

        editor.putString("login", login).putString("password", password).apply()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ask_for_credentials)

        saveCredentialButton.setOnClickListener {
            saveCredentials()
            val intent = Intent(applicationContext, DashboardActivity::class.java)
            startActivity(intent)
        }

        cancelSavingCredentialButton.setOnClickListener {
            val intent = Intent(applicationContext, DashboardActivity::class.java)
            startActivity(intent)
        }
    }
}