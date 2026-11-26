package com.example.ui.revision

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material.icons.rounded.WarningAmber
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.FormulaCallout
import com.example.model.RevisionConceptCard
import com.example.model.RevisionQuizUiState
import com.example.model.RevisionSubjectSheet
import com.example.ui.components.AnodizedPill
import com.example.ui.components.ObsidianCard
import com.example.ui.components.StaggeredEntrance
import com.example.ui.components.tactileScale
import com.example.ui.theme.AccentMood
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MonoBadgeStyle
import com.example.ui.theme.MonoMetricMedium

@Composable
fun RevisionNotesView(
    state: RevisionQuizUiState,
    isWideLayout: Boolean,
    onSelectSubject: (String) -> Unit,
    onToggleCompoundMix: () -> Unit,
    onToggleLabels: () -> Unit,
    onExportRevisionPdf: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val activeSheet = state.activeRevisionSheet
    val animatedAccent by animateColorAsState(
        targetValue = activeSheet.accentMood.accent,
        animationSpec = spring(dampingRatio = 0.86f, stiffness = 320f),
        label = "revisionSubjectAccent",
    )

    if (isWideLayout) {
        Row(
            modifier = modifier
                .fillMaxSize()
                .testTag("revision_notes_view"),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            RevisionSubjectSelectorPane(
                sheets = state.revisionSheets,
                selectedSubjectId = state.selectedRevisionSubjectId,
                isCompoundMixEnabled = state.isCompoundMixEnabled,
                activeAccent = animatedAccent,
                onSelectSubject = onSelectSubject,
                onToggleCompoundMix = onToggleCompoundMix,
                onExportPdf = onExportRevisionPdf,
                modifier = Modifier
                    .weight(0.32f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
            )

            RevisionReaderCanvas(
                sheet = activeSheet,
                accentColor = animatedAccent,
                showLabels = state.showDiagramLabels,
                onToggleLabels = onToggleLabels,
                modifier = Modifier
                    .weight(0.68f)
                    .fillMaxHeight(),
            )
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .testTag("revision_notes_view"),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {
            item(key = "rev_mobile_selector") {
                RevisionSubjectSelectorPane(
                    sheets = state.revisionSheets,
                    selectedSubjectId = state.selectedRevisionSubjectId,
                    isCompoundMixEnabled = state.isCompoundMixEnabled,
                    activeAccent = animatedAccent,
                    onSelectSubject = onSelectSubject,
                    onToggleCompoundMix = onToggleCompoundMix,
                    onExportPdf = onExportRevisionPdf,
                )
            }
            item(key = "rev_mobile_content_${activeSheet.id}") {
                RevisionSheetSections(
                    sheet = activeSheet,
                    accentColor = animatedAccent,
                    showLabels = state.showDiagramLabels,
                    onToggleLabels = onToggleLabels,
                )
            }
        }
    }
}

@Composable
private fun RevisionSubjectSelectorPane(
    sheets: List<RevisionSubjectSheet>,
    selectedSubjectId: String,
    isCompoundMixEnabled: Boolean,
    activeAccent: Color,
    onSelectSubject: (String) -> Unit,
    onToggleCompoundMix: () -> Unit,
    onExportPdf: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors

    ObsidianCard(
        modifier = modifier.testTag("revision_subject_selector_pane"),
        containerColor = tokens.obsidianSurface,
        cornerRadius = 22.dp,
        contentPadding = PaddingValues(16.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "YESTERDAY'S DIGEST",
                    style = MonoBadgeStyle,
                    color = activeAccent,
                )
                Text(
                    text = stringResource(R.string.revision_source_header),
                    style = MaterialTheme.typography.titleMedium,
                    color = tokens.inkPrimary,
                )
            }

            // Subject selector cards
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                sheets.forEach { sheet ->
                    val selected = !isCompoundMixEnabled && sheet.id == selectedSubjectId
                    val subjectAccent = sheet.accentMood.accent
                    val interaction = remember { MutableInteractionSource() }
                    val bg by animateColorAsState(
                        targetValue = if (selected) tokens.slateElevated else tokens.carbonSurface,
                        animationSpec = spring(stiffness = 400f),
                        label = "revTileBg_${sheet.id}",
                    )
                    val border by animateColorAsState(
                        targetValue = if (selected) subjectAccent.copy(alpha = 0.55f) else tokens.hairlineSubtle,
                        animationSpec = spring(stiffness = 400f),
                        label = "revTileBorder_${sheet.id}",
                    )

                    Surface(
                        shape = RoundedCornerShape(15.dp),
                        color = bg,
                        border = BorderStroke(1.dp, border),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("revision_subject_tile_${sheet.id}")
                            .minimumInteractiveComponentSize()
                            .tactileScale(interaction, pressedScale = 0.98f)
                            .clip(RoundedCornerShape(15.dp))
                            .clickable(
                                interactionSource = interaction,
                                indication = ripple(color = subjectAccent.copy(alpha = 0.16f)),
                                role = Role.Tab,
                                onClick = { onSelectSubject(sheet.id) },
                            ),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 11.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(3.5.dp)
                                    .height(34.dp)
                                    .clip(RoundedCornerShape(100.dp))
                                    .background(if (selected) subjectAccent else subjectAccent.copy(alpha = 0.45f)),
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp),
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(
                                        text = sheet.subjectName,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = tokens.inkPrimary,
                                    )
                                    Text(
                                        text = "${sheet.estimatedMinutes}m",
                                        style = MonoBadgeStyle,
                                        color = subjectAccent,
                                    )
                                }
                                Text(
                                    text = sheet.chapterTitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = tokens.inkSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }
                }
            }

            // Compound Revision Mix toggle badge card
            val mixInteraction = remember { MutableInteractionSource() }
            val mixAccent = AccentMood.AMBER.accent
            val mixBorder by animateColorAsState(
                targetValue = if (isCompoundMixEnabled) mixAccent.copy(alpha = 0.6f) else tokens.hairlineRest,
                animationSpec = spring(stiffness = 400f),
                label = "compoundMixBorder",
            )
            Surface(
                shape = RoundedCornerShape(15.dp),
                color = if (isCompoundMixEnabled) AccentMood.AMBER.container else tokens.carbonSurface,
                border = BorderStroke(1.dp, mixBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("compound_revision_mix_toggle")
                    .minimumInteractiveComponentSize()
                    .tactileScale(mixInteraction, pressedScale = 0.975f)
                    .clip(RoundedCornerShape(15.dp))
                    .clickable(
                        interactionSource = mixInteraction,
                        indication = ripple(color = mixAccent.copy(alpha = 0.2f)),
                        role = Role.Switch,
                        onClick = onToggleCompoundMix,
                    ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 13.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = mixAccent,
                        modifier = Modifier.size(19.dp),
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = stringResource(R.string.revision_compound_mix),
                                style = MaterialTheme.typography.titleSmall,
                                color = tokens.inkPrimary,
                            )
                            Text(
                                text = if (isCompoundMixEnabled) "ON" else "OFF",
                                style = MonoBadgeStyle,
                                color = if (isCompoundMixEnabled) mixAccent else tokens.inkMuted,
                            )
                        }
                        Text(
                            text = stringResource(R.string.revision_compound_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.inkSecondary,
                        )
                    }
                }
            }

            // Mock Export Revision PDF button
            val exportInteraction = remember { MutableInteractionSource() }
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = tokens.carbonSurface,
                border = BorderStroke(1.dp, activeAccent.copy(alpha = 0.42f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("export_revision_pdf_button")
                    .minimumInteractiveComponentSize()
                    .tactileScale(exportInteraction, pressedScale = 0.97f)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable(
                        interactionSource = exportInteraction,
                        indication = ripple(color = activeAccent.copy(alpha = 0.18f)),
                        role = Role.Button,
                        onClick = onExportPdf,
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
                        imageVector = Icons.Rounded.PictureAsPdf,
                        contentDescription = null,
                        tint = activeAccent,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.revision_export_pdf),
                        style = MaterialTheme.typography.labelLarge,
                        color = tokens.inkPrimary,
                    )
                }
            }
        }
    }
}

