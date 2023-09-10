package com.vladrip.ifchat.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.vladrip.ifchat.R
import com.vladrip.ifchat.data.entity.Chat
import com.vladrip.ifchat.data.entity.Message
import com.vladrip.ifchat.ui.auth.AuthActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Firebase.auth.currentUser == null) {
            startAuthActivity()
            return
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.restoreRequests()
                viewModel.saveDeviceToken(Firebase.messaging.token.await())
            }
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_container) as NavHostFragment
        navController = navHostFragment.navController

        intent.getStringExtra("message")?.let {
            val chatId = viewModel.gson().fromJson(it, Message::class.java).chatId
            val chatType: Chat.ChatType = Chat.ChatType
                .valueOf(intent.getStringExtra("chatType") ?: Chat.ChatType.GROUP.name)
            navController.navigate(
                R.id.action_chat_list_to_chat, bundleOf(
                    "chatId" to chatId,
                    "chatType" to chatType
                )
            )
        }

        val drawer = findViewById<NavigationView>(R.id.nav_drawer)
        drawer.setupWithNavController(navController)
        appBarConfiguration =
            AppBarConfiguration(navController.graph, drawer.parent as DrawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        setCustomMenuListeners(drawer.menu)

        Firebase.auth.currentUser?.let { user ->
            val drawerHeader = drawer.inflateHeaderView(R.layout.main_drawer_header)
            drawerHeader.findViewById<TextView>(R.id.drawer_phone_number).text = user.phoneNumber
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.CREATED) {
                    viewModel.getPerson(user.uid).collectLatest {
                        drawerHeader.findViewById<TextView>(R.id.drawer_username).text = it.fullName
                    }
                }
            }
        }
    }

    private fun setCustomMenuListeners(menu: Menu) {
        menu.findItem(R.id.logout).setOnMenuItemClickListener {
            lifecycleScope.launch { viewModel.logout() }
            startAuthActivity()
            return@setOnMenuItemClickListener true
        }
    }

    private fun startAuthActivity() {
        val authIntent = Intent(this, AuthActivity::class.java)
            .setAction(Intent.ACTION_VIEW)
        authIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(authIntent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }
}