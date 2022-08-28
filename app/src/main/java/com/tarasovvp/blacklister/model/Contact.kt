package com.tarasovvp.blacklister.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.PLUS_CHAR
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class Contact(
    var id: String = "",
    var name: String? = "",
    var photoUrl: String? = "",
    @PrimaryKey var phone: String = "",
    var isBlackFilter: Boolean = false,
    var isWhiteFilter: Boolean = false,
) : Parcelable, BaseAdapter.MainData {
    var trimmedPhone = phone.filter { it.isDigit() || it == PLUS_CHAR }
    fun contactTypeIcon(): Int {
        return when {
            isBlackFilter -> R.drawable.ic_block
            isWhiteFilter -> R.drawable.ic_accepted
            else -> 0
        }
    }
}
