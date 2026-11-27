package com.example.ui.timer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.RawDailyStudyPoint
import com.example.model.StudyTimerGroupUiState
import com.example.model.formatDurationCompactHm
import com.example.model.formatDurationHms
import com.example.model.formatMinutesHm
import com.example.ui.components.tactileScale
import com.example.ui.theme.AccentMood
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MicroTagMono

@Composable
fun TimerStatsAndLeaderboardSheet(
    state: StudyTimerGroupUiState,
    onDismiss: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val emerald = tokens.semanticSuccess
    val amber = AccentMood.AMBER.accent
    var activeTab by remember { mutableStateOf(0) } // 0 = Stats & Graph, 1 = Leaderboard

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = tokens.obsidianSurface,
            border = BorderStroke(1.dp, tokens.hairlineRest),
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.88f)
                .widthIn(max = 720.dp)
                .testTag("timer_stats_leaderboard_sheet"),
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "STUDY TIMER TELEMETRY & RANKING",
                            style = MicroTagMono,
                            color = tokens.themeAccent,
                        )
                        Text(
                            text = "Stats, Daily Graph & Leaderboard",
                            style = MaterialTheme.typography.titleLarge,
                            color = tokens.inkPrimary,
                        )
                    }

                    val closeInt = remember { MutableInteractionSource() }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = tokens.carbonSurface,
                        border = BorderStroke(1.dp, tokens.hairlineSubtle),
                        modifier = Modifier
                            .testTag("timer_stats_close_btn")
                            .minimumInteractiveComponentSize()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable(
                                interactionSource = closeInt,
                                indication = ripple(),
                                role = Role.Button,
                                onClick = onDismiss,
                            ),
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(40.dp)) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Close stats",
                                tint = tokens.inkSecondary,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }

                // 2-Tab Switcher
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    StatsTabPill(
                        label = "Daily Graph & Logs",
                        icon = Icons.Rounded.BarChart,
                        selected = activeTab == 0,
                        testTag = "timer_stats_tab_graph",
                        onClick = { activeTab = 0 },
                        modifier = Modifier.weight(1f),
                    )
                    StatsTabPill(
                        label = "Group Leaderboard",
                        icon = Icons.Rounded.EmojiEvents,
                        selected = activeTab == 1,
                        testTag = "timer_stats_tab_leaderboard",
                        onClick = { activeTab = 1 },
                        modifier = Modifier.weight(1f),
                    )
                }

                // Body
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    if (activeTab == 0) {
                        // Highest & Lowest Unoptimized Record Cards
                        val highest = state.highestStudyDay
                        val lowest = state.lowestStudyDay

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            RecordStatCard(
                                label = "HIGHEST STUDY DAY (RAW)",
                                value = highest?.let { formatMinutesHm(it.totalMinutes) } ?: "—",
                                sub = highest?.shortDate ?: "",
                                accent = emerald,
                                icon = Icons.Rounded.ArrowUpward,
                                modifier = Modifier.weight(1f),
                            )
                            RecordStatCard(
                                label = "LOWEST STUDY DAY (RAW)",
                                value = lowest?.let { formatMinutesHm(it.totalMinutes) } ?: "—",
                                sub = lowest?.shortDate ?: "",
                                accent = amber,
                                icon = Icons.Rounded.ArrowDownward,
                                modifier = Modifier.weight(1f),
                            )
                        }

                        // Unoptimized Daily Study Time Bar Chart
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = tokens.carbonSurface,
                            border = BorderStroke(1.dp, tokens.hairlineRest),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = "UNOPTIMIZED 20-DAY STUDY GRAPH",
                                        style = MicroTagMono,
                                        color = tokens.inkSecondary,
                                    )
                                    Text(
                                        text = "Today: ${formatDurationCompactHm(state.todayTotalStudySeconds)}",
                                        style = MicroTagMono,
                                        color = tokens.themeAccent,
                                    )
                                }

                                UnoptimizedDailyBarGraph(
                                    points = state.rawDailyStudyHistory,
                                    highestDayIndex = highest?.dayIndex,
                                    lowestDayIndex = lowest?.dayIndex,
                                    primaryBarColor = tokens.themeAccent,
                                    highestBarColor = emerald,
                                    lowestBarColor = amber,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(148.dp),
                                )

                                Text(
                                    text = "Raw daily totals remain unoptimized so true highest & lowest days are never smoothed out.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = tokens.inkMuted,
                                )
                            }
                        }

                        // Study Log RAG 3-day Auto-Optimization Summary
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = tokens.carbonSurface,
                            border = BorderStroke(1.dp, tokens.hairlineSubtle),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Storage,
                                        contentDescription = null,
                                        tint = tokens.themeAccent,
                                        modifier = Modifier.size(15.dp),
                                    )
                                    Text(
                                        text = "STUDY LOG RAG · 20d + 1m AVG + 6m AVG",
                                        style = MicroTagMono,
                                        color = tokens.themeAccent,
                                    )
                                }
                                Text(
                                    text = state.studyLogRag.compactedSummary,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = tokens.inkSecondary,
                                )
                                Text(
                                    text = state.studyLogRag.lastAutoOptimizedLabel,
                                    style = MicroTagMono,
                                    color = tokens.inkMuted,
                                )
                            }
                        }

                        // Today's Logged Sessions (including Auto Revision/Flashcards & Synced Lectures)
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "RECENT STUDY & LECTURE LOGS",
                                style = MicroTagMono,
                                color = tokens.inkMuted,
                            )
                            state.recentSessionLogs.take(8).forEach { log ->
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = tokens.carbonSurface,
                                    border = BorderStroke(1.dp, tokens.hairlineSubtle),
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
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
                                                Box(
                                                    modifier = Modifier
                                                        .size(7.dp)
                                                        .clip(CircleShape)
                                                        .background(log.accentMood.accent),
                                                )
                                                Text(
                                                    text = log.subjectName,
                                                    style = MaterialTheme.typography.labelLarge,
                                                    color = tokens.inkPrimary,
                                                )
                                                Text(
                                                    text = "· ${log.source.label}",
                                                    style = MicroTagMono,
                                                    color = tokens.inkMuted,
                                                )
                                            }
                                            if (log.syncedLectureTitle != null) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Rounded.Link,
                                                        contentDescription = null,
                                                        tint = tokens.themeAccent,
                                                        modifier = Modifier.size(12.dp),
                                                    )
                                                    Text(
                                                        text = log.syncedLectureTitle,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = tokens.inkSecondary,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis,
                                                    )
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = formatDurationCompactHm(log.durationSeconds),
                                                style = MaterialTheme.typography.labelLarge.copy(
                                                    fontFamily = FontFamily.Monospace,
                                                    fontFeatureSettings = "tnum",
                                                ),
                                                color = tokens.inkPrimary,
                                            )
                                            Text(
                                                text = log.timestampLabel,
                                                style = MicroTagMono,
                                                color = tokens.inkMuted,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // Live Group Study Leaderboard
                        val group = state.joinedGroup
                        val rankedMembers = remember(group, state.todayTotalStudySeconds) {
                            group?.members?.map { m ->
                                if (m.isCurrentUser) {
                                    m.copy(todayFocusSeconds = state.todayTotalStudySeconds)
                                } else m
                            }?.sortedByDescending { it.todayFocusSeconds }.orEmpty()
                        }

                        Text(
                            text = "${group?.name ?: "Study Group"} · LIVE DAILY RANKING",
                            style = MicroTagMono,
                            color = tokens.inkMuted,
                        )

                        rankedMembers.forEachIndexed { idx, member ->
                            val rank = idx + 1
                            val rankColor = when (rank) {
                                1 -> amber
                                2 -> tokens.themeAccent
                                3 -> emerald
                                else -> tokens.inkMuted
                            }
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (member.isCurrentUser) tokens.slateElevated else tokens.carbonSurface,
                                border = BorderStroke(
                                    1.dp,
                                    if (member.isCurrentUser) tokens.themeAccent.copy(alpha = 0.6f) else tokens.hairlineSubtle,
                                ),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    Text(
                                        text = "#$rank",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                        ),
                                        color = rankColor,
                                    )
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(2.dp),
                                    ) {
                                        Text(
                                            text = member.displayName,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = tokens.inkPrimary,
                                        )
                                        Text(
                                            text = "${member.status.label} · ${member.subjectTag}",
                                            style = MicroTagMono,
                                            color = tokens.inkSecondary,
                                        )
                                    }
                                    Text(
                                        text = formatDurationHms(member.todayFocusSeconds),
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            fontFeatureSettings = "tnum",
                                        ),
                                        color = if (member.isCurrentUser) tokens.themeAccent else tokens.inkPrimary,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsTabPill(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val interaction = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(12.dp)

    Surface(
        shape = shape,
        color = if (selected) tokens.primaryCta else tokens.carbonSurface,
        border = if (!selected) BorderStroke(1.dp, tokens.hairlineRest) else null,
        modifier = modifier
            .testTag(testTag)
            .minimumInteractiveComponentSize()
            .tactileScale(interaction, 0.97f)
            .clip(shape)
            .clickable(
                interactionSource = interaction,
                indication = ripple(),
                role = Role.Tab,
                onClick = onClick,
            ),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) tokens.onPrimaryCta else tokens.inkSecondary,
                modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) tokens.onPrimaryCta else tokens.inkSecondary,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun RecordStatCard(
    label: String,
    value: String,
    sub: String,
    accent: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = tokens.carbonSurface,
        border = BorderStroke(1.dp, accent.copy(alpha = 0.4f)),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(14.dp),
                )
                Text(
                    text = label,
                    style = MicroTagMono,
                    color = accent,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                ),
                color = tokens.inkPrimary,
            )
            Text(
                text = sub,
                style = MicroTagMono,
                color = tokens.inkMuted,
            )
        }
    }
}

