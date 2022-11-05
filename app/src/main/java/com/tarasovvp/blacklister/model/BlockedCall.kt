package com.tarasovvp.blacklister.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tarasovvp.blacklister.enums.FilterCondition
import com.tarasovvp.blacklister.extensions.EMPTY
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class BlockedCall(
    @PrimaryKey(autoGenerate = true) override var id: Int = 0,
    var blockFilter: String = String.EMPTY,
    var blockFilterFilterCondition: Int? = FilterCondition.FILTER_CONDITION_FULL.index,
) : Call(), Parcelable