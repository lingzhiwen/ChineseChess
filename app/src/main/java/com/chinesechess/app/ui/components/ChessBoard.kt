package com.chinesechess.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.chinesechess.app.data.model.ChessBoard
import com.chinesechess.app.data.model.ChessPiece
import com.chinesechess.app.data.model.Position
import com.chinesechess.app.ui.theme.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ChessBoardView(
    board: ChessBoard,
    onPieceClick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(9f / 10f)
            .padding(16.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = ChessBoardShadow,
                ambientColor = ChessBoardShadow
            ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = ChessBoardBorder
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = ChessBoardBackground
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    BackgroundGradientStart,
                                    BackgroundGradientEnd,
                                    BackgroundGradientStart
                                )
                            )
                        )
                ) {
                    // 绘制棋盘网格和装饰
                    Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        drawChessBoard()
                    }
                    
                    // 使用Grid布局来正确定位棋子
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(9),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(0.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp),
                        horizontalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        items(90) { index ->
                            val row = index / 9
                            val col = index % 9
                            val piece = board.getPieceAt(row, col)
                            
                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clickable { onPieceClick(row, col) }
                            ) {
                                if (piece != null) {
                                    val isSelected = board.selectedPiece?.id == piece.id
                                    val isHighlighted = board.validMoves.contains(Position(row, col))
                                    
                                    ChessPiece3D(
                                        piece = piece,
                                        isSelected = isSelected,
                                        isHighlighted = isHighlighted,
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .size(36.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawChessBoard() {
    val width = size.width
    val height = size.height
    val cellWidth = width / 9f
    val cellHeight = height / 10f
    
    // 绘制网格线
    drawGridLines(cellWidth, cellHeight)
    
    // 绘制炮台标记
    drawCannonPositions(cellWidth, cellHeight)
    
    // 绘制兵卒标记
    drawSoldierPositions(cellWidth, cellHeight)
    
    // 绘制九宫格
    drawPalaces(cellWidth, cellHeight)
    
    // 绘制河界
    drawRiver(cellWidth, cellHeight)
}

private fun DrawScope.drawGridLines(cellWidth: Float, cellHeight: Float) {
    val strokeWidth = 2.5f
    
    // 绘制垂直线
    for (i in 0..8) {
        val x = i * cellWidth + cellWidth / 2
        
        // 上半部分
        drawLine(
            color = GridLineBlack,
            start = Offset(x, cellHeight / 2),
            end = Offset(x, cellHeight * 4.5f),
            strokeWidth = strokeWidth
        )
        
        // 下半部分
        drawLine(
            color = GridLineBlack,
            start = Offset(x, cellHeight * 5.5f),
            end = Offset(x, size.height - cellHeight / 2),
            strokeWidth = strokeWidth
        )
    }
    
    // 绘制水平线
    for (i in 0..9) {
        val y = i * cellHeight + cellHeight / 2
        val startX = cellWidth / 2
        val endX = size.width - cellWidth / 2
        
        drawLine(
            color = GridLineBlack,
            start = Offset(startX, y),
            end = Offset(endX, y),
            strokeWidth = strokeWidth
        )
    }
    
    // 绘制边框加强线
    val borderStrokeWidth = 4f
    val padding = cellWidth / 2
    
    drawLine(
        color = GridLineBlack,
        start = Offset(padding, padding),
        end = Offset(size.width - padding, padding),
        strokeWidth = borderStrokeWidth
    )
    
    drawLine(
        color = GridLineBlack,
        start = Offset(padding, size.height - padding),
        end = Offset(size.width - padding, size.height - padding),
        strokeWidth = borderStrokeWidth
    )
    
    drawLine(
        color = GridLineBlack,
        start = Offset(padding, padding),
        end = Offset(padding, size.height - padding),
        strokeWidth = borderStrokeWidth
    )
    
    drawLine(
        color = GridLineBlack,
        start = Offset(size.width - padding, padding),
        end = Offset(size.width - padding, size.height - padding),
        strokeWidth = borderStrokeWidth
    )
}

private fun DrawScope.drawCannonPositions(cellWidth: Float, cellHeight: Float) {
    // 炮台位置 (红方: (2,1), (2,7); 黑方: (7,1), (7,7))
    val cannonPositions = listOf(
        Pair(2, 1), Pair(2, 7),  // 红方炮
        Pair(7, 1), Pair(7, 7)   // 黑方炮
    )
    
    cannonPositions.forEach { (row, col) ->
        drawPositionMarker(
            centerX = col * cellWidth + cellWidth / 2,
            centerY = row * cellHeight + cellHeight / 2,
            cellWidth = cellWidth,
            cellHeight = cellHeight
        )
    }
}

private fun DrawScope.drawSoldierPositions(cellWidth: Float, cellHeight: Float) {
    // 兵卒位置 (红方: (3, 0/2/4/6/8); 黑方: (6, 0/2/4/6/8))
    val soldierCols = listOf(0, 2, 4, 6, 8)
    
    soldierCols.forEach { col ->
        // 红方兵
        drawPositionMarker(
            centerX = col * cellWidth + cellWidth / 2,
            centerY = 3 * cellHeight + cellHeight / 2,
            cellWidth = cellWidth,
            cellHeight = cellHeight
        )
        
        // 黑方卒
        drawPositionMarker(
            centerX = col * cellWidth + cellWidth / 2,
            centerY = 6 * cellHeight + cellHeight / 2,
            cellWidth = cellWidth,
            cellHeight = cellHeight
        )
    }
}

private fun DrawScope.drawPositionMarker(
    centerX: Float,
    centerY: Float,
    cellWidth: Float,
    cellHeight: Float
) {
    val markerSize = cellWidth * 0.15f
    val markerStroke = 2f
    val offset = cellWidth * 0.25f
    
    // 定义8个方向的标记位置
    val positions = listOf(
        // 左上
        Triple(-1f, -1f, listOf(Pair(0f, markerSize), Pair(markerSize, 0f))),
        // 右上
        Triple(1f, -1f, listOf(Pair(0f, markerSize), Pair(-markerSize, 0f))),
        // 左下
        Triple(-1f, 1f, listOf(Pair(0f, -markerSize), Pair(markerSize, 0f))),
        // 右下
        Triple(1f, 1f, listOf(Pair(0f, -markerSize), Pair(-markerSize, 0f)))
    )
    
    positions.forEach { (xDir, yDir, lines) ->
        val baseX = centerX + xDir * offset
        val baseY = centerY + yDir * offset
        
        lines.forEach { (dx, dy) ->
            drawLine(
                color = GridLineBlack,
                start = Offset(baseX, baseY),
                end = Offset(baseX + dx, baseY + dy),
                strokeWidth = markerStroke
            )
        }
    }
}

private fun DrawScope.drawPalaces(cellWidth: Float, cellHeight: Float) {
    val strokeWidth = 3f
    
    // 红方九宫格
    drawPalace(
        left = cellWidth * 3.5f,
        top = cellHeight * 0.5f,
        right = cellWidth * 5.5f,
        bottom = cellHeight * 2.5f,
        strokeWidth = strokeWidth,
        color = PalaceGold
    )
    
    // 黑方九宫格
    drawPalace(
        left = cellWidth * 3.5f,
        top = cellHeight * 7.5f,
        right = cellWidth * 5.5f,
        bottom = cellHeight * 9.5f,
        strokeWidth = strokeWidth,
        color = PalaceGold
    )
}

private fun DrawScope.drawPalace(
    left: Float,
    top: Float,
    right: Float,
    bottom: Float,
    strokeWidth: Float,
    color: Color
) {
    // 绘制对角线
    drawLine(
        color = color,
        start = Offset(left, top),
        end = Offset(right, bottom),
        strokeWidth = strokeWidth
    )
    
    drawLine(
        color = color,
        start = Offset(right, top),
        end = Offset(left, bottom),
        strokeWidth = strokeWidth
    )
}

private fun DrawScope.drawRiver(cellWidth: Float, cellHeight: Float) {
    val riverY = cellHeight * 4.5f
    val riverHeight = cellHeight
    val strokeWidth = 3f
    
    // 绘制河界区域背景
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                RiverBlue.copy(alpha = 0.05f),
                RiverBlue.copy(alpha = 0.15f),
                RiverBlue.copy(alpha = 0.05f)
            ),
            startY = riverY,
            endY = riverY + riverHeight
        ),
        topLeft = Offset(cellWidth / 2, riverY),
        size = androidx.compose.ui.geometry.Size(
            width = size.width - cellWidth,
            height = riverHeight
        )
    )
    
    // 绘制装饰性波浪线
    drawWavyLine(
        y = riverY + riverHeight * 0.3f,
        startX = cellWidth / 2,
        endX = size.width - cellWidth / 2,
        amplitude = cellHeight * 0.08f,
        frequency = 5f,
        color = RiverBlue.copy(alpha = 0.3f),
        strokeWidth = 1.5f
    )
    
    drawWavyLine(
        y = riverY + riverHeight * 0.7f,
        startX = cellWidth / 2,
        endX = size.width - cellWidth / 2,
        amplitude = cellHeight * 0.08f,
        frequency = 5f,
        color = RiverBlue.copy(alpha = 0.3f),
        strokeWidth = 1.5f
    )
    
    // 绘制"楚河汉界"文字
    drawContext.canvas.nativeCanvas.apply {
        // 楚河文字
        val chuHePaint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#8B4513")
            textSize = cellHeight * 0.5f
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
            isFakeBoldText = true
            typeface = android.graphics.Typeface.create(
                android.graphics.Typeface.SERIF,
                android.graphics.Typeface.BOLD
            )
        }
        
        // 添加文字描边效果
        val strokePaint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#FFD700")
            textSize = cellHeight * 0.5f
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
            isFakeBoldText = true
            style = android.graphics.Paint.Style.STROKE
            setStrokeWidth(3f)
            typeface = android.graphics.Typeface.create(
                android.graphics.Typeface.SERIF,
                android.graphics.Typeface.BOLD
            )
        }
        
        val textY = riverY + riverHeight / 2 + cellHeight * 0.15f
        
        // 绘制"楚河"
        val chuHeX = cellWidth * 2.5f
        drawText("楚河", chuHeX, textY, strokePaint)
        drawText("楚河", chuHeX, textY, chuHePaint)
        
        // 绘制"汉界"
        val hanJieX = cellWidth * 6.5f
        drawText("汉界", hanJieX, textY, strokePaint)
        drawText("汉界", hanJieX, textY, chuHePaint)
    }
}

