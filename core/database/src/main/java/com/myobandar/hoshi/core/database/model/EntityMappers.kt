package com.myobandar.hoshi.core.database.model

import com.myobandar.hoshi.core.model.EdgeType
import com.myobandar.hoshi.core.model.GraphEdge
import com.myobandar.hoshi.core.model.Kanji
import com.myobandar.hoshi.core.model.Radical
import com.myobandar.hoshi.core.model.UserKanji

fun KanjiEntity.asExternalModel(): Kanji =
    Kanji(
        character = character,
        meanings = meanings,
        onyomi = onyomi,
        kunyomi = kunyomi,
        strokeCount = strokeCount,
        jlptLevel = jlptLevel
    )

fun RadicalEntity.asExternalModel(): Radical =
    Radical(
        character = character,
        meaning = meaning,
        strokeCount = strokeCount
    )

fun KanjiRadicalEntity.asExternalModel(): GraphEdge =
    GraphEdge(
        from = radical,
        to = kanji,
        type = when (relationType) {
            "official_radical" -> EdgeType.OfficialRadical
            else -> EdgeType.Component
        }
    )

fun UserKanjiEntity.asExternalModel(): UserKanji =
    UserKanji(
        kanji = kanji,
        discoveredAt = discoveredAt,
        lastSeenAt = lastSeenAt,
        discoveryCount = discoveryCount,
        masteryLevel = masteryLevel
    )
