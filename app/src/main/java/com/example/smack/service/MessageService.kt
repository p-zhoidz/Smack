package com.example.smack.service

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.smack.model.Channel
import com.example.smack.utility.URL_CHANNELS
import org.json.JSONException

object MessageService {
    val channels = ArrayList<Channel>()


    fun getChannels(context: Context, complete: (Boolean) -> Unit) {

        val getChannelsRequest =
            object :
                JsonArrayRequest(Method.GET, URL_CHANNELS, null, Response.Listener { response ->
                    try {
                        println(response)
                        for (x in 0 until response.length()) {
                            val jsonChannel = response.getJSONObject(x)
                            val channel = Channel(
                                name = jsonChannel.getString("name"),
                                description = jsonChannel.getString("description"),
                                id = jsonChannel.getString("_id")
                            )
                            channels.add(channel)
                        }
                        complete(true)
                    } catch (e: JSONException) {
                        Log.d("ERROR", "EXC:" + e.localizedMessage)
                        complete(false)
                    }

                }, Response.ErrorListener { error ->
                    Log.d("ERROR", "Could not load channels: $error")
                    complete(false)
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    return mutableMapOf("authorization" to "Bearer ${AuthService.authToken}")
                }

                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }
            }

        Volley.newRequestQueue(context).add(getChannelsRequest)
    }
}