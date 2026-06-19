package com.myobandar.hoshi.feature.graph.domain

data class GraphFeatureContent(
    val headline: String,
    val subtitle: String,
    val sampleKanji: List<String>,
    val metrics: List<GraphMetric>,
    val cardTitle: String,
    val bullets: List<String>
)

data class GraphMetric(
    val value: String,
    val label: String
)

val GraphPlaceholderContent = GraphFeatureContent(
    headline = "My Kanji Graph",
    subtitle = "Map radicals, known kanji, and new connections.",
    sampleKanji = listOf("亻", "休", "体"),
    metrics = listOf(GraphMetric("124", "nodes"), GraphMetric("38", "known")),
    cardTitle = "Graph placeholder",
    bullets = listOf("Radical and kanji nodes", "Relationship preview", "Search-ready graph canvas")
)
