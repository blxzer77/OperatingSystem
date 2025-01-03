# 操作系统课程设计项目

## 项目简介
本项目是一个操作系统核心概念的模拟实现，包含进程管理、存储管理、虚拟内存管理和文件管理四个主要模块。项目使用Java语言开发，提供图形化界面，帮助理解操作系统的基本原理和算法实现。

## 项目结构
```
OperatingSystem/
├── src/
│   ├── main/java/com/
│   │   ├── processmanagement/     # 进程管理模块
│   │   ├── memorymanagement/      # 存储管理模块
│   │   ├── virtualmemorymanagement/ # 虚拟内存管理模块
│   │   └── filemanagement/        # 文件管理模块
│   └── test/java/com/            # 测试代码目录
├── pom.xml                       # Maven配置文件
└── README.md                     # 项目说明文档
```

## 模块说明

### 1. 进程管理模块 (processmanagement)
- **功能**: 实现进程调度和管理
- **主要组件**:
  - `model/`: PCB和进程状态定义
  - `scheduler/`: 各种调度算法实现
  - `gui/`: 进程管理可视化界面
- **实现算法**:
  - FCFS (先来先服务)
  - SJF (短作业优先)
  - Priority (优先级调度)
  - RR (时间片轮转)

### 2. 存储管理模块 (memorymanagement)
- **功能**: 实现内存分配和回收
- **主要组件**:
  - `model/`: 分区和内存块定义
  - `service/`: 内存管理服务
  - `gui/`: 内存使用可视化
- **实现算法**:
  - 固定分区管理
  - 动态分区管理
  - 内存碎片整理

### 3. 虚拟内存管理模块 (virtualmemorymanagement)
- **功能**: 实现页面置换算法
- **主要组件**:
  - `model/`: 页面和页表定义
  - `algorithm/`: 置换算法实现
  - `gui/`: 页面置换过程可视化
- **实现算法**:
  - FIFO (先进先出)
  - LRU (最近最少使用)
  - LFU (最不经常使用)

### 4. 文件管理模块 (filemanagement)
- **功能**: 实现磁盘调度算法
- **主要组件**:
  - `model/`: 磁道和调度结果定义
  - `algorithm/`: 磁头调度算法
  - `gui/`: 磁头移动可视化
- **实现算法**:
  - FCFS (先来先服务)
  - SSTF (最短寻道时间优先)
  - SCAN (电梯算法)

## 技术栈
- 开发语言: Java
- 构建工具: Maven
- GUI框架: Java Swing
- 测试框架: JUnit 5

## 运行要求
- JDK 11或更高版本
- Maven 3.6或更高版本

## 如何运行
1. 克隆项目
```bash
git clone https://github.com/blxzer77/OperatingSystem.git
```

2. 编译项目
```bash
mvn clean install
```

3. 运行各模块
```bash
# 运行进程管理模块
java -jar target/processmanagement.jar

# 运行存储管理模块
java -jar target/memorymanagement.jar

# 运行虚拟内存管理模块
java -jar target/virtualmemorymanagement.jar

# 运行文件管理模块
java -jar target/filemanagement.jar
```

## 测试
运行所有测试:
```bash
mvn test
```

## 项目特点
1. 模块化设计，各模块独立运行
2. 完整的图形化界面
3. 详细的算法实现和注释
4. 完善的测试用例
5. 可视化的算法执行过程

## 文档
- 每个模块都有详细的注释文档
- 包含算法原理说明
- 提供完整的测试报告
- 有用户使用指南

## 贡献
欢迎提交问题和改进建议。

## 作者
佛山大学-22计算机科学与技术1班-梁书玮
