package com.example.ui.vault

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.FolderCopy
import androidx.compose.material.icons.rounded.PlayCircleOutline
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Style
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material.icons.rounded.Tune
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.PwVaultUiState
import com.example.model.StudyBatch
import com.example.model.VaultCategorySubView
import com.example.model.VaultChapterNode
import com.example.model.VaultLectureNode
import com.example.model.VaultPaletteFocus
import com.example.model.VaultSubjectNode
import com.example.ui.components.AnimatedLinearProgressBar
import com.example.ui.components.ObsidianCard
import com.example.ui.components.StaggeredEntrance
import com.example.ui.components.tactileScale
import com.example.ui.theme.AccentMood
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MicroTagMono
import com.example.ui.theme.MonoBadgeStyle

@Composable
fun PwVaultScreen(
    state: PwVaultUiState,
    batches: List<StudyBatch>,
    isWideLayout: Boolean,
    onCycleBatch: () -> Unit,
    onSelectSubject: (String?) -> Unit,
    onSelectChapter: (String?) -> Unit,
    onSelectLecture: (String?) -> Unit,
    onSelectSubView: (VaultCategorySubView) -> Unit,
    onToggleExpandClassNote: (String) -> Unit,
    onToggleExpandFlashcardChapter: (String) -> Unit,
    onSelectPreviewFlashcard: (String) -> Unit,
    onFlipPreviewFlashcard: () -> Unit,
    onSelectRevisionSubject: (String) -> Unit,
    onOpenCommandPalette: (VaultPaletteFocus) -> Unit,
    onCloseCommandPalette: () -> Unit,
    onUpdatePaletteQuery: (String) -> Unit,
    onSelectPaletteSubjectFilter: (String?) -> Unit,
    onSelectPaletteCategoryFilter: (VaultCategorySubView?) -> Unit,
    onClearPaletteFilters: () -> Unit,
    onJumpToLectureFromPalette: (VaultLectureNode, VaultCategorySubView) -> Unit,
    modifier: Modifier = Modifier,
) {
    val activeBatch = batches.firstOrNull { it.id == state.selectedBatchId } ?: batches.first()

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("pw_vault_screen"),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 1320.dp)
                .padding(
                    horizontal = if (isWideLayout) 26.dp else 16.dp,
                    vertical = if (isWideLayout) 20.dp else 14.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // 1. Clean Top Bar — NO inline search bars or multi-row filter chip clutter
            VaultCleanTopBar(
                activeBatch = activeBatch,
                hasMultipleBatches = batches.size > 1,
                hasActiveFilter = state.hasActivePaletteFilter,
                onCycleBatch = onCycleBatch,
                onOpenSearch = { onOpenCommandPalette(VaultPaletteFocus.SEARCH) },
                onOpenFilter = { onOpenCommandPalette(VaultPaletteFocus.FILTER) },
            )

            // 2. Collapsible PW-Style Breadcrumb Trail + Morphing Category Mode Strip
            if (isWideLayout) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    CollapsibleVaultBreadcrumbs(
                        batchName = activeBatch.name,
                        subject = state.selectedSubject,
                        chapter = state.selectedChapter,
                        lecture = state.selectedLecture,
                        onResetToBatch = { onSelectSubject(null) },
                        onSelectSubjectCrumb = { onSelectSubject(state.selectedSubjectId) },
                        onSelectChapterCrumb = { onSelectChapter(state.selectedChapterId) },
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    VaultCategoryMorphSwitcher(
                        activeSubView = state.activeSubView,
                        onSelectSubView = onSelectSubView,
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    CollapsibleVaultBreadcrumbs(
                        batchName = activeBatch.name,
                        subject = state.selectedSubject,
                        chapter = state.selectedChapter,
                        lecture = state.selectedLecture,
                        onResetToBatch = { onSelectSubject(null) },
                        onSelectSubjectCrumb = { onSelectSubject(state.selectedSubjectId) },
                        onSelectChapterCrumb = { onSelectChapter(state.selectedChapterId) },
                    )
                    VaultCategoryMorphSwitcher(
                        activeSubView = state.activeSubView,
                        onSelectSubView = onSelectSubView,
                    )
                }
            }

            // 3. Collapsible / Morphing Stage Transition between Hierarchy & Distinct Sub-Views
            AnimatedContent(
                targetState = state.activeSubView,
                transitionSpec = {
                    (fadeIn(tween(220)) + scaleIn(
                        initialScale = 0.97f,
                        animationSpec = spring(dampingRatio = 0.85f, stiffness = 400f),
                    )) togetherWith (fadeOut(tween(150)) + scaleOut(
                        targetScale = 0.98f,
                        animationSpec = spring(dampingRatio = 0.9f, stiffness = 440f),
                    ))
                },
                label = "vaultCategoryMorphTransition",
                modifier = Modifier.weight(1f),
            ) { subView ->
                when (subView) {
                    VaultCategorySubView.HIERARCHY_BROWSE -> VaultHierarchyBrowser(
                        state = state,
                        isWideLayout = isWideLayout,
                        onSelectSubject = { onSelectSubject(it) },
                        onSelectChapter = { onSelectChapter(it) },
                        onSelectLecture = { onSelectLecture(it) },
                        onOpenLectureSubView = { lecture, target ->
                            onJumpToLectureFromPalette(lecture, target)
                        },
                    )

                    VaultCategorySubView.CLASS_NOTES -> VaultClassNotesSubView(
                        state = state,
                        onToggleExpandNote = onToggleExpandClassNote,
                    )

                    VaultCategorySubView.FLASHCARDS -> VaultFlashcardsSubView(
                        state = state,
                        isWideLayout = isWideLayout,
                        onToggleChapter = onToggleExpandFlashcardChapter,
                        onSelectPreviewCard = onSelectPreviewFlashcard,
                        onFlipPreviewCard = onFlipPreviewFlashcard,
                    )

                    VaultCategorySubView.REVISION -> VaultRevisionSubView(
                        state = state,
                        onSelectRevisionSubject = onSelectRevisionSubject,
                    )
                }
            }
        }

        // Sleek Icon-Triggered Expandable Command Palette Overlay
        VaultCommandPaletteOverlay(
            state = state,
            onDismiss = onCloseCommandPalette,
            onQueryChange = onUpdatePaletteQuery,
            onSelectSubjectFilter = onSelectPaletteSubjectFilter,
            onSelectCategoryFilter = onSelectPaletteCategoryFilter,
            onClearFilters = onClearPaletteFilters,
            onJumpToLecture = onJumpToLectureFromPalette,
        )
    }
}

