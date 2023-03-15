package com.tarasovvp.smartblocker.database.dao

import androidx.room.*
import com.tarasovvp.smartblocker.database.entities.LogCall
import com.tarasovvp.smartblocker.database.database_views.LogCallWithFilter

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