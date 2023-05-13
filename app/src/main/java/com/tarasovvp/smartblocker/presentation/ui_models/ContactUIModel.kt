package com.tarasovvp.smartblocker.presentation.ui_models

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcelable
import androidx.core.content.ContextCompat
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.utils.extensions.*
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContactUIModel(
    var id: String = String.EMPTY,
    var name: String = String.EMPTY,
    var photoUrl: String = String.EMPTY,
    var number: String = String.EMPTY,
    var phoneNumberValue: String = String.EMPTY,
    var isPhoneNumberValid: Boolean? = false
) : Parcelable, NumberDataUIModel() {

    @IgnoredOnParcel
    var trimmedPhone = number.digitsTrimmed()

    fun placeHolder(context: Context): Drawable? {
        return if (name.nameInitial().isEmpty()) ContextCompat.getDrawable(context,
            R.drawable.ic_contact) else context.getInitialDrawable(name.nameInitial())
    }
}