@Composable
private fun RevisionReaderCanvas(
    sheet: RevisionSubjectSheet,
    accentColor: Color,
    showLabels: Boolean,
    onToggleLabels: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        targetState = sheet,
        contentKey = { it.id },
        transitionSpec = {
            (fadeIn(tween(220)) +
                slideInVertically(
                    initialOffsetY = { it / 22 },
                    animationSpec = spring(dampingRatio = 0.86f, stiffness = 380f),
                )) togetherWith fadeOut(tween(140))
        },
        modifier = modifier,
        label = "revisionSheetSwitch",
    ) { activeSheet ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 20.dp),
        ) {
            item(key = "sheet_content_${activeSheet.id}") {
                RevisionSheetSections(
                    sheet = activeSheet,
                    accentColor = accentColor,
                    showLabels = showLabels,
                    onToggleLabels = onToggleLabels,
                )
            }
        }
    }
}

@Composable
private fun RevisionSheetSections(
    sheet: RevisionSubjectSheet,
    accentColor: Color,
    showLabels: Boolean,
    onToggleLabels: () -> Unit,
) {
    val tokens = GhickinTokens.colors

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Sheet title header card
        ObsidianCard(
            containerColor = tokens.obsidianSurface,
            accentEdgeColor = accentColor.copy(alpha = 0.36f),
            surfaceWashColor = accentColor.copy(alpha = 0.06f),
            cornerRadius = 20.dp,
            contentPadding = PaddingValues(18.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AnodizedPill(
                        text = sheet.subjectName.uppercase(),
                        accentColor = accentColor,
                        containerColor = sheet.accentMood.container,
                        textColor = sheet.accentMood.onContainer,
                        borderColor = accentColor.copy(alpha = 0.34f),
                    )
                    Text(
                        text = sheet.sourceLogLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = tokens.inkMuted,
                    )
                }
                Text(
                    text = sheet.chapterTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    color = tokens.inkPrimary,
                )
            }
        }

        // Animated Conceptual Diagram Card
        ConceptualDiagramCard(
            diagramKind = sheet.diagramKind,
            title = sheet.diagramTitle,
            caption = sheet.diagramCaption,
            accentColor = accentColor,
            showLabels = showLabels,
            onToggleLabels = onToggleLabels,
        )

        // Key Formulas & Definitions Callout Boxes
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = stringResource(R.string.revision_formulas_header),
                style = MaterialTheme.typography.titleMedium,
                color = tokens.inkPrimary,
            )
            sheet.formulaCallouts.forEachIndexed { idx, formula ->
                StaggeredEntrance(delayMillis = idx * 45L) {
                    FormulaCalloutBox(formula = formula, accentColor = accentColor)
                }
            }
        }

        // Structured Concept Breakdown Cards
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = stringResource(R.string.revision_concepts_header),
                style = MaterialTheme.typography.titleMedium,
                color = tokens.inkPrimary,
            )
            sheet.conceptCards.forEachIndexed { idx, card ->
                StaggeredEntrance(delayMillis = idx * 50L) {
                    ConceptNoteCard(card = card, accentColor = accentColor)
                }
            }
        }
    }
}

