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
    @Query("SELECT * FROM callWithFilter")
    suspend fun allCallWithFilter(): List<CallWithFilter>

    @RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query("SELECT DISTINCT * FROM callWithFilter WHERE type != '2' GROUP BY number")
    suspend fun allDistinctCallWithFilter(): List<CallWithFilter>

    @RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query("SELECT DISTINCT * FROM callWithFilter WHERE filter = :filter GROUP BY number")
    suspend fun allCallWithFilterByFilter(filter: String): List<CallWithFilter>
}