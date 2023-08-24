package com.example.submission1aplikasistory.ui.addstories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.submission1aplikasistory.data.Resource
import com.example.submission1aplikasistory.data.api.ApiConfig
import com.example.submission1aplikasistory.data.model.response.BaseResponse
import com.example.submission1aplikasistory.helper.UserPreferences
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddStoriesViewModel(private val preferences: UserPreferences): ViewModel() {

    private val _uploadInfo = MutableLiveData<Resource<String>>()
    val uploadInfo: LiveData<Resource<String>> = _uploadInfo

    suspend fun upload(
        imageMultipart: MultipartBody.Part,
        descrition: RequestBody,
        asGuest: Boolean = false,
        lat: RequestBody?,
        lon: RequestBody?,
        ) {
        _uploadInfo.postValue(Resource.Loading())
        val client = if (asGuest) ApiConfig.apiInstance.addGuestStories(
            imageMultipart,
            descrition,
        ) else ApiConfig.apiInstance.addStories(
            token = "Bearer ${preferences.getUserKey().first()}",
            imageMultipart,
            descrition,
            lat,
            lon,
        )

        client.enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.isSuccessful) {
                    _uploadInfo.postValue(Resource.Success(response.body()?.message))
                } else {
                    val errorResponse = Gson().fromJson(
                        response.errorBody()?.charStream(),
                        BaseResponse::class.java
                    )
                    _uploadInfo.postValue(Resource.Error(errorResponse.message))
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                _uploadInfo.postValue(Resource.Error(t.message))
            }
        })
    }
}