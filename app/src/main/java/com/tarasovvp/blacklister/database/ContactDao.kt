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

    @Query("SELECT * FROM contact WHERE (trimmedPhone = :filter) OR (trimmedPhone LIKE '%' || :filter || '%' AND :contain) OR (trimmedPhone LIKE :filter || '%' AND :start) OR (trimmedPhone LIKE '%' || :filter AND :end)")
    fun queryContactList(
        filter: String,
        contain: Boolean,
        start: Boolean,
        end: Boolean,
    ): List<Contact>

    @Update
    fun updateContact(contact: Contact)

    @Query("DELETE FROM contact")
    fun deleteAllContacts()
}