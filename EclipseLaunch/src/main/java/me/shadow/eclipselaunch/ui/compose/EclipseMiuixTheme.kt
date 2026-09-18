package me.shadow.eclipselaunch.ui.compose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme

/**
 * EclipseLauncher's Miuix theme wrapper.
 * Provides MIUI-style light/dark color schemes.
 */
@Composable
fun EclipseMiuixTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) darkColorScheme() else lightColorScheme()
    MiuixTheme(
        colors = colors,
        content = content
    )
}
