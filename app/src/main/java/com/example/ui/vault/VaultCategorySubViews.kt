package com.example.ui.vault

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AttachFile
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Flip
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Style
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.DailyFlashcardItem
import com.example.model.PwVaultUiState
import com.example.model.VaultCategorySubView
import com.example.model.VaultChapterNode
import com.example.model.VaultLectureNode
import com.example.model.VaultSubjectNode
import com.example.ui.components.AnimatedLinearProgressBar
import com.example.ui.components.ObsidianCard
import com.example.ui.components.StaggeredEntrance
import com.example.ui.components.tactileScale
import com.example.ui.theme.AccentMood
import com.example.ui.theme.FormulaMonoDisplay
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.HumanistReadingBody
import com.example.ui.theme.MicroTagMono
import com.example.ui.theme.MonoBadgeStyle
import com.example.ui.theme.rememberScreenLayoutMetrics

private enum class CategoryDrillDepth {
    SUBJECTS,
    CHAPTERS,
    LECTURES,
}

/**
 * Shared Horizontal Recents Rail shown inside Class Notes, Flashcards, and Revision modes.
 * Strictly renders recent items for the active category only.
 */
@Composable
private fun VaultRecentsStrip(
    heading: String,
    lectures: List<VaultLectureNode>,
    mode: VaultCategorySubView,
    onSelectRecent: (VaultLectureNode) -> Unit,
) {
    if (lectures.isEmpty()) return
    val tokens = GhickinTokens.colors

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("vault_recents_${mode.name.lowercase()}"),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
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
                text = heading.uppercase(),
                style = MicroTagMono,
                color = tokens.inkMuted,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            lectures.forEach { lecture ->
                val interaction = remember { MutableInteractionSource() }
                val primaryLine = when (mode) {
                    VaultCategorySubView.CLASS_NOTES -> lecture.notesTitle
                    VaultCategorySubView.FLASHCARDS -> lecture.classLabel
                    VaultCategorySubView.REVISION -> lecture.title
                    VaultCategorySubView.HIERARCHY_BROWSE -> lecture.title
                }
                val metaLine = when (mode) {
                    VaultCategorySubView.CLASS_NOTES -> "${lecture.notesPageCount}p · RAG"
                    VaultCategorySubView.FLASHCARDS -> "${lecture.flashcardDeck.size} cards"
                    VaultCategorySubView.REVISION -> "${lecture.lectureCode} · Digest"
                    VaultCategorySubView.HIERARCHY_BROWSE -> lecture.durationLabel
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = tokens.obsidianSurface,
                    border = BorderStroke(1.dp, tokens.hairlineRest),
                    modifier = Modifier
                        .width(236.dp)
                        .testTag("vault_recent_${mode.name.lowercase()}_${lecture.id}")
                        .tactileScale(interaction, 0.97f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable(
                            interactionSource = interaction,
                            indication = ripple(color = tokens.inkPrimary.copy(alpha = 0.12f)),
                            role = Role.Button,
                            onClick = { onSelectRecent(lecture) },
                        ),
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 13.dp, vertical = 11.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(lecture.subjectMood.accent),
                                )
                                Text(
                                    text = lecture.subjectName.uppercase(),
                                    style = MicroTagMono,
                                    color = tokens.inkMuted,
                                    maxLines = 1,
                                )
                            }
                            Text(
                                text = metaLine,
                                style = MicroTagMono,
                                color = tokens.inkSecondary,
                            )
                        }
                        Text(
                            text = primaryLine,
                            style = MaterialTheme.typography.titleSmall,
                            color = tokens.inkPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = lecture.chapterTitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = tokens.inkMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

/**
 * Shared Subject -> Chapter drill-down grid used by Class Notes, Flashcards, and Revision
 * so every mode mirrors Browse's PW-style hierarchy while surfacing ONLY its own artifact counts.
 */
@Composable
private fun CategorySubjectHierarchyGrid(
    subjects: List<VaultSubjectNode>,
    mode: VaultCategorySubView,
    recentsHeading: String,
    recentLectures: List<VaultLectureNode>,
    onSelectRecent: (VaultLectureNode) -> Unit,
    onSelectSubject: (String) -> Unit,
) {
    val tokens = GhickinTokens.colors
    val metrics = rememberScreenLayoutMetrics()
    val columns = metrics.vaultSubjectColumns.coerceAtLeast(1)

    val modeIcon = when (mode) {
        VaultCategorySubView.CLASS_NOTES -> Icons.Rounded.Description
        VaultCategorySubView.FLASHCARDS -> Icons.Rounded.Style
        VaultCategorySubView.REVISION -> Icons.Rounded.AutoStories
        VaultCategorySubView.HIERARCHY_BROWSE -> Icons.Rounded.Description
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("vault_${mode.name.lowercase()}_subjects"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 20.dp),
    ) {
        if (recentLectures.isNotEmpty()) {
            item(key = "recents_${mode.name}") {
                VaultRecentsStrip(
                    heading = recentsHeading,
                    lectures = recentLectures,
                    mode = mode,
                    onSelectRecent = onSelectRecent,
                )
            }
        }

        item(key = "subjects_heading_${mode.name}") {
            Text(
                text = "BROWSE ${mode.label.uppercase()} BY SUBJECT",
                style = MicroTagMono,
                color = tokens.inkMuted,
            )
        }

        val rows = subjects.chunked(columns)
        itemsIndexed(rows) { rIdx, rowSubjects ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                rowSubjects.forEachIndexed { cIdx, subject ->
                    val artifactSummary = when (mode) {
                        VaultCategorySubView.CLASS_NOTES -> "${subject.totalLectures} Notes PDFs · ${subject.chapters.size} Chapters"
                        VaultCategorySubView.FLASHCARDS -> {
                            val totalCards = subject.chapters.sumOf { it.totalFlashcards }
                            "$totalCards Flashcards · ${subject.chapters.size} Chapters"
                        }
                        VaultCategorySubView.REVISION -> "${subject.totalLectures} Revision Sheets · ${subject.chapters.size} Chapters"
                        VaultCategorySubView.HIERARCHY_BROWSE -> "${subject.totalLectures} Lectures"
                    }

                    StaggeredEntrance(
                        delayMillis = 35L * (rIdx * columns + cIdx),
                        modifier = Modifier.weight(1f),
                    ) {
                        ObsidianCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("vault_${mode.name.lowercase()}_subj_${subject.id}"),
                            containerColor = tokens.obsidianSurface,
                            cornerRadius = 18.dp,
                            contentPadding = PaddingValues(16.dp),
                            onClick = { onSelectSubject(subject.id) },
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(RoundedCornerShape(11.dp))
                                            .background(tokens.carbonSurface),
                                    ) {
                                        Icon(
                                            imageVector = modeIcon,
                                            contentDescription = null,
                                            tint = tokens.inkPrimary,
                                            modifier = Modifier.size(19.dp),
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(subject.accentMood.accent),
                                    )
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Text(
                                        text = subject.name,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = tokens.inkPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Text(
                                        text = artifactSummary,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = tokens.inkSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(
                                        text = "OPEN ${mode.label.uppercase()}",
                                        style = MicroTagMono,
                                        color = tokens.inkMuted,
                                    )
                                    Icon(
                                        imageVector = Icons.Rounded.ChevronRight,
                                        contentDescription = "Open ${subject.name}",
                                        tint = tokens.inkMuted,
                                        modifier = Modifier.size(18.dp),
                                    )
                                }
                            }
                        }
                    }
                }
                repeat(columns - rowSubjects.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun CategoryChapterHierarchyGrid(
    subject: VaultSubjectNode,
    mode: VaultCategorySubView,
    onSelectChapter: (String) -> Unit,
) {
    val tokens = GhickinTokens.colors
    val metrics = rememberScreenLayoutMetrics()
    val columns = metrics.vaultChapterColumns.coerceAtLeast(1)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("vault_${mode.name.lowercase()}_chapters"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 20.dp),
    ) {
        item(key = "cat_chapters_heading") {
            Text(
                text = "${subject.name.uppercase()} · SELECT CHAPTER FOR ${mode.label.uppercase()}",
                style = MicroTagMono,
                color = tokens.inkMuted,
            )
        }

        val rows = subject.chapters.chunked(columns)
        itemsIndexed(rows) { rIdx, rowChapters ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                rowChapters.forEachIndexed { cIdx, chapter ->
                    val indexNum = rIdx * columns + cIdx + 1
                    val countTag = when (mode) {
                        VaultCategorySubView.CLASS_NOTES -> "${chapter.lectures.size} PDF Notes"
                        VaultCategorySubView.FLASHCARDS -> "${chapter.totalFlashcards} Cards · ${chapter.lectures.size} Classes"
                        VaultCategorySubView.REVISION -> "${chapter.lectures.size} Lecture Digests"
                        VaultCategorySubView.HIERARCHY_BROWSE -> "${chapter.lectures.size} Lectures"
                    }
                    val pct = if (chapter.lectures.isEmpty()) 0 else (chapter.completedLectures * 100) / chapter.lectures.size

                    StaggeredEntrance(
                        delayMillis = 35L * (rIdx * columns + cIdx),
                        modifier = Modifier.weight(1f),
                    ) {
                        ObsidianCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("vault_${mode.name.lowercase()}_ch_${chapter.id}"),
                            containerColor = tokens.obsidianSurface,
                            cornerRadius = 18.dp,
                            contentPadding = PaddingValues(16.dp),
                            onClick = { onSelectChapter(chapter.id) },
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = tokens.carbonSurface,
                                            border = BorderStroke(1.dp, tokens.hairlineRest),
                                        ) {
                                            Text(
                                                text = indexNum.toString().padStart(2, '0'),
                                                style = MonoBadgeStyle,
                                                color = tokens.inkPrimary,
                                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(chapter.subjectMood.accent),
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Rounded.ChevronRight,
                                        contentDescription = "Open chapter",
                                        tint = tokens.inkMuted,
                                        modifier = Modifier.size(18.dp),
                                    )
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = chapter.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = tokens.inkPrimary,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Text(
                                        text = countTag,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = tokens.inkSecondary,
                                    )
                                }

                                AnimatedLinearProgressBar(
                                    progressPercent = pct,
                                    accentColor = chapter.subjectMood.accent,
                                    height = 3.dp,
                                )
                            }
                        }
                    }
                }
                repeat(columns - rowChapters.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/**
 * SUB-VIEW 1: Class Notes — Recents + Subject -> Chapter -> Lecture Notes PDFs ONLY
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VaultClassNotesSubView(
    state: PwVaultUiState,
    onSelectSubject: (String) -> Unit,
    onSelectChapter: (String) -> Unit,
    onOpenRecentLecture: (VaultLectureNode) -> Unit,
    onToggleExpandNote: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val depth = when {
        state.selectedChapter != null -> CategoryDrillDepth.LECTURES
        state.selectedSubject != null -> CategoryDrillDepth.CHAPTERS
        else -> CategoryDrillDepth.SUBJECTS
    }

    AnimatedContent(
        targetState = Triple(depth, state.selectedSubject?.id, state.selectedChapter?.id),
        transitionSpec = {
            val deeper = targetState.first.ordinal >= initialState.first.ordinal
            val dir = if (deeper) 1 else -1
            (fadeIn(tween(200)) + slideInHorizontally(spring(0.86f, 400f)) { dir * (it / 16) }) togetherWith
                (fadeOut(tween(140)) + slideOutHorizontally(spring(0.86f, 400f)) { -dir * (it / 20) })
        },
        label = "vaultClassNotesDepth",
        modifier = modifier.fillMaxSize(),
    ) { (activeDepth, _, _) ->
        when (activeDepth) {
            CategoryDrillDepth.SUBJECTS -> CategorySubjectHierarchyGrid(
                subjects = state.subjectsForSelectedBatch,
                mode = VaultCategorySubView.CLASS_NOTES,
                recentsHeading = stringResource(R.string.vault_recents_notes_label),
                recentLectures = state.recentNotesLectures,
                onSelectRecent = onOpenRecentLecture,
                onSelectSubject = onSelectSubject,
            )

            CategoryDrillDepth.CHAPTERS -> CategoryChapterHierarchyGrid(
                subject = state.selectedSubject ?: return@AnimatedContent,
                mode = VaultCategorySubView.CLASS_NOTES,
                onSelectChapter = onSelectChapter,
            )

            CategoryDrillDepth.LECTURES -> {
                val chapter = state.selectedChapter ?: return@AnimatedContent
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("vault_subview_class_notes_list"),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 20.dp),
                ) {
                    item(key = "notes_chapter_header") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = "${chapter.chapterCode} · CLASS NOTES ONLY (${chapter.lectures.size} PDFs)",
                                style = MicroTagMono,
                                color = tokens.inkMuted,
                            )
                            Text(
                                text = "Tap to expand summary & RAG tags",
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.inkSecondary,
                            )
                        }
                    }

                    itemsIndexed(items = chapter.lectures, key = { _, lec -> lec.id }) { index, lecture ->
                        val expanded = state.expandedClassNoteLectureId == lecture.id
                        StaggeredEntrance(delayMillis = 35L * index) {
                            ClassNoteLectureCard(
                                lecture = lecture,
                                expanded = expanded,
                                onClick = { onToggleExpandNote(lecture.id) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ClassNoteLectureCard(
    lecture: VaultLectureNode,
    expanded: Boolean,
    onClick: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val subjectColor = lecture.subjectMood.accent
    val interaction = remember { MutableInteractionSource() }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = tokens.obsidianSurface,
        border = BorderStroke(
            1.dp,
            if (expanded) tokens.inkPrimary.copy(alpha = 0.24f) else tokens.hairlineRest,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("vault_class_note_${lecture.id}")
            .animateContentSize(spring(dampingRatio = 0.84f, stiffness = 380f))
            .tactileScale(interaction, 0.99f)
            .clip(RoundedCornerShape(18.dp))
            .clickable(
                interactionSource = interaction,
                indication = ripple(color = tokens.inkPrimary.copy(alpha = 0.12f)),
                role = Role.Button,
                onClick = onClick,
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .background(tokens.carbonSurface),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Description,
                        contentDescription = null,
                        tint = tokens.inkPrimary,
                        modifier = Modifier.size(19.dp),
                    )
                }

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
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(subjectColor),
                        )
                        Text(
                            text = "${lecture.lectureCode} · ${lecture.title}",
                            style = MicroTagMono,
                            color = tokens.inkMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Text(
                        text = lecture.notesTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = tokens.inkPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Text(
                    text = "${lecture.notesPageCount}p",
                    style = MonoBadgeStyle,
                    color = tokens.inkSecondary,
                )
                Icon(
                    imageVector = if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                    contentDescription = if (expanded) "Collapse note details" else "Expand note details",
                    tint = tokens.inkMuted,
                    modifier = Modifier.size(20.dp),
                )
            }

            // Crisp, low-key monochrome RAG metadata tags
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                if (lecture.ragTutorAttachable) {
                    LowKeyMetadataTag(
                        icon = Icons.Rounded.AttachFile,
                        text = stringResource(R.string.vault_rag_tutor_tag),
                    )
                }
                if (lecture.notebookLmUpcoming) {
                    LowKeyMetadataTag(
                        icon = Icons.Rounded.Schedule,
                        text = stringResource(R.string.vault_rag_notebooklm_tag),
                    )
                }
                if (lecture.isAutoSavedFromYesterday) {
                    LowKeyMetadataTag(
                        icon = Icons.Rounded.CloudDone,
                        text = "Synced Yesterday",
                    )
                }
            }

            if (expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(tokens.carbonSurface)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = lecture.notesSummary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = tokens.inkSecondary,
                    )
                    lecture.notesHighlights.forEach { bullet ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 7.dp)
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(subjectColor),
                            )
                            Text(
                                text = bullet,
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.inkPrimary,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LowKeyMetadataTag(
    icon: ImageVector,
    text: String,
) {
    val tokens = GhickinTokens.colors
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = tokens.carbonSurface,
        border = BorderStroke(1.dp, tokens.hairlineSubtle),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tokens.inkMuted,
                modifier = Modifier.size(12.dp),
            )
            Text(
                text = text,
                style = MicroTagMono,
                color = tokens.inkSecondary,
            )
        }
    }
}

/**
 * SUB-VIEW 2: Flashcards — Recents + Subject -> Chapter -> Class/Lecture Decks ONLY + 3D Flip Preview
 */
@Composable
fun VaultFlashcardsSubView(
    state: PwVaultUiState,
    isWideLayout: Boolean,
    onSelectSubject: (String) -> Unit,
    onSelectChapter: (String) -> Unit,
    onOpenRecentLecture: (VaultLectureNode) -> Unit,
    onSelectPreviewCard: (String) -> Unit,
    onFlipPreviewCard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val depth = when {
        state.selectedChapter != null -> CategoryDrillDepth.LECTURES
        state.selectedSubject != null -> CategoryDrillDepth.CHAPTERS
        else -> CategoryDrillDepth.SUBJECTS
    }

    AnimatedContent(
        targetState = Triple(depth, state.selectedSubject?.id, state.selectedChapter?.id),
        transitionSpec = {
            val deeper = targetState.first.ordinal >= initialState.first.ordinal
            val dir = if (deeper) 1 else -1
            (fadeIn(tween(200)) + slideInHorizontally(spring(0.86f, 400f)) { dir * (it / 16) }) togetherWith
                (fadeOut(tween(140)) + slideOutHorizontally(spring(0.86f, 400f)) { -dir * (it / 20) })
        },
        label = "vaultFlashcardsDepth",
        modifier = modifier.fillMaxSize(),
    ) { (activeDepth, _, _) ->
        when (activeDepth) {
            CategoryDrillDepth.SUBJECTS -> CategorySubjectHierarchyGrid(
                subjects = state.subjectsForSelectedBatch,
                mode = VaultCategorySubView.FLASHCARDS,
                recentsHeading = stringResource(R.string.vault_recents_flashcards_label),
                recentLectures = state.recentFlashcardLectures,
                onSelectRecent = onOpenRecentLecture,
                onSelectSubject = onSelectSubject,
            )

            CategoryDrillDepth.CHAPTERS -> CategoryChapterHierarchyGrid(
                subject = state.selectedSubject ?: return@AnimatedContent,
                mode = VaultCategorySubView.FLASHCARDS,
                onSelectChapter = onSelectChapter,
            )

            CategoryDrillDepth.LECTURES -> {
                val chapter = state.selectedChapter ?: return@AnimatedContent
                val allCards = remember(chapter) { chapter.lectures.flatMap { it.flashcardDeck } }
                val activePreviewCard = allCards.firstOrNull { it.id == state.previewFlashcardId }
                    ?: allCards.firstOrNull()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("vault_subview_flashcards_tree"),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "${chapter.chapterCode} · FLASHCARDS BY CLASS (${allCards.size} CARDS)",
                            style = MicroTagMono,
                            color = tokens.inkMuted,
                        )
                        Text(
                            text = "${chapter.lectures.size} Classes",
                            style = MonoBadgeStyle,
                            color = tokens.inkSecondary,
                        )
                    }

                    if (isWideLayout) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            LazyColumn(
                                modifier = Modifier
                                    .weight(0.52f)
                                    .fillMaxHeight(),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                contentPadding = PaddingValues(bottom = 16.dp),
                            ) {
                                items(items = chapter.lectures, key = { it.id }) { lecture ->
                                    FlashcardClassDeckTile(
                                        lecture = lecture,
                                        activeCardId = activePreviewCard?.id,
                                        onSelectCard = onSelectPreviewCard,
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .weight(0.48f)
                                    .fillMaxHeight(),
                            ) {
                                if (activePreviewCard != null) {
                                    InlineVaultFlashcardPreview(
                                        card = activePreviewCard,
                                        isFlipped = state.isPreviewCardFlipped,
                                        onFlip = onFlipPreviewCard,
                                    )
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 16.dp),
                        ) {
                            if (activePreviewCard != null) {
                                item(key = "mobile_flashcard_preview") {
                                    InlineVaultFlashcardPreview(
                                        card = activePreviewCard,
                                        isFlipped = state.isPreviewCardFlipped,
                                        onFlip = onFlipPreviewCard,
                                    )
                                }
                            }
                            items(items = chapter.lectures, key = { it.id }) { lecture ->
                                FlashcardClassDeckTile(
                                    lecture = lecture,
                                    activeCardId = activePreviewCard?.id,
                                    onSelectCard = onSelectPreviewCard,
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
private fun FlashcardClassDeckTile(
    lecture: VaultLectureNode,
    activeCardId: String?,
    onSelectCard: (String) -> Unit,
) {
    val tokens = GhickinTokens.colors
    val isClassSelected = lecture.flashcardDeck.any { it.id == activeCardId }
    val interaction = remember { MutableInteractionSource() }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isClassSelected) tokens.slateElevated else tokens.obsidianSurface,
        border = BorderStroke(
            1.dp,
            if (isClassSelected) tokens.inkPrimary.copy(alpha = 0.24f) else tokens.hairlineRest,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("vault_fc_class_${lecture.id}")
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interaction,
                indication = ripple(color = tokens.inkPrimary.copy(alpha = 0.12f)),
                role = Role.Button,
                onClick = { lecture.flashcardDeck.firstOrNull()?.let { onSelectCard(it.id) } },
            ),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(lecture.subjectMood.accent),
                    )
                    Text(
                        text = lecture.classLabel,
                        style = MaterialTheme.typography.titleSmall,
                        color = tokens.inkPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (lecture.isAutoSavedFromYesterday) {
                    Text(
                        text = "AUTO-SAVED",
                        style = MicroTagMono,
                        color = AccentMood.EMERALD.accent,
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                lecture.flashcardDeck.forEachIndexed { idx, card ->
                    val selectedCard = card.id == activeCardId
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (selectedCard) tokens.primaryCta else tokens.carbonSurface,
                        border = BorderStroke(
                            1.dp,
                            if (selectedCard) tokens.primaryCta else tokens.hairlineSubtle,
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onSelectCard(card.id) },
                    ) {
                        Text(
                            text = "Card 0${idx + 1}",
                            style = MicroTagMono,
                            color = if (selectedCard) tokens.onPrimaryCta else tokens.inkSecondary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InlineVaultFlashcardPreview(
    card: DailyFlashcardItem,
    isFlipped: Boolean,
    onFlip: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val accent = card.accentMood.accent
    val density = LocalDensity.current.density
    val interaction = remember { MutableInteractionSource() }

    val rotationY by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = spring(dampingRatio = 0.78f, stiffness = 320f),
        label = "vaultPreviewCardFlip",
    )
    val showingBack = rotationY > 90f

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (showingBack) tokens.carbonSurface else tokens.obsidianSurface,
        border = BorderStroke(
            1.dp,
            if (showingBack) tokens.inkPrimary.copy(alpha = 0.28f) else tokens.hairlineRest,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 250.dp)
            .testTag("vault_inline_flashcard_preview")
            .tactileScale(interaction, 0.985f)
            .graphicsLayer {
                this.rotationY = rotationY
                cameraDistance = 14f * density
            }
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = interaction,
                indication = ripple(color = tokens.inkPrimary.copy(alpha = 0.12f)),
                role = Role.Button,
                onClick = onFlip,
            ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .graphicsLayer {
                    if (showingBack) this.rotationY = 180f
                },
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
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
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(accent),
                        )
                        Text(
                            text = card.classLabel.uppercase(),
                            style = MicroTagMono,
                            color = tokens.inkMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Flip,
                            contentDescription = null,
                            tint = tokens.inkSecondary,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = if (showingBack) "ANSWER" else "TAP TO FLIP",
                            style = MicroTagMono,
                            color = tokens.inkSecondary,
                        )
                    }
                }

                if (!showingBack) {
                    Text(
                        text = card.frontPrompt,
                        style = MaterialTheme.typography.titleLarge,
                        color = tokens.inkPrimary,
                    )
                } else {
                    Text(
                        text = card.backAnswer,
                        style = HumanistReadingBody,
                        color = tokens.inkPrimary,
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = tokens.obsidianSurface,
                        border = BorderStroke(1.dp, tokens.hairlineSubtle),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = card.formulaHint,
                            style = FormulaMonoDisplay,
                            color = tokens.inkPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                        )
                    }
                }
            }
        }
    }
}

/**
 * SUB-VIEW 3: Revision — Recents + Subject -> Chapter -> Lecture Revision Digests ONLY
 */
@Composable
fun VaultRevisionSubView(
    state: PwVaultUiState,
    onSelectSubject: (String) -> Unit,
    onSelectChapter: (String) -> Unit,
    onOpenRecentLecture: (VaultLectureNode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val depth = when {
        state.selectedChapter != null -> CategoryDrillDepth.LECTURES
        state.selectedSubject != null -> CategoryDrillDepth.CHAPTERS
        else -> CategoryDrillDepth.SUBJECTS
    }

    AnimatedContent(
        targetState = Triple(depth, state.selectedSubject?.id, state.selectedChapter?.id),
        transitionSpec = {
            val deeper = targetState.first.ordinal >= initialState.first.ordinal
            val dir = if (deeper) 1 else -1
            (fadeIn(tween(200)) + slideInHorizontally(spring(0.86f, 400f)) { dir * (it / 16) }) togetherWith
                (fadeOut(tween(140)) + slideOutHorizontally(spring(0.86f, 400f)) { -dir * (it / 20) })
        },
        label = "vaultRevisionDepth",
        modifier = modifier.fillMaxSize(),
    ) { (activeDepth, _, _) ->
        when (activeDepth) {
            CategoryDrillDepth.SUBJECTS -> CategorySubjectHierarchyGrid(
                subjects = state.subjectsForSelectedBatch,
                mode = VaultCategorySubView.REVISION,
                recentsHeading = stringResource(R.string.vault_recents_revision_label),
                recentLectures = state.recentRevisionLectures,
                onSelectRecent = onOpenRecentLecture,
                onSelectSubject = onSelectSubject,
            )

            CategoryDrillDepth.CHAPTERS -> CategoryChapterHierarchyGrid(
                subject = state.selectedSubject ?: return@AnimatedContent,
                mode = VaultCategorySubView.REVISION,
                onSelectChapter = onSelectChapter,
            )

            CategoryDrillDepth.LECTURES -> {
                val chapter = state.selectedChapter ?: return@AnimatedContent
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("vault_subview_revision_tree"),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 20.dp),
                ) {
                    item(key = "rev_chapter_header") {
                        Text(
                            text = "${chapter.chapterCode} · REVISION SHEETS ONLY (${chapter.lectures.size} LECTURES)",
                            style = MicroTagMono,
                            color = tokens.inkMuted,
                        )
                    }

                    itemsIndexed(items = chapter.lectures, key = { _, lec -> lec.id }) { idx, lecture ->
                        StaggeredEntrance(delayMillis = 35L * idx) {
                            ObsidianCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("vault_rev_lecture_${lecture.id}"),
                                containerColor = tokens.obsidianSurface,
                                cornerRadius = 18.dp,
                                contentPadding = PaddingValues(16.dp),
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.weight(1f),
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(lecture.subjectMood.accent),
                                            )
                                            Text(
                                                text = "${lecture.lectureCode} · ${lecture.title}",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = tokens.inkPrimary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                            )
                                        }
                                        if (lecture.isAutoSavedFromYesterday) {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "AUTO-SAVED",
                                                style = MicroTagMono,
                                                color = AccentMood.EMERALD.accent,
                                            )
                                        }
                                    }

                                    Text(
                                        text = lecture.revisionHeadline,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = tokens.inkSecondary,
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = tokens.carbonSurface,
                                        border = BorderStroke(1.dp, tokens.hairlineSubtle),
                                        modifier = Modifier.fillMaxWidth(),
                                    ) {
                                        Text(
                                            text = lecture.revisionFormula,
                                            style = FormulaMonoDisplay,
                                            color = tokens.inkPrimary,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                                        )
                                    }

                                    lecture.revisionKeyPoints.forEach { pt ->
                                        Row(
                                            verticalAlignment = Alignment.Top,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .padding(top = 6.dp)
                                                    .size(4.dp)
                                                    .clip(CircleShape)
                                                    .background(tokens.inkMuted),
                                            )
                                            Text(
                                                text = pt,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = tokens.inkSecondary,
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
}
