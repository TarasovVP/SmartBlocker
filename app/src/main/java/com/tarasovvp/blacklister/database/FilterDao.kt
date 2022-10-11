package com.tarasovvp.blacklister.database

import androidx.room.*
import com.tarasovvp.blacklister.model.Filter

@Dao
interface FilterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllFilters(filters: ArrayList<Filter>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFilter(filter: Filter)

    @Query("SELECT * FROM filter WHERE filterType = :filterType")
    fun allFilters(filterType: Int): List<Filter>

    @Query("SELECT * FROM filter WHERE filter = :filter AND conditionType = :type AND filterType = :filterType")
    fun getFilter(filter: String, type: Int, filterType: Int): Filter?

    @Query("SELECT * FROM filter WHERE (filter = :filter AND conditionType = 1) OR (:filter LIKE filter || '%' AND conditionType = 1) OR (:filter LIKE '%' || filter || '%' AND conditionType = 2)")
    fun queryFilterList(filter: String): List<Filter>

    @Delete
    fun delete(filter: Filter)

    @Query("DELETE FROM filter")
    fun deleteAllFilters()
}