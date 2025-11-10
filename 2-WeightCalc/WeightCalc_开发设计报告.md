# WeightCalc 应用开发设计过程实验报告

## 1. 项目概述

### 1.1 项目背景
WeightCalc是一个基于Android平台的标准体重计算应用，旨在为用户提供一个简单、直观的工具来根据身高和性别计算标准体重。该项目采用现代Android开发技术，使用Kotlin语言和Jetpack Compose UI框架。

### 1.2 项目目标
- 开发一个用户友好的标准体重计算器
- 实现基于性别和身高的标准体重计算功能
- 提供简洁直观的用户界面
- 遵循现代Android开发最佳实践

## 2. 需求分析

### 2.1 功能需求
1. **性别选择功能**：用户可以选择男性或女性
2. **身高输入功能**：用户可以输入身高值（单位：厘米）
3. **计算功能**：根据选择的性别和输入的身高计算标准体重
4. **结果显示功能**：显示计算出的标准体重结果

### 2.2 非功能需求
1. **易用性**：界面简洁，操作直观
2. **响应性**：计算结果即时显示
3. **兼容性**：支持Android 7.0及以上版本
4. **可靠性**：输入验证和错误处理

### 2.3 计算公式
- 男性标准体重公式：(身高cm - 80) × 0.7
- 女性标准体重公式：(身高cm - 70) × 0.6

## 3. 系统设计

### 3.1 架构设计
项目采用单层架构设计，由于应用功能简单，不需要复杂的多层架构。主要组件包括：
- **MainActivity**：主活动，负责UI展示和用户交互
- **StandardWeightCalculator**：Composable函数，实现UI界面和计算逻辑

### 3.2 UI设计
采用Jetpack Compose构建响应式UI，界面元素包括：
- 性别选择区域（单选按钮组）
- 身高输入框（TextField）
- 计算按钮（Button）
- 结果显示区域（Text）

UI布局采用垂直排列的Column，所有元素居中对齐，确保界面美观和一致性。

### 3.3 状态管理
使用Jetpack Compose的状态管理机制：
- `remember`函数创建可变状态
- `mutableStateOf`定义响应式状态变量
- 状态变化自动触发UI重组

## 4. 技术选型

### 4.1 开发语言
- **Kotlin**：现代Android开发的首选语言，提供简洁的语法和强大的功能

### 4.2 UI框架
- **Jetpack Compose**：现代声明式UI工具包，简化UI开发
- **Material 3**：遵循最新的Material Design设计规范

### 4.3 构建工具
- **Gradle with Kotlin DSL**：使用Kotlin语法的构建脚本
- **Android Gradle Plugin 8.13.0**：最新的Android构建工具

### 4.4 依赖库
- AndroidX核心库
- Compose BOM（Bill of Materials）用于版本管理
- 测试框架（JUnit, Espresso）

## 5. 实现过程

### 5.1 项目初始化
1. 创建新的Android项目
2. 配置项目基本信息（包名、版本等）
3. 设置Kotlin和Compose支持

### 5.2 依赖配置
在`app/build.gradle.kts`中配置必要的依赖：
```kotlin
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    // 测试依赖...
}
```

### 5.3 UI实现
使用Jetpack Compose实现用户界面：

1. **主界面布局**：
   ```kotlin
   Column(
       modifier = Modifier.fillMaxSize().padding(16.dp),
       verticalArrangement = Arrangement.Center,
       horizontalAlignment = Alignment.CenterHorizontally
   ) {
       // UI组件
   }
   ```

2. **性别选择组件**：
   ```kotlin
   Row {
       Row(verticalAlignment = Alignment.CenterVertically) {
           RadioButton(
               selected = gender == "Male",
               onClick = { gender = "Male" }
           )
           Text("Male", modifier = Modifier.padding(end = 16.dp))
       }
       // 女性选项...
   }
   ```

3. **身高输入组件**：
   ```kotlin
   TextField(
       value = height,
       onValueChange = { height = it },
       label = { Text("Enter your height (cm)") },
       singleLine = true
   )
   ```

### 5.4 计算逻辑实现
在按钮点击事件中实现计算逻辑：
```kotlin
Button(onClick = {
    val h = height.toFloatOrNull()
    result = if (h != null) {
        val weight = if (gender == "Male") {
            (h - 80) * 0.7
        } else {
            (h - 70) * 0.6
        }
        "Standard weight: %.2f kg".format(weight)
    } else {
        "Please enter a valid height."
    }
}) {
    Text("View Result")
}
```

### 5.5 状态管理
使用Compose的状态管理机制：
```kotlin
var gender by remember { mutableStateOf("Male") }
var height by remember { mutableStateOf("") }
var result by remember { mutableStateOf("") }
```

## 6. 测试与验证

### 6.1 单元测试
创建基本的单元测试验证计算逻辑：
- 测试男性标准体重计算
- 测试女性标准体重计算
- 测试边界值处理

### 6.2 UI测试
使用Espresso和Compose测试工具进行UI测试：
- 测试用户交互流程
- 验证UI组件显示正确性
- 测试状态变化和UI更新

### 6.3 手动测试
在不同设备和Android版本上进行手动测试：
- 验证应用启动和基本功能
- 测试不同输入值的处理
- 验证界面响应性和布局适应性

## 7. 遇到的问题与解决方案

### 7.1 输入验证问题
**问题**：用户可能输入非数字字符导致计算错误
**解决方案**：使用`toFloatOrNull()`进行安全转换，并提供友好的错误提示

### 7.2 UI布局问题
**问题**：在不同屏幕尺寸上布局不一致
**解决方案**：使用Compose的响应式布局系统，设置适当的padding和spacing

### 7.3 状态管理问题
**问题**：状态变化时UI不更新
**解决方案**：确保使用`remember`和`mutableStateOf`正确管理状态

## 8. 项目总结与展望

### 8.1 项目成果
成功开发了一个功能完整、界面友好的标准体重计算应用，实现了所有预期功能：
- 性别选择功能
- 身高输入和验证
- 标准体重计算
- 结果显示

### 8.2 技术收获
- 掌握了Jetpack Compose的基本使用方法
- 理解了Compose状态管理机制
- 熟悉了现代Android开发流程
- 提升了Kotlin编程技能

### 8.3 未来改进方向
1. **功能扩展**：
   - 添加BMI计算功能
   - 支持不同单位系统（公制/英制）
   - 添加体重历史记录

2. **UI/UX改进**：
   - 添加动画效果
   - 支持深色模式
   - 优化界面布局

3. **技术优化**：
   - 采用MVVM架构
   - 添加数据持久化
   - 实现更全面的测试覆盖

## 9. 结论

WeightCalc项目成功展示了现代Android开发的技术和方法。通过使用Kotlin和Jetpack Compose，我们能够快速构建一个功能完整、用户友好的应用程序。项目过程中遇到的问题和解决方案也为未来的开发工作提供了宝贵的经验。

这个项目虽然功能简单，但涵盖了Android应用开发的核心概念，包括UI设计、状态管理、用户输入处理和基本计算逻辑，是学习现代Android开发的良好实践案例。