package com.example.ui.batches

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Layers
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
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MonoBadgeStyle

@Composable
fun BatchRosterPane(
    batches: List<StudyBatch>,
    selectedBatchId: String,
    onSelectBatch: (String) -> Unit,
    onCreateBatchClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val primary = MaterialTheme.colorScheme.primary
    val onPrimary = MaterialTheme.colorScheme.onPrimary

    ObsidianCard(
        modifier = modifier.testTag("batch_roster_pane"),
        containerColor = tokens.obsidianSurface,
        cornerRadius = 22.dp,
        contentPadding = PaddingValues(16.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
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
                        imageVector = Icons.Rounded.Layers,
                        contentDescription = null,
                        tint = primary,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = "Study Batches",
                        style = MaterialTheme.typography.titleMedium,
                        color = tokens.inkPrimary,
                    )
                }
                Text(
                    text = "${batches.size} ACTIVE",
                    style = MonoBadgeStyle,
                    color = tokens.inkMuted,
                )
            }

            // "+ Create Custom Batch" primary CTA
            val createInteraction = remember { MutableInteractionSource() }
            val ctaShape = RoundedCornerShape(14.dp)
            Surface(
                shape = ctaShape,
                color = primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("create_custom_batch_button")
                    .minimumInteractiveComponentSize()
                    .tactileScale(createInteraction, pressedScale = 0.965f)
                    .clip(ctaShape)
                    .clickable(
                        interactionSource = createInteraction,
                        indication = ripple(color = onPrimary.copy(alpha = 0.2f)),
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
                        tint = onPrimary,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.batches_create_cta),
                        style = MaterialTheme.typography.labelLarge,
                        color = onPrimary,
                    )
                }
            }

            // Batch list items
            Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                batches.forEach { batch ->
                    BatchRosterTile(
                        batch = batch,
                        selected = batch.id == selectedBatchId,
                        onClick = { onSelectBatch(batch.id) },
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
    onClick: () -> Unit,
) {
    val tokens = GhickinTokens.colors
    val accent = batch.accentMood.accent
    val interactionSource = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(16.dp)

    val containerColor by animateColorAsState(
        targetValue = if (selected) tokens.slateElevated else tokens.carbonSurface,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = 400f),
        label = "batchTileBg_${batch.id}",
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) accent.copy(alpha = 0.52f) else tokens.hairlineSubtle,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = 400f),
        label = "batchTileBorder_${batch.id}",
    )
    val barHeight by animateDpAsState(
        targetValue = if (selected) 36.dp else 14.dp,
        animationSpec = spring(dampingRatio = 0.78f, stiffness = 420f),
        label = "batchTileBar_${batch.id}",
    )

    Surface(
        shape = shape,
        color = containerColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("batch_tile_${batch.id}")
            .minimumInteractiveComponentSize()
            .tactileScale(interactionSource, pressedScale = 0.98f)
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = accent.copy(alpha = 0.16f)),
                role = Role.Tab,
                onClick = onClick,
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .width(3.5.dp)
                    .height(barHeight)
                    .clip(RoundedCornerShape(100.dp))
                    .background(if (selected) accent else accent.copy(alpha = 0.45f)),
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = batch.goalTag.uppercase(),
                        style = MonoBadgeStyle,
                        color = accent,
                        maxLines = 1,
                    )
                    Text(
                        text = "${batch.overallProgressPercent}%",
                        style = MonoBadgeStyle,
                        color = if (selected) tokens.inkPrimary else tokens.inkMuted,
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
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(subject.accentMood.accent),
                        )
                    }
                    Text(
                        text = "${batch.subjects.size} subjects · ${batch.completedChapters}/${batch.totalChapters} ch",
                        style = MaterialTheme.typography.labelSmall,
                        color = tokens.inkSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                AnimatedLinearProgressBar(
                    progressPercent = batch.overallProgressPercent,
                    accentColor = accent,
                    height = 4.dp,
                )
            }
        }
    }
}
