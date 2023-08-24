package com.example.submission1aplikasistory.data.model.response

import com.example.submission1aplikasistory.data.model.Login
import com.google.gson.annotations.SerializedName

data class LoginResponse(
	@field:SerializedName("loginResult")
	val loginResult: Login?,

	@field:SerializedName("error")
	val error: Boolean?,

	@field:SerializedName("message")
	val message: String?
)
