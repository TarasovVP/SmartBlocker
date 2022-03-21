package com.example.blacklister.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.blacklister.ui.base.BaseAdapter
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class BlackNumber(@PrimaryKey val blackNumber: String) : Parcelable, BaseAdapter.MainData
