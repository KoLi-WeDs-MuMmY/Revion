package com.example.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Devices
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Science
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.Style
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.model.AppRagFileId
import com.example.model.AppSettingsPreferences
import com.example.model.ClockThemeId
import com.example.model.FocusMode
import com.example.model.LayoutDensityMode
import com.example.model.MotionIntensity
import com.example.model.SettingsRagHubState
import com.example.model.SettingsSectionTab
import com.example.model.SubjectIndicatorStyle
import com.example.model.WorkspaceUiState
import com.example.ui.components.tactileScale
import com.example.ui.theme.AccentMood
import com.example.ui.theme.AppThemeId
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MicroTagMono
import com.example.ui.theme.rememberScreenLayoutMetrics
import com.example.ui.timer.FlipClockPreviewRow
import com.example.ui.timer.FlipTileSize

/**
 * Phase 1.6.4 Structured Settings Hierarchy:
 * 1. Appearance & Clock (4 XML Workspace Themes, 5 Flip-Clock Themes, Layout Posture, Motion)
 * 2. Study & Timer Defaults (Focus/Break steppers, Auto-Log toggle, Subject indicator & accents)
 * 3. Planner & RAG — Simulated Preview State (AI Planner toggle, 3-edit cap, expandable RAG Inspector)
 * 4. Data & App Info (Reset Mock Demo State with confirmation, Build & Phase readout)
 */
