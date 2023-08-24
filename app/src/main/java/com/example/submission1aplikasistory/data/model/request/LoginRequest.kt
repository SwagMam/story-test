package com.example.submission1aplikasistory.data.model.request

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email")
    val name: String?,

    @SerializedName("password")
    val password: String?
)
