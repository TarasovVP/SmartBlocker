package com.example.blacklister.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.blacklister.model.CallLog

@Dao
interface CallLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllCallLogs(callLogs: List<CallLog>?)

    @Query("SELECT * FROM callLog")
    fun getAllCallLogs(): LiveData<List<CallLog>>

    @Query("SELECT * FROM callLog WHERE phone = :phone")
    fun getCallLogByPhone(phone: String): CallLog?

    @Update
    fun updateCallLog(callLog: CallLog)

    @Query("DELETE FROM callLog")
    fun deleteAllCallLogs()
}