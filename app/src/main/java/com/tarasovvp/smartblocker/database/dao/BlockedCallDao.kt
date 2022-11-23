package com.tarasovvp.smartblocker.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tarasovvp.smartblocker.model.FilteredCall
import com.tarasovvp.smartblocker.model.Filter

@Dao
interface BlockedCallDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllBlockedCalls(blockedCalls: ArrayList<FilteredCall>)

    @Insert
    fun insertBlockedCall(filteredCall: FilteredCall?)

    @Query("SELECT * FROM blockedCall")
    suspend fun allBlockedCalls(): List<FilteredCall>

    @Query("SELECT * FROM blockedCall WHERE number = :number")
    suspend fun blockedCallsByNumber(number: String): List<FilteredCall>

    @Query("SELECT * FROM blockedCall WHERE filter = :filter")
    suspend fun blockedCallsByFilter(filter: Filter): List<FilteredCall>

    @Query("delete from blockedCall where id in (:callIdList)")
    fun deleteBlockCalls(callIdList: List<Int>)

    @Query("delete from blockedCall")
    fun deleteAllBlockCalls()
}