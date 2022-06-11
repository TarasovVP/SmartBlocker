package com.tarasovvp.blacklister.database

import androidx.room.*
import com.tarasovvp.blacklister.model.BlackNumber

@Dao
interface BlackNumberDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllBlackNumbers(blackNumbers: ArrayList<BlackNumber>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBlackNumber(blackNumbers: BlackNumber)

    @Query("SELECT * FROM blackNumber")
    fun getAllBlackNumbers(): List<BlackNumber>

    @Query("SELECT * FROM blackNumber WHERE blackNumber = :blackNumber")
    fun getBlackNumber(blackNumber: String): BlackNumber?

    @Query("SELECT * FROM blackNumber WHERE (blackNumber = :blackNumber) OR (:blackNumber LIKE '%' || blackNumber || '%' AND isContain = 1) OR (:blackNumber LIKE blackNumber || '%' AND isStart = 1) OR (:blackNumber LIKE '%' || blackNumber AND isEnd = 1)")
    fun getBlackNumberList(blackNumber: String): List<BlackNumber>

    @Delete
    fun delete(blackNumber: BlackNumber)

    @Query("DELETE FROM blackNumber")
    fun deleteAllBlackNumbers()
}