package com.chinesechess.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chinesechess.app.data.engine.ChessEngine
import com.chinesechess.app.data.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChessViewModel @Inject constructor(
    private val chessEngine: ChessEngine
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ChessUiState())
    val uiState: StateFlow<ChessUiState> = _uiState.asStateFlow()
    
    fun selectPiece(row: Int, col: Int) {
        val currentState = _uiState.value
        val piece = currentState.board.getPieceAt(row, col)
        
        if (piece != null && piece.color == currentState.board.currentPlayer) {
            val validMoves = chessEngine.getValidMoves(currentState.board, piece)
            _uiState.value = currentState.copy(
                selectedPiece = piece,
                validMoves = validMoves
            )
        } else if (currentState.selectedPiece != null) {
            // 尝试移动棋子
            movePiece(currentState.selectedPiece.row, currentState.selectedPiece.col, row, col)
        }
    }
    
    fun movePiece(fromRow: Int, fromCol: Int, toRow: Int, toCol: Int) {
        val currentState = _uiState.value
        val from = Position(fromRow, fromCol)
        val to = Position(toRow, toCol)
        
        if (chessEngine.isValidMove(currentState.board, from, to)) {
            val newBoard = chessEngine.makeMove(currentState.board, from, to)
            
            // 检查游戏状态
            val isRedCheck = chessEngine.isCheck(newBoard, PieceColor.RED)
            val isBlackCheck = chessEngine.isCheck(newBoard, PieceColor.BLACK)
            val isRedCheckmate = chessEngine.isCheckmate(newBoard, PieceColor.RED)
            val isBlackCheckmate = chessEngine.isCheckmate(newBoard, PieceColor.BLACK)
            
            val gameState = when {
                isRedCheckmate -> GameState.BLACK_WIN
                isBlackCheckmate -> GameState.RED_WIN
                else -> GameState.PLAYING
            }
            
            val message = when {
                isRedCheckmate -> "黑方获胜！"
                isBlackCheckmate -> "红方获胜！"
                isRedCheck -> "红方被将军！"
                isBlackCheck -> "黑方被将军！"
                else -> null
            }
            
            _uiState.value = currentState.copy(
                board = newBoard.copy(gameState = gameState),
                selectedPiece = null,
                validMoves = emptyList(),
                message = message
            )
        } else {
            _uiState.value = currentState.copy(
                selectedPiece = null,
                validMoves = emptyList(),
                message = "无效移动"
            )
        }
    }
    
    fun startNewGame() {
        _uiState.value = ChessUiState()
    }
    
    fun pauseGame() {
        val currentState = _uiState.value
        if (currentState.board.gameState == GameState.PLAYING) {
            _uiState.value = currentState.copy(
                board = currentState.board.copy(gameState = GameState.PAUSED)
            )
        }
    }
    
    fun resumeGame() {
        val currentState = _uiState.value
        if (currentState.board.gameState == GameState.PAUSED) {
            _uiState.value = currentState.copy(
                board = currentState.board.copy(gameState = GameState.PLAYING)
            )
        }
    }
    
    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}

data class ChessUiState(
    val board: ChessBoard = ChessBoard(),
    val selectedPiece: ChessPiece? = null,
    val validMoves: List<Position> = emptyList(),
    val message: String? = null
)
