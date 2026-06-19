package com.myobandar.hoshi.feature.graph.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.myobandar.hoshi.core.designsystem.component.FeatureMetric
import com.myobandar.hoshi.core.designsystem.component.FeatureOverview
import com.myobandar.hoshi.core.designsystem.component.FeatureOverviewScreen
import com.myobandar.hoshi.core.designsystem.theme.HoshiPurple
import com.myobandar.hoshi.feature.graph.domain.GraphPlaceholderContent

@Composable
fun GraphScreen(
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    FeatureOverviewScreen(
        overview = FeatureOverview(
            icon = icon,
            accent = HoshiPurple,
            headline = GraphPlaceholderContent.headline,
            subtitle = GraphPlaceholderContent.subtitle,
            sampleKanji = GraphPlaceholderContent.sampleKanji,
            metrics = GraphPlaceholderContent.metrics.map { FeatureMetric(it.value, it.label) },
            cardTitle = GraphPlaceholderContent.cardTitle,
            bullets = GraphPlaceholderContent.bullets
        ),
        modifier = modifier
    )
}
