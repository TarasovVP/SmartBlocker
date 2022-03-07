package com.example.blacklister.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.blacklister.model.Contact

@Dao
interface ContactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllDeposits(contacts: List<Contact>?)

    @Query("SELECT * FROM contact WHERE id = :id")
    fun getDepositById(id: String): LiveData<Contact?>

    @Update
    fun updateDeposit(contact: Contact)

    @Query("DELETE FROM contact")
    fun deleteAllDeposits()
}