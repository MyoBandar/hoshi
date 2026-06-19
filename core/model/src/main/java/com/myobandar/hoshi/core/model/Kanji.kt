package com.myobandar.hoshi.core.model

data class Kanji(
    val character: String,
    val meanings: List<String>,
    val onyomi: List<String>,
    val kunyomi: List<String>,
    val strokeCount: Int?,
    val jlptLevel: Int?
)
