package com.chinesechess.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chinesechess.app.data.model.ChessPiece
import com.chinesechess.app.data.model.PieceType
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import com.chinesechess.app.ui.theme.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ChessPiece3D(
    piece: ChessPiece,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    isSelected: Boolean = false,
    isHighlighted: Boolean = false,
    isLight: Boolean = true
) {
    // 选中动画 - 弹性缩放
    val selectedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "selectedScale"
    )

    // 高亮动画 - 脉冲效果
    val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    // 旋转动画 - 选中时轻微旋转
    val rotation by animateFloatAsState(
        targetValue = if (isSelected) 5f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "rotation"
    )

    // 浮动动画 - 选中时上浮
    val elevation by animateFloatAsState(
        targetValue = if (isSelected) 8f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "elevation"
    )

    Canvas(
        modifier = modifier.size(size)
    ) {
        draw3DPiece(
            piece = piece,
            isSelected = isSelected,
            isHighlighted = isHighlighted,
            selectedScale = selectedScale,
            pulseAlpha = if (isHighlighted) pulseAlpha else 1f,
            rotation = rotation,
            elevation = elevation,
            isLight = isLight
        )
    }
}

private fun DrawScope.draw3DPiece(
    piece: ChessPiece,
    isSelected: Boolean,
    isHighlighted: Boolean,
    selectedScale: Float,
    pulseAlpha: Float,
    rotation: Float,
    elevation: Float,
    isLight: Boolean
) {
    val center = size.center
    val baseRadius = size.minDimension / 2f * 0.85f
    val radius = baseRadius * selectedScale
    
    // 确定棋子颜色
    val isRedPiece = piece.isRed()
    val mainColor = if (isRedPiece) RedPieceMain else BlackPieceMain
    val lightColor = if (isRedPiece) RedPieceLight else BlackPieceLight
    val darkColor = if (isRedPiece) RedPieceDark else BlackPieceDark
    val glowColor = if (isRedPiece) RedPieceGlow else BlackPieceGlow

    // 绘制底部阴影（3D效果）
    draw3DShadow(
        center = Offset(center.x + elevation * 0.3f, center.y + elevation * 0.5f),
        radius = radius * 1.1f,
        elevation = elevation
    )

    // 绘制外发光效果（选中或高亮时）
    if (isSelected || isHighlighted) {
        drawOuterGlow(
            center = center,
            radius = radius,
            glowColor = if (isSelected) SelectionGlow else ValidMoveIndicator,
            intensity = pulseAlpha
        )
    }

    // 绘制3D圆柱体主体
    draw3DCylinder(
        center = center,
        radius = radius,
        mainColor = mainColor,
        lightColor = lightColor,
        darkColor = darkColor,
        isSelected = isSelected,
        isHighlighted = isHighlighted,
        pulseAlpha = pulseAlpha
    )

    // 绘制金属质感边缘
    drawMetallicRim(
        center = center,
        radius = radius,
        isRedPiece = isRedPiece
    )

    // 绘制内圈装饰
    drawInnerDecoration(
        center = center,
        radius = radius * 0.88f,
        color = if (isRedPiece) Color(0xFFFFD700) else Color(0xFFC0C0C0)
    )

    // 绘制精细边框
    drawDetailedBorder(
        center = center,
        radius = radius,
        isSelected = isSelected,
        isHighlighted = isHighlighted,
        glowColor = glowColor
    )

    // 绘制棋子文字
    drawEnhancedPieceText(
        center = center,
        radius = radius,
        piece = piece,
        isRedPiece = isRedPiece
    )
}

private fun DrawScope.draw3DShadow(
    center: Offset,
    radius: Float,
    elevation: Float
) {
    // 多层阴影，营造深度感
    val shadowLayers = 3
    for (i in shadowLayers downTo 1) {
        val shadowRadius = radius * (1f + i * 0.05f)
        val shadowAlpha = 0.15f / i * (1f + elevation * 0.1f)
        
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.Black.copy(alpha = shadowAlpha),
                    Color.Transparent
                ),
                radius = shadowRadius
            ),
            radius = shadowRadius,
            center = center
        )
    }
}

private fun DrawScope.drawOuterGlow(
    center: Offset,
    radius: Float,
    glowColor: Color,
    intensity: Float
) {
    // 外发光效果
    for (i in 3 downTo 1) {
        val glowRadius = radius * (1f + i * 0.15f)
        val glowAlpha = (0.3f / i) * intensity
        
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    glowColor.copy(alpha = glowAlpha),
                    glowColor.copy(alpha = glowAlpha * 0.5f),
                    Color.Transparent
                ),
                radius = glowRadius
            ),
            radius = glowRadius,
            center = center
        )
    }
}

private fun DrawScope.draw3DCylinder(
    center: Offset,
    radius: Float,
    mainColor: Color,
    lightColor: Color,
    darkColor: Color,
    isSelected: Boolean,
    isHighlighted: Boolean,
    pulseAlpha: Float
) {
    // 主体渐变 - 模拟3D圆柱光照
    val cylinderGradient = Brush.radialGradient(
        colors = listOf(
            lightColor.copy(alpha = 0.9f * pulseAlpha),
            mainColor.copy(alpha = 0.95f * pulseAlpha),
            darkColor.copy(alpha = 0.85f * pulseAlpha),
            darkColor.copy(alpha = 0.7f * pulseAlpha)
        ),
        center = Offset(center.x - radius * 0.2f, center.y - radius * 0.2f),
        radius = radius * 1.4f
    )

    // 绘制主体
    drawCircle(
        brush = cylinderGradient,
        radius = radius,
        center = center
    )

    // 顶部高光
    val highlightRadius = radius * 0.4f
    val highlightCenter = Offset(
        center.x - radius * 0.25f,
        center.y - radius * 0.25f
    )

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.6f * pulseAlpha),
                Color.White.copy(alpha = 0.3f * pulseAlpha),
                Color.Transparent
            ),
            radius = highlightRadius
        ),
        radius = highlightRadius,
        center = highlightCenter
    )

    // 底部暗部
    val shadowRadius = radius * 0.3f
    val shadowCenter = Offset(
        center.x + radius * 0.3f,
        center.y + radius * 0.3f
    )

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                darkColor.copy(alpha = 0.4f * pulseAlpha),
                Color.Transparent
            ),
            radius = shadowRadius
        ),
        radius = shadowRadius,
        center = shadowCenter
    )
}

