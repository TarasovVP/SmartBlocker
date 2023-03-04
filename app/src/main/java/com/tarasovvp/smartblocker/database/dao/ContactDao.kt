package com.tarasovvp.smartblocker.database.dao

import androidx.room.*
import com.tarasovvp.smartblocker.models.Contact

@Dao
interface ContactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllContacts(contacts: List<Contact>?)

    @Query("SELECT * FROM contacts")
    suspend fun getAllContacts(): List<Contact>

    @Transaction
    @Query("SELECT * FROM contacts LEFT JOIN filters ON contacts.number LIKE '%' || filters.filter || '%' WHERE (filters.filter = contacts.number AND filters.conditionType = 0) OR (contacts.number LIKE filters.filter || '%' AND filters.conditionType = 1) OR (contacts.number LIKE '%' || filters.filter || '%' AND filters.conditionType = 2)")
    suspend fun getContactsWithFilters(): List<Contact>
}