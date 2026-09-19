package me.shadow.eclipselaunch.ui.compose

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
import me.shadow.eclipselaunch.setting.AllSettings
import top.yukonga.miuix.kmp.basic.Slider
import top.yukonga.miuix.kmp.basic.TabRow
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.TextStyles

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
            .padding(vertical = 8.dp)
    ) {
        content()
    }
}

@Composable
private fun SettingsCategoryHeader(title: String) {
    Text(
        text = title,
        color = MiuixTheme.colorScheme.onSurfaceSecondary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

// --- Video Settings ---
@Composable
private fun VideoSettingsContent() {
    SettingsScrollContent {
        // Renderer
        SettingsCategoryHeader(stringResource(R.string.setting_category_video))
        var renderer by remember { mutableStateOf(AllSettings.renderer.getValue()) }
        ArrowPreference(
            title = stringResource(R.string.setting_renderer_title),
            summary = renderer,
            onClick = { /* TODO: renderer picker */ }
        )

        // Driver
        var driver by remember { mutableStateOf(AllSettings.driver.getValue()) }
        ArrowPreference(
            title = stringResource(R.string.setting_driver_title),
            summary = driver,
            onClick = { /* TODO: driver picker */ }
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

        // Resolution Ratio
        var resolutionRatio by remember { mutableStateOf(AllSettings.resolutionRatio.getValue().toFloat()) }
        Text(
            text = "${stringResource(R.string.setting_resolution_scaler_title)}: ${resolutionRatio.toInt()}%",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            color = MiuixTheme.colorScheme.onSurface
        )
        Slider(
            value = resolutionRatio,
            onValueChange = { resolutionRatio = it },
            onValueChangeFinished = { AllSettings.resolutionRatio.put(resolutionRatio.toInt()).save() },
            valueRange = 25f..100f,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // Sustained Performance
        var sustainedPerformance by remember { mutableStateOf(AllSettings.sustainedPerformance.getValue()) }
        SwitchPreference(
            checked = sustainedPerformance,
            onCheckedChange = { sustainedPerformance = it; AllSettings.sustainedPerformance.put(it).save() },
            title = stringResource(R.string.setting_sustained_performance_title)
        )

        // Alternate Surface (SurfaceView)
        var alternateSurface by remember { mutableStateOf(AllSettings.alternateSurface.getValue()) }
        SwitchPreference(
            checked = alternateSurface,
            onCheckedChange = { alternateSurface = it; AllSettings.alternateSurface.put(it).save() },
            title = stringResource(R.string.setting_use_surface_view_title),
            summary = stringResource(R.string.setting_use_surface_view_desc)
        )

        // Force Vsync
        var forceVsync by remember { mutableStateOf(AllSettings.forceVsync.getValue()) }
        SwitchPreference(
            checked = forceVsync,
            onCheckedChange = { forceVsync = it; AllSettings.forceVsync.put(it).save() },
            title = stringResource(R.string.setting_force_vsync_title),
            enabled = alternateSurface
        )

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

// --- Control Settings ---
@Composable
private fun ControlSettingsContent(onNavigateToCustomMouse: () -> Unit = {}) {
    SettingsScrollContent {
        SettingsCategoryHeader(stringResource(R.string.setting_category_control))

        // Disable Gestures
        var disableGestures by remember { mutableStateOf(AllSettings.disableGestures.getValue()) }
        SwitchPreference(
            checked = disableGestures,
            onCheckedChange = { disableGestures = it; AllSettings.disableGestures.put(it).save() },
            title = stringResource(R.string.setting_disable_gestures_title),
            summary = stringResource(R.string.setting_disable_gestures_desc)
        )

        // Time Long Press Trigger
        var timeLongPress by remember { mutableStateOf(AllSettings.timeLongPressTrigger.getValue().toFloat()) }
        Text(
            text = "${stringResource(R.string.setting_longpress_trigger_title)}: ${timeLongPress.toInt()}ms",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            color = MiuixTheme.colorScheme.onSurface
        )
        Slider(
            value = timeLongPress,
            onValueChange = { timeLongPress = it },
            onValueChangeFinished = { AllSettings.timeLongPressTrigger.put(timeLongPress.toInt()).save() },
            valueRange = 200f..2000f,
            modifier = Modifier.padding(horizontal = 16.dp),
            enabled = !disableGestures
        )

        // Button Scale
        var buttonScale by remember { mutableStateOf(AllSettings.buttonScale.getValue().toFloat()) }
        Text(
            text = "${stringResource(R.string.setting_button_scale_title)}: ${buttonScale.toInt()}%",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            color = MiuixTheme.colorScheme.onSurface
        )
        Slider(
            value = buttonScale,
            onValueChange = { buttonScale = it },
            onValueChangeFinished = { AllSettings.buttonScale.put(buttonScale.toInt()).save() },
            valueRange = 50f..200f,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // Button All Caps
        var buttonAllCaps by remember { mutableStateOf(AllSettings.buttonAllCaps.getValue()) }
        SwitchPreference(
            checked = buttonAllCaps,
            onCheckedChange = { buttonAllCaps = it; AllSettings.buttonAllCaps.put(it).save() },
            title = stringResource(R.string.setting_button_allcaps_title)
        )

        // Mouse category header
        SettingsCategoryHeader(stringResource(R.string.setting_category_virtual_mouse))

        // Mouse Scale
        var mouseScale by remember { mutableStateOf(AllSettings.mouseScale.getValue().toFloat()) }
        Text(
            text = "${stringResource(R.string.mouse_settings_scale_name)}: ${mouseScale.toInt()}%",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            color = MiuixTheme.colorScheme.onSurface
        )
        Slider(
            value = mouseScale,
            onValueChange = { mouseScale = it },
            onValueChangeFinished = { AllSettings.mouseScale.put(mouseScale.toInt()).save() },
            valueRange = 25f..400f,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // Mouse Speed
        var mouseSpeed by remember { mutableStateOf(AllSettings.mouseSpeed.getValue().toFloat()) }
        Text(
            text = "${stringResource(R.string.mouse_settings_speed_name)}: ${mouseSpeed.toInt()}%",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            color = MiuixTheme.colorScheme.onSurface
        )
        Slider(
            value = mouseSpeed,
            onValueChange = { mouseSpeed = it },
            onValueChangeFinished = { AllSettings.mouseSpeed.put(mouseSpeed.toInt()).save() },
            valueRange = 50f..400f,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // Virtual Mouse Start
        var virtualMouseStart by remember { mutableStateOf(AllSettings.virtualMouseStart.getValue()) }
        SwitchPreference(
            checked = virtualMouseStart,
            onCheckedChange = { virtualMouseStart = it; AllSettings.virtualMouseStart.put(it).save() },
            title = stringResource(R.string.setting_mouse_start_title),
            summary = stringResource(R.string.setting_mouse_start_desc)
        )

        // Custom Mouse
        ArrowPreference(
            title = stringResource(R.string.custom_mouse_title),
            summary = stringResource(R.string.custom_mouse_desc),
            onClick = onNavigateToCustomMouse
        )

        // Gyro category header
        SettingsCategoryHeader(stringResource(R.string.setting_category_gyro_controls))

        // Enable Gyro
        var enableGyro by remember { mutableStateOf(AllSettings.enableGyro.getValue()) }
        SwitchPreference(
            checked = enableGyro,
            onCheckedChange = { enableGyro = it; AllSettings.enableGyro.put(it).save() },
            title = stringResource(R.string.setting_enable_gyro_title),
            summary = stringResource(R.string.setting_enable_gyro_desc)
        )

        if (enableGyro) {
            // Gyro Sensitivity
            var gyroSensitivity by remember { mutableStateOf(AllSettings.gyroSensitivity.getValue().toFloat()) }
            Text(
                text = "${stringResource(R.string.setting_gyro_sensitivity_title)}: ${gyroSensitivity.toInt()}%",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                color = MiuixTheme.colorScheme.onSurface
            )
            Slider(
                value = gyroSensitivity,
                onValueChange = { gyroSensitivity = it },
                onValueChangeFinished = { AllSettings.gyroSensitivity.put(gyroSensitivity.toInt()).save() },
                valueRange = 10f..500f,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Gyro Sample Rate
            var gyroSampleRate by remember { mutableStateOf(AllSettings.gyroSampleRate.getValue().toFloat()) }
            Text(
                text = "${stringResource(R.string.setting_gyro_sample_rate_title)}: ${gyroSampleRate.toInt()}ms",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                color = MiuixTheme.colorScheme.onSurface
            )
            Slider(
                value = gyroSampleRate,
                onValueChange = { gyroSampleRate = it },
                onValueChangeFinished = { AllSettings.gyroSampleRate.put(gyroSampleRate.toInt()).save() },
                valueRange = 10f..100f,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Gyro Smoothing
            var gyroSmoothing by remember { mutableStateOf(AllSettings.gyroSmoothing.getValue()) }
            SwitchPreference(
                checked = gyroSmoothing,
                onCheckedChange = { gyroSmoothing = it; AllSettings.gyroSmoothing.put(it).save() },
                title = stringResource(R.string.setting_gyro_smoothing_title)
            )

            // Gyro Invert X
            var gyroInvertX by remember { mutableStateOf(AllSettings.gyroInvertX.getValue()) }
            SwitchPreference(
                checked = gyroInvertX,
                onCheckedChange = { gyroInvertX = it; AllSettings.gyroInvertX.put(it).save() },
                title = stringResource(R.string.setting_gyro_invert_x_axis),
                summary = stringResource(R.string.setting_gyro_invert_x_axis_description)
            )

            // Gyro Invert Y
            var gyroInvertY by remember { mutableStateOf(AllSettings.gyroInvertY.getValue()) }
            SwitchPreference(
                checked = gyroInvertY,
                onCheckedChange = { gyroInvertY = it; AllSettings.gyroInvertY.put(it).save() },
                title = stringResource(R.string.setting_gyro_invert_y_axis),
                summary = stringResource(R.string.setting_gyro_invert_y_axis_description)
            )
        }

        // Controller category
        SettingsCategoryHeader(stringResource(R.string.setting_category_controller_settings))

        // Gamepad Deadzone
        var deadZoneScale by remember { mutableStateOf(AllSettings.deadZoneScale.getValue().toFloat()) }
        Text(
            text = "${stringResource(R.string.setting_deadzone_scale_title)}: ${deadZoneScale.toInt()}%",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            color = MiuixTheme.colorScheme.onSurface
        )
        Slider(
            value = deadZoneScale,
            onValueChange = { deadZoneScale = it },
            onValueChangeFinished = { AllSettings.deadZoneScale.put(deadZoneScale.toInt()).save() },
            valueRange = 0f..100f,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

// --- Game Settings ---
@Composable
private fun GameSettingsContent() {
    SettingsScrollContent {
        SettingsCategoryHeader(stringResource(R.string.setting_category_version))

        // Version Isolation
        var versionIsolation by remember { mutableStateOf(AllSettings.versionIsolation.getValue()) }
        SwitchPreference(
            checked = versionIsolation,
            onCheckedChange = { versionIsolation = it; AllSettings.versionIsolation.put(it).save() },
            title = stringResource(R.string.setting_version_isolation_title),
            summary = stringResource(R.string.setting_version_isolation_desc)
        )

        SettingsCategoryHeader(stringResource(R.string.setting_category_language))

        // Game Language Overridden
        var gameLanguageOverridden by remember { mutableStateOf(AllSettings.gameLanguageOverridden.getValue()) }
        SwitchPreference(
            checked = gameLanguageOverridden,
            onCheckedChange = { gameLanguageOverridden = it; AllSettings.gameLanguageOverridden.put(it).save() },
            title = stringResource(R.string.setting_set_game_language_overridden_title),
            summary = stringResource(R.string.setting_set_game_language_overridden_desc)
        )

        SettingsCategoryHeader(stringResource(R.string.setting_category_java_tweaks))

        // Java Sandbox
        var javaSandbox by remember { mutableStateOf(AllSettings.javaSandbox.getValue()) }
        SwitchPreference(
            checked = javaSandbox,
            onCheckedChange = { javaSandbox = it; AllSettings.javaSandbox.put(it).save() },
            title = stringResource(R.string.setting_java_sandbox_title),
            summary = stringResource(R.string.setting_java_sandbox_desc)
        )

        // RAM Allocation
        var ramAllocation by remember { mutableStateOf(AllSettings.ramAllocation.value.getValue().toFloat()) }
        Text(
            text = "${stringResource(R.string.setting_java_memory_title)}: ${ramAllocation.toInt()}MB",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            color = MiuixTheme.colorScheme.onSurface
        )
        Slider(
            value = ramAllocation,
            onValueChange = { ramAllocation = it },
            onValueChangeFinished = { AllSettings.ramAllocation.value.put(ramAllocation.toInt()).save() },
            valueRange = 256f..4096f,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        SettingsCategoryHeader(stringResource(R.string.setting_category_game_menu))

        // Game Menu Show Memory
        var gameMenuShowMemory by remember { mutableStateOf(AllSettings.gameMenuShowMemory.getValue()) }
        SwitchPreference(
            checked = gameMenuShowMemory,
            onCheckedChange = { gameMenuShowMemory = it; AllSettings.gameMenuShowMemory.put(it).save() },
            title = stringResource(R.string.setting_game_menu_show_memory_title),
            summary = stringResource(R.string.setting_game_menu_show_memory_desc)
        )

        // Game Menu Show FPS
        var gameMenuShowFPS by remember { mutableStateOf(AllSettings.gameMenuShowFPS.getValue()) }
        SwitchPreference(
            checked = gameMenuShowFPS,
            onCheckedChange = { gameMenuShowFPS = it; AllSettings.gameMenuShowFPS.put(it).save() },
            title = stringResource(R.string.setting_game_menu_show_fps_title),
            summary = stringResource(R.string.setting_game_menu_show_fps_desc)
        )

        // Game Menu Alpha
        var gameMenuAlpha by remember { mutableStateOf(AllSettings.gameMenuAlpha.getValue().toFloat()) }
        Text(
            text = "${stringResource(R.string.setting_game_menu_alpha_title)}: ${gameMenuAlpha.toInt()}%",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            color = MiuixTheme.colorScheme.onSurface
        )
        Slider(
            value = gameMenuAlpha,
            onValueChange = { gameMenuAlpha = it },
            onValueChangeFinished = { AllSettings.gameMenuAlpha.put(gameMenuAlpha.toInt()).save() },
            valueRange = 0f..100f,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

// --- Launcher Settings ---
@Composable
private fun LauncherSettingsContent(onNavigateToCustomBackground: () -> Unit = {}) {
    SettingsScrollContent {
        SettingsCategoryHeader(stringResource(R.string.setting_category_launcher))

        // Check Libraries
        var checkLibraries by remember { mutableStateOf(AllSettings.checkLibraries.getValue()) }
        SwitchPreference(
            checked = checkLibraries,
            onCheckedChange = { checkLibraries = it; AllSettings.checkLibraries.put(it).save() },
            title = stringResource(R.string.setting_check_libraries_title),
            summary = stringResource(R.string.setting_check_libraries_desc)
        )

        // Verify Manifest
        var verifyManifest by remember { mutableStateOf(AllSettings.verifyManifest.getValue()) }
        SwitchPreference(
            checked = verifyManifest,
            onCheckedChange = { verifyManifest = it; AllSettings.verifyManifest.put(it).save() },
            title = stringResource(R.string.setting_verify_manifest_title),
            summary = stringResource(R.string.setting_verify_manifest_desc)
        )

        // Resource Image Cache
        var resourceImageCache by remember { mutableStateOf(AllSettings.resourceImageCache.getValue()) }
        SwitchPreference(
            checked = resourceImageCache,
            onCheckedChange = { resourceImageCache = it; AllSettings.resourceImageCache.put(it).save() },
            title = stringResource(R.string.setting_resource_image_cache_title),
            summary = stringResource(R.string.setting_resource_image_cache_desc)
        )

        // Add Full Resource Name
        var addFullResourceName by remember { mutableStateOf(AllSettings.addFullResourceName.getValue()) }
        SwitchPreference(
            checked = addFullResourceName,
            onCheckedChange = { addFullResourceName = it; AllSettings.addFullResourceName.put(it).save() },
            title = stringResource(R.string.setting_resource_full_name_title),
            summary = stringResource(R.string.setting_resource_full_name_desc)
        )

        SettingsCategoryHeader(stringResource(R.string.setting_category_personalization))

        // Animation
        var animation by remember { mutableStateOf(AllSettings.animation.getValue()) }
        SwitchPreference(
            checked = animation,
            onCheckedChange = { animation = it; AllSettings.animation.put(it).save() },
            title = stringResource(R.string.setting_animation_title),
            summary = stringResource(R.string.setting_animation_desc)
        )

        // Animation Speed
        var animationSpeed by remember { mutableStateOf(AllSettings.animationSpeed.getValue().toFloat()) }
        Text(
            text = "${stringResource(R.string.setting_animation_speed_title)}: ${animationSpeed.toInt()}ms",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            color = MiuixTheme.colorScheme.onSurface
        )
        Slider(
            value = animationSpeed,
            onValueChange = { animationSpeed = it },
            onValueChangeFinished = { AllSettings.animationSpeed.put(animationSpeed.toInt()).save() },
            valueRange = 100f..2000f,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // Page Opacity
        var pageOpacity by remember { mutableStateOf(AllSettings.pageOpacity.getValue().toFloat()) }
        Text(
            text = "${stringResource(R.string.setting_page_opacity_title)}: ${pageOpacity.toInt()}%",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            color = MiuixTheme.colorScheme.onSurface
        )
        Slider(
            value = pageOpacity,
            onValueChange = { pageOpacity = it },
            onValueChangeFinished = { AllSettings.pageOpacity.put(pageOpacity.toInt()).save() },
            valueRange = 30f..100f,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // Custom Background
        ArrowPreference(
            title = stringResource(R.string.custom_background_title),
            summary = stringResource(R.string.custom_background_desc),
            onClick = onNavigateToCustomBackground
        )

        // Enable Log Output
        var enableLogOutput by remember { mutableStateOf(AllSettings.enableLogOutput.getValue()) }
        SwitchPreference(
            checked = enableLogOutput,
            onCheckedChange = { enableLogOutput = it; AllSettings.enableLogOutput.put(it).save() },
            title = stringResource(R.string.setting_enable_log_output_title),
            summary = stringResource(R.string.setting_enable_log_output_desc)
        )

        // Quit Launcher
        var quitLauncher by remember { mutableStateOf(AllSettings.quitLauncher.getValue()) }
        SwitchPreference(
            checked = quitLauncher,
            onCheckedChange = { quitLauncher = it; AllSettings.quitLauncher.put(it).save() },
            title = stringResource(R.string.setting_quit_launcher_title),
            summary = stringResource(R.string.setting_quit_launcher_desc)
        )
    }
}

// --- Experimental Settings ---
@Composable
private fun ExperimentalSettingsContent() {
    SettingsScrollContent {
        SettingsCategoryHeader(stringResource(R.string.setting_category_experimental_patches))

        // Dump Shaders
        var dumpShaders by remember { mutableStateOf(AllSettings.dumpShaders.getValue()) }
        SwitchPreference(
            checked = dumpShaders,
            onCheckedChange = { dumpShaders = it; AllSettings.dumpShaders.put(it).save() },
            title = stringResource(R.string.setting_shader_dump_title),
            summary = stringResource(R.string.setting_shader_dump_desc)
        )

        // Big Core Affinity
        var bigCoreAffinity by remember { mutableStateOf(AllSettings.bigCoreAffinity.getValue()) }
        SwitchPreference(
            checked = bigCoreAffinity,
            onCheckedChange = { bigCoreAffinity = it; AllSettings.bigCoreAffinity.put(it).save() },
            title = stringResource(R.string.setting_force_big_core_title),
            summary = stringResource(R.string.setting_force_big_core_desc)
        )

        // Touch Calibration Vibrate Duration
        var tcVibrateDuration by remember { mutableStateOf(AllSettings.tcVibrateDuration.getValue().toFloat()) }
        Text(
            text = "${stringResource(R.string.setting_touch_controller_vibrate_duration_title)}: ${tcVibrateDuration.toInt()}ms",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            color = MiuixTheme.colorScheme.onSurface
        )
        Slider(
            value = tcVibrateDuration,
            onValueChange = { tcVibrateDuration = it },
            onValueChangeFinished = { AllSettings.tcVibrateDuration.put(tcVibrateDuration.toInt()).save() },
            valueRange = 0f..300f,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}
