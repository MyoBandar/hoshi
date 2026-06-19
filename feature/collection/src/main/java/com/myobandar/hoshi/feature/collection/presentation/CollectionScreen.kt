package com.myobandar.hoshi.feature.collection.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.myobandar.hoshi.core.designsystem.component.FeatureMetric
import com.myobandar.hoshi.core.designsystem.component.FeatureOverview
import com.myobandar.hoshi.core.designsystem.component.FeatureOverviewScreen
import com.myobandar.hoshi.core.designsystem.theme.HoshiCyan
import com.myobandar.hoshi.feature.collection.domain.CollectionPlaceholderContent

@Composable
fun CollectionScreen(
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    FeatureOverviewScreen(
        overview = FeatureOverview(
            icon = icon,
            accent = HoshiCyan,
            headline = CollectionPlaceholderContent.headline,
            subtitle = CollectionPlaceholderContent.subtitle,
            sampleKanji = CollectionPlaceholderContent.sampleKanji,
            metrics = CollectionPlaceholderContent.metrics.map { FeatureMetric(it.value, it.label) },
            cardTitle = CollectionPlaceholderContent.cardTitle,
            bullets = CollectionPlaceholderContent.bullets
        ),
        modifier = modifier
    )
}
