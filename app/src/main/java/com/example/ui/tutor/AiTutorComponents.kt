package com.example.ui.tutor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.model.ChapterDiagramRef
import com.example.model.StudyBatch
import com.example.model.TutorChatSession
import com.example.model.VaultChapterNode
import com.example.model.VaultSubjectNode
import com.example.ui.theme.AppThemeId
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MicroTagMono
import com.example.ui.theme.MonoBadgeStyle
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Distinct animated center brand emblem tailored to each of the 4 modular XML themes
 * (Claude radiating starburst, Grok kinetic orbital slash, Paper Light editorial prism,
 * Slate precision compass).
 */
@Composable
fun AnimatedThemeBrandLogo(
    size: Dp = 64.dp,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val infinite = rememberInfiniteTransition(label = "aiTutorBrandMotion")

    val slowOrbit by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 16_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "brandOrbit",
    )
    val breathe by infinite.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2_400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "brandBreathe",
    )

    val primary = tokens.chatLogoPrimary
    val secondary = tokens.chatLogoSecondary

    Canvas(
        modifier = modifier
            .size(size)
            .testTag("ai_tutor_animated_logo"),
    ) {
        val cx = this.size.width / 2f
        val cy = this.size.height / 2f
        val r = this.size.minDimension * 0.42f

        when (tokens.activeTheme) {
            AppThemeId.CLAUDE_EDITORIAL -> {
                // Warm radiating 10-ray Claude-style botanical starburst
                rotate(degrees = slowOrbit * 0.25f, pivot = Offset(cx, cy)) {
                    val rays = 10
                    for (i in 0 until rays) {
                        val angle = (i * 2.0 * PI / rays).toFloat()
                        val inner = r * 0.24f
                        val outer = r * (if (i % 2 == 0) 1.02f else 0.78f) * breathe
                        drawLine(
                            color = if (i % 2 == 0) primary else secondary.copy(alpha = 0.85f),
                            start = Offset(cx + cos(angle) * inner, cy + sin(angle) * inner),
                            end = Offset(cx + cos(angle) * outer, cy + sin(angle) * outer),
                            strokeWidth = this.size.minDimension * 0.085f,
                            cap = StrokeCap.Round,
                        )
                    }
                }
                drawCircle(
                    color = primary,
                    radius = r * 0.19f * breathe,
                    center = Offset(cx, cy),
                )
            }

            AppThemeId.GROK_BLACK -> {
                // Kinetic Grok-inspired minimalist ring + tilted orbital slash
                drawCircle(
                    color = secondary.copy(alpha = 0.35f),
                    radius = r * 0.92f,
                    center = Offset(cx, cy),
                    style = Stroke(width = this.size.minDimension * 0.045f),
                )
                rotate(degrees = slowOrbit * 0.5f, pivot = Offset(cx, cy)) {
                    drawArc(
                        color = primary,
                        startAngle = -55f,
                        sweepAngle = 210f,
                        useCenter = false,
                        style = Stroke(
                            width = this.size.minDimension * 0.07f,
                            cap = StrokeCap.Round,
                        ),
                    )
                }
                // Iconic diagonal razor slash
                val slash = r * 0.72f * breathe
                drawLine(
                    color = primary,
                    start = Offset(cx - slash * 0.78f, cy + slash * 0.78f),
                    end = Offset(cx + slash * 0.78f, cy - slash * 0.78f),
                    strokeWidth = this.size.minDimension * 0.085f,
                    cap = StrokeCap.Round,
                )
            }

            AppThemeId.PAPER_LIGHT -> {
                // Editorial geometric rotating prism & warm terracotta core
                rotate(degrees = slowOrbit * 0.35f, pivot = Offset(cx, cy)) {
                    val pts = (0 until 4).map { idx ->
                        val a = (idx * PI / 2.0).toFloat()
                        Offset(cx + cos(a) * r * 0.94f, cy + sin(a) * r * 0.94f)
                    }
                    for (i in 0 until 4) {
                        drawLine(
                            color = secondary.copy(alpha = 0.8f),
                            start = pts[i],
                            end = pts[(i + 1) % 4],
                            strokeWidth = this.size.minDimension * 0.055f,
                            cap = StrokeCap.Round,
                        )
                    }
                }
                rotate(degrees = -slowOrbit * 0.35f + 45f, pivot = Offset(cx, cy)) {
                    val pts = (0 until 4).map { idx ->
                        val a = (idx * PI / 2.0).toFloat()
                        Offset(cx + cos(a) * r * 0.62f * breathe, cy + sin(a) * r * 0.62f * breathe)
                    }
                    for (i in 0 until 4) {
                        drawLine(
                            color = primary,
                            start = pts[i],
                            end = pts[(i + 1) % 4],
                            strokeWidth = this.size.minDimension * 0.06f,
                            cap = StrokeCap.Round,
                        )
                    }
                }
                drawCircle(color = primary, radius = r * 0.18f, center = Offset(cx, cy))
            }

            AppThemeId.SLATE -> {
                // Precision slate-blue double-orbit compass
                drawCircle(
                    color = primary.copy(alpha = 0.25f),
                    radius = r * 0.96f,
                    center = Offset(cx, cy),
                    style = Stroke(width = this.size.minDimension * 0.04f),
                )
                rotate(degrees = slowOrbit * 0.45f, pivot = Offset(cx, cy)) {
                    drawArc(
                        color = primary,
                        startAngle = 0f,
                        sweepAngle = 120f,
                        useCenter = false,
                        style = Stroke(width = this.size.minDimension * 0.065f, cap = StrokeCap.Round),
                    )
                    drawArc(
                        color = secondary.copy(alpha = 0.7f),
                        startAngle = 180f,
                        sweepAngle = 110f,
                        useCenter = false,
                        style = Stroke(width = this.size.minDimension * 0.05f, cap = StrokeCap.Round),
                    )
                }
                drawCircle(
                    color = primary,
                    radius = r * 0.24f * breathe,
                    center = Offset(cx, cy),
                )
            }
        }
    }
}

