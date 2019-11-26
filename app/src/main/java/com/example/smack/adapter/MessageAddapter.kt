package com.example.smack.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smack.R
import com.example.smack.model.Message
import com.example.smack.service.UserDataService
import java.text.SimpleDateFormat
import java.util.*

class MessageAddapter(private val context: Context, private val messages: List<Message>) :
    RecyclerView.Adapter<MessageAddapter.MessageViewHolder>() {
    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        var formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        var parser = SimpleDateFormat("E, h:mm a", Locale.getDefault())


        private val userImage: ImageView = itemView.findViewById(R.id.messageUserImg)
        private val timeStamp: TextView = itemView.findViewById(R.id.messageTimestampeLable)
        private val userName: TextView = itemView.findViewById(R.id.messageUserNameLabel)
        private val messageBody: TextView = itemView.findViewById(R.id.messageBodyLabel)


        fun bindMessage(context: Context, message: Message) {
            userImage.setImageResource(
                context.resources.getIdentifier(
                    message.userAvatar,
                    "drawable",
                    context.packageName
                )
            )
            userImage.setBackgroundColor(UserDataService.getColor(message.userAvatarColor))


            timeStamp.text = formatTimeStamp(message.timeStamp)
            userName.text = message.userName
            messageBody.text = message.message

        }

        fun formatTimeStamp(timeStamp: String): String {
            return try {
                timeStamp.let {
                    formatter.parse(it)
                }.let {
                    parser.format(it!!)
                }
            } catch (e: Exception) {
                ""
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.message_list_view, parent, false)
        return MessageViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bindMessage(context, messages[position])
    }

}