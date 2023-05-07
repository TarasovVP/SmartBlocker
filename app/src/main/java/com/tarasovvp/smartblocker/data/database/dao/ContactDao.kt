package com.tarasovvp.smartblocker.data.database.dao

import androidx.room.*
import com.tarasovvp.smartblocker.domain.entities.db_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_entities.Contact

@Dao
interface ContactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllContacts(contacts: List<Contact>?)

    @RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query("SELECT * FROM ContactWithFilter")
    suspend fun allContactsWithFilters(): List<ContactWithFilter>

    @RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query("SELECT * FROM ContactWithFilter WHERE filter = :filter")
    suspend fun allContactsWithFiltersByFilter(filter: String): List<ContactWithFilter>
}