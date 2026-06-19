package com.myobandar.hoshi.core.model

data class GraphEdge(
    val from: String,
    val to: String,
    val type: EdgeType
)

enum class EdgeType {
    OfficialRadical,
    Component
}
