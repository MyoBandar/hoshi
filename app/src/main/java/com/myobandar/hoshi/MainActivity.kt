package com.myobandar.hoshi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountTree
import androidx.compose.material.icons.rounded.CollectionsBookmark
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.RateReview
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.myobandar.hoshi.core.designsystem.theme.HoshiTheme
import com.myobandar.hoshi.feature.collection.presentation.CollectionScreen
import com.myobandar.hoshi.feature.constellation.presentation.ConstellationScreen
import com.myobandar.hoshi.feature.review.presentation.ReviewScreen
import com.myobandar.hoshi.feature.scanner.presentation.ScannerScreen
import com.myobandar.hoshi.feature.settings.domain.ThemePreference
import com.myobandar.hoshi.feature.settings.presentation.SettingsScreen

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
    var selectedDestination by rememberSaveable { mutableStateOf(HoshiDestination.Constellation) }
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
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)

        if (settingsOpen) {
            SettingsScreen(
                darkTheme = darkTheme,
                themePreference = themePreference,
                onThemePreferenceChange = onThemePreferenceChange,
                modifier = contentModifier
            )
        } else {
            HoshiDestinationContent(
                destination = selectedDestination,
                modifier = contentModifier
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
    when (destination) {
        HoshiDestination.Constellation -> ConstellationScreen(
            icon = destination.icon,
            modifier = modifier
        )
        HoshiDestination.Discover -> ScannerScreen(
            icon = destination.icon,
            modifier = modifier
        )
        HoshiDestination.Review -> ReviewScreen(
            icon = destination.icon,
            modifier = modifier
        )
        HoshiDestination.Collection -> CollectionScreen(
            icon = destination.icon,
            modifier = modifier
        )
    }
}

private enum class HoshiDestination(
    val title: String,
    val icon: ImageVector
) {
    Constellation(
        title = "Constellation",
        icon = Icons.Rounded.AccountTree
    ),
    Discover(
        title = "Discover",
        icon = Icons.Rounded.Explore
    ),
    Review(
        title = "Review",
        icon = Icons.Rounded.RateReview
    ),
    Collection(
        title = "Collection",
        icon = Icons.Rounded.CollectionsBookmark
    )
}

@Preview(showBackground = true)
@Composable
private fun HoshiAppPreview() {
    HoshiTheme {
        HoshiApp(
            darkTheme = false,
            themePreference = ThemePreference.Light,
            onThemePreferenceChange = {}
        )
    }
}
