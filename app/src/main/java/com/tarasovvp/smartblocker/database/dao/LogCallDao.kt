package com.tarasovvp.smartblocker.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tarasovvp.smartblocker.models.LogCall

@Dao
interface LogCallDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllLogCalls(logCalls: List<LogCall>?)

    @Query("SELECT * FROM logCall")
    suspend fun allLogCalls(): List<LogCall>

    @Query("SELECT * FROM logCall WHERE type != '2'")
    suspend fun allNumbersNotFromContacts(): List<LogCall>

    @Query("SELECT * FROM logCall WHERE (number = :filter) OR (number LIKE :filter || '%' AND :type = 1) OR (number LIKE '%' || :filter || '%' AND :type = 2)")
    suspend fun queryCallList(
        filter: String,
        type: Int,
    ): List<LogCall>
}