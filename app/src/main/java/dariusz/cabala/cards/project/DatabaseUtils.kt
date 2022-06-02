package dariusz.cabala.cards.project

import android.content.Context
import androidx.room.Room
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import java.util.*

class DatabaseUtils {

    companion object {
        lateinit var db: CreditCardDatabase

        fun initDatabase(applicationContext: Context) {
            val builder = Room.databaseBuilder(
                applicationContext,
                CreditCardDatabase::class.java, "encypted"
            )
            val passphrase = SQLiteDatabase.getBytes(
                Base64.getDecoder().decode("aGFzbG9fel9kb2x1X3BsZWNvdw==").toString().toCharArray()
            )
            val factory = SupportFactory(passphrase)
            builder.openHelperFactory(factory)

            db = builder.build()
        }
    }
}