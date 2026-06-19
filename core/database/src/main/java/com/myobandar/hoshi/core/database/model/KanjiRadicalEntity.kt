package com.myobandar.hoshi.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "kanji_radicals",
    primaryKeys = ["kanji", "radical"],
    foreignKeys = [
        ForeignKey(
            entity = KanjiEntity::class,
            parentColumns = ["character"],
            childColumns = ["kanji"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RadicalEntity::class,
            parentColumns = ["character"],
            childColumns = ["radical"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("kanji"),
        Index("radical")
    ]
)
data class KanjiRadicalEntity(
    @ColumnInfo(name = "kanji")
    val kanji: String,
    @ColumnInfo(name = "radical")
    val radical: String,
    @ColumnInfo(name = "relation_type")
    val relationType: String
)
