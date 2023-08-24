package com.example.submission1aplikasistory

import com.example.submission1aplikasistory.data.model.Stories

object DataDummy {

    fun generateDummyStoryResponse(): List<Stories> {

        val items: MutableList<Stories> = arrayListOf()
        for (i in 0..100) {
            val story = Stories(
                id = "story-FvU4u0Vp2S3PMsFg",
                name = "Bagus",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                createdAt = "2022-01-08T06:34:18.598Z",
                description = "Lorem Ipsum",
                lon = -16.002,
                lat = -10.212
            )
            items.add(story)
        }
        return items
    }
}