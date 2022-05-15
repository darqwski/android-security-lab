package dariusz.cabala.cards.project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dariusz.cabala.cards.project.model.CreditCard
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.OutputStreamWriter
import java.lang.reflect.Type
import java.net.HttpURLConnection
import java.net.URL

class CreditCardForm : AppCompatActivity() {
    private val editFormCardName by lazy { findViewById<AppCompatEditText>(R.id.editFormCardName) }
    private val editFormCardNumber by lazy { findViewById<AppCompatEditText>(R.id.editFormCardNumber) }
    private val editFormCardProvider by lazy { findViewById<AppCompatEditText>(R.id.editFormCardProvider) }
    private val editFormCardDate by lazy { findViewById<AppCompatEditText>(R.id.editFormCardDate) }
    private val editFormCardCVC by lazy { findViewById<AppCompatEditText>(R.id.editFormCardCVC) }
    private val confirmFormButton by lazy { findViewById<MaterialButton>(R.id.confirmFormButton)  }

    fun applyCreditCardToForm(creditCard: CreditCard){
        editFormCardName.setText(creditCard.cardName, TextView.BufferType.EDITABLE)
        editFormCardNumber.setText(creditCard.cardNumber, TextView.BufferType.EDITABLE)
        editFormCardDate.setText(creditCard.mothYear, TextView.BufferType.EDITABLE)
        editFormCardCVC.setText(creditCard.CVC, TextView.BufferType.EDITABLE)
        editFormCardProvider.setText(creditCard.cardProvider, TextView.BufferType.EDITABLE)

    }

    fun getSingleCard(){
        val cardId = intent.getIntExtra("cardId", -1)

        if(cardId == -1){
            return
        }

        GlobalScope.launch {
            val url = URL("https://program-it-yourself.pl/BAM/cards/?cardId=$cardId")
            with(url.openConnection()  as HttpURLConnection) {
                var result = ""

                setRequestProperty("cookie",RequestSession.sessionCookie)
                inputStream.bufferedReader().use {
                    it.lines().forEach { line ->
                        result += line
                    }
                }

                val type : Type = object : TypeToken<CreditCard>() {}.type

                val gson = Gson()

                val creditCard: CreditCard = gson.fromJson(result, type)

                applyCreditCardToForm(creditCard)
            }
        }
    }

    fun gatherFormData(): CreditCard {
        val cardName = editFormCardName.text.toString()
        val cardNumber = editFormCardNumber.text.toString()
        val cardDate = editFormCardDate.text.toString()
        val cardCVC = editFormCardCVC.text.toString()
        val cardProvider = editFormCardProvider.text.toString()

        val creditCard = CreditCard(-1, -1, cardNumber, cardDate, cardCVC, cardProvider, cardName)

        return creditCard;
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credit_card_form)

        val formType = intent.getStringExtra("type")
        val cardId = intent.getIntExtra("cardId", -1)

        val isEditForm = formType == "edit"

        Log.wtf("formType",formType)
        Log.wtf("cardId","$cardId")
        if(isEditForm){
            getSingleCard()
        }


        confirmFormButton.setOnClickListener {
            val creditCard = gatherFormData()
            var requestData = HashMap<String, String>();

            requestData.set("cardNumber", creditCard.cardNumber)
            requestData.set("mothYear", creditCard.mothYear)
            requestData.set("CVC", creditCard.CVC)
            requestData.set("cardProvider", creditCard.cardProvider)
            requestData.set("cardName", creditCard.cardName)

            if (formType == "edit") {
                requestData.set("cardId", "$cardId")
            }

            val url = URL("https://program-it-yourself.pl/BAM/cards/")

            GlobalScope.launch {
                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = if (isEditForm) "PUT" else "POST"
                    setRequestProperty("cookie",RequestSession.sessionCookie)

                    val wr = OutputStreamWriter(outputStream);
                    wr.write(Gson().toJson(requestData));
                    wr.flush();

                    var result = ""
                    inputStream.bufferedReader().use {
                        it.lines().forEach { line ->
                            result += line
                        }
                    }

                    runOnUiThread {
                        Toast.makeText(
                            applicationContext,
                            if (isEditForm) "Kartę zmieniona pomyślnie" else "Karta dodana pomyślnie",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    finish()
                }
            }
        }
    }
}