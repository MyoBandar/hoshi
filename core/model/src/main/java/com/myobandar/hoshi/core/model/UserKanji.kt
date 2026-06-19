package com.myobandar.hoshi.core.model

data class UserKanji(
    val kanji: String,
    val discoveredAt: Long,
    val lastSeenAt: Long,
    val discoveryCount: Int,
    val masteryLevel: Int
)
