package com.tarasovvp.smartblocker.database.dao

import androidx.room.*
import com.tarasovvp.smartblocker.models.Filter
import com.tarasovvp.smartblocker.models.FilterWithCountryCode

@Dao
interface FilterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFilters(filters: ArrayList<Filter>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFilter(filter: Filter)

    @Update
    fun updateFilter(filter: Filter)

    @Query("SELECT * FROM filters ORDER BY created DESC")
    suspend fun allFilters(): List<Filter>

    @Transaction
    @Query("SELECT * FROM FilterWithCountryCode ORDER BY created DESC")
    suspend fun allFilterWithCountryCode(): List<FilterWithCountryCode>

    @Transaction
    @Query("SELECT * FROM FilterWithCountryCode WHERE filterType = :filterType")
    suspend fun allFiltersByType(filterType: Int): List<FilterWithCountryCode>

    @Transaction
    @Query("SELECT * FROM filters WHERE filter = :filter")
    suspend fun getFilter(filter: String): FilterWithCountryCode?

    @Transaction
    @Query("SELECT * FROM FilterWithCountryCode WHERE (filter = :number AND conditionType = 0) OR (:number LIKE filter || '%' AND conditionType = 1) OR (:number LIKE '%' || filter || '%' AND conditionType = 2)")
    suspend fun queryFullMatchFilterList(number: String): List<FilterWithCountryCode>

    @Delete
    fun delete(filter: Filter)

    @Query("DELETE FROM filters")
    suspend fun deleteAllFilters()
}