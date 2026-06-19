package com.myobandar.hoshi.feature.collection.domain

data class CollectionFeatureContent(
    val headline: String,
    val subtitle: String,
    val sampleKanji: List<String>,
    val metrics: List<CollectionMetric>,
    val cardTitle: String,
    val bullets: List<String>
)

data class CollectionMetric(
    val value: String,
    val label: String
)

val CollectionPlaceholderContent = CollectionFeatureContent(
    headline = "Collection",
    subtitle = "Browse saved kanji, radicals, and source sightings.",
    sampleKanji = listOf("位", "住", "使"),
    metrics = listOf(CollectionMetric("86", "saved"), CollectionMetric("24", "radicals")),
    cardTitle = "Collection placeholder",
    bullets = listOf("Saved kanji library", "Radical index", "Source and sighting history")
)
