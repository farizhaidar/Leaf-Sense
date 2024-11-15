package com.bangkit.leafsense.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit.leafsense.data.UserPreference

class MainViewModelFactory(private val userPreference: UserPreference) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            MainViewModel(userPreference) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}