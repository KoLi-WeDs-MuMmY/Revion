package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Pure AMOLED Canvas & Matte Obsidian Surfaces (Architectural Studio — Zero Neon)
val AmoledBlack = Color(0xFF000000)
val ObsidianSurface = Color(0xFF0B0D12)
val CarbonSurface = Color(0xFF121620)
val SlateElevated = Color(0xFF181D2A)
val TrackSubtle = Color(0xFF1C2230)

// Typographic Ink Hierarchy
val InkPrimary = Color(0xFFF4F6FA)
val InkSecondary = Color(0xFF98A1B3)
val InkMuted = Color(0xFF646E82)
val InkOnAccentDark = Color(0xFF06080C)

// Crisp Hairline Structural Strokes
val HairlineRest = Color(0x14FFFFFF) // ~8% white anodized edge
val HairlineSubtle = Color(0x0DFFFFFF) // ~5% divider
val HairlineSpecularTop = Color(0x22FFFFFF) // subtle top light catch

// Semantic Quiz Feedback Tokens (Restrained Editorial Contrast — Zero Neon)
val FeedbackCorrect = Color(0xFF34D399)
val FeedbackCorrectContainer = Color(0xFF0B2820)
val FeedbackIncorrect = Color(0xFFF87171)
val FeedbackIncorrectContainer = Color(0xFF2B1216)

// Refined Curated Multi-Color Dynamic Accents (Mineral / Editorial Tones — Non-Neon)
enum class AccentMood(
    val id: String,
    val label: String,
    val accent: Color,
    val onAccent: Color,
    val container: Color,
    val onContainer: Color,
    val hairlineTint: Color,
    val surfaceWash: Color,
) {
    CYAN(
        id = "cyan",
        label = "Cyan",
        accent = Color(0xFF38BDF8),
        onAccent = InkOnAccentDark,
        container = Color(0xFF0D2534),
        onContainer = Color(0xFFC8ECFD),
        hairlineTint = Color(0x4738BDF8), // ~28% crisp edge tint
        surfaceWash = Color(0x1238BDF8),  // ~7% quiet tonal wash
    ),
    VIOLET(
        id = "violet",
        label = "Violet",
        accent = Color(0xFFA78BFA),
        onAccent = InkOnAccentDark,
        container = Color(0xFF1E1733),
        onContainer = Color(0xFFE4DBFE),
        hairlineTint = Color(0x47A78BFA),
        surfaceWash = Color(0x12A78BFA),
    ),
    EMERALD(
        id = "emerald",
        label = "Emerald",
        accent = Color(0xFF34D399),
        onAccent = InkOnAccentDark,
        container = Color(0xFF0B2820),
        onContainer = Color(0xFFC4F5E1),
        hairlineTint = Color(0x4734D399),
        surfaceWash = Color(0x1234D399),
    ),
    AMBER(
        id = "amber",
        label = "Amber",
        accent = Color(0xFFFBBF24),
        onAccent = InkOnAccentDark,
        container = Color(0xFF2B200A),
        onContainer = Color(0xFFFDE9B4),
        hairlineTint = Color(0x47FBBF24),
        surfaceWash = Color(0x12FBBF24),
    );

    fun next(): AccentMood {
        val values = entries
        return values[(ordinal + 1) % values.size]
    }
}
