package com.example.blacklister.database

import androidx.room.*
import com.example.blacklister.model.CallLog

@Dao
interface CallLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllCallLogs(callLogs: List<CallLog>?)

    @Update
    fun updateCallLogs(callLogs: List<CallLog>?)

    @Insert
    fun insertCallLog(callLogs: CallLog?)

    @Query("SELECT * FROM callLog")
    fun allCallLogs(): List<CallLog>

    @Query("SELECT * FROM callLog WHERE phone = :phone")
    fun getCallLogByPhone(phone: String): CallLog?

    @Update
    fun updateCallLog(callLog: CallLog)

    @Query("DELETE FROM callLog")
    fun deleteAllCallLogs()
}