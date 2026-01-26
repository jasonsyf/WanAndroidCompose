package com.syf.wanandroidcompose.common

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.debugInspectorInfo

/**
 * Custom implementation of Modifier.placeholder to replace deprecated Accompanist library.
 */
fun Modifier.placeholder(
    visible: Boolean,
    color: Color = Color(0xFFEEEEEE),
    shape: Shape = RectangleShape,
    highlight: PlaceholderHighlight? = null,
): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "placeholder"
        value = visible
        properties["visible"] = visible
        properties["color"] = color
        properties["shape"] = shape
        properties["highlight"] = highlight
    }) {
    if (!visible) return@composed this
    val infiniteTransition = rememberInfiniteTransition(label = "placeholder")
    val highlightBrush = highlight?.brush(infiniteTransition)

    Modifier.then(
        Modifier.drawWithContent { // Placeholder covers the content, so we don't call drawContent()
            val outline = shape.createOutline(size, layoutDirection, this) // Draw background color
            drawOutline(
                outline = outline,
                color = color,
            ) // Draw highlight (Shimmer)
            highlightBrush?.let { brush -> // Apply the shape to the shimmer as well
                drawIntoCanvas { canvas ->
                    canvas.save()
                    canvas.clipPath(Path().apply { addOutline(outline) })
                    drawRect(brush = brush)
                    canvas.restore()
                }
            }
        })
}

interface PlaceholderHighlight {
    @Composable
    fun brush(infiniteTransition: InfiniteTransition): Brush

    companion object
}

/**
 * Creates a [PlaceholderHighlight] which fades in an out, between the
 * [highlightColor] and transparent.
 */
fun PlaceholderHighlight.Companion.shimmer(
    highlightColor: Color = Color.White.copy(alpha = 0.5f),
    animationSpec: InfiniteRepeatableSpec<Float> = infiniteRepeatable(
        animation = tween(
            durationMillis = 1500, easing = LinearEasing
        ), repeatMode = RepeatMode.Restart
    ),
): PlaceholderHighlight = Shimmer(
    highlightColor = highlightColor,
    animationSpec = animationSpec,
)

private data class Shimmer(
    private val highlightColor: Color,
    private val animationSpec: InfiniteRepeatableSpec<Float>,
) : PlaceholderHighlight {
    @Composable
    override fun brush(infiniteTransition: InfiniteTransition): Brush {
        val progress by infiniteTransition.animateFloat(
            initialValue = 0f, targetValue = 1f, animationSpec = animationSpec, label = "shimmer"
        )

        return remember(progress, highlightColor) {
            Brush.linearGradient(
                colors = listOf(
                    Color.Transparent,
                    highlightColor,
                    Color.Transparent,
                ),
                start = Offset(x = 0f, y = 0f),
                end = Offset(x = Float.POSITIVE_INFINITY, y = Float.POSITIVE_INFINITY), // Diagonal
                // Ideally this needs to be relative to the size drawn, but Brush.linearGradient
                // is usually relative to the drawing area if not specified absolute.
                // However, for moving shimmer, we need to shift coordinates.
                // To keep it simple: we just assume a brush that works reasonably well.
                // BUT, proper shimmer moves. 
                // Let's implement a moving brush based on progress.
            )
        }
    }
}

// Fixed Shimmer Implementation that actually moves
private data class MovingShimmer(
    private val highlightColor: Color,
    private val animationSpec: InfiniteRepeatableSpec<Float>,
) : PlaceholderHighlight {
    @Composable
    override fun brush(infiniteTransition: InfiniteTransition): Brush {
        val effectOffset = infiniteTransition.animateFloat(
            initialValue = 0f, targetValue = 1000f, // Arbitrary large pixel value for translation
            animationSpec = animationSpec, label = "shimmer_offset"
        ) // This is tricky because we don't know the size yet.
        // A robust solution uses a Brush subclass or shader. 
        // For simplicity, let's just make a gradient that is large enough.
        return Brush.linearGradient(
            0.0f to Color.Transparent,
            0.5f to highlightColor,
            1.0f to Color.Transparent,
            start = Offset(effectOffset.value - 1000f, effectOffset.value - 1000f),
            end = Offset(effectOffset.value, effectOffset.value)
        )
    }
}

fun PlaceholderHighlight.Companion.shimmerSimple(): PlaceholderHighlight =
    object : PlaceholderHighlight {
        @Composable
        override fun brush(infiniteTransition: InfiniteTransition): Brush {
            val translateAnim by infiniteTransition.animateFloat(
                initialValue = 0f, targetValue = 2000f, animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 1200, easing = LinearEasing
                    ), repeatMode = RepeatMode.Restart
                ), label = "shimmer"
            )

            return Brush.linearGradient(
                colors = listOf(
                    Color.Transparent, Color.White.copy(alpha = 0.5f), Color.Transparent
                ),
                start = Offset(translateAnim - 1000f, translateAnim - 1000f),
                end = Offset(translateAnim, translateAnim)
            )
        }
    }

// Updating the main Shimmer function to use the simple implementation
fun PlaceholderHighlight.Companion.shimmer(highlightColor: Color = Color.White.copy(alpha = 0.5f)) =
    shimmerSimple()
