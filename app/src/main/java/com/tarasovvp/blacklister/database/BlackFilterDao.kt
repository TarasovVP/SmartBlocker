package com.tarasovvp.blacklister.database

import androidx.room.*
import com.tarasovvp.blacklister.model.BlackFilter

@Dao
interface BlackFilterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllBlackFilters(blackFilters: ArrayList<BlackFilter>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBlackFilter(blackFilters: BlackFilter)

    @Query("SELECT * FROM blackfilter")
    fun allBlackFilters(): List<BlackFilter>

    @Query("SELECT * FROM blackfilter WHERE filter = :blackFilter AND type = :type")
    fun getBlackFilter(blackFilter: String, type: Int): BlackFilter?

    @Query("SELECT * FROM blackfilter WHERE (filter = :blackFilter) OR (:blackFilter LIKE filter || '%' AND type = 1) OR (:blackFilter LIKE '%' || filter || '%' AND type = 2)")
    fun queryBlackFilterList(blackFilter: String): List<BlackFilter>

    @Delete
    fun delete(blackFilter: BlackFilter)

    @Query("DELETE FROM blackfilter")
    fun deleteAllBlackFilters()
}