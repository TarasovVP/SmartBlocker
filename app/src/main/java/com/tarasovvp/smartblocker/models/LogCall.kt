package com.tarasovvp.smartblocker.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "logCall", indices = [Index(value = ["callDate"], unique = true)])
@Parcelize
data class LogCall(
    @PrimaryKey override var callId: Int = 0
) : Call(), Parcelable