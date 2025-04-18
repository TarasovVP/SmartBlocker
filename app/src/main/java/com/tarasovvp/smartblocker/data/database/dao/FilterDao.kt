package com.tarasovvp.smartblocker.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import androidx.room.Update
import com.tarasovvp.smartblocker.domain.entities.dbentities.Filter
import com.tarasovvp.smartblocker.domain.entities.dbviews.FilterWithFilteredNumber

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
    @Query("SELECT * FROM FilterWithFilteredNumber WHERE filterType = :filterType ORDER BY created DESC")
    suspend fun allFilterWithFilteredNumbersByType(filterType: Int): List<FilterWithFilteredNumber>

    @Transaction
    @Query("SELECT * FROM FilterWithFilteredNumber WHERE filter = :filter")
    suspend fun getFilter(filter: String): FilterWithFilteredNumber?

    @RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query(
        "SELECT * FROM FilterWithFilteredNumber " +
            "WHERE (filter = :number AND conditionType = 0) " +
            "OR (:number LIKE filter || '%' AND conditionType = 1) " +
            "OR (:number LIKE '%' || filter || '%' AND conditionType = 2) " +
            "ORDER BY LENGTH(filter) DESC, INSTR(:number, filter)",
    )
    suspend fun allFilterWithFilteredNumbersByNumber(number: String): List<FilterWithFilteredNumber>

    @Delete
    fun deleteFilters(filterList: List<Filter>)
}
