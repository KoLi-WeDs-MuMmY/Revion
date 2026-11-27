package com.example.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
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
    val themeAccent: Color,
    val semanticSuccess: Color,
    val inkPrimary: Color,
    val inkSecondary: Color,
    val inkMuted: Color,
    val activeMood: AccentMood,
    val activeTheme: AppThemeId,
    val isLightTheme: Boolean,
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
        themeAccent = GrokWhite,
        semanticSuccess = FeedbackCorrect,
        inkPrimary = InkPrimary,
        inkSecondary = InkSecondary,
        inkMuted = InkMuted,
        activeMood = AccentMood.CYAN,
        activeTheme = AppThemeId.GROK_BLACK,
        isLightTheme = false,
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
    appTheme: AppThemeId = AppThemeId.GROK_BLACK,
    accentMood: AccentMood = AccentMood.CYAN,
    content: @Composable () -> Unit,
) {
    val colorSpring = spring<Color>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMediumLow,
    )

    // Load active theme tokens directly from its standalone modular XML file
    val rawBg = colorResource(appTheme.bgRes)
    val rawSurface = colorResource(appTheme.surfaceRes)
    val rawCard = colorResource(appTheme.surfaceCardRes)
    val rawElevated = colorResource(appTheme.surfaceElevatedRes)
    val rawTrack = colorResource(appTheme.trackRes)
    val rawInkPrimary = colorResource(appTheme.inkPrimaryRes)
    val rawInkSecondary = colorResource(appTheme.inkSecondaryRes)
    val rawInkMuted = colorResource(appTheme.inkMutedRes)
    val rawCta = colorResource(appTheme.ctaRes)
    val rawOnCta = colorResource(appTheme.onCtaRes)
    val rawThemeAccent = colorResource(appTheme.accentRes)
    val rawHairline = colorResource(appTheme.hairlineRes)
    val rawHairlineSubtle = colorResource(appTheme.hairlineSubtleRes)
    val rawHairlineSpecular = colorResource(appTheme.hairlineSpecularRes)
    val rawError = colorResource(appTheme.errorRes)
    val rawSuccess = if (appTheme.isLight) FeedbackCorrectLight else AccentMood.EMERALD.accent

    val bg by animateColorAsState(rawBg, colorSpring, label = "themeBg")
    val surface by animateColorAsState(rawSurface, colorSpring, label = "themeSurface")
    val card by animateColorAsState(rawCard, colorSpring, label = "themeCard")
    val elevated by animateColorAsState(rawElevated, colorSpring, label = "themeElevated")
    val track by animateColorAsState(rawTrack, colorSpring, label = "themeTrack")
    val inkPrimary by animateColorAsState(rawInkPrimary, colorSpring, label = "themeInkPrimary")
    val inkSecondary by animateColorAsState(rawInkSecondary, colorSpring, label = "themeInkSecondary")
    val inkMuted by animateColorAsState(rawInkMuted, colorSpring, label = "themeInkMuted")
    val primaryCta by animateColorAsState(rawCta, colorSpring, label = "themeCta")
    val onPrimaryCta by animateColorAsState(rawOnCta, colorSpring, label = "themeOnCta")
    val themeAccent by animateColorAsState(rawThemeAccent, colorSpring, label = "themeAccent")
    val hairline by animateColorAsState(rawHairline, colorSpring, label = "themeHairline")
    val hairlineSubtle by animateColorAsState(rawHairlineSubtle, colorSpring, label = "themeHairlineSubtle")
    val hairlineSpecular by animateColorAsState(rawHairlineSpecular, colorSpring, label = "themeHairlineSpecular")
    val errorColor by animateColorAsState(rawError, colorSpring, label = "themeError")
    val semanticSuccess by animateColorAsState(rawSuccess, colorSpring, label = "themeSuccess")
    val subtleAccent by animateColorAsState(accentMood.accent, colorSpring, label = "subtleAccent")

    val colorScheme = if (appTheme.isLight) {
        lightColorScheme(
            primary = themeAccent,
            onPrimary = Color.White,
            primaryContainer = elevated,
            onPrimaryContainer = inkPrimary,
            secondary = subtleAccent,
            onSecondary = InkOnAccentDark,
            secondaryContainer = card,
            onSecondaryContainer = inkPrimary,
            tertiary = semanticSuccess,
            onTertiary = Color.White,
            tertiaryContainer = card,
            onTertiaryContainer = inkPrimary,
            background = bg,
            onBackground = inkPrimary,
            surface = surface,
            onSurface = inkPrimary,
            surfaceVariant = card,
            onSurfaceVariant = inkSecondary,
            surfaceContainerLowest = bg,
            surfaceContainerLow = surface,
            surfaceContainer = card,
            surfaceContainerHigh = elevated,
            surfaceContainerHighest = track,
            outline = hairline,
            outlineVariant = hairlineSubtle,
            error = errorColor,
            onError = Color.White,
        )
    } else {
        darkColorScheme(
            primary = themeAccent,
            onPrimary = onPrimaryCta,
            primaryContainer = elevated,
            onPrimaryContainer = inkPrimary,
            secondary = subtleAccent,
            onSecondary = InkOnAccentDark,
            secondaryContainer = card,
            onSecondaryContainer = inkPrimary,
            tertiary = semanticSuccess,
            onTertiary = InkOnAccentDark,
            tertiaryContainer = AccentMood.EMERALD.container,
            onTertiaryContainer = inkPrimary,
            background = bg,
            onBackground = inkPrimary,
            surface = surface,
            onSurface = inkPrimary,
            surfaceVariant = card,
            onSurfaceVariant = inkSecondary,
            surfaceContainerLowest = bg,
            surfaceContainerLow = surface,
            surfaceContainer = card,
            surfaceContainerHigh = elevated,
            surfaceContainerHighest = track,
            outline = hairline,
            outlineVariant = hairlineSubtle,
            error = errorColor,
            onError = InkOnAccentDark,
        )
    }

    val surfaceWash = if (appTheme.isLight) {
        themeAccent.copy(alpha = 0.05f)
    } else {
        themeAccent.copy(alpha = 0.045f)
    }

    val extendedColors = GhickinExtendedColors(
        amoledBackground = bg,
        obsidianSurface = surface,
        carbonSurface = card,
        slateElevated = elevated,
        trackSubtle = track,
        hairlineRest = hairline,
        hairlineSubtle = hairlineSubtle,
        hairlineSpecularTop = hairlineSpecular,
        accentHairline = hairlineSpecular,
        accentSurfaceWash = surfaceWash,
        primaryCta = primaryCta,
        onPrimaryCta = onPrimaryCta,
        themeAccent = themeAccent,
        semanticSuccess = semanticSuccess,
        inkPrimary = inkPrimary,
        inkSecondary = inkSecondary,
        inkMuted = inkMuted,
        activeMood = accentMood,
        activeTheme = appTheme,
        isLightTheme = appTheme.isLight,
    )

    val typography = remember(appTheme) { typographyForTheme(appTheme) }

    CompositionLocalProvider(LocalGhickinColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            shapes = GhickinShapes,
            content = content,
        )
    }
}

object GhickinTokens {
    val colors: GhickinExtendedColors
        @Composable get() = LocalGhickinColors.current
}
