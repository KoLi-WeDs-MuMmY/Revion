package com.example.ui.vault

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Functions
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Quiz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.model.MaterialType
import com.example.model.VaultMaterial
import com.example.ui.components.ObsidianCard
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MonoBadgeStyle

@Composable
fun VaultMaterialCard(
    material: VaultMaterial,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val accent = material.subjectMood.accent

    val edgeColor by animateColorAsState(
        targetValue = if (selected) accent.copy(alpha = 0.62f) else tokens.hairlineRest,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = 400f),
        label = "vaultCardEdge_${material.id}",
    )
    val washColor by animateColorAsState(
        targetValue = if (selected) material.subjectMood.surfaceWash else Color.Transparent,
        animationSpec = spring(stiffness = 380f),
        label = "vaultCardWash_${material.id}",
    )

    val typeIcon = when (material.type) {
        MaterialType.PDF_NOTES -> Icons.Rounded.Description
        MaterialType.FORMULA_SHEET -> Icons.Rounded.Functions
        MaterialType.LECTURE_DECK -> Icons.Rounded.MenuBook
        MaterialType.PRACTICE_SET -> Icons.Rounded.Quiz
    }

    ObsidianCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("vault_material_card_${material.id}"),
        containerColor = if (selected) tokens.carbonSurface else tokens.obsidianSurface,
        accentEdgeColor = edgeColor,
        surfaceWashColor = washColor,
        cornerRadius = 18.dp,
        contentPadding = PaddingValues(16.dp),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // Top badges row: Subject + Material Type
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(material.subjectMood.container),
                    ) {
                        Icon(
                            imageVector = typeIcon,
                            contentDescription = material.type.label,
                            tint = accent,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                    Column {
                        Text(
                            text = material.subjectName.uppercase(),
                            style = MonoBadgeStyle,
                            color = accent,
                        )
                        Text(
                            text = material.batchName,
                            style = MaterialTheme.typography.labelSmall,
                            color = tokens.inkMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = tokens.slateElevated,
                    border = BorderStroke(1.dp, tokens.hairlineRest),
                ) {
                    Text(
                        text = material.type.badgeCode,
                        style = MonoBadgeStyle,
                        color = tokens.inkSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
            }

            // Document title & chapter tag
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = material.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = tokens.inkPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = material.chapterTitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // Footer telemetry strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "${material.pageCount} pp",
                        style = MaterialTheme.typography.labelSmall,
                        color = tokens.inkPrimary,
                    )
                    Text(
                        text = "· ${material.fileSizeLabel}",
                        style = MaterialTheme.typography.labelSmall,
                        color = tokens.inkMuted,
                    )
                }
                Text(
                    text = material.lastOpenedLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = tokens.inkMuted,
                    maxLines = 1,
                )
            }
        }
    }
}
