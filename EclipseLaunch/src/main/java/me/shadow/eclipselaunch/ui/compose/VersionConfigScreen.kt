package me.shadow.eclipselaunch.ui.compose

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import me.shadow.eclipselaunch.R
import me.shadow.eclipselaunch.plugins.driver.DriverPluginManager
import me.shadow.eclipselaunch.renderer.Renderers
import me.shadow.eclipselaunch.feature.version.VersionConfig
import me.shadow.eclipselaunch.feature.version.VersionConfig.IsolationType
import me.shadow.eclipselaunch.feature.version.VersionConfig.CREATOR.getIsolationString
import net.kdt.pojavlaunch.multirt.MultiRTUtils
import net.kdt.pojavlaunch.Tools
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * VersionConfigScreen - Compose wrapper for per-version configuration.
 * The fragment still manages the VersionConfig lifecycle and EventBus events.
 */
@Composable
fun VersionConfigScreen(
    config: VersionConfig?,
    versionName: String,
    isolationTypes: List<IsolationType>,
    rendererNames: List<String>,
    rendererIds: List<String>,
    driverNames: List<String>,
    runtimeNames: List<String>,
    controlName: String,
    customPath: String,
    onIsolationChange: (IsolationType) -> Unit,
    onRendererChange: (Int) -> Unit,
    onDriverChange: (Int) -> Unit,
    onRuntimeChange: (Int) -> Unit,
    onCustomInfoChange: (String) -> Unit,
    onJavaArgsChange: (String) -> Unit,
    onControlClick: () -> Unit,
    onResetControl: () -> Unit,
    onCustomPathClick: () -> Unit,
    onResetCustomPath: () -> Unit,
    onIconClick: () -> Unit,
    onResetIcon: () -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp)
    ) {
        // Icon
        SmallTitle(text = stringResource(R.string.version_config_title))
        Card(modifier = Modifier.padding(horizontal = 12.dp)) {
            ArrowPreference(
                title = stringResource(R.string.version_install_custom_icon),
                summary = versionName,
                onClick = onIconClick
            )
            ArrowPreference(
                title = stringResource(R.string.version_install_reset_icon),
                summary = null,
                onClick = onResetIcon
            )
        }

        // Isolation
        SmallTitle(text = "Isolation")
        Card(modifier = Modifier.padding(horizontal = 12.dp)) {
            var selectedIsolation by remember { mutableStateOf(config?.getIsolationType() ?: IsolationType.FOLLOW) }
            val isoNames = isolationTypes.map { getIsolationString(context, it) }
            ArrowPreference(
                title = stringResource(R.string.version_isolation_title),
                summary = getIsolationString(context, selectedIsolation),
                onClick = {
                    val index = isolationTypes.indexOf(selectedIsolation)
                    val nextIndex = (index + 1) % isolationTypes.size
                    selectedIsolation = isolationTypes[nextIndex]
                    onIsolationChange(selectedIsolation)
                }
            )
        }

        // Control & Path
        SmallTitle(text = "Control & Path")
        Card(modifier = Modifier.padding(horizontal = 12.dp)) {
            ArrowPreference(
                title = stringResource(R.string.version_config_control),
                summary = controlName.ifEmpty { stringResource(R.string.version_config_control_default) },
                onClick = onControlClick
            )
            if (controlName.isNotEmpty()) {
                ArrowPreference(
                    title = stringResource(R.string.version_config_reset_control),
                    summary = null,
                    onClick = onResetControl
                )
            }
            ArrowPreference(
                title = stringResource(R.string.version_config_custom_path),
                summary = customPath.ifEmpty { stringResource(R.string.version_config_custom_path_default) },
                onClick = onCustomPathClick
            )
            if (customPath.isNotEmpty()) {
                ArrowPreference(
                    title = stringResource(R.string.version_config_reset_custom_path),
                    summary = null,
                    onClick = onResetCustomPath
                )
            }
        }

        // Renderer & Driver
        SmallTitle(text = "Renderer & Driver")
        Card(modifier = Modifier.padding(horizontal = 12.dp)) {
            var selectedRenderer by remember { mutableStateOf(config?.getRenderer() ?: "") }
            ArrowPreference(
                title = stringResource(R.string.setting_renderer_title),
                summary = selectedRenderer.ifEmpty { stringResource(R.string.generic_default) },
                onClick = {
                    val names = rendererNames.toMutableList()
                    names.add(context.getString(R.string.generic_default))
                    val defaultIndex = names.size - 1
                    // Cycle through options
                    val currentIndex = rendererIds.indexOf(selectedRenderer).coerceAtLeast(0)
                    val nextIndex = if (currentIndex >= rendererIds.size - 1) defaultIndex else currentIndex + 1
                    selectedRenderer = if (nextIndex == defaultIndex) "" else rendererIds[nextIndex]
                    onRendererChange(nextIndex)
                }
            )
            var selectedDriver by remember { mutableStateOf(config?.getDriver() ?: "") }
            ArrowPreference(
                title = stringResource(R.string.setting_driver_title),
                summary = selectedDriver.ifEmpty { stringResource(R.string.generic_default) },
                onClick = {
                    val names = driverNames.toMutableList()
                    names.add(context.getString(R.string.generic_default))
                    val defaultIndex = names.size - 1
                    val currentIndex = driverNames.indexOf(selectedDriver).coerceAtLeast(0)
                    val nextIndex = if (currentIndex >= driverNames.size - 1) defaultIndex else currentIndex + 1
                    selectedDriver = if (nextIndex == defaultIndex) "" else driverNames[nextIndex]
                    onDriverChange(nextIndex)
                }
            )
        }

        // Runtime
        SmallTitle(text = stringResource(R.string.setting_category_java_tweaks))
        Card(modifier = Modifier.padding(horizontal = 12.dp)) {
            var selectedRuntime by remember { mutableStateOf(config?.getJavaDir() ?: "") }
            ArrowPreference(
                title = stringResource(R.string.setting_java_select_runtime_title),
                summary = runtimeNames.lastOrNull()?.let { stringResource(R.string.install_auto_select) } ?: "",
                onClick = {
                    // Cycle through runtimes
                    onRuntimeChange(0)
                }
            )
        }

        // Custom Info & JVM Args
        SmallTitle(text = "Info & Args")
        Card(modifier = Modifier.padding(horizontal = 12.dp)) {
            var customInfo by remember { mutableStateOf(config?.getCustomInfo() ?: "") }
            ArrowPreference(
                title = stringResource(R.string.setting_version_custom_info_title),
                summary = customInfo.ifEmpty { stringResource(R.string.setting_version_custom_info_desc) },
                onClick = { /* Handled by EditTextDialog in fragment */ }
            )
            var javaArgs by remember { mutableStateOf(config?.getJavaArgs() ?: "") }
            ArrowPreference(
                title = stringResource(R.string.setting_java_args_title),
                summary = javaArgs.ifEmpty { stringResource(R.string.setting_java_args_desc) },
                onClick = { /* Handled by EditTextDialog in fragment */ }
            )
        }

        // Save / Cancel buttons
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onSave,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            Text(text = stringResource(R.string.generic_save))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            Text(text = stringResource(R.string.generic_cancel))
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}
