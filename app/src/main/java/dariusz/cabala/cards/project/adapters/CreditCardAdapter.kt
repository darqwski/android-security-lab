package dariusz.cabala.cards.project.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dariusz.cabala.cards.project.*
import dariusz.cabala.cards.project.model.CreditCard
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.OutputStreamWriter
import java.lang.reflect.Type
import java.net.HttpURLConnection
import java.net.URL

class CreditCardAdapter (private val dataSet: Array<CreditCard>, private val applicationContext: Context) :
    RecyclerView.Adapter<CreditCardAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardName: TextView
        val cardNumber: TextView
        val cardDate: TextView
        val cardProvider: TextView
        val editButton: Button
        val toggleButton: Button
        val deleteButton: Button

        init {
            cardName = view.findViewById(R.id.creditCardName)
            cardNumber = view.findViewById(R.id.creditCardRowCardNumber)
            cardDate = view.findViewById(R.id.creditCardRowDate)
            cardProvider = view.findViewById(R.id.creditCardRowProviderName)
            editButton = view.findViewById(R.id.editCardButton)
            toggleButton = view.findViewById(R.id.showNumberButton)
            deleteButton = view.findViewById(R.id.removeCardButton)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.credit_card_row, viewGroup, false)

        return ViewHolder(view)
    }

    fun maskCardCharacter(character: Char): Char{
        if(character == '0' || character == '1' || character == '2' || character == '3' || character == '4' ||
            character == '5' || character == '6' || character == '7' || character == '8' || character == '9' )
            return '*'

        return character
    }

    fun maskCardNumber(cardNumber: String): String {
        var buildedString = ""
        for (i in cardNumber.indices) {
            if(i >= cardNumber.length - 4){
                buildedString += cardNumber[i]
            } else {
                buildedString += maskCardCharacter(cardNumber[i])
            }
        }
        return buildedString;
    }

    fun removeCard(creditCard: CreditCard){
        val requestBody = HashMap<String, String>()
        requestBody.put("cardId",creditCard.cardId.toString())

        val gson = Gson()
        val jsonRequestBody = gson.toJson(requestBody)
        val url = URL("https://program-it-yourself.pl/BAM/cards/")

        GlobalScope.launch {
            with(url.openConnection()  as HttpURLConnection) {
                requestMethod = "DELETE"
                setRequestProperty("cookie", RequestSession.sessionCookie)

                val wr = OutputStreamWriter(outputStream);
                wr.write(jsonRequestBody);
                wr.flush();

                var result = ""

                inputStream.bufferedReader().use {
                    it.lines().forEach { line ->
                        result += line
                    }
                }

                (applicationContext as DashboardActivity).getCards()
            }
        }
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val creditCard = dataSet[position]
        viewHolder.cardName.text = creditCard.cardName
        viewHolder.cardNumber.text = maskCardNumber(creditCard.cardNumber)
        viewHolder.cardProvider.text = creditCard.cardProvider
        viewHolder.cardDate.text = creditCard.mothYear

        viewHolder.toggleButton.setOnClickListener {
            val asButton: Button = it as Button

            if (asButton.text.equals("Poka??")) {
                asButton.text = "Ukryj"
                viewHolder.cardNumber.text = creditCard.cardNumber

            } else {
                asButton.text = "Poka??"
                viewHolder.cardNumber.text = maskCardNumber(creditCard.cardNumber)

            }
        }

        viewHolder.deleteButton.setOnClickListener {
            removeCard(creditCard)
        }

        viewHolder.editButton.setOnClickListener {
            val intent = Intent(applicationContext, CreditCardForm::class.java)
            intent.putExtra("type","edit")
            intent.putExtra("cardId",creditCard.cardId)
            applicationContext.startActivity(intent)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}