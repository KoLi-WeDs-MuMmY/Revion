package com.example.ui.vault

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
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
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Flip
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
import com.example.model.VaultChapterNode
import com.example.model.VaultLectureNode
import com.example.model.VaultSubjectNode
import com.example.ui.components.ObsidianCard
import com.example.ui.components.StaggeredEntrance
import com.example.ui.components.tactileScale
import com.example.ui.theme.AccentMood
import com.example.ui.theme.FormulaMonoDisplay
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.HumanistReadingBody
import com.example.ui.theme.MicroTagMono
import com.example.ui.theme.MonoBadgeStyle

/**
 * SUB-VIEW 1: Class Notes & RAG Metadata
 * Clean expandable lecture note cards with crisp, low-key monochrome/graphite RAG metadata tags.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VaultClassNotesSubView(
    state: PwVaultUiState,
    onToggleExpandNote: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val lectures = remember(state.selectedBatchId, state.selectedSubjectId, state.selectedChapterId, state.subjectsByBatch) {
        when {
            state.selectedChapter != null -> state.selectedChapter!!.lectures
            state.selectedSubject != null -> state.selectedSubject!!.chapters.flatMap { it.lectures }
            else -> state.allLecturesInBatch
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("vault_subview_class_notes_list"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 20.dp),
    ) {
        item(key = "notes_section_meta") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "LECTURE NOTES ARCHIVE · ${lectures.size} DOCUMENTS",
                    style = MicroTagMono,
                    color = tokens.inkMuted,
                )
                Text(
                    text = "Tap any note to inspect summary & RAG index",
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkSecondary,
                )
            }
        }

        itemsIndexed(items = lectures, key = { _, lec -> lec.id }) { index, lecture ->
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
            if (expanded) tokens.hairlineSpecularTop.copy(alpha = 0.24f) else tokens.hairlineRest,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("vault_class_note_${lecture.id}")
            .animateContentSize(spring(dampingRatio = 0.84f, stiffness = 380f))
            .tactileScale(interaction, 0.99f)
            .clip(RoundedCornerShape(18.dp))
            .clickable(
                interactionSource = interaction,
                indication = ripple(color = subjectColor.copy(alpha = 0.12f)),
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
                        tint = subjectColor,
                        modifier = Modifier.size(20.dp),
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = "${lecture.subjectName.uppercase()} · ${lecture.chapterTitle} · ${lecture.lectureCode}",
                        style = MicroTagMono,
                        color = tokens.inkMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
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

            // Crisp, low-key monochrome/graphite RAG metadata tags (NOT bright neon badges)
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
                                    .background(subjectColor.copy(alpha = 0.75f)),
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
 * SUB-VIEW 2: Flashcards — Strictly Grouped Chapter -> Class
 * Distinct 2-level collapsible Chapter accordions expanding into Class rows + inline 3D flip preview.
 */
@Composable
fun VaultFlashcardsSubView(
    state: PwVaultUiState,
    isWideLayout: Boolean,
    onToggleChapter: (String) -> Unit,
    onSelectPreviewCard: (String) -> Unit,
    onFlipPreviewCard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val chapters = remember(state.selectedBatchId, state.selectedSubjectId, state.subjectsByBatch) {
        if (state.selectedSubject != null) {
            state.selectedSubject!!.chapters
        } else {
            state.allChaptersInBatch
        }
    }
    val allCards = remember(chapters) {
        chapters.flatMap { ch -> ch.lectures.flatMap { it.flashcardDeck } }
    }
    val activePreviewCard = allCards.firstOrNull { it.id == state.previewFlashcardId }
        ?: allCards.firstOrNull()

    Column(
        modifier = modifier
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
                text = stringResource(R.string.vault_flashcards_grouping_hint).uppercase(),
                style = MicroTagMono,
                color = tokens.inkMuted,
            )
            Text(
                text = "${chapters.size} Chapters · ${allCards.size} Cards",
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
                // Left (~54%): Chapter -> Class 2-level Accordion Tree
                LazyColumn(
                    modifier = Modifier
                        .weight(0.54f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 16.dp),
                ) {
                    items(items = chapters, key = { it.id }) { chapter ->
                        FlashcardChapterAccordion(
                            chapter = chapter,
                            expanded = state.expandedFlashcardChapterId == chapter.id,
                            activeCardId = activePreviewCard?.id,
                            onToggleChapter = { onToggleChapter(chapter.id) },
                            onSelectCard = onSelectPreviewCard,
                        )
                    }
                }

                // Right (~46%): Inline 3D Tactile Flip Preview Card
                Box(
                    modifier = Modifier
                        .weight(0.46f)
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
                items(items = chapters, key = { it.id }) { chapter ->
                    FlashcardChapterAccordion(
                        chapter = chapter,
                        expanded = state.expandedFlashcardChapterId == chapter.id,
                        activeCardId = activePreviewCard?.id,
                        onToggleChapter = { onToggleChapter(chapter.id) },
                        onSelectCard = onSelectPreviewCard,
                    )
                }
            }
        }
    }
}

