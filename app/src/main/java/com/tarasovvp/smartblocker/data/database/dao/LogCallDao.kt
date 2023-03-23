package com.tarasovvp.smartblocker.data.database.dao

import androidx.room.*
import com.tarasovvp.smartblocker.data.database.database_views.LogCallWithFilter
import com.tarasovvp.smartblocker.data.database.entities.LogCall

@Dao
interface LogCallDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllLogCalls(logCalls: List<LogCall>?)

    @RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query("SELECT * FROM LogCallWithFilter")
    suspend fun allLogCallWithFilter(): List<LogCallWithFilter>

    @RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query("SELECT * FROM LogCallWithFilter WHERE type != '2'")
    suspend fun allCallNumberWithFilter(): List<LogCallWithFilter>

    @RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query("SELECT * FROM LogCallWithFilter WHERE filter = :filter")
    suspend fun queryCallList(filter: String): List<LogCallWithFilter>
}