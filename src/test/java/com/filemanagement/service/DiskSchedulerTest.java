package com.filemanagement.service;

import com.filemanagement.model.SchedulingResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * 磁盘调度算法测试类
 * 测试序列：55, 58, 39, 18, 90, 160, 150, 38, 184
 * 初始磁头位置：50
 */
public class DiskSchedulerTest {
    private DiskScheduler scheduler;
    private static final int INITIAL_HEAD = 50;

    @BeforeEach
    void setUp() {
        scheduler = new DiskScheduler(INITIAL_HEAD);
    }

    @Test
    @DisplayName("测试FCFS磁盘调度算法")
    void testFCFSAlgorithm(@TempDir Path tempDir) throws IOException {
        Path testFile = tempDir.resolve("test_tracks.txt");
        Files.writeString(testFile, "55 58 39 18 90 160 150 38 184");

        scheduler.setAlgorithm("FCFS");
        SchedulingResult result = scheduler.executeDiskScheduling(testFile.toString());

        assertNotNull(result, "调度结果不应为空");
        assertTrue(result.getTotalMovement() > 0, "总移动距离应大于0");
        assertEquals(9, result.getAccessSequence().size(), "访问序列长度应为9");
        
        // 验证访问顺序是否与输入顺序相同
        List<Integer> expectedSequence = List.of(55, 58, 39, 18, 90, 160, 150, 38, 184);
        assertEquals(expectedSequence, result.getAccessSequence(), "FCFS访问顺序应与输入顺序相同");
    }

    @Test
    @DisplayName("测试SSTF磁盘调度算法")
    void testSSTFAlgorithm(@TempDir Path tempDir) throws IOException {
        Path testFile = tempDir.resolve("test_tracks.txt");
        Files.writeString(testFile, "55 58 39 18 90 160 150 38 184");

        scheduler.setAlgorithm("SSTF");
        SchedulingResult result = scheduler.executeDiskScheduling(testFile.toString());

        assertNotNull(result, "调度结果不应为空");
        assertTrue(result.getTotalMovement() > 0, "总移动距离应大于0");
        assertEquals(9, result.getAccessSequence().size(), "访问序列长度应为9");
        
        // 验证总移动距离是否小于FCFS
        scheduler.setAlgorithm("FCFS");
        SchedulingResult fcfsResult = scheduler.executeDiskScheduling(testFile.toString());
        assertTrue(result.getTotalMovement() <= fcfsResult.getTotalMovement(), 
                "SSTF的总移动距离应小于等于FCFS");
    }

    @Test
    @DisplayName("测试SCAN磁盘调度算法")
    void testScanAlgorithm(@TempDir Path tempDir) throws IOException {
        Path testFile = tempDir.resolve("test_tracks.txt");
        Files.writeString(testFile, "55 58 39 18 90 160 150 38 184");

        scheduler.setAlgorithm("SCAN");
        scheduler.setScanDirection(true); // 向大磁道方向移动
        SchedulingResult result = scheduler.executeDiskScheduling(testFile.toString());

        assertNotNull(result, "调度结果不应为空");
        assertTrue(result.getTotalMovement() > 0, "总移动距离应大于0");
        assertEquals(9, result.getAccessSequence().size(), "访问序列长度应为9");
        
        // 验证总移动距离是否小于FCFS
        scheduler.setAlgorithm("FCFS");
        SchedulingResult fcfsResult = scheduler.executeDiskScheduling(testFile.toString());
        assertTrue(result.getTotalMovement() <= fcfsResult.getTotalMovement(), 
                "SCAN的总移动距离应小于等于FCFS");
    }

    @Test
    @DisplayName("测试空文件处理")
    void testEmptyFile(@TempDir Path tempDir) throws IOException {
        Path emptyFile = tempDir.resolve("empty.txt");
        Files.writeString(emptyFile, "");

        scheduler.setAlgorithm("FCFS");
        assertThrows(IllegalStateException.class, () -> {
            scheduler.executeDiskScheduling(emptyFile.toString());
        }, "空文件应该抛出IllegalStateException异常");
    }

    @Test
    @DisplayName("测试文件不存在处理")
    void testFileNotFound() {
        scheduler.setAlgorithm("FCFS");
        assertThrows(IOException.class, () -> {
            scheduler.executeDiskScheduling("nonexistent_file.txt");
        }, "不存在的文件应该抛出IOException异常");
    }

    @Test
    @DisplayName("测试无效算法处理")
    void testInvalidAlgorithm() {
        assertThrows(IllegalArgumentException.class, () -> {
            scheduler.setAlgorithm("INVALID");
        }, "无效的算法名称应该抛出IllegalArgumentException异常");
    }
} 