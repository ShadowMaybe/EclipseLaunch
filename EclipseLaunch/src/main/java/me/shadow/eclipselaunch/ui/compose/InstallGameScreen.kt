package me.shadow.eclipselaunch.ui.compose

import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.shadow.eclipselaunch.R
import me.shadow.eclipselaunch.feature.version.install.Addon
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

data class AddonState(
    val addon: Addon,
    val selectedVersion: String? = null,
    val isIncompatible: Boolean = false,
    val incompatibleWith: List<String> = emptyList(),
    val isInstalled: Boolean = false
)

@Composable
fun InstallGameScreen(
    mcVersion: String,
    nameValue: String,
    addonStates: List<AddonState>,
    onNameChange: (String) -> Unit,
    onAddonClick: (Addon) -> Unit,
    onAddonRemove: (Addon) -> Unit,
    onInstall: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp)
    ) {
        // Version Name
        SmallTitle(text = stringResource(R.string.version_install_title))
        Card(modifier = Modifier.padding(horizontal = 12.dp)) {
            BasicComponent(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.version_install_name),
                summary = nameValue,
            )
        }

        // Addons
        SmallTitle(text = stringResource(R.string.version_install_addons))
        Card(modifier = Modifier.padding(horizontal = 12.dp)) {
            addonStates.forEach { state ->
                val title = state.addon.addonName
                val summary = when {
                    state.isIncompatible -> "Incompatible with ${state.incompatibleWith.joinToString(", ")}"
                    state.isInstalled -> state.selectedVersion
                    else -> null
                }
                val endIcon = when {
                    state.isInstalled -> Icons.Filled.Close
                    else -> Icons.Filled.KeyboardArrowRight
                }

                BasicComponent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !state.isIncompatible) {
                            if (state.isInstalled) {
                                onAddonRemove(state.addon)
                            } else {
                                onAddonClick(state.addon)
                            }
                        },
                    title = title,
                    summary = summary,
                    titleColor = if (state.isIncompatible) {
                        top.yukonga.miuix.kmp.basic.BasicComponentDefaults.titleColor(
                            color = Color.Gray
                        )
                    } else {
                        top.yukonga.miuix.kmp.basic.BasicComponentDefaults.titleColor()
                    },
                )
            }
        }

        // Install / Back
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onInstall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            Text(text = stringResource(R.string.generic_install))
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
