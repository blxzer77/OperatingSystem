package com.filemanagement.algorithm;

import com.filemanagement.model.SchedulingResult;

/**
 * 磁盘调度算法接口
 * 定义了所有磁盘调度算法必须实现的基本功能
 * 包括：
 * 1. FCFS (First Come First Serve) - 先来先服务算法
 * 2. SSTF (Shortest Seek Time First) - 最短寻道时间优先算法
 * 3. SCAN (电梯算法) - 单向扫描算法
 */
public interface DiskSchedulingAlgorithm {
    /**
     * 执行磁盘调度
     * 根据不同的调度策略，计算磁头的移动顺序和总寻道距离
     * 
     * @param currentPosition 当前磁头位置，表示磁头的起始磁道号
     * @param trackSequence 待访问的磁道序列，包含所有需要访问的磁道号
     * @return 调度结果，包含最终的访问顺序和总移动道数
     */
    SchedulingResult schedule(int currentPosition, int[] trackSequence);
} 