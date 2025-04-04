package com.tarasovvp.smartblocker.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import com.tarasovvp.smartblocker.domain.entities.dbentities.FilteredCall
import com.tarasovvp.smartblocker.domain.entities.dbviews.CallWithFilter

@Dao
interface FilteredCallDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFilteredCalls(filteredCalls: List<FilteredCall>)

    @Insert
    fun insertFilteredCall(filteredCall: FilteredCall)

    @Query("SELECT * FROM filtered_calls")
    suspend fun allFilteredCalls(): List<FilteredCall>

    @RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query("SELECT * FROM callWithFilter WHERE isFilteredCall = 1 AND filter = :filter ORDER BY callDate DESC")
    suspend fun allFilteredCallsByFilter(filter: String): List<CallWithFilter>

    @RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query("SELECT * FROM callWithFilter WHERE isFilteredCall = 1 AND number = :number AND callName = :name ORDER BY callDate DESC")
    suspend fun allFilteredCallsByNumber(
        number: String,
        name: String,
    ): List<CallWithFilter>

    @Query("delete from filtered_calls where callId in (:callIdList)")
    fun deleteFilteredCalls(callIdList: List<Int>)
}
