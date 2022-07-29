package com.tarasovvp.blacklister.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "logCall", indices = [Index(value = ["time"], unique = true)])
@Parcelize
data class LogCall(
    @PrimaryKey(autoGenerate = true) override var id: Int = 0,
) : Call(), Parcelable