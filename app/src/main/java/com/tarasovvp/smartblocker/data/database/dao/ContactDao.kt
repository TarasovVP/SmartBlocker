package com.tarasovvp.smartblocker.data.database.dao

import androidx.room.*
import com.tarasovvp.smartblocker.data.database.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.data.database.entities.Contact

@Dao
interface ContactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllContacts(contacts: List<Contact>?)

    @RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query("SELECT * FROM ContactWithFilter")
    suspend fun getContactsWithFilters(): List<ContactWithFilter>

    @RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query("SELECT * FROM ContactWithFilter WHERE filter = :filter")
    suspend fun getContactsWithFiltersByFilter(filter: String): List<ContactWithFilter>
}