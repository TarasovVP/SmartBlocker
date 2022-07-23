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
    fun allBlackNumbers(): List<BlackNumber>

    @Query("SELECT * FROM blackNumber WHERE number = :blackNumber")
    fun getBlackNumber(blackNumber: String): BlackNumber?

    @Query("SELECT * FROM blackNumber WHERE (number = :blackNumber) OR (:blackNumber LIKE '%' || number || '%' AND contain = 1) OR (:blackNumber LIKE number || '%' AND start = 1) OR (:blackNumber LIKE '%' || number AND `end` = 1)")
    fun queryBlackNumberList(blackNumber: String): List<BlackNumber>

    @Delete
    fun delete(blackNumber: BlackNumber)

    @Query("DELETE FROM blackNumber")
    fun deleteAllBlackNumbers()
}