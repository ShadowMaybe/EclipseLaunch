package me.shadow.eclipselaunch.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.shadow.eclipselaunch.ui.activity.InstallableItem
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.CircularProgressIndicator
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun SplashScreen(
    title: String,
    statusText: String,
    items: List<InstallableItem>,
    startEnabled: Boolean,
    onStartClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = title,
            style = MiuixTheme.textStyles.title1,
            fontWeight = FontWeight.Bold,
            color = MiuixTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = statusText,
            style = MiuixTheme.textStyles.body2,
            color = MiuixTheme.colorScheme.onSurfaceSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            itemsIndexed(items, key = { index, item -> item.name ?: index.toString() }) { _, item ->
                InstallableItemRow(item = item)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onStartClick,
            enabled = startEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = statusText.ifEmpty { "Start" },
                style = MiuixTheme.textStyles.body1
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun InstallableItemRow(item: InstallableItem) {
    BasicComponent(
        modifier = Modifier.fillMaxWidth(),
        title = item.name ?: "",
        summary = item.summary,
        endActions = {
            when {
                item.isRunning -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = 8.dp)
                    )
                }
                item.isFinished -> {
                    Text(
                        text = "✓",
                        style = MiuixTheme.textStyles.body1,
                        color = MiuixTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
        }
    )
}
