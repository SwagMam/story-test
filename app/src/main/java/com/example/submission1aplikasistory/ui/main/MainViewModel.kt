package com.example.submission1aplikasistory.ui.main

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.submission1aplikasistory.data.model.Stories
import com.example.submission1aplikasistory.data.paging.StoriesRepository

class MainViewModel(private val repository: StoriesRepository): ViewModel() {

    fun getStories(): LiveData<PagingData<Stories>> =
        repository.getStories().cachedIn(viewModelScope)

}
