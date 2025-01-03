package com.virtualmemorymanagement.algorithm;

import com.virtualmemorymanagement.model.ReplacementEvent;
import java.io.*;
import java.util.*;

/**
 * 先进先出(FIFO)页面置换算法实现类
 * 特点：
 * - 选择在内存中驻留时间最长的页面进行置换
 * - 维护一个队列来记录页面进入内存的顺序
 * - 当需要置换时，选择队列头部（最早进入）的页面
 * - 新页面总是添加到队列尾部
 */
public class FIFOAlgorithm implements PageReplacementAlgorithm {
    private final int frameCount;           // 内存帧数（可容纳的页面数）
    private final Queue<Integer> frames;     // 页面队列，按进入顺序存储页面
    private final Set<Integer> pageSet;      // 当前在内存中的页面集合，用于快速查找
    private final List<Integer> replacedPages; // 被置换出的页面历史记录
    private int pageFaults;                 // 缺页次数统计
    private Integer lastReplacedPage;       // 最近一次被置换出的页面

    /**
     * FIFO算法构造函数
     * @param frameCount 内存帧数，即可同时容纳的页面数量
     */
    public FIFOAlgorithm(int frameCount) {
        this.frameCount = frameCount;
        this.frames = new LinkedList<>();    // 使用LinkedList实现FIFO队列
        this.pageSet = new HashSet<>();      // 使用HashSet实现O(1)的页面查找
        this.replacedPages = new ArrayList<>();
        this.pageFaults = 0;
        this.lastReplacedPage = null;
    }

    /**
     * 执行页面置换算法
     * 读取页面引用序列并模拟FIFO置换过程
     */
    @Override
    public List<ReplacementEvent> executeReplacement(String filePath) throws IOException {
        List<ReplacementEvent> events = new ArrayList<>();
        List<Integer> pageSequence = readPageSequence(filePath);
        
        for (int pageNumber : pageSequence) {
            boolean pageFault = accessPage(pageNumber);
            events.add(new ReplacementEvent(pageNumber, pageFault, lastReplacedPage));
        }
        
        return events;
    }

    /**
     * 访问指定页面
     * 如果页面不在内存中，执行FIFO置换策略
     */
    @Override
    public boolean accessPage(int pageNumber) {
        // 如果页面已在内存中，不需要置换
        if (pageSet.contains(pageNumber)) {
            return false;
        }

        // 发生缺页
        pageFaults++;

        // 如果内存未满，直接添加
        if (frames.size() < frameCount) {
            frames.offer(pageNumber);
            pageSet.add(pageNumber);
            lastReplacedPage = null;
            return true;
        }

        // 内存已满，需要置换
        // 移除队列头部（最早进入）的页面
        int replacedPage = frames.poll();
        pageSet.remove(replacedPage);
        // 将新页面添加到队列尾部
        frames.offer(pageNumber);
        pageSet.add(pageNumber);
        lastReplacedPage = replacedPage;
        replacedPages.add(replacedPage);

        return true;
    }

    /**
     * 获取缺页次数
     */
    @Override
    public int getPageFaults() {
        return pageFaults;
    }

    /**
     * 获取最后被置换的页面
     */
    @Override
    public Integer getLastReplacedPage() {
        return lastReplacedPage;
    }

    /**
     * 获取当前内存中的页面列表
     */
    @Override
    public List<Integer> getCurrentPages() {
        return new ArrayList<>(frames);
    }

    /**
     * 获取被置换出的页面历史记录
     */
    @Override
    public List<Integer> getReplacedPages() {
        return new ArrayList<>(replacedPages);
    }

    /**
     * 从文件中读取页面引用序列
     * @param filePath 包含页面引用序列的文件路径
     * @return 页面引用序列列表
     * @throws IOException 当文件读取出错时抛出异常
     */
    private List<Integer> readPageSequence(String filePath) throws IOException {
        List<Integer> sequence = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();
            if (line != null && !line.trim().isEmpty()) {
                for (char c : line.trim().toCharArray()) {
                    sequence.add(Character.getNumericValue(c));
                }
            }
        }
        return sequence;
    }
}