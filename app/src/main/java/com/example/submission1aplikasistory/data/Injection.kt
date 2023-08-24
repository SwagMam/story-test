package com.example.submission1aplikasistory.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.submission1aplikasistory.data.api.ApiConfig
import com.example.submission1aplikasistory.data.lokal.StoriesDatabase
import com.example.submission1aplikasistory.data.paging.StoriesRepository
import com.example.submission1aplikasistory.helper.UserPreferences


object Injection {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_key")
    fun provideRepository(context: Context): StoriesRepository {
        val database = StoriesDatabase.getDatabase(context)
        val apiService = ApiConfig.apiInstance
        val pref = UserPreferences.getInstance(context.dataStore)
        return StoriesRepository(database, apiService, pref)
    }
}