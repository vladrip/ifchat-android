package com.vladrip.ifchat

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
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
import com.vladrip.ifchat.api.RequestRestorer
import com.vladrip.ifchat.data.MessagingRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    @Inject lateinit var requestRestorer: RequestRestorer
    @Inject lateinit var messagingRepository: MessagingRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Firebase.auth.currentUser == null) {
            startAuthActivity()
            return
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                requestRestorer.restoreRequests()
                if (getSharedPreferences(IFChat.PREFS_FIREBASE, 0)
                        .getString(IFChat.PREFS_FIREBASE_DEVICE_TOKEN, null).isNullOrBlank())
                    messagingRepository.saveDeviceToken(Firebase.messaging.token.await())
            }
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_container) as NavHostFragment
        navController = navHostFragment.navController
        val drawer = findViewById<NavigationView>(R.id.nav_drawer)
        drawer.setupWithNavController(navController)
        appBarConfiguration =
            AppBarConfiguration(navController.graph, drawer.parent as DrawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        setCustomMenuListeners(drawer.menu)
    }

    private fun setCustomMenuListeners(menu: Menu) {
        menu.findItem(R.id.logout).setOnMenuItemClickListener {
            lifecycleScope.launch { messagingRepository.deleteCurrentDeviceToken() }
            Firebase.auth.signOut()
            startAuthActivity()
            return@setOnMenuItemClickListener true
        }
    }

    private fun startAuthActivity() {
        val authIntent = Intent(this, AuthActivity::class.java)
        authIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(authIntent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }
}