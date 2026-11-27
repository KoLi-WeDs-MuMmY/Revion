package com.example.ui.tutor

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Functions
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.RagChapterContext
import com.example.model.TutorMessage
import com.example.model.TutorPlannerUiState
import com.example.ui.components.AnodizedPill
import com.example.ui.components.KineticFilterChip
import com.example.ui.components.ObsidianCard
import com.example.ui.components.StaggeredEntrance
import com.example.ui.components.tactileScale
import com.example.ui.theme.FeedbackCorrect
import com.example.ui.theme.FormulaMonoDisplay
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.HumanistReadingBody
import com.example.ui.theme.InkOnAccentDark
import com.example.ui.theme.MicroTagMono

@Composable
fun ChapterRagTutorView(
    state: TutorPlannerUiState,
    isWideLayout: Boolean,
    compactCoPlan: Boolean,
    onSelectChapter: (String) -> Unit,
    onSendDoubt: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val activeChapter = state.selectedChapter
    val useTwoPane = isWideLayout && !compactCoPlan

    if (useTwoPane) {
        Row(
            modifier = modifier
                .fillMaxSize()
                .testTag("chapter_rag_tutor_view"),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            RagChapterSidebar(
                chapters = state.chapters,
                selectedChapterId = activeChapter.id,
                onSelectChapter = onSelectChapter,
                modifier = Modifier
                    .weight(0.34f)
                    .fillMaxHeight(),
            )
            TutorConversationStage(
                chapter = activeChapter,
                messages = state.currentMessages,
                isTyping = state.isTutorTyping,
                compactHeader = false,
                onSendDoubt = onSendDoubt,
                modifier = Modifier
                    .weight(0.66f)
                    .fillMaxHeight(),
            )
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .testTag("chapter_rag_tutor_view"),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // Compact chapter selector strip for Co-Plan Split & portrait layouts
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                state.chapters.forEach { ctx ->
                    KineticFilterChip(
                        label = ctx.subjectName,
                        selected = ctx.id == activeChapter.id,
                        onClick = { onSelectChapter(ctx.id) },
                        accentColor = ctx.accentMood.accent,
                        leadingDotColor = ctx.accentMood.accent,
                        trailingBadge = "${ctx.pageCount}p",
                        modifier = Modifier.testTag("rag_chapter_chip_${ctx.id}"),
                    )
                }
            }

            TutorConversationStage(
                chapter = activeChapter,
                messages = state.currentMessages,
                isTyping = state.isTutorTyping,
                compactHeader = true,
                onSendDoubt = onSendDoubt,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RagChapterSidebar(
    chapters: List<RagChapterContext>,
    selectedChapterId: String,
    onSelectChapter: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val active = chapters.firstOrNull { it.id == selectedChapterId } ?: chapters.first()

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Attached RAG Context & Persistent Memory Status Card
        StaggeredEntrance(delayMillis = 20L) {
            ObsidianCard(
                accentEdgeColor = active.accentMood.accent.copy(alpha = 0.42f),
                surfaceWashColor = active.accentMood.surfaceWash,
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    AnodizedPill(
                        text = stringResource(R.string.tutor_rag_context_pill),
                        accentColor = active.accentMood.accent,
                        containerColor = active.accentMood.container,
                        textColor = active.accentMood.onContainer,
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Rounded.PictureAsPdf,
                                contentDescription = null,
                                tint = active.accentMood.accent,
                                modifier = Modifier
                                    .padding(end = 6.dp)
                                    .size(13.dp),
                            )
                        },
                    )

                    Text(
                        text = active.pdfFileName,
                        style = FormulaMonoDisplay,
                        color = tokens.inkPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Text(
                        text = active.summaryNote,
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.inkSecondary,
                    )

                    // Section tags
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        active.sectionTags.forEach { tag ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = tokens.carbonSurface,
                                border = BorderStroke(1.dp, tokens.hairlineRest),
                            ) {
                                Text(
                                    text = tag,
                                    style = MicroTagMono,
                                    color = tokens.inkSecondary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                )
                            }
                        }
                    }

                    // Persistent Memory indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Memory,
                            contentDescription = null,
                            tint = FeedbackCorrect,
                            modifier = Modifier.size(15.dp),
                        )
                        Text(
                            text = stringResource(
                                R.string.tutor_memory_badge_format,
                                active.memoryTurnsCount,
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = FeedbackCorrect,
                        )
                    }
                }
            }
        }

        // Chapter PDF Selector List
        Text(
            text = "CHAPTER PDF LIBRARY",
            style = MicroTagMono,
            color = tokens.inkMuted,
            modifier = Modifier.padding(horizontal = 4.dp),
        )

        chapters.forEachIndexed { index, ctx ->
            val selected = ctx.id == selectedChapterId
            StaggeredEntrance(delayMillis = 55L + index * 35L) {
                ObsidianCard(
                    accentEdgeColor = if (selected) ctx.accentMood.accent.copy(alpha = 0.62f) else tokens.hairlineRest,
                    surfaceWashColor = if (selected) ctx.accentMood.surfaceWash else Color.Transparent,
                    contentPadding = PaddingValues(14.dp),
                    onClick = { onSelectChapter(ctx.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("rag_chapter_card_${ctx.id}"),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(7.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(ctx.accentMood.accent),
                                )
                                Text(
                                    text = ctx.subjectName.uppercase(),
                                    style = MicroTagMono,
                                    color = ctx.accentMood.accent,
                                )
                            }
                            Text(
                                text = "${ctx.pageCount} PAGES",
                                style = MicroTagMono,
                                color = tokens.inkMuted,
                            )
                        }
                        Text(
                            text = ctx.chapterTitle,
                            style = MaterialTheme.typography.titleSmall,
                            color = if (selected) tokens.inkPrimary else tokens.inkSecondary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TutorConversationStage(
    chapter: RagChapterContext,
    messages: List<TutorMessage>,
    isTyping: Boolean,
    compactHeader: Boolean,
    onSendDoubt: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    var draftText by rememberSaveable(chapter.id) { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size, isTyping, chapter.id) {
        val targetIdx = (messages.size + if (isTyping) 1 else 0 - 1).coerceAtLeast(0)
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(targetIdx)
        }
    }

    ObsidianCard(
        accentEdgeColor = chapter.accentMood.accent.copy(alpha = 0.30f),
        contentPadding = PaddingValues(0.dp),
        modifier = modifier,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Stream Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(tokens.carbonSurface.copy(alpha = 0.72f))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(9.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = chapter.accentMood.accent,
                        modifier = Modifier.size(18.dp),
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                        Text(
                            text = chapter.chapterTitle,
                            style = MaterialTheme.typography.titleSmall,
                            color = tokens.inkPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = if (compactHeader) {
                                "${chapter.pdfFileName} · ${chapter.memoryTurnsCount} memory turns"
                            } else {
                                "Whole-chapter RAG citations active · ${chapter.pageCount} indexed pages"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = tokens.inkMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                AnodizedPill(
                    text = "RAG · ${chapter.pageCount}P",
                    accentColor = chapter.accentMood.accent,
                    containerColor = chapter.accentMood.container,
                    textColor = chapter.accentMood.onContainer,
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(tokens.hairlineRest),
            )

            // Message Stream
            AnimatedContent(
                targetState = chapter.id,
                transitionSpec = { fadeIn(tween(180)) togetherWith fadeOut(tween(120)) },
                label = "tutorChapterStreamSwitch",
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) { _ ->
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("tutor_message_stream"),
                ) {
                    items(messages, key = { it.id }) { msg ->
                        TutorMessageBubble(
                            message = msg,
                            accentColor = chapter.accentMood.accent,
                            accentContainer = chapter.accentMood.container,
                        )
                    }

                    if (isTyping) {
                        item(key = "tutor_typing_indicator") {
                            AnimatedTypingBubble(accentColor = chapter.accentMood.accent)
                        }
                    }
                }
            }

            // Quick Prompt Chips Row
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(tokens.obsidianSurface)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    chapter.quickPrompts.forEachIndexed { idx, prompt ->
                        val interaction = remember { MutableInteractionSource() }
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = tokens.carbonSurface,
                            border = BorderStroke(1.dp, chapter.accentMood.accent.copy(alpha = 0.28f)),
                            modifier = Modifier
                                .testTag("tutor_quick_prompt_$idx")
                                .tactileScale(interaction, pressedScale = 0.96f)
                                .clip(RoundedCornerShape(100.dp))
                                .clickable(
                                    interactionSource = interaction,
                                    indication = ripple(color = chapter.accentMood.accent.copy(alpha = 0.18f)),
                                    role = Role.Button,
                                    enabled = !isTyping,
                                    onClick = { onSendDoubt(prompt) },
                                ),
                        ) {
                            Text(
                                text = prompt,
                                style = MaterialTheme.typography.labelMedium,
                                color = tokens.inkSecondary,
                                maxLines = 1,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            )
                        }
                    }
                }

                // Animated Doubt Composer Input Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = draftText,
                        onValueChange = { draftText = it },
                        placeholder = {
                            Text(
                                text = stringResource(R.string.tutor_input_placeholder),
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.inkMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = tokens.inkPrimary),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = chapter.accentMood.accent.copy(alpha = 0.65f),
                            unfocusedBorderColor = tokens.hairlineRest,
                            focusedContainerColor = tokens.carbonSurface,
                            unfocusedContainerColor = tokens.carbonSurface,
                            cursorColor = chapter.accentMood.accent,
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (draftText.isNotBlank() && !isTyping) {
                                    onSendDoubt(draftText)
                                    draftText = ""
                                }
                            },
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tutor_input_field"),
                    )

                    val canSend = draftText.isNotBlank() && !isTyping
                    val sendBg by animateColorAsState(
                        targetValue = if (canSend) chapter.accentMood.accent else tokens.carbonSurface,
                        animationSpec = spring(stiffness = 420f),
                        label = "tutorSendBg",
                    )
                    val sendTint by animateColorAsState(
                        targetValue = if (canSend) InkOnAccentDark else tokens.inkMuted,
                        animationSpec = spring(stiffness = 420f),
                        label = "tutorSendTint",
                    )
                    val sendInteraction = remember { MutableInteractionSource() }

                    Surface(
                        shape = RoundedCornerShape(15.dp),
                        color = sendBg,
                        border = BorderStroke(
                            1.dp,
                            if (canSend) chapter.accentMood.accent else tokens.hairlineRest,
                        ),
                        modifier = Modifier
                            .testTag("tutor_send_button")
                            .minimumInteractiveComponentSize()
                            .size(48.dp)
                            .tactileScale(sendInteraction, pressedScale = 0.93f)
                            .clip(RoundedCornerShape(15.dp))
                            .clickable(
                                interactionSource = sendInteraction,
                                indication = ripple(color = chapter.accentMood.accent.copy(alpha = 0.25f)),
                                role = Role.Button,
                                enabled = canSend,
                                onClick = {
                                    onSendDoubt(draftText)
                                    draftText = ""
                                },
                            ),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.Send,
                                contentDescription = stringResource(R.string.tutor_send_desc),
                                tint = sendTint,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TutorMessageBubble(
    message: TutorMessage,
    accentColor: Color,
    accentContainer: Color,
) {
    val tokens = GhickinTokens.colors

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start,
    ) {
        val bubbleBg = when {
            message.isUser -> accentContainer.copy(alpha = 0.85f)
            message.isPlannerAuditNote -> tokens.slateElevated
            else -> tokens.carbonSurface
        }
        val bubbleBorder = when {
            message.isUser -> accentColor.copy(alpha = 0.42f)
            message.isPlannerAuditNote -> FeedbackCorrect.copy(alpha = 0.36f)
            else -> tokens.hairlineRest
        }

        Surface(
            shape = RoundedCornerShape(18.dp),
            color = bubbleBg,
            border = BorderStroke(1.dp, bubbleBorder),
            modifier = Modifier.widthIn(max = 560.dp),
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(9.dp),
            ) {
                // Header tag for Planner Guardrail notes or timestamps
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (message.isPlannerAuditNote) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.VerifiedUser,
                                contentDescription = null,
                                tint = FeedbackCorrect,
                                modifier = Modifier.size(13.dp),
                            )
                            Text(
                                text = stringResource(R.string.tutor_planner_audit_pill),
                                style = MicroTagMono,
                                color = FeedbackCorrect,
                            )
                        }
                    } else {
                        Text(
                            text = if (message.isUser) "YOU" else "GHICKIN RAG TUTOR",
                            style = MicroTagMono,
                            color = if (message.isUser) accentColor else tokens.inkMuted,
                        )
                    }

                    Text(
                        text = message.timestampLabel,
                        style = MicroTagMono,
                        color = tokens.inkMuted,
                    )
                }

                Text(
                    text = message.text,
                    style = HumanistReadingBody,
                    color = tokens.inkPrimary,
                )

                // Formula / Derivation Callout Block
                if (!message.formulaBlock.isNullOrBlank()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = tokens.obsidianSurface,
                        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.28f)),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Functions,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(13.dp),
                                )
                                Text(
                                    text = stringResource(R.string.tutor_formula_label),
                                    style = MicroTagMono,
                                    color = accentColor,
                                )
                            }
                            Text(
                                text = message.formulaBlock,
                                style = FormulaMonoDisplay,
                                color = tokens.inkPrimary,
                            )
                        }
                    }
                }

                // Cited PDF Page & Section Badges
                if (message.citations.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        message.citations.forEach { citation ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = tokens.obsidianSurface,
                                border = BorderStroke(1.dp, accentColor.copy(alpha = 0.30f)),
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Description,
                                        contentDescription = null,
                                        tint = accentColor,
                                        modifier = Modifier.size(12.dp),
                                    )
                                    Text(
                                        text = "${citation.pageLabel} · ${citation.sectionTag}",
                                        style = MicroTagMono,
                                        color = accentColor,
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
private fun AnimatedTypingBubble(
    accentColor: Color,
) {
    val tokens = GhickinTokens.colors
    val infinite = rememberInfiniteTransition(label = "tutorTypingDots")
    val dot1 by infinite.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(460), RepeatMode.Reverse),
        label = "dot1",
    )
    val dot2 by infinite.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(460, delayMillis = 140), RepeatMode.Reverse),
        label = "dot2",
    )
    val dot3 by infinite.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(460, delayMillis = 280), RepeatMode.Reverse),
        label = "dot3",
    )

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = tokens.carbonSurface,
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.32f)),
        modifier = Modifier.testTag("tutor_typing_bubble"),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                listOf(dot1, dot2, dot3).forEach { alphaVal ->
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .graphicsLayer { alpha = alphaVal }
                            .clip(CircleShape)
                            .background(accentColor),
                    )
                }
            }
            Text(
                text = stringResource(R.string.tutor_typing_status),
                style = MaterialTheme.typography.labelSmall,
                color = tokens.inkSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
