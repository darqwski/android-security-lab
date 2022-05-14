package dariusz.cabala.cards.project

import android.content.Context
import androidx.room.Room

class DatabaseUtils {

    companion object {
        lateinit var db: CreditCardDatabase

        fun initDatabase(applicationContext: Context) {
            db = Room.databaseBuilder(
                applicationContext,
                CreditCardDatabase::class.java, "database-name"
            ).build()
        }
    }
}