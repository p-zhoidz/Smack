package com.example.smack.utility

import android.content.Context
import com.android.volley.toolbox.Volley

class SharedPrefs(context: Context) {
    private val PREFS_FILENAME = "prefs"

    private val prefs = context.getSharedPreferences(PREFS_FILENAME, 0)

    private val IS_LOGGED_IN = "isLoggedIn"
    private val AUTH_TOKEN = "authToken"
    private val USER_EMAIL = "userEmail"

    var isLoggedIn: Boolean
        get() = prefs.getBoolean(IS_LOGGED_IN, false)
        set(value) = prefs.edit().putBoolean(IS_LOGGED_IN, value).apply()

    var authToken: String
        get() = prefs.getString(AUTH_TOKEN, "")!!
        set(value) = prefs.edit().putString(AUTH_TOKEN, value).apply()


    var userEmail: String
        get() = prefs.getString(USER_EMAIL, "")!!
        set(value) = prefs.edit().putString(USER_EMAIL, value).apply()


    val requestQueue = Volley.newRequestQueue(context)
}