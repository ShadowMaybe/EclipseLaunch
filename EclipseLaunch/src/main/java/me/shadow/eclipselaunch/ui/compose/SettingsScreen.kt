package me.shadow.eclipselaunch.ui.compose

import android.app.AlertDialog
import android.widget.EditText
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import me.shadow.eclipselaunch.setting.AllSettings
import me.shadow.eclipselaunch.utils.CleanUpCache
import me.shadow.eclipselaunch.utils.ZHTools
import me.shadow.eclipselaunch.utils.path.UrlManager
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.TabRow
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.preference.SliderPreference
import top.yukonga.miuix.kmp.preference.SwitchPreference

@Composable
fun SettingsScreen(
    onNavigateToCustomBackground: () -> Unit = {},
    onNavigateToCustomMouse: () -> Unit = {},
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.setting_category_video),
        stringResource(R.string.setting_category_control),
        stringResource(R.string.setting_category_game),
        stringResource(R.string.setting_category_launcher),
        stringResource(R.string.setting_category_experimental)
    )
    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            tabs = tabs,
            selectedTabIndex = selectedTab,
            onTabSelected = { selectedTab = it },
            modifier = Modifier.padding(bottom = 4.dp)
        )
        when (selectedTab) {
            0 -> VideoSettingsContent()
            1 -> ControlSettingsContent(onNavigateToCustomMouse)
            2 -> GameSettingsContent()
            3 -> LauncherSettingsContent(onNavigateToCustomBackground)
            4 -> ExperimentalSettingsContent()
        }
    }
}

@Composable
private fun SettingsScrollContent(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 12.dp, horizontal = 12.dp)
    ) { content() }
}

/** Show a single-choice list dialog from Compose */
private fun showListDialog(
    context: android.content.Context,
    title: String,
    entries: Array<String>,
    entryValues: Array<String>,
    currentValue: String,
    onSelect: (String) -> Unit
) {
    val index = entryValues.indexOf(currentValue).coerceAtLeast(0)
    AlertDialog.Builder(context)
        .setTitle(title)
        .setSingleChoiceItems(entries, index) { dialog, which ->
            onSelect(entryValues[which])
            dialog.dismiss()
        }
        .setNegativeButton(android.R.string.cancel, null)
        .show()
}

/** Show an EditText dialog from Compose */
private fun showEditDialog(
    context: android.content.Context,
    title: String,
    currentValue: String,
    hint: String = "",
    onConfirm: (String) -> Unit
) {
    val et = EditText(context).apply {
        setText(currentValue)
        this.hint = hint
    }
    AlertDialog.Builder(context)
        .setTitle(title)
        .setView(et)
        .setPositiveButton(R.string.generic_confirm) { _, _ -> onConfirm(et.text.toString()) }
        .setNegativeButton(android.R.string.cancel, null)
        .show()
}

