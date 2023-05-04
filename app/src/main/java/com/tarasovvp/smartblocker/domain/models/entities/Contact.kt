package com.tarasovvp.smartblocker.domain.models.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.CONTACTS
import com.tarasovvp.smartblocker.utils.extensions.*
import kotlinx.parcelize.Parcelize

@Entity(tableName = CONTACTS)
@Parcelize
data class Contact(
    @PrimaryKey var id: String = String.EMPTY,
    var name: String? = String.EMPTY,
    var photoUrl: String? = String.EMPTY,
    var number: String = String.EMPTY,
    var filter: String = String.EMPTY
) : Parcelable