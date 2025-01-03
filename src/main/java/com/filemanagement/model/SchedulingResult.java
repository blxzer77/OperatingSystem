package com.filemanagement.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 磁盘调度结果类
 * 用于封装磁盘调度算法执行后的结果信息
 * 包含两个主要信息：
 * 1. 磁道的访问顺序
 * 2. 磁头的总移动道数
 * 
 * 该类提供了结果的存储、访问和格式化输出功能
 */
@Getter
@Setter
public class SchedulingResult {
    // 按访问顺序存储的磁道号数组
    private int[] accessSequence;
    // 磁头移动的总道数，用于评估算法性能
    private int totalMovement;

    /**
     * 构造函数
     * 
     * @param accessSequence 磁道访问顺序数组
     * @param totalMovement 总移动道数
     */
    public SchedulingResult(int[] accessSequence, int totalMovement) {
        this.accessSequence = accessSequence;
        this.totalMovement = totalMovement;
    }

    /**
     * 获取磁道访问序列
     * 将内部数组转换为List形式，便于外部处理
     * 
     * @return 包含访问顺序的整数列表
     */
    public List<Integer> getAccessSequence() {
        List<Integer> result = new ArrayList<>();
        for (int track : accessSequence) {
            result.add(track);
        }
        return result;
    }

    /**
     * 将调度结果转换为字符串形式
     * 格式化输出包括：
     * 1. 第一行：磁道访问顺序，空格分隔
     * 2. 第二行：总移动道数
     * 
     * @return 格式化的结果字符串
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        // 输出磁道访问顺序，用空格分隔
        for (int track : accessSequence) {
            result.append(track).append(" ");
        }
        result.append("\n");
        // 输出总移动道数
        result.append("总移动道数: ").append(totalMovement);
        return result.toString();
    }
} 