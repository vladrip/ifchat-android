package com.vladrip.ifchat

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.vladrip.ifchat.Constants.PREFS_SESSION
import com.vladrip.ifchat.Constants.PREFS_SESSION_EMAIL
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

//        if (getSharedPreferences(PREFS_SESSION, 0)
//                .getString(PREFS_SESSION_EMAIL, null).isNullOrBlank()
//        ) {
//            openLoginActivity()
//            return
//        }

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