package com.filemanagement.service;

import com.filemanagement.algorithm.*;
import com.filemanagement.model.SchedulingResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

public class RequestedTest {
    private static final int INITIAL_HEAD = 100;  // 初始磁头位置
    private DiskScheduler scheduler;
    private List<Integer> trackSequence;

    @BeforeEach
    void setUp() {
        scheduler = new DiskScheduler();
        // 初始化磁道访问序列
        trackSequence = Arrays.asList(55, 58, 39, 18, 90, 160, 150, 38, 184);
        System.out.println("\n初始化磁头位置：" + INITIAL_HEAD);
        System.out.println("待访问磁道序列：" + trackSequence);
    }

    @Test
    void testFCFS() {
        System.out.println("\n测试FCFS（先来先服务）算法：");
        scheduler.setAlgorithm(new FCFSAlgorithm());
        SchedulingResult result = scheduler.executeDiskScheduling(INITIAL_HEAD, trackSequence);
        
        System.out.println("访问顺序：" + result.getAccessSequence());
        System.out.println("总移动距离：" + result.getTotalMovement());
        
        // 验证总移动距离
        assertEquals(498, result.getTotalMovement(), "FCFS总移动距离应为498");
        
        // 验证访问顺序是否与输入顺序相同
        List<Integer> expectedSequence = Arrays.asList(55, 58, 39, 18, 90, 160, 150, 38, 184);
        assertEquals(expectedSequence, result.getAccessSequence(), "FCFS访问顺序应与输入顺序相同");
    }

    @Test
    void testSSTF() {
        System.out.println("\n测试SSTF（最短寻道优先）算法：");
        scheduler.setAlgorithm(new SSTFAlgorithm());
        SchedulingResult result = scheduler.executeDiskScheduling(INITIAL_HEAD, trackSequence);
        
        System.out.println("访问顺序：" + result.getAccessSequence());
        System.out.println("总移动距离：" + result.getTotalMovement());
        
        // 验证总移动距离
        assertEquals(248, result.getTotalMovement(), "SSTF总移动距离应为248");
        
        // 验证访问顺序是否正确
        List<Integer> expectedSequence = Arrays.asList(90, 58, 55, 39, 38, 18, 150, 160, 184);
        assertEquals(expectedSequence, result.getAccessSequence(), "SSTF访问顺序不正确");
    }

    @Test
    void testSCAN() {
        System.out.println("\n测试SCAN（电梯）算法：");
        scheduler.setAlgorithm(new ScanAlgorithm());
        SchedulingResult result = scheduler.executeDiskScheduling(INITIAL_HEAD, trackSequence);
        
        System.out.println("访问顺序：" + result.getAccessSequence());
        System.out.println("总移动距离：" + result.getTotalMovement());
        
        // 验证总移动距离
        assertEquals(250, result.getTotalMovement(), "SCAN总移动距离应为250");
        
        // 验证访问顺序是否正确
        List<Integer> expectedSequence = Arrays.asList(150, 160, 184, 90, 58, 55, 39, 38, 18);
        assertEquals(expectedSequence, result.getAccessSequence(), "SCAN访问顺序不正确");
    }

    @Test
    void testAlgorithmComparison() {
        System.out.println("\n算法性能比较：");
        
        // FCFS
        scheduler.setAlgorithm(new FCFSAlgorithm());
        SchedulingResult fcfsResult = scheduler.executeDiskScheduling(INITIAL_HEAD, trackSequence);
        System.out.println("FCFS总移动距离：" + fcfsResult.getTotalMovement());
        
        // SSTF
        scheduler.setAlgorithm(new SSTFAlgorithm());
        SchedulingResult sstfResult = scheduler.executeDiskScheduling(INITIAL_HEAD, trackSequence);
        System.out.println("SSTF总移动距离：" + sstfResult.getTotalMovement());
        
        // SCAN
        scheduler.setAlgorithm(new ScanAlgorithm());
        SchedulingResult scanResult = scheduler.executeDiskScheduling(INITIAL_HEAD, trackSequence);
        System.out.println("SCAN总移动距离：" + scanResult.getTotalMovement());
        
        // 验证SSTF和SCAN的效率优于FCFS
        assertTrue(sstfResult.getTotalMovement() < fcfsResult.getTotalMovement(), 
                "SSTF应该比FCFS更有效率");
        assertTrue(scanResult.getTotalMovement() < fcfsResult.getTotalMovement(), 
                "SCAN应该比FCFS更有效率");
    }
} 