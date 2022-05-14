package dariusz.cabala.cards.project

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import java.math.BigInteger
import java.security.MessageDigest

class Utils {
    public val sharedPreferencesName = "sharedPreferencesName"

    fun md5Hash(str: String): String {
        val md = MessageDigest.getInstance("MD5")
        val bigInt = BigInteger(1, md.digest(str.toByteArray(Charsets.UTF_8)))
        return String.format("%032x", bigInt)
    }

    fun getEncryptedSharedPreferences(context: Context): SharedPreferences {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        return EncryptedSharedPreferences.create(
            Utils().sharedPreferencesName,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}