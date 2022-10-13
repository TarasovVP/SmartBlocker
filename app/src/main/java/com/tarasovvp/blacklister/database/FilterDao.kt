package com.tarasovvp.blacklister.database

import androidx.room.*
import com.tarasovvp.blacklister.model.Filter

@Dao
interface FilterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFilters(filters: ArrayList<Filter>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFilter(filter: Filter)

    @Query("SELECT * FROM filter WHERE filterType = :filterType")
    suspend fun allFilters(filterType: Int): List<Filter>

    @Query("SELECT * FROM filter WHERE filter = :filter AND conditionType = :type")
    suspend fun getFilter(filter: String, type: Int): Filter?

    @Query("SELECT * FROM filter WHERE (filter = :filter AND conditionType = 1) OR (:filter LIKE filter || '%' AND conditionType = 1) OR (:filter LIKE '%' || filter || '%' AND conditionType = 2)")
    suspend fun queryFilterList(filter: String): List<Filter>

    @Delete
    fun delete(filter: Filter)

    @Query("DELETE FROM filter")
    suspend fun deleteAllFilters()
}