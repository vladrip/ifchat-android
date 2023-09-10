package com.vladrip.ifchat.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.vladrip.ifchat.R
import com.vladrip.ifchat.ui.main.MainActivity

class AuthActivity : AppCompatActivity() {
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res -> onSignInResult(res) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        createSignInIntent()
    }

    private fun createSignInIntent() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.PhoneBuilder().build()
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setTheme(R.style.Theme_IFChat_Auth)
            .build()
        Log.d(TAG, signInIntent.toString())
        signInLauncher.launch(signInIntent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            startMainActivity()
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            Log.e(TAG, response?.error?.message ?: "cancelled or null")
        }
    }

    private fun startMainActivity() {
        val mainIntent = Intent(this, MainActivity::class.java)
            .setAction(Intent.ACTION_VIEW)
        mainIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(mainIntent)
    }

    companion object {
        private const val TAG = "PhoneAuthActivity"
    }
}