@Composable
fun SettingsScreen(
    uiState: WorkspaceUiState,
    activeTheme: AppThemeId,
    activeMood: AccentMood,
    focusMode: FocusMode,
    ragHubState: SettingsRagHubState,
    settingsPrefs: AppSettingsPreferences,
    isWideLayout: Boolean,
    onSelectSection: (SettingsSectionTab) -> Unit,
    onSelectTheme: (AppThemeId) -> Unit,
    onSelectClockTheme: (ClockThemeId) -> Unit,
    onSelectMotionIntensity: (MotionIntensity) -> Unit,
    onSelectLayoutDensity: (LayoutDensityMode) -> Unit,
    onAdjustDefaultFocusMinutes: (Int) -> Unit,
    onAdjustDefaultBreakMinutes: (Int) -> Unit,
    onToggleAutoLogStudy: (Boolean) -> Unit,
    onSelectSubjectIndicatorStyle: (SubjectIndicatorStyle) -> Unit,
    onSelectAccentMood: (AccentMood) -> Unit,
    onToggleFocusMode: () -> Unit,
    onToggleAiPlanner: (Boolean) -> Unit,
    onAdjustManualEditCap: (Int) -> Unit,
    onSelectRagFile: (AppRagFileId) -> Unit,
    onUpdateRagUserNote: (String) -> Unit,
    onRunRagAiOptimization: (AppRagFileId) -> Unit,
    onRequestResetDemoState: () -> Unit,
    onConfirmResetDemoState: () -> Unit,
    onDismissResetDemoDialog: () -> Unit,
    onCloseSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onCloseSettings)

    val tokens = GhickinTokens.colors
    val metrics = rememberScreenLayoutMetrics()
    val isRagHubExpanded = ragHubState.selectedSection == SettingsSectionTab.RAG_FILES_AI

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen_root"),
        contentAlignment = Alignment.TopCenter,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = if (isWideLayout) 860.dp else 640.dp),
            contentPadding = PaddingValues(
                horizontal = metrics.horizontalPadding,
                vertical = metrics.verticalPadding,
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            // Top Bar
            item(key = "settings_header") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = tokens.carbonSurface,
                        border = BorderStroke(1.dp, tokens.hairlineRest),
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .minimumInteractiveComponentSize()
                            .tactileScale()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(color = tokens.inkPrimary),
                                role = Role.Button,
                                onClick = onCloseSettings,
                            )
                            .testTag("settings_back_button"),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Close Settings",
                                tint = tokens.inkPrimary,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Settings",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = tokens.inkPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = "Workspace themes, flip clock, timer defaults & simulated RAG preview",
                            style = MaterialTheme.typography.bodySmall,
                            color = tokens.inkSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }

            // ─────────────────────────────────────────────────────────────
            // SECTION 1: APPEARANCE & CLOCK
            // ─────────────────────────────────────────────────────────────
            item(key = "section_appearance_clock") {
                SettingsGroupContainer(
                    sectionTitle = "APPEARANCE & CLOCK",
                    modifier = Modifier.testTag("settings_group_appearance"),
                ) {
                    // 1A. Workspace Theme (4 Modular XML Themes)
                    SettingsRowHeader(
                        icon = Icons.Rounded.Palette,
                        title = "Workspace Theme",
                        subtitle = "${activeTheme.label} · ${activeTheme.xmlFileLabel}",
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                    ) {
                        items(items = AppThemeId.entries, key = { it.id }) { themeOption ->
                            val selected = themeOption == activeTheme
                            val bgPreview = colorResource(themeOption.bgRes)
                            val cardPreview = colorResource(themeOption.surfaceCardRes)
                            val accentPreview = colorResource(themeOption.accentRes)

                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (selected) tokens.slateElevated else tokens.obsidianSurface,
                                border = BorderStroke(
                                    width = if (selected) 1.5.dp else 1.dp,
                                    color = if (selected) tokens.themeAccent else tokens.hairlineRest,
                                ),
                                modifier = Modifier
                                    .width(156.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .tactileScale()
                                    .clickable(role = Role.RadioButton) { onSelectTheme(themeOption) }
                                    .testTag("settings_theme_${themeOption.id}"),
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Box(
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .clip(CircleShape)
                                                    .background(bgPreview)
                                                    .border(1.dp, tokens.hairlineRest, CircleShape),
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .clip(CircleShape)
                                                    .background(cardPreview)
                                                    .border(1.dp, tokens.hairlineRest, CircleShape),
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .clip(CircleShape)
                                                    .background(accentPreview),
                                            )
                                        }
                                        if (selected) {
                                            Icon(
                                                imageVector = Icons.Rounded.CheckCircle,
                                                contentDescription = "Selected theme",
                                                tint = tokens.themeAccent,
                                                modifier = Modifier.size(16.dp),
                                            )
                                        }
                                    }
                                    Text(
                                        text = themeOption.label,
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                        color = tokens.inkPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Text(
                                        text = themeOption.fontPairingLabel,
                                        style = MicroTagMono,
                                        color = tokens.inkMuted,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(6.dp))
                    SettingsDivider()

                    // 1B. Flip-Clock Theme Selector (5 Dedicated Clock Themes)
                    SettingsRowHeader(
                        icon = Icons.Rounded.Schedule,
                        title = "Clock Theme",
                        subtitle = "${settingsPrefs.clockTheme.label} · Applied to Home & Timer flip clocks",
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("settings_clock_theme_picker"),
                    ) {
                        items(items = ClockThemeId.entries, key = { it.id }) { clockOpt ->
                            val selected = clockOpt == settingsPrefs.clockTheme
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (selected) tokens.slateElevated else tokens.obsidianSurface,
                                border = BorderStroke(
                                    width = if (selected) 1.5.dp else 1.dp,
                                    color = if (selected) clockOpt.palette.accentColor else tokens.hairlineRest,
                                ),
                                modifier = Modifier
                                    .width(172.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .tactileScale()
                                    .clickable(role = Role.RadioButton) { onSelectClockTheme(clockOpt) }
                                    .testTag("settings_clock_theme_${clockOpt.id}"),
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    FlipClockPreviewRow(
                                        minutesText = "25",
                                        secondsText = "00",
                                        clockTheme = clockOpt,
                                        tileSize = FlipTileSize.NARROW_SPLIT,
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        Text(
                                            text = clockOpt.label,
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = tokens.inkPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f),
                                        )
                                        if (selected) {
                                            Icon(
                                                imageVector = Icons.Rounded.CheckCircle,
                                                contentDescription = "Selected clock theme",
                                                tint = clockOpt.palette.accentColor,
                                                modifier = Modifier.size(15.dp),
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(4.dp))
                    SettingsDivider()

                    // 1C. Layout Behavior (16:9 / 9:16)
                    SettingsSegmentedRow(
                        icon = Icons.Rounded.Devices,
                        title = "Layout Behavior (16:9 / 9:16)",
                        subtitle = if (isWideLayout) "Active: 16:9 Landscape Rail" else "Active: 9:16 Portrait Bar",
                        options = LayoutDensityMode.entries.map { it.label },
                        selectedIndex = settingsPrefs.layoutDensity.ordinal,
                        onSelect = { idx -> onSelectLayoutDensity(LayoutDensityMode.entries[idx]) },
                        testTagPrefix = "settings_layout_density",
                    )

                    SettingsDivider()

                    // 1D. Motion & Spring Intensity
                    SettingsSegmentedRow(
                        icon = Icons.Rounded.Speed,
                        title = "Motion & Spring Intensity",
                        subtitle = settingsPrefs.motionIntensity.subtitle,
                        options = MotionIntensity.entries.map { it.label },
                        selectedIndex = settingsPrefs.motionIntensity.ordinal,
                        onSelect = { idx -> onSelectMotionIntensity(MotionIntensity.entries[idx]) },
                        testTagPrefix = "settings_motion",
                    )
                }
            }

            // ─────────────────────────────────────────────────────────────
            // SECTION 2: STUDY & TIMER DEFAULTS
            // ─────────────────────────────────────────────────────────────
            item(key = "section_study_timer_defaults") {
                SettingsGroupContainer(
                    sectionTitle = "STUDY & TIMER DEFAULTS",
                    modifier = Modifier.testTag("settings_group_timer"),
                ) {
                    SettingsStepperRow(
                        icon = Icons.Rounded.Timer,
                        title = "Default Pomodoro Focus",
                        subtitle = "Starting duration on Home desk clock & Timer",
                        valueLabel = "${settingsPrefs.defaultFocusMinutes} min",
                        onDecrement = { onAdjustDefaultFocusMinutes(-5) },
                        onIncrement = { onAdjustDefaultFocusMinutes(5) },
                        testTagPrefix = "settings_default_focus",
                    )

                    SettingsDivider()

                    SettingsStepperRow(
                        icon = Icons.Rounded.History,
                        title = "Default Break Length",
                        subtitle = "Recovery window between deep focus blocks",
                        valueLabel = "${settingsPrefs.defaultBreakMinutes} min",
                        onDecrement = { onAdjustDefaultBreakMinutes(-5) },
                        onIncrement = { onAdjustDefaultBreakMinutes(5) },
                        testTagPrefix = "settings_default_break",
                    )

                    SettingsDivider()

                    SettingsToggleRow(
                        icon = Icons.Rounded.CheckCircle,
                        title = "Auto-Log to Today's Study Time",
                        subtitle = "Automatically record completed sessions & Revision/Flashcards",
                        checked = settingsPrefs.autoLogStudySessions,
                        onCheckedChange = onToggleAutoLogStudy,
                        testTag = "settings_auto_log_switch",
                    )

                    SettingsDivider()

                    SettingsSegmentedRow(
                        icon = Icons.Rounded.Style,
                        title = "Subject Indicator Style",
                        subtitle = settingsPrefs.subjectIndicatorStyle.subtitle,
                        options = SubjectIndicatorStyle.entries.map { it.label },
                        selectedIndex = settingsPrefs.subjectIndicatorStyle.ordinal,
                        onSelect = { idx -> onSelectSubjectIndicatorStyle(SubjectIndicatorStyle.entries[idx]) },
                        testTagPrefix = "settings_subject_style",
                    )

                    SettingsDivider()

                    // Subject Accent Mood Dots
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Primary Subject Accent",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = tokens.inkPrimary,
                            )
                            Text(
                                text = "${activeMood.label} · Quiet mineral accent dot",
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.inkMuted,
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AccentMood.entries.forEach { mood ->
                                val sel = mood == activeMood
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(mood.accent.copy(alpha = if (sel) 1f else 0.35f))
                                        .border(
                                            width = if (sel) 2.dp else 1.dp,
                                            color = if (sel) tokens.inkPrimary else tokens.hairlineRest,
                                            shape = CircleShape,
                                        )
                                        .clickable(role = Role.RadioButton) { onSelectAccentMood(mood) }
                                        .testTag("settings_mood_${mood.id}"),
                                )
                            }
                        }
                    }
                }
            }

            // ─────────────────────────────────────────────────────────────
            // SECTION 3: PLANNER & RAG (SIMULATED PREVIEW STATE)
            // ─────────────────────────────────────────────────────────────
            item(key = "section_planner_rag") {
                SettingsGroupContainer(
                    sectionTitle = "PLANNER & RAG (SIMULATED PREVIEW)",
                    modifier = Modifier.testTag("settings_group_planner_rag"),
                ) {
                    SettingsToggleRow(
                        icon = Icons.Rounded.AutoAwesome,
                        title = "AI-Governed Daily Planner",
                        subtitle = "AI generates the day plan; schedule changes go through Appeal",
                        checked = uiState.standalonePlannerState.isAiEnabled,
                        onCheckedChange = onToggleAiPlanner,
                        testTag = "settings_ai_planner_switch",
                    )

                    SettingsDivider()

                    SettingsStepperRow(
                        icon = Icons.Rounded.EditNote,
                        title = "Manual Task Edit Cap",
                        subtitle = "Edits allowed before AI Intervention automatically re-balances tasks",
                        valueLabel = "${settingsPrefs.manualTaskEditCap} edits",
                        onDecrement = { onAdjustManualEditCap(-1) },
                        onIncrement = { onAdjustManualEditCap(1) },
                        testTagPrefix = "settings_edit_cap",
                    )

                    SettingsDivider()

                    // Honest Simulated Telemetry & Digital Wellbeing Status Card
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = tokens.obsidianSurface,
                        border = BorderStroke(1.dp, AccentMood.AMBER.accent.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Science,
                                contentDescription = null,
                                tint = AccentMood.AMBER.accent,
                                modifier = Modifier.size(18.dp),
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Text(
                                    text = "Simulated Preview Data Active",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = tokens.inkPrimary,
                                )
                                Text(
                                    text = "RAG(1), RAG(2), and screen-time patterns currently use simulated mock data. Real Android Digital Wellbeing (UsageStatsManager) and live RAG persistence will connect in the upcoming phase.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = tokens.inkSecondary,
                                )
                            }
                        }
                    }

                    SettingsDivider()

                    // Expandable RAG Files & AI Inspector Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(role = Role.Button) {
                                onSelectSection(
                                    if (isRagHubExpanded) SettingsSectionTab.THEMES_DISPLAY
                                    else SettingsSectionTab.RAG_FILES_AI,
                                )
                            }
                            .padding(horizontal = 14.dp, vertical = 13.dp)
                            .testTag("settings_toggle_rag_inspector_row"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Storage,
                            contentDescription = null,
                            tint = tokens.themeAccent,
                            modifier = Modifier.size(20.dp),
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Inspect App RAG Files (Simulated)",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = tokens.inkPrimary,
                            )
                            Text(
                                text = "Planner RAG(1) · RAG(2) · Study Log RAG · Diagram RAG",
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.inkMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        Icon(
                            imageVector = if (isRagHubExpanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                            contentDescription = "Expand RAG Inspector",
                            tint = tokens.inkSecondary,
                        )
                    }

                    AnimatedVisibility(visible = isRagHubExpanded) {
                        Box(modifier = Modifier.padding(12.dp)) {
                            SettingsRagHubSection(
                                uiState = uiState,
                                ragHubState = ragHubState,
                                isWideLayout = isWideLayout,
                                onSelectRagFile = onSelectRagFile,
                                onUpdateUserNote = onUpdateRagUserNote,
                                onRunAiOptimization = onRunRagAiOptimization,
                            )
                        }
                    }
                }
            }

            // ─────────────────────────────────────────────────────────────
            // SECTION 4: DATA & APP INFO
            // ─────────────────────────────────────────────────────────────
            item(key = "section_data_info") {
                SettingsGroupContainer(
                    sectionTitle = "DATA & APP INFO",
                    modifier = Modifier.testTag("settings_group_data"),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(role = Role.Button, onClick = onRequestResetDemoState)
                            .padding(horizontal = 14.dp, vertical = 13.dp)
                            .testTag("settings_reset_demo_row"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.RestartAlt,
                            contentDescription = null,
                            tint = AccentMood.AMBER.accent,
                            modifier = Modifier.size(20.dp),
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Reset Mock Demo State",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = tokens.inkPrimary,
                            )
                            Text(
                                text = "Restore default schedule, 3/3 manual edits, timer logs & demo batches",
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.inkMuted,
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AccentMood.AMBER.accent.copy(alpha = 0.14f),
                            border = BorderStroke(1.dp, AccentMood.AMBER.accent.copy(alpha = 0.45f)),
                        ) {
                            Text(
                                text = "Reset",
                                style = MicroTagMono.copy(fontWeight = FontWeight.Bold),
                                color = AccentMood.AMBER.accent,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            )
                        }
                    }

                    SettingsDivider()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 13.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Info,
                            contentDescription = null,
                            tint = tokens.inkSecondary,
                            modifier = Modifier.size(20.dp),
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "GHICKIN Workspace · Phase 1.6.4",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = tokens.inkPrimary,
                            )
                            Text(
                                text = "Dual 16:9 & 9:16 XML layouts · 4 Theme XMLs · 5 Flip-Clock Themes · Isolated Tick Engine",
                                style = MaterialTheme.typography.bodySmall,
                                color = tokens.inkMuted,
                            )
                        }
                    }
                }
            }
        }
    }

    if (settingsPrefs.showResetDemoConfirmDialog) {
        Dialog(onDismissRequest = onDismissResetDemoDialog) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = tokens.carbonSurface,
                border = BorderStroke(1.dp, tokens.hairlineRest),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 380.dp)
                    .testTag("settings_reset_confirm_dialog"),
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "Reset Demo State?",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = tokens.inkPrimary,
                    )
                    Text(
                        text = "This resets the simulated planner, restores 3/3 task manual edits, resets active study timers, and keeps your selected Workspace and Clock themes.",
                        style = MaterialTheme.typography.bodySmall,
                        color = tokens.inkSecondary,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = tokens.obsidianSurface,
                            border = BorderStroke(1.dp, tokens.hairlineRest),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable(onClick = onDismissResetDemoDialog),
                        ) {
                            Text(
                                text = "Cancel",
                                style = MaterialTheme.typography.labelMedium,
                                color = tokens.inkSecondary,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = tokens.primaryCta,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable(onClick = onConfirmResetDemoState)
                                .testTag("settings_confirm_reset_button"),
                        ) {
                            Text(
                                text = "Reset Now",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = tokens.onPrimaryCta,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsGroupContainer(
    sectionTitle: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val tokens = GhickinTokens.colors
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = sectionTitle,
            style = MicroTagMono.copy(fontWeight = FontWeight.Bold),
            color = tokens.inkMuted,
            modifier = Modifier.padding(start = 4.dp),
        )
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = tokens.carbonSurface,
            border = BorderStroke(1.dp, tokens.hairlineRest),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                content()
            }
        }
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        color = GhickinTokens.colors.hairlineSubtle,
        thickness = 1.dp,
        modifier = Modifier.padding(horizontal = 14.dp),
    )
}

