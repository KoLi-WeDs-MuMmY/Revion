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
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.FolderCopy
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Style
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.ui.theme.rememberScreenLayoutMetrics
import kotlinx.coroutines.delay

@Composable
fun PwVaultScreen(
    state: PwVaultUiState,
    batches: List<StudyBatch>,
    isWideLayout: Boolean,
    onOpenSettings: () -> Unit,
    onCycleBatch: () -> Unit,
    onSelectSubject: (String?) -> Unit,
    onSelectChapter: (String?) -> Unit,
    onSelectLecture: (String?) -> Unit,
    onSelectSubView: (VaultCategorySubView) -> Unit,
    onToggleExpandClassNote: (String) -> Unit,
    onSelectPreviewFlashcard: (String) -> Unit,
    onFlipPreviewFlashcard: () -> Unit,
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
    val metrics = rememberScreenLayoutMetrics()
    val tokens = GhickinTokens.colors

    // Calm dismissible banner state for "Play Lecture" (upcoming in next updates)
    var upcomingPlayLectureTitle by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(upcomingPlayLectureTitle) {
        if (upcomingPlayLectureTitle != null) {
            delay(3600L)
            upcomingPlayLectureTitle = null
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("pw_vault_screen"),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = metrics.maxContentWidth)
                .padding(
                    horizontal = metrics.horizontalPadding,
                    vertical = metrics.verticalPadding,
                ),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // 1. Clean Top Bar — Search, Filter & Settings behind clean icon buttons
            VaultCleanTopBar(
                activeBatch = activeBatch,
                hasMultipleBatches = batches.size > 1,
                hasActiveFilter = state.hasActivePaletteFilter,
                onCycleBatch = onCycleBatch,
                onOpenSearch = { onOpenCommandPalette(VaultPaletteFocus.SEARCH) },
                onOpenFilter = { onOpenCommandPalette(VaultPaletteFocus.FILTER) },
                onOpenSettings = onOpenSettings,
            )

            // 2. Collapsible PW-Style Breadcrumb Trail + Category Mode Switcher
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
                        lecture = if (state.activeSubView == VaultCategorySubView.HIERARCHY_BROWSE) state.selectedLecture else null,
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
                        lecture = if (state.activeSubView == VaultCategorySubView.HIERARCHY_BROWSE) state.selectedLecture else null,
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

            // 3. Stage Content across Browse + Category Subject-Hierarchy Modes
            AnimatedContent(
                targetState = state.activeSubView,
                transitionSpec = {
                    (fadeIn(tween(210)) + scaleIn(
                        initialScale = 0.975f,
                        animationSpec = spring(dampingRatio = 0.86f, stiffness = 400f),
                    )) togetherWith (fadeOut(tween(140)) + scaleOut(
                        targetScale = 0.985f,
                        animationSpec = spring(dampingRatio = 0.9f, stiffness = 440f),
                    ))
                },
                label = "vaultCategoryMorphTransition",
                modifier = Modifier.weight(1f),
            ) { subView ->
                when (subView) {
                    VaultCategorySubView.HIERARCHY_BROWSE -> VaultHierarchyBrowser(
                        state = state,
                        onSelectSubject = { onSelectSubject(it) },
                        onSelectChapter = { onSelectChapter(it) },
                        onSelectLecture = { onSelectLecture(it) },
                        onPlayLectureClick = { lecture -> upcomingPlayLectureTitle = lecture.title },
                        onOpenLectureSubView = { lecture, target ->
                            onJumpToLectureFromPalette(lecture, target)
                        },
                    )

                    VaultCategorySubView.CLASS_NOTES -> VaultClassNotesSubView(
                        state = state,
                        onSelectSubject = { onSelectSubject(it) },
                        onSelectChapter = { onSelectChapter(it) },
                        onOpenRecentLecture = { lec ->
                            onJumpToLectureFromPalette(lec, VaultCategorySubView.CLASS_NOTES)
                        },
                        onToggleExpandNote = onToggleExpandClassNote,
                    )

                    VaultCategorySubView.FLASHCARDS -> VaultFlashcardsSubView(
                        state = state,
                        isWideLayout = isWideLayout,
                        onSelectSubject = { onSelectSubject(it) },
                        onSelectChapter = { onSelectChapter(it) },
                        onOpenRecentLecture = { lec ->
                            onJumpToLectureFromPalette(lec, VaultCategorySubView.FLASHCARDS)
                        },
                        onSelectPreviewCard = onSelectPreviewFlashcard,
                        onFlipPreviewCard = onFlipPreviewFlashcard,
                    )

                    VaultCategorySubView.REVISION -> VaultRevisionSubView(
                        state = state,
                        onSelectSubject = { onSelectSubject(it) },
                        onSelectChapter = { onSelectChapter(it) },
                        onOpenRecentLecture = { lec ->
                            onJumpToLectureFromPalette(lec, VaultCategorySubView.REVISION)
                        },
                    )
                }
            }
        }

        // Calm dismissible banner when tapping "Play Lecture"
        androidx.compose.animation.AnimatedVisibility(
            visible = upcomingPlayLectureTitle != null,
            enter = fadeIn(tween(180)) + slideInVertically(spring(0.85f, 400f)) { it / 2 },
            exit = fadeOut(tween(150)) + slideOutVertically(spring(0.9f, 420f)) { it / 2 },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp, vertical = 18.dp),
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = tokens.slateElevated,
                border = BorderStroke(1.dp, tokens.inkPrimary.copy(alpha = 0.22f)),
                modifier = Modifier
                    .widthIn(max = 520.dp)
                    .testTag("vault_play_lecture_upcoming_banner"),
            ) {
                Row(
                    modifier = Modifier.padding(start = 14.dp, end = 6.dp, top = 8.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = null,
                        tint = tokens.inkPrimary,
                        modifier = Modifier.size(18.dp),
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.vault_play_lecture_upcoming_notice),
                            style = MaterialTheme.typography.labelLarge,
                            color = tokens.inkPrimary,
                        )
                        upcomingPlayLectureTitle?.let { title ->
                            Text(
                                text = title,
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.inkSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                    IconButton(
                        onClick = { upcomingPlayLectureTitle = null },
                        modifier = Modifier.testTag("vault_play_notice_dismiss"),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Dismiss notice",
                            tint = tokens.inkMuted,
                            modifier = Modifier.size(16.dp),
                        )
                    }
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
    onOpenSettings: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val batchDot = activeBatch.accentMood.accent

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
                    border = BorderStroke(1.dp, tokens.hairlineRest),
                    modifier = Modifier
                        .testTag("vault_batch_switcher_pill")
                        .tactileScale(batchInteraction, 0.96f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(
                            enabled = hasMultipleBatches,
                            interactionSource = batchInteraction,
                            indication = ripple(color = tokens.inkPrimary.copy(alpha = 0.12f)),
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
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(batchDot),
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

        // Two clean monochrome icon buttons for Search & Filter Command Palette
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            VaultTopIconButton(
                icon = Icons.Rounded.Search,
                contentDesc = stringResource(R.string.vault_icon_search_desc),
                showActiveDot = false,
                testTag = "vault_open_search_palette",
                onClick = onOpenSearch,
            )
            VaultTopIconButton(
                icon = Icons.Rounded.Tune,
                contentDesc = stringResource(R.string.vault_icon_filter_desc),
                showActiveDot = hasActiveFilter,
                testTag = "vault_open_filter_palette",
                onClick = onOpenFilter,
            )
            VaultTopIconButton(
                icon = Icons.Rounded.Settings,
                contentDesc = stringResource(R.string.settings_open_desc),
                showActiveDot = false,
                testTag = "vault_open_settings_button",
                onClick = onOpenSettings,
            )
        }
    }
}

@Composable
private fun VaultTopIconButton(
    icon: ImageVector,
    contentDesc: String,
    showActiveDot: Boolean,
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
                indication = ripple(color = tokens.inkPrimary.copy(alpha = 0.16f)),
                role = Role.Button,
                onClick = onClick,
            ),
    ) {
        Surface(
            shape = CircleShape,
            color = Color.Transparent,
            border = BorderStroke(
                1.dp,
                if (showActiveDot) tokens.inkPrimary.copy(alpha = 0.45f) else tokens.hairlineRest,
            ),
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDesc,
                    tint = tokens.inkPrimary,
                    modifier = Modifier.size(20.dp),
                )
                if (showActiveDot) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(tokens.inkPrimary),
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
            dotColor = null,
            onClick = onResetToBatch,
        )

        if (subject != null) {
            BreadcrumbChevron()
            BreadcrumbChip(
                text = subject.name,
                isTerminal = chapter == null,
                dotColor = subject.accentMood.accent,
                onClick = onSelectSubjectCrumb,
            )
        }

        if (chapter != null) {
            BreadcrumbChevron()
            BreadcrumbChip(
                text = "${chapter.chapterCode} · ${chapter.title}",
                isTerminal = lecture == null,
                dotColor = null,
                onClick = onSelectChapterCrumb,
            )
        }

        if (lecture != null) {
            BreadcrumbChevron()
            BreadcrumbChip(
                text = lecture.lectureCode,
                isTerminal = true,
                dotColor = null,
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
    dotColor: Color?,
    onClick: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val interaction = remember { MutableInteractionSource() }
    Surface(
        shape = RoundedCornerShape(9.dp),
        color = if (isTerminal) tokens.slateElevated else tokens.obsidianSurface,
        border = BorderStroke(
            1.dp,
            if (isTerminal) tokens.inkPrimary.copy(alpha = 0.24f) else tokens.hairlineSubtle,
        ),
        modifier = Modifier
            .tactileScale(interaction, 0.96f)
            .clip(RoundedCornerShape(9.dp))
            .clickable(
                enabled = !isTerminal,
                interactionSource = interaction,
                indication = ripple(color = tokens.inkPrimary.copy(alpha = 0.12f)),
                role = Role.Button,
                onClick = onClick,
            ),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            if (dotColor != null) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(dotColor),
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = if (isTerminal) tokens.inkPrimary else tokens.inkSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun VaultCategoryMorphSwitcher(
    activeSubView: VaultCategorySubView,
    onSelectSubView: (VaultCategorySubView) -> Unit,
) {
    val tokens = GhickinTokens.colors

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
                    targetValue = if (selected) tokens.primaryCta else Color.Transparent,
                    animationSpec = spring(stiffness = 420f),
                    label = "vaultCatBg_${mode.name}",
                )
                val ink = if (selected) tokens.onPrimaryCta else tokens.inkSecondary
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = bg,
                    modifier = Modifier
                        .testTag("vault_mode_${mode.name.lowercase()}")
                        .tactileScale(interaction, 0.96f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(
                            interactionSource = interaction,
                            indication = ripple(color = tokens.inkPrimary.copy(alpha = 0.14f)),
                            role = Role.Tab,
                            onClick = { onSelectSubView(mode) },
                        ),
                ) {
                    Text(
                        text = mode.label,
                        style = MaterialTheme.typography.labelLarge,
                        color = ink,
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
    onSelectSubject: (String) -> Unit,
    onSelectChapter: (String) -> Unit,
    onSelectLecture: (String) -> Unit,
    onPlayLectureClick: (VaultLectureNode) -> Unit,
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
                onSelectSubject = onSelectSubject,
            )

            VaultHierarchyDepth.CHAPTERS -> VaultChapterGrid(
                subject = state.selectedSubject ?: return@AnimatedContent,
                onSelectChapter = onSelectChapter,
            )

            VaultHierarchyDepth.LECTURES -> VaultPwLectureStream(
                chapter = state.selectedChapter ?: return@AnimatedContent,
                selectedLectureId = state.selectedLectureId,
                onSelectLecture = onSelectLecture,
                onPlayLectureClick = onPlayLectureClick,
                onOpenLectureSubView = onOpenLectureSubView,
            )
        }
    }
}

@Composable
private fun VaultSubjectGrid(
    subjects: List<VaultSubjectNode>,
    onSelectSubject: (String) -> Unit,
) {
    val tokens = GhickinTokens.colors
    val metrics = rememberScreenLayoutMetrics()
    val columns = metrics.vaultSubjectColumns.coerceAtLeast(1)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("vault_subjects_list"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 18.dp),
    ) {
        item(key = "vault_subjects_heading") {
            Text(
                text = "SELECT A SUBJECT TO BROWSE CHAPTERS & LECTURES",
                style = MicroTagMono,
                color = tokens.inkMuted,
            )
        }

        val rows = subjects.chunked(columns)
        itemsIndexed(rows) { rIdx, rowSubjects ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                rowSubjects.forEachIndexed { cIdx, subject ->
                    StaggeredEntrance(
                        delayMillis = 35L * (rIdx * columns + cIdx),
                        modifier = Modifier.weight(1f),
                    ) {
                        MorphingSubjectCard(
                            subject = subject,
                            onClick = { onSelectSubject(subject.id) },
                        )
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
private fun MorphingSubjectCard(
    subject: VaultSubjectNode,
    onClick: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val dotAccent = subject.accentMood.accent
    val pct = if (subject.totalLectures == 0) 0 else (subject.completedLectures * 100) / subject.totalLectures

    ObsidianCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("vault_subject_card_${subject.id}"),
        containerColor = tokens.obsidianSurface,
        cornerRadius = 18.dp,
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
                        tint = tokens.inkPrimary,
                        modifier = Modifier.size(19.dp),
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(dotAccent),
                    )
                    Text(
                        text = "${subject.chapters.size} CH · ${subject.totalLectures} LEC",
                        style = MicroTagMono,
                        color = tokens.inkMuted,
                    )
                }
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
                accentColor = dotAccent,
                height = 4.dp,
            )
        }
    }
}

