package me.shadow.eclipselaunch.ui.compose

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import me.shadow.eclipselaunch.InfoCenter
import me.shadow.eclipselaunch.R
import me.shadow.eclipselaunch.utils.stringutils.StringUtils
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.theme.MiuixTheme

data class ContributorItem(
    val icon: Drawable?,
    val name: String,
    val description: String,
    val buttonLabel: String? = null,
    val buttonUrl: String? = null
)

@Composable
fun AboutScreen(
    contributors: List<ContributorItem>,
    onOpenLink: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MiuixTheme.colorScheme.background)
    ) {
        // Left side: scrollable content (~69% matching original guideline)
        LazyColumn(
            modifier = Modifier
                .weight(0.69f)
                .fillMaxHeight()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Title + descriptions card
            item {
                AboutInfoCard(context)
            }

            // Contributors card
            item {
                ContributorsSection(contributors, onOpenLink)
            }
        }

        // Right side: sidebar (~31%)
        AboutSidebar(context, onBack)
    }
}

@Composable
private fun AboutInfoCard(context: Context) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.defaultColors(
            color = MiuixTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title image
            Image(
                painter = painterResource(R.drawable.app_name_title),
                contentDescription = InfoCenter.replaceName(context, R.string.app_name),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                contentScale = ContentScale.FitCenter
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Description 1
            Text(
                text = InfoCenter.replaceName(context, R.string.about_dec1),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MiuixTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Description 2
            Text(
                text = InfoCenter.replaceName(context, R.string.about_dec2),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MiuixTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Description 3
            Text(
                text = InfoCenter.replaceName(context, R.string.about_dec3),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MiuixTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ContributorsSection(
    contributors: List<ContributorItem>,
    onOpenLink: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.defaultColors(
            color = MiuixTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Contributors header
            Text(
                text = stringResource(R.string.about_contributors),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MiuixTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Contributor items
            contributors.forEach { contributor ->
                ContributorItemCard(contributor, onOpenLink)
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun ContributorItemCard(
    contributor: ContributorItem,
    onOpenLink: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        contributor.icon?.let { drawable ->
            val bitmap = drawable.toBitmap(48, 48)
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = contributor.name,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(24.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(15.dp))

        // Name and description
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = contributor.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MiuixTheme.colorScheme.onSurface
            )
            Text(
                text = contributor.description,
                fontSize = 11.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MiuixTheme.colorScheme.onSurfaceSecondary
            )
        }

        // Action button
        contributor.buttonLabel?.let { label ->
            Spacer(modifier = Modifier.width(8.dp))
            TextButton(
                text = label,
                onClick = {
                    contributor.buttonUrl?.let { onOpenLink(it) }
                }
            )
        }
    }
}

@Composable
private fun AboutSidebar(
    context: Context,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(200.dp)
            .background(MiuixTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // App version info
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        val versionInfo = listOf(
                            "${context.getString(R.string.about_version_name)} ${me.shadow.eclipselaunch.utils.ZHTools.getVersionName()}",
                            "${context.getString(R.string.about_version_code)} ${me.shadow.eclipselaunch.utils.ZHTools.getVersionCode()}",
                            "${context.getString(R.string.about_last_update_time)} ${me.shadow.eclipselaunch.utils.ZHTools.getLastUpdateTime(context)}",
                            "${context.getString(R.string.about_version_status)} ${me.shadow.eclipselaunch.utils.ZHTools.getVersionStatus(context)}"
                        ).joinToString("\n")
                        StringUtils.copyText("text", versionInfo, context)
                    }
            ) {
                Text(
                    text = "${context.getString(R.string.about_version_name)} ${me.shadow.eclipselaunch.utils.ZHTools.getVersionName()}",
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = MiuixTheme.colorScheme.onSurface
                )
                Text(
                    text = "${context.getString(R.string.about_version_code)} ${me.shadow.eclipselaunch.utils.ZHTools.getVersionCode()}",
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = MiuixTheme.colorScheme.onSurface
                )
                Text(
                    text = "${context.getString(R.string.about_last_update_time)} ${me.shadow.eclipselaunch.utils.ZHTools.getLastUpdateTime(context)}",
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = MiuixTheme.colorScheme.onSurface
                )
                Text(
                    text = "${context.getString(R.string.about_version_status)} ${me.shadow.eclipselaunch.utils.ZHTools.getVersionStatus(context)}",
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = MiuixTheme.colorScheme.onSurface
                )
            }

            // Return button
            TextButton(
                text = context.getString(R.string.generic_return),
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
    }
}