private fun DrawScope.drawMetallicRim(
    center: Offset,
    radius: Float,
    isRedPiece: Boolean
) {
    // 金属边缘效果
    val rimColor = if (isRedPiece) BronzeGold else Color(0xFFC0C0C0)
    
    // 外圈
    drawCircle(
        color = rimColor.copy(alpha = 0.7f),
        radius = radius,
        center = center,
        style = Stroke(width = 2.5f)
    )
    
    // 内圈高光
    drawCircle(
        color = rimColor.copy(alpha = 0.4f),
        radius = radius - 1.5f,
        center = center,
        style = Stroke(width = 1f)
    )
    
    // 外圈阴影
    drawCircle(
        color = Color.Black.copy(alpha = 0.3f),
        radius = radius + 1f,
        center = center,
        style = Stroke(width = 1f)
    )
}

private fun DrawScope.drawInnerDecoration(
    center: Offset,
    radius: Float,
    color: Color
) {
    // 内圈装饰圆环
    drawCircle(
        color = color.copy(alpha = 0.5f),
        radius = radius,
        center = center,
        style = Stroke(width = 2.5f)
    )
    
    // 更内层的细环
    drawCircle(
        color = color.copy(alpha = 0.3f),
        radius = radius - 4f,
        center = center,
        style = Stroke(width = 1f)
    )
}

private fun DrawScope.drawDetailedBorder(
    center: Offset,
    radius: Float,
    isSelected: Boolean,
    isHighlighted: Boolean,
    glowColor: Color
) {
    val borderColor = when {
        isSelected -> SelectionGlow
        isHighlighted -> ValidMoveIndicator
        else -> Color.Black.copy(alpha = 0.4f)
    }
    
    val borderWidth = if (isSelected) 3.5f else 2f

    // 主边框
    drawCircle(
        color = borderColor,
        radius = radius,
        center = center,
        style = Stroke(width = borderWidth)
    )
    
    // 内侧光晕
    if (isSelected || isHighlighted) {
        drawCircle(
            color = borderColor.copy(alpha = 0.3f),
            radius = radius - borderWidth,
            center = center,
            style = Stroke(width = 1.5f)
        )
    }
}

private fun DrawScope.drawEnhancedPieceText(
    center: Offset,
    radius: Float,
    piece: ChessPiece,
    isRedPiece: Boolean
) {
    val text = getPieceDisplayText(piece.type, piece.color)
    val fontSize = radius * 0.9f
    
    // 文字颜色
    val textColor = if (isRedPiece) {
        android.graphics.Color.parseColor("#FFFFFF") // 白色文字
    } else {
        android.graphics.Color.parseColor("#FFFFFF") // 白色文字
    }
    
    val strokeColor = if (isRedPiece) {
        android.graphics.Color.parseColor("#8B0000") // 深红描边
    } else {
        android.graphics.Color.parseColor("#000000") // 黑色描边
    }

    drawContext.canvas.nativeCanvas.apply {
        // 外层粗描边（阴影效果）
        val shadowPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = fontSize
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
            style = android.graphics.Paint.Style.STROKE
            strokeWidth = 8f
            isFakeBoldText = true
            typeface = android.graphics.Typeface.create(
                android.graphics.Typeface.SERIF,
                android.graphics.Typeface.BOLD
            )
        }
        
        // 中层描边
        val strokePaint = android.graphics.Paint().apply {
            color = strokeColor
            textSize = fontSize
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
            style = android.graphics.Paint.Style.STROKE
            strokeWidth = 5f
            isFakeBoldText = true
            typeface = android.graphics.Typeface.create(
                android.graphics.Typeface.SERIF,
                android.graphics.Typeface.BOLD
            )
        }
        
        // 主文字
        val fillPaint = android.graphics.Paint().apply {
            color = textColor
            textSize = fontSize
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
            isFakeBoldText = true
            typeface = android.graphics.Typeface.create(
                android.graphics.Typeface.SERIF,
                android.graphics.Typeface.BOLD
            )
        }
        
        val textY = center.y + fontSize * 0.35f
        
        // 绘制层次：阴影 -> 描边 -> 填充
        drawText(text, center.x, textY, shadowPaint)
        drawText(text, center.x, textY, strokePaint)
        drawText(text, center.x, textY, fillPaint)
    }
}

private fun getPieceDisplayText(
    type: PieceType, 
    color: com.chinesechess.app.data.model.PieceColor
): String {
    return when (type) {
        PieceType.GENERAL -> if (color == com.chinesechess.app.data.model.PieceColor.RED) "帅" else "将"
        PieceType.ADVISOR -> "士"
        PieceType.ELEPHANT -> if (color == com.chinesechess.app.data.model.PieceColor.RED) "相" else "象"
        PieceType.HORSE -> "马"
        PieceType.CHARIOT -> "车"
        PieceType.CANNON -> "炮"
        PieceType.SOLDIER -> if (color == com.chinesechess.app.data.model.PieceColor.RED) "兵" else "卒"
    }
}
