package com.tarasovvp.blacklister.database

import androidx.room.*
import com.tarasovvp.blacklister.model.Contact

@Dao
interface ContactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllContacts(contacts: List<Contact>?)

    @Query("SELECT * FROM contact")
    fun getAllContacts(): List<Contact>

    @Query("SELECT * FROM contact WHERE trimmedPhone = :phone")
    fun getContactByPhone(phone: String): Contact?

    @Query("SELECT * FROM contact WHERE (trimmedPhone = :filter) OR (trimmedPhone LIKE :filter || '%' AND :type = 1) OR (trimmedPhone LIKE '%' || :filter || '%' AND :type = 2)")
    fun queryContactList(
        filter: String,
        type: Int
    ): List<Contact>

    @Update
    fun updateContact(contact: Contact)

    @Query("DELETE FROM contact")
    fun deleteAllContacts()
}