package com.syf.wanandroidcompose.common

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
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
 * Modifier.placeholder 的自定义实现，用于替代已废弃的 Accompanist 库。
 *
 * @param visible 是否显示占位符。
 * @param color 占位符的背景颜色。
 * @param shape 占位符的形状。
 * @param highlight 占位符的高亮效果，如闪烁。
 */
fun Modifier.placeholder(
    visible: Boolean,
    color: Color = Color.Unspecified,
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
    // 如果颜色未指定，则使用主题中的颜色
    val resolvedColor =
        if (color == Color.Unspecified) {
            MaterialTheme.colorScheme.surfaceContainerHighest
        } else {
            color
        }
    val infiniteTransition = rememberInfiniteTransition(label = "placeholder")
    val highlightBrush = highlight?.brush(infiniteTransition)

    Modifier.then(
        Modifier.drawWithContent { // 占位符会覆盖内容，因此我们不调用 drawContent()
            val outline = shape.createOutline(size, layoutDirection, this) // 绘制背景颜色
            drawOutline(
                outline = outline,
                color = resolvedColor,
            ) // 绘制高亮效果（闪烁）
            highlightBrush?.let { brush -> // 同样为闪烁效果应用形状
                drawIntoCanvas { canvas ->
                    canvas.save()
                    canvas.clipPath(Path().apply { addOutline(outline) })
                    drawRect(brush = brush)
                    canvas.restore()
                }
            }
        })
}

/**
 * 定义占位符高亮效果的接口。
 */
interface PlaceholderHighlight {
    @Composable
    fun brush(infiniteTransition: InfiniteTransition): Brush

    companion object
}

/**
 * 创建一个渐隐渐现的 [PlaceholderHighlight] 效果，在高亮颜色和透明色之间过渡。
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
                end = Offset(x = Float.POSITIVE_INFINITY, y = Float.POSITIVE_INFINITY), // 对角线
                // 理想情况下，这需要相对于绘制的尺寸，但 Brush.linearGradient
                // 如果没有指定绝对值，通常是相对于绘制区域的。
                // 然而，对于移动的闪烁效果，我们需要平移坐标。
                // 为简单起见：我们只假设一个效果合理的画刷。
                // 但是，真正的闪烁效果是移动的。
                // 让我们基于进度实现一个移动的画刷。
            )
        }
    }
}

// 真正移动的闪烁效果实现
private data class MovingShimmer(
    private val highlightColor: Color,
    private val animationSpec: InfiniteRepeatableSpec<Float>,
) : PlaceholderHighlight {
    @Composable
    override fun brush(infiniteTransition: InfiniteTransition): Brush {
        val effectOffset = infiniteTransition.animateFloat(
            initialValue = 0f, targetValue = 1000f, // 用于平移的任意大像素值
            animationSpec = animationSpec, label = "shimmer_offset"
        ) // 这很棘手，因为我们还不知道尺寸。
        // 一个健壮的解决方案是使用 Brush 的子类或着色器。
        // 为简单起见，我们只创建一个足够大的渐变。
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

// 更新主 Shimmer 函数以使用简单的实现
fun PlaceholderHighlight.Companion.shimmer(highlightColor: Color = Color.Unspecified) =
    object : PlaceholderHighlight {
        @Composable
        override fun brush(infiniteTransition: InfiniteTransition): Brush {
            val resolvedHighlight =
                if (highlightColor == Color.Unspecified) {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                } else {
                    highlightColor
                }
            val translateAnim by infiniteTransition.animateFloat(
                initialValue = 0f, targetValue = 2000f, animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 1200, easing = LinearEasing
                    ), repeatMode = RepeatMode.Restart
                ), label = "shimmer"
            )

            return Brush.linearGradient(
                colors = listOf(
                    Color.Transparent, resolvedHighlight, Color.Transparent
                ),
                start = Offset(translateAnim - 1000f, translateAnim - 1000f),
                end = Offset(translateAnim, translateAnim)
            )
        }
    }
