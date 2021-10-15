package com.maveri.figma.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import javax.inject.Inject

@Entity(tableName = "user_table")
data class User @Inject constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var firebaseId: String,
)