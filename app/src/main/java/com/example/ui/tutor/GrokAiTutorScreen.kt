package com.example.ui.tutor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.mock.Phase162MockData
import com.example.model.ChapterDiagramRef
import com.example.model.DiagramAttachment
import com.example.model.GrokTutorUiState
import com.example.model.StudyBatch
import com.example.model.TutorChatMessage
import com.example.model.VaultChapterNode
import com.example.model.VaultSubjectNode
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MicroTagMono
import com.example.ui.theme.MonoBadgeStyle
import com.example.ui.theme.rememberScreenLayoutMetrics
import com.example.ui.webviewer.DiagramZoomModalDialog
import com.example.ui.webviewer.InlineDiagramArtifactCard
import com.example.ui.webviewer.InteractiveDiagramWebViewPane

@Composable
fun GrokAiTutorScreen(
    state: GrokTutorUiState,
    batches: List<StudyBatch>,
    subjectsByBatch: Map<String, List<VaultSubjectNode>>,
    isWideLayout: Boolean,
    onOpenSettings: () -> Unit,
    onToggleSidebar: () -> Unit,
    onCloseSidebar: () -> Unit,
    onStartNewChat: () -> Unit,
    onSelectSession: (String) -> Unit,
    onOpenChapterPicker: () -> Unit,
    onCloseChapterPicker: () -> Unit,
    onConfirmLinkedChapters: (batchId: String, chapterIds: List<String>) -> Unit,
    onOpenRagIndexDialog: () -> Unit,
    onCloseRagIndexDialog: () -> Unit,
    onUpdateComposerText: (String) -> Unit,
    onSendPrompt: (String) -> Unit,
    onOpenDiagramModal: (DiagramAttachment) -> Unit,
    onOpenDiagramSplitScreen: (DiagramAttachment) -> Unit,
    onPreviewCatalogDiagram: (ChapterDiagramRef) -> Unit,
    onToggleDiagramSplitScreen: () -> Unit,
    onCloseDiagramViewer: () -> Unit,
    onZoomDiagram: (Int) -> Unit,
    onResetDiagramZoom: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val metrics = rememberScreenLayoutMetrics()

    val allChaptersById = remember(subjectsByBatch) {
        subjectsByBatch.values.flatten().flatMap { it.chapters }.associateBy { it.id }
    }
    val linkedChapters = remember(state.currentLinkedChapterIds, allChaptersById) {
        state.currentLinkedChapterIds.mapNotNull { allChaptersById[it] }
    }
    val scopedDiagrams = remember(state.currentLinkedChapterIds, state.diagramCatalog) {
        Phase162MockData.diagramsForLinkedChapters(state.currentLinkedChapterIds, state.diagramCatalog)
    }

    val activeViewerAttachment = state.viewerState.attachment
    val isSplitActive = state.viewerState.isSplitScreen && activeViewerAttachment != null

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(tokens.amoledBackground)
            .testTag("grok_ai_tutor_screen"),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = if (isWideLayout) 14.dp else 10.dp,
                    vertical = if (isWideLayout) 12.dp else 8.dp,
                ),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // 16:9 Landscape inline collapsible sidebar (collapsed at start!)
            if (isWideLayout) {
                AnimatedVisibility(
                    visible = state.isSidebarExpanded,
                    enter = expandHorizontally(spring(0.86f, 400f)) + fadeIn(),
                    exit = shrinkHorizontally(spring(0.86f, 400f)) + fadeOut(),
                ) {
                    TutorHistorySidebar(
                        sessions = state.sessions,
                        activeSessionId = state.activeSessionId,
                        batches = batches,
                        subjectsByBatch = subjectsByBatch,
                        onNewChat = onStartNewChat,
                        onSelectSession = onSelectSession,
                        onCloseSidebar = onCloseSidebar,
                        showCloseIcon = false,
                        modifier = Modifier.width(264.dp),
                    )
                }
            }

            // Main Chat + Optional Split-Screen Image-Code Web Viewer
            if (isSplitActive && activeViewerAttachment != null) {
                if (isWideLayout) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        TutorMainChatColumn(
                            state = state,
                            linkedChapters = linkedChapters,
                            scopedDiagramCount = scopedDiagrams.size,
                            isWideLayout = true,
                            onOpenSettings = onOpenSettings,
                            onToggleSidebar = onToggleSidebar,
                            onStartNewChat = onStartNewChat,
                            onOpenChapterPicker = onOpenChapterPicker,
                            onOpenRagIndexDialog = onOpenRagIndexDialog,
                            onUpdateComposerText = onUpdateComposerText,
                            onSendPrompt = onSendPrompt,
                            onOpenDiagramModal = onOpenDiagramModal,
                            onOpenDiagramSplitScreen = onOpenDiagramSplitScreen,
                            modifier = Modifier
                                .weight(0.54f)
                                .fillMaxHeight(),
                        )
                        InteractiveDiagramWebViewPane(
                            attachment = activeViewerAttachment,
                            zoomPercent = state.viewerState.zoomPercent,
                            isSplitScreen = true,
                            onZoomIn = { onZoomDiagram(15) },
                            onZoomOut = { onZoomDiagram(-15) },
                            onResetZoom = onResetDiagramZoom,
                            onToggleSplitScreen = onToggleDiagramSplitScreen,
                            onClose = onCloseDiagramViewer,
                            modifier = Modifier
                                .weight(0.46f)
                                .fillMaxHeight(),
                        )
                    }
                } else {
                    // 9:16 Portrait Split Screen: Interactive WebView docked cleanly on top, chat below
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        InteractiveDiagramWebViewPane(
                            attachment = activeViewerAttachment,
                            zoomPercent = state.viewerState.zoomPercent,
                            isSplitScreen = true,
                            onZoomIn = { onZoomDiagram(15) },
                            onZoomOut = { onZoomDiagram(-15) },
                            onResetZoom = onResetDiagramZoom,
                            onToggleSplitScreen = onToggleDiagramSplitScreen,
                            onClose = onCloseDiagramViewer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.46f),
                        )
                        TutorMainChatColumn(
                            state = state,
                            linkedChapters = linkedChapters,
                            scopedDiagramCount = scopedDiagrams.size,
                            isWideLayout = false,
                            onOpenSettings = onOpenSettings,
                            onToggleSidebar = onToggleSidebar,
                            onStartNewChat = onStartNewChat,
                            onOpenChapterPicker = onOpenChapterPicker,
                            onOpenRagIndexDialog = onOpenRagIndexDialog,
                            onUpdateComposerText = onUpdateComposerText,
                            onSendPrompt = onSendPrompt,
                            onOpenDiagramModal = onOpenDiagramModal,
                            onOpenDiagramSplitScreen = onOpenDiagramSplitScreen,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.54f),
                        )
                    }
                }
            } else {
                TutorMainChatColumn(
                    state = state,
                    linkedChapters = linkedChapters,
                    scopedDiagramCount = scopedDiagrams.size,
                    isWideLayout = isWideLayout,
                    onOpenSettings = onOpenSettings,
                    onToggleSidebar = onToggleSidebar,
                    onStartNewChat = onStartNewChat,
                    onOpenChapterPicker = onOpenChapterPicker,
                    onOpenRagIndexDialog = onOpenRagIndexDialog,
                    onUpdateComposerText = onUpdateComposerText,
                    onSendPrompt = onSendPrompt,
                    onOpenDiagramModal = onOpenDiagramModal,
                    onOpenDiagramSplitScreen = onOpenDiagramSplitScreen,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                )
            }
        }

        // 9:16 Portrait Slide-Over Sidebar Drawer
        if (!isWideLayout) {
            AnimatedVisibility(
                visible = state.isSidebarExpanded,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(tokens.amoledBackground.copy(alpha = 0.72f))
                        .clickable(onClick = onCloseSidebar),
                )
            }
            AnimatedVisibility(
                visible = state.isSidebarExpanded,
                enter = slideInHorizontally(spring(0.86f, 400f)) { -it },
                exit = slideOutHorizontally(spring(0.86f, 400f)) { -it },
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 8.dp, horizontal = 8.dp),
            ) {
                TutorHistorySidebar(
                    sessions = state.sessions,
                    activeSessionId = state.activeSessionId,
                    batches = batches,
                    subjectsByBatch = subjectsByBatch,
                    onNewChat = onStartNewChat,
                    onSelectSession = { sessionId ->
                        onSelectSession(sessionId)
                        onCloseSidebar()
                    },
                    onCloseSidebar = onCloseSidebar,
                    showCloseIcon = true,
                    modifier = Modifier.width(288.dp),
                )
            }
        }
    }

    // Chapter Choosing Window (supports 1 or multiple linked chapters!)
    if (state.isChapterPickerOpen) {
        ChapterPickerDialog(
            batches = batches,
            subjectsByBatch = subjectsByBatch,
            initialBatchId = state.activeSession?.batchId ?: state.draftBatchId,
            initialSelectedChapterIds = state.currentLinkedChapterIds,
            onConfirm = onConfirmLinkedChapters,
            onDismiss = onCloseChapterPicker,
        )
    }

    // Chapter-Scoped Diagram RAG Index Inspector
    if (state.isRagIndexDialogOpen) {
        ChapterRagIndexDialog(
            scopedDiagrams = scopedDiagrams,
            onPreviewDiagram = onPreviewCatalogDiagram,
            onDismiss = onCloseRagIndexDialog,
        )
    }

    // Full-Screen Zoomable Image-Code Web Viewer Modal
    if (state.viewerState.isFullModalOpen && activeViewerAttachment != null) {
        DiagramZoomModalDialog(
            attachment = activeViewerAttachment,
            zoomPercent = state.viewerState.zoomPercent,
            onZoomIn = { onZoomDiagram(15) },
            onZoomOut = { onZoomDiagram(-15) },
            onResetZoom = onResetDiagramZoom,
            onSwitchToSplitScreen = onToggleDiagramSplitScreen,
            onDismiss = onCloseDiagramViewer,
        )
    }
}