@Composable
private fun FlashcardChapterAccordion(
    chapter: VaultChapterNode,
    expanded: Boolean,
    activeCardId: String?,
    onToggleChapter: () -> Unit,
    onSelectCard: (String) -> Unit,
) {
    val tokens = GhickinTokens.colors
    val accent = chapter.subjectMood.accent
    val headerInteraction = remember { MutableInteractionSource() }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = tokens.obsidianSurface,
        border = BorderStroke(
            1.dp,
            if (expanded) accent.copy(alpha = 0.36f) else tokens.hairlineRest,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("vault_fc_chapter_${chapter.id}")
            .animateContentSize(spring(dampingRatio = 0.84f, stiffness = 380f)),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Level 1: Chapter Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .minimumInteractiveComponentSize()
                    .clickable(
                        interactionSource = headerInteraction,
                        indication = ripple(color = accent.copy(alpha = 0.14f)),
                        role = Role.Button,
                        onClick = onToggleChapter,
                    )
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .clip(CircleShape)
                        .background(accent),
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${chapter.chapterCode} · ${chapter.subjectName.uppercase()}",
                        style = MicroTagMono,
                        color = tokens.inkMuted,
                    )
                    Text(
                        text = chapter.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = tokens.inkPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = tokens.carbonSurface,
                    border = BorderStroke(1.dp, tokens.hairlineSubtle),
                ) {
                    Text(
                        text = "${chapter.totalFlashcards} cards",
                        style = MicroTagMono,
                        color = tokens.inkSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                    contentDescription = if (expanded) "Collapse chapter" else "Expand chapter",
                    tint = tokens.inkSecondary,
                    modifier = Modifier.size(20.dp),
                )
            }

            // Level 2: Class / Lecture Rows nested inside this Chapter
            if (expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    chapter.lectures.forEach { lecture ->
                        val isClassSelected = lecture.flashcardDeck.any { it.id == activeCardId }
                        val classInteraction = remember { MutableInteractionSource() }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isClassSelected) tokens.slateElevated else tokens.carbonSurface,
                            border = BorderStroke(
                                1.dp,
                                if (isClassSelected) accent.copy(alpha = 0.42f) else tokens.hairlineSubtle,
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("vault_fc_class_${lecture.id}")
                                .clip(RoundedCornerShape(12.dp))
                                .clickable(
                                    interactionSource = classInteraction,
                                    indication = ripple(color = accent.copy(alpha = 0.14f)),
                                    role = Role.Button,
                                    onClick = {
                                        lecture.flashcardDeck.firstOrNull()?.let { onSelectCard(it.id) }
                                    },
                                ),
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 13.dp, vertical = 10.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Style,
                                            contentDescription = null,
                                            tint = if (isClassSelected) accent else tokens.inkMuted,
                                            modifier = Modifier.size(15.dp),
                                        )
                                        Text(
                                            text = lecture.classLabel,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = tokens.inkPrimary,
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

                                // Individual card pills inside this Class
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
                                            color = if (selectedCard) accent.copy(alpha = 0.16f) else tokens.obsidianSurface,
                                            border = BorderStroke(
                                                1.dp,
                                                if (selectedCard) accent.copy(alpha = 0.45f) else tokens.hairlineSubtle,
                                            ),
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable { onSelectCard(card.id) },
                                        ) {
                                            Text(
                                                text = "Card 0${idx + 1}",
                                                style = MicroTagMono,
                                                color = if (selectedCard) accent else tokens.inkSecondary,
                                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
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
        shape = RoundedCornerShape(22.dp),
        color = if (showingBack) tokens.carbonSurface else tokens.obsidianSurface,
        border = BorderStroke(1.dp, if (showingBack) accent.copy(alpha = 0.44f) else tokens.hairlineRest),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 250.dp)
            .testTag("vault_inline_flashcard_preview")
            .tactileScale(interaction, 0.985f)
            .graphicsLayer {
                this.rotationY = rotationY
                cameraDistance = 14f * density
            }
            .clip(RoundedCornerShape(22.dp))
            .clickable(
                interactionSource = interaction,
                indication = ripple(color = accent.copy(alpha = 0.14f)),
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
                    Text(
                        text = "${card.chapterTitle.uppercase()} · ${card.classLabel}",
                        style = MicroTagMono,
                        color = tokens.inkMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Flip,
                            contentDescription = null,
                            tint = accent,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = if (showingBack) "ANSWER" else "TAP TO FLIP",
                            style = MicroTagMono,
                            color = accent,
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
                            color = accent,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                        )
                    }
                }
            }
        }
    }
}

