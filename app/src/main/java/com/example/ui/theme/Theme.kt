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
        accentHairline = AccentMood.CYAN.hairlineTint,
        accentSurfaceWash = AccentMood.CYAN.surfaceWash,
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
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp),
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

    val primary by animateColorAsState(accentMood.accent, colorSpring, label = "primary")
    val onPrimary by animateColorAsState(accentMood.onAccent, colorSpring, label = "onPrimary")
    val primaryContainer by animateColorAsState(accentMood.container, colorSpring, label = "primaryContainer")
    val onPrimaryContainer by animateColorAsState(accentMood.onContainer, colorSpring, label = "onPrimaryContainer")
    val accentHairline by animateColorAsState(accentMood.hairlineTint, colorSpring, label = "accentHairline")
    val accentSurfaceWash by animateColorAsState(accentMood.surfaceWash, colorSpring, label = "accentSurfaceWash")

    val secondaryMood = accentMood.next()
    val secondary by animateColorAsState(secondaryMood.accent, colorSpring, label = "secondary")
    val secondaryContainer by animateColorAsState(secondaryMood.container, colorSpring, label = "secondaryContainer")

    val colorScheme = darkColorScheme(
        primary = primary,
        onPrimary = onPrimary,
        primaryContainer = primaryContainer,
        onPrimaryContainer = onPrimaryContainer,
        secondary = secondary,
        onSecondary = InkOnAccentDark,
        secondaryContainer = secondaryContainer,
        onSecondaryContainer = secondaryMood.onContainer,
        tertiary = AccentMood.EMERALD.accent,
        onTertiary = InkOnAccentDark,
        tertiaryContainer = AccentMood.EMERALD.container,
        onTertiaryContainer = AccentMood.EMERALD.onContainer,
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
        accentHairline = accentHairline,
        accentSurfaceWash = accentSurfaceWash,
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
