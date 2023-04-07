package com.vladrip.ifchat

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.vladrip.ifchat.Constants.PREFS_SESSION
import com.vladrip.ifchat.Constants.PREFS_SESSION_EMAIL
import com.vladrip.ifchat.mock.service.MockUserService

class LoginActivity : AppCompatActivity() {
    private lateinit var mockUserService: MockUserService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mockUserService = MockUserService(this)

        val loginSignUp = findViewById<TextView>(R.id.login_sign_up)
        loginSignUp.text =
            RegisterActivity.putSpanAfterQuestionMark(loginSignUp.text, getColor(R.color.teal_700))
    }

    fun onSubmit(v: View) {
        val phoneNum = findViewById<TextInputEditText>(R.id.login_phone)
        val password = findViewById<TextInputEditText>(R.id.login_password)

        if (!phoneNum.text?.matches(getString(R.string.regex_phone_number).toRegex())!!)
            phoneNum.error = "Phone number is invalid"
        else if (!mockUserService.containsPhoneNum(phoneNum.text.toString()))
            phoneNum.error = "User with such phone number does not exist"

        if (phoneNum.error != null) return

        val user = mockUserService.findByPhoneNumAndPassword(
            phoneNum.text.toString(),
            password.text.toString()
        )
        if (user == null) password.error = "Wrong password"
        else {
            val successfulLoginMsg = getString(R.string.login_successful, user.username)
            Toast.makeText(this, successfulLoginMsg, Toast.LENGTH_SHORT).show()
            getSharedPreferences(PREFS_SESSION, 0).edit()
                .putString(PREFS_SESSION_EMAIL, user.phoneNum)
                .apply()
            openMainActivity()
        }
    }

    fun openRegisterActivity(v: View) {
        val registerIntent = Intent(this, RegisterActivity::class.java)
        startActivity(registerIntent)
    }

    private fun openMainActivity() {
        val mainIntent = Intent(this, MainActivity::class.java)
        mainIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(mainIntent)
    }
}