package com.example.smack.service

import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.example.smack.controller.App
import com.example.smack.model.Channel
import com.example.smack.model.Message
import com.example.smack.utility.URL_CHANNELS
import com.example.smack.utility.URL_MESSAGES
import org.json.JSONException

object MessageService {
    val channels = ArrayList<Channel>()
    val messages = ArrayList<Message>()

    fun getChannels(complete: (Boolean) -> Unit) {

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
                    return mutableMapOf("authorization" to "Bearer ${App.prefs.authToken}")
                }

                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }
            }

        App.prefs.requestQueue.add(getChannelsRequest)
    }


    fun getMessages(channelId: String, complete: (Boolean) -> Unit) {

        val getMessagesRequest =
            object :
                JsonArrayRequest(
                    Method.GET,
                    "$URL_MESSAGES$channelId",
                    null,
                    Response.Listener { response ->
                        try {
                            println(response)
                            for (x in 0 until response.length()) {
                                val jsonMessage = response.getJSONObject(x)
                                val message = Message(
                                    message = jsonMessage.getString("messageBody"),
                                    userName = jsonMessage.getString("userName"),
                                    channelId = jsonMessage.getString("channelId"),
                                    userAvatar = jsonMessage.getString("userAvatar"),
                                    userAvatarColor = jsonMessage.getString("userAvatarColor"),
                                    id = jsonMessage.getString("_id"),
                                    timeStamp = jsonMessage.getString("timeStamp")
                                )
                                messages.add(message)
                            }
                            complete(true)
                        } catch (e: JSONException) {
                            Log.d("ERROR", "EXC:" + e.localizedMessage)
                            complete(false)
                        }

                    },
                    Response.ErrorListener { error ->
                        Log.d("ERROR", "Could not load channels: $error")
                        complete(false)
                    }) {
                override fun getHeaders(): MutableMap<String, String> {
                    return mutableMapOf("authorization" to "Bearer ${App.prefs.authToken}")
                }

                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }
            }

        App.prefs.requestQueue.add(getMessagesRequest)
    }


    fun clearMessages() {
        messages.clear()
    }

    fun clearChannels() {
        channels.clear()
    }
}