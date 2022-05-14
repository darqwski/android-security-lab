package dariusz.cabala.cards.project.model

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CreditCardDao {
    @Query("SELECT * FROM creditCard")
    fun getAll(): List<CreditCard>
    @Query("SELECT * FROM creditCard")
    fun getAllCursor(): Cursor
    @Insert
    fun insert(creditCard: CreditCard)
    @Query("DELETE FROM creditCard")
    fun deleteAll()
}