package com.example.smack.service

import android.graphics.Color
import java.util.*

object UserDataService {

    lateinit var id: String
    lateinit var avatarColor: String
    lateinit var avatarName: String
    lateinit var email: String
    lateinit var name: String

    fun logOut() {
        id = ""
        avatarColor = ""
        avatarName = ""
        email = ""
        name = ""

        AuthService.authToken = ""
        AuthService.userEmail = ""
        AuthService.isLoggedIn = false
    }


    fun getColor(avatarColor: String): Int {
        val strippedColor = avatarColor
            .replace("[", "")
            .replace("]", "")
            .replace(",", "")

        var r = 0
        var g = 0
        var b = 0

        val scanner = Scanner(strippedColor)

        if (scanner.hasNext()) {
            r = (scanner.nextDouble() * 255).toInt()
            g = (scanner.nextDouble() * 255).toInt()
            b = (scanner.nextDouble() * 255).toInt()
        }
        return Color.rgb(r, g, b)
    }
}