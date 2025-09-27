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

// FIX: 定义 Compose Color 常量来替换缺失的 R.color 资源，解决 Unresolved reference 错误。
private val RED_SIDE_LIGHT = Color(0xFFFFCCCC) // 浅红
private val RED_SIDE_DARK = Color(0xFFCC0000)  // 深红
private val BLACK_SIDE_LIGHT = Color(0xFFCCCCCC) // 浅灰/黑
private val BLACK_SIDE_DARK = Color(0xFF444444) // 深灰/黑
private val CIRCLE_COLOR = Color(0xFFF0E68C) // 卡其色/米黄色

// 用于传递已解析颜色的数据类
private data class ResolvedColors(
    val redSideLight: Color,
    val redSideDark: Color,
    val blackSideLight: Color,
    val blackSideDark: Color,
    val circleColor: Color
)

@Composable
fun ChessPiece3D(
    piece: ChessPiece,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    isSelected: Boolean = false,
    isHighlighted: Boolean = false,
    isLight: Boolean = true
) {
    // 添加选中动画
    val selectedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "selectedScale"
    )

    // 添加高亮动画
    val highlightAlpha by animateFloatAsState(
        targetValue = if (isHighlighted) 0.8f else 1f,
        animationSpec = tween(500),
        label = "highlightAlpha"
    )

    // FIX: 使用定义的常量来初始化颜色，避免 resource lookup 错误
    val resolvedColors = ResolvedColors(
        redSideLight = RED_SIDE_LIGHT,
        redSideDark = RED_SIDE_DARK,
        blackSideLight = BLACK_SIDE_LIGHT,
        blackSideDark = BLACK_SIDE_DARK,
        circleColor = CIRCLE_COLOR
    )

    Canvas(
        modifier = modifier.size(size)
    ) {
        draw3DPiece(
            piece = piece,
            isSelected = isSelected,
            isHighlighted = isHighlighted,
            selectedScale = selectedScale,
            highlightAlpha = highlightAlpha,
            isLight = isLight,
            resolvedColors = resolvedColors
        )
    }
}

private fun DrawScope.draw3DPiece(
    piece: ChessPiece,
    isSelected: Boolean,
    isHighlighted: Boolean,
    selectedScale: Float,
    highlightAlpha: Float,
    isLight: Boolean,
    resolvedColors: ResolvedColors
) {
    val center = size.center
    val baseRadius = size.minDimension / 2f * 0.8f
    val radius = baseRadius * selectedScale

    // 绘制3D效果
    draw3DCylinder(
        center = center,
        radius = radius,
        color = piece.color.color,
        isSelected = isSelected,
        isHighlighted = isHighlighted,
        highlightAlpha = highlightAlpha
    )

    // 绘制棋子细节
    drawPieceDetails(
        center = center,
        radius = radius,
        pieceColor = piece.color.color,
        isLight = isLight,
        isRedSide = piece.isRed(),
        resolvedColors = resolvedColors
    )

    // 绘制棋子文字
    drawPieceText(
        center = center,
        piece = piece,
        //color = if (piece.isRed()) Color.White else Color.White
    )
}