@Composable
private fun TutorMainChatColumn(
    state: GrokTutorUiState,
    linkedChapters: List<VaultChapterNode>,
    scopedDiagramCount: Int,
    isWideLayout: Boolean,
    onOpenSettings: () -> Unit,
    onToggleSidebar: () -> Unit,
    onStartNewChat: () -> Unit,
    onOpenChapterPicker: () -> Unit,
    onOpenRagIndexDialog: () -> Unit,
    onUpdateComposerText: (String) -> Unit,
    onSendPrompt: (String) -> Unit,
    onOpenDiagramModal: (DiagramAttachment) -> Unit,
    onOpenDiagramSplitScreen: (DiagramAttachment) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    var menuExpanded by remember { mutableStateOf(false) }
    val activeSession = state.activeSession
    val messages = activeSession?.messages.orEmpty()
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size, state.isGeneratingReply) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Minimal Top Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = tokens.obsidianSurface,
                border = BorderStroke(1.dp, tokens.hairlineRest),
                modifier = Modifier
                    .testTag("ai_tutor_sidebar_toggle")
                    .minimumInteractiveComponentSize()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(role = Role.Button, onClick = onToggleSidebar),
            ) {
                Box(
                    modifier = Modifier.padding(10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Menu,
                        contentDescription = stringResource(R.string.grok_tutor_sidebar_desc),
                        tint = tokens.inkPrimary,
                        modifier = Modifier.size(19.dp),
                    )
                }
            }

            // Linked chapter pill / selector in header
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = tokens.obsidianSurface,
                border = BorderStroke(1.dp, tokens.hairlineRest),
                modifier = Modifier
                    .weight(1f)
                    .testTag("ai_tutor_header_chapter_pill")
                    .minimumInteractiveComponentSize()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(role = Role.Button, onClick = onOpenChapterPicker),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Book,
                        contentDescription = null,
                        tint = if (linkedChapters.isNotEmpty()) tokens.themeAccent else tokens.inkMuted,
                        modifier = Modifier.size(15.dp),
                    )
                    Text(
                        text = when {
                            linkedChapters.isEmpty() -> stringResource(R.string.grok_tutor_choose_chapters_cta)
                            linkedChapters.size == 1 -> linkedChapters.first().title
                            else -> "${linkedChapters.first().title} +${linkedChapters.size - 1}"
                        },
                        style = MaterialTheme.typography.labelLarge,
                        color = tokens.inkPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    if (linkedChapters.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = tokens.carbonSurface,
                        ) {
                            Text(
                                text = "$scopedDiagramCount RAG",
                                style = MicroTagMono,
                                color = tokens.themeAccent,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            )
                        }
                    }
                }
            }

            // 3-dot (⋮) Chat Menu: Edit linked chapters anytime + View Diagram RAG Index + New Chat
            Box {
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier.testTag("ai_tutor_more_menu_button"),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = stringResource(R.string.grok_tutor_menu_desc),
                        tint = tokens.inkPrimary,
                    )
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    containerColor = tokens.obsidianSurface,
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = stringResource(R.string.grok_tutor_change_chapters_menu),
                                color = tokens.inkPrimary,
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Rounded.Book, contentDescription = null, tint = tokens.themeAccent)
                        },
                        onClick = {
                            menuExpanded = false
                            onOpenChapterPicker()
                        },
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = stringResource(R.string.grok_tutor_inspect_rag_menu),
                                color = tokens.inkPrimary,
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Rounded.Code, contentDescription = null, tint = tokens.inkSecondary)
                        },
                        onClick = {
                            menuExpanded = false
                            onOpenRagIndexDialog()
                        },
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = stringResource(R.string.grok_tutor_new_chat),
                                color = tokens.inkPrimary,
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Rounded.Add, contentDescription = null, tint = tokens.inkSecondary)
                        },
                        onClick = {
                            menuExpanded = false
                            onStartNewChat()
                        },
                    )
                }
            }

            if (!isWideLayout) {
                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier.testTag("ai_tutor_settings_button"),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = stringResource(R.string.settings_open_desc),
                        tint = tokens.inkSecondary,
                    )
                }
            }
        }

        // Main Body: New Chat Centered Welcome OR Active Message Stream
        if (messages.isEmpty()) {
            NewChatWelcomeCanvas(
                linkedChapters = linkedChapters,
                scopedDiagramCount = scopedDiagramCount,
                isWideLayout = isWideLayout,
                onOpenChapterPicker = onOpenChapterPicker,
                onQuickPrompt = { prompt ->
                    if (linkedChapters.isEmpty()) {
                        onOpenChapterPicker()
                    } else {
                        onSendPrompt(prompt)
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .widthIn(max = 760.dp),
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .widthIn(max = 780.dp),
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                // Linked chapter chips bar at top of active thread
                if (linkedChapters.isNotEmpty()) {
                    item(key = "linked_chapters_strip") {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            items(items = linkedChapters, key = { "chip_${it.id}" }) { ch ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = tokens.carbonSurface,
                                    border = BorderStroke(1.dp, tokens.hairlineSubtle),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable(role = Role.Button, onClick = onOpenChapterPicker),
                                ) {
                                    Text(
                                        text = "${ch.chapterCode} · ${ch.title}",
                                        style = MicroTagMono,
                                        color = tokens.inkSecondary,
                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                    )
                                }
                            }
                        }
                    }
                }

                items(items = messages, key = { it.id }) { msg ->
                    TutorMessageItem(
                        message = msg,
                        onOpenDiagramModal = onOpenDiagramModal,
                        onOpenDiagramSplitScreen = onOpenDiagramSplitScreen,
                    )
                }

                if (state.isGeneratingReply) {
                    item(key = "typing_indicator") {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        ) {
                            AnimatedThemeBrandLogo(size = 24.dp)
                            Text(
                                text = "Scanning linked chapter diagram RAG & crafting explanation…",
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.inkMuted,
                            )
                        }
                    }
                }
            }
        }

        // Theme-Adapted Floating Chat Composer
        TutorFloatingComposer(
            text = state.composerText,
            hasLinkedChapters = linkedChapters.isNotEmpty(),
            isGenerating = state.isGeneratingReply,
            onTextChange = onUpdateComposerText,
            onRequestChooseChapter = onOpenChapterPicker,
            onSend = { onSendPrompt(state.composerText) },
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 780.dp)
                .padding(top = 6.dp),
        )
    }
}