// ═══════════════════════════════════════════════════════
// VIDEO SETTINGS — matches settings_fragment_video.xml
// ONE card: video_category
// ═══════════════════════════════════════════════════════
@Composable
private fun VideoSettingsContent() {
    val context = LocalContext.current
    SettingsScrollContent {
        SmallTitle(text = stringResource(R.string.setting_category_video))
        Card(modifier = Modifier.padding(horizontal = 0.dp)) {
            // Renderer
            var renderer by remember { mutableStateOf(AllSettings.renderer.getValue()) }
            val renderers = remember { Renderers.getCompatibleRenderers(context).first }
            ArrowPreference(
                title = stringResource(R.string.setting_renderer_title),
                summary = renderer.ifEmpty { stringResource(R.string.generic_default) },
                onClick = {
                    val names = renderers.rendererNames.toTypedArray()
                    val ids = renderers.rendererIdentifier.toTypedArray()
                    showListDialog(context, context.getString(R.string.setting_renderer_title),
                        names, ids, renderer) { selected ->
                        AllSettings.renderer.put(selected).save()
                        renderer = selected
                    }
                }
            )
            // Renderer Download
            ArrowPreference(
                title = stringResource(R.string.setting_category_download),
                summary = null,
                onClick = { ZHTools.openLink(context, UrlManager.URL_FCL_RENDERER_PLUGIN) }
            )
            // Renderer Local Import
            ArrowPreference(
                title = stringResource(R.string.setting_renderer_local_import_title),
                summary = stringResource(R.string.setting_renderer_local_import_desc),
                onClick = { /* requires ActivityResultLauncher */ }
            )
            // Driver
            var driver by remember { mutableStateOf(AllSettings.driver.getValue()) }
            val driverNames = remember { DriverPluginManager.getDriverNameList().toTypedArray() }
            ArrowPreference(
                title = stringResource(R.string.setting_driver_title),
                summary = driver.ifEmpty { stringResource(R.string.generic_default) },
                onClick = {
                    showListDialog(context, context.getString(R.string.setting_driver_title),
                        driverNames, driverNames, driver) { selected ->
                        AllSettings.driver.put(selected).save()
                        driver = selected
                    }
                }
            )
            // Driver Download
            ArrowPreference(
                title = stringResource(R.string.setting_category_download),
                summary = null,
                onClick = { ZHTools.openLink(context, UrlManager.URL_FCL_DRIVER_PLUGIN) }
            )
            // Ignore Notch
            var ignoreNotch by remember { mutableStateOf(AllSettings.ignoreNotch.getValue()) }
            SwitchPreference(
                checked = ignoreNotch,
                onCheckedChange = { ignoreNotch = it; AllSettings.ignoreNotch.put(it).save() },
                title = stringResource(R.string.setting_ignore_notch_title),
                summary = stringResource(R.string.setting_ignore_notch_desc)
            )
            // Ignore Notch Launcher
            var ignoreNotchLauncher by remember { mutableStateOf(AllSettings.ignoreNotchLauncher.getValue()) }
            SwitchPreference(
                checked = ignoreNotchLauncher,
                onCheckedChange = { ignoreNotchLauncher = it; AllSettings.ignoreNotchLauncher.put(it).save() },
                title = stringResource(R.string.setting_launcher_ignore_notch_title),
                summary = stringResource(R.string.setting_launcher_ignore_notch_desc)
            )
            // Resolution Ratio (XML: min=25, max=300)
            var resolutionRatio by remember { mutableStateOf(AllSettings.resolutionRatio.getValue().toFloat()) }
            SliderPreference(
                value = resolutionRatio,
                onValueChange = { resolutionRatio = it },
                onValueChangeFinished = { AllSettings.resolutionRatio.put(resolutionRatio.toInt()).save() },
                title = stringResource(R.string.setting_resolution_scaler_title),
                summary = "${resolutionRatio.toInt()}%",
                valueRange = 25f..300f
            )
            // Sustained Performance
            var sustainedPerformance by remember { mutableStateOf(AllSettings.sustainedPerformance.getValue()) }
            SwitchPreference(
                checked = sustainedPerformance,
                onCheckedChange = { sustainedPerformance = it; AllSettings.sustainedPerformance.put(it).save() },
                title = stringResource(R.string.setting_sustained_performance_title),
                summary = stringResource(R.string.setting_limit_overheating_throttling)
            )
            // Alternate Surface
            var alternateSurface by remember { mutableStateOf(AllSettings.alternateSurface.getValue()) }
            SwitchPreference(
                checked = alternateSurface,
                onCheckedChange = { alternateSurface = it; AllSettings.alternateSurface.put(it).save() },
                title = stringResource(R.string.setting_use_surface_view_title),
                summary = stringResource(R.string.setting_use_surface_view_desc)
            )
            // Force Vsync — only when alternateSurface is on
            if (alternateSurface) {
                var forceVsync by remember { mutableStateOf(AllSettings.forceVsync.getValue()) }
                SwitchPreference(
                    checked = forceVsync,
                    onCheckedChange = { forceVsync = it; AllSettings.forceVsync.put(it).save() },
                    title = stringResource(R.string.setting_force_vsync_title),
                    summary = stringResource(R.string.setting_limit_overheating_throttling)
                )
            }
            // VSync in Zink
            var vsyncInZink by remember { mutableStateOf(AllSettings.vsyncInZink.getValue()) }
            SwitchPreference(
                checked = vsyncInZink,
                onCheckedChange = { vsyncInZink = it; AllSettings.vsyncInZink.put(it).save() },
                title = stringResource(R.string.setting_vsync_in_zink_title),
                summary = stringResource(R.string.setting_vsync_in_zink_desc)
            )
            // Zink Prefer System Driver
            var zinkPreferSystemDriver by remember { mutableStateOf(AllSettings.zinkPreferSystemDriver.getValue()) }
            SwitchPreference(
                checked = zinkPreferSystemDriver,
                onCheckedChange = { zinkPreferSystemDriver = it; AllSettings.zinkPreferSystemDriver.put(it).save() },
                title = stringResource(R.string.setting_vulkan_driver_system_title),
                summary = stringResource(R.string.setting_vulkan_driver_system_desc)
            )
        }
    }
}

