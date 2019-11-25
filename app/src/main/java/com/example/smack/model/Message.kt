package com.example.smack.model

data class Message(
    val message: String, val userName: String, val channelId: String,
    val userAvatar: String, val userAvatarColor: String,
    val id: String, val timeStamp: String
)