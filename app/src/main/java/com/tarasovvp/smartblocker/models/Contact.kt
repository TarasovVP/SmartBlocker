package com.tarasovvp.smartblocker.models

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcelable
import androidx.core.content.ContextCompat
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.extensions.*
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class Contact(
    @PrimaryKey var id: String = String.EMPTY,
    var name: String? = String.EMPTY,
    var photoUrl: String? = String.EMPTY,
    var number: String = String.EMPTY,
    var filter: Filter? = Filter(),
) : Parcelable, NumberData() {

    var trimmedPhone = number.digitsTrimmed()

    fun isBlackFilter(): Boolean {
        return filter?.filterType == BLOCKER
    }

    fun isNameEmpty(): Boolean {
        return name.isNullOrEmpty()
    }

    fun placeHolder(context: Context): Drawable? {
        return if (name.isNullOrEmpty()) ContextCompat.getDrawable(context, R.drawable.ic_contact_list) else  context.getInitialDrawable(name.nameInitial())
    }

    fun nationalNumber(country: String): String {
        return trimmedPhone.getPhoneNumber(country)?.nationalNumber.toString()
    }
}
