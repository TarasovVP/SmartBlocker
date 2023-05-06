package com.tarasovvp.smartblocker.data.database.dao

import androidx.room.*
import com.tarasovvp.smartblocker.domain.entities.db_views.LogCallWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_entities.LogCall

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
    @Query("SELECT DISTINCT * FROM LogCallWithFilter WHERE type != '2' GROUP BY number")
    suspend fun allCallNumberWithFilter(): List<LogCallWithFilter>

    @RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query("SELECT DISTINCT * FROM LogCallWithFilter WHERE filter = :filter GROUP BY number")
    suspend fun queryCallList(filter: String): List<LogCallWithFilter>
}