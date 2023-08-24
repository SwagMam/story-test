package com.example.submission1aplikasistory.helper

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.submission1aplikasistory.ui.addstories.AddStoriesViewModel
import com.example.submission1aplikasistory.ui.auth.AuthViewModel
import com.example.submission1aplikasistory.ui.map.MapsViewModel

class ViewModelFactory(private val pref: UserPreferences): ViewModelProvider.NewInstanceFactory() {

    private lateinit var mApplication: Application

    fun setApplication(application: Application) {
        mApplication = application
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            AuthViewModel::class.java -> AuthViewModel(pref) as T
            AddStoriesViewModel::class.java -> AddStoriesViewModel(pref) as T
            MapsViewModel::class.java -> MapsViewModel(pref, mApplication) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}