@Composable
private fun NewChatWelcomeCanvas(
    linkedChapters: List<VaultChapterNode>,
    scopedDiagramCount: Int,
    isWideLayout: Boolean,
    onOpenChapterPicker: () -> Unit,
    onQuickPrompt: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val quickPrompts = remember(linkedChapters) {
        if (linkedChapters.isEmpty()) {
            listOf(
                "Explain with an interactive diagram",
                "Compare hollow vs solid sphere V(r) & E(r)",
                "Summarize key exam traps & formulas",
            )
        } else {
            val chName = linkedChapters.first().title
            listOf(
                "Explain $chName with an interactive diagram",
                "Give me the key formulas & sign traps in $chName",
                "Show a visual plot to master $chName",
            )
        }
    }

    Column(
        modifier = modifier.padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        AnimatedThemeBrandLogo(size = if (isWideLayout) 66.dp else 56.dp)
        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = if (linkedChapters.isEmpty()) {
                stringResource(R.string.grok_tutor_welcome_headline)
            } else {
                "Ready on ${linkedChapters.first().title}"
            },
            style = MaterialTheme.typography.headlineSmall,
            color = tokens.inkPrimary,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = if (linkedChapters.isEmpty()) {
                stringResource(R.string.grok_tutor_welcome_subtitle)
            } else {
                "${linkedChapters.size} chapter(s) linked · $scopedDiagramCount diagram(s) ready in private RAG storage"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = tokens.inkSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 520.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Prominent Chapter Selector Card / Button
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = if (linkedChapters.isEmpty()) tokens.primaryCta else tokens.carbonSurface,
            border = BorderStroke(
                1.dp,
                if (linkedChapters.isEmpty()) tokens.primaryCta else tokens.hairlineSpecularTop,
            ),
            modifier = Modifier
                .testTag("ai_tutor_welcome_choose_chapter_cta")
                .minimumInteractiveComponentSize()
                .clip(RoundedCornerShape(14.dp))
                .clickable(role = Role.Button, onClick = onOpenChapterPicker),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Book,
                    contentDescription = null,
                    tint = if (linkedChapters.isEmpty()) tokens.onPrimaryCta else tokens.themeAccent,
                    modifier = Modifier.size(17.dp),
                )
                Text(
                    text = if (linkedChapters.isEmpty()) {
                        stringResource(R.string.grok_tutor_choose_chapters_cta)
                    } else {
                        "Edit Linked Chapters (${linkedChapters.size})"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = if (linkedChapters.isEmpty()) tokens.onPrimaryCta else tokens.inkPrimary,
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Quick starter prompts
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp),
        ) {
            items(items = quickPrompts) { prompt ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = tokens.chatAssistantSurface,
                    border = BorderStroke(1.dp, tokens.hairlineRest),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(role = Role.Button) { onQuickPrompt(prompt) },
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = tokens.themeAccent,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = prompt,
                            style = MaterialTheme.typography.labelMedium,
                            color = tokens.inkSecondary,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TutorMessageItem(
    message: TutorChatMessage,
    onOpenDiagramModal: (DiagramAttachment) -> Unit,
    onOpenDiagramSplitScreen: (DiagramAttachment) -> Unit,
) {
    val tokens = GhickinTokens.colors

    if (message.isUser) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            Surface(
                shape = RoundedCornerShape(tokens.chatBubbleRadius),
                color = tokens.chatUserBubble,
                border = BorderStroke(1.dp, tokens.hairlineRest),
                modifier = Modifier.widthIn(max = 520.dp),
            ) {
                Text(
                    text = message.visibleText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = tokens.inkPrimary,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
                )
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AnimatedThemeBrandLogo(size = 20.dp)
                Text(
                    text = "GHICKIN Tutor",
                    style = MaterialTheme.typography.labelLarge,
                    color = tokens.inkPrimary,
                )
                Text(
                    text = "· ${message.timestampLabel}",
                    style = MicroTagMono,
                    color = tokens.inkMuted,
                )
            }

            Surface(
                shape = RoundedCornerShape(tokens.chatBubbleRadius),
                color = tokens.chatAssistantSurface,
                border = BorderStroke(1.dp, tokens.hairlineSubtle),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = message.visibleText,
                        style = MaterialTheme.typography.bodyLarge,
                        color = tokens.inkPrimary,
                    )

                    message.formulaBlock?.let { formula ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = tokens.chatCodeBg,
                            border = BorderStroke(1.dp, tokens.hairlineSubtle),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = formula,
                                style = MonoBadgeStyle,
                                color = tokens.themeAccent,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                            )
                        }
                    }

                    // Inline Gemini/Claude-style Diagram Card when chapter RAG matched/generated a diagram
                    message.diagramAttachment?.let { attachment ->
                        InlineDiagramArtifactCard(
                            attachment = attachment,
                            onOpenZoomModal = { onOpenDiagramModal(attachment) },
                            onOpenSplitScreen = { onOpenDiagramSplitScreen(attachment) },
                        )
                    }

                    if (message.citations.isNotEmpty()) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(items = message.citations) { cite ->
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = tokens.carbonSurface,
                                ) {
                                    Text(
                                        text = cite,
                                        style = MicroTagMono,
                                        color = tokens.inkMuted,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
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
private fun TutorFloatingComposer(
    text: String,
    hasLinkedChapters: Boolean,
    isGenerating: Boolean,
    onTextChange: (String) -> Unit,
    onRequestChooseChapter: () -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val composerShape = RoundedCornerShape(tokens.chatComposerRadius)

    Surface(
        shape = composerShape,
        color = tokens.chatComposerBg,
        border = BorderStroke(1.dp, tokens.hairlineRest),
        modifier = modifier.testTag("ai_tutor_composer"),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (!hasLinkedChapters) {
                // Tapping chat box when no chapter is linked opens the Chapter Choosing Window!
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(role = Role.Button, onClick = onRequestChooseChapter)
                        .padding(horizontal = 6.dp, vertical = 10.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Text(
                        text = stringResource(R.string.grok_tutor_composer_choose_first),
                        style = MaterialTheme.typography.bodyMedium,
                        color = tokens.inkMuted,
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 6.dp, vertical = 6.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (text.isEmpty()) {
                        Text(
                            text = stringResource(R.string.grok_tutor_composer_placeholder),
                            style = MaterialTheme.typography.bodyMedium,
                            color = tokens.inkMuted,
                        )
                    }
                    BasicTextField(
                        value = text,
                        onValueChange = onTextChange,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = tokens.inkPrimary),
                        cursorBrush = SolidColor(tokens.themeAccent),
                        maxLines = 4,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_tutor_composer_input"),
                    )
                }
            }

            val canSend = hasLinkedChapters && text.isNotBlank() && !isGenerating
            Surface(
                shape = CircleShape,
                color = if (canSend) tokens.primaryCta else tokens.trackSubtle,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("ai_tutor_send_button")
                    .clip(CircleShape)
                    .clickable(
                        enabled = canSend || !hasLinkedChapters,
                        role = Role.Button,
                        onClick = {
                            if (!hasLinkedChapters) onRequestChooseChapter() else onSend()
                        },
                    ),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (hasLinkedChapters) Icons.AutoMirrored.Rounded.Send else Icons.Rounded.Book,
                        contentDescription = stringResource(R.string.tutor_send_desc),
                        tint = if (canSend) tokens.onPrimaryCta else tokens.inkMuted,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}
