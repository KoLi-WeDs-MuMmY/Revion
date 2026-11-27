package com.example.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Immutable
data class GhickinExtendedColors(
    val amoledBackground: Color,
    val obsidianSurface: Color,
    val carbonSurface: Color,
    val slateElevated: Color,
    val trackSubtle: Color,
    val hairlineRest: Color,
    val hairlineSubtle: Color,
    val hairlineSpecularTop: Color,
    val accentHairline: Color,
    val accentSurfaceWash: Color,
    val primaryCta: Color,
    val onPrimaryCta: Color,
    val inkPrimary: Color,
    val inkSecondary: Color,
    val inkMuted: Color,
    val activeMood: AccentMood,
)

val LocalGhickinColors = staticCompositionLocalOf {
    GhickinExtendedColors(
        amoledBackground = AmoledBlack,
        obsidianSurface = ObsidianSurface,
        carbonSurface = CarbonSurface,
        slateElevated = SlateElevated,
        trackSubtle = TrackSubtle,
        hairlineRest = HairlineRest,
        hairlineSubtle = HairlineSubtle,
        hairlineSpecularTop = HairlineSpecularTop,
        accentHairline = HairlineSpecularTop,
        accentSurfaceWash = Color(0x06FFFFFF),
        primaryCta = GrokWhite,
        onPrimaryCta = GrokInkOnWhite,
        inkPrimary = InkPrimary,
        inkSecondary = InkSecondary,
        inkMuted = InkMuted,
        activeMood = AccentMood.CYAN,
    )
}

val GhickinShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(26.dp),
)

@Composable
fun GhickinTheme(
    accentMood: AccentMood = AccentMood.CYAN,
    content: @Composable () -> Unit,
) {
    val colorSpring = spring<Color>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMediumLow,
    )

    // Grok Minimal: pure monochrome white primary CTAs on true #000000 black,
    // keeping color strictly restrained to subtle mineral dots & semantic cues.
    val subtleAccent by animateColorAsState(accentMood.accent, colorSpring, label = "subtleAccent")

    val colorScheme = darkColorScheme(
        primary = GrokWhite,
        onPrimary = GrokInkOnWhite,
        primaryContainer = SlateElevated,
        onPrimaryContainer = InkPrimary,
        secondary = subtleAccent,
        onSecondary = InkOnAccentDark,
        secondaryContainer = CarbonSurface,
        onSecondaryContainer = InkPrimary,
        tertiary = AccentMood.EMERALD.accent,
        onTertiary = InkOnAccentDark,
        tertiaryContainer = AccentMood.EMERALD.container,
        onTertiaryContainer = InkPrimary,
        background = AmoledBlack,
        onBackground = InkPrimary,
        surface = ObsidianSurface,
        onSurface = InkPrimary,
        surfaceVariant = CarbonSurface,
        onSurfaceVariant = InkSecondary,
        surfaceContainerLowest = AmoledBlack,
        surfaceContainerLow = ObsidianSurface,
        surfaceContainer = CarbonSurface,
        surfaceContainerHigh = SlateElevated,
        surfaceContainerHighest = TrackSubtle,
        outline = HairlineRest,
        outlineVariant = HairlineSubtle,
    )

    val extendedColors = GhickinExtendedColors(
        amoledBackground = AmoledBlack,
        obsidianSurface = ObsidianSurface,
        carbonSurface = CarbonSurface,
        slateElevated = SlateElevated,
        trackSubtle = TrackSubtle,
        hairlineRest = HairlineRest,
        hairlineSubtle = HairlineSubtle,
        hairlineSpecularTop = HairlineSpecularTop,
        accentHairline = HairlineSpecularTop,
        accentSurfaceWash = Color(0x05FFFFFF),
        primaryCta = GrokWhite,
        onPrimaryCta = GrokInkOnWhite,
        inkPrimary = InkPrimary,
        inkSecondary = InkSecondary,
        inkMuted = InkMuted,
        activeMood = accentMood,
    )

    CompositionLocalProvider(LocalGhickinColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = GhickinTypography,
            shapes = GhickinShapes,
            content = content,
        )
    }
}

object GhickinTokens {
    val colors: GhickinExtendedColors
        @Composable get() = LocalGhickinColors.current
}
