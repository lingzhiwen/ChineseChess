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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.chinesechess.app.data.model.ChessBoard
import com.chinesechess.app.data.model.ChessPiece
import com.chinesechess.app.data.model.Position
import com.chinesechess.app.ui.theme.*

@Composable
fun ChessBoardView(
    board: ChessBoard,
    onPieceClick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(9f / 10f)
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ChessBoardLight)
        ) {
            // 绘制棋盘网格
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
                                    .size(32.dp)
                            )
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
    
    // 绘制九宫格
    drawPalaces(cellWidth, cellHeight)
    
    // 绘制河界
    drawRiver(cellWidth, cellHeight)
}

private fun DrawScope.drawGridLines(cellWidth: Float, cellHeight: Float) {
    val strokeWidth = 2f
    
    // 绘制垂直线
    for (i in 0..8) {
        val x = i * cellWidth + cellWidth / 2
        drawLine(
            color = Color.Black,
            start = Offset(x, cellHeight / 2),
            end = Offset(x, size.height - cellHeight / 2),
            strokeWidth = strokeWidth
        )
    }
    
    // 绘制水平线
    for (i in 0..9) {
        val y = i * cellHeight + cellHeight / 2
        val startX = if (i == 0 || i == 9) cellWidth / 2 else 0f
        val endX = if (i == 0 || i == 9) size.width - cellWidth / 2 else size.width
        
        drawLine(
            color = Color.Black,
            start = Offset(startX, y),
            end = Offset(endX, y),
            strokeWidth = strokeWidth
        )
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
        strokeWidth = strokeWidth
    )
    
    // 黑方九宫格
    drawPalace(
        left = cellWidth * 3.5f,
        top = cellHeight * 7.5f,
        right = cellWidth * 5.5f,
        bottom = cellHeight * 9.5f,
        strokeWidth = strokeWidth
    )
}

private fun DrawScope.drawPalace(
    left: Float,
    top: Float,
    right: Float,
    bottom: Float,
    strokeWidth: Float
) {
    val centerX = (left + right) / 2
    val centerY = (top + bottom) / 2
    
    // 绘制对角线
    drawLine(
        color = Color.Black,
        start = Offset(left, top),
        end = Offset(right, bottom),
        strokeWidth = strokeWidth
    )
    
    drawLine(
        color = Color.Black,
        start = Offset(right, top),
        end = Offset(left, bottom),
        strokeWidth = strokeWidth
    )
}

private fun DrawScope.drawRiver(cellWidth: Float, cellHeight: Float) {
    val riverY = cellHeight * 4.5f
    val strokeWidth = 3f
    
    // 绘制河界线
    drawLine(
        color = Color.Black,
        start = Offset(0f, riverY),
        end = Offset(size.width, riverY),
        strokeWidth = strokeWidth
    )
    
    // 绘制"楚河汉界"文字
    drawContext.canvas.nativeCanvas.apply {
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 24f
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
        }
        
        val textY = riverY + 15f
        drawText("楚河", cellWidth * 2, textY, paint)
        drawText("汉界", cellWidth * 7, textY, paint)
    }
}

@Composable
fun GameInfoPanel(
    currentPlayer: com.chinesechess.app.data.model.PieceColor,
    gameState: com.chinesechess.app.data.model.GameState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "当前回合：${currentPlayer.displayName}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (currentPlayer == com.chinesechess.app.data.model.PieceColor.RED) 
                    ChessRedPiece else ChessBlackPiece
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            when (gameState) {
                com.chinesechess.app.data.model.GameState.PLAYING -> {
                    Text(
                        text = "游戏进行中",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                com.chinesechess.app.data.model.GameState.PAUSED -> {
                    Text(
                        text = "游戏暂停",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                com.chinesechess.app.data.model.GameState.RED_WIN -> {
                    Text(
                        text = "红方获胜！",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = ChessRedPiece
                    )
                }
                com.chinesechess.app.data.model.GameState.BLACK_WIN -> {
                    Text(
                        text = "黑方获胜！",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = ChessBlackPiece
                    )
                }
                com.chinesechess.app.data.model.GameState.DRAW -> {
                    Text(
                        text = "和棋",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}