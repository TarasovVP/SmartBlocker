package com.tarasovvp.smartblocker.models

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcelable
import androidx.core.content.ContextCompat
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tarasovvp.smartblocker.R
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

    fun isNameEmpty(): Boolean {
        return name.isNullOrEmpty()
    }

    fun placeHolder(context: Context): Drawable? {
        return if (name.nameInitial().isEmpty()) ContextCompat.getDrawable(context,
            R.drawable.ic_contact) else context.getInitialDrawable(name.nameInitial())
    }

    fun isFilterNullOrEmpty(): Boolean {
        return filter?.filter.isNullOrEmpty().isTrue()
    }
}
