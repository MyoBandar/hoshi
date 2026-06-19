package com.myobandar.hoshi.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "radicals")
data class RadicalEntity(
    @PrimaryKey
    @ColumnInfo(name = "character")
    val character: String,
    @ColumnInfo(name = "meaning")
    val meaning: String?,
    @ColumnInfo(name = "stroke_count")
    val strokeCount: Int?
)
