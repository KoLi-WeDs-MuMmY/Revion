package com.example.ui.vault

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.model.VaultMaterial
import com.example.ui.components.AnodizedPill
import com.example.ui.components.ObsidianCard
import com.example.ui.components.tactileScale
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MonoBadgeStyle
import com.example.ui.theme.MonoMetricMedium

/**
 * Live right-hand split preview pane for the Study Material Vault.
 * Clicking "Open PDF" collapses this preview panel cleanly and opens the focused mock reader.
 */
@Composable
fun VaultPreviewPane(
    material: VaultMaterial,
    onClose: () -> Unit,
    onOpenPdf: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val accent = material.subjectMood.accent

    ObsidianCard(
        modifier = modifier.testTag("vault_split_preview_pane"),
        containerColor = tokens.obsidianSurface,
        accentEdgeColor = accent.copy(alpha = 0.44f),
        surfaceWashColor = material.subjectMood.surfaceWash,
        cornerRadius = 22.dp,
        contentPadding = PaddingValues(18.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Top bar: Inspector pill + Close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                AnodizedPill(
                    text = stringResource(R.string.vault_preview_badge),
                    accentColor = accent,
                    containerColor = material.subjectMood.container,
                    textColor = material.subjectMood.onContainer,
                    borderColor = accent.copy(alpha = 0.34f),
                )
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.testTag("vault_preview_close_button"),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Collapse preview pane",
                        tint = tokens.inkSecondary,
                    )
                }
            }

            // Mock Architectural PDF Thumbnail Preview Card
            MockPageThumbnail(
                material = material,
                accentColor = accent,
            )

            // Title & Chapter metadata
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = material.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = tokens.inkPrimary,
                )
                Text(
                    text = "${material.subjectName} · ${material.chapterTitle}",
                    style = MaterialTheme.typography.bodySmall,
                    color = accent,
                )
            }

            // 3-Up Metadata Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                MetaMetricBox("Pages", "${material.pageCount}", Modifier.weight(1f))
                MetaMetricBox("Size", material.fileSizeLabel, Modifier.weight(1f))
                MetaMetricBox("Read", "${material.estimatedReadMinutes}m", Modifier.weight(1f))
            }

            Text(
                text = material.summary,
                style = MaterialTheme.typography.bodySmall,
                color = tokens.inkSecondary,
            )

            // Chapter Outline List
            Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Text(
                    text = stringResource(R.string.vault_outline_label).uppercase(),
                    style = MonoBadgeStyle,
                    color = tokens.inkMuted,
                )
                material.outlineSections.forEach { section ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(accent),
                        )
                        Text(
                            text = section,
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.inkPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }

            // Primary CTA: Open PDF (collapses this preview pane cleanly!)
            val openInteraction = remember { MutableInteractionSource() }
            val ctaShape = RoundedCornerShape(14.dp)
            Surface(
                shape = ctaShape,
                color = accent,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("vault_open_pdf_button")
                    .minimumInteractiveComponentSize()
                    .tactileScale(openInteraction, pressedScale = 0.965f)
                    .clip(ctaShape)
                    .clickable(
                        interactionSource = openInteraction,
                        indication = ripple(color = Color.Black.copy(alpha = 0.2f)),
                        role = Role.Button,
                        onClick = onOpenPdf,
                    ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 13.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = stringResource(R.string.vault_open_pdf_cta),
                        style = MaterialTheme.typography.labelLarge,
                        color = tokens.amoledBackground,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                        contentDescription = null,
                        tint = tokens.amoledBackground,
                        modifier = Modifier.size(17.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun MockPageThumbnail(
    material: VaultMaterial,
    accentColor: Color,
) {
    val tokens = GhickinTokens.colors

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = tokens.carbonSurface,
        border = BorderStroke(1.dp, tokens.hairlineRest),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "PAGE 01 PREVIEW",
                    style = MonoBadgeStyle,
                    color = accentColor,
                )
                Text(
                    text = material.type.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = tokens.inkMuted,
                )
            }
            // Crisp architectural mini-document wireframe lines (clean editorial aesthetic)
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.74f)
                    .height(7.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(tokens.inkPrimary.copy(alpha = 0.22f)),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(tokens.hairlineRest),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.86f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(tokens.hairlineRest),
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.12f)),
                )
                Box(
                    modifier = Modifier
                        .weight(1.3f)
                        .height(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(tokens.slateElevated),
                )
            }
        }
    }
}

@Composable
private fun MetaMetricBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = tokens.carbonSurface,
        border = BorderStroke(1.dp, tokens.hairlineSubtle),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = value,
                style = MonoMetricMedium,
                color = tokens.inkPrimary,
                maxLines = 1,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = tokens.inkMuted,
            )
        }
    }
}
