package com.chinesechess.app

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chinesechess.app.data.model.GameState
import com.chinesechess.app.ui.components.*
import com.chinesechess.app.ui.viewmodel.ChessViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.chinesechess.app.ui.theme.*

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
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        BackgroundGradientStart,
                        BackgroundGradientEnd,
                        BackgroundGradientStart,
                        BackgroundGradientEnd
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            // 精美标题
            AnimatedTitle()
            
            Spacer(modifier = Modifier.height(8.dp))
            
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
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AnimatedTitle() {
    // 标题动画
    val infiniteTransition = rememberInfiniteTransition(label = "titleTransition")
    
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerAlpha"
    )
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "titleScale"
    )
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
    ) {
        // 主标题
        Text(
            text = "中国象棋",
            fontSize = 48.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.displayLarge.copy(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        ChineseRed,
                        VermilionRed,
                        ChineseRed
                    )
                )
            ),
            modifier = Modifier.alpha(shimmerAlpha)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // 副标题
        Text(
            text = "Chinese Chess",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            letterSpacing = 2.sp
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 装饰线
        Divider(
            modifier = Modifier
                .width(120.dp)
                .alpha(shimmerAlpha),
            thickness = 3.dp,
            color = ImperialGold
        )
    }
}
