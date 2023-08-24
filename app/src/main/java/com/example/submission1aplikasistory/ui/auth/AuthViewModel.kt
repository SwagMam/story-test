package com.example.submission1aplikasistory.ui.auth


import androidx.lifecycle.*
import com.example.submission1aplikasistory.data.Resource
import com.example.submission1aplikasistory.data.api.ApiConfig
import com.example.submission1aplikasistory.data.model.request.LoginRequest
import com.example.submission1aplikasistory.data.model.request.RegisterRequest
import com.example.submission1aplikasistory.data.model.response.BaseResponse
import com.example.submission1aplikasistory.data.model.response.LoginResponse
import com.example.submission1aplikasistory.helper.UserPreferences
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel(private val preferences: UserPreferences): ViewModel() {

    private val _authInfo = MutableLiveData<Resource<String>>()
    val authInfo: LiveData<Resource<String>> = _authInfo

    fun login(email: String, password: String) {
        _authInfo.postValue(Resource.Loading())
        val client = ApiConfig.apiInstance.login(LoginRequest(email, password))

        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResult = response.body()?.loginResult?.token

                    loginResult?.let { saveUserKey(it) }
                    _authInfo.postValue(Resource.Success(loginResult))
                } else {
                    val errorResponse = Gson().fromJson(
                        response.errorBody()?.charStream(),
                        BaseResponse::class.java
                    )
                    _authInfo.postValue(Resource.Error(errorResponse.message))
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _authInfo.postValue(Resource.Error(t.message))
            }
        })
    }

    fun register(name: String, email: String, password: String) {
        _authInfo.postValue(Resource.Loading())
        val client = ApiConfig.apiInstance.register(RegisterRequest(name, email, password))

        client.enqueue(object : Callback<BaseResponse> {
            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.isSuccessful) {
                    val message = response.body()?.message.toString()
                    _authInfo.postValue(Resource.Success(message))
                } else {
                    val errorResponse = Gson().fromJson(
                        response.errorBody()?.charStream(),
                        BaseResponse::class.java
                    )
                    _authInfo.postValue(Resource.Error(errorResponse.message))
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                _authInfo.postValue(Resource.Error(t.message))
            }
        })
    }

    fun logout() = deleteUserKey()

    fun getUserKey() = preferences.getUserKey().asLiveData()

    private fun saveUserKey(key: String) {
        viewModelScope.launch {
            preferences.saveUserKey(key)
        }
    }

    private fun deleteUserKey() {
        viewModelScope.launch {
            preferences.deleteUserKey()
        }
    }
}