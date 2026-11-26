package com.example.ui.vault

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FolderOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.StudyBatch
import com.example.model.VaultMaterial
import com.example.model.VaultUiState
import com.example.ui.components.ObsidianCard
import com.example.ui.components.StaggeredEntrance
import com.example.ui.components.tactileScale
import com.example.ui.theme.AccentMood
import com.example.ui.theme.GhickinTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultScreen(
    vaultState: VaultUiState,
    batches: List<StudyBatch>,
    activeThemeMood: AccentMood,
    isWideLayout: Boolean,
    onSelectBatchFilter: (String?) -> Unit,
    onSelectSubjectFilter: (String?) -> Unit,
    onSelectChapterFilter: (String?) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onClearFilters: () -> Unit,
    onPreviewMaterial: (String) -> Unit,
    onClosePreview: () -> Unit,
    onOpenPdfReader: (String) -> Unit,
    onClosePdfReader: () -> Unit,
    onToggleReaderInfo: () -> Unit,
    onStepReaderPage: (Int) -> Unit,
    onJumpReaderToPage: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors

    // Derive cascading Batch -> Subject -> Chapter lists cleanly
    val availableSubjects = remember(batches, vaultState.selectedBatchId) {
        val scopedBatches = if (vaultState.selectedBatchId != null) {
            batches.filter { it.id == vaultState.selectedBatchId }
        } else {
            batches
        }
        scopedBatches.flatMap { it.subjects }.distinctBy { it.id }
    }

    val availableChapters = remember(vaultState.materials, vaultState.selectedBatchId, vaultState.selectedSubjectId) {
        vaultState.materials
            .filter { mat ->
                (vaultState.selectedBatchId == null || mat.batchId == vaultState.selectedBatchId) &&
                    (vaultState.selectedSubjectId == null || mat.subjectId == vaultState.selectedSubjectId)
            }
            .map { it.chapterTitle }
            .distinct()
    }

    val filteredMaterials = remember(
        vaultState.materials,
        vaultState.selectedBatchId,
        vaultState.selectedSubjectId,
        vaultState.selectedChapter,
        vaultState.searchQuery,
    ) {
        val q = vaultState.searchQuery.trim()
        vaultState.materials.filter { mat ->
            val matchBatch = vaultState.selectedBatchId == null || mat.batchId == vaultState.selectedBatchId
            val matchSubj = vaultState.selectedSubjectId == null || mat.subjectId == vaultState.selectedSubjectId
            val matchChap = vaultState.selectedChapter == null || mat.chapterTitle == vaultState.selectedChapter
            val matchQuery = q.isEmpty() ||
                mat.title.contains(q, ignoreCase = true) ||
                mat.chapterTitle.contains(q, ignoreCase = true) ||
                mat.subjectName.contains(q, ignoreCase = true) ||
                mat.type.label.contains(q, ignoreCase = true)
            matchBatch && matchSubj && matchChap && matchQuery
        }
    }

    // Smooth subject accent interpolation based on active preview/filter selection
    val selectedSubjectMood = availableSubjects
        .firstOrNull { it.id == vaultState.selectedSubjectId }
        ?.accentMood
        ?: vaultState.previewedMaterial?.subjectMood
        ?: activeThemeMood

    val animatedFocusAccent by animateColorAsState(
        targetValue = selectedSubjectMood.accent,
        animationSpec = spring(dampingRatio = 0.86f, stiffness = 320f),
        label = "vaultFocusAccent",
    )

    // Smooth switch between Vault Browser (with live split preview) and Full Reader Deck (preview collapsed!)
    AnimatedContent(
        targetState = vaultState.openedReaderMaterial,
        contentKey = { it?.id ?: "vault_browser_mode" },
        transitionSpec = {
            fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(160))
        },
        modifier = modifier
            .fillMaxSize()
            .testTag("vault_screen"),
        label = "vaultReaderModeSwitch",
    ) { openedMaterial ->
        if (openedMaterial != null) {
            VaultMockPdfReader(
                material = openedMaterial,
                currentPage = vaultState.readerCurrentPage,
                showInfoDrawer = vaultState.showReaderInfoDrawer,
                isWideLayout = isWideLayout,
                onCloseReader = onClosePdfReader,
                onToggleInfoDrawer = onToggleReaderInfo,
                onStepPage = onStepReaderPage,
                onJumpToPage = onJumpReaderToPage,
            )
        } else {
            VaultBrowserStage(
                vaultState = vaultState,
                batches = batches,
                availableSubjects = availableSubjects,
                availableChapters = availableChapters,
                filteredMaterials = filteredMaterials,
                focusAccent = animatedFocusAccent,
                isWideLayout = isWideLayout,
                onSelectBatchFilter = onSelectBatchFilter,
                onSelectSubjectFilter = onSelectSubjectFilter,
                onSelectChapterFilter = onSelectChapterFilter,
                onSearchQueryChange = onSearchQueryChange,
                onClearFilters = onClearFilters,
                onPreviewMaterial = onPreviewMaterial,
                onClosePreview = onClosePreview,
                onOpenPdfReader = onOpenPdfReader,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VaultBrowserStage(
    vaultState: VaultUiState,
    batches: List<StudyBatch>,
    availableSubjects: List<com.example.model.BatchSubject>,
    availableChapters: List<String>,
    filteredMaterials: List<VaultMaterial>,
    focusAccent: Color,
    isWideLayout: Boolean,
    onSelectBatchFilter: (String?) -> Unit,
    onSelectSubjectFilter: (String?) -> Unit,
    onSelectChapterFilter: (String?) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onClearFilters: () -> Unit,
    onPreviewMaterial: (String) -> Unit,
    onClosePreview: () -> Unit,
    onOpenPdfReader: (String) -> Unit,
) {
    val tokens = GhickinTokens.colors
    val previewedMaterial = vaultState.previewedMaterial
    val showWideSplitPreview = isWideLayout && previewedMaterial != null

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 1320.dp)
                .padding(
                    horizontal = if (isWideLayout) 24.dp else 16.dp,
                    vertical = if (isWideLayout) 20.dp else 16.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            VaultFilterHeader(
                batches = batches,
                selectedBatchId = vaultState.selectedBatchId,
                availableSubjects = availableSubjects,
                selectedSubjectId = vaultState.selectedSubjectId,
                availableChapters = availableChapters,
                selectedChapter = vaultState.selectedChapter,
                searchQuery = vaultState.searchQuery,
                resultCount = filteredMaterials.size,
                focusAccent = focusAccent,
                isWideLayout = isWideLayout,
                onSelectBatch = onSelectBatchFilter,
                onSelectSubject = onSelectSubjectFilter,
                onSelectChapter = onSelectChapterFilter,
                onSearchChange = onSearchQueryChange,
            )

            // Main Split Stage: Material Grid (left) + Live Right-Hand Split Preview Pane (right)
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(if (showWideSplitPreview) 16.dp else 0.dp),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                ) {
                    if (filteredMaterials.isEmpty()) {
                        VaultEmptyState(
                            accentColor = focusAccent,
                            onReset = onClearFilters,
                        )
                    } else {
                        val columns = when {
                            showWideSplitPreview -> GridCells.Adaptive(minSize = 260.dp)
                            isWideLayout -> GridCells.Adaptive(minSize = 290.dp)
                            else -> GridCells.Fixed(1)
                        }

                        LazyVerticalGrid(
                            columns = columns,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 16.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("vault_material_grid"),
                        ) {
                            itemsIndexed(
                                items = filteredMaterials,
                                key = { _, item -> item.id },
                            ) { index, material ->
                                StaggeredEntrance(delayMillis = (index % 6) * 40L) {
                                    VaultMaterialCard(
                                        material = material,
                                        selected = material.id == vaultState.previewedMaterialId,
                                        onClick = { onPreviewMaterial(material.id) },
                                    )
                                }
                            }
                        }
                    }
                }

                // Live Right-Hand Split Preview Pane on landscape — collapses smoothly with spring physics
                val sizeSpring = spring<IntSize>(dampingRatio = 0.86f, stiffness = 360f)
                val offsetSpring = spring<IntOffset>(dampingRatio = 0.86f, stiffness = 360f)
                AnimatedVisibility(
                    visible = showWideSplitPreview,
                    enter = fadeIn(tween(200)) +
                        expandHorizontally(animationSpec = sizeSpring, expandFrom = Alignment.End) +
                        slideInHorizontally(animationSpec = offsetSpring) { it / 4 },
                    exit = fadeOut(tween(150)) +
                        shrinkHorizontally(animationSpec = sizeSpring, shrinkTowards = Alignment.End) +
                        slideOutHorizontally(animationSpec = offsetSpring) { it / 4 },
                ) {
                    if (previewedMaterial != null) {
                        VaultPreviewPane(
                            material = previewedMaterial,
                            onClose = onClosePreview,
                            onOpenPdf = { onOpenPdfReader(previewedMaterial.id) },
                            modifier = Modifier
                                .width(360.dp)
                                .fillMaxHeight(),
                        )
                    }
                }
            }
        }

        // Portrait fallback: clean modal preview sheet (also collapses cleanly when Open PDF is tapped)
        if (!isWideLayout && previewedMaterial != null) {
            ModalBottomSheet(
                onDismissRequest = onClosePreview,
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = tokens.obsidianSurface,
                scrimColor = Color.Black.copy(alpha = 0.72f),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    VaultPreviewPane(
                        material = previewedMaterial,
                        onClose = onClosePreview,
                        onOpenPdf = { onOpenPdfReader(previewedMaterial.id) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun VaultEmptyState(
    accentColor: Color,
    onReset: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val interaction = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("vault_empty_state"),
        contentAlignment = Alignment.Center,
    ) {
        ObsidianCard(
            modifier = Modifier.widthIn(max = 420.dp),
            containerColor = tokens.obsidianSurface,
            cornerRadius = 22.dp,
            contentPadding = PaddingValues(26.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector = Icons.Rounded.FolderOff,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(34.dp),
                )
                Text(
                    text = stringResource(R.string.vault_empty_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = tokens.inkPrimary,
                )
                Text(
                    text = stringResource(R.string.vault_empty_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkSecondary,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = tokens.carbonSurface,
                    border = BorderStroke(1.dp, accentColor.copy(alpha = 0.42f)),
                    modifier = Modifier
                        .testTag("vault_reset_filters_button")
                        .minimumInteractiveComponentSize()
                        .tactileScale(interaction, pressedScale = 0.96f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = interaction,
                            indication = ripple(color = accentColor.copy(alpha = 0.18f)),
                            role = Role.Button,
                            onClick = onReset,
                        ),
                ) {
                    Text(
                        text = stringResource(R.string.vault_reset_filters),
                        style = MaterialTheme.typography.labelLarge,
                        color = accentColor,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    )
                }
            }
        }
    }
}