// ═══════════════════════════════════════════════════════
// CONTROL SETTINGS — matches settings_fragment_control.xml
// Cards: custom_controls_category, controls_category,
//        mouse_category, enableGyro_category, controller_category
// ═══════════════════════════════════════════════════════
@Composable
private fun ControlSettingsContent(onNavigateToCustomMouse: () -> Unit = {}) {
    val context = LocalContext.current
    SettingsScrollContent {
        // --- custom_controls_category ---
        SmallTitle(text = stringResource(R.string.setting_category_control))
        Card(modifier = Modifier.padding(horizontal = 0.dp)) {
            var disableGestures by remember { mutableStateOf(AllSettings.disableGestures.getValue()) }
            SwitchPreference(
                checked = disableGestures,
                onCheckedChange = { disableGestures = it; AllSettings.disableGestures.put(it).save() },
                title = stringResource(R.string.setting_disable_gestures_title),
                summary = stringResource(R.string.setting_disable_gestures_desc)
            )
            var disableDoubleTap by remember { mutableStateOf(AllSettings.disableDoubleTap.getValue()) }
            SwitchPreference(
                checked = disableDoubleTap,
                onCheckedChange = { disableDoubleTap = it; AllSettings.disableDoubleTap.put(it).save() },
                title = stringResource(R.string.setting_disable_swap_hand_title),
                summary = stringResource(R.string.setting_disable_swap_hand_desc)
            )
            // Time Long Press Trigger — always visible in XML (slider 100-1000)
            var timeLongPress by remember { mutableStateOf(AllSettings.timeLongPressTrigger.getValue().toFloat()) }
            SliderPreference(
                value = timeLongPress,
                onValueChange = { timeLongPress = it },
                onValueChangeFinished = { AllSettings.timeLongPressTrigger.put(timeLongPress.toInt()).save() },
                title = stringResource(R.string.setting_longpress_trigger_title),
                summary = "${timeLongPress.toInt()}ms",
                valueRange = 100f..1000f
            )
        }

        // --- controls_category ---
        SmallTitle(text = stringResource(R.string.pedit_control))
        Card(modifier = Modifier.padding(horizontal = 0.dp)) {
            var buttonScale by remember { mutableStateOf(AllSettings.buttonScale.getValue().toFloat()) }
            SliderPreference(
                value = buttonScale,
                onValueChange = { buttonScale = it },
                onValueChangeFinished = { AllSettings.buttonScale.put(buttonScale.toInt()).save() },
                title = stringResource(R.string.setting_button_scale_title),
                summary = "${buttonScale.toInt()}%",
                valueRange = 80f..250f
            )
            var buttonAllCaps by remember { mutableStateOf(AllSettings.buttonAllCaps.getValue()) }
            SwitchPreference(
                checked = buttonAllCaps,
                onCheckedChange = { buttonAllCaps = it; AllSettings.buttonAllCaps.put(it).save() },
                title = stringResource(R.string.setting_button_allcaps_title),
                summary = stringResource(R.string.setting_button_allcaps_desc)
            )
        }

        // --- mouse_category ---
        SmallTitle(text = stringResource(R.string.setting_category_virtual_mouse))
        Card(modifier = Modifier.padding(horizontal = 0.dp)) {
            var mouseScale by remember { mutableStateOf(AllSettings.mouseScale.getValue().toFloat()) }
            SliderPreference(
                value = mouseScale,
                onValueChange = { mouseScale = it },
                onValueChangeFinished = { AllSettings.mouseScale.put(mouseScale.toInt()).save() },
                title = stringResource(R.string.mouse_settings_scale_name),
                summary = "${mouseScale.toInt()}%",
                valueRange = 25f..300f
            )
            var mouseSpeed by remember { mutableStateOf(AllSettings.mouseSpeed.getValue().toFloat()) }
            SliderPreference(
                value = mouseSpeed,
                onValueChange = { mouseSpeed = it },
                onValueChangeFinished = { AllSettings.mouseSpeed.put(mouseSpeed.toInt()).save() },
                title = stringResource(R.string.mouse_settings_speed_name),
                summary = "${mouseSpeed.toInt()}%",
                valueRange = 25f..300f
            )
            var virtualMouseStart by remember { mutableStateOf(AllSettings.virtualMouseStart.getValue()) }
            SwitchPreference(
                checked = virtualMouseStart,
                onCheckedChange = { virtualMouseStart = it; AllSettings.virtualMouseStart.put(it).save() },
                title = stringResource(R.string.setting_mouse_start_title),
                summary = stringResource(R.string.setting_mouse_start_desc)
            )
            ArrowPreference(
                title = stringResource(R.string.custom_mouse_title),
                summary = stringResource(R.string.custom_mouse_desc),
                onClick = onNavigateToCustomMouse
            )
        }

        // --- enableGyro_category ---
        SmallTitle(text = stringResource(R.string.setting_category_gyro_controls))
        Card(modifier = Modifier.padding(horizontal = 0.dp)) {
            var enableGyro by remember { mutableStateOf(AllSettings.enableGyro.getValue()) }
            SwitchPreference(
                checked = enableGyro,
                onCheckedChange = { enableGyro = it; AllSettings.enableGyro.put(it).save() },
                title = stringResource(R.string.setting_enable_gyro_title),
                summary = stringResource(R.string.setting_enable_gyro_desc)
            )
            var gyroSensitivity by remember { mutableStateOf(AllSettings.gyroSensitivity.getValue().toFloat()) }
            SliderPreference(
                value = gyroSensitivity,
                onValueChange = { gyroSensitivity = it },
                onValueChangeFinished = { AllSettings.gyroSensitivity.put(gyroSensitivity.toInt()).save() },
                title = stringResource(R.string.setting_gyro_sensitivity_title),
                summary = "${gyroSensitivity.toInt()}%",
                valueRange = 25f..300f
            )
            var gyroSampleRate by remember { mutableStateOf(AllSettings.gyroSampleRate.getValue().toFloat()) }
            SliderPreference(
                value = gyroSampleRate,
                onValueChange = { gyroSampleRate = it },
                onValueChangeFinished = { AllSettings.gyroSampleRate.put(gyroSampleRate.toInt()).save() },
                title = stringResource(R.string.setting_gyro_sample_rate_title),
                summary = "${gyroSampleRate.toInt()}ms",
                valueRange = 5f..50f
            )
            var gyroSmoothing by remember { mutableStateOf(AllSettings.gyroSmoothing.getValue()) }
            SwitchPreference(
                checked = gyroSmoothing,
                onCheckedChange = { gyroSmoothing = it; AllSettings.gyroSmoothing.put(it).save() },
                title = stringResource(R.string.setting_gyro_smoothing_title),
                summary = stringResource(R.string.setting_gyro_smoothing_desc)
            )
            var gyroInvertX by remember { mutableStateOf(AllSettings.gyroInvertX.getValue()) }
            SwitchPreference(
                checked = gyroInvertX,
                onCheckedChange = { gyroInvertX = it; AllSettings.gyroInvertX.put(it).save() },
                title = stringResource(R.string.setting_gyro_invert_x_axis),
                summary = stringResource(R.string.setting_gyro_invert_x_axis_description)
            )
            var gyroInvertY by remember { mutableStateOf(AllSettings.gyroInvertY.getValue()) }
            SwitchPreference(
                checked = gyroInvertY,
                onCheckedChange = { gyroInvertY = it; AllSettings.gyroInvertY.put(it).save() },
                title = stringResource(R.string.setting_gyro_invert_y_axis),
                summary = stringResource(R.string.setting_gyro_invert_y_axis_description)
            )
        }

        // --- controller_category ---
        SmallTitle(text = stringResource(R.string.setting_category_controller_settings))
        Card(modifier = Modifier.padding(horizontal = 0.dp)) {
            ArrowPreference(
                title = stringResource(R.string.setting_remap_controller_title),
                summary = stringResource(R.string.setting_remap_controller_desc),
                onClick = { /* navigate to GamepadMapperFragment */ }
            )
            ArrowPreference(
                title = stringResource(R.string.setting_wipe_controller_title),
                summary = stringResource(R.string.setting_wipe_controller_desc),
                onClick = {
                    fr.spse.gamepad_remapper.Remapper.wipePreferences(context)
                    Toast.makeText(context, R.string.setting_controller_map_wiped, Toast.LENGTH_SHORT).show()
                }
            )
            var deadZoneScale by remember { mutableStateOf(AllSettings.deadZoneScale.getValue().toFloat()) }
            SliderPreference(
                value = deadZoneScale,
                onValueChange = { deadZoneScale = it },
                onValueChangeFinished = { AllSettings.deadZoneScale.put(deadZoneScale.toInt()).save() },
                title = stringResource(R.string.setting_deadzone_scale_title),
                summary = "${deadZoneScale.toInt()}%",
                valueRange = 50f..200f
            )
        }
    }
}

