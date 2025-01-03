package com.memorymanagement.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import com.memorymanagement.model.Partition;
import com.memorymanagement.model.AllocatedPartition;

/**
 * 内存管理测试类
 * 测试固定分区和可变分区两种内存分配方式：
 * 1. 固定分区：1K、1K、2K、4K、4K、8K、16K、88K
 * 2. 可变分区：动态分配，最小单位1K
 * 总内存空间：128K，其中前4K被OS占用
 */
public class MemoryManagerTest {
    private FixedPartitionManager fixedManager;
    private DynamicPartitionManager dynamicManager;
    private static final int TOTAL_MEMORY = 128 * 1024; // 128K
    private static final int OS_MEMORY = 4 * 1024;      // 4K

    @BeforeEach
    void setUp() {
        fixedManager = new FixedPartitionManager();
        dynamicManager = new DynamicPartitionManager();
    }

    @Test
    @DisplayName("测试固定分区基本分配")
    void testFixedPartitionBasicAllocation() {
        assertTrue(fixedManager.allocateMemory("Job1", 8 * 1024), 
                "应该能够在16K分区中分配8K内存");
        assertTrue(fixedManager.allocateMemory("Job2", 12 * 1024), 
                "应该能够在16K分区中分配12K内存");
        
        // 验证分区状态
        List<Partition> partitions = fixedManager.getMemoryStatus();
        assertFalse(partitions.isEmpty(), "分区列表不应为空");
        assertTrue(partitions.stream().anyMatch(p -> p instanceof AllocatedPartition 
                && ((AllocatedPartition)p).getJobName().equals("Job1")), 
                "应该能找到Job1的分区");
    }

    @Test
    @DisplayName("测试固定分区大分区分配")
    void testFixedPartitionLargeAllocation() {
        assertTrue(fixedManager.allocateMemory("Job1", 50 * 1024), 
                "应该能够在88K分区中分配50K内存");
        
        // 验证分区状态
        List<Partition> partitions = fixedManager.getMemoryStatus();
        assertTrue(partitions.stream().anyMatch(p -> p instanceof AllocatedPartition 
                && ((AllocatedPartition)p).getJobName().equals("Job1")), 
                "应该能找到Job1的分区");
    }

    @Test
    @DisplayName("测试固定分区超限分配")
    void testFixedPartitionOverAllocation() {
        assertFalse(fixedManager.allocateMemory("Job1", 100 * 1024), 
                "不应该能够分配超过最大分区大小的内存");
        
        // 验证分区状态
        List<Partition> partitions = fixedManager.getMemoryStatus();
        assertFalse(partitions.stream().anyMatch(p -> p instanceof AllocatedPartition 
                && ((AllocatedPartition)p).getJobName().equals("Job1")), 
                "不应该找到Job1的分区");
    }

    @Test
    @DisplayName("测试固定分区内存释放")
    void testFixedPartitionRelease() {
        // 先分配内存
        assertTrue(fixedManager.allocateMemory("Job1", 16 * 1024), 
                "应该能够分配16K内存");
        
        // 释放内存
        assertTrue(fixedManager.releaseMemory("Job1"), 
                "应该能够成功释放已分配的内存");
        assertFalse(fixedManager.releaseMemory("NonExistentJob"), 
                "不应该能够释放不存在的作业");
        
        // 验证分区状态
        List<Partition> partitions = fixedManager.getMemoryStatus();
        assertFalse(partitions.stream().anyMatch(p -> p instanceof AllocatedPartition 
                && ((AllocatedPartition)p).getJobName().equals("Job1")), 
                "不应该能找到已释放的Job1分区");
    }

    @Test
    @DisplayName("测试可变分区基本分配")
    void testDynamicPartitionBasicAllocation() {
        assertTrue(dynamicManager.allocateMemory("Job1", 10 * 1024), 
                "应该能够分配10K内存");
        assertTrue(dynamicManager.allocateMemory("Job2", 20 * 1024), 
                "应该能够分配20K内存");
        
        // 验证分区状态
        List<Partition> partitions = dynamicManager.getMemoryStatus();
        assertTrue(partitions.stream().anyMatch(p -> p instanceof AllocatedPartition 
                && ((AllocatedPartition)p).getJobName().equals("Job1")), 
                "应该能找到Job1的分区");
    }

    @Test
    @DisplayName("测试可变分区内存释放和合并")
    void testDynamicPartitionReleaseAndMerge() {
        // 分配三个连续的分区
        assertTrue(dynamicManager.allocateMemory("Job1", 10 * 1024), 
                "应该能够分配10K内存");
        assertTrue(dynamicManager.allocateMemory("Job2", 20 * 1024), 
                "应该能够分配20K内存");
        assertTrue(dynamicManager.allocateMemory("Job3", 30 * 1024), 
                "应该能够分配30K内存");
        
        // 释放中间的分区
        assertTrue(dynamicManager.releaseMemory("Job2"), 
                "应该能够释放Job2的内存");
        
        // 验证分区状态
        List<Partition> partitions = dynamicManager.getMemoryStatus();
        assertTrue(partitions.stream().anyMatch(p -> !(p instanceof AllocatedPartition) 
                && p.getSize() >= 20 * 1024), 
                "应该存在大于等于20K的空闲分区");
    }

    @Test
    @DisplayName("测试可变分区碎片整理")
    void testDynamicPartitionDefragmentation() {
        // 分配多个不同大小的分区
        assertTrue(dynamicManager.allocateMemory("Job1", 5 * 1024), 
                "应该能够分配5K内存");
        assertTrue(dynamicManager.allocateMemory("Job2", 10 * 1024), 
                "应该能够分配10K内存");
        assertTrue(dynamicManager.allocateMemory("Job3", 15 * 1024), 
                "应该能够分配15K内存");
        
        // 随机释放一些分区
        assertTrue(dynamicManager.releaseMemory("Job1"), 
                "应该能够释放Job1的内存");
        assertTrue(dynamicManager.releaseMemory("Job3"), 
                "应该能够释放Job3的内存");
        
        // 尝试分配一个大分区
        assertTrue(dynamicManager.allocateMemory("Job4", 18 * 1024), 
                "应该能够分配18K内存");
    }
} 