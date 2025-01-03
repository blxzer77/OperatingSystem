package com.virtualmemorymanagement.algorithm;

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
 * 按照老师要求的测试类
 * 测试虚拟存储器管理的三种页面置换算法：
 * 1. FIFO（先进先出）
 * 2. LRU（最近最少使用）
 * 3. LFU（最近最不常用）
 * 
 * 测试序列：70120304230321201701
 * 物理块数：3
 */
public class RequestedTest {
    private FIFOAlgorithm fifoAlgorithm;
    private LRUAlgorithm lruAlgorithm;
    private LFUAlgorithm lfuAlgorithm;
    private static final int FRAME_COUNT = 3; // 物理块数
    private static final String PAGE_SEQUENCE = "70120304230321201701";
    
    @BeforeEach
    void setUp(@TempDir Path tempDir) throws IOException {
        System.out.println("\n=== 初始化虚拟存储器管理系统 ===");
        System.out.println("物理块数: " + FRAME_COUNT);
        
        // 创建测试文件
        Path testFile = tempDir.resolve("page_sequence.txt");
        Files.writeString(testFile, PAGE_SEQUENCE);
        System.out.println("创建测试文件，页面序列：" + PAGE_SEQUENCE);
        
        // 初始化算法
        fifoAlgorithm = new FIFOAlgorithm(FRAME_COUNT);
        lruAlgorithm = new LRUAlgorithm(FRAME_COUNT);
        lfuAlgorithm = new LFUAlgorithm(FRAME_COUNT);
        System.out.println("算法初始化完成");
    }

    @Test
    @DisplayName("测试FIFO算法")
    void testFIFO() {
        System.out.println("\n========== FIFO（先进先出）算法测试 ==========");
        System.out.println("开始处理页面序列...");
        
        // 处理每个页面
        for (char c : PAGE_SEQUENCE.toCharArray()) {
            int page = Character.getNumericValue(c);
            System.out.println("\n>>> 处理页面: " + page);
            
            boolean isPageFault = fifoAlgorithm.accessPage(page);
            if (isPageFault) {
                System.out.println("发生缺页，当前缺页次数：" + fifoAlgorithm.getPageFaults());
                Integer replacedPage = fifoAlgorithm.getLastReplacedPage();
                if (replacedPage != null) {
                    System.out.println("淘汰页面：" + replacedPage);
                }
            } else {
                System.out.println("页面命中");
            }
            
            System.out.println("当前内存状态：" + fifoAlgorithm.getCurrentPages());
        }
        
        System.out.println("\n结果统计：");
        System.out.println("淘汰页面序列：" + fifoAlgorithm.getReplacedPages());
        System.out.println("总缺页次数：" + fifoAlgorithm.getPageFaults());
        System.out.println("\n========== FIFO算法测试结束 ==========");
    }

    @Test
    @DisplayName("测试LRU算法")
    void testLRU() {
        System.out.println("\n========== LRU（最近最少使用）算法测试 ==========");
        System.out.println("开始处理页面序列...");
        
        // 处理每个页面
        for (char c : PAGE_SEQUENCE.toCharArray()) {
            int page = Character.getNumericValue(c);
            System.out.println("\n>>> 处理页面: " + page);
            
            boolean isPageFault = lruAlgorithm.accessPage(page);
            if (isPageFault) {
                System.out.println("发生缺页，当前缺页次数：" + lruAlgorithm.getPageFaults());
                Integer replacedPage = lruAlgorithm.getLastReplacedPage();
                if (replacedPage != null) {
                    System.out.println("淘汰页面：" + replacedPage);
                }
            } else {
                System.out.println("页面命中");
            }
            
            System.out.println("当前内存状态：" + lruAlgorithm.getCurrentPages());
        }
        
        System.out.println("\n结果统计：");
        System.out.println("淘汰页面序列：" + lruAlgorithm.getReplacedPages());
        System.out.println("总缺页次数：" + lruAlgorithm.getPageFaults());
        System.out.println("\n========== LRU算法测试结束 ==========");
    }

    @Test
    @DisplayName("测试LFU算法")
    void testLFU() {
        System.out.println("\n========== LFU（最近最不常用）算法测试 ==========");
        System.out.println("开始处理页面序列...");
        
        // 处理每个页面
        for (char c : PAGE_SEQUENCE.toCharArray()) {
            int page = Character.getNumericValue(c);
            System.out.println("\n>>> 处理页面: " + page);
            
            boolean isPageFault = lfuAlgorithm.accessPage(page);
            if (isPageFault) {
                System.out.println("发生缺页，当前缺页次数：" + lfuAlgorithm.getPageFaults());
                Integer replacedPage = lfuAlgorithm.getLastReplacedPage();
                if (replacedPage != null) {
                    System.out.println("淘汰页面：" + replacedPage);
                }
            } else {
                System.out.println("页面命中");
            }
            
            System.out.println("当前内存状态：" + lfuAlgorithm.getCurrentPages());
            System.out.println("页面访问频率：" + lfuAlgorithm.getPageFrequencies());
        }
        
        System.out.println("\n结果统计：");
        System.out.println("淘汰页面序列：" + lfuAlgorithm.getReplacedPages());
        System.out.println("总缺页次数：" + lfuAlgorithm.getPageFaults());
        System.out.println("\n========== LFU算法测试结束 ==========");
    }

    @Test
    @DisplayName("测试从文件读取页面序列")
    void testReadFromFile(@TempDir Path tempDir) throws IOException {
        System.out.println("\n========== 文件读取测试 ==========");
        
        // 创建测试文件
        Path testFile = tempDir.resolve("page_sequence.txt");
        Files.writeString(testFile, PAGE_SEQUENCE);
        System.out.println("创建测试文件：" + testFile);
        
        // 读取文件内容
        String content = Files.readString(testFile);
        System.out.println("读取的页面序列：" + content);
        assertEquals(PAGE_SEQUENCE, content, "文件内容应该与原始序列相同");
        
        // 验证序列长度
        assertEquals(20, content.length(), "页面序列长度应该为20");
        
        // 验证序列中的数字是否有效
        assertTrue(content.matches("\\d+"), "序列应该只包含数字");
        
        System.out.println("文件读取测试通过");
        System.out.println("\n========== 文件读取测试结束 ==========");
    }

    @Test
    @DisplayName("测试算法结果比较")
    void testAlgorithmComparison() {
        System.out.println("\n========== 算法结果比较 ==========");
        
        // 运行所有算法
        runAlgorithm(fifoAlgorithm, "FIFO");
        runAlgorithm(lruAlgorithm, "LRU");
        runAlgorithm(lfuAlgorithm, "LFU");
        
        // 比较结果
        System.out.println("\n各算法缺页次数比较：");
        System.out.println("FIFO：" + fifoAlgorithm.getPageFaults());
        System.out.println("LRU：" + lruAlgorithm.getPageFaults());
        System.out.println("LFU：" + lfuAlgorithm.getPageFaults());
        
        System.out.println("\n各算法淘汰页面序列：");
        System.out.println("FIFO：" + fifoAlgorithm.getReplacedPages());
        System.out.println("LRU：" + lruAlgorithm.getReplacedPages());
        System.out.println("LFU：" + lfuAlgorithm.getReplacedPages());
        
        System.out.println("\n========== 算法结果比较结束 ==========");
    }
    
    private void runAlgorithm(PageReplacementAlgorithm algorithm, String name) {
        System.out.println("\n运行" + name + "算法...");
        for (char c : PAGE_SEQUENCE.toCharArray()) {
            algorithm.accessPage(Character.getNumericValue(c));
        }
    }
} 