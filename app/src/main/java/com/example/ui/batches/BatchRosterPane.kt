package com.example.ui.batches

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.StarOutline
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.StudyBatch
import com.example.ui.components.AnimatedLinearProgressBar
import com.example.ui.components.ObsidianCard
import com.example.ui.components.tactileScale
import com.example.ui.theme.AccentMood
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MicroTagMono
import com.example.ui.theme.MonoBadgeStyle

@Composable
fun BatchRosterPane(
    batches: List<StudyBatch>,
    selectedBatchId: String,
    primaryBatchId: String,
    onSelectBatch: (String) -> Unit,
    onCreateBatchClick: () -> Unit,
    onSetPrimaryBatch: (String) -> Unit,
    onEditBatchClick: (StudyBatch) -> Unit,
    onAddSubjectToBatchClick: (StudyBatch) -> Unit,
    onDeleteBatchClick: (StudyBatch) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors

    ObsidianCard(
        modifier = modifier.testTag("batch_roster_pane"),
        containerColor = tokens.obsidianSurface,
        cornerRadius = 20.dp,
        contentPadding = PaddingValues(16.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Layers,
                        contentDescription = null,
                        tint = tokens.inkSecondary,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = "Study Batches",
                        style = MaterialTheme.typography.titleMedium,
                        color = tokens.inkPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Text(
                    text = "${batches.size} ACTIVE",
                    style = MicroTagMono,
                    color = tokens.inkMuted,
                    maxLines = 1,
                    softWrap = false,
                )
            }

            // Grok pure monochrome white primary CTA
            val createInteraction = remember { MutableInteractionSource() }
            val ctaShape = RoundedCornerShape(14.dp)
            Surface(
                shape = ctaShape,
                color = tokens.primaryCta,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("create_custom_batch_button")
                    .minimumInteractiveComponentSize()
                    .tactileScale(createInteraction, pressedScale = 0.965f)
                    .clip(ctaShape)
                    .clickable(
                        interactionSource = createInteraction,
                        indication = ripple(color = tokens.onPrimaryCta.copy(alpha = 0.2f)),
                        role = Role.Button,
                        onClick = onCreateBatchClick,
                    ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null,
                        tint = tokens.onPrimaryCta,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.batches_create_cta),
                        style = MaterialTheme.typography.labelLarge,
                        color = tokens.onPrimaryCta,
                    )
                }
            }

            // Batch list items with inline 3-dot CRUD menus
            Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                batches.forEach { batch ->
                    BatchRosterTile(
                        batch = batch,
                        selected = batch.id == selectedBatchId,
                        isPrimary = batch.id == primaryBatchId,
                        canDelete = batches.size > 1,
                        onClick = { onSelectBatch(batch.id) },
                        onSetPrimary = { onSetPrimaryBatch(batch.id) },
                        onEdit = { onEditBatchClick(batch) },
                        onAddSubject = { onAddSubjectToBatchClick(batch) },
                        onDelete = { onDeleteBatchClick(batch) },
                    )
                }
            }
        }
    }
}

@Composable
private fun BatchRosterTile(
    batch: StudyBatch,
    selected: Boolean,
    isPrimary: Boolean,
    canDelete: Boolean,
    onClick: () -> Unit,
    onSetPrimary: () -> Unit,
    onEdit: () -> Unit,
    onAddSubject: () -> Unit,
    onDelete: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val dotAccent = batch.accentMood.accent
    val interactionSource = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(15.dp)
    var menuExpanded by remember { mutableStateOf(false) }

    val containerColor by animateColorAsState(
        targetValue = if (selected) tokens.slateElevated else tokens.carbonSurface,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = 400f),
        label = "batchTileBg_${batch.id}",
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) tokens.inkPrimary.copy(alpha = 0.24f) else tokens.hairlineSubtle,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = 400f),
        label = "batchTileBorder_${batch.id}",
    )

    Surface(
        shape = shape,
        color = containerColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("batch_tile_${batch.id}")
            .minimumInteractiveComponentSize()
            .tactileScale(interactionSource, pressedScale = 0.985f)
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = tokens.inkPrimary.copy(alpha = 0.12f)),
                role = Role.Tab,
                onClick = onClick,
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 14.dp, end = 6.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp),
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
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(dotAccent),
                        )
                        Text(
                            text = batch.goalTag.uppercase(),
                            style = MicroTagMono,
                            color = tokens.inkMuted,
                            maxLines = 1,
                        )
                        if (isPrimary) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = tokens.obsidianSurface,
                                border = BorderStroke(1.dp, tokens.hairlineRest),
                            ) {
                                Text(
                                    text = stringResource(R.string.batches_primary_badge),
                                    style = MicroTagMono,
                                    color = tokens.inkPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                )
                            }
                        }
                    }
                    Text(
                        text = "${batch.overallProgressPercent}%",
                        style = MonoBadgeStyle,
                        color = if (selected) tokens.inkPrimary else tokens.inkSecondary,
                    )
                }

                Text(
                    text = batch.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = tokens.inkPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    batch.subjects.take(4).forEach { subject ->
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(subject.accentMood.accent),
                        )
                    }
                    Text(
                        text = "${batch.subjects.size} subjects · ${batch.totalChapters} ch",
                        style = MaterialTheme.typography.labelSmall,
                        color = tokens.inkSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                AnimatedLinearProgressBar(
                    progressPercent = batch.overallProgressPercent,
                    accentColor = tokens.inkPrimary.copy(alpha = 0.85f),
                    height = 3.dp,
                )
            }

            // Inline 3-dot menu for full batch CRUD
            Box {
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier.testTag("batch_menu_${batch.id}"),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = "Batch options for ${batch.name}",
                        tint = tokens.inkMuted,
                        modifier = Modifier.size(18.dp),
                    )
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    containerColor = tokens.carbonSurface,
                    border = BorderStroke(1.dp, tokens.hairlineRest),
                ) {
                    if (!isPrimary) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.batches_set_primary_cta), color = tokens.inkPrimary) },
                            leadingIcon = {
                                Icon(Icons.Rounded.StarOutline, contentDescription = null, tint = tokens.inkSecondary, modifier = Modifier.size(18.dp))
                            },
                            onClick = {
                                menuExpanded = false
                                onSetPrimary()
                            },
                        )
                    }
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.batches_add_subject_cta), color = tokens.inkPrimary) },
                        leadingIcon = {
                            Icon(Icons.Rounded.Add, contentDescription = null, tint = tokens.inkSecondary, modifier = Modifier.size(18.dp))
                        },
                        onClick = {
                            menuExpanded = false
                            onAddSubject()
                        },
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.batches_action_edit), color = tokens.inkPrimary) },
                        leadingIcon = {
                            Icon(Icons.Rounded.Edit, contentDescription = null, tint = tokens.inkSecondary, modifier = Modifier.size(18.dp))
                        },
                        onClick = {
                            menuExpanded = false
                            onEdit()
                        },
                    )
                    if (canDelete) {
                        val errorColor = MaterialTheme.colorScheme.error
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.batches_action_delete), color = errorColor) },
                            leadingIcon = {
                                Icon(Icons.Rounded.DeleteOutline, contentDescription = null, tint = errorColor, modifier = Modifier.size(18.dp))
                            },
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            },
                        )
                    }
                }
            }
        }
    }
}

