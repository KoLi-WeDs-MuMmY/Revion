package com.example.ui.groupstudy

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CloudQueue
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.FolderCopy
import androidx.compose.material.icons.rounded.Functions
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.LocalCafe
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.LiveClockTick
import com.example.model.StudyTimerGroupUiState
import com.example.model.YptAllowedApp
import com.example.model.YptGroupMember
import com.example.model.YptMemberStatus
import com.example.model.YptStudyGroup
import com.example.ui.components.tactileScale
import com.example.ui.theme.AccentMood
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MicroTagMono
import com.example.ui.timer.YptLiveMemberClockLeaf
import kotlinx.coroutines.flow.StateFlow

/**
 * Authentic Yeolpumta (YPT)-style Quiet Study Room pane:
 * - Room header with active studying counter, user live timer & room switcher
 * - Compact Member Desk Matrix with distinct status icons & isolated live HH:MM:SS clocks
 * - Silent Boost member quick-inspect dialog (no chat distractions)
 * - Iconic bottom "Allowed Apps" dock with interactive app allow-list editor
 */
@Composable
fun YptGroupStudyPane(
    state: StudyTimerGroupUiState,
    clockTickFlow: StateFlow<LiveClockTick>,
    onJoinGroup: (String) -> Unit,
    onOpenCreateGroupDialog: () -> Unit,
    onSelectUserStatus: (YptMemberStatus) -> Unit,
    onInspectMember: (String?) -> Unit,
    onSendSilentBoost: (String) -> Unit,
    onOpenAllowedAppsSheet: () -> Unit,
    onCloseAllowedAppsSheet: () -> Unit,
    onToggleAllowedApp: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val activeGroup = state.joinedGroup
    val baseUserSeconds = remember(state.todaySubjectSeconds) {
        state.todaySubjectSeconds.values.sum()
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(tokens.obsidianSurface)
            .testTag("ypt_group_study_pane"),
    ) {
        val isNarrow = maxWidth < 340.dp
        val isVeryWide = maxWidth >= 560.dp
        val gridColumns = when {
            isVeryWide -> 3
            maxWidth >= 320.dp -> 3
            else -> 2
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = if (isNarrow) 10.dp else 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // 1. YPT Room Header Bar
            YptRoomHeaderCard(
                activeGroup = activeGroup,
                baseUserSeconds = baseUserSeconds,
                clockTickFlow = clockTickFlow,
                isNarrow = isNarrow,
                onOpenCreateGroupDialog = onOpenCreateGroupDialog,
            )

            // 2. Public Study Room Switcher Row (stable keys)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ypt_group_switcher_row"),
            ) {
                items(items = state.groups, key = { it.id }) { group ->
                    val selected = group.id == activeGroup?.id
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (selected) tokens.primaryCta else tokens.carbonSurface,
                        border = BorderStroke(
                            1.dp,
                            if (selected) tokens.primaryCta else tokens.hairlineRest,
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .tactileScale()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(color = tokens.inkPrimary),
                                role = Role.Button,
                                onClick = { onJoinGroup(group.id) },
                            )
                            .testTag("ypt_room_chip_${group.id}"),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (selected) tokens.onPrimaryCta else AccentMood.EMERALD.accent,
                                    ),
                            )
                            Text(
                                text = group.name,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = if (selected) tokens.onPrimaryCta else tokens.inkPrimary,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = "${group.activeMemberCount}/${group.members.size}",
                                style = MicroTagMono,
                                color = if (selected) tokens.onPrimaryCta.copy(alpha = 0.8f) else tokens.inkMuted,
                                maxLines = 1,
                                softWrap = false,
                            )
                        }
                    }
                }
            }

            // 3. Compact Status Bar (Your Presence Selector)
            YptMyPresenceBar(
                currentStatus = state.userStatus,
                isNarrow = isNarrow,
                onSelectStatus = onSelectUserStatus,
            )

            // 4. Iconic YPT Member Desk Matrix (stable keys + isolated leaf clocks)
            if (activeGroup != null) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(gridColumns),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 4.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .testTag("ypt_member_desk_grid"),
                ) {
                    items(
                        items = activeGroup.members,
                        key = { it.id },
                    ) { member ->
                        val effectiveStatus = if (member.isCurrentUser) state.userStatus else member.status
                        val effectiveSubject = if (member.isCurrentUser) {
                            state.activeSubjectName ?: member.subjectTag
                        } else {
                            member.subjectTag
                        }
                        val effectiveBaseSec = if (member.isCurrentUser) baseUserSeconds else member.todayFocusSeconds
                        val boostCount = state.silentBoostCounts[member.id] ?: 0

                        YptDeskMemberTile(
                            member = member,
                            effectiveStatus = effectiveStatus,
                            effectiveSubject = effectiveSubject,
                            baseSeconds = effectiveBaseSec,
                            boostCount = boostCount,
                            clockTickFlow = clockTickFlow,
                            compact = isNarrow,
                            onClick = { onInspectMember(member.id) },
                        )
                    }
                }
            }

            // 5. Iconic YPT Bottom "Allowed Apps" Dock
            YptAllowedAppsDock(
                allowedApps = state.allowedApps,
                isNarrow = isNarrow,
                onEditAllowedApps = onOpenAllowedAppsSheet,
            )
        }

        // Member Quick-Inspect & Silent Boost Dialog
        val inspectedMember = remember(activeGroup, state.inspectedMemberId) {
            activeGroup?.members?.firstOrNull { it.id == state.inspectedMemberId }
        }
        if (inspectedMember != null) {
            YptMemberInspectDialog(
                member = inspectedMember,
                effectiveStatus = if (inspectedMember.isCurrentUser) state.userStatus else inspectedMember.status,
                baseSeconds = if (inspectedMember.isCurrentUser) baseUserSeconds else inspectedMember.todayFocusSeconds,
                boostCount = state.silentBoostCounts[inspectedMember.id] ?: 0,
                clockTickFlow = clockTickFlow,
                onSendBoost = { onSendSilentBoost(inspectedMember.id) },
                onDismiss = { onInspectMember(null) },
            )
        }

        // Allowed Apps Configurator Dialog
        if (state.showAllowedAppsSheet) {
            YptAllowedAppsConfigDialog(
                apps = state.allowedApps,
                onToggleApp = onToggleAllowedApp,
                onDismiss = onCloseAllowedAppsSheet,
            )
        }
    }
}

