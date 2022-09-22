package com.tarasovvp.blacklister.database

import androidx.room.*
import com.tarasovvp.blacklister.model.WhiteFilter

@Dao
interface WhiteFilterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllWhiteFilters(whiteFilters: ArrayList<WhiteFilter>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWhiteFilter(whiteFilter: WhiteFilter)

    @Query("SELECT * FROM whitefilter")
    fun allWhiteFilters(): List<WhiteFilter>

    @Query("SELECT * FROM whitefilter WHERE filter = :whiteFilter AND type = :type")
    fun getWhiteFilter(whiteFilter: String, type: Int): WhiteFilter?

    @Query("SELECT * FROM whitefilter WHERE (filter = :whiteFilter) OR (:whiteFilter LIKE filter || '%' AND type = 1) OR (:whiteFilter LIKE '%' || filter || '%' AND type = 2)")
    fun queryWhiteFilterList(whiteFilter: String): List<WhiteFilter>

    @Delete
    fun delete(whiteFilter: WhiteFilter)

    @Query("DELETE FROM whitefilter")
    fun deleteAllWhiteFilters()
}