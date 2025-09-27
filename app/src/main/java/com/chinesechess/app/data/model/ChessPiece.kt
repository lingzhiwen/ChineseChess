package com.chinesechess.app.data.model

import androidx.compose.ui.graphics.Color

/**
 * 象棋棋子类型
 */
enum class PieceType(val displayName: String, val value: Int) {
    GENERAL("将/帅", 1),
    ADVISOR("士", 2),
    ELEPHANT("象/相", 3),
    HORSE("马", 4),
    CHARIOT("车", 5),
    CANNON("炮", 6),
    SOLDIER("兵/卒", 7)
}

/**
 * 棋子颜色（红方/黑方）
 */
enum class PieceColor(val displayName: String, val color: Color) {
    RED("红方", Color(0xFFDC143C)),
    BLACK("黑方", Color(0xFF2F2F2F))
}

/**
 * 象棋棋子数据类
 */
data class ChessPiece(
    val type: PieceType,
    val color: PieceColor,
    val row: Int,
    val col: Int,
    val id: String = "${color.name}_${type.name}_${row}_${col}"
) {
    fun isRed(): Boolean = color == PieceColor.RED
    fun isBlack(): Boolean = color == PieceColor.BLACK
    
    fun copy(newRow: Int, newCol: Int): ChessPiece {
        return copy(row = newRow, col = newCol)
    }
}

/**
 * 移动结果
 */
sealed class MoveResult {
    object Valid : MoveResult()
    object Invalid : MoveResult()
    object Check : MoveResult()
    object Checkmate : MoveResult()
    object Stalemate : MoveResult()
    data class Capture(val capturedPiece: ChessPiece) : MoveResult()
}
