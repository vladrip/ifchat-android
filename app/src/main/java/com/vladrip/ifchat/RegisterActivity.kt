package com.vladrip.ifchat

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.vladrip.ifchat.Constants.PASSWORD_MIN_LENGTH
import com.vladrip.ifchat.Constants.USERNAME_MIN_LENGTH
import com.vladrip.ifchat.mock.dto.User
import com.vladrip.ifchat.mock.service.MockUserService

class RegisterActivity : AppCompatActivity() {
    companion object {
        fun putSpanAfterQuestionMark(text: CharSequence, color: Int): CharSequence {
            val qMarkSpan = SpannableStringBuilder(text)
            if (qMarkSpan.contains("?"))
                qMarkSpan.setSpan(
                    ForegroundColorSpan(color),
                    qMarkSpan.indexOf("?") + 1, qMarkSpan.length,
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                )
            return qMarkSpan
        }
    }

    private lateinit var mockUserService: MockUserService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        mockUserService = MockUserService(this)

        val registerSignIn = findViewById<TextView>(R.id.register_sign_in)
        registerSignIn.text =
            putSpanAfterQuestionMark(registerSignIn.text, getColor(R.color.teal_700))
    }

    fun onSubmit(v: View) {
        val phoneNum = findViewById<TextInputEditText>(R.id.register_phone)
        val username = findViewById<TextInputEditText>(R.id.register_username)
        val password = findViewById<TextInputEditText>(R.id.register_password)
        val passwordRepeat = findViewById<TextInputEditText>(R.id.register_password_repeat)

        if (!phoneNum.text?.matches(getString(R.string.regex_phone_number).toRegex())!!)
            phoneNum.error = "Email is invalid"
        if (username.length() < USERNAME_MIN_LENGTH)
            username.error = "Username is too short"
        if (password.text?.length!! < PASSWORD_MIN_LENGTH)
            password.error = "Password is too short"
        if (password.text.toString() != passwordRepeat.text.toString())
            passwordRepeat.error = "Passwords do not match"

        if (phoneNum.error != null || username.error != null
            || password.error != null || passwordRepeat.error != null
        )
            return

        val isUserSaved = mockUserService.save(
            User(-1, phoneNum.text.toString(), username.text.toString(), password.text.toString())
        )
        if (isUserSaved) openLoginActivity(v)
        else phoneNum.error = "User with the same email already exists"
    }

    fun openLoginActivity(v: View) {
        val loginIntent = Intent(this, LoginActivity::class.java)
        loginIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(loginIntent)
    }
}