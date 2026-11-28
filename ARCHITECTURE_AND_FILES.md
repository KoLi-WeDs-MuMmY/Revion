# GHICKIN — Living Architecture & File Manifest

> **Standing Maintenance Rule:** Keep this file updated after every future code prompt (adding/renaming/removing files, updating implemented phase notes, and refining the roadmap) unless explicitly instructed otherwise.

---

## 1. App Overview & Core Architecture

**GHICKIN** is a native Android study, active-recall, and focus workspace built with **Kotlin** and **Jetpack Compose (Material 3)**. It is engineered for distraction-free daily learning across both **16:9 landscape** (tablets, foldables, horizontal study stands) and **9:16 portrait** (phones and vertical tablets) form factors.

### Architectural Pillars
- **Single-Activity MVVM + Anti-Lag Isolated Leaf Tick Stream:** `MainActivity` hosts `WorkspaceViewModel`, which exposes `StateFlow<WorkspaceUiState>` for structural UI state plus a dedicated `StateFlow<LiveClockTick>` (`clockTick`) observed ONLY by leaf clock composables (`FlipClockReadoutLeaf`, `LiveClockTickTextLeaf`, `YptLiveMemberClockLeaf`, `AutoTimerPillReadoutLeaf`) so root layouts, NavRail, and sibling cards never recompose on 1-second timer ticks.
- **Modular 4-Theme XML + 5 Flip-Clock Theme Architecture:** Every workspace theme lives in its own dedicated XML color resource file (`theme_grok_black.xml`, `theme_claude_editorial.xml`, `theme_paper_light.xml`, `theme_slate.xml`) paired with bundled local typography, theme-matched Home artwork, and 5 selectable physical 3D split-flap Clock Themes (*Obsidian Flip*, *Warm Terracotta Flip*, *Paper Ivory Flip*, *Slate Steel Flip*, *AMOLED Pure Flip*).
- **Dual-Orientation Layout System (`16:9` + `9:16`):** Separate orientation-qualified dimension XMLs (`values-land/dimens_screen.xml` and `values-port/dimens_screen.xml`) work alongside `BoxWithConstraints` (`maxWidth > maxHeight`) to switch cleanly between a left `GhickinNavRail` + multi-column workspace in landscape and a `GhickinBottomNavBar` + full-width single-column stack in portrait.
- **Private On-Device WebView Diagram Engine:** `PrivateDiagramStore` stores self-contained HTML/SVG/interactive-JS diagrams inside `context.filesDir/diagrams/` and renders them offline in `ImageCodeWebViewer` with pinch/button zoom, modal, and split-screen modes.
- **Distraction-Free Full-Screen Focus Lock:** Opening Study Timer & YPT Group Study or launching Play from Home's Flip Clock collapses all navigation chrome; running a manual timer enforces a single-timer lock and restricts navigation strictly to Timer and Vault (with a persistent return banner in Vault).

---

## 2. Implemented Features by Phase (Phases 1.1 – 1.6.4)

