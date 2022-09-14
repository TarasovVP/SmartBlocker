package com.tarasovvp.blacklister.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.tarasovvp.blacklister.model.BlockedCall
import com.tarasovvp.blacklister.model.Contact

@Dao
interface BlockedCallDao {
    @Insert
    fun insertBlockedCall(blockedCall: BlockedCall?)

    @Query("SELECT * FROM blockedCall")
    fun allBlockedCalls(): List<BlockedCall>

    @Query("SELECT * FROM blockedCall WHERE number = :number")
    fun blockedCallsByPhone(number: String): List<BlockedCall>

    @Query("delete from blockedcall where id in (:callIdList)")
    fun deleteBlockCalls(callIdList: List<Int>)
}