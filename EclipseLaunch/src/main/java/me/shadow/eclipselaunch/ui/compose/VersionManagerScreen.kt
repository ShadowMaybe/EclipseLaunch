package me.shadow.eclipselaunch.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import me.shadow.eclipselaunch.R
import top.yukonga.miuix.kmp.basic.ArrowPreference
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun VersionManagerScreen(
    onModsClick: () -> Unit = {},
    onGamePathClick: () -> Unit = {},
    onResourcePathClick: () -> Unit = {},
    onWorldPathClick: () -> Unit = {},
    onShaderPathClick: () -> Unit = {},
    onScreenshotPathClick: () -> Unit = {},
    onLogsPathClick: () -> Unit = {},
    onCrashReportPathClick: () -> Unit = {},
    onVersionSettingsClick: () -> Unit = {},
    onVersionRenameClick: () -> Unit = {},
    onVersionCopyClick: () -> Unit = {},
    onVersionDeleteClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Left panel - Shortcuts
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.version_manager_shortcuts),
                style = MiuixTheme.textStyles.title2,
                color = MiuixTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            ArrowPreference(
                title = stringResource(R.string.version_manager_shortcuts_mods),
                summary = null,
                onClick = onModsClick
            )
            ArrowPreference(
                title = stringResource(R.string.version_manager_shortcuts_version_path),
                summary = null,
                onClick = onGamePathClick
            )
            ArrowPreference(
                title = stringResource(R.string.version_manager_shortcuts_resource_path),
                summary = null,
                onClick = onResourcePathClick
            )
            ArrowPreference(
                title = stringResource(R.string.version_manager_shortcuts_world_path),
                summary = null,
                onClick = onWorldPathClick
            )
            ArrowPreference(
                title = stringResource(R.string.version_manager_shortcuts_shader_path),
                summary = null,
                onClick = onShaderPathClick
            )
            ArrowPreference(
                title = stringResource(R.string.version_manager_shortcuts_screenshot_path),
                summary = null,
                onClick = onScreenshotPathClick
            )
            ArrowPreference(
                title = stringResource(R.string.version_manager_shortcuts_logs_path),
                summary = null,
                onClick = onLogsPathClick
            )
            ArrowPreference(
                title = stringResource(R.string.version_manager_shortcuts_crash_report_path),
                summary = null,
                onClick = onCrashReportPathClick
            )
        }

        // Right panel - Version Management
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.version_manager_title),
                style = MiuixTheme.textStyles.title2,
                color = MiuixTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            ArrowPreference(
                title = stringResource(R.string.setting_category_version),
                summary = null,
                onClick = onVersionSettingsClick
            )
            ArrowPreference(
                title = stringResource(R.string.version_manager_rename),
                summary = null,
                onClick = onVersionRenameClick
            )
            ArrowPreference(
                title = stringResource(R.string.version_manager_copy),
                summary = null,
                onClick = onVersionCopyClick
            )
            ArrowPreference(
                title = stringResource(R.string.version_manager_delete),
                summary = null,
                onClick = onVersionDeleteClick
            )
        }
    }
}
