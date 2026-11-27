package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

// Declare single Normal base font per static TTF so Compose properly synthesizes
// crisp Bold & SemiBold weights rather than flattening Bold back to Regular.
val SpaceGroteskFamily = FontFamily(
    Font(R.font.space_grotesk, FontWeight.Normal),
)

val InterFamily = FontFamily(
    Font(R.font.inter, FontWeight.Normal),
)

val JetBrainsMonoFamily = FontFamily(
    Font(R.font.jetbrains_mono, FontWeight.Normal),
)

val LoraFamily = FontFamily(
    Font(R.font.lora, FontWeight.Normal),
)

val PlusJakartaSansFamily = FontFamily(
    Font(R.font.plus_jakarta_sans, FontWeight.Normal),
)

val OutfitFamily = FontFamily(
    Font(R.font.outfit, FontWeight.Normal),
)

fun displayFontForTheme(theme: AppThemeId): FontFamily = when (theme) {
    AppThemeId.GROK_BLACK -> SpaceGroteskFamily
    AppThemeId.CLAUDE_EDITORIAL -> LoraFamily
    AppThemeId.PAPER_LIGHT -> LoraFamily
    AppThemeId.SLATE -> OutfitFamily
}

fun bodyFontForTheme(theme: AppThemeId): FontFamily = when (theme) {
    AppThemeId.GROK_BLACK -> InterFamily
    AppThemeId.CLAUDE_EDITORIAL -> PlusJakartaSansFamily
    AppThemeId.PAPER_LIGHT -> PlusJakartaSansFamily
    AppThemeId.SLATE -> InterFamily
}

fun typographyForTheme(theme: AppThemeId): Typography {
    val display = displayFontForTheme(theme)
    val body = bodyFontForTheme(theme)
    val isSerif = theme == AppThemeId.CLAUDE_EDITORIAL || theme == AppThemeId.PAPER_LIGHT

    return Typography(
        displayLarge = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.Bold,
            fontSize = 46.sp,
            lineHeight = 50.sp,
            letterSpacing = if (isSerif) (-0.8).sp else (-1.4).sp,
            fontFeatureSettings = "tnum",
        ),
        displayMedium = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.Bold,
            fontSize = 34.sp,
            lineHeight = 38.sp,
            letterSpacing = if (isSerif) (-0.5).sp else (-0.9).sp,
            fontFeatureSettings = "tnum",
        ),
        displaySmall = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            lineHeight = 34.sp,
            letterSpacing = if (isSerif) (-0.3).sp else (-0.6).sp,
        ),
        headlineMedium = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.Bold,
            fontSize = 27.sp,
            lineHeight = 33.sp,
            letterSpacing = if (isSerif) (-0.25).sp else (-0.5).sp,
        ),
        headlineSmall = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.Bold,
            fontSize = 21.sp,
            lineHeight = 27.sp,
            letterSpacing = if (isSerif) (-0.15).sp else (-0.35).sp,
        ),
        titleLarge = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.Bold,
            fontSize = 19.sp,
            lineHeight = 25.sp,
            letterSpacing = (-0.2).sp,
        ),
        titleMedium = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            letterSpacing = (-0.1).sp,
        ),
        titleSmall = TextStyle(
            fontFamily = body,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            lineHeight = 19.sp,
            letterSpacing = 0.05.sp,
        ),
        bodyLarge = TextStyle(
            fontFamily = body,
            fontWeight = FontWeight.Normal,
            fontSize = 15.5.sp,
            lineHeight = 23.sp,
            letterSpacing = 0.sp,
        ),
        bodyMedium = TextStyle(
            fontFamily = body,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.5.sp,
            letterSpacing = 0.1.sp,
        ),
        bodySmall = TextStyle(
            fontFamily = body,
            fontWeight = FontWeight.Normal,
            fontSize = 12.5.sp,
            lineHeight = 18.sp,
            letterSpacing = 0.12.sp,
        ),
        labelLarge = TextStyle(
            fontFamily = body,
            fontWeight = FontWeight.Bold,
            fontSize = 13.5.sp,
            lineHeight = 18.sp,
            letterSpacing = 0.15.sp,
        ),
        labelMedium = TextStyle(
            fontFamily = body,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.25.sp,
        ),
        labelSmall = TextStyle(
            fontFamily = JetBrainsMonoFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 10.5.sp,
            lineHeight = 14.sp,
            letterSpacing = 0.45.sp,
            fontFeatureSettings = "tnum",
        ),
    )
}

val KineticDisplayXL = TextStyle(
    fontFamily = SpaceGroteskFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 46.sp,
    lineHeight = 50.sp,
    letterSpacing = (-1.4).sp,
    fontFeatureSettings = "tnum",
)

val KineticDisplayLarge = TextStyle(
    fontFamily = SpaceGroteskFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 34.sp,
    lineHeight = 38.sp,
    letterSpacing = (-0.9).sp,
    fontFeatureSettings = "tnum",
)

val FlashcardPromptDisplay = TextStyle(
    fontFamily = SpaceGroteskFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 23.sp,
    lineHeight = 31.sp,
    letterSpacing = (-0.35).sp,
)

val HumanistReadingBody = TextStyle(
    fontFamily = InterFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 15.sp,
    lineHeight = 23.sp,
    letterSpacing = 0.05.sp,
)

val FormulaMonoDisplay = TextStyle(
    fontFamily = JetBrainsMonoFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 14.5.sp,
    lineHeight = 21.sp,
    letterSpacing = (-0.1).sp,
    fontFeatureSettings = "tnum",
)

val MicroTagMono = TextStyle(
    fontFamily = JetBrainsMonoFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 10.5.sp,
    lineHeight = 13.5.sp,
    letterSpacing = 0.85.sp,
    fontFeatureSettings = "tnum",
)

val MonoMetricLarge = TextStyle(
    fontFamily = JetBrainsMonoFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 24.sp,
    lineHeight = 30.sp,
    letterSpacing = (-0.5).sp,
    fontFeatureSettings = "tnum",
)

val MonoMetricMedium = TextStyle(
    fontFamily = JetBrainsMonoFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 15.sp,
    lineHeight = 19.sp,
    letterSpacing = 0.sp,
    fontFeatureSettings = "tnum",
)

val MonoBadgeStyle = TextStyle(
    fontFamily = JetBrainsMonoFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 11.sp,
    lineHeight = 14.sp,
    letterSpacing = 0.6.sp,
    fontFeatureSettings = "tnum",
)

val GhickinTypography = typographyForTheme(AppThemeId.GROK_BLACK)
