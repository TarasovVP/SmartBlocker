package com.tarasovvp.smartblocker.database.dao

import androidx.room.*
import com.tarasovvp.smartblocker.models.FilteredCall
import com.tarasovvp.smartblocker.models.FilteredCallWithFilter

@Dao
interface FilteredCallDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFilteredCalls(filteredCalls: ArrayList<FilteredCall>)

    @Insert
    fun insertFilteredCall(filteredCall: FilteredCall?)

    @Query("SELECT * FROM filtered_calls")
    suspend fun allFilteredCalls(): List<FilteredCall>
    @Transaction
    @Query("SELECT * FROM FilteredCallWithFilter")
    suspend fun allFilteredCallWithFilter(): List<FilteredCallWithFilter>
    @Transaction
    @Query("SELECT * FROM FilteredCallWithFilter WHERE filter = :filter ORDER BY callDate DESC")
    suspend fun filteredCallsByFilter(filter: String): List<FilteredCallWithFilter>
    @Transaction
    @Query("SELECT * FROM FilteredCallWithFilter WHERE number = :number ORDER BY callDate DESC")
    suspend fun filteredCallsByNumber(number: String): List<FilteredCallWithFilter>

    @Query("delete from filtered_calls where callId  in (:callIdList)")
    fun deleteFilteredCalls(callIdList: List<Int>)

    @Query("delete from filtered_calls")
    fun deleteAllFilteredCalls()
}