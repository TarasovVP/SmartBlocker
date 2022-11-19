package com.tarasovvp.blacklister.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tarasovvp.blacklister.model.BlockedCall
import com.tarasovvp.blacklister.model.Filter

@Dao
interface BlockedCallDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllBlockedCalls(blockedCalls: ArrayList<BlockedCall>)

    @Insert
    fun insertBlockedCall(blockedCall: BlockedCall?)

    @Query("SELECT * FROM blockedCall")
    suspend fun allBlockedCalls(): List<BlockedCall>

    @Query("SELECT * FROM blockedCall WHERE number = :number")
    suspend fun blockedCallsByNumber(number: String): List<BlockedCall>

    @Query("SELECT * FROM blockedCall WHERE filter = :filter")
    suspend fun blockedCallsByFilter(filter: Filter): List<BlockedCall>

    @Query("delete from blockedCall where id in (:callIdList)")
    fun deleteBlockCalls(callIdList: List<Int>)

    @Query("delete from blockedCall")
    fun deleteAllBlockCalls()
}