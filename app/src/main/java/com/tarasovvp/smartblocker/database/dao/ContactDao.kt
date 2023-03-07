package com.tarasovvp.smartblocker.database.dao

import androidx.room.*
import com.tarasovvp.smartblocker.models.Contact
import com.tarasovvp.smartblocker.models.ContactWithFilter

@Dao
interface ContactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllContacts(contacts: List<Contact>?)

    @Transaction
    @Query("SELECT * FROM ContactWithFilter")
    suspend fun getContactsWithFilters(): List<ContactWithFilter>
}