package com.example.submission1aplikasistory.data.paging

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.example.submission1aplikasistory.data.api.ApiService
import com.example.submission1aplikasistory.data.lokal.StoriesDatabase
import com.example.submission1aplikasistory.data.model.Stories
import com.example.submission1aplikasistory.helper.UserPreferences


class StoriesRepository(
    private val storiesDatabase: StoriesDatabase,
    private val apiService: ApiService,
    private val token: UserPreferences
) {
    fun getStories(): LiveData<PagingData<Stories>> {

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoriesRemoteMediator(storiesDatabase, apiService, token),
            pagingSourceFactory = {
                storiesDatabase.storiesDao().findAll()
            }
        ).liveData
    }
}