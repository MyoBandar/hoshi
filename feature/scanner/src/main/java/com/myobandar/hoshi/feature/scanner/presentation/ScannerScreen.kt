package com.myobandar.hoshi.feature.scanner.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.myobandar.hoshi.core.designsystem.component.FeatureMetric
import com.myobandar.hoshi.core.designsystem.component.FeatureOverview
import com.myobandar.hoshi.core.designsystem.component.FeatureOverviewScreen
import com.myobandar.hoshi.core.designsystem.theme.HoshiGreen
import com.myobandar.hoshi.feature.scanner.domain.ScannerPlaceholderContent

@Composable
fun ScannerScreen(
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    FeatureOverviewScreen(
        overview = FeatureOverview(
            icon = icon,
            accent = HoshiGreen,
            headline = ScannerPlaceholderContent.headline,
            subtitle = ScannerPlaceholderContent.subtitle,
            sampleKanji = ScannerPlaceholderContent.sampleKanji,
            metrics = ScannerPlaceholderContent.metrics.map { FeatureMetric(it.value, it.label) },
            cardTitle = ScannerPlaceholderContent.cardTitle,
            bullets = ScannerPlaceholderContent.bullets
        ),
        modifier = modifier
    )
}