@Composable
private fun YptRoomHeaderCard(
    activeGroup: YptStudyGroup?,
    baseUserSeconds: Int,
    clockTickFlow: StateFlow<LiveClockTick>,
    isNarrow: Boolean,
    onOpenCreateGroupDialog: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val yptFlame = Color(0xFFFF7A2F)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = tokens.carbonSurface,
        border = BorderStroke(1.dp, tokens.hairlineRest),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(yptFlame.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Groups,
                        contentDescription = null,
                        tint = yptFlame,
                        modifier = Modifier.size(17.dp),
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = activeGroup?.name ?: "Quiet Study Room",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = tokens.inkPrimary,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = "${activeGroup?.activeMemberCount ?: 0}/${activeGroup?.members?.size ?: 0} studying",
                            style = MicroTagMono,
                            color = AccentMood.EMERALD.accent,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis,
                        )
                        if (!isNarrow) {
                            Text(
                                text = "· Sim peers (Firebase next)",
                                style = MicroTagMono,
                                color = tokens.inkMuted,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = tokens.slateElevated,
                    border = BorderStroke(1.dp, tokens.hairlineRest),
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .minimumInteractiveComponentSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = tokens.inkPrimary),
                            role = Role.Button,
                            onClick = onOpenCreateGroupDialog,
                        )
                        .testTag("ypt_create_room_button"),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Create public study room",
                            tint = tokens.inkPrimary,
                            modifier = Modifier.size(15.dp),
                        )
                        if (!isNarrow) {
                            Text(
                                text = "Room",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = tokens.inkPrimary,
                                maxLines = 1,
                                softWrap = false,
                            )
                        }
                    }
                }
            }

            // YPT Personal Total Focus Bar
            Surface(
                shape = RoundedCornerShape(11.dp),
                color = tokens.obsidianSurface,
                border = BorderStroke(1.dp, tokens.hairlineSubtle),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.LocalFireDepartment,
                            contentDescription = null,
                            tint = yptFlame,
                            modifier = Modifier.size(15.dp),
                        )
                        Text(
                            text = "MY TODAY TOTAL",
                            style = MicroTagMono.copy(fontWeight = FontWeight.Bold),
                            color = tokens.inkSecondary,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    YptLiveMemberClockLeaf(
                        clockTickFlow = clockTickFlow,
                        baseTodaySeconds = baseUserSeconds,
                        isActivelyStudying = true,
                        isCurrentUser = true,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                        ),
                        color = yptFlame,
                    )
                }
            }
        }
    }
}

