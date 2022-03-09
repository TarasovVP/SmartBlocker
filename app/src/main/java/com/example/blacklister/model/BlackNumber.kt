package com.example.blacklister.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BlackNumber(@PrimaryKey val blackNumber: String)
