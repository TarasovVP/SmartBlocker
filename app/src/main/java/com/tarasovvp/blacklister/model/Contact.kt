package com.tarasovvp.blacklister.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.BLACK_FILTER
import com.tarasovvp.blacklister.constants.Constants.DEFAULT_FILTER
import com.tarasovvp.blacklister.constants.Constants.WHITE_FILTER
import com.tarasovvp.blacklister.extensions.EMPTY
import com.tarasovvp.blacklister.extensions.trimmed
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import kotlinx.android.parcel.Parcelize
import java.util.*

@Entity
@Parcelize
data class Contact(
    var id: String = String.EMPTY,
    var name: String? = String.EMPTY,
    var photoUrl: String? = String.EMPTY,
    @PrimaryKey var phone: String = String.EMPTY,
    var filterType: Int = DEFAULT_FILTER
) : Parcelable, BaseAdapter.MainData {
    var trimmedPhone = phone.trimmed()
    fun contactTypeIcon(): Int {
        return when(filterType) {
            BLACK_FILTER -> R.drawable.ic_block
            WHITE_FILTER -> R.drawable.ic_accepted
            else -> 0
        }
    }
    fun isBlackFilter(): Boolean {
        return filterType == BLACK_FILTER
    }

    fun isWhiteFilter(): Boolean {
        return filterType == WHITE_FILTER
    }

    fun nameInitial(): String {
        return name?.split(Regex("\\W+"))?.take(2)
            ?.mapNotNull { it.firstOrNull() }
            ?.joinToString(String.EMPTY)?.uppercase(Locale.getDefault()).orEmpty()
    }
}