// ═══════════════════════════════════════════════════════
// GAME SETTINGS — matches settings_fragment_game.xml
// Cards: version_category, language_category,
//        java_category, game_menu_category
// ═══════════════════════════════════════════════════════
@Composable
private fun GameSettingsContent() {
    val context = LocalContext.current
    SettingsScrollContent {
        // --- version_category ---
        SmallTitle(text = stringResource(R.string.setting_category_version))
        Card(modifier = Modifier.padding(horizontal = 0.dp)) {
            var versionIsolation by remember { mutableStateOf(AllSettings.versionIsolation.getValue()) }
            SwitchPreference(
                checked = versionIsolation,
                onCheckedChange = { versionIsolation = it; AllSettings.versionIsolation.put(it).save() },
                title = stringResource(R.string.setting_version_isolation_title),
                summary = stringResource(R.string.setting_version_isolation_desc)
            )
            var versionCustomInfo by remember { mutableStateOf(AllSettings.versionCustomInfo.getValue()) }
            ArrowPreference(
                title = stringResource(R.string.setting_version_custom_info_title),
                summary = versionCustomInfo.ifEmpty { stringResource(R.string.setting_version_custom_info_desc) },
                onClick = {
                    showEditDialog(context, context.getString(R.string.setting_version_custom_info_title),
                        versionCustomInfo, context.getString(R.string.setting_version_custom_info_desc)) { text ->
                        AllSettings.versionCustomInfo.put(text).save()
                        versionCustomInfo = text
                    }
                }
            )
        }

        // --- language_category ---
        SmallTitle(text = stringResource(R.string.setting_category_language))
        Card(modifier = Modifier.padding(horizontal = 0.dp)) {
            var autoSetGameLanguage by remember { mutableStateOf(AllSettings.autoSetGameLanguage.getValue()) }
            SwitchPreference(
                checked = autoSetGameLanguage,
                onCheckedChange = { autoSetGameLanguage = it; AllSettings.autoSetGameLanguage.put(it).save() },
                title = stringResource(R.string.setting_set_game_language_title),
                summary = stringResource(R.string.setting_set_game_language_desc)
            )
            var gameLanguageOverridden by remember { mutableStateOf(AllSettings.gameLanguageOverridden.getValue()) }
            SwitchPreference(
                checked = gameLanguageOverridden,
                onCheckedChange = { gameLanguageOverridden = it; AllSettings.gameLanguageOverridden.put(it).save() },
                title = stringResource(R.string.setting_set_game_language_overridden_title),
                summary = stringResource(R.string.setting_set_game_language_overridden_desc)
            )
            var currentLang by remember { mutableStateOf(AllSettings.setGameLanguage.getValue()) }
            val langNames = context.resources.getStringArray(R.array.all_game_language)
            val langValues = context.resources.getStringArray(R.array.all_game_language_value)
            ArrowPreference(
                title = stringResource(R.string.setting_set_game_language_list),
                summary = langNames.getOrElse(langValues.indexOf(currentLang).coerceAtLeast(0)) { currentLang },
                onClick = {
                    showListDialog(context, context.getString(R.string.setting_set_game_language_list),
                        langNames, langValues, currentLang) { selected ->
                        AllSettings.setGameLanguage.put(selected).save()
                        currentLang = selected
                    }
                }
            )
        }

        // --- java_category ---
        SmallTitle(text = stringResource(R.string.setting_category_java_tweaks))
        Card(modifier = Modifier.padding(horizontal = 0.dp)) {
            ArrowPreference(
                title = stringResource(R.string.setting_java_multirt_title),
                summary = stringResource(R.string.setting_java_multirt_desc),
                onClick = { /* MultiRTConfigDialog */ }
            )
            var currentRt by remember { mutableStateOf(AllSettings.selectRuntimeMode.getValue()) }
            val rtNames = context.resources.getStringArray(R.array.select_java_runtime_names)
            val rtValues = context.resources.getStringArray(R.array.select_java_runtime_values)
            ArrowPreference(
                title = stringResource(R.string.setting_java_select_runtime_title),
                summary = rtNames.getOrElse(rtValues.indexOf(currentRt).coerceAtLeast(0)) { currentRt },
                onClick = {
                    showListDialog(context, context.getString(R.string.setting_java_select_runtime_title),
                        rtNames, rtValues, currentRt) { selected ->
                        AllSettings.selectRuntimeMode.put(selected).save()
                        currentRt = selected
                    }
                }
            )
            var javaArgs by remember { mutableStateOf(AllSettings.javaArgs.getValue()) }
            ArrowPreference(
                title = stringResource(R.string.setting_java_args_title),
                summary = javaArgs.ifEmpty { stringResource(R.string.setting_java_args_desc) },
                onClick = {
                    showEditDialog(context, context.getString(R.string.setting_java_args_title),
                        javaArgs, context.getString(R.string.setting_java_args_desc)) { text ->
                        AllSettings.javaArgs.put(text).save()
                        javaArgs = text
                    }
                }
            )
            // RAM Allocation — XML: min=256, no max (goes to device capacity)
            var ramAllocation by remember { mutableStateOf(AllSettings.ramAllocation.value.getValue().toFloat()) }
            SliderPreference(
                value = ramAllocation,
                onValueChange = { ramAllocation = it },
                onValueChangeFinished = { AllSettings.ramAllocation.value.put(ramAllocation.toInt()).save() },
                title = stringResource(R.string.setting_java_memory_title),
                summary = "${ramAllocation.toInt()}MB",
                valueRange = 256f..4096f
            )
            var javaSandbox by remember { mutableStateOf(AllSettings.javaSandbox.getValue()) }
            SwitchPreference(
                checked = javaSandbox,
                onCheckedChange = { javaSandbox = it; AllSettings.javaSandbox.put(it).save() },
                title = stringResource(R.string.setting_java_sandbox_title),
                summary = stringResource(R.string.setting_java_sandbox_desc)
            )
        }

        // --- game_menu_category ---
        SmallTitle(text = stringResource(R.string.setting_category_game_menu))
        Card(modifier = Modifier.padding(horizontal = 0.dp)) {
            var gameMenuShowMemory by remember { mutableStateOf(AllSettings.gameMenuShowMemory.getValue()) }
            SwitchPreference(
                checked = gameMenuShowMemory,
                onCheckedChange = { gameMenuShowMemory = it; AllSettings.gameMenuShowMemory.put(it).save() },
                title = stringResource(R.string.setting_game_menu_show_memory_title),
                summary = stringResource(R.string.setting_game_menu_show_memory_desc)
            )
            var gameMenuShowFPS by remember { mutableStateOf(AllSettings.gameMenuShowFPS.getValue()) }
            SwitchPreference(
                checked = gameMenuShowFPS,
                onCheckedChange = { gameMenuShowFPS = it; AllSettings.gameMenuShowFPS.put(it).save() },
                title = stringResource(R.string.setting_game_menu_show_fps_title),
                summary = stringResource(R.string.setting_game_menu_show_fps_desc)
            )
            var gameMenuMemoryText by remember { mutableStateOf(AllSettings.gameMenuMemoryText.getValue()) }
            ArrowPreference(
                title = stringResource(R.string.setting_game_menu_memory_text_title),
                summary = gameMenuMemoryText,
                onClick = {
                    showEditDialog(context, context.getString(R.string.setting_game_menu_memory_text_title),
                        gameMenuMemoryText, "M:") { text ->
                        AllSettings.gameMenuMemoryText.put(text).save()
                        gameMenuMemoryText = text
                    }
                }
            )
            var currentLoc by remember { mutableStateOf(AllSettings.gameMenuLocation.getValue()) }
            val locNames = context.resources.getStringArray(R.array.game_menu_location_names)
            val locValues = context.resources.getStringArray(R.array.game_menu_location_values)
            ArrowPreference(
                title = stringResource(R.string.setting_game_menu_location_title),
                summary = locNames.getOrElse(locValues.indexOf(currentLoc).coerceAtLeast(0)) { currentLoc },
                onClick = {
                    showListDialog(context, context.getString(R.string.setting_game_menu_location_title),
                        locNames, locValues, currentLoc) { selected ->
                        AllSettings.gameMenuLocation.put(selected).save()
                        currentLoc = selected
                    }
                }
            )
            // Game Menu Info Refresh Rate (XML: min=500, max=5000)
            var gameMenuInfoRefreshRate by remember { mutableStateOf(AllSettings.gameMenuInfoRefreshRate.getValue().toFloat()) }
            SliderPreference(
                value = gameMenuInfoRefreshRate,
                onValueChange = { gameMenuInfoRefreshRate = it },
                onValueChangeFinished = { AllSettings.gameMenuInfoRefreshRate.put(gameMenuInfoRefreshRate.toInt()).save() },
                title = stringResource(R.string.setting_game_menu_info_refresh_rate_title),
                summary = "${gameMenuInfoRefreshRate.toInt()}ms",
                valueRange = 500f..5000f
            )
            // Game Menu Alpha (XML: min=20, max=100)
            var gameMenuAlpha by remember { mutableStateOf(AllSettings.gameMenuAlpha.getValue().toFloat()) }
            SliderPreference(
                value = gameMenuAlpha,
                onValueChange = { gameMenuAlpha = it },
                onValueChangeFinished = { AllSettings.gameMenuAlpha.put(gameMenuAlpha.toInt()).save() },
                title = stringResource(R.string.setting_game_menu_alpha_title),
                summary = "${gameMenuAlpha.toInt()}%",
                valueRange = 20f..100f
            )
        }
    }
}

