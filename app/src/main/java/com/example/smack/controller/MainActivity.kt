package com.example.smack.controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.smack.R
import com.example.smack.model.Channel
import com.example.smack.service.AuthService
import com.example.smack.service.MessageService
import com.example.smack.service.UserDataService
import com.example.smack.utility.BRAODCAST_USER_DATA_CHANGED
import com.example.smack.utility.SOCKET_URL
import com.google.android.material.navigation.NavigationView
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var channelAdapter: ArrayAdapter<Channel>
    var selectedChannel: Channel? = null

    private val onNewChannel = Emitter.Listener { args ->
        runOnUiThread {
            val channel = Channel(
                name = args[0] as String,
                description = args[1] as String,
                id = args[2] as String
            )

            MessageService.channels.add(channel)
            channelAdapter.notifyDataSetChanged()

            println(channel)
        }
    }

    val socket = IO.socket(SOCKET_URL)

    private val userDataReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            runOnUiThread {
                if (App.prefs.isLoggedIn) {
                    userNameNavHeaderTxt.text = UserDataService.name
                    userEmailNavHeaderTxt.text = UserDataService.email
                    val resourceId =
                        resources.getIdentifier(UserDataService.avatarName, "drawable", packageName)

                    userImageNavHeader.setImageResource(resourceId)
                    userImageNavHeader.setBackgroundColor(UserDataService.getColor(UserDataService.avatarColor))
                    loginBtn.text = context.resources.getString(R.string.nav_header_logout)


                    MessageService.getChannels { complete ->
                        if (complete) {
                            if (MessageService.channels.count() > 0) {
                                selectedChannel = MessageService.channels[0]
                                channelAdapter.notifyDataSetChanged()
                                updateWithChannel()
                            } else {

                            }
                        }
                    }

                } else {
                    println("!!!!!!!!!!!!")
                }
            }
        }
    }

    fun updateWithChannel() {
        mainChannelName.text = "#${selectedChannel?.name}"
    }

    override fun onResume() {
        super.onResume()
        socket.connect()
        socket.on("channelCreated", onNewChannel)
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataReceiver)
        socket.disconnect()

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

        setAdapters()

        LocalBroadcastManager.getInstance(this).registerReceiver(
            userDataReceiver,
            IntentFilter(BRAODCAST_USER_DATA_CHANGED)
        )

        if (App.prefs.isLoggedIn) {
            AuthService.findUserByEmail(this) {

            }
        }

        chanel_list.setOnItemClickListener { _, _, position, _ ->
            selectedChannel = MessageService.channels[position]
            drawer_layout.closeDrawer(GravityCompat.START)
            updateWithChannel()

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun loginBtnNavClick(view: View) {
        if (App.prefs.isLoggedIn) {
            UserDataService.logOut()
            setDefaults()
        } else {
            val logInIntent = Intent(this, LoginActivity::class.java)
            startActivity(logInIntent)
        }
    }

    fun addChannelClicked(view: View) {

        if (App.prefs.isLoggedIn) {
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)

            builder.setView(dialogView)
                .setPositiveButton("Add") { _, _ ->
                    val nameTxtField = dialogView.findViewById<EditText>(R.id.addChannelNameTxt)
                    val nameDescField = dialogView.findViewById<EditText>(R.id.addChannelDescTxt)

                    val channelName = nameTxtField.text
                    val channelDesc = nameDescField.text

                    socket.emit("newChannel", channelName, channelDesc)
                }
                .setNegativeButton("Cancel") { _, _ ->
                }.show()
        }

    }

    fun sendMessageBtnClick(view: View) {
        hideKeyBoard()
    }


    private fun setDefaults() {
        runOnUiThread {
            userNameNavHeaderTxt.text = this.resources.getString(R.string.nav_header_title)
            userEmailNavHeaderTxt.text = this.resources.getString(R.string.nav_header_subtitle)
            loginBtn.text = this.resources.getString(R.string.nav_header_login)
            userImageNavHeader.setBackgroundColor(Color.TRANSPARENT)

            val resourceId =
                resources.getIdentifier("profiledefault", "drawable", packageName)

            userImageNavHeader.setImageResource(resourceId)
        }
    }

    private fun hideKeyBoard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }


    private fun setAdapters() {
        channelAdapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, MessageService.channels)
        chanel_list.adapter = channelAdapter
    }
}
