package com.tarasovvp.smartblocker.data.database.dao

import androidx.room.*
import com.tarasovvp.smartblocker.domain.entities.db_entities.LogCall
import com.tarasovvp.smartblocker.domain.entities.db_views.CallWithFilter

@Dao
interface LogCallDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllLogCalls(logCalls: List<LogCall>?)

    @RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query("SELECT DISTINCT callId, * FROM callWithFilter WHERE isFilteredCall = 1 OR callId NOT IN (SELECT callId FROM callWithFilter WHERE isFilteredCall = 1)")
    suspend fun allCallWithFilters(): List<CallWithFilter>

    @RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query("SELECT DISTINCT * FROM callWithFilter WHERE type != '2' GROUP BY number")
    suspend fun allDistinctCallsWithFilter(): List<CallWithFilter>

    @RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query("SELECT DISTINCT * FROM callWithFilter WHERE filter = :filter GROUP BY number")
    suspend fun allCallWithFiltersByFilter(filter: String): List<CallWithFilter>
}