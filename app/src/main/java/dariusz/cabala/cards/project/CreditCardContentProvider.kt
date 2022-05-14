package dariusz.cabala.cards.project

import android.content.*
import android.database.Cursor
import android.net.Uri
import androidx.annotation.Nullable
import dariusz.cabala.cards.project.model.CreditCardDao


// Poniższa implementacja jest bazowana na kodzie tego przemiłego pana z Indii:
// https://medium.com/@aniket93shetty/content-provider-for-sharing-room-database-using-kotlin-c196ca1d8471
class CreditCardContentProvider : ContentProvider() {
    companion object {
        /** The authority of this content provider.  */
        const val AUTHORITY = "dariusz.cabala.cards.project.CreditCardContentProvider"

        /**The match code for some items in the companyTM table.  */
        private const val ALL_CREDIT_CARDS= 1
        /** The match code for an item in the companyTM table.  */
        private val MATCHER = UriMatcher(UriMatcher.NO_MATCH)

        init {
            MATCHER.addURI(
                AUTHORITY,
                "creditCard",
                ALL_CREDIT_CARDS
            )


        }
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String?>?
    ): Int {
        TODO("Not implemented")
    }

    override fun delete(
        uri: Uri, selection: String?,
        selectionArgs: Array<String?>?
    ): Int {
        TODO("Not implemented")
    }


    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        TODO("Not implemented")
    }

    @Nullable
    override fun query(
        uri: Uri, @Nullable projection: Array<String>?, @Nullable selection: String?,
        @Nullable selectionArgs: Array<String>?, @Nullable sortOrder: String?
    ): Cursor? {
        val context = context ?: return null
        val creditCardDao: CreditCardDao = DatabaseUtils.db.creditCardDao()
        val cursor: Cursor
        cursor = creditCardDao.getAllCursor()!!
        cursor.setNotificationUri(context.contentResolver, uri)

        return cursor
    }

    @Nullable
    override fun getType(uri: Uri): String? {
        return when (MATCHER.match(uri)) {
            ALL_CREDIT_CARDS -> "vnd.android.cursor.dir/$AUTHORITY.creditCard"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }


    @Throws(OperationApplicationException::class)
    override fun applyBatch(
        operations: ArrayList<ContentProviderOperation?>
    ): Array<ContentProviderResult?> {
        val database: CreditCardDatabase = DatabaseUtils.db
        database.beginTransaction()
        return try {
            val result = super.applyBatch(operations)
            database.setTransactionSuccessful()
            result
        } finally {
            database.endTransaction()
        }
    }

}