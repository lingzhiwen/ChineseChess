package com.chinesechess.app.data.model

/**
 * 象棋棋盘状态
 */
data class ChessBoard(
    val pieces: List<ChessPiece> = initialPieces(),
    val currentPlayer: PieceColor = PieceColor.RED,
    val gameState: GameState = GameState.PLAYING,
    val lastMove: Move? = null,
    val selectedPiece: ChessPiece? = null,
    val validMoves: List<Position> = emptyList()
) {
    fun getPieceAt(row: Int, col: Int): ChessPiece? {
        return pieces.find { it.row == row && it.col == col }
    }
    
    fun isEmpty(row: Int, col: Int): Boolean {
        return getPieceAt(row, col) == null
    }
    
    fun isInBounds(row: Int, col: Int): Boolean {
        return row in 0..9 && col in 0..8
    }
    
    fun isInRedPalace(row: Int, col: Int): Boolean {
        return row in 0..2 && col in 3..5
    }
    
    fun isInBlackPalace(row: Int, col: Int): Boolean {
        return row in 7..9 && col in 3..5
    }
    
    fun isInPalace(row: Int, col: Int, color: PieceColor): Boolean {
        return if (color == PieceColor.RED) isInRedPalace(row, col) else isInBlackPalace(row, col)
    }
    
    fun isCrossedRiver(row: Int, color: PieceColor): Boolean {
        return if (color == PieceColor.RED) row > 4 else row < 5
    }
}

/**
 * 位置坐标
 */
data class Position(
    val row: Int,
    val col: Int
)

/**
 * 移动记录
 */
data class Move(
    val from: Position,
    val to: Position,
    val piece: ChessPiece,
    val capturedPiece: ChessPiece? = null
)

/**
 * 游戏状态
 */
enum class GameState {
    PLAYING,
    PAUSED,
    RED_WIN,
    BLACK_WIN,
    DRAW
}

/**
 * 初始化棋盘棋子
 */
private fun initialPieces(): List<ChessPiece> {
    val pieces = mutableListOf<ChessPiece>()
    
    // 红方棋子（下方）
    pieces.addAll(listOf(
        // 兵
        ChessPiece(PieceType.SOLDIER, PieceColor.RED, 3, 0),
        ChessPiece(PieceType.SOLDIER, PieceColor.RED, 3, 2),
        ChessPiece(PieceType.SOLDIER, PieceColor.RED, 3, 4),
        ChessPiece(PieceType.SOLDIER, PieceColor.RED, 3, 6),
        ChessPiece(PieceType.SOLDIER, PieceColor.RED, 3, 8),
        // 炮
        ChessPiece(PieceType.CANNON, PieceColor.RED, 2, 1),
        ChessPiece(PieceType.CANNON, PieceColor.RED, 2, 7),
        // 车
        ChessPiece(PieceType.CHARIOT, PieceColor.RED, 0, 0),
        ChessPiece(PieceType.CHARIOT, PieceColor.RED, 0, 8),
        // 马
        ChessPiece(PieceType.HORSE, PieceColor.RED, 0, 1),
        ChessPiece(PieceType.HORSE, PieceColor.RED, 0, 7),
        // 象
        ChessPiece(PieceType.ELEPHANT, PieceColor.RED, 0, 2),
        ChessPiece(PieceType.ELEPHANT, PieceColor.RED, 0, 6),
        // 士
        ChessPiece(PieceType.ADVISOR, PieceColor.RED, 0, 3),
        ChessPiece(PieceType.ADVISOR, PieceColor.RED, 0, 5),
        // 帅
        ChessPiece(PieceType.GENERAL, PieceColor.RED, 0, 4)
    ))
    
    // 黑方棋子（上方）
    pieces.addAll(listOf(
        // 卒
        ChessPiece(PieceType.SOLDIER, PieceColor.BLACK, 6, 0),
        ChessPiece(PieceType.SOLDIER, PieceColor.BLACK, 6, 2),
        ChessPiece(PieceType.SOLDIER, PieceColor.BLACK, 6, 4),
        ChessPiece(PieceType.SOLDIER, PieceColor.BLACK, 6, 6),
        ChessPiece(PieceType.SOLDIER, PieceColor.BLACK, 6, 8),
        // 炮
        ChessPiece(PieceType.CANNON, PieceColor.BLACK, 7, 1),
        ChessPiece(PieceType.CANNON, PieceColor.BLACK, 7, 7),
        // 车
        ChessPiece(PieceType.CHARIOT, PieceColor.BLACK, 9, 0),
        ChessPiece(PieceType.CHARIOT, PieceColor.BLACK, 9, 8),
        // 马
        ChessPiece(PieceType.HORSE, PieceColor.BLACK, 9, 1),
        ChessPiece(PieceType.HORSE, PieceColor.BLACK, 9, 7),
        // 象
        ChessPiece(PieceType.ELEPHANT, PieceColor.BLACK, 9, 2),
        ChessPiece(PieceType.ELEPHANT, PieceColor.BLACK, 9, 6),
        // 士
        ChessPiece(PieceType.ADVISOR, PieceColor.BLACK, 9, 3),
        ChessPiece(PieceType.ADVISOR, PieceColor.BLACK, 9, 5),
        // 将
        ChessPiece(PieceType.GENERAL, PieceColor.BLACK, 9, 4)
    ))
    
    return pieces
}
