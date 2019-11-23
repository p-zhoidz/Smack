package com.example.smack.controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.smack.R
import com.example.smack.service.AuthService
import com.example.smack.service.UserDataService
import com.example.smack.utility.BRAODCAST_USER_DATA_CHANGED
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.nav_header_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private val userDataReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            runOnUiThread {
                if (AuthService.isLoggedIn) {
                    userNameNavHeaderTxt.text = UserDataService.name
                    userEmailNavHeaderTxt.text = UserDataService.email
                    val resourceId =
                        resources.getIdentifier(UserDataService.avatarName, "drawable", packageName)

                    userImageNavHeader.setImageResource(resourceId)
                    userImageNavHeader.setBackgroundColor(UserDataService.getColor(UserDataService.avatarColor))
                    loginBtn.text = context.resources.getString(R.string.nav_header_logout)

                } else {
                    println("!!!!!!!!!!!!")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        setDefaults()

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        LocalBroadcastManager.getInstance(navView.context).registerReceiver(
            userDataReceiver,
            IntentFilter(BRAODCAST_USER_DATA_CHANGED)
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun loginBtnNavClick(view: View) {
        if (AuthService.isLoggedIn) {
            UserDataService.logOut()
            setDefaults()
        } else {
            val logInIntent = Intent(this, LoginActivity::class.java)
            startActivity(logInIntent)
        }
    }

    fun addChannelClicked(view: View) {

    }

    fun sendMessageBtnClick(view: View) {

    }

    private fun setDefaults() {
        userNameNavHeaderTxt.text = this.resources.getString(R.string.nav_header_title)
        userEmailNavHeaderTxt.text = this.resources.getString(R.string.nav_header_subtitle)
        loginBtn.text = this.resources.getString(R.string.nav_header_login)
        userImageNavHeader.setBackgroundColor(Color.TRANSPARENT)

        val resourceId =
            resources.getIdentifier("profiledefault", "drawable", packageName)

        userImageNavHeader.setImageResource(resourceId)
    }
}