/**
 * SUB-VIEW 3: Revision — Strictly Organized Subject -> Chapter -> Lecture
 * Distinct 3-tier hierarchy: Top Subject Selector Strip -> Chapter Section Rails -> Indented Lecture Digests.
 */
@Composable
fun VaultRevisionSubView(
    state: PwVaultUiState,
    onSelectRevisionSubject: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val subjects = state.subjectsForSelectedBatch
    val activeSubject: VaultSubjectNode? = subjects.firstOrNull { it.id == state.selectedRevisionSubjectId }
        ?: subjects.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("vault_subview_revision_tree"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.vault_revision_grouping_hint).uppercase(),
                style = MicroTagMono,
                color = tokens.inkMuted,
            )
            if (activeSubject != null) {
                Text(
                    text = "${activeSubject.chapters.size} Chapters · ${activeSubject.totalLectures} Lecture Sheets",
                    style = MonoBadgeStyle,
                    color = tokens.inkSecondary,
                )
            }
        }

        // TIER 1: Subject Selector Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            subjects.forEach { subject ->
                val selected = subject.id == activeSubject?.id
                val accent = subject.accentMood.accent
                val interaction = remember { MutableInteractionSource() }
                val bg by animateColorAsState(
                    targetValue = if (selected) tokens.slateElevated else tokens.obsidianSurface,
                    animationSpec = spring(stiffness = 400f),
                    label = "revSubjBg_${subject.id}",
                )
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = bg,
                    border = BorderStroke(
                        1.dp,
                        if (selected) accent.copy(alpha = 0.45f) else tokens.hairlineRest,
                    ),
                    modifier = Modifier
                        .testTag("vault_rev_subject_tab_${subject.id}")
                        .minimumInteractiveComponentSize()
                        .tactileScale(interaction, 0.96f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable(
                            interactionSource = interaction,
                            indication = ripple(color = accent.copy(alpha = 0.16f)),
                            role = Role.Tab,
                            onClick = { onSelectRevisionSubject(subject.id) },
                        ),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(accent),
                        )
                        Text(
                            text = subject.name,
                            style = MaterialTheme.typography.titleSmall,
                            color = if (selected) tokens.inkPrimary else tokens.inkSecondary,
                        )
                        Text(
                            text = "${subject.totalLectures}",
                            style = MicroTagMono,
                            color = tokens.inkMuted,
                        )
                    }
                }
            }
        }

        // TIER 2 (Chapters) -> TIER 3 (Lectures)
        if (activeSubject != null) {
            AnimatedContent(
                targetState = activeSubject,
                contentKey = { it.id },
                transitionSpec = {
                    (fadeIn(tween(200)) + slideInVertically(spring(0.86f, 400f)) { it / 24 }) togetherWith
                        fadeOut(tween(140))
                },
                label = "vaultRevisionSubjectSwitch",
                modifier = Modifier.weight(1f),
            ) { subjectNode ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 20.dp),
                ) {
                    items(items = subjectNode.chapters, key = { it.id }) { chapter ->
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Tier 2: Chapter Rail Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(7.dp),
                                    color = tokens.carbonSurface,
                                    border = BorderStroke(1.dp, subjectNode.accentMood.accent.copy(alpha = 0.34f)),
                                ) {
                                    Text(
                                        text = chapter.chapterCode,
                                        style = MicroTagMono,
                                        color = subjectNode.accentMood.accent,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    )
                                }
                                Text(
                                    text = chapter.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = tokens.inkPrimary,
                                )
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(1.dp)
                                        .background(tokens.hairlineSubtle),
                                )
                            }

                            // Tier 3: Indented Lecture Revision Digest Sheets
                            chapter.lectures.forEach { lecture ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .height(110.dp)
                                            .clip(RoundedCornerShape(100.dp))
                                            .background(subjectNode.accentMood.accent.copy(alpha = 0.32f)),
                                    )

                                    ObsidianCard(
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("vault_rev_lecture_${lecture.id}"),
                                        containerColor = tokens.obsidianSurface,
                                        cornerRadius = 16.dp,
                                        contentPadding = PaddingValues(15.dp),
                                    ) {
                                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                            ) {
                                                Text(
                                                    text = "${lecture.lectureCode} · ${lecture.title}",
                                                    style = MaterialTheme.typography.titleSmall,
                                                    color = tokens.inkPrimary,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier = Modifier.weight(1f),
                                                )
                                                if (lecture.isAutoSavedFromYesterday) {
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = "YESTERDAY'S AI DIGEST",
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
                                                    color = subjectNode.accentMood.accent,
                                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
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
    }
}
