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

    @Query("SELECT * FROM filters WHERE filterType = :filterType")
    suspend fun allFiltersByType(filterType: Int): List<Filter>

    @Query("SELECT * FROM filters WHERE filter = :filter AND conditionType = :conditionType")
    suspend fun getFilter(filter: String, conditionType: Int): Filter?

    @Transaction
    @Query("SELECT * FROM filters LEFT JOIN country_codes ON country_codes.country = filters.country WHERE filters.filter = :filter")
    fun getFilterWithCountryCode(filter: String): FilterWithCountryCode?

    @Query("SELECT * FROM filters WHERE (filter = :number AND conditionType = 0) OR (:number LIKE filter || '%' AND conditionType = 1) OR (:number LIKE '%' || filter || '%' AND conditionType = 2)")
    suspend fun queryFullMatchFilterList(number: String): List<Filter>

    @Delete
    fun delete(filter: Filter)

    @Query("DELETE FROM filters")
    suspend fun deleteAllFilters()
}