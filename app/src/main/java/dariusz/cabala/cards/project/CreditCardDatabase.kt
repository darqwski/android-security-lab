package dariusz.cabala.cards.project

import androidx.room.Database
import androidx.room.RoomDatabase
import dariusz.cabala.cards.project.model.CreditCard
import dariusz.cabala.cards.project.model.CreditCardDao

@Database(entities = [CreditCard::class], version = 1)
abstract class CreditCardDatabase : RoomDatabase() {
    abstract fun creditCardDao(): CreditCardDao
}