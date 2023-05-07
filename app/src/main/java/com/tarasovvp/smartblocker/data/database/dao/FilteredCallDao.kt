package com.tarasovvp.smartblocker.data.database.dao

import androidx.room.*
import com.tarasovvp.smartblocker.domain.entities.db_entities.FilteredCall
import com.tarasovvp.smartblocker.domain.entities.db_views.CallWithFilter

@Dao
interface FilteredCallDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFilteredCalls(filteredCalls: List<FilteredCall>)

    @Insert
    fun insertFilteredCall(filteredCall: FilteredCall?)

    @Query("SELECT * FROM filtered_calls")
    suspend fun allFilteredCalls(): List<FilteredCall>

    @Query("SELECT * FROM callWithFilter WHERE isFilteredCall = 1")
    suspend fun allFilteredCallsWithFilter(): List<CallWithFilter>

    @RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query("SELECT * FROM callWithFilter WHERE isFilteredCall = 1 AND filter = :filter ORDER BY callDate DESC")
    suspend fun allFilteredCallsByFilter(filter: String): List<CallWithFilter>

    @RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query("SELECT * FROM callWithFilter WHERE isFilteredCall = 1 AND number = :number ORDER BY callDate DESC")
    suspend fun allFilteredCallsByNumber(number: String): List<CallWithFilter>

    @Query("delete from filtered_calls where callId in (:callIdList)")
    fun deleteFilteredCalls(callIdList: List<Int>)
}