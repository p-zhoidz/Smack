package com.example.smack.controller

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.smack.R
import com.example.smack.service.AuthService
import com.example.smack.service.UserDataService
import com.example.smack.utility.BRAODCAST_USER_DATA_CHANGED
import kotlinx.android.synthetic.main.activity_create_user.*
import java.util.*


class CreateUserActivity : AppCompatActivity() {

    var userAvatar = "profileDefault"
    var avatarColor = "[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)

        createSpinner.visibility = View.INVISIBLE
    }

    fun generateUserAvatar(view: View) {
        val random = Random()
        val color = random.nextInt(2)
        val avatar = random.nextInt(28)

        userAvatar = if (color == 0) {
            "light$avatar"
        } else {
            "dark$avatar"
        }

        val resourceId = resources.getIdentifier(userAvatar, "drawable", packageName)
        createUserAvatarView.setImageResource(resourceId)
    }

    fun generateBackgroundColorClick(view: View) {
        val random = Random()
        var r = random.nextInt(255)
        val g = random.nextInt(255)
        val b = random.nextInt(255)

        createUserAvatarView.setBackgroundColor(Color.rgb(r, g, b))

        val savedR = r.toDouble() / 255
        val savedG = g.toDouble() / 255
        val savedB = b.toDouble() / 255

        avatarColor = "[$savedR, $savedG, $savedB, 1]"
    }

    fun createUserClick(view: View) {
        enableSpinner(true)

        val userName = createUserNameTxt.text.toString()
        val email = createEmailTxt.text.toString()
        val password = createPasswordTxt.text.toString()

        if (userName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {


            AuthService.registerUser(this, email, password) {
                if (it) {
                    AuthService.loginUser(this, email, password) {
                        if (it) {
                            AuthService.createUser(
                                this,
                                email,
                                avatarColor,
                                userAvatar,
                                userName
                            ) { success ->

                                if (success) {
                                    val userDateChanged = Intent(BRAODCAST_USER_DATA_CHANGED)
                                    LocalBroadcastManager.getInstance(this).sendBroadcast(userDateChanged)
                                    enableSpinner(false)

                          /*          val mainActivity = Intent(this, MainActivity::class.java)
                                    startActivity(mainActivity)*/
                                    finish()
                                } else {
                                    errorToast()
                                }
                            }
                        } else {
                            errorToast()
                        }
                    }
                } else {
                    errorToast()
                }

            }
        } else {
            Toast.makeText(this, "Make sure fields are not empty", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun errorToast() {
        Toast.makeText(this, "Something went wrong. Please try again", Toast.LENGTH_LONG)
            .show()
        enableSpinner(true)
    }

    private fun enableSpinner(enabled: Boolean) {
        createSpinner.visibility = if (enabled) View.VISIBLE else View.INVISIBLE
        createUserBackgroundBtn.isEnabled = !enabled
        createUserNameTxt.isEnabled = !enabled
        createEmailTxt.isEnabled = !enabled
        createPasswordTxt.isEnabled = !enabled
    }
}
