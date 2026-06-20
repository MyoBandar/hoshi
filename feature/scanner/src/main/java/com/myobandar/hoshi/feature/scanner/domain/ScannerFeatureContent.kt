package com.myobandar.hoshi.feature.scanner.domain

data class ScannerFeatureContent(
    val headline: String,
    val subtitle: String,
    val sampleKanji: List<String>,
    val metrics: List<ScannerMetric>,
    val cardTitle: String,
    val bullets: List<String>
)

data class ScannerMetric(
    val value: String,
    val label: String
)

val ScannerPlaceholderContent = ScannerFeatureContent(
    headline = "Discover Kanji",
    subtitle = "Capture, extract, and save kanji from the world.",
    sampleKanji = listOf("非", "常", "口"),
    metrics = listOf(ScannerMetric("3", "found"), ScannerMetric("1", "saved")),
    cardTitle = "Discovery placeholder",
    bullets = listOf("Camera extraction entry point", "Detected kanji queue", "Save-to-constellation action")
)
