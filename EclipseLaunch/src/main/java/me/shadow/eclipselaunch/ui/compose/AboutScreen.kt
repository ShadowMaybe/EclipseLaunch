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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import me.shadow.eclipselaunch.InfoCenter
import me.shadow.eclipselaunch.InfoDistributor
import me.shadow.eclipselaunch.R
import me.shadow.eclipselaunch.utils.stringutils.StringUtils
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Text
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
        // Left side: scrollable content
        LazyColumn(
            modifier = Modifier
                .weight(0.7f)
                .fillMaxHeight()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Title card
            item {
                AboutTitleCard()
            }

            // Description card
            item {
                AboutDescriptionCard(context)
            }

            // Contributors card
            item {
                Text(
                    text = stringResource(R.string.about_contributors),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MiuixTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            items(contributors) { contributor ->
                ContributorCard(
                    contributor = contributor,
                    onOpenLink = onOpenLink
                )
            }
        }

        // Right side: sidebar
        AboutSidebar(
            context = context,
            onBack = onBack
        )
    }
}

@Composable
private fun AboutTitleCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MiuixTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App title image — use text as fallback
            Text(
                text = InfoDistributor.APP_NAME,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MiuixTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun AboutDescriptionCard(context: Context) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MiuixTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = InfoCenter.replaceName(context, R.string.about_dec1),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MiuixTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = InfoCenter.replaceName(context, R.string.about_dec2),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MiuixTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
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
private fun ContributorCard(
    contributor: ContributorItem,
    onOpenLink: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MiuixTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
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
                    color = MiuixTheme.colorScheme.onSurfaceVariant
                )
            }

            // Button
            contributor.buttonLabel?.let { label ->
                Spacer(modifier = Modifier.width(8.dp))
                top.yukonga.miuix.kmp.basic.TextButton(
                    text = label,
                    onClick = {
                        contributor.buttonUrl?.let { onOpenLink(it) }
                    },
                    modifier = Modifier.padding(0.dp)
                )
            }
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
            .width(200.dp)
            .fillMaxHeight()
            .background(MiuixTheme.colorScheme.surface)
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // App info
            Column(modifier = Modifier.padding(8.dp)) {
                val versionInfo = listOf(
                    "${context.getString(R.string.about_version_name)} ${me.shadow.eclipselaunch.utils.ZHTools.getVersionName()}",
                    "${context.getString(R.string.about_version_code)} ${me.shadow.eclipselaunch.utils.ZHTools.getVersionCode()}",
                    "${context.getString(R.string.about_last_update_time)} ${me.shadow.eclipselaunch.utils.ZHTools.getLastUpdateTime(context)}",
                    "${context.getString(R.string.about_version_status)} ${me.shadow.eclipselaunch.utils.ZHTools.getVersionStatus(context)}"
                ).joinToString("\n")

                Text(
                    text = versionInfo,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = MiuixTheme.colorScheme.onSurface,
                    modifier = Modifier.clickable {
                        StringUtils.copyText("text", versionInfo, context)
                    }
                )
            }

            // Return button
            top.yukonga.miuix.kmp.basic.TextButton(
                text = context.getString(R.string.generic_return),
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
    }
}
