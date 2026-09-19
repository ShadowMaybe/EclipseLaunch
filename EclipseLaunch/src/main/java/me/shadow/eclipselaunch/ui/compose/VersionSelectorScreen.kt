package me.shadow.eclipselaunch.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import me.shadow.eclipselaunch.R
import top.yukonga.miuix.kmp.basic.TabRow
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun VersionSelectorScreen(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onTabSelected: (Int) -> Unit,
    onVersionSelected: (String) -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.generic_release),
        stringResource(R.string.version_snapshot),
        stringResource(R.string.version_beta),
        stringResource(R.string.version_alpha)
    )

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            tabs = tabs,
            selectedTabIndex = selectedTab,
            onTabSelected = { index ->
                selectedTab = index
                onTabSelected(index)
            }
        )

        TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            label = stringResource(R.string.generic_search),
        )
    }
}