@Composable
private fun UnoptimizedDailyBarGraph(
    points: List<RawDailyStudyPoint>,
    highestDayIndex: Int?,
    lowestDayIndex: Int?,
    primaryBarColor: Color,
    highestBarColor: Color,
    lowestBarColor: Color,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val hairline = tokens.hairlineSubtle
    val maxVal = (points.maxOfOrNull { it.totalMinutes } ?: 360).coerceAtLeast(60)

    Canvas(modifier = modifier) {
        if (points.isEmpty()) return@Canvas
        val barCount = points.size
        val gap = 4.dp.toPx()
        val barW = ((size.width - gap * (barCount - 1)) / barCount).coerceAtLeast(4.dp.toPx())
        val topPad = 8.dp.toPx()
        val usableH = (size.height - topPad - 6.dp.toPx()).coerceAtLeast(24f)

        // Subtle baseline & midpoint guides
        drawLine(
            color = hairline,
            start = Offset(0f, size.height - 2.dp.toPx()),
            end = Offset(size.width, size.height - 2.dp.toPx()),
            strokeWidth = 1.dp.toPx(),
        )
        drawLine(
            color = hairline.copy(alpha = 0.55f),
            start = Offset(0f, topPad + usableH / 2f),
            end = Offset(size.width, topPad + usableH / 2f),
            strokeWidth = 1.dp.toPx(),
        )

        points.forEachIndexed { idx, pt ->
            val frac = (pt.totalMinutes.toFloat() / maxVal.toFloat()).coerceIn(0.06f, 1f)
            val barH = frac * usableH
            val x = idx * (barW + gap)
            val y = size.height - 2.dp.toPx() - barH
            val color = when (pt.dayIndex) {
                highestDayIndex -> highestBarColor
                lowestDayIndex -> lowestBarColor
                else -> primaryBarColor.copy(alpha = 0.72f)
            }
            drawRoundRect(
                color = color,
                topLeft = Offset(x, y),
                size = Size(barW, barH),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()),
            )
        }
    }
}
