package com.example.smack.controller

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smack.R
import com.example.smack.service.AuthService
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        enableSpinner(false)
    }

    fun loginBtnClick(view: View) {
        hideKeyBoard()
        enableSpinner(true)
        val email = loginEmailTxt.text.toString()
        val password = loginPasswordTxt.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {

            AuthService.loginUser(this, email, password) {
                if (it) {
                    finish()
                } else {
                    errorToast()
                }
            }
        } else {
            Toast.makeText(this, "Please set data", Toast.LENGTH_LONG).show()
        }
    }

    fun signInBtnClick(view: View) {
        val createUserIntent = Intent(this, CreateUserActivity::class.java)
        startActivity(createUserIntent)
        finish()
    }

    private fun errorToast() {
        Toast.makeText(this, "Something went wrong. Please try again", Toast.LENGTH_LONG)
            .show()
        enableSpinner(false)
    }

    private fun enableSpinner(enabled: Boolean) {
        loginSpinner.visibility = if (enabled) View.VISIBLE else View.INVISIBLE
        loginBtn.isEnabled = !enabled
        signInBtn.isEnabled = !enabled
    }

    fun hideKeyBoard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }
}
