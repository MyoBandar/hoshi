package com.myobandar.hoshi.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kanji")
data class KanjiEntity(
    @PrimaryKey
    @ColumnInfo(name = "character")
    val character: String,
    @ColumnInfo(name = "meanings")
    val meanings: List<String>,
    @ColumnInfo(name = "onyomi")
    val onyomi: List<String>,
    @ColumnInfo(name = "kunyomi")
    val kunyomi: List<String>,
    @ColumnInfo(name = "stroke_count")
    val strokeCount: Int?,
    @ColumnInfo(name = "jlpt_level")
    val jlptLevel: Int?
)