@Composable
private fun VaultCleanTopBar(
    activeBatch: StudyBatch,
    hasMultipleBatches: Boolean,
    hasActiveFilter: Boolean,
    onCycleBatch: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenFilter: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val primary = MaterialTheme.colorScheme.primary
    val batchAccent = activeBatch.accentMood.accent

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = stringResource(R.string.vault_pw_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = tokens.inkPrimary,
                )

                val batchInteraction = remember { MutableInteractionSource() }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = tokens.obsidianSurface,
                    border = BorderStroke(1.dp, batchAccent.copy(alpha = 0.36f)),
                    modifier = Modifier
                        .testTag("vault_batch_switcher_pill")
                        .tactileScale(batchInteraction, 0.96f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(
                            enabled = hasMultipleBatches,
                            interactionSource = batchInteraction,
                            indication = ripple(color = batchAccent.copy(alpha = 0.16f)),
                            role = Role.Button,
                            onClick = onCycleBatch,
                        ),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(batchAccent),
                        )
                        Text(
                            text = activeBatch.name,
                            style = MaterialTheme.typography.labelMedium,
                            color = tokens.inkPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        if (hasMultipleBatches) {
                            Icon(
                                imageVector = Icons.Rounded.SwapHoriz,
                                contentDescription = "Switch Vault Batch",
                                tint = tokens.inkMuted,
                                modifier = Modifier.size(14.dp),
                            )
                        }
                    }
                }
            }
            Text(
                text = stringResource(R.string.vault_pw_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = tokens.inkSecondary,
            )
        }

        // Strictly two clean icon buttons triggering the Expandable Command Palette
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            VaultTopIconButton(
                icon = Icons.Rounded.Search,
                contentDesc = stringResource(R.string.vault_icon_search_desc),
                showActiveDot = false,
                dotColor = primary,
                testTag = "vault_open_search_palette",
                onClick = onOpenSearch,
            )
            VaultTopIconButton(
                icon = Icons.Rounded.Tune,
                contentDesc = stringResource(R.string.vault_icon_filter_desc),
                showActiveDot = hasActiveFilter,
                dotColor = primary,
                testTag = "vault_open_filter_palette",
                onClick = onOpenFilter,
            )
        }
    }
}

