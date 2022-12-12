package com.tarasovvp.smartblocker.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tarasovvp.smartblocker.models.FilteredCall

@Dao
interface FilteredCallDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFilteredCalls(filteredCalls: ArrayList<FilteredCall>)

    @Insert
    fun insertFilteredCall(filteredCall: FilteredCall?)

    @Query("SELECT * FROM filteredCall")
    suspend fun allFilteredCalls(): List<FilteredCall>

    @Query("SELECT * FROM filteredCall WHERE number = :number")
    suspend fun filteredCallsByNumber(number: String): List<FilteredCall>

    @Query("SELECT * FROM filteredCall WHERE filter_filter = :filter")
    suspend fun filteredCallsByFilter(filter: String): List<FilteredCall>

    @Query("delete from filteredCall where callId  in (:callIdList)")
    fun deleteFilteredCalls(callIdList: List<Int>)

    @Query("delete from filteredCall")
    fun deleteAllFilteredCalls()
}