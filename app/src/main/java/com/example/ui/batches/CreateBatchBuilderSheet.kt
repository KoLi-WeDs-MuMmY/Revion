package com.example.ui.batches

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.Phase12MockData
import com.example.model.PresetSubjectTemplate
import com.example.ui.components.KineticFilterChip
import com.example.ui.components.tactileScale
import com.example.ui.theme.AccentMood
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MonoBadgeStyle

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateBatchBuilderSheet(
    visible: Boolean,
    isWideLayout: Boolean,
    defaultAccent: AccentMood,
    onDismiss: () -> Unit,
    onCreateBatch: (
        name: String,
        goalTag: String,
        accentMood: AccentMood,
        selectedSubjects: List<PresetSubjectTemplate>,
        customSubjectName: String,
    ) -> Unit,
) {
    val tokens = GhickinTokens.colors
    val slideSpring = spring<IntOffset>(dampingRatio = 0.86f, stiffness = 380f)

    // Dimmed scrim
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.68f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss,
                ),
        )
    }

    // Slide-in side sheet on landscape, bottom sheet on portrait
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = if (isWideLayout) Alignment.CenterEnd else Alignment.BottomCenter,
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = if (isWideLayout) {
                slideInHorizontally(animationSpec = slideSpring) { it } + fadeIn()
            } else {
                slideInVertically(animationSpec = slideSpring) { it } + fadeIn()
            },
            exit = if (isWideLayout) {
                slideOutHorizontally(animationSpec = slideSpring) { it } + fadeOut()
            } else {
                slideOutVertically(animationSpec = slideSpring) { it } + fadeOut()
            },
        ) {
            var batchName by remember(visible) { mutableStateOf("") }
            var selectedGoal by remember(visible) { mutableStateOf(Phase12MockData.goalTagPresets.first()) }
            var selectedMood by remember(visible) { mutableStateOf(defaultAccent) }
            var selectedIds by remember(visible) {
                mutableStateOf(setOf("tmpl_phys", "tmpl_bio"))
            }
            var customSubjectName by remember(visible) { mutableStateOf("") }

            val accentAnimated by animateColorAsState(
                targetValue = selectedMood.accent,
                animationSpec = spring(stiffness = 340f),
                label = "builderAccent",
            )

            val sheetShape = if (isWideLayout) {
                RoundedCornerShape(topStart = 26.dp, bottomStart = 26.dp)
            } else {
                RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp)
            }

            Surface(
                shape = sheetShape,
                color = tokens.obsidianSurface,
                border = BorderStroke(1.dp, accentAnimated.copy(alpha = 0.36f)),
                modifier = if (isWideLayout) {
                    Modifier
                        .fillMaxHeight()
                        .widthIn(min = 380.dp, max = 460.dp)
                        .testTag("create_batch_builder_sheet")
                } else {
                    Modifier
                        .fillMaxWidth()
                        .testTag("create_batch_builder_sheet")
                },
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text(
                                text = "CUSTOM ARCHITECT",
                                style = MonoBadgeStyle,
                                color = accentAnimated,
                            )
                            Text(
                                text = stringResource(R.string.batches_builder_title),
                                style = MaterialTheme.typography.headlineSmall,
                                color = tokens.inkPrimary,
                            )
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("builder_close_button"),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Close batch builder",
                                tint = tokens.inkSecondary,
                            )
                        }
                    }

                    Text(
                        text = stringResource(R.string.batches_builder_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.inkSecondary,
                    )

                    // Batch Name input
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = stringResource(R.string.batches_builder_name_label),
                            style = MaterialTheme.typography.titleSmall,
                            color = tokens.inkPrimary,
                        )
                        OutlinedTextField(
                            value = batchName,
                            onValueChange = { batchName = it },
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.batches_builder_name_hint),
                                    color = tokens.inkMuted,
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = accentAnimated,
                                unfocusedBorderColor = tokens.hairlineRest,
                                focusedContainerColor = tokens.carbonSurface,
                                unfocusedContainerColor = tokens.carbonSurface,
                                focusedTextColor = tokens.inkPrimary,
                                unfocusedTextColor = tokens.inkPrimary,
                                cursorColor = accentAnimated,
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("builder_batch_name_input"),
                        )
                    }

                    // Target Goal Tag spring chips
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = stringResource(R.string.batches_builder_goal_label),
                            style = MaterialTheme.typography.titleSmall,
                            color = tokens.inkPrimary,
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Phase12MockData.goalTagPresets.forEach { goal ->
                                KineticFilterChip(
                                    label = goal,
                                    selected = selectedGoal == goal,
                                    accentColor = accentAnimated,
                                    onClick = { selectedGoal = goal },
                                    modifier = Modifier.testTag("builder_goal_${goal.lowercase().replace(' ', '_')}"),
                                )
                            }
                        }
                    }

                    // Pick & Mix Subjects
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = stringResource(R.string.batches_builder_subjects_label),
                                style = MaterialTheme.typography.titleSmall,
                                color = tokens.inkPrimary,
                            )
                            Text(
                                text = "${selectedIds.size} SELECTED",
                                style = MonoBadgeStyle,
                                color = accentAnimated,
                            )
                        }
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Phase12MockData.presetSubjectTemplates.forEach { tmpl ->
                                val isPicked = tmpl.id in selectedIds
                                KineticFilterChip(
                                    label = tmpl.name,
                                    selected = isPicked,
                                    accentColor = tmpl.accentMood.accent,
                                    leadingDotColor = tmpl.accentMood.accent,
                                    leadingIcon = if (isPicked) Icons.Rounded.Check else null,
                                    onClick = {
                                        selectedIds = if (isPicked && selectedIds.size > 1) {
                                            selectedIds - tmpl.id
                                        } else {
                                            selectedIds + tmpl.id
                                        }
                                    },
                                    modifier = Modifier.testTag("builder_subject_chip_${tmpl.id}"),
                                )
                            }
                        }

                        OutlinedTextField(
                            value = customSubjectName,
                            onValueChange = { customSubjectName = it },
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.batches_builder_custom_subject_hint),
                                    color = tokens.inkMuted,
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = accentAnimated,
                                unfocusedBorderColor = tokens.hairlineRest,
                                focusedContainerColor = tokens.carbonSurface,
                                unfocusedContainerColor = tokens.carbonSurface,
                                focusedTextColor = tokens.inkPrimary,
                                unfocusedTextColor = tokens.inkPrimary,
                                cursorColor = accentAnimated,
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("builder_custom_subject_input"),
                        )
                    }

                    // Batch Accent Mood picker
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = stringResource(R.string.batches_builder_accent_label),
                            style = MaterialTheme.typography.titleSmall,
                            color = tokens.inkPrimary,
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            AccentMood.entries.forEach { mood ->
                                val isSelected = mood == selectedMood
                                val dotSize by animateDpAsState(
                                    targetValue = if (isSelected) 24.dp else 18.dp,
                                    animationSpec = spring(dampingRatio = 0.72f, stiffness = 460f),
                                    label = "builderMood_${mood.id}",
                                )
                                val interaction = remember { MutableInteractionSource() }
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) mood.container else tokens.carbonSurface,
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) mood.accent else tokens.hairlineRest,
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("builder_accent_${mood.id}")
                                        .minimumInteractiveComponentSize()
                                        .tactileScale(interaction, pressedScale = 0.94f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable(
                                            interactionSource = interaction,
                                            indication = ripple(color = mood.accent.copy(alpha = 0.2f)),
                                            role = Role.RadioButton,
                                            onClick = { selectedMood = mood },
                                        ),
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(5.dp),
                                        modifier = Modifier.padding(vertical = 9.dp),
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(dotSize)
                                                .clip(CircleShape)
                                                .background(mood.accent)
                                                .border(
                                                    width = if (isSelected) 2.dp else 0.dp,
                                                    color = tokens.inkPrimary,
                                                    shape = CircleShape,
                                                ),
                                        )
                                        Text(
                                            text = mood.label,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isSelected) tokens.inkPrimary else tokens.inkMuted,
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Submit button
                    val canSubmit = batchName.trim().isNotEmpty()
                    val submitInteraction = remember { MutableInteractionSource() }
                    val submitShape = RoundedCornerShape(14.dp)
                    Surface(
                        shape = submitShape,
                        color = if (canSubmit) accentAnimated else tokens.slateElevated,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("builder_submit_button")
                            .minimumInteractiveComponentSize()
                            .tactileScale(submitInteraction, pressedScale = 0.97f)
                            .clip(submitShape)
                            .clickable(
                                enabled = canSubmit,
                                interactionSource = submitInteraction,
                                indication = ripple(color = Color.Black.copy(alpha = 0.2f)),
                                role = Role.Button,
                                onClick = {
                                    val chosen = Phase12MockData.presetSubjectTemplates
                                        .filter { it.id in selectedIds }
                                    onCreateBatch(
                                        batchName,
                                        selectedGoal,
                                        selectedMood,
                                        chosen,
                                        customSubjectName,
                                    )
                                },
                            ),
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 14.dp),
                        ) {
                            Text(
                                text = stringResource(R.string.batches_builder_submit),
                                style = MaterialTheme.typography.labelLarge,
                                color = if (canSubmit) tokens.amoledBackground else tokens.inkMuted,
                            )
                        }
                    }
                }
            }
        }
    }
}
