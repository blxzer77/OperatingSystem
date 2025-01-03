package com.virtualmemorymanagement.algorithm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Map;

/**
 * 页面置换算法测试类
 * 测试序列：7, 0, 1, 2, 0, 3, 0, 4, 2, 3
 * 物理块数：3
 */
public class PageReplacementAlgorithmTest {
    private static final int FRAME_COUNT = 3;
    private FIFOAlgorithm fifoAlgorithm;
    private LRUAlgorithm lruAlgorithm;
    private LFUAlgorithm lfuAlgorithm;

    @BeforeEach
    void setUp() {
        fifoAlgorithm = new FIFOAlgorithm(FRAME_COUNT);
        lruAlgorithm = new LRUAlgorithm(FRAME_COUNT);
        lfuAlgorithm = new LFUAlgorithm(FRAME_COUNT);
    }

    @Test
    @DisplayName("测试FIFO页面置换算法")
    void testFIFO() {
        // 页面引用序列：7, 0, 1, 2, 0, 3, 0, 4, 2, 3
        int[] pageSequence = {7, 0, 1, 2, 0, 3, 0, 4, 2, 3};
        for (int page : pageSequence) {
            fifoAlgorithm.accessPage(page);
        }
        assertEquals(9, fifoAlgorithm.getPageFaults(), "FIFO算法缺页次数应为9");
        
        // 验证最终内存状态
        List<Integer> currentPages = fifoAlgorithm.getCurrentPages();
        assertEquals(3, currentPages.size(), "内存中应该有3个页面");
        
        // 验证置换页面序列
        List<Integer> replacedPages = fifoAlgorithm.getReplacedPages();
        assertTrue(replacedPages.size() >= 4, "应该至少有4次页面置换");
    }

    @Test
    @DisplayName("测试LRU页面置换算法")
    void testLRU() {
        // 页面引用序列：7, 0, 1, 2, 0, 3, 0, 4, 2, 3
        int[] pageSequence = {7, 0, 1, 2, 0, 3, 0, 4, 2, 3};
        for (int page : pageSequence) {
            lruAlgorithm.accessPage(page);
        }
        assertEquals(8, lruAlgorithm.getPageFaults(), "LRU算法缺页次数应为8");
        
        // 验证最终内存状态
        List<Integer> currentPages = lruAlgorithm.getCurrentPages();
        assertEquals(3, currentPages.size(), "内存中应该有3个页面");
        
        // 验证置换页面序列
        List<Integer> replacedPages = lruAlgorithm.getReplacedPages();
        assertTrue(replacedPages.size() >= 5, "应该至少有5次页面置换");
    }

    @Test
    @DisplayName("测试LFU页面置换算法")
    void testLFU() {
        // 页面引用序列：7, 0, 1, 2, 0, 3, 0, 4, 2, 3
        int[] pageSequence = {7, 0, 1, 2, 0, 3, 0, 4, 2, 3};
        for (int page : pageSequence) {
            lfuAlgorithm.accessPage(page);
        }
        assertEquals(8, lfuAlgorithm.getPageFaults(), "LFU算法缺页次数应为8");
        
        // 验证最终内存状态
        List<Integer> currentPages = lfuAlgorithm.getCurrentPages();
        assertEquals(3, currentPages.size(), "内存中应该有3个页面");
        
        // 验证置换页面序列
        List<Integer> replacedPages = lfuAlgorithm.getReplacedPages();
        assertTrue(replacedPages.size() >= 4, "应该至少有4次页面置换");
        
        // 验证页面访问频率
        Map<Integer, Integer> frequencies = lfuAlgorithm.getPageFrequencies();
        assertNotNull(frequencies, "页面访问频率不应为空");
        assertTrue(frequencies.size() <= 3, "内存中不应超过3个页面");
    }

    @Test
    @DisplayName("测试页面置换算法性能比较")
    void testAlgorithmComparison() {
        // 页面引用序列：7, 0, 1, 2, 0, 3, 0, 4, 2, 3
        int[] pageSequence = {7, 0, 1, 2, 0, 3, 0, 4, 2, 3};
        
        // 运行FIFO算法
        for (int page : pageSequence) {
            fifoAlgorithm.accessPage(page);
        }
        
        // 运行LRU算法
        for (int page : pageSequence) {
            lruAlgorithm.accessPage(page);
        }
        
        // 运行LFU算法
        for (int page : pageSequence) {
            lfuAlgorithm.accessPage(page);
        }
        
        // 比较结果
        assertTrue(fifoAlgorithm.getPageFaults() <= 9, "FIFO算法缺页次数应不超过9次");
        assertTrue(lruAlgorithm.getPageFaults() <= 8, "LRU算法缺页次数应不超过8次");
        assertTrue(lfuAlgorithm.getPageFaults() <= 8, "LFU算法缺页次数应不超过8次");
        
        // 验证各算法的置换页面序列长度
        assertTrue(fifoAlgorithm.getReplacedPages().size() >= 4, "FIFO算法应至少有4次页面置换");
        assertTrue(lruAlgorithm.getReplacedPages().size() >= 5, "LRU算法应至少有5次页面置换");
        assertTrue(lfuAlgorithm.getReplacedPages().size() >= 4, "LFU算法应至少有4次页面置换");
    }

    @Test
    @DisplayName("测试指定页面序列的详细过程")
    void testDetailedSequence() {
        // 页面引用序列：70120304230321201701
        int[] pageSequence = {7,0,1,2,0,3,0,4,2,3,0,3,2,1,2,0,1,7,0,1};
        
        System.out.println("页面序列：");
        for (int page : pageSequence) {
            System.out.print(page + " ");
        }
        System.out.println("\n");
        
        // FIFO算法详细过程
        System.out.println("FIFO算法过程：");
        for (int page : pageSequence) {
            fifoAlgorithm.accessPage(page);
            System.out.print("访问页面 " + page + " 后的内存状态: ");
            System.out.println(fifoAlgorithm.getCurrentPages());
        }
        System.out.println("FIFO缺页次数：" + fifoAlgorithm.getPageFaults());
        System.out.println("FIFO置换页面序列：" + fifoAlgorithm.getReplacedPages());
        System.out.println();
        
        // LRU算法详细过程
        System.out.println("LRU算法过程：");
        for (int page : pageSequence) {
            lruAlgorithm.accessPage(page);
            System.out.print("访问页面 " + page + " 后的内存状态: ");
            System.out.println(lruAlgorithm.getCurrentPages());
        }
        System.out.println("LRU缺页次数：" + lruAlgorithm.getPageFaults());
        System.out.println("LRU置换页面序列：" + lruAlgorithm.getReplacedPages());
        System.out.println();
        
        // LFU算法详细过程
        System.out.println("LFU算法过程：");
        for (int page : pageSequence) {
            lfuAlgorithm.accessPage(page);
            System.out.print("访问页面 " + page + " 后的内存状态: ");
            System.out.println(lfuAlgorithm.getCurrentPages());
            System.out.println("页面访问频率：" + lfuAlgorithm.getPageFrequencies());
        }
        System.out.println("LFU缺页次数：" + lfuAlgorithm.getPageFaults());
        System.out.println("LFU置换页面序列：" + lfuAlgorithm.getReplacedPages());
    }
} 