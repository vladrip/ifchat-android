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
import com.vladrip.ifchat.api.RequestRestorer
import com.vladrip.ifchat.mock.Constants.PREFS_SESSION
import com.vladrip.ifchat.mock.Constants.PREFS_SESSION_EMAIL
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    @Inject
    lateinit var requestRestorer: RequestRestorer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

//        if (getSharedPreferences(PREFS_SESSION, 0)
//                .getString(PREFS_SESSION_EMAIL, null).isNullOrBlank()
//        ) {
//            openLoginActivity()
//            return
//        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                requestRestorer.restoreRequests()
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
            getSharedPreferences(PREFS_SESSION, 0).edit()
                .putString(PREFS_SESSION_EMAIL, null)
                .apply()
            openLoginActivity()
            true
        }
    }

    private fun openLoginActivity() {
        val loginIntent = Intent(this, LoginActivity::class.java)
        loginIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(loginIntent)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }
}