package dariusz.cabala.cards.project

import android.util.Log
import java.net.URLConnection

class RequestSession {
    companion object {
        var sessionCookie = ""

        fun saveSessionCookie(headers:Map<String, List<String>>){
            for(headerEntry in headers.entries){
                if(headerEntry.key == "set-cookie"){
                    sessionCookie = headerEntry.value.joinToString()
                }
            }
        }
    }
}