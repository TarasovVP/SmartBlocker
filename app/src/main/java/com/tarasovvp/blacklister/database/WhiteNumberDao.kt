package com.tarasovvp.blacklister.database

import androidx.room.*
import com.tarasovvp.blacklister.model.WhiteNumber

@Dao
interface WhiteNumberDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllWhiteNumbers(whiteNumbers: ArrayList<WhiteNumber>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWhiteNumber(whiteNumbers: WhiteNumber)

    @Query("SELECT * FROM whiteNumber")
    fun getAllWhiteNumbers(): List<WhiteNumber>

    @Query("SELECT * FROM whiteNumber WHERE whiteNumber = :whiteNumber")
    fun getWhiteNumber(whiteNumber: String): WhiteNumber?

    @Query("SELECT * FROM whiteNumber WHERE (whiteNumber = :whiteNumber) OR (:whiteNumber LIKE '%' || whiteNumber || '%' AND isContain = 1) OR (:whiteNumber LIKE whiteNumber || '%' AND isStart = 1) OR (:whiteNumber LIKE '%' || whiteNumber AND isEnd = 1)")
    fun getWhiteNumberList(whiteNumber: String): List<WhiteNumber>

    @Delete
    fun delete(whiteNumber: WhiteNumber)

    @Query("DELETE FROM whiteNumber")
    fun deleteAllWhiteNumbers()
}