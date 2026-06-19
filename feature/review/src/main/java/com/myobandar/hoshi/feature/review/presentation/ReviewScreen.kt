package com.myobandar.hoshi.feature.review.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.myobandar.hoshi.core.designsystem.component.FeatureMetric
import com.myobandar.hoshi.core.designsystem.component.FeatureOverview
import com.myobandar.hoshi.core.designsystem.component.FeatureOverviewScreen
import com.myobandar.hoshi.core.designsystem.theme.HoshiAmber
import com.myobandar.hoshi.feature.review.domain.ReviewPlaceholderContent

@Composable
fun ReviewScreen(
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    FeatureOverviewScreen(
        overview = FeatureOverview(
            icon = icon,
            accent = HoshiAmber,
            headline = ReviewPlaceholderContent.headline,
            subtitle = ReviewPlaceholderContent.subtitle,
            sampleKanji = ReviewPlaceholderContent.sampleKanji,
            metrics = ReviewPlaceholderContent.metrics.map { FeatureMetric(it.value, it.label) },
            cardTitle = ReviewPlaceholderContent.cardTitle,
            bullets = ReviewPlaceholderContent.bullets
        ),
        modifier = modifier
    )
}
