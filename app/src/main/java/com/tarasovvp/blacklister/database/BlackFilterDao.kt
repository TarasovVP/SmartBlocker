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

    @Query("SELECT * FROM blackfilter WHERE filter = :blackFilter")
    fun getBlackFilter(blackFilter: String): BlackFilter?

    @Query("SELECT * FROM blackfilter WHERE (filter = :blackFilter) OR (:blackFilter LIKE '%' || filter || '%' AND contain = 1) OR (:blackFilter LIKE filter || '%' AND start = 1) OR (:blackFilter LIKE '%' || filter AND `end` = 1)")
    fun queryBlackFilterList(blackFilter: String): List<BlackFilter>

    @Delete
    fun delete(blackFilter: BlackFilter)

    @Query("DELETE FROM blackfilter")
    fun deleteAllBlackFilters()
}