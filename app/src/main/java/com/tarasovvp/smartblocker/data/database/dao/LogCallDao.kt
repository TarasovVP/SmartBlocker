package com.tarasovvp.smartblocker.data.database.dao

import android.annotation.SuppressLint
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import com.tarasovvp.smartblocker.domain.entities.db_entities.LogCall
import com.tarasovvp.smartblocker.domain.entities.db_views.CallWithFilter

@Dao
interface LogCallDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllLogCalls(logCalls: List<LogCall>)

    @RewriteQueriesToDropUnusedColumns
    @SuppressLint("RoomWarnings.CURSOR_MISMATCH")
    @Transaction
    @Query(
        "SELECT DISTINCT callId, * FROM callWithFilter WHERE isFilteredCall = 1 OR callId NOT IN (SELECT callId FROM callWithFilter WHERE isFilteredCall = 1)",
    )
    suspend fun allCallWithFilters(): List<CallWithFilter>

    @RewriteQueriesToDropUnusedColumns
    @SuppressLint("RoomWarnings.CURSOR_MISMATCH")
    @Transaction
    @Query("SELECT DISTINCT * FROM callWithFilter WHERE filter = :filter AND type != '2' GROUP BY number")
    suspend fun allCallWithFiltersByFilter(filter: String): List<CallWithFilter>
}
