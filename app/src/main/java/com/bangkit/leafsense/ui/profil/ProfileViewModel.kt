package com.bangkit.leafsense.ui.profil

import androidx.lifecycle.ViewModel
import com.bangkit.leafsense.data.UserPreference

class ProfileViewModel(private val userPreference: UserPreference) : ViewModel() {

    fun logout() {
        userPreference.setLoginStatus(false)
        userPreference.clear()
    }
}
