package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.res.booleanResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.unit.Dp
import com.example.R

/**
 * Orientation-qualified layout metrics sourced directly from separate
 * 9:16 portrait (res/values/dimens_screen.xml) and
 * 16:9 landscape (res/values-land/dimens_screen.xml) XML files.
 */
@Immutable
data class ScreenLayoutMetrics(
    val horizontalPadding: Dp,
    val verticalPadding: Dp,
    val sectionSpacing: Dp,
    val cardPadding: Dp,
    val cardCornerRadius: Dp,
    val dailyCardHeight: Dp,
    val heroBannerHeight: Dp,
    val navRailWidth: Dp,
    val maxContentWidth: Dp,
    val vaultSubjectColumns: Int,
    val vaultChapterColumns: Int,
    val isLandscape16By9: Boolean,
)

@Composable
fun rememberScreenLayoutMetrics(): ScreenLayoutMetrics {
    return ScreenLayoutMetrics(
        horizontalPadding = dimensionResource(R.dimen.screen_horizontal_padding),
        verticalPadding = dimensionResource(R.dimen.screen_vertical_padding),
        sectionSpacing = dimensionResource(R.dimen.section_spacing),
        cardPadding = dimensionResource(R.dimen.card_padding),
        cardCornerRadius = dimensionResource(R.dimen.card_corner_radius),
        dailyCardHeight = dimensionResource(R.dimen.daily_card_height),
        heroBannerHeight = dimensionResource(R.dimen.hero_banner_height),
        navRailWidth = dimensionResource(R.dimen.nav_rail_width),
        maxContentWidth = dimensionResource(R.dimen.max_content_width),
        vaultSubjectColumns = integerResource(R.integer.vault_subject_columns),
        vaultChapterColumns = integerResource(R.integer.vault_chapter_columns),
        isLandscape16By9 = booleanResource(R.bool.is_landscape_16_9),
    )
}
