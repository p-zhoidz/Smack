package com.example.smack.controller

import android.app.Application
import com.example.smack.utility.SharedPrefs

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        prefs = SharedPrefs(applicationContext)
    }

    companion object {
        lateinit var prefs: SharedPrefs
    }
}