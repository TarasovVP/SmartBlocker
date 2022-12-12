package com.tarasovvp.smartblocker.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tarasovvp.smartblocker.models.Contact

@Dao
interface ContactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllContacts(contacts: List<Contact>?)

    @Query("SELECT * FROM contact")
    suspend fun getAllContacts(): List<Contact>
}