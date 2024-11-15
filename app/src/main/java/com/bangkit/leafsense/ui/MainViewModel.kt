package com.bangkit.leafsense.ui

import androidx.lifecycle.ViewModel
import com.bangkit.leafsense.data.UserPreference

class MainViewModel (private val userPreference: UserPreference) : ViewModel(){

    fun logout() {
        userPreference.setLoginStatus(false)
        userPreference.clear()
    }

}