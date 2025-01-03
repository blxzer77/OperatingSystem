package com.filemanagement.algorithm;

import com.filemanagement.model.SchedulingResult;
import java.util.Arrays;

/**
 * 先来先服务(FCFS)磁盘调度算法
 * 
 * 算法特点：
 * 1. 按照磁道访问请求的先后顺序进行调度
 * 2. 公平、简单，但可能导致磁头移动距离较大
 * 3. 没有任何优化，完全按照请求顺序处理
 * 
 * 优点：
 * - 实现简单，开销小
 * - 公平性好，不会出现饥饿现象
 * 
 * 缺点：
 * - 平均寻道时间可能较长
 * - 相邻磁道的请求可能不能被连续处理
 */
public class FCFSAlgorithm implements DiskSchedulingAlgorithm {
    
    /**
     * 执行FCFS磁盘调度算法
     * 
     * @param currentPosition 当前磁头位置
     * @param trackSequence 待访问的磁道序列
     * @return 包含访问顺序和总移动距离的调度结果
     */
    @Override
    public SchedulingResult schedule(int currentPosition, int[] trackSequence) {
        // 记录总移动道数
        int totalMovement = 0;
        // 记录当前磁头位置
        int currentTrack = currentPosition;
        // 用于存储访问顺序的数组
        int[] accessSequence = new int[trackSequence.length];
        
        // 按照请求顺序依次访问每个磁道
        for (int i = 0; i < trackSequence.length; i++) {
            // 获取下一个要访问的磁道号
            int nextTrack = trackSequence[i];
            // 计算当前位置到下一个磁道的距离（移动道数）
            totalMovement += Math.abs(nextTrack - currentTrack);
            // 更新当前磁头位置
            currentTrack = nextTrack;
            // 将访问的磁道号记录到访问序列中
            accessSequence[i] = nextTrack;
        }

        // 返回调度结果，包含访问顺序和总移动道数
        return new SchedulingResult(accessSequence, totalMovement);
    }
} 