# 故障排除指南

## Gradle 配置错误

如果您遇到以下错误：
```
A problem occurred configuring project ':app'.
> Failed to notify project evaluation listener.
> 'org.gradle.api.file.FileCollection org.gradle.api.artifacts.Configuration.fileCollection(org.gradle.api.specs.Spec)'
```

### 解决方案

#### 方法1：使用传统Gradle文件
1. 删除 `build.gradle.kts` 和 `app/build.gradle.kts`
2. 使用提供的 `build.gradle` 和 `app/build.gradle` 文件
3. 重新同步项目

#### 方法2：清理和重建
```bash
# 清理项目
./gradlew clean

# 删除Gradle缓存
rm -rf ~/.gradle/caches/

# 重新构建
./gradlew build
```

#### 方法3：降级版本
如果问题持续，请使用以下版本组合：
- Android Gradle Plugin: 7.4.2
- Gradle: 7.5
- Kotlin: 1.7.10

#### 方法4：检查Android Studio版本
确保使用 Android Studio Flamingo (2022.2.1) 或更新版本。

## 常见问题

### Q: 编译时出现 "Unresolved reference" 错误
A: 确保所有依赖版本兼容，特别是 Compose 和 Hilt 版本。

### Q: 运行时崩溃
A: 检查是否在 AndroidManifest.xml 中正确配置了 Application 类。

### Q: 3D效果不显示
A: 确保设备支持 OpenGL ES 2.0，或者使用简化的2D版本。

## 联系支持

如果问题仍然存在，请提供：
1. Android Studio 版本
2. Gradle 版本
3. 完整的错误日志
4. 操作系统信息