// ═══════════════════════════════════════════════════════
// LAUNCHER SETTINGS — matches settings_fragment_launcher.xml
// Cards: download_category, personalization_category, launcher_category
// ═══════════════════════════════════════════════════════
@Composable
private fun LauncherSettingsContent(onNavigateToCustomBackground: () -> Unit = {}) {
    val context = LocalContext.current
    SettingsScrollContent {
        // --- download_category (all download settings in ONE card) ---
        SmallTitle(text = stringResource(R.string.setting_category_download))
        Card(modifier = Modifier.padding(horizontal = 0.dp)) {
            var checkLibraries by remember { mutableStateOf(AllSettings.checkLibraries.getValue()) }
            SwitchPreference(
                checked = checkLibraries,
                onCheckedChange = { checkLibraries = it; AllSettings.checkLibraries.put(it).save() },
                title = stringResource(R.string.setting_check_libraries_title),
                summary = stringResource(R.string.setting_check_libraries_desc)
            )
            var verifyManifest by remember { mutableStateOf(AllSettings.verifyManifest.getValue()) }
            SwitchPreference(
                checked = verifyManifest,
                onCheckedChange = { verifyManifest = it; AllSettings.verifyManifest.put(it).save() },
                title = stringResource(R.string.setting_verify_manifest_title),
                summary = stringResource(R.string.setting_verify_manifest_desc)
            )
            var resourceImageCache by remember { mutableStateOf(AllSettings.resourceImageCache.getValue()) }
            SwitchPreference(
                checked = resourceImageCache,
                onCheckedChange = { resourceImageCache = it; AllSettings.resourceImageCache.put(it).save() },
                title = stringResource(R.string.setting_resource_image_cache_title),
                summary = stringResource(R.string.setting_resource_image_cache_desc)
            )
            var addFullResourceName by remember { mutableStateOf(AllSettings.addFullResourceName.getValue()) }
            SwitchPreference(
                checked = addFullResourceName,
                onCheckedChange = { addFullResourceName = it; AllSettings.addFullResourceName.put(it).save() },
                title = stringResource(R.string.setting_resource_full_name_title),
                summary = stringResource(R.string.setting_resource_full_name_desc)
            )
            // Download Source
            var currentDs by remember { mutableStateOf(AllSettings.downloadSource.getValue()) }
            val dsNames = context.resources.getStringArray(R.array.download_source_names)
            val dsValues = context.resources.getStringArray(R.array.download_source_values)
            ArrowPreference(
                title = stringResource(R.string.setting_download_source_title),
                summary = dsNames.getOrElse(dsValues.indexOf(currentDs).coerceAtLeast(0)) { currentDs },
                onClick = {
                    showListDialog(context, context.getString(R.string.setting_download_source_title),
                        dsNames, dsValues, currentDs) { selected ->
                        AllSettings.downloadSource.put(selected).save()
                        currentDs = selected
                    }
                }
            )
            // Max Download Threads (XML: min=1, max=128)
            var maxDownloadThreads by remember { mutableStateOf(AllSettings.maxDownloadThreads.getValue().toFloat()) }
            SliderPreference(
                value = maxDownloadThreads,
                onValueChange = { maxDownloadThreads = it },
                onValueChangeFinished = { AllSettings.maxDownloadThreads.put(maxDownloadThreads.toInt()).save() },
                title = stringResource(R.string.setting_max_download_threads_title),
                summary = "${maxDownloadThreads.toInt()}",
                valueRange = 1f..128f
            )
        }

        // --- personalization_category ---
        SmallTitle(text = stringResource(R.string.setting_category_personalization))
        Card(modifier = Modifier.padding(horizontal = 0.dp)) {
            var currentTh by remember { mutableStateOf(AllSettings.launcherTheme.getValue()) }
            val thNames = context.resources.getStringArray(R.array.launcher_theme_names)
            val thValues = context.resources.getStringArray(R.array.launcher_theme_values)
            ArrowPreference(
                title = stringResource(R.string.setting_launcher_theme),
                summary = thNames.getOrElse(thValues.indexOf(currentTh).coerceAtLeast(0)) { currentTh },
                onClick = {
                    showListDialog(context, context.getString(R.string.setting_launcher_theme),
                        thNames, thValues, currentTh) { selected ->
                        AllSettings.launcherTheme.put(selected).save()
                        currentTh = selected
                    }
                }
            )
            ArrowPreference(
                title = stringResource(R.string.custom_background_title),
                summary = stringResource(R.string.custom_background_desc),
                onClick = onNavigateToCustomBackground
            )
            var animation by remember { mutableStateOf(AllSettings.animation.getValue()) }
            SwitchPreference(
                checked = animation,
                onCheckedChange = { animation = it; AllSettings.animation.put(it).save() },
                title = stringResource(R.string.setting_animation_title),
                summary = stringResource(R.string.setting_animation_desc)
            )
            // Animation Speed (XML: min=300, max=1500)
            var animationSpeed by remember { mutableStateOf(AllSettings.animationSpeed.getValue().toFloat()) }
            SliderPreference(
                value = animationSpeed,
                onValueChange = { animationSpeed = it },
                onValueChangeFinished = { AllSettings.animationSpeed.put(animationSpeed.toInt()).save() },
                title = stringResource(R.string.setting_animation_speed_title),
                summary = "${animationSpeed.toInt()}ms",
                valueRange = 300f..1500f
            )
            // Page Opacity (XML: min=50, max=100)
            var pageOpacity by remember { mutableStateOf(AllSettings.pageOpacity.getValue().toFloat()) }
            SliderPreference(
                value = pageOpacity,
                onValueChange = { pageOpacity = it },
                onValueChangeFinished = { AllSettings.pageOpacity.put(pageOpacity.toInt()).save() },
                title = stringResource(R.string.setting_page_opacity_title),
                summary = "${pageOpacity.toInt()}%",
                valueRange = 50f..100f
            )
        }

        // --- launcher_category ---
        SmallTitle(text = stringResource(R.string.setting_category_launcher))
        Card(modifier = Modifier.padding(horizontal = 0.dp)) {
            var enableLogOutput by remember { mutableStateOf(AllSettings.enableLogOutput.getValue()) }
            SwitchPreference(
                checked = enableLogOutput,
                onCheckedChange = { enableLogOutput = it; AllSettings.enableLogOutput.put(it).save() },
                title = stringResource(R.string.setting_enable_log_output_title),
                summary = stringResource(R.string.setting_enable_log_output_desc)
            )
            var quitLauncher by remember { mutableStateOf(AllSettings.quitLauncher.getValue()) }
            SwitchPreference(
                checked = quitLauncher,
                onCheckedChange = { quitLauncher = it; AllSettings.quitLauncher.put(it).save() },
                title = stringResource(R.string.setting_quit_launcher_title),
                summary = stringResource(R.string.setting_quit_launcher_desc)
            )
            ArrowPreference(
                title = stringResource(R.string.clear_up_cache),
                summary = stringResource(R.string.clear_up_cache_desc),
                onClick = { CleanUpCache.start(context) }
            )
            ArrowPreference(
                title = stringResource(R.string.curseforge_api_key_title),
                summary = stringResource(R.string.curseforge_api_key_desc),
                onClick = {
                    showEditDialog(context, context.getString(R.string.curseforge_api_key_title),
                        AllSettings.curseforgeApiKey.getValue().ifEmpty {
                            me.shadow.eclipselaunch.InfoDistributor.CURSEFORGE_API_KEY
                        },
                        context.getString(R.string.curseforge_api_key_hint)) { text ->
                        AllSettings.curseforgeApiKey.put(text.trim()).save()
                        Toast.makeText(context, "CurseForge API key saved", Toast.LENGTH_SHORT).show()
                    }
                }
            )
            ArrowPreference(
                title = stringResource(R.string.update),
                summary = stringResource(R.string.update_summary),
                onClick = { Toast.makeText(context, "Update checking is disabled", Toast.LENGTH_SHORT).show() }
            )
            var notificationPermissionRequest by remember { mutableStateOf(AllSettings.notificationPermissionRequest.getValue()) }
            SwitchPreference(
                checked = notificationPermissionRequest,
                onCheckedChange = { notificationPermissionRequest = it; AllSettings.notificationPermissionRequest.put(it).save() },
                title = stringResource(R.string.setting_ask_for_notification_title),
                summary = stringResource(R.string.setting_ask_for_notification_desc)
            )
        }
    }
}

