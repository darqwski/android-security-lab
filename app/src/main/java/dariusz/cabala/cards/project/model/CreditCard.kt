package dariusz.cabala.cards.project.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CreditCard(
    @PrimaryKey(autoGenerate = true) val cardUniqueId: Int,
    @ColumnInfo(name = "cardId")  val cardId: Int,
    @ColumnInfo(name = "cardNumber") val cardNumber: String,
    @ColumnInfo(name = "mothYear") val mothYear: String,
    @ColumnInfo(name = "CVC") val CVC: String,
    @ColumnInfo(name = "cardProvider")val cardProvider: String,
    @ColumnInfo(name = "cardName") val cardName: String
    )
