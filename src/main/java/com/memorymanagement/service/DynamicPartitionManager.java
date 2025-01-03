package com.memorymanagement.service;

import com.memorymanagement.model.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 可变分区管理器
 * 实现动态分区分配方式的内存管理
 * 
 * 特点：
 * 1. 根据进程需要，动态分配内存空间
 * 2. 支持分区的分割和合并
 * 3. 使用首次适应算法进行分区分配
 * 4. 自动处理内存碎片
 * 
 * 内存布局：
 * - 0~4K：操作系统区域
 * - 4K~128K：用户区域（可分配区域）
 */
public class DynamicPartitionManager implements MemoryManager {
    // 操作系统占用空间大小（4K）
    private static final int OS_SIZE = 4 * 1024;
    // 总内存大小（128K）
    private static final int TOTAL_MEMORY = 128 * 1024;
    // 已分配分区列表
    private List<AllocatedPartition> allocatedPartitions;
    // 空闲分区列表
    private List<FreePartition> freePartitions;

    /**
     * 构造函数
     * 初始化内存管理器，创建初始的空闲分区
     * 初始状态：
     * 1. 创建空的已分配分区列表
     * 2. 创建空闲分区列表
     * 3. 初始化一个大的空闲分区（除去OS区域的所有空间）
     */
    public DynamicPartitionManager() {
        allocatedPartitions = new ArrayList<>();
        freePartitions = new ArrayList<>();
        initializeMemory();
    }

    /**
     * 初始化内存空间
     * 创建一个初始的大空闲分区，起始地址为OS_SIZE，
     * 大小为总内存减去OS占用的空间
     */
    private void initializeMemory() {
        freePartitions.add(new FreePartition(OS_SIZE, TOTAL_MEMORY - OS_SIZE));
    }

    /**
     * 分配内存空间
     * 使用首次适应算法，在空闲分区中查找第一个足够大的分区
     * 
     * @param jobName 作业名称
     * @param size 申请的空间大小
     * @return 是否分配成功
     */
    @Override
    public boolean allocateMemory(String jobName, int size) {
        // 验证申请的空间大小是否有效
        if (size <= 0) {
            return false;
        }
        
        // 遍历空闲分区列表，查找第一个足够大的分区
        for (int i = 0; i < freePartitions.size(); i++) {
            FreePartition freePartition = freePartitions.get(i);
            if (freePartition.getSize() >= size) {
                // 在找到的空闲分区中创建新的已分配分区
                AllocatedPartition allocatedPartition = new AllocatedPartition(
                        freePartition.getStartAddress(),
                        size,
                        jobName
                );
                allocatedPartitions.add(allocatedPartition);

                // 处理剩余空间
                if (freePartition.getSize() > size) {
                    // 如果空闲分区大于申请空间，分割分区
                    FreePartition newFreePartition = new FreePartition(
                            freePartition.getStartAddress() + size,
                            freePartition.getSize() - size
                    );
                    freePartitions.set(i, newFreePartition);
                } else {
                    // 如果空闲分区大小正好，直接移除该空闲分区
                    freePartitions.remove(i);
                }
                return true;
            }
        }
        // 没有找到合适的分区
        return false;
    }

    /**
     * 释放内存空间
     * 将指定作业占用的内存空间释放，并尝试与相邻的空闲分区合并
     * 
     * @param jobName 要释放内存的作业名称
     * @return 是否释放成功
     */
    @Override
    public boolean releaseMemory(String jobName) {
        // 查找要释放的已分配分区
        AllocatedPartition partitionToRelease = null;
        for (int i = 0; i < allocatedPartitions.size(); i++) {
            if (allocatedPartitions.get(i).getJobName().equals(jobName)) {
                partitionToRelease = allocatedPartitions.remove(i);
                break;
            }
        }

        // 如果没有找到对应的分区，返回失败
        if (partitionToRelease == null) {
            return false;
        }

        // 创建新的空闲分区
        FreePartition newFreePartition = new FreePartition(
                partitionToRelease.getStartAddress(),
                partitionToRelease.getSize()
        );

        // 尝试与相邻的空闲分区合并
        mergePartitions(newFreePartition);
        return true;
    }

    /**
     * 合并相邻的空闲分区
     * 检查新释放的分区是否与现有的空闲分区相邻，如果相邻则合并
     * 
     * @param newFreePartition 新释放的空闲分区
     */
    private void mergePartitions(FreePartition newFreePartition) {
        List<FreePartition> mergedPartitions = new ArrayList<>();
        int newStart = newFreePartition.getStartAddress();
        int newSize = newFreePartition.getSize();

        // 遍历现有空闲分区，查找可以合并的分区
        for (FreePartition partition : freePartitions) {
            if (partition.getStartAddress() + partition.getSize() == newStart) {
                // 当前分区的结束地址等于新分区的起始地址，可以向后合并
                newStart = partition.getStartAddress();
                newSize += partition.getSize();
                mergedPartitions.add(partition);
            } else if (newStart + newSize == partition.getStartAddress()) {
                // 新分区的结束地址等于当前分区的起始地址，可以向前合并
                newSize += partition.getSize();
                mergedPartitions.add(partition);
            }
        }

        // 从空闲分区列表中移除已合并的分区
        freePartitions.removeAll(mergedPartitions);
        // 添加合并后的新空闲分区
        freePartitions.add(new FreePartition(newStart, newSize));
    }

    /**
     * 显示当前内存使用状态
     * 输出所有分区的信息，包括已分配和空闲分区
     */
    @Override
    public void displayMemoryStatus() {
        // 具体实现在GUI中完成
    }

    /**
     * 获取当前内存状态
     * 返回按起始地址排序的所有分区列表
     * 
     * @return 包含所有分区（已分配和空闲）的列表
     */
    @Override
    public List<Partition> getMemoryStatus() {
        List<Partition> allPartitions = new ArrayList<>();
        // 将已分配和空闲分区合并到一个列表
        allPartitions.addAll(allocatedPartitions);
        allPartitions.addAll(freePartitions);
        // 按起始地址排序
        allPartitions.sort((p1, p2) -> p1.getStartAddress() - p2.getStartAddress());
        return allPartitions;
    }
}