// ═══════════════════════════════════════════════════════
// EXPERIMENTAL SETTINGS — matches settings_fragment_experimental.xml
// Cards: experimental_category, support_category
// ═══════════════════════════════════════════════════════
@Composable
private fun ExperimentalSettingsContent() {
    SettingsScrollContent {
        // --- experimental_category ---
        SmallTitle(text = stringResource(R.string.setting_category_experimental_patches))
        Card(modifier = Modifier.padding(horizontal = 0.dp)) {
            var dumpShaders by remember { mutableStateOf(AllSettings.dumpShaders.getValue()) }
            SwitchPreference(
                checked = dumpShaders,
                onCheckedChange = { dumpShaders = it; AllSettings.dumpShaders.put(it).save() },
                title = stringResource(R.string.setting_shader_dump_title),
                summary = stringResource(R.string.setting_shader_dump_desc)
            )
            var bigCoreAffinity by remember { mutableStateOf(AllSettings.bigCoreAffinity.getValue()) }
            SwitchPreference(
                checked = bigCoreAffinity,
                onCheckedChange = { bigCoreAffinity = it; AllSettings.bigCoreAffinity.put(it).save() },
                title = stringResource(R.string.setting_force_big_core_title),
                summary = stringResource(R.string.setting_force_big_core_desc)
            )
        }
        // --- support_category ---
        SmallTitle(text = stringResource(R.string.setting_category_support))
        Card(modifier = Modifier.padding(horizontal = 0.dp)) {
            // Touch Controller Vibrate Duration (XML: min=80, max=500)
            var tcVibrateDuration by remember { mutableStateOf(AllSettings.tcVibrateDuration.getValue().toFloat()) }
            SliderPreference(
                value = tcVibrateDuration,
                onValueChange = { tcVibrateDuration = it },
                onValueChangeFinished = { AllSettings.tcVibrateDuration.put(tcVibrateDuration.toInt()).save() },
                title = stringResource(R.string.setting_touch_controller_vibrate_duration_title),
                summary = "${tcVibrateDuration.toInt()}ms",
                valueRange = 80f..500f
            )
        }
    }
}
