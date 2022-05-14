package dariusz.cabala.cards.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dariusz.cabala.cards.project.adapters.CreditCardAdapter
import dariusz.cabala.cards.project.model.CreditCard
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.OutputStreamWriter
import java.lang.reflect.Type
import java.net.HttpURLConnection
import java.net.URL

class DashboardActivity : AppCompatActivity() {
    private val creditCardsRecyclerView by lazy { findViewById<RecyclerView>(R.id.creditCardsRecyclerView) }
    private lateinit var downloadedCards: Array<CreditCard>

    fun showCards(){
        runOnUiThread {
            val adapter = CreditCardAdapter(downloadedCards)
            creditCardsRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
            creditCardsRecyclerView.adapter = adapter
        }
    }

    fun getCards(){
        GlobalScope.launch {
            val url = URL("https://program-it-yourself.pl/BAM/cards/")
            with(url.openConnection()  as HttpURLConnection) {
                var result = ""

                setRequestProperty("cookie",RequestSession.sessionCookie)
                inputStream.bufferedReader().use {
                    it.lines().forEach { line ->
                        result += line
                    }
                }

                val type : Type = object : TypeToken<Array<CreditCard>>() {}.type

                val gson = Gson()

                val creditCards: Array<CreditCard> = gson.fromJson(result, type)

                for(creditCard in creditCards){
                    Log.d("CreditCard",creditCard.cardNumber)
                }

                downloadedCards = creditCards

                showCards()


//                runOnUiThread(Runnable {
//                    Toast.makeText(applicationContext, response.get("message"), Toast.LENGTH_LONG).show()
//                })
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        getCards()
    }
}