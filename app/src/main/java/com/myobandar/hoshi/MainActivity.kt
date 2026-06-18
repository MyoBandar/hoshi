package com.myobandar.hoshi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountTree
import androidx.compose.material.icons.rounded.CollectionsBookmark
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.RateReview
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.myobandar.hoshi.ui.theme.HoshiAmber
import com.myobandar.hoshi.ui.theme.HoshiCyan
import com.myobandar.hoshi.ui.theme.HoshiGreen
import com.myobandar.hoshi.ui.theme.HoshiPurple
import com.myobandar.hoshi.ui.theme.HoshiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var themePreference by rememberSaveable { mutableStateOf(ThemePreference.System) }
            val systemDarkTheme = isSystemInDarkTheme()
            val darkTheme = when (themePreference) {
                ThemePreference.System -> systemDarkTheme
                ThemePreference.Light -> false
                ThemePreference.Dark -> true
            }

            HoshiTheme(darkTheme = darkTheme) {
                HoshiApp(
                    darkTheme = darkTheme,
                    themePreference = themePreference,
                    onThemePreferenceChange = { themePreference = it }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HoshiApp(
    darkTheme: Boolean,
    themePreference: ThemePreference,
    onThemePreferenceChange: (ThemePreference) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDestination by rememberSaveable { mutableStateOf(HoshiDestination.Graph) }
    var settingsOpen by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (settingsOpen) "Settings" else selectedDestination.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = "Search"
                        )
                    }
                    IconButton(onClick = { settingsOpen = true }) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "Settings"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        },
        bottomBar = {
            HoshiBottomNavigation(
                selectedDestination = selectedDestination,
                onDestinationSelected = {
                    selectedDestination = it
                    settingsOpen = false
                }
            )
        }
    ) { innerPadding ->
        if (settingsOpen) {
            SettingsContent(
                darkTheme = darkTheme,
                themePreference = themePreference,
                onThemePreferenceChange = onThemePreferenceChange,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            HoshiDestinationContent(
                destination = selectedDestination,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }
}

@Composable
private fun HoshiBottomNavigation(
    selectedDestination: HoshiDestination,
    onDestinationSelected: (HoshiDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        HoshiDestination.entries.forEach { destination ->
            NavigationBarItem(
                selected = selectedDestination == destination,
                onClick = { onDestinationSelected(destination) },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.title
                    )
                },
                label = { Text(destination.title) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
private fun HoshiDestinationContent(
    destination: HoshiDestination,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ScreenHero(destination = destination)
        MetricRow(destination = destination)
        DummyContentCard(destination = destination)
    }
}

@Composable
private fun ScreenHero(destination: HoshiDestination) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = destination.accent.copy(alpha = 0.18f),
                    contentColor = destination.accent
                ) {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = null,
                        modifier = Modifier.padding(14.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = destination.headline,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = destination.subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            KanjiPreview(destination = destination)
        }
    }
}

@Composable
private fun KanjiPreview(destination: HoshiDestination) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        destination.sampleKanji.forEachIndexed { index, kanji ->
            val isPrimary = index == 0
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                color = if (isPrimary) destination.accent.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isPrimary) destination.accent else MaterialTheme.colorScheme.outlineVariant
                )
            ) {
                Box(
                    modifier = Modifier
                        .height(76.dp)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = kanji,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (isPrimary) destination.accent else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricRow(destination: HoshiDestination) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        destination.metrics.forEach { metric ->
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = metric.value,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = metric.label,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun DummyContentCard(destination: HoshiDestination) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = destination.cardTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            destination.bullets.forEach { bullet ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(destination.accent, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = bullet,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsContent(
    darkTheme: Boolean,
    themePreference: ThemePreference,
    onThemePreferenceChange: (ThemePreference) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Appearance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Preview and tune the app theme while the Hoshi screens are still placeholders.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(18.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                SettingsSwitchRow(
                    title = "Dark theme",
                    subtitle = if (themePreference == ThemePreference.System) {
                        "Following the system setting"
                    } else {
                        "Manual override"
                    },
                    checked = darkTheme,
                    onCheckedChange = { enabled ->
                        onThemePreferenceChange(
                            if (enabled) ThemePreference.Dark else ThemePreference.Light
                        )
                    }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                SettingsSwitchRow(
                    title = "Use system setting",
                    subtitle = "Automatically match Android light or dark mode.",
                    checked = themePreference == ThemePreference.System,
                    onCheckedChange = { enabled ->
                        if (enabled) {
                            onThemePreferenceChange(ThemePreference.System)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

enum class ThemePreference {
    System,
    Light,
    Dark
}

private enum class HoshiDestination(
    val title: String,
    val icon: ImageVector,
    val accent: Color,
    val headline: String,
    val subtitle: String,
    val sampleKanji: List<String>,
    val metrics: List<Metric>,
    val cardTitle: String,
    val bullets: List<String>
) {
    Graph(
        title = "Graph",
        icon = Icons.Rounded.AccountTree,
        accent = HoshiPurple,
        headline = "My Kanji Graph",
        subtitle = "Map radicals, known kanji, and new connections.",
        sampleKanji = listOf("亻", "休", "体"),
        metrics = listOf(Metric("124", "nodes"), Metric("38", "known")),
        cardTitle = "Graph placeholder",
        bullets = listOf("Radical and kanji nodes", "Relationship preview", "Search-ready graph canvas")
    ),
    Discover(
        title = "Discover",
        icon = Icons.Rounded.Explore,
        accent = HoshiGreen,
        headline = "Discover Kanji",
        subtitle = "Capture, extract, and save kanji from the world.",
        sampleKanji = listOf("非", "常", "口"),
        metrics = listOf(Metric("3", "found"), Metric("1", "saved")),
        cardTitle = "Discovery placeholder",
        bullets = listOf("Camera extraction entry point", "Detected kanji queue", "Save-to-graph action")
    ),
    Review(
        title = "Review",
        icon = Icons.Rounded.RateReview,
        accent = HoshiAmber,
        headline = "Review Queue",
        subtitle = "Practice the words and kanji you have already met.",
        sampleKanji = listOf("休", "作", "何"),
        metrics = listOf(Metric("12", "due"), Metric("7", "seen")),
        cardTitle = "Review placeholder",
        bullets = listOf("Due cards overview", "Recent mistakes", "Lightweight progress snapshot")
    ),
    Collection(
        title = "Collection",
        icon = Icons.Rounded.CollectionsBookmark,
        accent = HoshiCyan,
        headline = "Collection",
        subtitle = "Browse saved kanji, radicals, and source sightings.",
        sampleKanji = listOf("位", "住", "使"),
        metrics = listOf(Metric("86", "saved"), Metric("24", "radicals")),
        cardTitle = "Collection placeholder",
        bullets = listOf("Saved kanji library", "Radical index", "Source and sighting history")
    )
}

private data class Metric(
    val value: String,
    val label: String
)

@Preview(showBackground = true)
@Composable
private fun HoshiAppLightPreview() {
    HoshiTheme(darkTheme = false) {
        HoshiApp(
            darkTheme = false,
            themePreference = ThemePreference.Light,
            onThemePreferenceChange = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HoshiAppDarkPreview() {
    HoshiTheme(darkTheme = true) {
        HoshiApp(
            darkTheme = true,
            themePreference = ThemePreference.Dark,
            onThemePreferenceChange = { }
        )
    }
}