@Composable
private fun SettingsRowHeader(
    icon: ImageVector,
    title: String,
    subtitle: String,
) {
    val tokens = GhickinTokens.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 14.dp, end = 14.dp, top = 13.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(icon, contentDescription = null, tint = tokens.themeAccent, modifier = Modifier.size(20.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = tokens.inkPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = tokens.inkMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String,
) {
    val tokens = GhickinTokens.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(icon, contentDescription = null, tint = tokens.themeAccent, modifier = Modifier.size(20.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = tokens.inkPrimary,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = tokens.inkMuted,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = tokens.onPrimaryCta,
                checkedTrackColor = tokens.primaryCta,
            ),
            modifier = Modifier.testTag(testTag),
        )
    }
}

@Composable
private fun SettingsStepperRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    valueLabel: String,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    testTagPrefix: String,
) {
    val tokens = GhickinTokens.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(icon, contentDescription = null, tint = tokens.themeAccent, modifier = Modifier.size(20.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = tokens.inkPrimary,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = tokens.inkMuted,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Surface(
                shape = RoundedCornerShape(9.dp),
                color = tokens.obsidianSurface,
                border = BorderStroke(1.dp, tokens.hairlineRest),
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .clickable(role = Role.Button, onClick = onDecrement)
                    .testTag("${testTagPrefix}_minus"),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.Remove, contentDescription = "Decrease", tint = tokens.inkPrimary, modifier = Modifier.size(16.dp))
                }
            }
            Text(
                text = valueLabel,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                ),
                color = tokens.inkPrimary,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.padding(horizontal = 4.dp),
            )
            Surface(
                shape = RoundedCornerShape(9.dp),
                color = tokens.obsidianSurface,
                border = BorderStroke(1.dp, tokens.hairlineRest),
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .clickable(role = Role.Button, onClick = onIncrement)
                    .testTag("${testTagPrefix}_plus"),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.Add, contentDescription = "Increase", tint = tokens.inkPrimary, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
private fun SettingsSegmentedRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    testTagPrefix: String,
) {
    val tokens = GhickinTokens.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 11.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(icon, contentDescription = null, tint = tokens.themeAccent, modifier = Modifier.size(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = tokens.inkPrimary,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(
                items = options.indices.toList(),
                key = { it },
            ) { idx ->
                val sel = idx == selectedIndex
                Surface(
                    shape = RoundedCornerShape(9.dp),
                    color = if (sel) tokens.primaryCta else tokens.obsidianSurface,
                    border = BorderStroke(1.dp, if (sel) tokens.primaryCta else tokens.hairlineRest),
                    modifier = Modifier
                        .clip(RoundedCornerShape(9.dp))
                        .clickable(role = Role.RadioButton) { onSelect(idx) }
                        .testTag("${testTagPrefix}_$idx"),
                ) {
                    Text(
                        text = options[idx],
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = if (sel) tokens.onPrimaryCta else tokens.inkSecondary,
                        maxLines = 1,
                        softWrap = false,
                        modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp),
                    )
                }
            }
        }
    }
}