@Composable
private fun YptMyPresenceBar(
    currentStatus: YptMemberStatus,
    isNarrow: Boolean,
    onSelectStatus: (YptMemberStatus) -> Unit,
) {
    val tokens = GhickinTokens.colors
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        items(items = YptMemberStatus.entries, key = { it.name }) { status ->
            val selected = status == currentStatus
            val spec = yptStatusVisual(status)
            Surface(
                shape = RoundedCornerShape(9.dp),
                color = if (selected) spec.accent.copy(alpha = 0.16f) else tokens.carbonSurface,
                border = BorderStroke(
                    1.dp,
                    if (selected) spec.accent.copy(alpha = 0.65f) else tokens.hairlineSubtle,
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(9.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = spec.accent),
                        role = Role.Button,
                        onClick = { onSelectStatus(status) },
                    )
                    .testTag("ypt_status_${status.name.lowercase()}"),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = if (isNarrow) 8.dp else 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Icon(
                        imageVector = spec.icon,
                        contentDescription = status.label,
                        tint = if (selected) spec.accent else tokens.inkMuted,
                        modifier = Modifier.size(13.dp),
                    )
                    Text(
                        text = status.label,
                        style = MicroTagMono.copy(fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium),
                        color = if (selected) spec.accent else tokens.inkSecondary,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun YptDeskMemberTile(
    member: YptGroupMember,
    effectiveStatus: YptMemberStatus,
    effectiveSubject: String,
    baseSeconds: Int,
    boostCount: Int,
    clockTickFlow: StateFlow<LiveClockTick>,
    compact: Boolean,
    onClick: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val statusSpec = yptStatusVisual(effectiveStatus)
    val isStudying = effectiveStatus == YptMemberStatus.FOCUSING || effectiveStatus == YptMemberStatus.IN_REVISION

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isStudying) tokens.carbonSurface else tokens.obsidianSurface.copy(alpha = 0.85f),
        border = BorderStroke(
            width = if (member.isCurrentUser || isStudying) 1.25.dp else 1.dp,
            color = when {
                member.isCurrentUser -> statusSpec.accent.copy(alpha = 0.72f)
                isStudying -> statusSpec.accent.copy(alpha = 0.40f)
                else -> tokens.hairlineSubtle
            },
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .tactileScale()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = statusSpec.accent),
                role = Role.Button,
                onClick = onClick,
            )
            .testTag("ypt_desk_tile_${member.id}"),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = if (compact) 8.dp else 10.dp, vertical = if (compact) 10.dp else 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            // Top micro row: YOU badge or silent boost counter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (member.isCurrentUser) {
                    Surface(
                        shape = RoundedCornerShape(5.dp),
                        color = statusSpec.accent.copy(alpha = 0.18f),
                    ) {
                        Text(
                            text = "YOU",
                            style = MicroTagMono.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold),
                            color = statusSpec.accent,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(if (isStudying) statusSpec.accent else tokens.inkMuted.copy(alpha = 0.35f)),
                    )
                }

                if (boostCount > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Bolt,
                            contentDescription = null,
                            tint = AccentMood.AMBER.accent,
                            modifier = Modifier.size(11.dp),
                        )
                        Text(
                            text = "$boostCount",
                            style = MicroTagMono.copy(fontSize = 9.sp),
                            color = tokens.inkMuted,
                            maxLines = 1,
                            softWrap = false,
                        )
                    }
                }
            }

            // Iconic YPT Desk Status Circle Emblem
            Box(
                modifier = Modifier
                    .size(if (compact) 42.dp else 48.dp)
                    .clip(CircleShape)
                    .background(
                        if (isStudying) statusSpec.accent.copy(alpha = 0.16f) else tokens.slateElevated,
                    )
                    .border(
                        width = if (isStudying) 1.5.dp else 1.dp,
                        color = if (isStudying) statusSpec.accent.copy(alpha = 0.72f) else tokens.hairlineRest,
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = statusSpec.icon,
                    contentDescription = effectiveStatus.label,
                    tint = if (isStudying) statusSpec.accent else tokens.inkMuted,
                    modifier = Modifier.size(if (compact) 21.dp else 24.dp),
                )
            }

            // Member Display Name
            Text(
                text = if (member.isCurrentUser) "You" else member.displayName,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isStudying) tokens.inkPrimary else tokens.inkSecondary,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )

            // Isolated Live Monospace Focus Timer (HH:MM:SS)
            YptLiveMemberClockLeaf(
                clockTickFlow = clockTickFlow,
                baseTodaySeconds = baseSeconds,
                isActivelyStudying = isStudying,
                isCurrentUser = member.isCurrentUser,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = if (compact) 11.sp else 12.sp,
                ),
                color = if (isStudying) statusSpec.accent else tokens.inkMuted,
            )

            // Subject Pill (single-line ellipsized — never wraps vertically)
            Surface(
                shape = RoundedCornerShape(7.dp),
                color = tokens.obsidianSurface,
                border = BorderStroke(1.dp, tokens.hairlineSubtle),
            ) {
                Text(
                    text = effectiveSubject,
                    style = MicroTagMono.copy(fontSize = 9.sp),
                    color = tokens.inkSecondary,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                )
            }
        }
    }
}

