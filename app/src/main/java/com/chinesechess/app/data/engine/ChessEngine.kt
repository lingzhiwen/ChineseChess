package com.chinesechess.app.data.engine

import com.chinesechess.app.data.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChessEngine @Inject constructor() {
    
    /**
     * 验证移动是否合法
     */
    fun isValidMove(board: ChessBoard, from: Position, to: Position): Boolean {
        val piece = board.getPieceAt(from.row, from.col) ?: return false
        
        // 检查目标位置是否在边界内
        if (!board.isInBounds(to.row, to.col)) return false
        
        // 检查是否移动到了相同位置
        if (from.row == to.row && from.col == to.col) return false
        
        // 检查目标位置是否有己方棋子
        val targetPiece = board.getPieceAt(to.row, to.col)
        if (targetPiece?.color == piece.color) return false
        
        // 根据棋子类型验证移动规则
        return when (piece.type) {
            PieceType.GENERAL -> isValidGeneralMove(board, piece, to)
            PieceType.ADVISOR -> isValidAdvisorMove(board, piece, to)
            PieceType.ELEPHANT -> isValidElephantMove(board, piece, to)
            PieceType.HORSE -> isValidHorseMove(board, piece, to)
            PieceType.CHARIOT -> isValidChariotMove(board, piece, to)
            PieceType.CANNON -> isValidCannonMove(board, piece, to)
            PieceType.SOLDIER -> isValidSoldierMove(board, piece, to)
        }
    }
    
    /**
     * 获取棋子的所有合法移动
     */
    fun getValidMoves(board: ChessBoard, piece: ChessPiece): List<Position> {
        val validMoves = mutableListOf<Position>()
        
        for (row in 0..9) {
            for (col in 0..8) {
                if (isValidMove(board, Position(piece.row, piece.col), Position(row, col))) {
                    validMoves.add(Position(row, col))
                }
            }
        }
        
        return validMoves
    }
    
    /**
     * 执行移动
     */
    fun makeMove(board: ChessBoard, from: Position, to: Position): ChessBoard {
        val piece = board.getPieceAt(from.row, from.col) ?: return board
        val capturedPiece = board.getPieceAt(to.row, to.col)
        
        val newPieces = board.pieces.toMutableList()
        
        // 移除被吃掉的棋子
        if (capturedPiece != null) {
            newPieces.remove(capturedPiece)
        }
        
        // 更新棋子位置
        val pieceIndex = newPieces.indexOfFirst { it.id == piece.id }
        if (pieceIndex != -1) {
            newPieces[pieceIndex] = piece.copy(to.row, to.col)
        }
        
        val move = Move(from, to, piece, capturedPiece)
        val newCurrentPlayer = if (board.currentPlayer == PieceColor.RED) PieceColor.BLACK else PieceColor.RED
        
        return board.copy(
            pieces = newPieces,
            currentPlayer = newCurrentPlayer,
            lastMove = move,
            selectedPiece = null,
            validMoves = emptyList()
        )
    }
    
    /**
     * 检查是否将军
     */
    fun isCheck(board: ChessBoard, color: PieceColor): Boolean {
        val general = board.pieces.find { it.type == PieceType.GENERAL && it.color == color }
            ?: return false
        
        val opponentPieces = board.pieces.filter { it.color != color }
        
        return opponentPieces.any { piece ->
            isValidMove(board, Position(piece.row, piece.col), Position(general.row, general.col))
        }
    }
    
    /**
     * 检查是否将死
     */
    fun isCheckmate(board: ChessBoard, color: PieceColor): Boolean {
        if (!isCheck(board, color)) return false
        
        val playerPieces = board.pieces.filter { it.color == color }
        
        return playerPieces.none { piece ->
            getValidMoves(board, piece).any { move ->
                val newBoard = makeMove(board, Position(piece.row, piece.col), move)
                !isCheck(newBoard, color)
            }
        }
    }
    
    // 各种棋子的移动规则验证
    private fun isValidGeneralMove(board: ChessBoard, piece: ChessPiece, to: Position): Boolean {
        if (!board.isInPalace(to.row, to.col, piece.color)) return false
        
        val rowDiff = kotlin.math.abs(to.row - piece.row)
        val colDiff = kotlin.math.abs(to.col - piece.col)
        
        return (rowDiff == 1 && colDiff == 0) || (rowDiff == 0 && colDiff == 1)
    }
    
    private fun isValidAdvisorMove(board: ChessBoard, piece: ChessPiece, to: Position): Boolean {
        if (!board.isInPalace(to.row, to.col, piece.color)) return false
        
        val rowDiff = kotlin.math.abs(to.row - piece.row)
        val colDiff = kotlin.math.abs(to.col - piece.col)
        
        return rowDiff == 1 && colDiff == 1
    }
    
    private fun isValidElephantMove(board: ChessBoard, piece: ChessPiece, to: Position): Boolean {
        val rowDiff = to.row - piece.row
        val colDiff = to.col - piece.col
        
        // 象不能过河
        if (piece.color == PieceColor.RED && to.row > 4) return false
        if (piece.color == PieceColor.BLACK && to.row < 5) return false
        
        // 象走田字
        if (kotlin.math.abs(rowDiff) != 2 || kotlin.math.abs(colDiff) != 2) return false
        
        // 检查象眼是否被堵
        val eyeRow = piece.row + rowDiff / 2
        val eyeCol = piece.col + colDiff / 2
        return board.isEmpty(eyeRow, eyeCol)
    }
    
    private fun isValidHorseMove(board: ChessBoard, piece: ChessPiece, to: Position): Boolean {
        val rowDiff = to.row - piece.row
        val colDiff = to.col - piece.col
        
        // 马走日字
        val absRowDiff = kotlin.math.abs(rowDiff)
        val absColDiff = kotlin.math.abs(colDiff)
        
        if (!((absRowDiff == 2 && absColDiff == 1) || (absRowDiff == 1 && absColDiff == 2))) {
            return false
        }
        
        // 检查马腿是否被堵
        val legRow = if (absRowDiff == 2) piece.row + rowDiff / 2 else piece.row
        val legCol = if (absColDiff == 2) piece.col + colDiff / 2 else piece.col
        
        return board.isEmpty(legRow, legCol)
    }
    
    private fun isValidChariotMove(board: ChessBoard, piece: ChessPiece, to: Position): Boolean {
        val rowDiff = to.row - piece.row
        val colDiff = to.col - piece.col
        
        // 车走直线
        if (rowDiff != 0 && colDiff != 0) return false
        
        // 检查路径是否被堵
        val stepRow = if (rowDiff != 0) rowDiff / kotlin.math.abs(rowDiff) else 0
        val stepCol = if (colDiff != 0) colDiff / kotlin.math.abs(colDiff) else 0
        
        var currentRow = piece.row + stepRow
        var currentCol = piece.col + stepCol
        
        while (currentRow != to.row || currentCol != to.col) {
            if (!board.isEmpty(currentRow, currentCol)) return false
            currentRow += stepRow
            currentCol += stepCol
        }
        
        return true
    }
    
    private fun isValidCannonMove(board: ChessBoard, piece: ChessPiece, to: Position): Boolean {
        val rowDiff = to.row - piece.row
        val colDiff = to.col - piece.col
        
        // 炮走直线
        if (rowDiff != 0 && colDiff != 0) return false
        
        // 检查路径
        val stepRow = if (rowDiff != 0) rowDiff / kotlin.math.abs(rowDiff) else 0
        val stepCol = if (colDiff != 0) colDiff / kotlin.math.abs(colDiff) else 0
        
        var currentRow = piece.row + stepRow
        var currentCol = piece.col + stepCol
        var jumpCount = 0
        
        while (currentRow != to.row || currentCol != to.col) {
            if (!board.isEmpty(currentRow, currentCol)) {
                jumpCount++
            }
            currentRow += stepRow
            currentCol += stepCol
        }
        
        val targetPiece = board.getPieceAt(to.row, to.col)
        return if (targetPiece == null) {
            jumpCount == 0 // 吃子需要翻山
        } else {
            jumpCount == 1 // 吃子需要翻山
        }
    }
    
    private fun isValidSoldierMove(board: ChessBoard, piece: ChessPiece, to: Position): Boolean {
        val rowDiff = to.row - piece.row
        val colDiff = to.col - piece.col
        
        // 兵只能向前走一步
        val forwardDirection = if (piece.color == PieceColor.RED) 1 else -1
        if (rowDiff != forwardDirection || colDiff != 0) {
            // 过河后可以左右走
            if (board.isCrossedRiver(piece.row, piece.color)) {
                return rowDiff == 0 && kotlin.math.abs(colDiff) == 1
            }
            return false
        }
        
        return true
    }
}
