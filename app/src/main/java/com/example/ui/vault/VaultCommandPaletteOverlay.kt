package com.example.ui.vault

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.PwVaultUiState
import com.example.model.VaultCategorySubView
import com.example.model.VaultLectureNode
import com.example.ui.components.tactileScale
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MicroTagMono
import com.example.ui.theme.MonoBadgeStyle

@Composable
fun VaultCommandPaletteOverlay(
    state: PwVaultUiState,
    onDismiss: () -> Unit,
    onQueryChange: (String) -> Unit,
    onSelectSubjectFilter: (String?) -> Unit,
    onSelectCategoryFilter: (VaultCategorySubView?) -> Unit,
    onClearFilters: () -> Unit,
    onJumpToLecture: (VaultLectureNode, VaultCategorySubView) -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(enabled = state.isCommandPaletteOpen, onBack = onDismiss)

    val tokens = GhickinTokens.colors
    val primary = MaterialTheme.colorScheme.primary
    val backdropInteraction = remember { MutableInteractionSource() }

    AnimatedVisibility(
        visible = state.isCommandPaletteOpen,
        enter = fadeIn(tween(180)),
        exit = fadeOut(tween(150)),
        modifier = modifier.fillMaxSize(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.74f))
                .clickable(
                    interactionSource = backdropInteraction,
                    indication = null,
                    onClick = onDismiss,
                )
                .padding(horizontal = 20.dp, vertical = 22.dp),
            contentAlignment = Alignment.TopCenter,
        ) {
            androidx.compose.animation.AnimatedVisibility(
                visible = state.isCommandPaletteOpen,
                enter = fadeIn(tween(180)) + scaleIn(
                    initialScale = 0.94f,
                    animationSpec = spring(dampingRatio = 0.84f, stiffness = 420f),
                ),
                exit = fadeOut(tween(140)) + scaleOut(
                    targetScale = 0.96f,
                    animationSpec = spring(dampingRatio = 0.9f, stiffness = 460f),
                ),
            ) {
                val panelInteraction = remember { MutableInteractionSource() }
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = tokens.obsidianSurface,
                    border = BorderStroke(1.dp, tokens.hairlineRest),
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 820.dp)
                        .heightIn(max = 560.dp)
                        .testTag("vault_command_palette_overlay")
                        .clickable(
                            interactionSource = panelInteraction,
                            indication = null,
                            onClick = {},
                        ),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        // Header row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Tune,
                                    contentDescription = null,
                                    tint = primary,
                                    modifier = Modifier.size(18.dp),
                                )
                                Text(
                                    text = stringResource(R.string.vault_palette_title),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = tokens.inkPrimary,
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                if (state.hasActivePaletteFilter) {
                                    val resetInteraction = remember { MutableInteractionSource() }
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = tokens.carbonSurface,
                                        border = BorderStroke(1.dp, tokens.hairlineRest),
                                        modifier = Modifier
                                            .testTag("vault_palette_clear_filters")
                                            .tactileScale(resetInteraction, 0.95f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable(
                                                interactionSource = resetInteraction,
                                                indication = ripple(color = primary.copy(alpha = 0.16f)),
                                                role = Role.Button,
                                                onClick = onClearFilters,
                                            ),
                                    ) {
                                        Text(
                                            text = stringResource(R.string.vault_reset_filters),
                                            style = MicroTagMono,
                                            color = tokens.inkSecondary,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = onDismiss,
                                    modifier = Modifier.testTag("vault_palette_close_button"),
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Close,
                                        contentDescription = "Close Command Palette",
                                        tint = tokens.inkSecondary,
                                    )
                                }
                            }
                        }

                        // Instant search input
                        OutlinedTextField(
                            value = state.paletteSearchQuery,
                            onValueChange = onQueryChange,
                            singleLine = true,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Search,
                                    contentDescription = null,
                                    tint = tokens.inkMuted,
                                    modifier = Modifier.size(19.dp),
                                )
                            },
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.vault_palette_search_placeholder),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = tokens.inkMuted,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = tokens.carbonSurface,
                                unfocusedContainerColor = tokens.carbonSurface,
                                focusedBorderColor = primary.copy(alpha = 0.5f),
                                unfocusedBorderColor = tokens.hairlineSubtle,
                                focusedTextColor = tokens.inkPrimary,
                                unfocusedTextColor = tokens.inkPrimary,
                                cursorColor = primary,
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("vault_palette_search_input"),
                        )

                        // Subject filter strip
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "SUBJECT",
                                style = MicroTagMono,
                                color = tokens.inkMuted,
                            )
                            PaletteFilterChip(
                                label = stringResource(R.string.vault_filter_all_subjects),
                                selected = state.paletteFilterSubjectId == null,
                                dotColor = null,
                                testTag = "palette_subject_all",
                                onClick = { onSelectSubjectFilter(null) },
                            )
                            state.subjectsForSelectedBatch.forEach { subj ->
                                PaletteFilterChip(
                                    label = subj.name,
                                    selected = state.paletteFilterSubjectId == subj.id,
                                    dotColor = subj.accentMood.accent,
                                    testTag = "palette_subject_${subj.id}",
                                    onClick = { onSelectSubjectFilter(subj.id) },
                                )
                            }
                        }

                        // Target Category filter strip
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "OPEN IN",
                                style = MicroTagMono,
                                color = tokens.inkMuted,
                            )
                            PaletteFilterChip(
                                label = "Lecture View",
                                selected = state.paletteFilterCategory == null,
                                dotColor = null,
                                testTag = "palette_cat_all",
                                onClick = { onSelectCategoryFilter(null) },
                            )
                            listOf(
                                VaultCategorySubView.CLASS_NOTES,
                                VaultCategorySubView.FLASHCARDS,
                                VaultCategorySubView.REVISION,
                            ).forEach { cat ->
                                PaletteFilterChip(
                                    label = cat.label,
                                    selected = state.paletteFilterCategory == cat,
                                    dotColor = null,
                                    testTag = "palette_cat_${cat.name.lowercase()}",
                                    onClick = { onSelectCategoryFilter(cat) },
                                )
                            }
                        }

                        // Matching results
                        val matches = state.paletteMatchingLectures
                        val defaultTarget = state.paletteFilterCategory ?: VaultCategorySubView.HIERARCHY_BROWSE

                        if (matches.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = stringResource(R.string.vault_empty_title),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = tokens.inkMuted,
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.weight(1f, fill = false),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(bottom = 4.dp),
                            ) {
                                items(items = matches, key = { it.id }) { lecture ->
                                    PaletteLectureResultRow(
                                        lecture = lecture,
                                        defaultTarget = defaultTarget,
                                        onJump = { target -> onJumpToLecture(lecture, target) },
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
private fun PaletteFilterChip(
    label: String,
    selected: Boolean,
    dotColor: Color?,
    testTag: String,
    onClick: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val primary = MaterialTheme.colorScheme.primary
    val interaction = remember { MutableInteractionSource() }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (selected) tokens.primaryCta else tokens.carbonSurface,
        border = BorderStroke(
            1.dp,
            if (selected) tokens.primaryCta else tokens.hairlineSubtle,
        ),
        modifier = Modifier
            .testTag(testTag)
            .tactileScale(interaction, 0.96f)
            .clip(RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = interaction,
                indication = ripple(color = tokens.inkPrimary.copy(alpha = 0.14f)),
                role = Role.RadioButton,
                onClick = onClick,
            ),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp),
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
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = if (selected) tokens.onPrimaryCta else tokens.inkSecondary,
            )
        }
    }
}

