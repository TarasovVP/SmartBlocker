package com.tarasovvp.smartblocker.database.dao

import androidx.room.*
import com.tarasovvp.smartblocker.database.entities.Contact
import com.tarasovvp.smartblocker.database.database_views.ContactWithFilter

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