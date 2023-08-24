package com.example.submission1aplikasistory.data.api

import com.example.submission1aplikasistory.data.model.request.LoginRequest
import com.example.submission1aplikasistory.data.model.request.RegisterRequest
import com.example.submission1aplikasistory.data.model.response.BaseResponse
import com.example.submission1aplikasistory.data.model.response.LoginResponse
import com.example.submission1aplikasistory.data.model.response.StoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("register")
    fun register(@Body request: RegisterRequest): Call<BaseResponse>

    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("location") location: Int? = null
    ): StoriesResponse

    @GET("stories?location=1")
    fun getStoriesLocation(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
    ): Call<StoriesResponse>

    @Multipart
    @POST("stories")
    fun addStories(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?,
    ): Call<BaseResponse>

    @Multipart
    @POST("stories/guest")
    fun addGuestStories(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<BaseResponse>
}