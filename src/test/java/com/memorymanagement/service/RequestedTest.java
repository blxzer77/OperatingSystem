package com.memorymanagement.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 按照老师要求的测试类
 * 测试内存管理的固定分区和可变分区两种分配方式
 * 总内存空间：128K，其中前4K被OS占用，可分配空间从4K开始
 */
public class RequestedTest {
    private FixedPartitionManager fixedManager;
    private DynamicPartitionManager dynamicManager;
    private static final int TOTAL_MEMORY = 128 * 1024; // 128K
    private static final int OS_MEMORY = 4 * 1024;      // 4K

    @BeforeEach
    void setUp() {
        // 初始化固定分区管理器
        fixedManager = new FixedPartitionManager();
        
        // 初始化可变分区管理器
        dynamicManager = new DynamicPartitionManager();
    }

    @Test
    @DisplayName("测试固定分区分配")
    void testFixedPartition() {
        // 进程1（1.5K）
        assertTrue(fixedManager.allocateMemory("Process1", 1536), "进程1应该成功分配到内存");
        
        // 进程2（3K）
        assertTrue(fixedManager.allocateMemory("Process2", 3 * 1024), "进程2应该成功分配到内存");
        
        // 进程3（3K）
        assertTrue(fixedManager.allocateMemory("Process3", 3 * 1024), "进程3应该成功分配到内存");
        
        // 进程2运行结束，释放内存
        assertTrue(fixedManager.releaseMemory("Process2"), "进程2的内存应该成功释放");
        
        // 进程4（1.6K）
        assertTrue(fixedManager.allocateMemory("Process4", 1640), "进程4应该成功分配到内存");
        
        // 进程5（9K）
        assertTrue(fixedManager.allocateMemory("Process5", 9 * 1024), "进程5应该成功分配到内存");
    }

    @Test
    @DisplayName("测试可变分区分配")
    void testDynamicPartition() {
        // 进程1（1K）
        assertTrue(dynamicManager.allocateMemory("Process1", 1024), "进程1应该成功分配到内存");
        
        // 进程2（2K）
        assertTrue(dynamicManager.allocateMemory("Process2", 2 * 1024), "进程2应该成功分配到内存");
        
        // 进程3（3K）
        assertTrue(dynamicManager.allocateMemory("Process3", 3 * 1024), "进程3应该成功分配到内存");
        
        // 进程4（1K）
        assertTrue(dynamicManager.allocateMemory("Process4", 1024), "进程4应该成功分配到内存");
        
        // 进程5（9K）
        assertTrue(dynamicManager.allocateMemory("Process5", 9 * 1024), "进程5应该成功分配到内存");

        // 按顺序释放进程
        assertTrue(dynamicManager.releaseMemory("Process2"), "进程2的内存应该成功释放");
        assertTrue(dynamicManager.releaseMemory("Process3"), "进程3的内存应该成功释放");
        assertTrue(dynamicManager.releaseMemory("Process1"), "进程1的内存应该成功释放");
        assertTrue(dynamicManager.releaseMemory("Process5"), "进程5的内存应该成功释放");
        assertTrue(dynamicManager.releaseMemory("Process4"), "进程4的内存应该成功释放");

        // 验证最终应该只有一个大的空闲分区
        assertEquals(1, dynamicManager.getMemoryStatus().size(), "释放所有进程后应该只有一个空闲分区");
    }
} 