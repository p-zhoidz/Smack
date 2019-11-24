package com.example.smack.service

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.example.smack.controller.App
import com.example.smack.utility.*
import org.json.JSONException
import org.json.JSONObject

object AuthService {

    fun registerUser(
        email: String,
        password: String,
        complete: (Boolean) -> Unit
    ) {
        val url = URL_REGISTER

        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)

        val requestBody = jsonBody.toString()

        val registerRequest = object : StringRequest(Method.POST, url, Response.Listener {
            complete(true)
        }, Response.ErrorListener { error ->
            Log.d("ERROR", "Could not register user: $error")
            complete(false)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }
        App.prefs.requestQueue.add(registerRequest)
    }


    fun loginUser(context: Context, email: String, password: String, complete: (Boolean) -> Unit) {

        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("password", password)

        val requestBody = jsonBody.toString()


        val loginRequest =
            object : JsonObjectRequest(Method.POST, URL_LOGIN, null, Response.Listener {
                try {
                    println(it)
                    App.prefs.authToken = it.getString("token")
                    App.prefs.userEmail = it.getString("user")
                    App.prefs.isLoggedIn = true

                    findUserByEmail(context) { found ->
                        complete(found)
                    }
                } catch (e: JSONException) {
                    Log.d("ERROR", "EXC:" + e.localizedMessage)
                    App.prefs.authToken = ""
                    App.prefs.userEmail = ""
                    App.prefs.isLoggedIn = false
                    complete(false)
                }

            }, Response.ErrorListener { error ->
                Log.d("ERROR", "Could not login user: $error")
                complete(false)
            }) {

                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }

                override fun getBody(): ByteArray {
                    return requestBody.toByteArray()
                }
            }

        App.prefs.requestQueue.add(loginRequest)
    }


    fun createUser(
        email: String,
        avatarColor: String,
        avatarName: String,
        name: String,
        complete: (Boolean) -> Unit
    ) {


        val jsonBody = JSONObject()
        jsonBody.put("email", email)
        jsonBody.put("name", name)
        jsonBody.put("avatarColor", avatarColor)
        jsonBody.put("avatarName", avatarName)


        val requestBody = jsonBody.toString()


        val createUserRequest =
            object : JsonObjectRequest(Method.POST, URL_CREATE_USER, null, Response.Listener {
                try {
                    UserDataService.email = it.getString("email")
                    UserDataService.name = it.getString("name")
                    UserDataService.id = it.getString("_id")
                    UserDataService.avatarColor = it.getString("avatarColor")
                    UserDataService.avatarName = it.getString("avatarName")
                    complete(true)
                } catch (e: JSONException) {
                    Log.d("ERROR", "EXC:" + e.localizedMessage)
                    complete(false)
                }
            }, Response.ErrorListener { error ->
                Log.d("ERROR", "Could not create user: $error")
                complete(false)
            }) {

                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }

                override fun getBody(): ByteArray {
                    return requestBody.toByteArray()
                }

                override fun getHeaders(): MutableMap<String, String> {
                    return mutableMapOf("authorization" to "Bearer ${App.prefs.authToken}")
                }
            }

        App.prefs.requestQueue.add(createUserRequest)
    }


    fun findUserByEmail(context: Context, complete: (Boolean) -> Unit) {

        val findByEmailRequest =
            object :
                JsonObjectRequest(
                    Method.GET,
                    "$URL_FIND_USER${App.prefs.userEmail}",
                    null,
                    Response.Listener {
                        try {
                            println(it)

                            UserDataService.email = it.getString("email")
                            UserDataService.name = it.getString("name")
                            UserDataService.id = it.getString("_id")
                            UserDataService.avatarColor = it.getString("avatarColor")
                            UserDataService.avatarName = it.getString("avatarName")

                            val userDateChanged = Intent(BRAODCAST_USER_DATA_CHANGED)
                            LocalBroadcastManager.getInstance(context)
                                .sendBroadcast(userDateChanged)

                            complete(true)
                        } catch (e: JSONException) {
                            Log.d("ERROR", "EXC:" + e.localizedMessage)


                            UserDataService.email = ""
                            UserDataService.name = ""
                            UserDataService.id = ""
                            UserDataService.avatarColor = ""
                            UserDataService.avatarName = ""
                            complete(false)
                        }

                    },
                    Response.ErrorListener { error ->
                        Log.d("ERROR", "Could not find user: $error")
                        complete(false)
                    }) {
                override fun getHeaders(): MutableMap<String, String> {
                    return mutableMapOf("authorization" to "Bearer ${App.prefs.authToken}")
                }
            }

        App.prefs.requestQueue.add(findByEmailRequest)
    }


}