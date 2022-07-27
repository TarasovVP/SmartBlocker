package com.tarasovvp.blacklister.database

import androidx.room.*
import com.tarasovvp.blacklister.model.Contact

@Dao
interface ContactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllContacts(contacts: List<Contact>?)

    @Query("SELECT * FROM contact")
    fun getAllContacts(): List<Contact>

    @Query("SELECT * FROM contact WHERE `replace` (phone, ' ', '') = :phone")
    fun getContactByPhone(phone: String): Contact?

    @Query("SELECT * FROM contact WHERE (`replace` (phone, ' ', '') = :number) OR (`replace` (phone, ' ', '') LIKE '%' || :number || '%' AND :contain) OR (`replace` (phone, ' ', '') LIKE :number || '%' AND :start) OR (`replace` (phone, ' ', '') LIKE '%' || :number AND :end)")
    fun queryContactList(number: String, contain: Boolean, start: Boolean, end: Boolean): List<Contact>

    @Update
    fun updateContact(contact: Contact)

    @Query("DELETE FROM contact")
    fun deleteAllContacts()
}