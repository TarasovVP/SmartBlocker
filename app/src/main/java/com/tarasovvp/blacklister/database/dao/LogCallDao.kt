package com.tarasovvp.blacklister.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tarasovvp.blacklister.model.LogCall

@Dao
interface LogCallDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllLogCalls(logCalls: List<LogCall>?)

    @Query("SELECT * FROM logCall")
    suspend fun allLogCalls(): List<LogCall>
}