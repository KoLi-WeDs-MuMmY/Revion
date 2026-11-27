package com.example.ui.theme

import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.example.R

// Default Grok True-Black OLED Fallback Tokens
val AmoledBlack = Color(0xFF000000)
val ObsidianSurface = Color(0xFF0A0A0C)
val CarbonSurface = Color(0xFF121215)
val SlateElevated = Color(0xFF1B1B20)
val TrackSubtle = Color(0xFF24242A)

// Grok Monochrome Pure White Primary Action Tokens
val GrokWhite = Color(0xFFFFFFFF)
val GrokInkOnWhite = Color(0xFF000000)

// Ink Hierarchy Fallbacks
val InkPrimary = Color(0xFFF5F5F7)
val InkSecondary = Color(0xFFA1A1AA)
val InkMuted = Color(0xFF71717A)
val InkOnAccentDark = Color(0xFF050507)

// Whisper-Thin Neutral Structural Hairlines
val HairlineRest = Color(0x1CFFFFFF)
val HairlineSubtle = Color(0x10FFFFFF)
val HairlineSpecularTop = Color(0x2EFFFFFF)

// Restrained Semantic Feedback Tokens
val FeedbackCorrect = Color(0xFF4ADE80)
val FeedbackCorrectLight = Color(0xFF15803D)
val FeedbackCorrectContainer = Color(0xFF0D1F16)
val FeedbackIncorrect = Color(0xFFF87171)
val FeedbackIncorrectContainer = Color(0xFF241114)

/**
 * Modular Workspace Themes — each theme binds to its own standalone XML color
 * resource file under res/values/theme_*.xml, paired with a curated font family,
 * theme-matched daily card artwork, and dedicated AI Tutor chat surface tokens.
 */
