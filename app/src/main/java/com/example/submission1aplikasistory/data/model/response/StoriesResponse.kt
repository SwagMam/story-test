package com.example.submission1aplikasistory.data.model.response

import com.example.submission1aplikasistory.data.model.Stories
import com.google.gson.annotations.SerializedName

data class StoriesResponse(

	@field:SerializedName("listStory")
	val listStory: List<Stories>,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)
