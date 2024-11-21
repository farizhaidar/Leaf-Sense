package com.bangkit.leafsense.data

import android.content.Context
import android.content.SharedPreferences

class UserPreference(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

    fun setLoginStatus(isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()
    }

    fun clear() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    companion object {
        private const val PREFS_NAME = "user_prefs"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"

        @Volatile
        private var INSTANCE: UserPreference? = null

        fun getInstance(context: Context): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(context)
                INSTANCE = instance
                instance
            }
        }
    }

}
