package com.vladrip.ifchat.mock.service

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.vladrip.ifchat.mock.Constants
import com.vladrip.ifchat.mock.dto.User

class MockUserService(c: Context) {
    private val gson = Gson()
    private var prefsUsers: SharedPreferences = c.getSharedPreferences(Constants.PREFS_USERS, 0)

    fun save(user: User): Boolean {
        if (prefsUsers.contains(user.phoneNum))
            return false

        prefsUsers.edit()
            .putString(user.phoneNum, gson.toJson(user))
            .apply()
        return true
    }

    fun containsPhoneNum(phoneNum: String): Boolean {
        return prefsUsers.contains(phoneNum)
    }

    fun findByPhoneNumAndPassword(phoneNum: String, password: String): User? {
        val user = gson.fromJson(prefsUsers.getString(phoneNum, null), User::class.java)
        return if (user.password == password) user else null
    }
}