package com.myobandar.hoshi.feature.review.domain

data class ReviewFeatureContent(
    val headline: String,
    val subtitle: String,
    val sampleKanji: List<String>,
    val metrics: List<ReviewMetric>,
    val cardTitle: String,
    val bullets: List<String>
)

data class ReviewMetric(
    val value: String,
    val label: String
)

val ReviewPlaceholderContent = ReviewFeatureContent(
    headline = "Review Queue",
    subtitle = "Practice the words and kanji you have already met.",
    sampleKanji = listOf("休", "作", "何"),
    metrics = listOf(ReviewMetric("12", "due"), ReviewMetric("7", "seen")),
    cardTitle = "Review placeholder",
    bullets = listOf("Due cards overview", "Recent mistakes", "Lightweight progress snapshot")
)