@Composable
private fun YptAllowedAppsDock(
    allowedApps: List<YptAllowedApp>,
    isNarrow: Boolean,
    onEditAllowedApps: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val enabledApps = remember(allowedApps) { allowedApps.filter { it.isAllowed } }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = tokens.carbonSurface,
        border = BorderStroke(1.dp, tokens.hairlineRest),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ypt_allowed_apps_dock"),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Apps,
                        contentDescription = null,
                        tint = tokens.inkSecondary,
                        modifier = Modifier.size(14.dp),
                    )
                    Text(
                        text = "ALLOWED APPS (${enabledApps.size})",
                        style = MicroTagMono.copy(fontWeight = FontWeight.Bold),
                        color = tokens.inkSecondary,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = tokens.slateElevated,
                    border = BorderStroke(1.dp, tokens.hairlineRest),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = tokens.inkPrimary),
                            role = Role.Button,
                            onClick = onEditAllowedApps,
                        )
                        .testTag("ypt_edit_allowed_apps_button"),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Tune,
                            contentDescription = "Configure allowed apps",
                            tint = tokens.inkPrimary,
                            modifier = Modifier.size(12.dp),
                        )
                        Text(
                            text = "Edit",
                            style = MicroTagMono.copy(fontWeight = FontWeight.SemiBold),
                            color = tokens.inkPrimary,
                            maxLines = 1,
                            softWrap = false,
                        )
                    }
                }
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(items = enabledApps, key = { it.id }) { app ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = tokens.obsidianSurface,
                        border = BorderStroke(1.dp, tokens.hairlineSubtle),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Icon(
                                imageVector = allowedAppVector(app.iconKey),
                                contentDescription = app.name,
                                tint = tokens.themeAccent,
                                modifier = Modifier.size(15.dp),
                            )
                            if (!isNarrow) {
                                Text(
                                    text = app.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = tokens.inkPrimary,
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun YptMemberInspectDialog(
    member: YptGroupMember,
    effectiveStatus: YptMemberStatus,
    baseSeconds: Int,
    boostCount: Int,
    clockTickFlow: StateFlow<LiveClockTick>,
    onSendBoost: () -> Unit,
    onDismiss: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val spec = yptStatusVisual(effectiveStatus)
    val isStudying = effectiveStatus == YptMemberStatus.FOCUSING || effectiveStatus == YptMemberStatus.IN_REVISION

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = tokens.carbonSurface,
            border = BorderStroke(1.dp, tokens.hairlineRest),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 380.dp)
                .testTag("ypt_member_inspect_dialog"),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(spec.accent.copy(alpha = 0.16f))
                            .border(1.5.dp, spec.accent.copy(alpha = 0.65f), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = spec.icon,
                            contentDescription = effectiveStatus.label,
                            tint = spec.accent,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = member.displayName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = tokens.inkPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = "${member.handle} · ${effectiveStatus.label}",
                            style = MaterialTheme.typography.bodySmall,
                            color = spec.accent,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Rounded.Close, contentDescription = "Close", tint = tokens.inkMuted)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = tokens.obsidianSurface,
                    border = BorderStroke(1.dp, tokens.hairlineSubtle),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text("TODAY'S FOCUS LOG", style = MicroTagMono, color = tokens.inkMuted)
                        YptLiveMemberClockLeaf(
                            clockTickFlow = clockTickFlow,
                            baseTodaySeconds = baseSeconds,
                            isActivelyStudying = isStudying,
                            isCurrentUser = member.isCurrentUser,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                            ),
                            color = spec.accent,
                        )
                        Text(
                            text = "${member.subjectTag} · ${member.chapterTag}",
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.inkSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                if (!member.isCurrentUser) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = tokens.primaryCta,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .minimumInteractiveComponentSize()
                            .clickable(role = Role.Button, onClick = onSendBoost)
                            .testTag("ypt_send_silent_boost_button"),
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Bolt,
                                contentDescription = null,
                                tint = tokens.onPrimaryCta,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Send Silent Boost ($boostCount)",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = tokens.onPrimaryCta,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun YptAllowedAppsConfigDialog(
    apps: List<YptAllowedApp>,
    onToggleApp: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = tokens.carbonSurface,
            border = BorderStroke(1.dp, tokens.hairlineRest),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 440.dp)
                .testTag("ypt_allowed_apps_dialog"),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Allowed Study Apps",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = tokens.inkPrimary,
                        )
                        Text(
                            text = "Apps shown in your YPT study room dock during active focus",
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.inkSecondary,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Rounded.Close, contentDescription = "Close", tint = tokens.inkMuted)
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    apps.forEach { app ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = tokens.obsidianSurface,
                            border = BorderStroke(1.dp, tokens.hairlineSubtle),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(tokens.slateElevated),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = allowedAppVector(app.iconKey),
                                        contentDescription = null,
                                        tint = if (app.isAllowed) tokens.themeAccent else tokens.inkMuted,
                                        modifier = Modifier.size(18.dp),
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = app.name,
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                                        color = tokens.inkPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Text(
                                        text = app.category,
                                        style = MicroTagMono,
                                        color = tokens.inkMuted,
                                        maxLines = 1,
                                    )
                                }
                                Switch(
                                    checked = app.isAllowed,
                                    onCheckedChange = { onToggleApp(app.id) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = tokens.onPrimaryCta,
                                        checkedTrackColor = tokens.primaryCta,
                                    ),
                                    modifier = Modifier.testTag("ypt_allowed_app_switch_${app.id}"),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreateYptGroupDialog(
    onCreate: (name: String, category: String, targetHours: Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    var groupName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("COMPETITIVE") }
    var targetHours by remember { mutableIntStateOf(6) }
    val categories = remember { listOf("COMPETITIVE", "BOARDS", "CODING", "GENERAL") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = tokens.carbonSurface,
            border = BorderStroke(1.dp, tokens.hairlineRest),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 440.dp)
                .testTag("create_ypt_group_dialog"),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Create Public Study Room",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = tokens.inkPrimary,
                    )
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Rounded.Close, contentDescription = "Close", tint = tokens.inkMuted)
                    }
                }

                OutlinedTextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    label = { Text("Room Name") },
                    placeholder = { Text("e.g., Late Night JEE Sprint") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = tokens.themeAccent,
                        unfocusedBorderColor = tokens.hairlineRest,
                        focusedTextColor = tokens.inkPrimary,
                        unfocusedTextColor = tokens.inkPrimary,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("create_ypt_group_name_input"),
                )

                Text("CATEGORY", style = MicroTagMono, color = tokens.inkMuted)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    categories.forEach { cat ->
                        val sel = cat == category
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (sel) tokens.primaryCta else tokens.obsidianSurface,
                            border = BorderStroke(1.dp, if (sel) tokens.primaryCta else tokens.hairlineSubtle),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { category = cat },
                        ) {
                            Text(
                                text = cat,
                                style = MicroTagMono,
                                color = if (sel) tokens.onPrimaryCta else tokens.inkSecondary,
                                maxLines = 1,
                                softWrap = false,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            )
                        }
                    }
                }

                Text("DAILY TARGET: ${targetHours}H", style = MicroTagMono, color = tokens.inkMuted)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(4, 6, 8, 10).forEach { hrs ->
                        val sel = hrs == targetHours
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (sel) tokens.primaryCta else tokens.obsidianSurface,
                            border = BorderStroke(1.dp, if (sel) tokens.primaryCta else tokens.hairlineSubtle),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { targetHours = hrs },
                        ) {
                            Text(
                                text = "${hrs}h/d",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = if (sel) tokens.onPrimaryCta else tokens.inkPrimary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CloudQueue,
                        contentDescription = null,
                        tint = tokens.inkMuted,
                        modifier = Modifier.size(14.dp),
                    )
                    Text(
                        text = "Room runs locally now; Firebase multi-device sync arrives next phase.",
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.inkMuted,
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = tokens.primaryCta,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .minimumInteractiveComponentSize()
                        .clickable {
                            val trimmed = groupName.trim().ifBlank { "Focus Study Hall" }
                            onCreate(trimmed, category, targetHours)
                        }
                        .testTag("create_ypt_group_confirm_button"),
                ) {
                    Text(
                        text = "Create & Join Room",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = tokens.onPrimaryCta,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                    )
                }
            }
        }
    }
}

private data class YptStatusVisualSpec(
    val icon: ImageVector,
    val accent: Color,
)

private fun yptStatusVisual(status: YptMemberStatus): YptStatusVisualSpec = when (status) {
    YptMemberStatus.FOCUSING -> YptStatusVisualSpec(Icons.Rounded.LocalFireDepartment, Color(0xFFFF7A2F))
    YptMemberStatus.IN_REVISION -> YptStatusVisualSpec(Icons.Rounded.AutoAwesome, Color(0xFF34D399))
    YptMemberStatus.ON_BREAK -> YptStatusVisualSpec(Icons.Rounded.LocalCafe, Color(0xFFFBBF24))
    YptMemberStatus.IDLE -> YptStatusVisualSpec(Icons.Rounded.Bedtime, Color(0xFF94A3B8))
}

private fun allowedAppVector(iconKey: String): ImageVector = when (iconKey) {
    "vault" -> Icons.Rounded.FolderCopy
    "calculator" -> Icons.Rounded.Calculate
    "dictionary" -> Icons.Rounded.MenuBook
    "pdf" -> Icons.Rounded.Description
    "formula" -> Icons.Rounded.Functions
    "audio" -> Icons.Rounded.Headphones
    else -> Icons.Rounded.Apps
}
