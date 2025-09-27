# 中国象棋 Android 客户端

一个使用 Kotlin + Jetpack Compose 开发的现代化中国象棋 Android 应用，具有美观的3D棋子效果。

## ⚠️ 重要说明

如果遇到 Gradle 配置错误，请按照以下步骤解决：

1. **清理项目**：
   ```bash
   ./gradlew clean
   ```

2. **删除 Gradle 缓存**：
   ```bash
   rm -rf ~/.gradle/caches/
   ```

3. **重新同步项目**：
   在 Android Studio 中点击 "Sync Project with Gradle Files"

4. **如果仍有问题，请使用以下版本**：
   - Android Gradle Plugin: 8.0.2
   - Gradle: 7.6.1
   - Kotlin: 1.8.10

## 功能特性

### 🎮 游戏功能
- ✅ 完整的中国象棋规则实现
- ✅ 红方/黑方轮流对弈
- ✅ 所有棋子的正确移动规则
- ✅ 将军、将死检测
- ✅ 游戏状态管理（开始、暂停、重新开始）

### 🎨 界面设计
- ✅ 现代化 Material Design 3 界面
- ✅ 3D立体棋子效果
- ✅ 流畅的动画和交互
- ✅ 响应式布局设计
- ✅ 美观的棋盘和UI组件

### 🔧 技术特性
- ✅ Kotlin + Jetpack Compose
- ✅ MVVM 架构模式
- ✅ Hilt 依赖注入
- ✅ 状态管理
- ✅ 自定义3D绘制效果

## 技术栈

- **语言**: Kotlin
- **UI框架**: Jetpack Compose
- **架构**: MVVM + Repository
- **依赖注入**: Hilt
- **状态管理**: StateFlow + ViewModel
- **3D渲染**: Canvas + 自定义绘制
- **动画**: Compose Animation

## 项目结构

```
app/
├── src/main/java/com/chinesechess/app/
│   ├── data/
│   │   ├── model/           # 数据模型
│   │   │   ├── ChessPiece.kt
│   │   │   └── ChessBoard.kt
│   │   └── engine/          # 游戏逻辑引擎
│   │       └── ChessEngine.kt
│   ├── ui/
│   │   ├── components/      # UI组件
│   │   │   ├── ChessBoard.kt
│   │   │   ├── ChessPiece3D.kt
│   │   │   └── GameControls.kt
│   │   ├── theme/           # 主题配置
│   │   │   ├── Color.kt
│   │   │   ├── Theme.kt
│   │   │   └── Type.kt
│   │   └── viewmodel/       # ViewModel
│   │       └── ChessViewModel.kt
│   ├── MainActivity.kt
│   ├── ChineseChessApp.kt
│   └── ChineseChessApplication.kt
└── src/main/res/            # 资源文件
    ├── values/
    │   ├── colors.xml
    │   ├── strings.xml
    │   └── themes.xml
    └── xml/
        ├── backup_rules.xml
        └── data_extraction_rules.xml
```

## 核心组件

### 1. 数据模型 (Data Model)
- `ChessPiece`: 棋子数据类，包含类型、颜色、位置等信息
- `ChessBoard`: 棋盘状态管理，包含所有棋子和游戏状态
- `Position`: 位置坐标
- `Move`: 移动记录

### 2. 游戏引擎 (Chess Engine)
- `ChessEngine`: 核心游戏逻辑，包含：
  - 移动规则验证
  - 将军/将死检测
  - 合法移动计算
  - 游戏状态判断

### 3. UI组件 (UI Components)
- `ChessBoardView`: 棋盘主视图
- `ChessPiece3D`: 3D立体棋子组件
- `GameControls`: 游戏控制面板
- `GameInfoPanel`: 游戏信息显示

### 4. 3D效果实现
使用 Canvas 和自定义绘制实现3D效果：
- 径向渐变模拟光照
- 高光和阴影效果
- 浮动动画
- 选中状态动画

## 安装和运行

1. 克隆项目到本地
2. 使用 Android Studio 打开项目
3. 同步 Gradle 依赖
4. 连接 Android 设备或启动模拟器
5. 运行应用

## 系统要求

- Android 7.0 (API 24) 或更高版本
- OpenGL ES 2.0 支持（用于3D效果）

## 开发计划

- [ ] 添加音效和背景音乐
- [ ] 实现AI对手
- [ ] 添加游戏历史记录
- [ ] 支持网络对战
- [ ] 添加更多动画效果
- [ ] 支持主题切换

## 贡献

欢迎提交 Issue 和 Pull Request 来改进这个项目！

## 许可证

MIT License
