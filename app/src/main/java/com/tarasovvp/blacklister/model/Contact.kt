package com.tarasovvp.blacklister.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.BLACK_FILTER
import com.tarasovvp.blacklister.constants.Constants.DEFAULT_FILTER
import com.tarasovvp.blacklister.constants.Constants.WHITE_FILTER
import com.tarasovvp.blacklister.extensions.EMPTY
import com.tarasovvp.blacklister.extensions.digitsTrimmed
import com.tarasovvp.blacklister.extensions.nameInitial
import com.tarasovvp.blacklister.ui.number_data.NumberData
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class Contact(
    @PrimaryKey var id: String = String.EMPTY,
    var name: String? = String.EMPTY,
    var photoUrl: String? = String.EMPTY,
    var number: String = String.EMPTY,
    var filterType: Int = DEFAULT_FILTER,
) : Parcelable, NumberData() {
    var trimmedPhone = number.digitsTrimmed()
    var searchText = String.EMPTY

    fun filterTypeIcon(): Int {
        return when (filterType) {
            BLACK_FILTER -> R.drawable.ic_black_filter
            WHITE_FILTER -> R.drawable.ic_white_filter
            else -> 0
        }
    }

    fun avatar(): Int {
        return R.drawable.ic_avatar
    }

    fun isBlackFilter(): Boolean {
        return filterType == BLACK_FILTER
    }

    fun isNameEmpty(): Boolean {
        return name.isNullOrEmpty()
    }

    fun nameInitial(): String {
        return if (name.isNullOrEmpty()) String(Character.toChars(128222)) else name.nameInitial()
    }
}