/**
 * Compact 9:16 portrait horizontal batch switcher strip replacing the tall sidebar roster.
 * Eliminates text wrapping and vertical crowding on narrow portrait screens.
 */
@Composable
fun PortraitBatchSelectorStrip(
    batches: List<StudyBatch>,
    selectedBatchId: String,
    primaryBatchId: String,
    onSelectBatch: (String) -> Unit,
    onCreateBatchClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val createInteraction = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("portrait_batch_selector_strip"),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "STUDY BATCHES · ${batches.size} ACTIVE",
                style = MicroTagMono,
                color = tokens.inkMuted,
            )

            Surface(
                shape = RoundedCornerShape(100.dp),
                color = tokens.primaryCta,
                modifier = Modifier
                    .testTag("create_custom_batch_button")
                    .minimumInteractiveComponentSize()
                    .tactileScale(createInteraction, pressedScale = 0.95f)
                    .clip(RoundedCornerShape(100.dp))
                    .clickable(
                        interactionSource = createInteraction,
                        indication = ripple(color = tokens.onPrimaryCta.copy(alpha = 0.2f)),
                        role = Role.Button,
                        onClick = onCreateBatchClick,
                    ),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 13.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = null,
                        tint = tokens.onPrimaryCta,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = "New Batch",
                        style = MaterialTheme.typography.labelMedium,
                        color = tokens.onPrimaryCta,
                        maxLines = 1,
                    )
                }
            }
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(items = batches, key = { it.id }) { batch ->
                val selected = batch.id == selectedBatchId
                val isPrimary = batch.id == primaryBatchId
                val interaction = remember { MutableInteractionSource() }
                val bgColor by animateColorAsState(
                    targetValue = if (selected) tokens.slateElevated else tokens.obsidianSurface,
                    animationSpec = spring(stiffness = 400f),
                    label = "portraitBatchChipBg_${batch.id}",
                )
                val borderColor by animateColorAsState(
                    targetValue = if (selected) tokens.themeAccent else tokens.hairlineRest,
                    animationSpec = spring(stiffness = 400f),
                    label = "portraitBatchChipBorder_${batch.id}",
                )

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = bgColor,
                    border = BorderStroke(if (selected) 1.5.dp else 1.dp, borderColor),
                    modifier = Modifier
                        .testTag("batch_tile_${batch.id}")
                        .minimumInteractiveComponentSize()
                        .tactileScale(interaction, pressedScale = 0.96f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable(
                            interactionSource = interaction,
                            indication = ripple(color = tokens.themeAccent.copy(alpha = 0.16f)),
                            role = Role.Tab,
                            onClick = { onSelectBatch(batch.id) },
                        ),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(batch.accentMood.accent),
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Text(
                                    text = batch.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = tokens.inkPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                if (isPrimary) {
                                    Text(
                                        text = "★",
                                        style = MonoBadgeStyle,
                                        color = tokens.themeAccent,
                                    )
                                }
                            }
                            Text(
                                text = "${batch.overallProgressPercent}% · ${batch.subjects.size} subjects",
                                style = MicroTagMono,
                                color = tokens.inkSecondary,
                                maxLines = 1,
                            )
                        }
                    }
                }
            }
        }
    }
}
