package com.example.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.WorkspaceTab
import com.example.ui.components.GhickinMonogramMark
import com.example.ui.components.tactileScale
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MonoBadgeStyle

@Composable
fun GhickinNavRail(
    activeTab: WorkspaceTab,
    onSelectTab: (WorkspaceTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors

    Row(
        modifier = modifier
            .fillMaxHeight()
            .width(92.dp)
            .background(tokens.obsidianSurface)
            .testTag("workspace_nav_rail"),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            // Top GHICKIN architectural monogram mark
            val brandDesc = stringResource(R.string.nav_brand_monogram_desc)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .semantics { contentDescription = brandDesc },
            ) {
                GhickinMonogramMark(size = 40.dp)
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "GHICKIN",
                    style = MonoBadgeStyle,
                    color = tokens.inkMuted,
                    maxLines = 1,
                )
            }

            WorkspaceTab.entries.forEach { tab ->
                NavRailDestinationItem(
                    tab = tab,
                    selected = tab == activeTab,
                    onClick = { onSelectTab(tab) },
                )
            }
        }

        // Crisp 1dp structural vertical hairline divider separating rail from stage
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
                .background(tokens.hairlineRest),
        )
    }
}

@Composable
private fun NavRailDestinationItem(
    tab: WorkspaceTab,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val primary = MaterialTheme.colorScheme.primary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    val interactionSource = remember { MutableInteractionSource() }

    val pillWidth by animateDpAsState(
        targetValue = if (selected) 56.dp else 36.dp,
        animationSpec = spring(dampingRatio = 0.78f, stiffness = 420f),
        label = "railPillWidth_${tab.routeId}",
    )
    val pillColor by animateColorAsState(
        targetValue = if (selected) primaryContainer else Color.Transparent,
        animationSpec = spring(stiffness = 380f),
        label = "railPillColor_${tab.routeId}",
    )
    val iconColor by animateColorAsState(
        targetValue = if (selected) primary else tokens.inkSecondary,
        animationSpec = spring(stiffness = 380f),
        label = "railIconColor_${tab.routeId}",
    )
    val labelColor by animateColorAsState(
        targetValue = if (selected) tokens.inkPrimary else tokens.inkMuted,
        animationSpec = spring(stiffness = 380f),
        label = "railLabelColor_${tab.routeId}",
    )
    val barHeight by animateDpAsState(
        targetValue = if (selected) 20.dp else 0.dp,
        animationSpec = spring(dampingRatio = 0.78f, stiffness = 420f),
        label = "railAccentBar_${tab.routeId}",
    )

    val itemShape = RoundedCornerShape(14.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("nav_rail_item_${tab.routeId}")
            .minimumInteractiveComponentSize()
            .tactileScale(interactionSource, pressedScale = 0.94f)
            .clip(itemShape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = primary.copy(alpha = 0.16f)),
                role = Role.Tab,
                onClick = onClick,
            )
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        // Left spring-animated active accent bar
        if (barHeight > 0.dp) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(3.dp)
                    .height(barHeight)
                    .clip(RoundedCornerShape(100.dp))
                    .background(primary),
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .width(pillWidth)
                    .height(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(pillColor),
            ) {
                Icon(
                    imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                    contentDescription = tab.label,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp),
                )
            }

            Text(
                text = tab.label,
                style = MaterialTheme.typography.labelMedium,
                color = labelColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 2.dp),
            )
        }
    }
}

@Composable
fun GhickinBottomNavBar(
    activeTab: WorkspaceTab,
    onSelectTab: (WorkspaceTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val primary = MaterialTheme.colorScheme.primary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(tokens.obsidianSurface)
            .navigationBarsPadding()
            .testTag("workspace_bottom_bar"),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(tokens.hairlineRest),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WorkspaceTab.entries.forEach { tab ->
                val selected = tab == activeTab
                val interactionSource = remember { MutableInteractionSource() }
                val pillColor by animateColorAsState(
                    targetValue = if (selected) primaryContainer else Color.Transparent,
                    animationSpec = spring(stiffness = 380f),
                    label = "bottomPill_${tab.routeId}",
                )
                val iconColor by animateColorAsState(
                    targetValue = if (selected) primary else tokens.inkSecondary,
                    animationSpec = spring(stiffness = 380f),
                    label = "bottomIcon_${tab.routeId}",
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("nav_bottom_item_${tab.routeId}")
                        .minimumInteractiveComponentSize()
                        .tactileScale(interactionSource, pressedScale = 0.94f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = interactionSource,
                            indication = ripple(color = primary.copy(alpha = 0.16f)),
                            role = Role.Tab,
                            onClick = { onSelectTab(tab) },
                        )
                        .padding(vertical = 4.dp),
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .width(42.dp)
                            .height(28.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(pillColor),
                    ) {
                        Icon(
                            imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                            contentDescription = tab.label,
                            tint = iconColor,
                            modifier = Modifier.size(19.dp),
                        )
                    }
                    Text(
                        text = tab.shortLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selected) tokens.inkPrimary else tokens.inkMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}
