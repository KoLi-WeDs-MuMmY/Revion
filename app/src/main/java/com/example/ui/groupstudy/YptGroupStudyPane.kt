package com.example.ui.groupstudy

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CloudQueue
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.StudyTimerGroupUiState
import com.example.model.YptGroupMember
import com.example.model.YptMemberStatus
import com.example.model.formatDurationCompactHm
import com.example.model.formatDurationHms
import com.example.ui.components.tactileScale
import com.example.ui.theme.AccentMood
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MicroTagMono

@Composable
fun YptGroupStudyPane(
    state: StudyTimerGroupUiState,
    isPureGroupScreen: Boolean,
    isCompactWidth: Boolean,
    onJoinGroup: (String) -> Unit,
    onOpenCreateGroupDialog: () -> Unit,
    onSelectUserStatus: (YptMemberStatus) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val emerald = tokens.semanticSuccess
    val group = state.joinedGroup

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(tokens.amoledBackground)
            .verticalScroll(rememberScrollState())
            .padding(
                start = 14.dp,
                end = 14.dp,
                top = if (isPureGroupScreen && state.isTimerActiveSession) 56.dp else 12.dp,
                bottom = 16.dp,
            )
            .testTag("ypt_group_study_pane"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Header: Active YPT Group + Create Public Room
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Groups,
                        contentDescription = null,
                        tint = tokens.themeAccent,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = "YPT GROUP STUDY · PUBLIC ROOMS",
                        style = MicroTagMono,
                        color = tokens.themeAccent,
                    )
                }
                Text(
                    text = group?.name ?: "Select a Study Group",
                    style = MaterialTheme.typography.titleLarge,
                    color = tokens.inkPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            val createInt = remember { MutableInteractionSource() }
            Surface(
                shape = RoundedCornerShape(11.dp),
                color = tokens.carbonSurface,
                border = BorderStroke(1.dp, tokens.hairlineRest),
                modifier = Modifier
                    .testTag("ypt_create_group_button")
                    .minimumInteractiveComponentSize()
                    .tactileScale(createInt, 0.95f)
                    .clip(RoundedCornerShape(11.dp))
                    .clickable(
                        interactionSource = createInt,
                        indication = ripple(),
                        role = Role.Button,
                        onClick = onOpenCreateGroupDialog,
                    ),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Create public study group",
                        tint = tokens.themeAccent,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = "Create",
                        style = MaterialTheme.typography.labelMedium,
                        color = tokens.inkPrimary,
                    )
                }
            }
        }

        // Public Group Switcher Strip (Join / Switch any public room)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            state.groups.forEach { room ->
                val isJoined = room.id == state.joinedGroupId
                val roomInt = remember { MutableInteractionSource() }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isJoined) tokens.slateElevated else tokens.obsidianSurface,
                    border = BorderStroke(
                        1.dp,
                        if (isJoined) tokens.themeAccent else tokens.hairlineRest,
                    ),
                    modifier = Modifier
                        .testTag("ypt_group_chip_${room.id}")
                        .tactileScale(roomInt, 0.96f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = roomInt,
                            indication = ripple(),
                            role = Role.RadioButton,
                            onClick = { onJoinGroup(room.id) },
                        ),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 11.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            imageVector = if (isJoined) Icons.Rounded.CheckCircle else Icons.Rounded.Public,
                            contentDescription = null,
                            tint = if (isJoined) emerald else tokens.inkMuted,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = room.name,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isJoined) tokens.inkPrimary else tokens.inkSecondary,
                            maxLines = 1,
                        )
                        Text(
                            text = "${room.activeMemberCount} live",
                            style = MicroTagMono,
                            color = tokens.inkMuted,
                        )
                    }
                }
            }
        }

        // Group Summary + Firebase Upcoming Notice
        if (group != null) {
            Surface(
                shape = RoundedCornerShape(15.dp),
                color = tokens.obsidianSurface,
                border = BorderStroke(1.dp, tokens.hairlineRest),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "${group.activeMemberCount}/${group.members.size} FOCUSING · TARGET ${group.dailyTargetHours}H/DAY",
                            style = MicroTagMono,
                            color = emerald,
                        )
                        Text(
                            text = "GROUP: ${formatDurationCompactHm(group.totalGroupSecondsToday)}",
                            style = MicroTagMono,
                            color = tokens.inkPrimary,
                        )
                    }
                    Text(
                        text = group.goalSummary,
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.inkSecondary,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CloudQueue,
                            contentDescription = null,
                            tint = tokens.inkMuted,
                            modifier = Modifier.size(13.dp),
                        )
                        Text(
                            text = "Synced with your active timer · Live multi-device Firebase Group Sync coming in next update",
                            style = MicroTagMono,
                            color = tokens.inkMuted,
                            maxLines = 2,
                        )
                    }
                }
            }
        }

        // User Status Quick Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            YptMemberStatus.entries.forEach { st ->
                val active = state.userStatus == st
                val stInt = remember { MutableInteractionSource() }
                val stColor = statusColor(st, emerald, tokens.themeAccent)
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = if (active) stColor.copy(alpha = 0.16f) else tokens.carbonSurface,
                    border = BorderStroke(1.dp, if (active) stColor else tokens.hairlineSubtle),
                    modifier = Modifier
                        .testTag("ypt_user_status_${st.name.lowercase()}")
                        .clip(RoundedCornerShape(100.dp))
                        .clickable(
                            interactionSource = stInt,
                            indication = ripple(),
                            role = Role.RadioButton,
                            onClick = { onSelectUserStatus(st) },
                        ),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(stColor),
                        )
                        Text(
                            text = st.label,
                            style = MicroTagMono,
                            color = if (active) stColor else tokens.inkSecondary,
                        )
                    }
                }
            }
        }

        // Calm Quiet-Presence Member Grid (2 columns when wide enough, 1 column when narrow)
        if (group != null) {
            val members = group.members.map { m ->
                if (m.isCurrentUser) {
                    m.copy(
                        status = state.userStatus,
                        subjectTag = state.activeSubjectName ?: "Tagless Focus",
                        chapterTag = state.syncedLectureTitle ?: state.userChapterTag,
                        todayFocusSeconds = state.todayTotalStudySeconds,
                    )
                } else m
            }

            if (isCompactWidth) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    members.forEach { member ->
                        YptMemberPresenceCard(member = member)
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    members.chunked(2).forEach { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            pair.forEach { member ->
                                YptMemberPresenceCard(
                                    member = member,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            if (pair.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun YptMemberPresenceCard(
    member: YptGroupMember,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val emerald = tokens.semanticSuccess
    val stColor = statusColor(member.status, emerald, tokens.themeAccent)
    val isLive = member.status == YptMemberStatus.FOCUSING || member.status == YptMemberStatus.IN_REVISION

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (member.isCurrentUser) tokens.slateElevated else tokens.obsidianSurface,
        border = BorderStroke(
            1.dp,
            when {
                member.isCurrentUser && isLive -> emerald.copy(alpha = 0.55f)
                member.isCurrentUser -> tokens.themeAccent.copy(alpha = 0.45f)
                isLive -> tokens.hairlineRest
                else -> tokens.hairlineSubtle
            },
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("ypt_member_card_${member.id}"),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(stColor),
                    )
                    Text(
                        text = member.displayName,
                        style = MaterialTheme.typography.titleSmall,
                        color = tokens.inkPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Text(
                    text = member.status.label.uppercase(),
                    style = MicroTagMono,
                    color = stColor,
                    maxLines = 1,
                )
            }

            // Live ticking focus time in tabular monospace
            Text(
                text = formatDurationHms(member.todayFocusSeconds),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontFeatureSettings = "tnum",
                ),
                color = if (isLive) tokens.inkPrimary else tokens.inkMuted,
                maxLines = 1,
            )

            // Subject & Chapter tag
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = tokens.carbonSurface,
                ) {
                    Text(
                        text = member.subjectTag,
                        style = MicroTagMono,
                        color = member.accentMood.accent,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        maxLines = 1,
                    )
                }
                Text(
                    text = member.chapterTag,
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
fun CreateYptGroupDialog(
    onCreateGroup: (name: String, category: String, targetHours: Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("JEE / NEET") }
    var targetHours by remember { mutableStateOf(6) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = tokens.obsidianSurface,
            border = BorderStroke(1.dp, tokens.hairlineRest),
            modifier = Modifier
                .fillMaxWidth(0.90f)
                .widthIn(max = 460.dp)
                .testTag("ypt_create_group_dialog"),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Create Public Study Group",
                        style = MaterialTheme.typography.titleLarge,
                        color = tokens.inkPrimary,
                    )
                    val closeInt = remember { MutableInteractionSource() }
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable(
                                interactionSource = closeInt,
                                indication = ripple(),
                                role = Role.Button,
                                onClick = onDismiss,
                            ),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Close create group dialog",
                            tint = tokens.inkSecondary,
                        )
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Public Group Name") },
                    placeholder = { Text("e.g. Late Night Physics Sprints") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = tokens.themeAccent,
                        unfocusedBorderColor = tokens.hairlineRest,
                        focusedTextColor = tokens.inkPrimary,
                        unfocusedTextColor = tokens.inkPrimary,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ypt_create_group_name_input"),
                )

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Exam / Track Category") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = tokens.themeAccent,
                        unfocusedBorderColor = tokens.hairlineRest,
                        focusedTextColor = tokens.inkPrimary,
                        unfocusedTextColor = tokens.inkPrimary,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "DAILY TARGET:",
                        style = MicroTagMono,
                        color = tokens.inkMuted,
                    )
                    listOf(4, 6, 8, 10).forEach { hrs ->
                        val selected = targetHours == hrs
                        val hInt = remember { MutableInteractionSource() }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (selected) tokens.themeAccent.copy(alpha = 0.18f) else tokens.carbonSurface,
                            border = BorderStroke(1.dp, if (selected) tokens.themeAccent else tokens.hairlineSubtle),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable(
                                    interactionSource = hInt,
                                    indication = ripple(),
                                    role = Role.RadioButton,
                                    onClick = { targetHours = hrs },
                                ),
                        ) {
                            Text(
                                text = "${hrs}h",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (selected) tokens.themeAccent else tokens.inkSecondary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            )
                        }
                    }
                }

                val submitInt = remember { MutableInteractionSource() }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = tokens.primaryCta,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ypt_create_group_submit")
                        .minimumInteractiveComponentSize()
                        .tactileScale(submitInt, 0.97f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = submitInt,
                            indication = ripple(),
                            role = Role.Button,
                            onClick = {
                                val finalName = name.trim().ifEmpty { "Focus Study Hall" }
                                onCreateGroup(finalName, category.trim().ifEmpty { "STUDY" }, targetHours)
                            },
                        ),
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(vertical = 13.dp),
                    ) {
                        Text(
                            text = "Create & Join Public Group",
                            style = MaterialTheme.typography.labelLarge,
                            color = tokens.onPrimaryCta,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun statusColor(
    status: YptMemberStatus,
    emerald: androidx.compose.ui.graphics.Color,
    themeAccent: androidx.compose.ui.graphics.Color,
): androidx.compose.ui.graphics.Color {
    val tokens = GhickinTokens.colors
    return when (status) {
        YptMemberStatus.FOCUSING -> emerald
        YptMemberStatus.IN_REVISION -> themeAccent
        YptMemberStatus.ON_BREAK -> AccentMood.AMBER.accent
        YptMemberStatus.IDLE -> tokens.inkMuted
    }
}