enum class AppThemeId(
    val id: String,
    val label: String,
    val tagline: String,
    val fontPairingLabel: String,
    val xmlFileLabel: String,
    val isLight: Boolean,
    @DrawableRes val revisionArtworkRes: Int,
    @DrawableRes val flashcardsArtworkRes: Int,
    @ColorRes val bgRes: Int,
    @ColorRes val surfaceRes: Int,
    @ColorRes val surfaceCardRes: Int,
    @ColorRes val surfaceElevatedRes: Int,
    @ColorRes val trackRes: Int,
    @ColorRes val inkPrimaryRes: Int,
    @ColorRes val inkSecondaryRes: Int,
    @ColorRes val inkMutedRes: Int,
    @ColorRes val ctaRes: Int,
    @ColorRes val onCtaRes: Int,
    @ColorRes val accentRes: Int,
    @ColorRes val hairlineRes: Int,
    @ColorRes val hairlineSubtleRes: Int,
    @ColorRes val hairlineSpecularRes: Int,
    @ColorRes val errorRes: Int,
    @ColorRes val chatUserBubbleRes: Int,
    @ColorRes val chatAssistantSurfaceRes: Int,
    @ColorRes val chatSidebarBgRes: Int,
    @ColorRes val chatComposerBgRes: Int,
    @ColorRes val chatCodeBgRes: Int,
    @ColorRes val chatLogoPrimaryRes: Int,
    @ColorRes val chatLogoSecondaryRes: Int,
    @DimenRes val chatComposerRadiusRes: Int,
    @DimenRes val chatBubbleRadiusRes: Int,
) {
    GROK_BLACK(
        id = "grok_black",
        label = "Grok Black",
        tagline = "True #000000 OLED monochrome & crisp white CTAs",
        fontPairingLabel = "Space Grotesk · Inter",
        xmlFileLabel = "theme_grok_black.xml",
        isLight = false,
        revisionArtworkRes = R.drawable.img_today_revision_art,
        flashcardsArtworkRes = R.drawable.img_today_flashcards_art,
        bgRes = R.color.theme_grok_bg,
        surfaceRes = R.color.theme_grok_surface,
        surfaceCardRes = R.color.theme_grok_surface_card,
        surfaceElevatedRes = R.color.theme_grok_surface_elevated,
        trackRes = R.color.theme_grok_track,
        inkPrimaryRes = R.color.theme_grok_ink_primary,
        inkSecondaryRes = R.color.theme_grok_ink_secondary,
        inkMutedRes = R.color.theme_grok_ink_muted,
        ctaRes = R.color.theme_grok_cta,
        onCtaRes = R.color.theme_grok_on_cta,
        accentRes = R.color.theme_grok_accent,
        hairlineRes = R.color.theme_grok_hairline,
        hairlineSubtleRes = R.color.theme_grok_hairline_subtle,
        hairlineSpecularRes = R.color.theme_grok_hairline_specular,
        errorRes = R.color.theme_grok_error,
        chatUserBubbleRes = R.color.theme_grok_chat_user_bubble,
        chatAssistantSurfaceRes = R.color.theme_grok_chat_assistant_surface,
        chatSidebarBgRes = R.color.theme_grok_chat_sidebar_bg,
        chatComposerBgRes = R.color.theme_grok_chat_composer_bg,
        chatCodeBgRes = R.color.theme_grok_chat_code_bg,
        chatLogoPrimaryRes = R.color.theme_grok_chat_logo_primary,
        chatLogoSecondaryRes = R.color.theme_grok_chat_logo_secondary,
        chatComposerRadiusRes = R.dimen.theme_grok_chat_composer_radius,
        chatBubbleRadiusRes = R.dimen.theme_grok_chat_bubble_radius,
    ),
    CLAUDE_EDITORIAL(
        id = "claude_editorial",
        label = "Claude Editorial",
        tagline = "Warm charcoal studio, terracotta accents & cream ink",
        fontPairingLabel = "Lora Serif · Plus Jakarta Sans",
        xmlFileLabel = "theme_claude_editorial.xml",
        isLight = false,
        revisionArtworkRes = R.drawable.img_revision_claude,
        flashcardsArtworkRes = R.drawable.img_flashcards_claude,
        bgRes = R.color.theme_claude_bg,
        surfaceRes = R.color.theme_claude_surface,
        surfaceCardRes = R.color.theme_claude_surface_card,
        surfaceElevatedRes = R.color.theme_claude_surface_elevated,
        trackRes = R.color.theme_claude_track,
        inkPrimaryRes = R.color.theme_claude_ink_primary,
        inkSecondaryRes = R.color.theme_claude_ink_secondary,
        inkMutedRes = R.color.theme_claude_ink_muted,
        ctaRes = R.color.theme_claude_cta,
        onCtaRes = R.color.theme_claude_on_cta,
        accentRes = R.color.theme_claude_accent,
        hairlineRes = R.color.theme_claude_hairline,
        hairlineSubtleRes = R.color.theme_claude_hairline_subtle,
        hairlineSpecularRes = R.color.theme_claude_hairline_specular,
        errorRes = R.color.theme_claude_error,
        chatUserBubbleRes = R.color.theme_claude_chat_user_bubble,
        chatAssistantSurfaceRes = R.color.theme_claude_chat_assistant_surface,
        chatSidebarBgRes = R.color.theme_claude_chat_sidebar_bg,
        chatComposerBgRes = R.color.theme_claude_chat_composer_bg,
        chatCodeBgRes = R.color.theme_claude_chat_code_bg,
        chatLogoPrimaryRes = R.color.theme_claude_chat_logo_primary,
        chatLogoSecondaryRes = R.color.theme_claude_chat_logo_secondary,
        chatComposerRadiusRes = R.dimen.theme_claude_chat_composer_radius,
        chatBubbleRadiusRes = R.dimen.theme_claude_chat_bubble_radius,
    ),
    PAPER_LIGHT(
        id = "paper_light",
        label = "Paper Light",
        tagline = "Sunlit warm alabaster, pure white cards & ink typography",
        fontPairingLabel = "Lora Serif · Plus Jakarta Sans",
        xmlFileLabel = "theme_paper_light.xml",
        isLight = true,
        revisionArtworkRes = R.drawable.img_revision_paper,
        flashcardsArtworkRes = R.drawable.img_flashcards_paper,
        bgRes = R.color.theme_paper_bg,
        surfaceRes = R.color.theme_paper_surface,
        surfaceCardRes = R.color.theme_paper_surface_card,
        surfaceElevatedRes = R.color.theme_paper_surface_elevated,
        trackRes = R.color.theme_paper_track,
        inkPrimaryRes = R.color.theme_paper_ink_primary,
        inkSecondaryRes = R.color.theme_paper_ink_secondary,
        inkMutedRes = R.color.theme_paper_ink_muted,
        ctaRes = R.color.theme_paper_cta,
        onCtaRes = R.color.theme_paper_on_cta,
        accentRes = R.color.theme_paper_accent,
        hairlineRes = R.color.theme_paper_hairline,
        hairlineSubtleRes = R.color.theme_paper_hairline_subtle,
        hairlineSpecularRes = R.color.theme_paper_hairline_specular,
        errorRes = R.color.theme_paper_error,
        chatUserBubbleRes = R.color.theme_paper_chat_user_bubble,
        chatAssistantSurfaceRes = R.color.theme_paper_chat_assistant_surface,
        chatSidebarBgRes = R.color.theme_paper_chat_sidebar_bg,
        chatComposerBgRes = R.color.theme_paper_chat_composer_bg,
        chatCodeBgRes = R.color.theme_paper_chat_code_bg,
        chatLogoPrimaryRes = R.color.theme_paper_chat_logo_primary,
        chatLogoSecondaryRes = R.color.theme_paper_chat_logo_secondary,
        chatComposerRadiusRes = R.dimen.theme_paper_chat_composer_radius,
        chatBubbleRadiusRes = R.dimen.theme_paper_chat_bubble_radius,
    ),
    SLATE(
        id = "slate",
        label = "Slate",
        tagline = "Deep graphite surfaces with restrained slate-blue focus",
        fontPairingLabel = "Outfit · Inter",
        xmlFileLabel = "theme_slate.xml",
        isLight = false,
        revisionArtworkRes = R.drawable.img_today_revision_art,
        flashcardsArtworkRes = R.drawable.img_today_flashcards_art,
        bgRes = R.color.theme_slate_bg,
        surfaceRes = R.color.theme_slate_surface,
        surfaceCardRes = R.color.theme_slate_surface_card,
        surfaceElevatedRes = R.color.theme_slate_surface_elevated,
        trackRes = R.color.theme_slate_track,
        inkPrimaryRes = R.color.theme_slate_ink_primary,
        inkSecondaryRes = R.color.theme_slate_ink_secondary,
        inkMutedRes = R.color.theme_slate_ink_muted,
        ctaRes = R.color.theme_slate_cta,
        onCtaRes = R.color.theme_slate_on_cta,
        accentRes = R.color.theme_slate_accent,
        hairlineRes = R.color.theme_slate_hairline,
        hairlineSubtleRes = R.color.theme_slate_hairline_subtle,
        hairlineSpecularRes = R.color.theme_slate_hairline_specular,
        errorRes = R.color.theme_slate_error,
        chatUserBubbleRes = R.color.theme_slate_chat_user_bubble,
        chatAssistantSurfaceRes = R.color.theme_slate_chat_assistant_surface,
        chatSidebarBgRes = R.color.theme_slate_chat_sidebar_bg,
        chatComposerBgRes = R.color.theme_slate_chat_composer_bg,
        chatCodeBgRes = R.color.theme_slate_chat_code_bg,
        chatLogoPrimaryRes = R.color.theme_slate_chat_logo_primary,
        chatLogoSecondaryRes = R.color.theme_slate_chat_logo_secondary,
        chatComposerRadiusRes = R.dimen.theme_slate_chat_composer_radius,
        chatBubbleRadiusRes = R.dimen.theme_slate_chat_bubble_radius,
    ),
}

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
