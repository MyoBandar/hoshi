package com.myobandar.hoshi.feature.constellation.domain

data class ConstellationFeatureContent(
    val headline: String,
    val subtitle: String,
    val sampleKanji: List<String>,
    val metrics: List<ConstellationMetric>,
    val cardTitle: String,
    val bullets: List<String>
)

data class ConstellationMetric(
    val value: String,
    val label: String
)

val ConstellationPlaceholderContent = ConstellationFeatureContent(
    headline = "My Kanji Constellation",
    subtitle = "Orbit radicals, known kanji, and new connections.",
    sampleKanji = listOf("亻", "休", "体"),
    metrics = listOf(ConstellationMetric("124", "nodes"), ConstellationMetric("38", "known")),
    cardTitle = "Constellation placeholder",
    bullets = listOf("Radical and kanji orbits", "Relationship preview", "Search-ready constellation canvas")
)