- **Phases 1.1 – 1.5 (Core Workspace Foundations):** Edge-to-edge workspace shell, subject accent moods (`Cyan`, `Violet`, `Emerald`, `Amber`), initial Batches & Syllabus hierarchy, Vault browser & mock PDF reader, Revision notes & conceptual diagrams, Flashcard flip decks & Mistake Bank, and initial Co-Plan / Tutor foundations.
- **Phase 1.6.1 & Manual Multi-Theme / Dual-Layout Tweaks:**
  - 4 standalone XML themes (*Grok Black*, *Claude Editorial*, *Paper Light*, *Slate*) + dedicated Settings screen + bundled Google Fonts + theme-aware Revision/Flashcards artwork.
  - Dedicated `9:16` portrait XML dimens & single-column portrait layouts alongside `16:9` landscape layouts.
  - Home Study Gate (unlocking today's new study after Revision + Flashcards, plus Quick Gate Test toggle), live computed syllabus & recall analytics, and Primary Batch sync.
  - Full CRUD (create, rename, edit, delete) across Batches, Subjects, Chapters, and Lectures.
  - PW-Style Vault hierarchy (*Subjects → Chapters → Lectures*) with dedicated *Class Notes*, *Flashcards*, and *Revision* sub-views and Command Palette jump/search.
- **Phase 1.6.2 (Grok-Style AI Tutor, Image-Code Web Viewer & Test Arena):**
  - Dedicated **Quizzes** navigation tab with PW-style Subject/Chapter browser, **Exam Mode** (live countdown timer, mark-for-review, question palette, `+4 / -1` negative marking) vs **Normal Practice Mode**, step-by-step explanations, and weak-topic analytics with one-tap Tutor handoff.
  - **Grok-Style AI Tutor** with collapsible batch/chapter-grouped chat sidebar, multi-chapter picker (`3-dot menu` + chat start prompt), scoped Chapter Diagram RAG lookup, invisible filename tagging (`image.<file>.html`), inline clickable diagram cards, and zoomable modal + split-screen `ImageCodeWebViewer`.
- **Phase 1.6.3 (Standalone Daily Planner, Synced Study Timer + YPT Group Study & Settings RAG Hub):**
  - **Standalone Daily Planner (`DAILY_PLANNER`):** Independent of AI Tutor with zero-context carryover on every window open (resets to 0 turns, loads strictly from Planner RAG(1) + RAG(2)); AI Planner ON/OFF toggle; dual RAG architecture — **RAG(1)** (20-day individual + 1–2m & 6m combined screen time & study hours, 3-day auto-opt, first-time compaction + simulation control) and **RAG(2)** (app-generated via AI: active hours, peak focus hours, live task completion %); interactive **AI Approval Gate Modal** with Approve, Counter-Proposal, and Deny paths for confirming/editing/cancelling/adding tasks.
  - **Synced Full-Screen Study Timer & YPT Group Study (`TIMER_GROUP` + Home Entry + Auto-Timers):** Full-screen collapsed-navigation page; Home quick-launch Study Timer card; automatic `00:00` count-up timers for Revision & Flashcards pages that auto-log on exit; Pomodoro (4 custom Canvas ring designs + presets `25/5`, `50/10`, `90/15` + `+5m`) and Calendar/Flip-Clock Stopwatch; hold-and-drag 5-ratio split screen (`100:0`, `75:25`, `50:50`, `25:75`, `0:100`) with Group Study toggle, auto-switch to `25:75` on start, and upper-left floating clock in `0:100`; high-timer Subjects quick-start panel, main Start Subject-Tag + optional Lecture-sync dialog (with Skip Tagless), single-timer lock, and Vault-only lockout while active; public YPT group browser/creator + silent presence grid + unoptimized 20-day daily study graph (preserving Highest/Lowest day records) + Group Leaderboard.
  - **Restructured Settings (`Themes & Display` | `RAG Files & AI`):** Preserves all 4 modular XML themes and display controls while centralizing all 4 app RAG files (*Planner RAG(1)*, *Planner RAG(2)*, *Study Log RAG*, *Chapter Diagram RAG*) with **Ask AI to Optimize & Recommend** enforcing **Data-First Priority**.
- **Phase 1.6.4 (UI/UX Overhaul, Anti-Lag Architecture, 3D Flip Clock, Authentic YPT Copy & Materialized Planner):**
  - **Anti-Lag Leaf Tick Architecture:** Isolated 1-second timer/stopwatch & peer ticks into `WorkspaceViewModel.clockTick: StateFlow<LiveClockTick>` observed only by leaf composables; stable keys across all lazy lists and spring physics scaled by `MotionIntensity`.
  - **Physical 3D Split-Flap Flip Clock & 5 Clock Themes:** Hardware-accelerated `graphicsLayer(rotationX, cameraDistance)` half-card mechanical flip clock across Home, Pomodoro (`25/5`, `50/10`, `90/15`, custom min/sec steppers), and Flip Stopwatch, styled dynamically by 5 selectable Clock Themes in Settings.
  - **Structured 4-Section Settings Hierarchy:** *Appearance & Clock* (4 XML themes, 5 Clock Themes with miniature flip previews, 16:9/9:16 density, motion intensity), *Study & Timer Defaults* (focus/break steppers, auto-log toggle, subject indicator style, accent moods), *Planner & RAG — Simulated Preview* (AI toggle, manual task edit cap, honest simulated-data notice, expandable RAG Inspector), and *Data & App Info* (Reset Demo State with confirmation dialog).
  - **Home Screen Desk Clock & Plan Upgrades:** Modern rectangular Flip-Clock card (Play opens cascading `Batch → Subject → Chapter → Lecture` / Start Untagged picker and launches Full-Screen Flip Clock collapsing navigation until Stop) paired with an airy **Today's Plan & Tasks** card (next scheduled blocks, interactive task checkboxes, one-tap Open Planner).
  - **Zero-Ratio Smooth Split-Screen & Authentic YPT-Copy Group Study:** Eliminated all ratio text (`100:0`..`0:100`), replaced with a single **Split View** toggle + spring-snapping drag divider across 5 `SplitPosture` stops (`maxLines = 1, softWrap = false` guards against vertical letter wrapping), and overhauled Group Study into an authentic YPT-style desk matrix (room header, status icons, isolated live member clocks, tap-to-inspect silent boost dialog, and bottom **Allowed Apps** dock + editor).
  - **Materialized Daily Planner, 28% / Slide-Over Zero-Context AI Drawer, Appeal System & 3-Edit Tasks:** Materialized cards + **28-day Study & Task Completion Heatmap** + transparent Simulated RAG preview modal; 2-option view toggle (`Planner Only` vs `Planner + AI`) opening a **28% side panel** on landscape or **slide-over drawer** on portrait that resets to 0 turns on every open; AI-Only schedule blocks with **Appeal Modal** (*Approved & Updated* and *Declined* paths); Tasks section with **3-edit limit** automatically triggering **AI Intervention** (re-balancing tasks & locking manual edits until AI re-authorizes).

---

## 3. Complete File-by-File Manifest

### Root & Gradle Configuration
- `ARCHITECTURE_AND_FILES.md` — Living architecture overview, implemented phases, full file index, and upcoming roadmap.
- `metadata.json` — AI Studio platform app metadata (app name, description, and required capabilities).
- `settings.gradle.kts` — Gradle root project configuration (`GHICKIN`) and module includes.
- `build.gradle.kts` — Root Gradle build script declaring Android/Kotlin plugin declarations.
- `gradle.properties` — Project-wide Gradle JVM and AndroidX build flags.
- `gradle/libs.versions.toml` — Centralized Version Catalog defining Kotlin, Compose, Material 3, and AndroidX dependency versions.
- `gradle/wrapper/gradle-wrapper.properties` — Gradle wrapper distribution properties.
- `app/build.gradle.kts` — App module Gradle build config (`applicationId`, SDK targets, Compose setup, signing configs, dependencies).
- `app/proguard-rules.pro` — ProGuard/R8 shrinking and obfuscation rules.

### Android Manifest, Entry Point & ViewModel
- `app/src/main/AndroidManifest.xml` — App manifest declaring `MainActivity`, launcher intent, and adaptive launcher icon resources.
- `app/src/main/java/com/example/MainActivity.kt` — Single-activity Compose entry point binding `WorkspaceViewModel`, `clockTick`, `Phase163Actions`, and `Phase164Actions` into `WorkspaceShell`.
- `app/src/main/java/com/example/viewmodel/WorkspaceViewModel.kt` — Central `ViewModel` managing `WorkspaceUiState`, isolated zero-lag `StateFlow<LiveClockTick>`, batch/vault/quiz/tutor logic, materialized planner (appeals, 3-edit AI intervention, zero-context drawer), flip-clock timer & YPT room actions, and structured Settings preferences.

### Data Layer & Models (`data/` & `model/`)
- `app/src/main/java/com/example/data/diagram/PrivateDiagramStore.kt` — Writes and resolves named offline HTML/SVG/interactive-JS diagram files in private `context.filesDir/diagrams/`.
- `app/src/main/java/com/example/data/mock/Phase162MockData.kt` — Seed sessions, chapter diagram RAG catalog, and PW-style quiz banks/attempts for Phase 1.6.2.
- `app/src/main/java/com/example/data/mock/Phase163MockData.kt` — Seed simulated data for Planner RAG(1)/RAG(2), 28-day heatmap, user tasks, zero-context AI greetings, unoptimized 20-day study history, YPT public rooms & Allowed Apps, and Data-First RAG optimizer.
- `app/src/main/java/com/example/model/WorkspaceModels.kt` — Top-level `WorkspaceTab`, `FocusMode`, `WorkspaceUiState` (including `settingsPrefs` and `homeClockTagPicker`), and Home live analytics models.
- `app/src/main/java/com/example/model/Phase12Models.kt` — Core data models for `StudyBatch`, `BatchSubject`, `BatchChapter`, `VaultMaterial`, and CRUD dialog states.
- `app/src/main/java/com/example/model/Phase12MockData.kt` — Initial study batches and legacy vault material seed data.
- `app/src/main/java/com/example/model/Phase13Models.kt` — Data models for legacy Revision sheets, conceptual diagrams, and quiz modes.
- `app/src/main/java/com/example/model/Phase13MockData.kt` — Seed revision sheets, compound mix sheet, and question sets for Phase 1.3.
- `app/src/main/java/com/example/model/Phase14Models.kt` — Data models for spaced-repetition flashcards (`x-1`, `x-3`, `x-7`) and Mistake Bank entries.
- `app/src/main/java/com/example/model/Phase14MockData.kt` — Initial flashcard deck and mistake bank seed data.
- `app/src/main/java/com/example/model/Phase15Models.kt` — Data models for legacy Co-Plan AI Tutor messages and permission-gated planner blocks.
- `app/src/main/java/com/example/model/Phase15MockData.kt` — Seed chapters, chat messages, and planner blocks for Phase 1.5.
- `app/src/main/java/com/example/model/Phase161Models.kt` — Models for Home Study Gate, Daily Pages, PW-style Vault hierarchy (`VaultSubjectNode`, `VaultChapterNode`, `VaultLectureNode`), and Command Palette.
- `app/src/main/java/com/example/model/Phase161MockData.kt` — Seed PW-style Vault subjects/chapters/lectures and yesterday's Revision & Flashcards data.
- `app/src/main/java/com/example/model/Phase162Models.kt` — Models for Grok-style AI Tutor sessions, linked chapters, diagram attachments/viewer state, and Exam/Normal Test Arena state.
- `app/src/main/java/com/example/model/Phase163Models.kt` — Models for Standalone Daily Planner (RAG(1), RAG(2), AI Gate, heatmap/appeals/tasks state), Synced Study Timer + YPT Group Study, and Settings RAG Hub.
- `app/src/main/java/com/example/model/Phase164Models.kt` — Models for isolated `LiveClockTick`, 5 `ClockThemeId` flip-clock palettes, `AppSettingsPreferences`, `HomeClockTagPickerState`, zero-ratio `SplitPosture`, `YptAllowedApp`, `PlannerHeatmapDay`, `PlannerAppealRequest`, `PlannerUserTask`, and `PlannerAiSideMessage`.

### UI — Navigation & Shell (`ui/navigation/`, `ui/placeholders/`)
- `app/src/main/java/com/example/ui/navigation/WorkspaceShell.kt` — Responsive 16:9/9:16 workspace shell, `Phase163Actions` & `Phase164Actions` holders, leaf `clockTickFlow` routing, full-screen timer chrome collapse, Vault-only lockout banner, and animated tab stage.
- `app/src/main/java/com/example/ui/navigation/WorkspaceNavRail.kt` — Landscape `GhickinNavRail` (with pinned bottom Settings gear) and portrait `GhickinBottomNavBar`.
- `app/src/main/java/com/example/ui/placeholders/PhasePlaceholderScreen.kt` — Reusable phase placeholder screen retained for future expansion tabs.

### UI — Theme & Shared Components (`ui/theme/`, `ui/components/`)
- `app/src/main/java/com/example/ui/theme/Color.kt` — Color token definitions and `AccentMood` / `AppThemeId` enums mapped to modular XML theme files.
- `app/src/main/java/com/example/ui/theme/Theme.kt` — `GhickinTheme` composable and `GhickinTokens` `CompositionLocal` loading active XML theme tokens dynamically.
- `app/src/main/java/com/example/ui/theme/Type.kt` — Multi-font Material 3 typography definitions bound to theme-specific bundled `.ttf` fonts and tabular monospace styles.
- `app/src/main/java/com/example/ui/theme/ScreenMetrics.kt` — Composable helper loading orientation-specific spacing and layout dimensions from `dimens_screen.xml`.
- `app/src/main/java/com/example/ui/components/AmbientMineralBackdrop.kt` — Theme-adaptive subtle ambient backdrop rendered behind workspace surfaces.
- `app/src/main/java/com/example/ui/components/GhickinSurface.kt` — Signature `ObsidianCard` surface primitive adapting cleanly to dark and light themes.
- `app/src/main/java/com/example/ui/components/KineticControls.kt` — Tactile interactive pills, press scale modifiers, and linear progress bar components.
- `app/src/main/java/com/example/ui/components/KineticMotion.kt` — Staggered entrance animations and kinetic numeric counter text utilities.
- `app/src/main/java/com/example/ui/components/MasteryRing.kt` — Animated circular mastery progress ring used on Home and analytics cards.
- `app/src/main/java/com/example/ui/components/StatusPulse.kt` — Live focus status pulse dot indicator.

### UI — Home Command Center (`ui/home/`)
- `app/src/main/java/com/example/ui/home/HomeCommandCenter.kt` — Primary Home dashboard featuring real-time batch progress, rectangular 3D Flip-Clock card, Today's Plan & Tasks card, Study Gate, Today's Revision & Flashcards cards, and Studio Quick-Launch strip.
- `app/src/main/java/com/example/ui/home/HomeClockAndPlanCards.kt` — Rectangular 3D Flip-Clock desk card, airy Today's Plan & Tasks quick-check card, and cascading `Batch → Subject → Chapter → Lecture` clock tag picker modal.
- `app/src/main/java/com/example/ui/home/HomeDailyPages.kt` — Dedicated `TodayRevisionPage` and `TodayFlashcardsPage` full views with isolated leaf `AutoTimerPillReadoutLeaf` count-up timer badges.
- `app/src/main/java/com/example/ui/home/HomeHeaderBar.kt` — Top greeting header bar with streak pill, mood selector, and Settings entry.
- `app/src/main/java/com/example/ui/home/HeroRevisionCard.kt` — Legacy hero revision summary card component.
- `app/src/main/java/com/example/ui/home/InspectDetailSheet.kt` — Detail inspection modal sheet for subjects, logged topics, and spaced reviews.
- `app/src/main/java/com/example/ui/home/SubjectMasteryRow.kt` — Horizontal mastery bar row component for subject breakdowns.
- `app/src/main/java/com/example/ui/home/TopicsAndReviewSection.kt` — Yesterday's logged topics and spaced-review schedule card section.

### UI — Batches & Vault (`ui/batches/`, `ui/vault/`)
- `app/src/main/java/com/example/ui/batches/BatchesScreen.kt` — Editable Batches & Syllabus workspace coordinating roster, subjects, chapters, lectures, and CRUD modals.
- `app/src/main/java/com/example/ui/batches/BatchRosterPane.kt` — Left/top batch selector roster with Primary Batch badge and create CTA.
- `app/src/main/java/com/example/ui/batches/BatchSubjectCard.kt` — Expandable subject/chapter/lecture card with completion checkboxes and 3-dot CRUD menus.
- `app/src/main/java/com/example/ui/batches/BatchCrudDialogs.kt` — Create, edit, and delete confirmation dialogs for batches, subjects, chapters, and lectures.
- `app/src/main/java/com/example/ui/batches/CreateBatchBuilderSheet.kt` — Multi-subject custom batch builder modal sheet.
- `app/src/main/java/com/example/ui/vault/PwVaultScreen.kt` — PW-Style Study Vault main screen supporting *Hierarchy*, *Class Notes*, *Flashcards*, and *Revision* modes.
- `app/src/main/java/com/example/ui/vault/VaultCategorySubViews.kt` — Specialized sub-views for Vault Class Notes, Flashcards preview, and Revision sheets.
- `app/src/main/java/com/example/ui/vault/VaultCommandPaletteOverlay.kt` — Search & filter command palette overlay for instant Vault lecture jumping.
- `app/src/main/java/com/example/ui/vault/VaultFilterHeader.kt` — Legacy Vault subject/category filter header bar.
- `app/src/main/java/com/example/ui/vault/VaultMaterialCard.kt` — Legacy Vault study material list item card.
- `app/src/main/java/com/example/ui/vault/VaultMockPdfReader.kt` — Simulated annotated PDF & lecture notes reader pane.
- `app/src/main/java/com/example/ui/vault/VaultPreviewPane.kt` — Legacy Vault split-view material preview pane.
- `app/src/main/java/com/example/ui/vault/VaultScreen.kt` — Legacy Vault screen preserved for backward compatibility.

### UI — Grok AI Tutor, Image-Code Web Viewer & Quizzes (`ui/tutor/`, `ui/webviewer/`, `ui/test/`, `ui/quiz/`, `ui/revision/`, `ui/flashcards/`, `ui/mistakes/`)
- `app/src/main/java/com/example/ui/tutor/GrokAiTutorScreen.kt` — Grok-style AI Tutor with batch/chapter-grouped collapsible sidebar, multi-chapter picker, chapter RAG diagram lookup, and inline diagram cards.
- `app/src/main/java/com/example/ui/webviewer/ImageCodeWebViewer.kt` — Offline Android `WebView` modal and split-screen viewer rendering private `context.filesDir` HTML/SVG/JS diagrams with zoom controls.
- `app/src/main/java/com/example/ui/test/PwTestArenaScreen.kt` — PW-style Quizzes Arena subject/chapter launcher and Exam vs. Normal Mode selection gate.
- `app/src/main/java/com/example/ui/test/PwTestSessionViews.kt` — Active quiz runner (countdown timer, `+4/-1` scoring, mark-for-review, palette) and post-test breakdown with weak-topic analytics.
- `app/src/main/java/com/example/ui/tutor/AiTutorComponents.kt` — Legacy AI Tutor message bubble and suggestion chip components.
- `app/src/main/java/com/example/ui/tutor/ChapterRagTutorView.kt` — Legacy chapter-scoped RAG tutor chat pane.
- `app/src/main/java/com/example/ui/tutor/TutorPlannerHubScreen.kt` — Legacy combined Tutor + Co-Plan hub screen.
- `app/src/main/java/com/example/ui/quiz/QuizArenaView.kt` — Legacy Phase 1.3 quiz runner view.
- `app/src/main/java/com/example/ui/quiz/QuizResultBreakdownView.kt` — Legacy Phase 1.3 quiz result breakdown view.
- `app/src/main/java/com/example/ui/revision/ConceptualDiagramCard.kt` — Legacy vector conceptual diagram card component.
- `app/src/main/java/com/example/ui/revision/RevisionNotesView.kt` — Legacy structured revision notes reader view.
- `app/src/main/java/com/example/ui/revision/RevisionQuizHubScreen.kt` — Legacy Revision & Quiz hub container.
- `app/src/main/java/com/example/ui/flashcards/FlashcardDeckView.kt` — Spaced-repetition flashcard deck browser view.
- `app/src/main/java/com/example/ui/flashcards/FlashcardsHubScreen.kt` — Flashcards & Mistake Bank hub screen.
- `app/src/main/java/com/example/ui/flashcards/PhysicalFlipFlashcard.kt` — 3D animated flippable flashcard component.
- `app/src/main/java/com/example/ui/mistakes/MistakeBankView.kt` — Mistake Bank list and remediation detail view.

### UI — Standalone Daily Planner (`ui/planner/`)
- `app/src/main/java/com/example/ui/planner/StandaloneDailyPlannerScreen.kt` — Materialized Daily Planner with 28-day study & task completion heatmap, 2-option view toggle (`Planner Only` | `Planner + AI` as 28% landscape panel / slide-over portrait drawer resetting to 0 turns), AI-Only schedule blocks with Appeal action, and 3-edit Tasks list with automatic AI Intervention.
- `app/src/main/java/com/example/ui/planner/PlannerAppealAndRagDialogs.kt` — Interactive `PlannerAppealModal` (*Approved & Updated* and *Declined* paths), transparent `SimulatedPlannerRagSheetDialog` (clearly labeling simulated preview data), and `PlannerTaskEditorDialog`.
- `app/src/main/java/com/example/ui/planner/PlannerAiGateModal.kt` — Legacy Phase 1.6.3 AI Gate modal retained for reference.
- `app/src/main/java/com/example/ui/planner/DailyPlannerView.kt` — Legacy Phase 1.5 daily timeline view.
- `app/src/main/java/com/example/ui/planner/PlannerModals.kt` — Legacy Phase 1.5 schedule permission modals.

### UI — Synced Study Timer & YPT Group Study (`ui/timer/`, `ui/groupstudy/`)
- `app/src/main/java/com/example/ui/timer/FlipClockComponents.kt` — Hardware-accelerated 3D half-card mechanical split-flap Flip Clock (`FlipClockReadoutLeaf`, `FlipDigitPairTile`, `FlipClockPreviewRow`) and zero-lag leaf clock readouts (`LiveClockTickTextLeaf`, `YptLiveMemberClockLeaf`, `AutoTimerPillReadoutLeaf`).
- `app/src/main/java/com/example/ui/timer/StudyTimerAndGroupScreen.kt` — Full-screen collapsed-chrome 3D Flip-Clock Timer & YPT Group Study workspace with zero ratio labels, single Split View toggle, spring-snapping drag divider, custom min/sec steppers, and single-line narrow-pane overflow guards.
- `app/src/main/java/com/example/ui/timer/TimerVisualInstruments.kt` — Legacy Phase 1.6.3 Canvas Pomodoro rings & Stopwatch dial.
- `app/src/main/java/com/example/ui/timer/TimerStartTagDialog.kt` — Main Start popup for tagging a Subject (only), optionally syncing a Lecture to track exact lecture time, or skipping tagless.
- `app/src/main/java/com/example/ui/timer/TimerStatsAndLeaderboardSheet.kt` — Unoptimized 20-day daily study bar graph (preserving Highest/Lowest day records), 3-day Study Log RAG summary, recent session/lecture logs, and live Group Leaderboard.
- `app/src/main/java/com/example/ui/groupstudy/YptGroupStudyPane.kt` — Authentic YPT-copy Group Study desk matrix with room switcher, Create Public Room dialog, member status icons, isolated live peer clocks, tap-to-inspect silent boost dialog, and iconic bottom Allowed Apps dock + editor.

### UI — Settings & Centralized RAG Hub (`ui/settings/`)
- `app/src/main/java/com/example/ui/settings/SettingsScreen.kt` — Structured 4-section Settings hierarchy (*Appearance & Clock*, *Study & Timer Defaults*, *Planner & RAG — Simulated Preview*, *Data & App Info*) with 4 XML themes, 5 live-preview Flip-Clock Themes, timer steppers, and Reset Demo State confirmation dialog.
- `app/src/main/java/com/example/ui/settings/SettingsRagHubSection.kt` — Expandable RAG Files & AI hub inspecting all 4 simulated app RAGs with Data-First Priority AI optimization & recommendations.

### Resources (`app/src/main/res/`)
- `app/src/main/res/values/strings.xml` — App name (`GHICKIN`) and localized UI string resources across all phases.
- `app/src/main/res/values/colors.xml` — Base Android system/launcher color resources.
- `app/src/main/res/values/themes.xml` — Base Android activity window theme.
- `app/src/main/res/values/theme_grok_black.xml` — Modular XML color palette for **Grok Black** theme.
- `app/src/main/res/values/theme_claude_editorial.xml` — Modular XML color palette for **Claude Editorial** theme.
- `app/src/main/res/values/theme_paper_light.xml` — Modular XML color palette for **Paper Light** theme.
- `app/src/main/res/values/theme_slate.xml` — Modular XML color palette for **Slate** theme.
- `app/src/main/res/values/dimens_screen.xml` — Default screen spacing and layout dimensions.
- `app/src/main/res/values-land/dimens_screen.xml` — `16:9` landscape-specific screen padding, card density, and split layout dimensions.
- `app/src/main/res/values-port/dimens_screen.xml` — `9:16` portrait-specific screen padding, card density, and single-column layout dimensions.
- `app/src/main/res/font/inter.ttf` — Local bundled Inter body font.
- `app/src/main/res/font/jetbrains_mono.ttf` — Local bundled JetBrains Mono tabular/code font.
- `app/src/main/res/font/lora.ttf` — Local bundled Lora editorial serif font (Claude Editorial theme).
- `app/src/main/res/font/outfit.ttf` — Local bundled Outfit display font.
- `app/src/main/res/font/plus_jakarta_sans.ttf` — Local bundled Plus Jakarta Sans UI font.
- `app/src/main/res/font/space_grotesk.ttf` — Local bundled Space Grotesk geometric display font (Grok Black theme).
- `app/src/main/res/drawable/ic_ghickin_mark.png` — Brand mark drawable for GHICKIN headers and nav rail.
- `app/src/main/res/drawable/ic_launcher_background.xml` — Adaptive launcher icon background vector/color.
- `app/src/main/res/drawable/ic_launcher_foreground.xml` — Adaptive launcher icon foreground vector.
- `app/src/main/res/drawable/img_today_revision_art.jpg` — Grok/Slate theme artwork for Today's Revision card.
- `app/src/main/res/drawable/img_today_flashcards_art.jpg` — Grok/Slate theme artwork for Today's Flashcards card.
- `app/src/main/res/drawable/img_revision_claude.jpg` — Claude Editorial theme artwork for Today's Revision card.
- `app/src/main/res/drawable/img_flashcards_claude.jpg` — Claude Editorial theme artwork for Today's Flashcards card.
- `app/src/main/res/drawable/img_revision_paper.jpg` — Paper Light theme artwork for Today's Revision card.
- `app/src/main/res/drawable/img_flashcards_paper.jpg` — Paper Light theme artwork for Today's Flashcards card.
- `app/src/main/res/mipmap-*/ic_launcher*.png` & `mipmap-anydpi-v26/ic_launcher*.xml` — Adaptive & density-bucket launcher icon assets.
- `app/src/main/res/xml/backup_rules.xml` — Android Auto Backup rules.
- `app/src/main/res/xml/data_extraction_rules.xml` — Android 12+ cloud/device-transfer extraction rules.

### Tests (`app/src/test/` & `app/src/androidTest/`)
- `app/src/test/java/com/example/ExampleUnitTest.kt` — Baseline JVM unit test template.
- `app/src/test/java/com/example/ExampleRobolectricTest.kt` — Baseline Robolectric local JVM test template.
- `app/src/androidTest/java/com/example/ExampleInstrumentedTest.kt` — Standard Android test template stub.

---

## 4. Planned Upcoming Roadmap (Post-Phase 1.6.4)

1. **Real OS Telemetry Ingestion (`UsageStatsManager`):** Wire real Android Digital Wellbeing / screen-time permissions (`PACKAGE_USAGE_STATS`) to replace the simulated preview data in Planner RAG(1) and auto-derive real RAG(2) active/peak windows.
2. **Firebase Group Study Live Sync (YPT Realtime Backend):** Provision Firebase Auth + Firestore to sync public study rooms, live peer focus timers, Allowed Apps status, silent boosts, and daily group leaderboards across real devices.
3. **Real AI Engine, YouTube & PDF Ingestion:** Connect Firebase AI Logic (Gemini) for live Grok-style AI Tutor reasoning, dynamic HTML/SVG/interactive-simulation generation into `PrivateDiagramStore`, live zero-context Daily Planner Appeals & Task AI Intervention, Data-First Settings RAG optimization, and real Vault YouTube/PDF extraction.
4. **Durable Local & Cloud Persistence:** Persist editable Batches, Vault notes, Study Timer session & lecture logs, unoptimized daily study history, Quiz attempts, Settings preferences, and RAG files via Room + Firestore sync.
5. **Android Home Launcher Mode (`CATEGORY_HOME`):** Optional distraction-free study launcher posture allowing GHICKIN to act as a dedicated study device home screen.
