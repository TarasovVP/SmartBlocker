package com.tarasovvp.smartblocker.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import com.tarasovvp.smartblocker.domain.entities.dbentities.Contact
import com.tarasovvp.smartblocker.domain.entities.dbviews.ContactWithFilter

@Dao
interface ContactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllContacts(contacts: List<Contact>)

    @RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query("SELECT * FROM ContactWithFilter")
    suspend fun allContactsWithFilters(): List<ContactWithFilter>

    @RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query("SELECT * FROM ContactWithFilter WHERE filter = :filter")
    suspend fun allContactsWithFiltersByFilter(filter: String): List<ContactWithFilter>

    @Transaction
    @Query("SELECT * FROM ContactWithFilter WHERE digitsTrimmedNumber LIKE '%' || :filter || '%' ORDER BY digitsTrimmedNumber")
    suspend fun allContactsWithFiltersByCreateFilter(filter: String): List<ContactWithFilter>
}