/**
 * Collapsible chat history sidebar (side rail on 16:9 landscape; drawer overlay on 9:16 portrait)
 * displaying a Recents box plus collapsible Batch -> Chapter folders synced with editable batches.
 */
@Composable
fun TutorHistorySidebar(
    sessions: List<TutorChatSession>,
    activeSessionId: String?,
    batches: List<StudyBatch>,
    subjectsByBatch: Map<String, List<VaultSubjectNode>>,
    onNewChat: () -> Unit,
    onSelectSession: (String) -> Unit,
    onCloseSidebar: () -> Unit,
    showCloseIcon: Boolean,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    var expandedBatchId by remember(batches) { mutableStateOf(batches.firstOrNull()?.id) }

    val chapterById = remember(subjectsByBatch) {
        subjectsByBatch.values.flatten().flatMap { it.chapters }.associateBy { it.id }
    }

    Surface(
        color = tokens.chatSidebarBg,
        border = BorderStroke(1.dp, tokens.hairlineRest),
        shape = RoundedCornerShape(18.dp),
        modifier = modifier
            .fillMaxHeight()
            .testTag("ai_tutor_history_sidebar"),
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Top bar: New Chat button + optional close
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = tokens.primaryCta,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("sidebar_new_chat_button")
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(role = Role.Button, onClick = onNewChat),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = stringResource(R.string.grok_tutor_new_chat),
                            tint = tokens.onPrimaryCta,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.grok_tutor_new_chat),
                            style = MaterialTheme.typography.labelLarge,
                            color = tokens.onPrimaryCta,
                            maxLines = 1,
                        )
                    }
                }

                if (showCloseIcon) {
                    IconButton(
                        onClick = onCloseSidebar,
                        modifier = Modifier.testTag("sidebar_close_button"),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Close history sidebar",
                            tint = tokens.inkSecondary,
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 12.dp),
            ) {
                // RECENTS SECTION
                item(key = "recents_header") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.History,
                            contentDescription = null,
                            tint = tokens.inkMuted,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = stringResource(R.string.grok_tutor_recents_label),
                            style = MicroTagMono,
                            color = tokens.inkMuted,
                        )
                    }
                }

                items(
                    items = sessions.sortedByDescending { it.updatedAtMillis }.take(5),
                    key = { "recent_${it.id}" },
                ) { session ->
                    val isSelected = session.id == activeSessionId
                    val chapterSubtitle = session.linkedChapterIds
                        .mapNotNull { chapterById[it]?.title }
                        .joinToString(" · ")
                        .ifEmpty { "General Study" }

                    SidebarSessionRow(
                        title = session.title,
                        subtitle = chapterSubtitle,
                        timestamp = session.updatedAtLabel,
                        selected = isSelected,
                        onClick = { onSelectSession(session.id) },
                    )
                }

                // BATCH -> CHAPTER FOLDERS
                item(key = "folders_header") {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.FolderOpen,
                            contentDescription = null,
                            tint = tokens.inkMuted,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = stringResource(R.string.grok_tutor_folders_label),
                            style = MicroTagMono,
                            color = tokens.inkMuted,
                        )
                    }
                }

                items(items = batches, key = { "folder_batch_${it.id}" }) { batch ->
                    val batchSessions = sessions.filter { it.batchId == batch.id }
                    val isExpanded = expandedBatchId == batch.id
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            shape = RoundedCornerShape(11.dp),
                            color = tokens.obsidianSurface,
                            border = BorderStroke(1.dp, tokens.hairlineSubtle),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(11.dp))
                                .clickable(role = Role.Button) {
                                    expandedBatchId = if (isExpanded) null else batch.id
                                },
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(batch.accentMood.accent),
                                    )
                                    Text(
                                        text = batch.name,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = tokens.inkPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                                Text(
                                    text = "${batchSessions.size}",
                                    style = MicroTagMono,
                                    color = tokens.inkMuted,
                                )
                                Icon(
                                    imageVector = if (isExpanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                                    contentDescription = null,
                                    tint = tokens.inkMuted,
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        }

                        AnimatedVisibility(visible = isExpanded) {
                            Column(
                                modifier = Modifier.padding(start = 10.dp),
                                verticalArrangement = Arrangement.spacedBy(5.dp),
                            ) {
                                if (batchSessions.isEmpty()) {
                                    Text(
                                        text = "No chats in this batch yet",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = tokens.inkMuted,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    )
                                } else {
                                    batchSessions.forEach { session ->
                                        val chapterLabel = session.linkedChapterIds
                                            .mapNotNull { chapterById[it]?.title }
                                            .joinToString(", ")
                                            .ifEmpty { "Linked Chapter" }
                                        SidebarSessionRow(
                                            title = session.title,
                                            subtitle = chapterLabel,
                                            timestamp = session.updatedAtLabel,
                                            selected = session.id == activeSessionId,
                                            onClick = { onSelectSession(session.id) },
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
}

@Composable
private fun SidebarSessionRow(
    title: String,
    subtitle: String,
    timestamp: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    Surface(
        shape = RoundedCornerShape(11.dp),
        color = if (selected) tokens.slateElevated else tokens.obsidianSurface.copy(alpha = 0.55f),
        border = BorderStroke(
            1.dp,
            if (selected) tokens.hairlineSpecularTop else tokens.hairlineSubtle,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(11.dp))
            .clickable(role = Role.Button, onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.ChatBubbleOutline,
                contentDescription = null,
                tint = if (selected) tokens.themeAccent else tokens.inkMuted,
                modifier = Modifier.size(15.dp),
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = tokens.inkPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "$subtitle · $timestamp",
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

/**
 * Choosing Window allowing the student to browse their editable Batch -> Subject -> Chapter
 * hierarchy and link ONE or MULTIPLE chapters to a single AI Tutor chat.
 */
@Composable
fun ChapterPickerDialog(
    batches: List<StudyBatch>,
    subjectsByBatch: Map<String, List<VaultSubjectNode>>,
    initialBatchId: String,
    initialSelectedChapterIds: List<String>,
    onConfirm: (batchId: String, selectedChapterIds: List<String>) -> Unit,
    onDismiss: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    var activeBatchId by remember(initialBatchId, batches) {
        mutableStateOf(
            batches.firstOrNull { it.id == initialBatchId }?.id
                ?: batches.firstOrNull()?.id.orEmpty(),
        )
    }
    var selectedIds by remember(initialSelectedChapterIds) {
        mutableStateOf(initialSelectedChapterIds.toSet())
    }

    val batchSubjects = subjectsByBatch[activeBatchId].orEmpty()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = tokens.obsidianSurface,
            border = BorderStroke(1.dp, tokens.hairlineRest),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .heightIn(max = 580.dp)
                .testTag("ai_tutor_chapter_picker_dialog"),
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.grok_tutor_picker_title),
                            style = MaterialTheme.typography.titleLarge,
                            color = tokens.inkPrimary,
                        )
                        Text(
                            text = stringResource(R.string.grok_tutor_picker_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.inkSecondary,
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Close chapter picker",
                            tint = tokens.inkSecondary,
                        )
                    }
                }

                // Batch selector pills
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    items(items = batches, key = { "pick_batch_${it.id}" }) { batch ->
                        val active = batch.id == activeBatchId
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (active) tokens.primaryCta else tokens.carbonSurface,
                            border = BorderStroke(1.dp, if (active) tokens.primaryCta else tokens.hairlineRest),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable(role = Role.Tab) { activeBatchId = batch.id },
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (active) tokens.onPrimaryCta else batch.accentMood.accent),
                                )
                                Text(
                                    text = batch.name,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (active) tokens.onPrimaryCta else tokens.inkPrimary,
                                    maxLines = 1,
                                )
                            }
                        }
                    }
                }

                // Subject -> Chapter checklist
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    batchSubjects.forEach { subject ->
                        item(key = "subj_hdr_${subject.id}") {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(top = 4.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(subject.accentMood.accent),
                                )
                                Text(
                                    text = subject.name.uppercase(),
                                    style = MicroTagMono,
                                    color = tokens.inkMuted,
                                )
                            }
                        }

                        items(items = subject.chapters, key = { "pick_ch_${it.id}" }) { chapter: VaultChapterNode ->
                            val isChecked = chapter.id in selectedIds
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isChecked) tokens.slateElevated else tokens.carbonSurface,
                                border = BorderStroke(
                                    1.dp,
                                    if (isChecked) tokens.themeAccent.copy(alpha = 0.65f) else tokens.hairlineSubtle,
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("chapter_picker_item_${chapter.id}")
                                    .minimumInteractiveComponentSize()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable(role = Role.Checkbox) {
                                        selectedIds = if (isChecked) {
                                            selectedIds - chapter.id
                                        } else {
                                            selectedIds + chapter.id
                                        }
                                    },
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                ) {
                                    Icon(
                                        imageVector = if (isChecked) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = if (isChecked) tokens.themeAccent else tokens.inkMuted,
                                        modifier = Modifier.size(20.dp),
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${chapter.chapterCode} · ${chapter.title}",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = tokens.inkPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                        Text(
                                            text = "${chapter.lectures.size} lectures · Scopes chapter diagram RAG",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = tokens.inkSecondary,
                                            maxLines = 1,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Footer action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "${selectedIds.size} chapter(s) selected",
                        style = MonoBadgeStyle,
                        color = tokens.inkSecondary,
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (selectedIds.isNotEmpty()) tokens.primaryCta else tokens.trackSubtle,
                        modifier = Modifier
                            .testTag("chapter_picker_confirm_button")
                            .minimumInteractiveComponentSize()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(
                                enabled = selectedIds.isNotEmpty(),
                                role = Role.Button,
                                onClick = { onConfirm(activeBatchId, selectedIds.toList()) },
                            ),
                    ) {
                        Text(
                            text = stringResource(R.string.grok_tutor_picker_confirm),
                            style = MaterialTheme.typography.labelLarge,
                            color = if (selectedIds.isNotEmpty()) tokens.onPrimaryCta else tokens.inkMuted,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        )
                    }
                }
            }
        }
    }
}