@Composable
private fun VaultTopIconButton(
    icon: ImageVector,
    contentDesc: String,
    showActiveDot: Boolean,
    dotColor: Color,
    testTag: String,
    onClick: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val interaction = remember { MutableInteractionSource() }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(48.dp)
            .testTag(testTag)
            .tactileScale(interaction, 0.92f)
            .clip(CircleShape)
            .background(tokens.obsidianSurface)
            .clickable(
                interactionSource = interaction,
                indication = ripple(color = dotColor.copy(alpha = 0.18f)),
                role = Role.Button,
                onClick = onClick,
            ),
    ) {
        Surface(
            shape = CircleShape,
            color = Color.Transparent,
            border = BorderStroke(1.dp, if (showActiveDot) dotColor.copy(alpha = 0.45f) else tokens.hairlineRest),
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDesc,
                    tint = if (showActiveDot) dotColor else tokens.inkPrimary,
                    modifier = Modifier.size(20.dp),
                )
                if (showActiveDot) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(dotColor),
                    )
                }
            }
        }
    }
}

@Composable
private fun CollapsibleVaultBreadcrumbs(
    batchName: String,
    subject: VaultSubjectNode?,
    chapter: VaultChapterNode?,
    lecture: VaultLectureNode?,
    onResetToBatch: () -> Unit,
    onSelectSubjectCrumb: () -> Unit,
    onSelectChapterCrumb: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors

    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .animateContentSize(spring(dampingRatio = 0.85f, stiffness = 400f))
            .testTag("vault_breadcrumb_trail"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        BreadcrumbChip(
            text = batchName,
            isTerminal = subject == null,
            accentColor = MaterialTheme.colorScheme.primary,
            onClick = onResetToBatch,
        )

        if (subject != null) {
            BreadcrumbChevron()
            BreadcrumbChip(
                text = subject.name,
                isTerminal = chapter == null,
                accentColor = subject.accentMood.accent,
                onClick = onSelectSubjectCrumb,
            )
        }

        if (chapter != null) {
            BreadcrumbChevron()
            BreadcrumbChip(
                text = "${chapter.chapterCode} · ${chapter.title}",
                isTerminal = lecture == null,
                accentColor = chapter.subjectMood.accent,
                onClick = onSelectChapterCrumb,
            )
        }

        if (lecture != null) {
            BreadcrumbChevron()
            BreadcrumbChip(
                text = lecture.lectureCode,
                isTerminal = true,
                accentColor = lecture.subjectMood.accent,
                onClick = {},
            )
        }
    }
}

@Composable
private fun BreadcrumbChevron() {
    Icon(
        imageVector = Icons.Rounded.ChevronRight,
        contentDescription = null,
        tint = GhickinTokens.colors.inkMuted,
        modifier = Modifier.size(16.dp),
    )
}

@Composable
private fun BreadcrumbChip(
    text: String,
    isTerminal: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val interaction = remember { MutableInteractionSource() }
    Surface(
        shape = RoundedCornerShape(9.dp),
        color = if (isTerminal) tokens.slateElevated else tokens.obsidianSurface,
        border = BorderStroke(
            1.dp,
            if (isTerminal) accentColor.copy(alpha = 0.36f) else tokens.hairlineSubtle,
        ),
        modifier = Modifier
            .tactileScale(interaction, 0.96f)
            .clip(RoundedCornerShape(9.dp))
            .clickable(
                enabled = !isTerminal,
                interactionSource = interaction,
                indication = ripple(color = accentColor.copy(alpha = 0.14f)),
                role = Role.Button,
                onClick = onClick,
            ),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = if (isTerminal) tokens.inkPrimary else tokens.inkSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
        )
    }
}

