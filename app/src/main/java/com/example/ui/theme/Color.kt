package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Pure True-Black OLED Canvas & Grok/Claude Neutral Carbon Surfaces (Zero Neon)
val AmoledBlack = Color(0xFF000000)
val ObsidianSurface = Color(0xFF0A0A0C)
val CarbonSurface = Color(0xFF121215)
val SlateElevated = Color(0xFF1A1A1F)
val TrackSubtle = Color(0xFF24242A)

// Grok Monochrome Pure White Primary Action Tokens
val GrokWhite = Color(0xFFFFFFFF)
val GrokInkOnWhite = Color(0xFF000000)

// Warm Editorial Chalk Ink Hierarchy (Claude-inspired legibility)
val InkPrimary = Color(0xFFF5F5F7)
val InkSecondary = Color(0xFFA1A1AA)
val InkMuted = Color(0xFF6E6E77)
val InkOnAccentDark = Color(0xFF050507)

// Whisper-Thin Neutral Structural Hairlines
val HairlineRest = Color(0x18FFFFFF)        // ~9% crisp anodized hairline
val HairlineSubtle = Color(0x0EFFFFFF)      // ~5.5% quiet inner divider
val HairlineSpecularTop = Color(0x2AFFFFFF) // subtle edge catch on active cards

// Restrained Semantic Feedback Tokens
val FeedbackCorrect = Color(0xFF4ADE80)
val FeedbackCorrectContainer = Color(0xFF0D1F16)
val FeedbackIncorrect = Color(0xFFF87171)
val FeedbackIncorrectContainer = Color(0xFF241114)

// Subtle Muted Mineral Dot Accents (Strictly for 6dp subject dots & quiet progress bars — Zero Neon)
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
        label = "Slate",
        accent = Color(0xFF7DD3FC),
        onAccent = InkOnAccentDark,
        container = Color(0xFF14181E),
        onContainer = Color(0xFFE4E4E7),
        hairlineTint = Color(0x26FFFFFF),
        surfaceWash = Color(0x06FFFFFF),
    ),
    VIOLET(
        id = "violet",
        label = "Iris",
        accent = Color(0xFFC4B5FD),
        onAccent = InkOnAccentDark,
        container = Color(0xFF17151F),
        onContainer = Color(0xFFE4E4E7),
        hairlineTint = Color(0x26FFFFFF),
        surfaceWash = Color(0x06FFFFFF),
    ),
    EMERALD(
        id = "emerald",
        label = "Sage",
        accent = Color(0xFF6EE7B7),
        onAccent = InkOnAccentDark,
        container = Color(0xFF121C18),
        onContainer = Color(0xFFE4E4E7),
        hairlineTint = Color(0x26FFFFFF),
        surfaceWash = Color(0x06FFFFFF),
    ),
    AMBER(
        id = "amber",
        label = "Sand",
        accent = Color(0xFFFCD34D),
        onAccent = InkOnAccentDark,
        container = Color(0xFF1D1A12),
        onContainer = Color(0xFFE4E4E7),
        hairlineTint = Color(0x26FFFFFF),
        surfaceWash = Color(0x06FFFFFF),
    );

    fun next(): AccentMood {
        val values = entries
        return values[(ordinal + 1) % values.size]
    }
}
