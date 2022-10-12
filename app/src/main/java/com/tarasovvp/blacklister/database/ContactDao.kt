package com.tarasovvp.blacklister.database

import androidx.room.*
import com.tarasovvp.blacklister.model.Contact

@Dao
interface ContactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllContacts(contacts: List<Contact>?)

    @Query("SELECT * FROM contact")
    suspend fun getAllContacts(): List<Contact>

    @Query("SELECT * FROM contact WHERE trimmedPhone = :phone")
    suspend fun getContactByPhone(phone: String): Contact?

    @Query("SELECT * FROM contact WHERE (trimmedPhone = :filter) OR (trimmedPhone LIKE :filter || '%' AND :type = 1) OR (trimmedPhone LIKE '%' || :filter || '%' AND :type = 2)")
    suspend fun queryContactList(
        filter: String,
        type: Int
    ): List<Contact>
}