@Composable
private fun VaultCategoryMorphSwitcher(
    activeSubView: VaultCategorySubView,
    onSelectSubView: (VaultCategorySubView) -> Unit,
) {
    val tokens = GhickinTokens.colors
    val primary = MaterialTheme.colorScheme.primary

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = tokens.obsidianSurface,
        border = BorderStroke(1.dp, tokens.hairlineRest),
        modifier = Modifier.testTag("vault_category_morph_switcher"),
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            VaultCategorySubView.entries.forEach { mode ->
                val selected = mode == activeSubView
                val interaction = remember { MutableInteractionSource() }
                val bg by animateColorAsState(
                    targetValue = if (selected) tokens.slateElevated else Color.Transparent,
                    animationSpec = spring(stiffness = 420f),
                    label = "vaultCatBg_${mode.name}",
                )
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = bg,
                    border = if (selected) BorderStroke(1.dp, primary.copy(alpha = 0.36f)) else null,
                    modifier = Modifier
                        .testTag("vault_mode_${mode.name.lowercase()}")
                        .tactileScale(interaction, 0.96f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(
                            interactionSource = interaction,
                            indication = ripple(color = primary.copy(alpha = 0.16f)),
                            role = Role.Tab,
                            onClick = { onSelectSubView(mode) },
                        ),
                ) {
                    Text(
                        text = mode.label,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (selected) tokens.inkPrimary else tokens.inkMuted,
                        modifier = Modifier.padding(horizontal = 13.dp, vertical = 8.dp),
                    )
                }
            }
        }
    }
}

private enum class VaultHierarchyDepth {
    SUBJECTS,
    CHAPTERS,
    LECTURES,
}

@Composable
private fun VaultHierarchyBrowser(
    state: PwVaultUiState,
    isWideLayout: Boolean,
    onSelectSubject: (String) -> Unit,
    onSelectChapter: (String) -> Unit,
    onSelectLecture: (String) -> Unit,
    onOpenLectureSubView: (VaultLectureNode, VaultCategorySubView) -> Unit,
) {
    val depth = when {
        state.selectedChapter != null -> VaultHierarchyDepth.LECTURES
        state.selectedSubject != null -> VaultHierarchyDepth.CHAPTERS
        else -> VaultHierarchyDepth.SUBJECTS
    }

    AnimatedContent(
        targetState = Triple(depth, state.selectedSubject?.id, state.selectedChapter?.id),
        transitionSpec = {
            val deeper = targetState.first.ordinal >= initialState.first.ordinal
            val dir = if (deeper) 1 else -1
            (fadeIn(tween(210)) + slideInHorizontally(spring(0.86f, 400f)) { dir * (it / 16) }) togetherWith
                (fadeOut(tween(150)) + slideOutHorizontally(spring(0.86f, 400f)) { -dir * (it / 20) })
        },
        label = "vaultHierarchyDepthMorph",
    ) { (activeDepth, _, _) ->
        when (activeDepth) {
            VaultHierarchyDepth.SUBJECTS -> VaultSubjectGrid(
                subjects = state.subjectsForSelectedBatch,
                isWideLayout = isWideLayout,
                onSelectSubject = onSelectSubject,
            )

            VaultHierarchyDepth.CHAPTERS -> VaultChapterList(
                subject = state.selectedSubject ?: return@AnimatedContent,
                onSelectChapter = onSelectChapter,
            )

            VaultHierarchyDepth.LECTURES -> VaultLectureList(
                chapter = state.selectedChapter ?: return@AnimatedContent,
                selectedLectureId = state.selectedLectureId,
                onSelectLecture = onSelectLecture,
                onOpenLectureSubView = onOpenLectureSubView,
            )
        }
    }
}

