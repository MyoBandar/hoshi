package com.myobandar.hoshi.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_kanji",
    foreignKeys = [
        ForeignKey(
            entity = KanjiEntity::class,
            parentColumns = ["character"],
            childColumns = ["kanji"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("kanji")]
)
data class UserKanjiEntity(
    @PrimaryKey
    @ColumnInfo(name = "kanji")
    val kanji: String,
    @ColumnInfo(name = "discovered_at")
    val discoveredAt: Long,
    @ColumnInfo(name = "last_seen_at")
    val lastSeenAt: Long,
    @ColumnInfo(name = "discovery_count")
    val discoveryCount: Int,
    @ColumnInfo(name = "mastery_level")
    val masteryLevel: Int
)
