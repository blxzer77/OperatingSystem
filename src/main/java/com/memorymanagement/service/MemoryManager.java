package com.memorymanagement.service;

import com.memorymanagement.model.Partition;
import java.util.List;

/**
 * 内存管理器接口
 * 定义了内存管理系统的核心功能和基本操作
 * 
 * 主要功能：
 * 1. 内存空间的分配与回收
 * 2. 作业内存的管理
 * 3. 内存使用状态的监控
 * 
 * 实现类需要处理：
 * - 内存碎片问题
 * - 内存分配策略
 * - 内存回收和合并
 */
public interface MemoryManager {
    /**
     * 为作业分配内存空间
     * 
     * @param jobName 申请内存的作业名称
     * @param size 申请的内存空间大小（单位：KB）
     * @return 分配结果：true表示分配成功，false表示分配失败
     * 
     * 失败可能的原因：
     * 1. 没有足够的连续空闲空间
     * 2. 申请的空间大小超出系统限制
     * 3. 作业名称已存在
     */
    boolean allocateMemory(String jobName, int size);

    /**
     * 释放指定作业占用的内存空间
     * 
     * @param jobName 要释放内存的作业名称
     * @return 释放结果：true表示释放成功，false表示释放失败
     * 
     * 失败可能的原因：
     * 1. 作业不存在
     * 2. 作业已经释放
     * 3. 系统内部错误
     */
    boolean releaseMemory(String jobName);

    /**
     * 显示当前内存使用状态
     * 包括：
     * 1. 已分配分区的信息（起始地址、大小、占用作业）
     * 2. 空闲分区的信息（起始地址、大小）
     * 3. 内存碎片情况
     */
    void displayMemoryStatus();

    /**
     * 获取当前内存分区状态列表
     * 
     * @return 包含所有分区信息的列表，每个分区包含：
     *         - 起始地址
     *         - 分区大小
     *         - 分区状态（空闲/占用）
     *         - 占用作业名称（如果被占用）
     */
    List<Partition> getMemoryStatus();
}