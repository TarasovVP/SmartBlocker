package com.tarasovvp.blacklister.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.tarasovvp.blacklister.model.BlockedCall

@Dao
interface BlockedCallDao {
    @Insert
    fun insertBlockedCall(blockedCall: BlockedCall?)

    @Query("SELECT * FROM blockedCall")
    fun allBlockedCalls(): List<BlockedCall>

    @Query("DELETE FROM blockedCall")
    fun deleteAllCallLogs()
}