@Composable
private fun FormulaCalloutBox(
    formula: FormulaCallout,
    accentColor: Color,
) {
    val tokens = GhickinTokens.colors
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = tokens.carbonSurface,
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.28f)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .width(3.5.dp)
                    .height(52.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(accentColor),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Text(
                    text = formula.title,
                    style = MaterialTheme.typography.labelMedium,
                    color = accentColor,
                )
                Text(
                    text = formula.expression,
                    style = MonoMetricMedium,
                    color = tokens.inkPrimary,
                )
                Text(
                    text = formula.note,
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkSecondary,
                )
            }
        }
    }
}

@Composable
private fun ConceptNoteCard(
    card: RevisionConceptCard,
    accentColor: Color,
) {
    val tokens = GhickinTokens.colors
    ObsidianCard(
        containerColor = tokens.obsidianSurface,
        cornerRadius = 18.dp,
        contentPadding = PaddingValues(16.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = card.tag,
                style = MonoBadgeStyle,
                color = accentColor,
            )
            Text(
                text = card.title,
                style = MaterialTheme.typography.titleMedium,
                color = tokens.inkPrimary,
            )
            Text(
                text = card.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = tokens.inkSecondary,
            )
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = AccentMood.AMBER.container.copy(alpha = 0.65f),
                border = BorderStroke(1.dp, AccentMood.AMBER.accent.copy(alpha = 0.28f)),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.WarningAmber,
                        contentDescription = null,
                        tint = AccentMood.AMBER.accent,
                        modifier = Modifier
                            .padding(top = 1.dp)
                            .size(16.dp),
                    )
                    Text(
                        text = card.trapAlert,
                        style = MaterialTheme.typography.bodySmall,
                        color = AccentMood.AMBER.onContainer,
                    )
                }
            }
        }
    }
}