/**
 * Upgraded Materialized PW-Style Chapters Grid (2 columns on 16:9, 1 column on 9:16).
 */
@Composable
private fun VaultChapterGrid(
    subject: VaultSubjectNode,
    onSelectChapter: (String) -> Unit,
) {
    val tokens = GhickinTokens.colors
    val metrics = rememberScreenLayoutMetrics()
    val columns = metrics.vaultChapterColumns.coerceAtLeast(1)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("vault_chapters_list"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 20.dp),
    ) {
        item(key = "vault_chapters_heading") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "${subject.name.uppercase()} CHAPTERS · ${subject.chapters.size} MODULES",
                    style = MicroTagMono,
                    color = tokens.inkMuted,
                )
                Text(
                    text = "Tap any chapter to view PW lecture cards",
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkSecondary,
                )
            }
        }

        val rows = subject.chapters.chunked(columns)
        itemsIndexed(rows) { rIdx, rowChapters ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                rowChapters.forEachIndexed { cIdx, chapter ->
                    val number = rIdx * columns + cIdx + 1
                    val pct = if (chapter.lectures.isEmpty()) 0 else (chapter.completedLectures * 100) / chapter.lectures.size

                    StaggeredEntrance(
                        delayMillis = 35L * (rIdx * columns + cIdx),
                        modifier = Modifier.weight(1f),
                    ) {
                        PwMaterializedChapterCard(
                            chapterNumber = number,
                            chapter = chapter,
                            progressPercent = pct,
                            onClick = { onSelectChapter(chapter.id) },
                        )
                    }
                }
                repeat(columns - rowChapters.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun PwMaterializedChapterCard(
    chapterNumber: Int,
    chapter: VaultChapterNode,
    progressPercent: Int,
    onClick: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val dotAccent = chapter.subjectMood.accent

    ObsidianCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("vault_chapter_card_${chapter.id}"),
        containerColor = tokens.obsidianSurface,
        cornerRadius = 18.dp,
        contentPadding = PaddingValues(18.dp),
        onClick = onClick,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Top bar: PW numbered chapter plate + subtle dot + completion %
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
                        shape = RoundedCornerShape(9.dp),
                        color = tokens.carbonSurface,
                        border = BorderStroke(1.dp, tokens.hairlineRest),
                    ) {
                        Text(
                            text = chapterNumber.toString().padStart(2, '0'),
                            style = MonoBadgeStyle,
                            color = tokens.inkPrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(dotAccent),
                    )
                    Text(
                        text = chapter.chapterCode,
                        style = MicroTagMono,
                        color = tokens.inkMuted,
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = "$progressPercent%",
                        style = MonoBadgeStyle,
                        color = tokens.inkSecondary,
                    )
                    Icon(
                        imageVector = Icons.Rounded.ChevronRight,
                        contentDescription = "Open chapter",
                        tint = tokens.inkMuted,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }

            // Chapter Title
            Text(
                text = chapter.title,
                style = MaterialTheme.typography.titleLarge,
                color = tokens.inkPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            // Materialized pill stats row (Lectures · Notes · Flashcards)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                PwMetaPill(text = "${chapter.lectures.size} Lectures")
                PwMetaPill(text = "${chapter.lectures.size} Notes")
                PwMetaPill(text = "${chapter.totalFlashcards} Cards")
            }

            AnimatedLinearProgressBar(
                progressPercent = progressPercent,
                accentColor = dotAccent,
                height = 4.dp,
            )
        }
    }
}

