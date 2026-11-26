package com.example.ui.vault

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.BatchSubject
import com.example.model.StudyBatch
import com.example.ui.components.KineticFilterChip
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MonoBadgeStyle

@Composable
fun VaultFilterHeader(
    batches: List<StudyBatch>,
    selectedBatchId: String?,
    availableSubjects: List<BatchSubject>,
    selectedSubjectId: String?,
    availableChapters: List<String>,
    selectedChapter: String?,
    searchQuery: String,
    resultCount: Int,
    focusAccent: Color,
    isWideLayout: Boolean,
    onSelectBatch: (String?) -> Unit,
    onSelectSubject: (String?) -> Unit,
    onSelectChapter: (String?) -> Unit,
    onSearchChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Title + live search row
        if (isWideLayout) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = stringResource(R.string.vault_title),
                        style = MaterialTheme.typography.headlineMedium,
                        color = tokens.inkPrimary,
                    )
                    Text(
                        text = stringResource(R.string.vault_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.inkSecondary,
                    )
                }

                VaultSearchBar(
                    query = searchQuery,
                    accentColor = focusAccent,
                    onQueryChange = onSearchChange,
                    modifier = Modifier.widthIn(min = 280.dp, max = 380.dp),
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = stringResource(R.string.vault_title),
                        style = MaterialTheme.typography.headlineMedium,
                        color = tokens.inkPrimary,
                    )
                    Text(
                        text = stringResource(R.string.vault_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.inkSecondary,
                    )
                }
                VaultSearchBar(
                    query = searchQuery,
                    accentColor = focusAccent,
                    onQueryChange = onSearchChange,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        // Row 1: Batch hierarchy chips + result counter
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = "BATCH",
                style = MonoBadgeStyle,
                color = tokens.inkMuted,
            )
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                KineticFilterChip(
                    label = stringResource(R.string.vault_filter_all_batches),
                    selected = selectedBatchId == null,
                    accentColor = focusAccent,
                    onClick = { onSelectBatch(null) },
                    modifier = Modifier.testTag("vault_batch_chip_all"),
                )
                batches.forEach { batch ->
                    KineticFilterChip(
                        label = batch.name,
                        selected = selectedBatchId == batch.id,
                        accentColor = batch.accentMood.accent,
                        leadingDotColor = batch.accentMood.accent,
                        onClick = { onSelectBatch(batch.id) },
                        modifier = Modifier.testTag("vault_batch_chip_${batch.id}"),
                    )
                }
            }
            Text(
                text = "$resultCount FILES",
                style = MonoBadgeStyle,
                color = tokens.inkMuted,
            )
        }

        // Row 2: Subject filter chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = "SUBJ ",
                style = MonoBadgeStyle,
                color = tokens.inkMuted,
            )
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                KineticFilterChip(
                    label = stringResource(R.string.vault_filter_all_subjects),
                    selected = selectedSubjectId == null,
                    accentColor = focusAccent,
                    onClick = { onSelectSubject(null) },
                    modifier = Modifier.testTag("vault_subject_chip_all"),
                )
                availableSubjects.forEach { subject ->
                    KineticFilterChip(
                        label = subject.name,
                        selected = selectedSubjectId == subject.id,
                        accentColor = subject.accentMood.accent,
                        leadingDotColor = subject.accentMood.accent,
                        onClick = { onSelectSubject(subject.id) },
                        modifier = Modifier.testTag("vault_subject_chip_${subject.id}"),
                    )
                }
            }
        }

        // Row 3: Chapter filter chips (reveals smoothly when chapters are available)
        AnimatedVisibility(
            visible = availableChapters.isNotEmpty(),
            enter = fadeIn() + expandVertically(animationSpec = spring(dampingRatio = 0.86f, stiffness = 380f)),
            exit = fadeOut() + shrinkVertically(animationSpec = spring(dampingRatio = 0.86f, stiffness = 380f)),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = "CHAP ",
                    style = MonoBadgeStyle,
                    color = tokens.inkMuted,
                )
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    KineticFilterChip(
                        label = stringResource(R.string.vault_filter_all_chapters),
                        selected = selectedChapter == null,
                        accentColor = focusAccent,
                        onClick = { onSelectChapter(null) },
                        modifier = Modifier.testTag("vault_chapter_chip_all"),
                    )
                    availableChapters.forEach { chapter ->
                        KineticFilterChip(
                            label = chapter,
                            selected = selectedChapter == chapter,
                            accentColor = focusAccent,
                            onClick = { onSelectChapter(chapter) },
                            modifier = Modifier.testTag("vault_chapter_chip_${chapter.hashCode()}"),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VaultSearchBar(
    query: String,
    accentColor: Color,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        singleLine = true,
        placeholder = {
            Text(
                text = stringResource(R.string.vault_search_hint),
                style = MaterialTheme.typography.bodySmall,
                color = tokens.inkMuted,
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = "Search Vault",
                tint = tokens.inkMuted,
                modifier = Modifier.size(18.dp),
            )
        },
        trailingIcon = if (query.isNotEmpty()) {
            {
                IconButton(
                    onClick = { onQueryChange("") },
                    modifier = Modifier.testTag("vault_search_clear_button"),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Clear search",
                        tint = tokens.inkSecondary,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        } else {
            null
        },
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = accentColor,
            unfocusedBorderColor = tokens.hairlineRest,
            focusedContainerColor = tokens.obsidianSurface,
            unfocusedContainerColor = tokens.obsidianSurface,
            focusedTextColor = tokens.inkPrimary,
            unfocusedTextColor = tokens.inkPrimary,
            cursorColor = accentColor,
        ),
        modifier = modifier.testTag("vault_search_input"),
    )
}