@Composable
private fun PaletteLectureResultRow(
    lecture: VaultLectureNode,
    defaultTarget: VaultCategorySubView,
    onJump: (VaultCategorySubView) -> Unit,
) {
    val tokens = GhickinTokens.colors
    val subjectColor = lecture.subjectMood.accent
    val rowInteraction = remember { MutableInteractionSource() }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = tokens.carbonSurface,
        border = BorderStroke(1.dp, tokens.hairlineSubtle),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("palette_result_${lecture.id}")
            .minimumInteractiveComponentSize()
            .tactileScale(rowInteraction, 0.985f)
            .clip(RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = rowInteraction,
                indication = ripple(color = subjectColor.copy(alpha = 0.14f)),
                role = Role.Button,
                onClick = { onJump(defaultTarget) },
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(subjectColor),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = "${lecture.subjectName} › ${lecture.chapterTitle} › ${lecture.lectureCode}",
                    style = MicroTagMono,
                    color = tokens.inkMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = lecture.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = tokens.inkPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                PaletteMiniJumpTag("Notes") { onJump(VaultCategorySubView.CLASS_NOTES) }
                PaletteMiniJumpTag("Cards") { onJump(VaultCategorySubView.FLASHCARDS) }
                PaletteMiniJumpTag("Rev") { onJump(VaultCategorySubView.REVISION) }
            }
        }
    }
}

@Composable
private fun PaletteMiniJumpTag(
    label: String,
    onClick: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val interaction = remember { MutableInteractionSource() }
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = tokens.obsidianSurface,
        border = BorderStroke(1.dp, tokens.hairlineRest),
        modifier = Modifier
            .tactileScale(interaction, 0.94f)
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = interaction,
                indication = ripple(color = tokens.inkPrimary.copy(alpha = 0.12f)),
                role = Role.Button,
                onClick = onClick,
            ),
    ) {
        Text(
            text = label,
            style = MonoBadgeStyle,
            color = tokens.inkSecondary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
        )
    }
}