@Composable
private fun PwMetaPill(text: String) {
    val tokens = GhickinTokens.colors
    Surface(
        shape = RoundedCornerShape(7.dp),
        color = tokens.carbonSurface,
        border = BorderStroke(1.dp, tokens.hairlineSubtle),
    ) {
        Text(
            text = text,
            style = MicroTagMono,
            color = tokens.inkSecondary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

/**
 * Upgraded Materialized PW-Style Lecture Cards:
 * Stylized lecture thumbnail plate + Grok pure-white "Play Lecture" CTA + "More" dropdown menu
 * offering Notes, Flashcards, and Revision.
 */
@Composable
private fun VaultPwLectureStream(
    chapter: VaultChapterNode,
    selectedLectureId: String?,
    onSelectLecture: (String) -> Unit,
    onPlayLectureClick: (VaultLectureNode) -> Unit,
    onOpenLectureSubView: (VaultLectureNode, VaultCategorySubView) -> Unit,
) {
    val tokens = GhickinTokens.colors

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("vault_lectures_list"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 28.dp),
    ) {
        item(key = "vault_lectures_heading") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "${chapter.chapterCode} · ${chapter.title.uppercase()}",
                    style = MicroTagMono,
                    color = tokens.inkMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${chapter.lectures.size} LECTURES",
                    style = MicroTagMono,
                    color = tokens.inkSecondary,
                )
            }
        }

        itemsIndexed(chapter.lectures, key = { _, lec -> lec.id }) { idx, lecture ->
            val selected = lecture.id == selectedLectureId
            StaggeredEntrance(delayMillis = 35L * idx) {
                PwMaterializedLectureCard(
                    lecture = lecture,
                    selected = selected,
                    onClick = { onSelectLecture(lecture.id) },
                    onPlayClick = { onPlayLectureClick(lecture) },
                    onOpenNotes = { onOpenLectureSubView(lecture, VaultCategorySubView.CLASS_NOTES) },
                    onOpenFlashcards = { onOpenLectureSubView(lecture, VaultCategorySubView.FLASHCARDS) },
                    onOpenRevision = { onOpenLectureSubView(lecture, VaultCategorySubView.REVISION) },
                )
            }
        }
    }
}