private fun DrawScope.drawWavyLine(
    y: Float,
    startX: Float,
    endX: Float,
    amplitude: Float,
    frequency: Float,
    color: Color,
    strokeWidth: Float
) {
    val path = Path()
    val width = endX - startX
    val steps = 100
    
    path.moveTo(startX, y)
    
    for (i in 1..steps) {
        val x = startX + (width * i / steps)
        val waveY = y + amplitude * sin(2 * PI * frequency * i / steps).toFloat()
        path.lineTo(x, waveY)
    }
    
    drawPath(
        path = path,
        color = color,
        style = Stroke(width = strokeWidth)
    )
}

@Composable
fun GameInfoPanel(
    currentPlayer: com.chinesechess.app.data.model.PieceColor,
    gameState: com.chinesechess.app.data.model.GameState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "当前回合",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = currentPlayer.displayName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (currentPlayer == com.chinesechess.app.data.model.PieceColor.RED) 
                        RedPieceMain else BlackPieceMain
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Divider(
                    modifier = Modifier.padding(horizontal = 40.dp),
                    thickness = 2.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                when (gameState) {
                    com.chinesechess.app.data.model.GameState.PLAYING -> {
                        Text(
                            text = "⚔️ 游戏进行中 ⚔️",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    com.chinesechess.app.data.model.GameState.PAUSED -> {
                        Text(
                            text = "⏸️ 游戏暂停",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    com.chinesechess.app.data.model.GameState.RED_WIN -> {
                        Text(
                            text = "🎉 红方获胜！🎉",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = RedPieceMain
                        )
                    }
                    com.chinesechess.app.data.model.GameState.BLACK_WIN -> {
                        Text(
                            text = "🎉 黑方获胜！🎉",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = BlackPieceMain
                        )
                    }
                    com.chinesechess.app.data.model.GameState.DRAW -> {
                        Text(
                            text = "🤝 和棋 🤝",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
