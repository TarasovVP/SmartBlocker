package com.tarasovvp.smartblocker.database.dao

import androidx.room.*
import com.tarasovvp.smartblocker.models.LogCall
import com.tarasovvp.smartblocker.models.LogCallWithFilter

@Dao
interface LogCallDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllLogCalls(logCalls: List<LogCall>?)
    @Transaction
    @Query("SELECT * FROM LogCallWithFilter")
    suspend fun allLogCallWithFilter(): List<LogCallWithFilter>
    @Transaction
    @Query("SELECT * FROM LogCallWithFilter WHERE type != '2'")
    suspend fun allCallNumberWithFilter(): List<LogCallWithFilter>
    @Transaction
    @Query("SELECT * FROM LogCallWithFilter WHERE filter = :filter")
    suspend fun queryCallList(filter: String): List<LogCallWithFilter>
}