/**
 * Displays the chapter-scoped private diagram RAG index (filenames only passed via invisible prompt)
 * and lets the student preview any stored diagram directly.
 */
@Composable
fun ChapterRagIndexDialog(
    scopedDiagrams: List<ChapterDiagramRef>,
    onPreviewDiagram: (ChapterDiagramRef) -> Unit,
    onDismiss: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = tokens.obsidianSurface,
            border = BorderStroke(1.dp, tokens.hairlineRest),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .heightIn(max = 480.dp)
                .testTag("chapter_rag_index_dialog"),
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.grok_tutor_rag_index_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = tokens.inkPrimary,
                        )
                        Text(
                            text = stringResource(R.string.grok_tutor_rag_index_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.inkSecondary,
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Close RAG index",
                            tint = tokens.inkSecondary,
                        )
                    }
                }

                if (scopedDiagrams.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = tokens.carbonSurface,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = "No pre-made diagrams saved for the linked chapter(s) yet. Ask the AI Tutor to draw or visualize a concept and it will save a reusable image.<name>.html file privately.",
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.inkSecondary,
                            modifier = Modifier.padding(14.dp),
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f, fill = false),
                    ) {
                        items(items = scopedDiagrams, key = { it.fileName }) { ref ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = tokens.carbonSurface,
                                border = BorderStroke(1.dp, tokens.hairlineSubtle),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable(role = Role.Button) { onPreviewDiagram(ref) },
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Code,
                                        contentDescription = null,
                                        tint = tokens.themeAccent,
                                        modifier = Modifier.size(18.dp),
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = ref.fileName,
                                            style = MonoBadgeStyle,
                                            color = tokens.themeAccent,
                                        )
                                        Text(
                                            text = ref.title,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = tokens.inkPrimary,
                                        )
                                        Text(
                                            text = ref.chapterTitle,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = tokens.inkMuted,
                                        )
                                    }
                                    Text(
                                        text = "Open",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = tokens.inkPrimary,
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
