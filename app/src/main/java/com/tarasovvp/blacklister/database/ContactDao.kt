package com.tarasovvp.blacklister.database

import androidx.room.*
import com.tarasovvp.blacklister.model.Contact

@Dao
interface ContactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllContacts(contacts: List<Contact>?)

    @Query("SELECT * FROM contact")
    fun getAllContacts(): List<Contact>

    @Query("SELECT * FROM contact WHERE phone = :phone")
    fun getContactByPhone(phone: String): Contact?

    @Update
    fun updateContact(contact: Contact)

    @Query("DELETE FROM contact")
    fun deleteAllContacts()
}