private fun DrawScope.draw3DCylinder(
    center: Offset,
    radius: Float,
    color: Color,
    isSelected: Boolean,
    isHighlighted: Boolean,
    highlightAlpha: Float
) {
    val gradient = Brush.radialGradient(
        colors = listOf(
            color.copy(alpha = 0.9f * highlightAlpha),
            color.copy(alpha = 0.7f * highlightAlpha),
            color.copy(alpha = 0.5f * highlightAlpha)
        ),
        radius = radius
    )

    // 绘制主体圆柱
    drawCircle(
        brush = gradient,
        radius = radius,
        center = center
    )

    // 绘制高光效果
    val highlightRadius = radius * 0.3f
    val highlightCenter = Offset(
        center.x - radius * 0.3f,
        center.y - radius * 0.3f
    )

    drawCircle(
        color = Color.White.copy(alpha = 0.3f * highlightAlpha),
        radius = highlightRadius,
        center = highlightCenter
    )

    // 绘制阴影
    val shadowRadius = radius * 0.95f
    val shadowCenter = Offset(
        center.x + radius * 0.1f,
        center.y + radius * 0.1f
    )

    drawCircle(
        color = Color.Black.copy(alpha = 0.2f * highlightAlpha),
        radius = shadowRadius,
        center = shadowCenter
    )

    // 绘制边框
    val borderColor = when {
        isSelected -> Color(0xFFFFD700) // 金色
        isHighlighted -> Color(0xFF90EE90) // 绿色
        else -> Color.Black.copy(alpha = 0.3f)
    }

    drawCircle(
        color = Color.Transparent,
        radius = radius,
        center = center,
        style = Stroke(width = if (isSelected) 4f else 2f)
    )

    // 绘制3D边框效果
    draw3DBorder(center, radius, borderColor, isSelected)
}

private fun DrawScope.draw3DBorder(
    center: Offset,
    radius: Float,
    color: Color,
    isSelected: Boolean
) {
    val borderWidth = if (isSelected) 4f else 2f
    val innerRadius = radius - borderWidth / 2

    // 绘制外圈高光
    drawCircle(
        color = color.copy(alpha = 0.8f),
        radius = radius,
        center = center,
        style = Stroke(width = borderWidth)
    )

    // 绘制内圈阴影
    drawCircle(
        color = Color.Black.copy(alpha = 0.3f),
        radius = innerRadius,
        center = center,
        style = Stroke(width = 1f)
    )
}

private fun DrawScope.drawPieceDetails(
    center: Offset,
    radius: Float,
    pieceColor: Color,
    isLight: Boolean,
    isRedSide: Boolean,
    resolvedColors: ResolvedColors
) {
    // Draw the piece outline/base background

    // FIX: 移除了可能导致 Val cannot be reassigned 的冗余声明或赋值
    val backgroundColor = if (isRedSide) {
        if (isLight) resolvedColors.redSideLight else resolvedColors.redSideDark
    } else {
        if (isLight) resolvedColors.blackSideLight else resolvedColors.blackSideDark
    }

    // 绘制棋子底边（使用 resolvedColors.circleColor）
    drawCircle(
        color = resolvedColors.circleColor,
        radius = radius * 0.9f,
        center = center,
        style = Stroke(width = 2.dp.toPx())
    )
}

private fun DrawScope.drawPieceText(
    center: Offset,
    piece: ChessPiece,
    //color: Color
) {
    val text = getPieceDisplayText(piece.type, piece.color)
    val fontSize = size.minDimension * 0.4f
    val shadowOffset = Offset(1.5f, 1.5f) // 定义阴影偏移量，使阴影更明显

    // 使用 Compose Color 定义棋子文字颜色
    val redPieceColor = Color(0xFFDC143C) // 红色 (Crimson)
    val blackPieceColor = Color(0xFF2F2F2F) // 黑色 (Dark Gray)

    // 绘制文字阴影
    drawContext.canvas.nativeCanvas.apply {
        val paint = android.graphics.Paint().apply {
            //color = Color.Black // 阴影颜色
            textSize = fontSize
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
        }
        drawText(
            text,
            center.x + shadowOffset.x,
            center.y + shadowOffset.y + fontSize / 3,
            paint
        )
    }

    // 绘制主文字
    drawContext.canvas.nativeCanvas.apply {
        val paint = android.graphics.Paint().apply {
            // 使用 Compose Color 变量并转换为 Int (toArgb())
//            color = if (piece.isRed()) {
//                redPieceColor // 使用 Compose Color
//            } else {
//                blackPieceColor // 使用 Compose Color
//            }
            textSize = fontSize
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
            isFakeBoldText = true
        }
        drawText(
            text,
            center.x,
            center.y + fontSize / 3,
            paint
        )
    }
}

private fun getPieceDisplayText(type: PieceType, color: com.chinesechess.app.data.model.PieceColor): String {
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
