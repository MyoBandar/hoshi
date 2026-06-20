package com.myobandar.hoshi.feature.graph.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.myobandar.hoshi.core.designsystem.theme.HoshiCyan
import com.myobandar.hoshi.core.designsystem.theme.HoshiGreen
import com.myobandar.hoshi.core.designsystem.theme.HoshiPurple
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun GraphScreen(
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    KanjiGraphCanvas(modifier = modifier)
}

@Composable
private fun KanjiGraphCanvas(
    modifier: Modifier = Modifier
) {
    val graph = rememberKanjiRadicalGraph()
    val density = LocalDensity.current
    var scale by remember { mutableFloatStateOf(1f) }
    var pan by remember { mutableStateOf(Offset.Zero) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var selectedRadicalId by remember { mutableStateOf<String?>(null) }

    val colorScheme = MaterialTheme.colorScheme
    val center = Offset(canvasSize.width / 2f, canvasSize.height / 2f)
    val focusScale by animateFloatAsState(
        targetValue = graph.focusScaleFor(
            selectedRadicalId = selectedRadicalId,
            canvasSize = canvasSize
        ),
        animationSpec = tween(durationMillis = 320),
        label = "graphFocusScale"
    )
    val focusedPan by animateOffsetAsState(
        targetValue = pan,
        animationSpec = tween(durationMillis = 320),
        label = "graphPan"
    )
    val renderedScale = scale * focusScale

    val visibleNodeIds = remember(graph, selectedRadicalId) {
        graph.visibleNodeIds(selectedRadicalId = selectedRadicalId)
    }
    val visibleEdges = remember(graph, visibleNodeIds, selectedRadicalId) {
        graph.visibleEdges(
            visibleNodeIds = visibleNodeIds,
            selectedRadicalId = selectedRadicalId
        )
    }
    val visibleNodes = remember(graph, visibleNodeIds) {
        graph.nodes.filter { it.id in visibleNodeIds }
    }
    val visiblePositions = remember(graph, visibleNodes, selectedRadicalId) {
        graph.positionsFor(
            nodes = visibleNodes,
            selectedRadicalId = selectedRadicalId
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .onSizeChanged { canvasSize = it }
            .pointerInput(Unit) {
                detectTransformGestures { centroid, gesturePan, gestureZoom, _ ->
                    val nextScale = (scale * gestureZoom).coerceIn(0.42f, 3.2f)
                    val zoomRatio = nextScale / scale
                    val focusFromCenter = centroid - center

                    pan = focusFromCenter - (focusFromCenter - pan) * zoomRatio + gesturePan
                    scale = nextScale
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawGraphGrid(
                size = size,
                center = center,
                pan = focusedPan,
                scale = renderedScale,
                gridColor = colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            graph.orbitRadii(selectedRadicalId).forEach { radius ->
                drawCircle(
                    color = colorScheme.outlineVariant.copy(alpha = 0.34f),
                    radius = radius * renderedScale,
                    center = center + focusedPan,
                    style = Stroke(width = 1.4f)
                )
            }

            graph.contextRadii(selectedRadicalId).forEach { radius ->
                drawCircle(
                    color = colorScheme.outlineVariant.copy(alpha = 0.18f),
                    radius = radius * renderedScale,
                    center = center + focusedPan,
                    style = Stroke(width = 1.1f)
                )
            }

            visibleEdges.forEach { edge ->
                val from = visiblePositions.getValue(edge.from)
                    .toScreen(center, focusedPan, renderedScale)
                val to = visiblePositions.getValue(edge.to)
                    .toScreen(center, focusedPan, renderedScale)
                drawLine(
                    color = edge.kind.color().copy(alpha = edge.alpha(selectedRadicalId)),
                    start = from,
                    end = to,
                    strokeWidth = if (edge.kind == EdgeKind.KanjiToKanji) 3.4f else 2.2f
                )
            }
        }

        visibleNodes.forEach { node ->
            val screenPosition = visiblePositions.getValue(node.id)
                .toScreen(center, focusedPan, renderedScale)
            val nodeSize = node.kind.size()
            val nodeSizePx = with(density) { nodeSize.toPx() }
            GraphNodeChip(
                node = node,
                selected = node.id == selectedRadicalId,
                alpha = graph.alphaFor(node, selectedRadicalId),
                onClick = {
                    if (node.kind == NodeKind.Radical) {
                        selectedRadicalId = if (selectedRadicalId == node.id) null else node.id
                        scale = 1f
                        pan = Offset.Zero
                    }
                },
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = (screenPosition.x - nodeSizePx / 2f).roundToInt(),
                            y = (screenPosition.y - nodeSizePx / 2f).roundToInt()
                        )
                    }
                    .size(nodeSize)
            )
        }
    }
}

@Composable
private fun GraphNodeChip(
    node: GraphNode,
    selected: Boolean,
    alpha: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = node.kind.accentColor()
    val containerColor = when (node.kind) {
        NodeKind.Kanji -> MaterialTheme.colorScheme.surface
        NodeKind.Radical -> MaterialTheme.colorScheme.surfaceVariant
    }
    val labelStyle = if (node.kind == NodeKind.Kanji) {
        MaterialTheme.typography.bodyLarge
    } else {
        MaterialTheme.typography.bodyMedium
    }

    Surface(
        modifier = modifier
            .alpha(alpha)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = borderColor.copy(alpha = if (selected) 1f else 0.72f),
                shape = CircleShape
            ),
        shape = CircleShape,
        color = containerColor,
        tonalElevation = if (node.kind == NodeKind.Kanji || selected) 4.dp else 2.dp,
        shadowElevation = if (node.kind == NodeKind.Kanji || selected) 5.dp else 2.dp
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${node.label}\n${node.subtitle}",
                color = MaterialTheme.colorScheme.onSurface,
                style = labelStyle,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun rememberKanjiRadicalGraph(): KanjiRadicalGraph = remember {
    val kanji = listOf(
        GraphNode("k_star", "星", "star", NodeKind.Kanji, Offset(0f, -250f), strokeCount = 9),
        GraphNode("k_sky", "空", "sky", NodeKind.Kanji, Offset(-230f, -190f), strokeCount = 8),
        GraphNode("k_light", "光", "light", NodeKind.Kanji, Offset(235f, -185f), strokeCount = 6),
        GraphNode("k_forest", "森", "forest", NodeKind.Kanji, Offset(-380f, -40f), strokeCount = 12),
        GraphNode("k_road", "道", "road", NodeKind.Kanji, Offset(385f, -45f), strokeCount = 12),
        GraphNode("k_word", "語", "word", NodeKind.Kanji, Offset(-255f, 105f), strokeCount = 14),
        GraphNode("k_study", "学", "study", NodeKind.Kanji, Offset(0f, 85f), strokeCount = 8),
        GraphNode("k_sea", "海", "sea", NodeKind.Kanji, Offset(255f, 100f), strokeCount = 9),
        GraphNode("k_flower", "花", "flower", NodeKind.Kanji, Offset(-390f, 255f), strokeCount = 7),
        GraphNode("k_rest", "休", "rest", NodeKind.Kanji, Offset(-135f, 285f), strokeCount = 6),
        GraphNode("k_bright", "明", "bright", NodeKind.Kanji, Offset(125f, 285f), strokeCount = 8),
        GraphNode("k_time", "時", "time", NodeKind.Kanji, Offset(390f, 260f), strokeCount = 10),
        GraphNode("k_grove", "林", "grove", NodeKind.Kanji, Offset(-520f, -205f), strokeCount = 8),
        GraphNode("k_school", "校", "school", NodeKind.Kanji, Offset(-510f, 105f), strokeCount = 10),
        GraphNode("k_town", "町", "town", NodeKind.Kanji, Offset(510f, 90f), strokeCount = 7),
        GraphNode("k_electric", "電", "electric", NodeKind.Kanji, Offset(520f, -215f), strokeCount = 13),
        GraphNode("k_food", "食", "food", NodeKind.Kanji, Offset(-235f, 455f), strokeCount = 9),
        GraphNode("k_drink", "飲", "drink", NodeKind.Kanji, Offset(0f, 500f), strokeCount = 12),
        GraphNode("k_see", "見", "see", NodeKind.Kanji, Offset(235f, 455f), strokeCount = 7),
        GraphNode("k_parent", "親", "parent", NodeKind.Kanji, Offset(470f, 430f), strokeCount = 16)
    )
    val radicals = listOf(
        GraphNode("r_sun", "日", "sun", NodeKind.Radical, Offset(-80f, -430f)),
        GraphNode("r_life", "生", "life", NodeKind.Radical, Offset(95f, -425f)),
        GraphNode("r_cave", "穴", "cave", NodeKind.Radical, Offset(-330f, -340f)),
        GraphNode("r_work", "工", "work", NodeKind.Radical, Offset(-150f, -340f)),
        GraphNode("r_legs", "儿", "legs", NodeKind.Radical, Offset(150f, -340f)),
        GraphNode("r_fire", "火", "fire", NodeKind.Radical, Offset(340f, -335f)),
        GraphNode("r_tree", "木", "tree", NodeKind.Radical, Offset(-560f, -40f)),
        GraphNode("r_walk", "辶", "walk", NodeKind.Radical, Offset(560f, -40f)),
        GraphNode("r_head", "首", "head", NodeKind.Radical, Offset(430f, -345f)),
        GraphNode("r_speech", "言", "speech", NodeKind.Radical, Offset(-420f, 125f)),
        GraphNode("r_mouth", "口", "mouth", NodeKind.Radical, Offset(-120f, 210f)),
        GraphNode("r_child", "子", "child", NodeKind.Radical, Offset(0f, -70f)),
        GraphNode("r_cover", "冖", "cover", NodeKind.Radical, Offset(130f, -45f)),
        GraphNode("r_water", "氵", "water", NodeKind.Radical, Offset(420f, 120f)),
        GraphNode("r_mother", "母", "mother", NodeKind.Radical, Offset(135f, 175f)),
        GraphNode("r_grass", "艹", "grass", NodeKind.Radical, Offset(-565f, 270f)),
        GraphNode("r_person", "亻", "person", NodeKind.Radical, Offset(-300f, 335f)),
        GraphNode("r_moon", "月", "moon", NodeKind.Radical, Offset(260f, 350f)),
        GraphNode("r_temple", "寺", "temple", NodeKind.Radical, Offset(560f, 250f)),
        GraphNode("r_field", "田", "field", NodeKind.Radical, Offset(620f, 80f)),
        GraphNode("r_street", "丁", "block", NodeKind.Radical, Offset(395f, 155f)),
        GraphNode("r_rain", "雨", "rain", NodeKind.Radical, Offset(620f, -320f)),
        GraphNode("r_eat", "食", "eat", NodeKind.Radical, Offset(-340f, 520f)),
        GraphNode("r_lack", "欠", "lack", NodeKind.Radical, Offset(80f, 610f)),
        GraphNode("r_see", "見", "see", NodeKind.Radical, Offset(335f, 540f)),
        GraphNode("r_stand", "立", "stand", NodeKind.Radical, Offset(560f, 560f))
    )
    val radicalEdges = listOf(
        GraphEdge("k_star", "r_sun"),
        GraphEdge("k_star", "r_life"),
        GraphEdge("k_sky", "r_cave"),
        GraphEdge("k_sky", "r_work"),
        GraphEdge("k_light", "r_legs"),
        GraphEdge("k_light", "r_fire"),
        GraphEdge("k_forest", "r_tree"),
        GraphEdge("k_road", "r_walk"),
        GraphEdge("k_road", "r_head"),
        GraphEdge("k_word", "r_speech"),
        GraphEdge("k_word", "r_mouth"),
        GraphEdge("k_study", "r_child"),
        GraphEdge("k_study", "r_cover"),
        GraphEdge("k_sea", "r_water"),
        GraphEdge("k_sea", "r_mother"),
        GraphEdge("k_flower", "r_grass"),
        GraphEdge("k_flower", "r_person"),
        GraphEdge("k_rest", "r_person"),
        GraphEdge("k_rest", "r_tree"),
        GraphEdge("k_bright", "r_sun"),
        GraphEdge("k_bright", "r_moon"),
        GraphEdge("k_time", "r_sun"),
        GraphEdge("k_time", "r_temple"),
        GraphEdge("k_grove", "r_tree"),
        GraphEdge("k_school", "r_tree"),
        GraphEdge("k_town", "r_field"),
        GraphEdge("k_town", "r_street"),
        GraphEdge("k_electric", "r_rain"),
        GraphEdge("k_electric", "r_field"),
        GraphEdge("k_food", "r_eat"),
        GraphEdge("k_drink", "r_eat"),
        GraphEdge("k_drink", "r_lack"),
        GraphEdge("k_see", "r_see"),
        GraphEdge("k_parent", "r_stand"),
        GraphEdge("k_parent", "r_tree"),
        GraphEdge("k_parent", "r_see")
    )
    val kanjiEdges = listOf(
        GraphEdge("k_forest", "k_grove", EdgeKind.KanjiToKanji),
        GraphEdge("k_forest", "k_rest", EdgeKind.KanjiToKanji),
        GraphEdge("k_food", "k_drink", EdgeKind.KanjiToKanji),
        GraphEdge("k_star", "k_bright", EdgeKind.KanjiToKanji),
        GraphEdge("k_word", "k_study", EdgeKind.KanjiToKanji)
    )

    KanjiRadicalGraph(nodes = kanji + radicals, edges = radicalEdges + kanjiEdges)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawGraphGrid(
    size: Size,
    center: Offset,
    pan: Offset,
    scale: Float,
    gridColor: Color
) {
    val spacing = (42f * scale).coerceAtLeast(18f)
    val origin = center + pan
    val startX = origin.x.mod(spacing)
    val startY = origin.y.mod(spacing)

    var x = startX
    while (x < size.width) {
        drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
        x += spacing
    }

    var y = startY
    while (y < size.height) {
        drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
        y += spacing
    }

    drawCircle(
        color = HoshiPurple.copy(alpha = 0.08f),
        radius = 260f * scale,
        center = origin
    )
}

private fun Offset.toScreen(
    center: Offset,
    pan: Offset,
    scale: Float
): Offset = center + pan + this * scale

private fun NodeKind.size(): Dp = when (this) {
    NodeKind.Kanji -> 76.dp
    NodeKind.Radical -> 58.dp
}

private fun NodeKind.accentColor(): Color = when (this) {
    NodeKind.Kanji -> HoshiPurple
    NodeKind.Radical -> HoshiCyan
}

private fun EdgeKind.color(): Color = when (this) {
    EdgeKind.KanjiToRadical -> HoshiCyan
    EdgeKind.KanjiToKanji -> HoshiGreen
}

private data class KanjiRadicalGraph(
    val nodes: List<GraphNode>,
    val edges: List<GraphEdge>
) {
    val nodesById: Map<String, GraphNode> = nodes.associateBy { it.id }
    private val radicals = nodes.filter { it.kind == NodeKind.Radical }
    private val kanji = nodes.filter { it.kind == NodeKind.Kanji }

    fun visibleNodeIds(selectedRadicalId: String?): Set<String> {
        val visibleIds = if (selectedRadicalId == null) {
            nodes.asSequence()
                .filter { it.kind == NodeKind.Radical }
                .map { it.id }
                .toMutableSet()
        } else {
            mutableSetOf(selectedRadicalId)
        }

        if (selectedRadicalId != null) {
            visibleIds += edges.asSequence()
                .filter { it.kind == EdgeKind.KanjiToRadical && it.connects(selectedRadicalId) }
                .mapNotNull { edge ->
                    listOf(edge.from, edge.to).firstOrNull { nodesById.getValue(it).kind == NodeKind.Kanji }
                }
            visibleIds += contextNodes(selectedRadicalId).map { it.id }
        }

        return visibleIds
    }

    fun visibleEdges(
        visibleNodeIds: Set<String>,
        selectedRadicalId: String?
    ): List<GraphEdge> = edges.filter { edge ->
        edge.from in visibleNodeIds &&
            edge.to in visibleNodeIds &&
            (edge.connects(selectedRadicalId) || edge.connectsContextNode(selectedRadicalId))
    }

    fun positionsFor(
        nodes: List<GraphNode>,
        selectedRadicalId: String?
    ): Map<String, Offset> {
        val rawPositions = nodes.associate { node ->
            node.id to positionFor(
                node = node,
                selectedRadicalId = selectedRadicalId
            )
        }

        if (selectedRadicalId == null) {
            return separateOverlappingPositions(
                positions = rawPositions,
                pinnedNodeId = null,
                minDistance = OverviewMinimumNodeDistance
            )
        }

        return separateOverlappingPositions(
            positions = rawPositions,
            pinnedNodeId = selectedRadicalId,
            minDistance = FocusedMinimumNodeDistance
        )
    }

    fun positionFor(
        node: GraphNode,
        selectedRadicalId: String?
    ): Offset {
        if (selectedRadicalId == null) {
            return overviewPosition(node)
        }

        if (node.id == selectedRadicalId) {
            return Offset.Zero
        }

        val relatedKanji = associatedKanji(selectedRadicalId)
        val relatedIndex = relatedKanji.indexOfFirst { it.id == node.id }
        if (relatedIndex >= 0) {
            return orbitalKanjiPosition(
                orbitalNode = orbitalKanji(selectedRadicalId)[relatedIndex]
            )
        }

        val contextNode = contextOrbitalNodes(selectedRadicalId).firstOrNull { it.node.id == node.id }
        if (contextNode != null) {
            return orbitalKanjiPosition(contextNode)
        }

        return overviewPosition(node)
    }

    fun orbitRadii(selectedRadicalId: String?): List<Float> {
        if (selectedRadicalId == null) {
            return emptyList()
        }

        return orbitalKanji(selectedRadicalId)
            .map { it.radius }
            .distinct()
            .sorted()
    }

    fun contextRadii(selectedRadicalId: String?): List<Float> {
        if (selectedRadicalId == null) {
            return emptyList()
        }

        return contextOrbitalNodes(selectedRadicalId)
            .map { it.radius }
            .distinct()
            .sorted()
    }

    fun focusScaleFor(
        selectedRadicalId: String?,
        canvasSize: IntSize
    ): Float {
        if (selectedRadicalId == null || canvasSize.width == 0 || canvasSize.height == 0) {
            return 1f
        }

        val outerRadius = orbitalKanji(selectedRadicalId).maxOfOrNull { it.radius } ?: InnerOrbitRadius
        val contextRadius = contextOrbitalNodes(selectedRadicalId).maxOfOrNull { it.radius } ?: outerRadius
        val fittedRadius = maxOf(outerRadius, contextRadius)
        val availableSpan = min(canvasSize.width, canvasSize.height).toFloat() - FocusedViewportPadding
        val neededRadius = fittedRadius + KanjiNodeLogicalRadius
        val fitScale = availableSpan / (neededRadius * 2f)

        return fitScale.coerceIn(MinFocusedScale, MaxFocusedScale)
    }

    fun alphaFor(
        node: GraphNode,
        selectedRadicalId: String?
    ): Float = when {
        selectedRadicalId == null -> 1f
        node.id == selectedRadicalId -> 1f
        else -> {
            val orbitalNode = orbitalKanji(selectedRadicalId).firstOrNull { it.node.id == node.id }
            if (orbitalNode == null) {
                ContextNodeAlpha
            } else {
                (1f - orbitalNode.ringIndex * 0.12f).coerceIn(0.58f, 0.94f)
            }
        }
    }

    private fun overviewPosition(node: GraphNode): Offset {
        val radicalIndex = radicals.indexOfFirst { it.id == node.id }
        if (radicalIndex >= 0) {
            return gridPosition(
                index = radicalIndex,
                count = radicals.size,
                columns = 5,
                horizontalGap = 150f,
                verticalGap = 130f
            )
        }

        val kanjiIndex = kanji.indexOfFirst { it.id == node.id }
        return gridPosition(
            index = kanjiIndex,
            count = kanji.size,
            columns = 5,
            horizontalGap = 168f,
            verticalGap = 142f
        )
    }

    private fun associatedKanji(radicalId: String): List<GraphNode> {
        val kanjiIds = edges.asSequence()
            .filter { it.kind == EdgeKind.KanjiToRadical && it.connects(radicalId) }
            .mapNotNull { edge ->
                listOf(edge.from, edge.to).firstOrNull { nodesById.getValue(it).kind == NodeKind.Kanji }
            }
            .toSet()

        return kanji
            .filter { it.id in kanjiIds }
            .sortedWith(compareBy<GraphNode> { it.strokeCount }.thenBy { it.label })
    }

    private fun contextNodes(radicalId: String): List<GraphNode> = contextNodeAnchors(radicalId)
        .map { it.node }

    private fun contextNodeAnchors(radicalId: String): List<ContextNodeAnchor> {
        val primaryKanji = associatedKanji(radicalId)
        val primaryKanjiIds = primaryKanji.map { it.id }.toSet()
        val anchorsByNodeId = linkedMapOf<String, ContextNodeAnchor>()

        edges.asSequence()
            .filter { it.kind == EdgeKind.KanjiToRadical }
            .filter { it.from in primaryKanjiIds || it.to in primaryKanjiIds }
            .forEach { edge ->
                val primaryKanjiId = listOf(edge.from, edge.to)
                    .firstOrNull { it in primaryKanjiIds }
                val radicalNodeId = listOf(edge.from, edge.to)
                    .firstOrNull { nodesById.getValue(it).kind == NodeKind.Radical }

                if (primaryKanjiId != null && radicalNodeId != null && radicalNodeId != radicalId) {
                    anchorsByNodeId.putIfAbsent(
                        radicalNodeId,
                        ContextNodeAnchor(
                            node = nodesById.getValue(radicalNodeId),
                            primaryKanjiId = primaryKanjiId
                        )
                    )
                }
            }

        edges.asSequence()
            .filter { it.kind == EdgeKind.KanjiToKanji }
            .filter { it.from in primaryKanjiIds || it.to in primaryKanjiIds }
            .forEach { edge ->
                val primaryKanjiId = listOf(edge.from, edge.to)
                    .firstOrNull { it in primaryKanjiIds }
                val peerKanjiId = listOf(edge.from, edge.to)
                    .firstOrNull { it !in primaryKanjiIds }

                if (primaryKanjiId != null && peerKanjiId != null) {
                    anchorsByNodeId.putIfAbsent(
                        peerKanjiId,
                        ContextNodeAnchor(
                            node = nodesById.getValue(peerKanjiId),
                            primaryKanjiId = primaryKanjiId
                        )
                    )
                }
            }

        return anchorsByNodeId.values.sortedWith(
            compareBy<ContextNodeAnchor> {
                primaryKanji.indexOfFirst { kanji -> kanji.id == it.primaryKanjiId }
            }
                .thenBy { it.node.kind.ordinal }
                .thenBy { it.node.strokeCount }
                .thenBy { it.node.label }
        )
    }

    private fun orbitalKanji(radicalId: String): List<OrbitalNode> {
        val sortedKanji = associatedKanji(radicalId)
        val orbitalNodes = mutableListOf<OrbitalNode>()
        var ringIndex = 0
        var kanjiIndex = 0

        while (kanjiIndex < sortedKanji.size) {
            val radius = InnerOrbitRadius + ringIndex * OrbitGap
            val capacity = orbitCapacity(radius)
            val ringKanji = sortedKanji.drop(kanjiIndex).take(capacity)
            val angleStep = (2f * PI.toFloat()) / ringKanji.size.coerceAtLeast(1)

            ringKanji.forEachIndexed { slotIndex, node ->
                orbitalNodes += OrbitalNode(
                    node = node,
                    radius = radius,
                    angleRadians = -PI.toFloat() / 2f + slotIndex * angleStep,
                    ringIndex = ringIndex
                )
            }

            kanjiIndex += ringKanji.size
            ringIndex += 1
        }

        return orbitalNodes
    }

    private fun contextOrbitalNodes(radicalId: String): List<OrbitalNode> {
        val primaryKanjiById = orbitalKanji(radicalId).associateBy { it.node.id }
        return contextNodeAnchors(radicalId)
            .groupBy { it.primaryKanjiId }
            .flatMap { (primaryKanjiId, anchoredNodes) ->
                val primaryOrbitalNode = primaryKanjiById[primaryKanjiId] ?: return@flatMap emptyList()
                val angleOffsets = localContextAngleOffsets(anchoredNodes.size)

                anchoredNodes.mapIndexed { index, anchor ->
                    OrbitalNode(
                        node = anchor.node,
                        radius = primaryOrbitalNode.radius + ContextRingGap,
                        angleRadians = primaryOrbitalNode.angleRadians + angleOffsets[index],
                        ringIndex = primaryOrbitalNode.ringIndex + ContextRingIndexOffset
                    )
                }
            }
    }

    private fun GraphEdge.connectsContextNode(selectedRadicalId: String?): Boolean {
        if (selectedRadicalId == null) {
            return false
        }

        val contextNodeIds = contextNodes(selectedRadicalId).map { it.id }.toSet()
        val primaryKanjiIds = associatedKanji(selectedRadicalId).map { it.id }.toSet()
        return (from in contextNodeIds && to in primaryKanjiIds) ||
            (to in contextNodeIds && from in primaryKanjiIds)
    }
}

private fun gridPosition(
    index: Int,
    count: Int,
    columns: Int,
    horizontalGap: Float,
    verticalGap: Float
): Offset {
    val rows = (count + columns - 1) / columns
    val row = index / columns
    val column = index % columns
    val rowCount = if (row == rows - 1) {
        count - row * columns
    } else {
        columns
    }
    val centeredColumn = column - (rowCount - 1) / 2f
    val centeredRow = row - (rows - 1) / 2f

    return Offset(centeredColumn * horizontalGap, centeredRow * verticalGap)
}

private fun orbitalKanjiPosition(orbitalNode: OrbitalNode): Offset {
    return Offset(
        x = cos(orbitalNode.angleRadians) * orbitalNode.radius,
        y = sin(orbitalNode.angleRadians) * orbitalNode.radius
    )
}

private fun separateOverlappingPositions(
    positions: Map<String, Offset>,
    pinnedNodeId: String?,
    minDistance: Float
): Map<String, Offset> {
    val resolved = positions.toMutableMap()
    val ids = resolved.keys.toList()

    repeat(CollisionResolutionPasses) {
        ids.forEachIndexed { firstIndex, firstId ->
            ids.drop(firstIndex + 1).forEach { secondId ->
                val first = resolved.getValue(firstId)
                val second = resolved.getValue(secondId)
                val delta = second - first
                val distance = hypot(delta.x, delta.y)
                if (distance >= minDistance) {
                    return@forEach
                }

                val fallbackAngle = (firstIndex + 1) * 2.3999631f
                val direction = if (distance < 0.001f) {
                    Offset(cos(fallbackAngle), sin(fallbackAngle))
                } else {
                    delta / distance
                }
                val push = (minDistance - distance) / 2f
                val firstPinned = firstId == pinnedNodeId
                val secondPinned = secondId == pinnedNodeId

                when {
                    firstPinned && !secondPinned -> resolved[secondId] = second + direction * (push * 2f)
                    secondPinned && !firstPinned -> resolved[firstId] = first - direction * (push * 2f)
                    !firstPinned && !secondPinned -> {
                        resolved[firstId] = first - direction * push
                        resolved[secondId] = second + direction * push
                    }
                }
            }
        }
    }

    return resolved
}

private fun GraphEdge.alpha(selectedRadicalId: String?): Float = when {
    selectedRadicalId == null -> 0f
    connects(selectedRadicalId) -> 0.44f
    else -> ContextEdgeAlpha
}

private fun localContextAngleOffsets(count: Int): List<Float> {
    if (count == 1) {
        return listOf(0f)
    }

    val midpoint = (count - 1) / 2f
    return List(count) { index -> (index - midpoint) * LocalContextAngleStepRadians }
}

private data class GraphNode(
    val id: String,
    val label: String,
    val subtitle: String,
    val kind: NodeKind,
    val position: Offset,
    val strokeCount: Int = 0
)

private data class GraphEdge(
    val from: String,
    val to: String,
    val kind: EdgeKind = EdgeKind.KanjiToRadical
) {
    fun connects(nodeId: String?): Boolean = nodeId != null && (from == nodeId || to == nodeId)
}

private enum class NodeKind {
    Kanji,
    Radical
}

private enum class EdgeKind {
    KanjiToRadical,
    KanjiToKanji
}

private data class OrbitalNode(
    val node: GraphNode,
    val radius: Float,
    val angleRadians: Float,
    val ringIndex: Int
)

private data class ContextNodeAnchor(
    val node: GraphNode,
    val primaryKanjiId: String
)

private fun orbitCapacity(radius: Float): Int {
    val circumference = 2f * PI.toFloat() * radius
    return floor(circumference / MinimumOrbitalNodeSpacing).toInt().coerceAtLeast(MinOrbitCapacity)
}

private const val InnerOrbitRadius = 170f
private const val OrbitGap = 132f
private const val MinOrbitCapacity = 1
private const val MinimumOrbitalNodeSpacing = 156f
private const val KanjiNodeLogicalRadius = 48f
private const val FocusedViewportPadding = 96f
private const val MinFocusedScale = 0.42f
private const val MaxFocusedScale = 1.28f
private const val ContextRingGap = 150f
private const val ContextRingIndexOffset = 2
private const val ContextNodeAlpha = 0.2f
private const val ContextEdgeAlpha = 0.12f
private const val LocalContextAngleStepRadians = 0.22f
private const val OverviewMinimumNodeDistance = 92f
private const val FocusedMinimumNodeDistance = 124f
private const val CollisionResolutionPasses = 10
