package com.tarasovvp.smartblocker.data.database.dao

import androidx.room.*
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithFilteredNumbers
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter

@Dao
interface FilterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFilters(filters: List<Filter>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFilter(filter: Filter)

    @Update
    fun updateFilter(filter: Filter)

    @Query("SELECT * FROM filters ORDER BY created DESC")
    suspend fun allFilters(): List<Filter>

    @RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query("SELECT * FROM FilterWithFilteredNumbers WHERE filterType = :filterType")
    suspend fun allFilterWithFilteredNumbersByType(filterType: Int): List<FilterWithFilteredNumbers>

    @Transaction
    @Query("SELECT * FROM filters WHERE filter = :filter")
    suspend fun getFilter(filter: String): FilterWithFilteredNumbers?

    @RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query("SELECT * FROM FilterWithFilteredNumbers WHERE (filter = :number AND conditionType = 0) OR (:number LIKE filter || '%' AND conditionType = 1) OR (:number LIKE '%' || filter || '%' AND conditionType = 2) ORDER BY LENGTH(filter) DESC, INSTR(:number, filter)")
    suspend fun allFilterWithFilteredNumbersByNumber(number: String): List<FilterWithFilteredNumbers>

    @Delete
    fun deleteFilters(filterList: List<Filter>)
}