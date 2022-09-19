package com.tarasovvp.blacklister.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.extensions.trimmed
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
    var trimmedPhone = phone.trimmed()
    fun contactTypeIcon(): Int {
        return when {
            isBlackFilter -> R.drawable.ic_block
            isWhiteFilter -> R.drawable.ic_accepted
            else -> 0
        }
    }
}