@Composable
private fun VaultSubjectGrid(
    subjects: List<VaultSubjectNode>,
    isWideLayout: Boolean,
    onSelectSubject: (String) -> Unit,
) {
    val tokens = GhickinTokens.colors
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("vault_subjects_list"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 18.dp),
    ) {
        item(key = "vault_subjects_heading") {
            Text(
                text = "SELECT A SUBJECT TO EXPAND CHAPTERS & LECTURES",
                style = MicroTagMono,
                color = tokens.inkMuted,
            )
        }

        if (isWideLayout) {
            val rows = subjects.chunked(3)
            itemsIndexed(rows) { rIdx, rowSubjects ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    rowSubjects.forEachIndexed { cIdx, subject ->
                        StaggeredEntrance(
                            delayMillis = 40L * (rIdx * 3 + cIdx),
                            modifier = Modifier.weight(1f),
                        ) {
                            MorphingSubjectCard(
                                subject = subject,
                                onClick = { onSelectSubject(subject.id) },
                            )
                        }
                    }
                    repeat(3 - rowSubjects.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        } else {
            itemsIndexed(subjects, key = { _, s -> s.id }) { idx, subject ->
                StaggeredEntrance(delayMillis = 40L * idx) {
                    MorphingSubjectCard(
                        subject = subject,
                        onClick = { onSelectSubject(subject.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun MorphingSubjectCard(
    subject: VaultSubjectNode,
    onClick: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val accent = subject.accentMood.accent
    val pct = if (subject.totalLectures == 0) 0 else (subject.completedLectures * 100) / subject.totalLectures

    ObsidianCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("vault_subject_card_${subject.id}"),
        containerColor = tokens.obsidianSurface,
        accentEdgeColor = accent.copy(alpha = 0.34f),
        surfaceWashColor = accent.copy(alpha = 0.05f),
        cornerRadius = 20.dp,
        contentPadding = PaddingValues(18.dp),
        onClick = onClick,
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
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(tokens.carbonSurface),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.FolderCopy,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Text(
                    text = "${subject.chapters.size} CH · ${subject.totalLectures} LEC",
                    style = MicroTagMono,
                    color = tokens.inkMuted,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = subject.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = tokens.inkPrimary,
                )
                Text(
                    text = "${subject.completedLectures}/${subject.totalLectures} lectures completed",
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkSecondary,
                )
            }

            AnimatedLinearProgressBar(
                progressPercent = pct,
                accentColor = accent,
                height = 5.dp,
            )
        }
    }
}

@Composable
private fun VaultChapterList(
    subject: VaultSubjectNode,
    onSelectChapter: (String) -> Unit,
) {
    val tokens = GhickinTokens.colors
    val accent = subject.accentMood.accent

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("vault_chapters_list"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 18.dp),
    ) {
        item(key = "vault_chapters_heading") {
            Text(
                text = "${subject.name.uppercase()} CHAPTERS · TAP TO OPEN LECTURES",
                style = MicroTagMono,
                color = tokens.inkMuted,
            )
        }

        itemsIndexed(subject.chapters, key = { _, ch -> ch.id }) { idx, chapter ->
            val pct = if (chapter.lectures.isEmpty()) 0 else (chapter.completedLectures * 100) / chapter.lectures.size
            StaggeredEntrance(delayMillis = 40L * idx) {
                ObsidianCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("vault_chapter_card_${chapter.id}"),
                    containerColor = tokens.obsidianSurface,
                    accentEdgeColor = accent.copy(alpha = 0.30f),
                    cornerRadius = 18.dp,
                    contentPadding = PaddingValues(18.dp),
                    onClick = { onSelectChapter(chapter.id) },
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = tokens.carbonSurface,
                            border = BorderStroke(1.dp, accent.copy(alpha = 0.34f)),
                        ) {
                            Text(
                                text = chapter.chapterCode,
                                style = MonoBadgeStyle,
                                color = accent,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            )
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(5.dp),
                        ) {
                            Text(
                                text = chapter.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = tokens.inkPrimary,
                            )
                            Text(
                                text = "${chapter.lectures.size} Lectures · ${chapter.totalFlashcards} Flashcards · Notes & Revision Ready",
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.inkSecondary,
                            )
                            AnimatedLinearProgressBar(
                                progressPercent = pct,
                                accentColor = accent,
                                height = 4.dp,
                            )
                        }

                        Icon(
                            imageVector = Icons.Rounded.ChevronRight,
                            contentDescription = "Open chapter",
                            tint = tokens.inkSecondary,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VaultLectureList(
    chapter: VaultChapterNode,
    selectedLectureId: String?,
    onSelectLecture: (String) -> Unit,
    onOpenLectureSubView: (VaultLectureNode, VaultCategorySubView) -> Unit,
) {
    val tokens = GhickinTokens.colors
    val accent = chapter.subjectMood.accent

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("vault_lectures_list"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 20.dp),
    ) {
        item(key = "vault_lectures_heading") {
            Text(
                text = "${chapter.chapterCode} · ${chapter.title.uppercase()} · LECTURE STREAM",
                style = MicroTagMono,
                color = tokens.inkMuted,
            )
        }

        itemsIndexed(chapter.lectures, key = { _, lec -> lec.id }) { idx, lecture ->
            val expanded = lecture.id == selectedLectureId || (selectedLectureId == null && idx == 0)
            StaggeredEntrance(delayMillis = 40L * idx) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = tokens.obsidianSurface,
                    border = BorderStroke(
                        1.dp,
                        if (expanded) accent.copy(alpha = 0.42f) else tokens.hairlineRest,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("vault_lecture_card_${lecture.id}")
                        .animateContentSize(spring(dampingRatio = 0.84f, stiffness = 380f))
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onSelectLecture(lecture.id) },
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
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
                                    imageVector = if (lecture.isCompleted) Icons.Rounded.CheckCircle else Icons.Rounded.PlayCircleOutline,
                                    contentDescription = null,
                                    tint = if (lecture.isCompleted) AccentMood.EMERALD.accent else accent,
                                    modifier = Modifier.size(21.dp),
                                )
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp),
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Text(
                                        text = "${lecture.lectureCode} · ${lecture.durationLabel} · ${lecture.dateLabel}",
                                        style = MicroTagMono,
                                        color = tokens.inkMuted,
                                    )
                                    if (lecture.isAutoSavedFromYesterday) {
                                        Icon(
                                            imageVector = Icons.Rounded.CloudDone,
                                            contentDescription = "Auto-Saved",
                                            tint = AccentMood.EMERALD.accent,
                                            modifier = Modifier.size(13.dp),
                                        )
                                    }
                                }
                                Text(
                                    text = lecture.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = tokens.inkPrimary,
                                )
                            }
                        }

                        if (expanded) {
                            Text(
                                text = lecture.notesSummary,
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.inkSecondary,
                            )

                            // 3 Direct Morph Buttons into the Lecture's Distinct Sub-Views
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                LectureSubViewActionChip(
                                    icon = Icons.Rounded.Description,
                                    label = "Class Notes",
                                    meta = "${lecture.notesPageCount}p · RAG",
                                    accent = accent,
                                    testTag = "vault_lec_open_notes_${lecture.id}",
                                    onClick = { onOpenLectureSubView(lecture, VaultCategorySubView.CLASS_NOTES) },
                                    modifier = Modifier.weight(1f),
                                )
                                LectureSubViewActionChip(
                                    icon = Icons.Rounded.Style,
                                    label = "Flashcards",
                                    meta = "${lecture.flashcardDeck.size} cards",
                                    accent = AccentMood.AMBER.accent,
                                    testTag = "vault_lec_open_cards_${lecture.id}",
                                    onClick = { onOpenLectureSubView(lecture, VaultCategorySubView.FLASHCARDS) },
                                    modifier = Modifier.weight(1f),
                                )
                                LectureSubViewActionChip(
                                    icon = Icons.Rounded.AutoStories,
                                    label = "Revision",
                                    meta = "Digest",
                                    accent = AccentMood.EMERALD.accent,
                                    testTag = "vault_lec_open_rev_${lecture.id}",
                                    onClick = { onOpenLectureSubView(lecture, VaultCategorySubView.REVISION) },
                                    modifier = Modifier.weight(1f),
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
private fun LectureSubViewActionChip(
    icon: ImageVector,
    label: String,
    meta: String,
    accent: Color,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val interaction = remember { MutableInteractionSource() }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = tokens.carbonSurface,
        border = BorderStroke(1.dp, tokens.hairlineSubtle),
        modifier = modifier
            .testTag(testTag)
            .minimumInteractiveComponentSize()
            .tactileScale(interaction, 0.96f)
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = interaction,
                indication = ripple(color = accent.copy(alpha = 0.16f)),
                role = Role.Button,
                onClick = onClick,
            ),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(17.dp),
            )
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = tokens.inkPrimary,
                    maxLines = 1,
                )
                Text(
                    text = meta,
                    style = MicroTagMono,
                    color = tokens.inkMuted,
                    maxLines = 1,
                )
            }
        }
    }
}
