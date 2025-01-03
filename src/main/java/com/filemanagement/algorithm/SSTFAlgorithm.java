package com.filemanagement.algorithm;

import com.filemanagement.model.SchedulingResult;
import java.util.ArrayList;
import java.util.List;

/**
 * 最短寻道时间优先(SSTF)磁盘调度算法
 * 
 * 算法特点：
 * 1. 每次选择距离当前磁头位置最近的磁道进行访问
 * 2. 通过最小化寻道距离来优化性能
 * 3. 属于局部优化算法
 * 
 * 优点：
 * - 平均寻道时间较短
 * - 吞吐量较高
 * 
 * 缺点：
 * - 可能导致某些请求长期得不到服务（饥饿现象）
 * - 需要计算所有待访问磁道与当前位置的距离，开销较大
 */
public class SSTFAlgorithm implements DiskSchedulingAlgorithm {
    
    /**
     * 执行SSTF磁盘调度算法
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
        
        // 创建待访问磁道列表，用于动态维护未访问的磁道
        List<Integer> remainingTracks = new ArrayList<>();
        for (int track : trackSequence) {
            remainingTracks.add(track);
        }

        // 循环处理所有磁道访问请求
        for (int i = 0; i < trackSequence.length; i++) {
            // 找到距离当前磁头位置最近的待访问磁道
            int nearestTrack = findNearestTrack(currentTrack, remainingTracks);
            // 计算移动到最近磁道所需的道数
            totalMovement += Math.abs(nearestTrack - currentTrack);
            // 更新当前磁头位置为最近的磁道
            currentTrack = nearestTrack;
            // 将访问的磁道记录到访问序列中
            accessSequence[i] = nearestTrack;
            // 从待访问列表中移除已处理的磁道
            remainingTracks.remove(Integer.valueOf(nearestTrack));
        }

        // 返回调度结果，包含访问顺序和总移动道数
        return new SchedulingResult(accessSequence, totalMovement);
    }

    /**
     * 找到距离当前磁道最近的待访问磁道
     * 
     * @param currentTrack 当前磁头所在磁道
     * @param tracks 待访问的磁道列表
     * @return 距离当前磁道最近的待访问磁道号
     */
    private int findNearestTrack(int currentTrack, List<Integer> tracks) {
        // 初始化最近磁道为第一个待访问磁道
        int nearestTrack = tracks.get(0);
        // 初始化最小距离为当前位置到第一个磁道的距离
        int minDistance = Math.abs(currentTrack - nearestTrack);

        // 遍历所有待访问磁道，找到距离最短的那个
        for (int track : tracks) {
            int distance = Math.abs(currentTrack - track);
            // 如果找到更近的磁道，更新最小距离和最近磁道
            if (distance < minDistance) {
                minDistance = distance;
                nearestTrack = track;
            }
        }

        return nearestTrack;
    }
} 