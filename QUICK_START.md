# 快速开始指南

本指南帮助你快速了解美化后的中国象棋应用。

## 🎯 美化亮点一览

### 视觉效果
✨ **中国传统配色** - 中国红、帝王金、红木色
✨ **3D立体棋子** - 多层阴影、金属边缘、发光效果
✨ **精致棋盘** - 炮台标记、楚河汉界、波浪装饰
✨ **流畅动画** - 弹性缩放、脉冲发光、按压反馈

## 🚀 快速体验

### 运行应用

1. **使用 Android Studio**
   ```bash
   # 打开项目
   File -> Open -> 选择项目目录
   
   # 同步 Gradle
   Sync Project with Gradle Files
   
   # 运行
   Run 'app'
   ```

2. **使用命令行**
   ```bash
   # 清理构建
   ./gradlew clean
   
   # 构建 APK
   ./gradlew assembleDebug
   
   # 安装到设备
   ./gradlew installDebug
   ```

### 系统要求
- Android 7.0 (API 24) 或更高
- 建议 Android 12+ 以获得最佳效果

## 🎨 美化功能演示

### 1. 棋子交互
- **点击棋子** → 金色发光 + 放大 15% + 轻微旋转
- **可移动位置** → 绿色脉冲高亮
- **移动棋子** → 流畅过渡动画

### 2. 棋盘细节
- **炮台位置** → 四角标记
- **兵卒位置** → 四角标记
- **楚河汉界** → 蓝色波浪线 + 金色书法字
- **九宫格** → 金色对角线

### 3. 界面动画
- **标题** → 闪烁效果 + 缩放脉冲
- **按钮** → 按压缩放 + 图标脉冲
- **消息提示** → 淡入淡出 + 缩放进出

## 📂 主要文件说明

### 核心 UI 文件

```
app/src/main/java/com/chinesechess/app/ui/
├── theme/
│   ├── Color.kt          # 中国传统配色方案
│   ├── Theme.kt          # Material 3 主题配置
│   └── Type.kt           # 精致排版系统
├── components/
│   ├── ChessBoard.kt     # 美化的棋盘组件
│   ├── ChessPiece3D.kt   # 增强的 3D 棋子
│   └── GameControls.kt   # 精美的控制面板
└── ChineseChessApp.kt    # 主应用界面
```

### 资源文件

```
app/src/main/res/values/
├── colors.xml            # XML 颜色定义
├── themes.xml            # XML 主题配置
└── strings.xml           # 字符串资源
```

## 🎨 配色速查

### 主要颜色
- **中国红**: `#D2232A` - 红方、主色调
- **帝王金**: `#FFD700` - 选中高亮、装饰
- **红木色**: `#8B4513` - 棋盘边框
- **米色**: `#FFF8DC` - 棋盘底色

### 交互颜色
- **选中**: `#FFD700` (金色)
- **有效移动**: `#7FFF7F` (亮绿)
- **红方棋子**: `#DC143C` (深红)
- **黑方棋子**: `#2F2F2F` (深灰黑)

## 🔧 自定义美化

### 修改配色

编辑 `Color.kt`:
```kotlin
val ChineseRed = Color(0xFFD2232A)  // 修改为你喜欢的颜色
```

### 修改动画速度

编辑 `ChessPiece3D.kt`:
```kotlin
animationSpec = spring(
    dampingRatio = Spring.DampingRatioMediumBouncy,  // 弹性程度
    stiffness = Spring.StiffnessLow                  // 速度
)
```

### 修改棋子大小

编辑 `ChessBoard.kt`:
```kotlin
ChessPiece3D(
    ...,
    modifier = Modifier.size(36.dp)  // 调整尺寸
)
```

## 📊 性能优化提示

### 建议配置
- **启用硬件加速**: 默认已启用
- **使用真机测试**: 动画效果在真机上更流畅
- **避免过度绘制**: 已优化 Canvas 绘制

### 低配置设备优化

可以在 `ChessPiece3D.kt` 中减少效果:
```kotlin
// 减少阴影层数
val shadowLayers = 2  // 原值: 3

// 减少发光层数
for (i in 2 downTo 1) {  // 原值: 3 downTo 1
```

## 🐛 常见问题

### Q: 颜色显示不正确？
A: 确保设备支持 Android 12+ 的 Material You，或在 Theme.kt 中设置 `dynamicColor = false`

### Q: 动画卡顿？
A: 
1. 检查设备性能
2. 关闭开发者选项中的"动画缩放"
3. 使用真机而非模拟器

### Q: 棋盘显示异常？
A: 
1. 检查屏幕方向是否为竖屏
2. 确保布局没有被截断
3. 尝试调整 padding 值

## 📚 深入学习

### 详细文档
- [BEAUTIFICATION.md](BEAUTIFICATION.md) - 完整美化说明
- [README.md](README.md) - 项目总览
- [TROUBLESHOOTING.md](TROUBLESHOOTING.md) - 问题排查

### 相关技术
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Material Design 3](https://m3.material.io/)
- [Canvas 绘制](https://developer.android.com/jetpack/compose/graphics)
- [动画系统](https://developer.android.com/jetpack/compose/animation)

## 🤝 贡献代码

欢迎提交 Pull Request 来改进美化效果！

### 贡献建议
1. 保持配色一致性
2. 遵循 Material Design 3 规范
3. 确保动画流畅自然
4. 优化性能

## 📞 联系方式

有问题或建议？欢迎提交 Issue！

---

*享受精美的中国象棋体验！* 🎉

