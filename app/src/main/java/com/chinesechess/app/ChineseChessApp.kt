package com.chinesechess.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chinesechess.app.data.model.GameState
import com.chinesechess.app.ui.components.*
import com.chinesechess.app.ui.viewmodel.ChessViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ChineseChessApp(
    modifier: Modifier = Modifier,
    viewModel: ChessViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    LaunchedEffect(uiState.message) {
        if (uiState.message != null) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearMessage()
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 标题
        Text(
            text = "中国象棋",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        
        // 游戏信息面板
        GameInfoPanel(
            currentPlayer = uiState.board.currentPlayer,
            gameState = uiState.board.gameState
        )
        
        // 棋盘
        ChessBoardView(
            board = uiState.board,
            onPieceClick = { row, col ->
                viewModel.selectPiece(row, col)
            }
        )
        
        // 游戏控制面板
        GameControls(
            gameState = uiState.board.gameState,
            onStartNewGame = { viewModel.startNewGame() },
            onPauseGame = { viewModel.pauseGame() },
            onResumeGame = { viewModel.resumeGame() }
        )
        
        // 消息提示
        MessageSnackbar(
            message = uiState.message,
            onDismiss = { viewModel.clearMessage() }
        )
    }
}