@Composable
private fun PwMaterializedLectureCard(
    lecture: VaultLectureNode,
    selected: Boolean,
    onClick: () -> Unit,
    onPlayClick: () -> Unit,
    onOpenNotes: () -> Unit,
    onOpenFlashcards: () -> Unit,
    onOpenRevision: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val dotAccent = lecture.subjectMood.accent
    var moreMenuExpanded by remember { mutableStateOf(false) }
    val playInteraction = remember { MutableInteractionSource() }
    val moreInteraction = remember { MutableInteractionSource() }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = tokens.obsidianSurface,
        border = BorderStroke(
            1.dp,
            if (selected) tokens.inkPrimary.copy(alpha = 0.28f) else tokens.hairlineRest,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("vault_lecture_card_${lecture.id}")
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Top PW Lecture Row: Stylized Thumbnail Plate + Metadata & Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                // Stylized PW Lecture Thumbnail Plate
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = tokens.carbonSurface,
                    border = BorderStroke(1.dp, tokens.hairlineRest),
                    modifier = Modifier
                        .width(96.dp)
                        .height(64.dp),
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Subtle top-left lecture code
                        Text(
                            text = lecture.lectureCode,
                            style = MicroTagMono,
                            color = tokens.inkMuted,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(start = 8.dp, top = 6.dp),
                        )
                        // Center play glyph
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(28.dp)
                                .align(Alignment.Center)
                                .clip(CircleShape)
                                .background(tokens.slateElevated),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.PlayArrow,
                                contentDescription = null,
                                tint = tokens.inkPrimary,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                        // Bottom-right duration pill
                        Text(
                            text = lecture.durationLabel,
                            style = MicroTagMono,
                            color = tokens.inkSecondary,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 7.dp, bottom = 5.dp),
                        )
                        // Top-right completion check or subject dot
                        if (lecture.isCompleted) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = "Completed",
                                tint = AccentMood.EMERALD.accent,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(6.dp)
                                    .size(13.dp),
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(dotAccent),
                            )
                        }
                    }
                }

                // Lecture title, subtitle & auto-saved indicator
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "${lecture.subjectName.uppercase()} · ${lecture.dateLabel}",
                            style = MicroTagMono,
                            color = tokens.inkMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
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
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "${lecture.notesPageCount}p Notes · ${lecture.flashcardDeck.size} Flashcards · Revision Sheet",
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.inkSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            // Bottom PW Action Row: Pure Monochrome White "Play Lecture" CTA + Anchored "More" Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                // Grok pure-white primary Play Lecture button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = tokens.primaryCta,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("vault_play_lecture_${lecture.id}")
                        .minimumInteractiveComponentSize()
                        .tactileScale(playInteraction, 0.97f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = playInteraction,
                            indication = ripple(color = tokens.onPrimaryCta.copy(alpha = 0.2f)),
                            role = Role.Button,
                            onClick = onPlayClick,
                        ),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 11.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = null,
                            tint = tokens.onPrimaryCta,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.vault_play_lecture_cta),
                            style = MaterialTheme.typography.labelLarge,
                            color = tokens.onPrimaryCta,
                        )
                    }
                }

                // Anchored "More" button opening Class Notes / Flashcards / Revision menu
                Box {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = tokens.carbonSurface,
                        border = BorderStroke(1.dp, tokens.hairlineRest),
                        modifier = Modifier
                            .testTag("vault_lecture_more_${lecture.id}")
                            .minimumInteractiveComponentSize()
                            .tactileScale(moreInteraction, 0.95f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(
                                interactionSource = moreInteraction,
                                indication = ripple(color = tokens.inkPrimary.copy(alpha = 0.14f)),
                                role = Role.Button,
                                onClick = { moreMenuExpanded = true },
                            ),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                        ) {
                            Text(
                                text = stringResource(R.string.vault_more_options_cta),
                                style = MaterialTheme.typography.labelLarge,
                                color = tokens.inkPrimary,
                            )
                            Icon(
                                imageVector = Icons.Rounded.MoreVert,
                                contentDescription = "More study resources for ${lecture.title}",
                                tint = tokens.inkSecondary,
                                modifier = Modifier.size(17.dp),
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = moreMenuExpanded,
                        onDismissRequest = { moreMenuExpanded = false },
                        containerColor = tokens.carbonSurface,
                        border = BorderStroke(1.dp, tokens.hairlineRest),
                    ) {
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text("Class Notes", style = MaterialTheme.typography.labelLarge, color = tokens.inkPrimary)
                                    Text("${lecture.notesPageCount} pages · RAG Ready", style = MicroTagMono, color = tokens.inkMuted)
                                }
                            },
                            leadingIcon = {
                                Icon(Icons.Rounded.Description, contentDescription = null, tint = tokens.inkSecondary, modifier = Modifier.size(18.dp))
                            },
                            modifier = Modifier.testTag("vault_more_notes_${lecture.id}"),
                            onClick = {
                                moreMenuExpanded = false
                                onOpenNotes()
                            },
                        )
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text("Flashcards", style = MaterialTheme.typography.labelLarge, color = tokens.inkPrimary)
                                    Text("${lecture.flashcardDeck.size} active recall cards", style = MicroTagMono, color = tokens.inkMuted)
                                }
                            },
                            leadingIcon = {
                                Icon(Icons.Rounded.Style, contentDescription = null, tint = tokens.inkSecondary, modifier = Modifier.size(18.dp))
                            },
                            modifier = Modifier.testTag("vault_more_flashcards_${lecture.id}"),
                            onClick = {
                                moreMenuExpanded = false
                                onOpenFlashcards()
                            },
                        )
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text("Revision", style = MaterialTheme.typography.labelLarge, color = tokens.inkPrimary)
                                    Text("Formula & takeaway digest", style = MicroTagMono, color = tokens.inkMuted)
                                }
                            },
                            leadingIcon = {
                                Icon(Icons.Rounded.AutoStories, contentDescription = null, tint = tokens.inkSecondary, modifier = Modifier.size(18.dp))
                            },
                            modifier = Modifier.testTag("vault_more_revision_${lecture.id}"),
                            onClick = {
                                moreMenuExpanded = false
                                onOpenRevision()
                            },
                        )
                    }
                }
            }
        }
    }
}
