package com.example.ui.webviewer

import android.annotation.SuppressLint
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.OpenInFull
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Splitscreen
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material.icons.rounded.ZoomOut
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.diagram.PrivateDiagramStore
import com.example.model.DiagramAttachment
import com.example.ui.components.tactileScale
import com.example.ui.theme.GhickinTokens
import com.example.ui.theme.MicroTagMono
import com.example.ui.theme.MonoBadgeStyle

private const val SYNTHETIC_DIAGRAM_ORIGIN = "https://diagrams.ghickin.local/"

private fun Color.toCssHex(): String {
    val argb = this.toArgb()
    val r = (argb shr 16) and 0xFF
    val g = (argb shr 8) and 0xFF
    val b = argb and 0xFF
    return String.format("#%02X%02X%02X", r, g, b)
}

/**
 * Gemini / Claude-inspired inline diagram artifact displayed directly inside
 * or at the end of an AI Tutor reply. Tapping opens the zoomable interactive
 * WebView modal or immediately docks it into Split Screen alongside chat.
 */
@Composable
fun InlineDiagramArtifactCard(
    attachment: DiagramAttachment,
    onOpenZoomModal: () -> Unit,
    onOpenSplitScreen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val cardInteraction = remember { MutableInteractionSource() }
    val splitInteraction = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(tokens.chatBubbleRadius)

    Surface(
        shape = shape,
        color = tokens.chatCodeBg,
        border = BorderStroke(1.dp, tokens.hairlineSpecularTop.copy(alpha = 0.65f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("inline_diagram_card_${attachment.fileName}")
            .tactileScale(cardInteraction, pressedScale = 0.985f)
            .clip(shape)
            .clickable(
                interactionSource = cardInteraction,
                indication = ripple(color = tokens.themeAccent.copy(alpha = 0.16f)),
                role = Role.Button,
                onClick = onOpenZoomModal,
            ),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // Top bar: Private Diagram tag + filename badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Code,
                        contentDescription = null,
                        tint = tokens.themeAccent,
                        modifier = Modifier.size(14.dp),
                    )
                    Text(
                        text = if (attachment.generatedOnTheFly) {
                            "GENERATED · SAVED PRIVATELY"
                        } else {
                            stringResource(R.string.webviewer_private_badge)
                        },
                        style = MicroTagMono,
                        color = tokens.themeAccent,
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = tokens.obsidianSurface,
                    border = BorderStroke(1.dp, tokens.hairlineSubtle),
                ) {
                    Text(
                        text = attachment.fileName,
                        style = MicroTagMono,
                        color = tokens.inkMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                    )
                }
            }

            // Crisp schematic live thumbnail banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(86.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(tokens.obsidianSurface)
                    .padding(10.dp),
            ) {
                val accent = tokens.themeAccent
                val success = tokens.semanticSuccess
                val gridColor = tokens.hairlineSubtle
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    // Subtle engineering grid
                    val cols = 6
                    for (i in 1 until cols) {
                        val x = w * i / cols
                        drawLine(gridColor, Offset(x, 0f), Offset(x, h), strokeWidth = 1f)
                    }
                    drawLine(gridColor, Offset(0f, h * 0.5f), Offset(w, h * 0.5f), strokeWidth = 1f)
                    // Primary curve
                    val curve = Path().apply {
                        moveTo(w * 0.06f, h * 0.78f)
                        lineTo(w * 0.36f, h * 0.78f)
                        lineTo(w * 0.38f, h * 0.20f)
                        cubicTo(w * 0.52f, h * 0.42f, w * 0.72f, h * 0.66f, w * 0.94f, h * 0.74f)
                    }
                    drawPath(
                        path = curve,
                        color = accent,
                        style = Stroke(width = 3.2f, cap = StrokeCap.Round),
                    )
                    // Secondary continuous potential curve
                    val potential = Path().apply {
                        moveTo(w * 0.06f, h * 0.34f)
                        lineTo(w * 0.38f, h * 0.34f)
                        cubicTo(w * 0.58f, h * 0.44f, w * 0.76f, h * 0.58f, w * 0.94f, h * 0.66f)
                    }
                    drawPath(
                        path = potential,
                        color = success,
                        style = Stroke(width = 2.4f, cap = StrokeCap.Round),
                    )
                    drawCircle(color = accent, radius = 5.5f, center = Offset(w * 0.38f, h * 0.20f))
                    drawCircle(color = success, radius = 5.5f, center = Offset(w * 0.38f, h * 0.34f))
                }

                Surface(
                    shape = RoundedCornerShape(7.dp),
                    color = tokens.amoledBackground.copy(alpha = 0.78f),
                    border = BorderStroke(1.dp, tokens.hairlineSubtle),
                    modifier = Modifier.align(Alignment.BottomEnd),
                ) {
                    Text(
                        text = "Interactive HTML/SVG · Tap to Zoom",
                        style = MicroTagMono,
                        color = tokens.inkSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = attachment.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = tokens.inkPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = attachment.summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = tokens.inkSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // Action strip: Open & Zoom + Split Screen
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = tokens.primaryCta,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("diagram_open_zoom_button")
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(role = Role.Button, onClick = onOpenZoomModal),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.OpenInFull,
                            contentDescription = stringResource(R.string.webviewer_open_zoom),
                            tint = tokens.onPrimaryCta,
                            modifier = Modifier.size(15.dp),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.webviewer_open_zoom),
                            style = MaterialTheme.typography.labelLarge,
                            color = tokens.onPrimaryCta,
                            maxLines = 1,
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = tokens.obsidianSurface,
                    border = BorderStroke(1.dp, tokens.hairlineRest),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("diagram_split_screen_button")
                        .minimumInteractiveComponentSize()
                        .tactileScale(splitInteraction, pressedScale = 0.96f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(
                            interactionSource = splitInteraction,
                            indication = ripple(color = tokens.themeAccent.copy(alpha = 0.16f)),
                            role = Role.Button,
                            onClick = onOpenSplitScreen,
                        ),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Splitscreen,
                            contentDescription = stringResource(R.string.webviewer_split_screen),
                            tint = tokens.inkPrimary,
                            modifier = Modifier.size(15.dp),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.webviewer_split_screen),
                            style = MaterialTheme.typography.labelLarge,
                            color = tokens.inkPrimary,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

/**
 * Interactive Android WebView pane rendering validated HTML/SVG/JS study diagrams from
 * app-private internal storage with live theme CSS injection, pinch + button zoom,
 * and strict local-origin sandboxing.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun InteractiveDiagramWebViewPane(
    attachment: DiagramAttachment,
    zoomPercent: Int,
    isSplitScreen: Boolean,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onResetZoom: () -> Unit,
    onToggleSplitScreen: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = GhickinTokens.colors
    val context = LocalContext.current
    val diagramStore = remember(context) { PrivateDiagramStore(context.applicationContext) }

    val bgHex = tokens.amoledBackground.toCssHex()
    val surfaceHex = tokens.obsidianSurface.toCssHex()
    val cardHex = tokens.carbonSurface.toCssHex()
    val inkHex = tokens.inkPrimary.toCssHex()
    val mutedHex = tokens.inkSecondary.toCssHex()
    val accentHex = tokens.themeAccent.toCssHex()
    val borderHex = if (tokens.isLightTheme) "rgba(26,25,23,0.16)" else "rgba(255,255,255,0.16)"

    val themedHtml = remember(
        attachment.fileName,
        tokens.activeTheme,
        bgHex,
        surfaceHex,
        cardHex,
        inkHex,
        accentHex,
    ) {
        diagramStore.readThemedDiagramHtml(
            fileName = attachment.fileName,
            bgHex = bgHex,
            surfaceHex = surfaceHex,
            cardHex = cardHex,
            inkHex = inkHex,
            mutedHex = mutedHex,
            accentHex = accentHex,
            borderHex = borderHex,
            isLight = tokens.isLightTheme,
        )
    }

    val renderKey = "${attachment.fileName}|${tokens.activeTheme.id}|$bgHex"

    Surface(
        shape = RoundedCornerShape(if (isSplitScreen) 18.dp else 22.dp),
        color = tokens.obsidianSurface,
        border = BorderStroke(1.dp, tokens.hairlineRest),
        modifier = modifier
            .fillMaxSize()
            .testTag("image_code_web_viewer_pane"),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Viewer Header Toolbar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(tokens.carbonSurface)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = attachment.title,
                        style = MaterialTheme.typography.labelLarge,
                        color = tokens.inkPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "${attachment.fileName} · $zoomPercent%",
                        style = MicroTagMono,
                        color = tokens.inkMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                // Zoom controls
                IconButton(
                    onClick = onZoomOut,
                    modifier = Modifier.testTag("webviewer_zoom_out"),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ZoomOut,
                        contentDescription = stringResource(R.string.webviewer_zoom_out_desc),
                        tint = tokens.inkSecondary,
                        modifier = Modifier.size(19.dp),
                    )
                }
                IconButton(
                    onClick = onResetZoom,
                    modifier = Modifier.testTag("webviewer_zoom_reset"),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = stringResource(R.string.webviewer_reset_zoom_desc),
                        tint = tokens.inkSecondary,
                        modifier = Modifier.size(18.dp),
                    )
                }
                IconButton(
                    onClick = onZoomIn,
                    modifier = Modifier.testTag("webviewer_zoom_in"),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ZoomIn,
                        contentDescription = stringResource(R.string.webviewer_zoom_in_desc),
                        tint = tokens.inkSecondary,
                        modifier = Modifier.size(19.dp),
                    )
                }

                // Split-screen toggle
                Surface(
                    shape = RoundedCornerShape(9.dp),
                    color = if (isSplitScreen) tokens.primaryCta else tokens.obsidianSurface,
                    border = BorderStroke(1.dp, tokens.hairlineRest),
                    modifier = Modifier
                        .testTag("webviewer_toggle_split")
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(9.dp))
                        .clickable(role = Role.Button, onClick = onToggleSplitScreen),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Splitscreen,
                            contentDescription = stringResource(R.string.webviewer_split_screen),
                            tint = if (isSplitScreen) tokens.onPrimaryCta else tokens.inkPrimary,
                            modifier = Modifier.size(15.dp),
                        )
                        Text(
                            text = if (isSplitScreen) "Split" else "Split",
                            style = MonoBadgeStyle,
                            color = if (isSplitScreen) tokens.onPrimaryCta else tokens.inkPrimary,
                        )
                    }
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier.testTag("webviewer_close_button"),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = stringResource(R.string.webviewer_close_desc),
                        tint = tokens.inkPrimary,
                        modifier = Modifier.size(19.dp),
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(tokens.hairlineRest),
            )

            // Locked-down Android WebView
            AndroidView(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(tokens.amoledBackground),
                factory = { ctx ->
                    WebView(ctx).apply {
                        setBackgroundColor(tokens.amoledBackground.toArgb())
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = false
                        settings.allowFileAccess = false
                        settings.allowContentAccess = false
                        @Suppress("DEPRECATION")
                        settings.allowFileAccessFromFileURLs = false
                        @Suppress("DEPRECATION")
                        settings.allowUniversalAccessFromFileURLs = false
                        settings.cacheMode = WebSettings.LOAD_NO_CACHE
                        settings.setSupportZoom(true)
                        settings.builtInZoomControls = true
                        settings.displayZoomControls = false
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true
                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?,
                            ): Boolean {
                                val uri = request?.url?.toString().orEmpty()
                                // Strictly block all external network or file:// navigation
                                return !uri.startsWith(SYNTHETIC_DIAGRAM_ORIGIN)
                            }
                        }
                        tag = renderKey
                        settings.textZoom = zoomPercent
                        loadDataWithBaseURL(
                            SYNTHETIC_DIAGRAM_ORIGIN,
                            themedHtml,
                            "text/html",
                            "UTF-8",
                            null,
                        )
                    }
                },
                update = { webView ->
                    webView.setBackgroundColor(tokens.amoledBackground.toArgb())
                    webView.settings.textZoom = zoomPercent
                    if (webView.tag != renderKey) {
                        webView.tag = renderKey
                        webView.loadDataWithBaseURL(
                            SYNTHETIC_DIAGRAM_ORIGIN,
                            themedHtml,
                            "text/html",
                            "UTF-8",
                            null,
                        )
                    }
                },
            )
        }
    }
}

@Composable
fun DiagramZoomModalDialog(
    attachment: DiagramAttachment,
    zoomPercent: Int,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onResetZoom: () -> Unit,
    onSwitchToSplitScreen: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
        ) {
            InteractiveDiagramWebViewPane(
                attachment = attachment,
                zoomPercent = zoomPercent,
                isSplitScreen = false,
                onZoomIn = onZoomIn,
                onZoomOut = onZoomOut,
                onResetZoom = onResetZoom,
                onToggleSplitScreen = onSwitchToSplitScreen,
                onClose = onDismiss,
            )
        }
    }
}
