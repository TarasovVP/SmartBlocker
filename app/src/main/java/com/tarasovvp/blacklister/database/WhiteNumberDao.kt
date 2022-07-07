package com.tarasovvp.blacklister.database

import androidx.room.*
import com.tarasovvp.blacklister.model.WhiteNumber

@Dao
interface WhiteNumberDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllWhiteNumbers(whiteNumbers: ArrayList<WhiteNumber>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWhiteNumber(whiteNumber: WhiteNumber)

    @Query("SELECT * FROM whiteNumber")
    fun getAllWhiteNumbers(): List<WhiteNumber>

    @Query("SELECT * FROM whiteNumber WHERE number = :whiteNumber")
    fun getWhiteNumber(whiteNumber: String): WhiteNumber?

    @Query("SELECT * FROM whiteNumber WHERE (number = :whiteNumber) OR (:whiteNumber LIKE '%' || number || '%' AND contain = 1) OR (:whiteNumber LIKE number || '%' AND start = 1) OR (:whiteNumber LIKE '%' || number AND `end` = 1)")
    fun getWhiteNumberList(whiteNumber: String): List<WhiteNumber>

    @Delete
    fun delete(whiteNumber: WhiteNumber)

    @Query("DELETE FROM whiteNumber")
    fun deleteAllWhiteNumbers()
}