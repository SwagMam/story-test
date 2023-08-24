package com.example.submission1aplikasistory.data.lokal

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.submission1aplikasistory.data.model.Stories

@Dao
interface StoriesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stories: Stories)

    @Query("DELETE FROM stories")
    suspend fun deleteAll(): Int

    @Query("SELECT * FROM stories")
    fun findAll(): PagingSource<Int, Stories>
}