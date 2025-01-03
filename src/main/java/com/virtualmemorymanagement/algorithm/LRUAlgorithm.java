package com.virtualmemorymanagement.algorithm;

import com.virtualmemorymanagement.model.ReplacementEvent;
import java.io.*;
import java.util.*;

/**
 * 最近最少使用(LRU)页面置换算法实现类
 * 特点：
 * - 选择最长时间没有被访问的页面进行置换
 * - 每次访问页面时都会更新该页面的访问时间
 * - 需要记录每个页面最后一次访问的时间
 * - 比FIFO更接近理想的OPT算法，但实现复杂度更高
 */
public class LRUAlgorithm implements PageReplacementAlgorithm {
    private final int frameCount;               // 内存帧数（可容纳的页面数）
    private final LinkedHashSet<Integer> frames; // 使用LinkedHashSet维护页面访问顺序
    private final List<Integer> replacedPages;   // 被置换出的页面历史记录
    private int pageFaults;                     // 缺页次数统计
    private Integer lastReplacedPage;           // 最近一次被置换出的页面

    /**
     * LRU算法构造函数
     * @param frameCount 内存帧数，即可同时容纳的页面数量
     */
    public LRUAlgorithm(int frameCount) {
        this.frameCount = frameCount;
        this.frames = new LinkedHashSet<>();     // LinkedHashSet保持插入顺序
        this.replacedPages = new ArrayList<>();
        this.pageFaults = 0;
        this.lastReplacedPage = null;
    }

    /**
     * 执行页面置换算法
     * 读取页面引用序列并模拟LRU置换过程
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
     * 如果页面在内存中，更新其访问时间（重新插入到LinkedHashSet末尾）
     * 如果页面不在内存中，执行LRU置换策略
     */
    @Override
    public boolean accessPage(int pageNumber) {
        // 如果页面已在内存中，更新访问顺序
        // 通过移除后重新添加，将页面移动到LinkedHashSet的末尾
        if (frames.contains(pageNumber)) {
            frames.remove(pageNumber);
            frames.add(pageNumber);
            return false;
        }

        // 发生缺页
        pageFaults++;

        // 如果内存未满，直接添加
        if (frames.size() < frameCount) {
            frames.add(pageNumber);
            lastReplacedPage = null;
            return true;
        }

        // 内存已满，需要置换最久未使用的页面
        // LinkedHashSet的第一个元素就是最久未使用的页面
        int replacedPage = frames.iterator().next();
        frames.remove(replacedPage);
        frames.add(pageNumber);
        lastReplacedPage = replacedPage;
        replacedPages.add(replacedPage);

        return true;
    }

    /**
     * 获取缺页次数
     * @return 返回算法执行过程中发生的缺页次数
     */
    @Override
    public int getPageFaults() {
        return pageFaults;
    }

    /**
     * 获取最后被置换的页面
     * @return 返回最近一次被置换出的页面号
     */
    @Override
    public Integer getLastReplacedPage() {
        return lastReplacedPage;
    }

    /**
     * 获取当前内存中的页面列表
     * 返回的列表按照页面最后访问时间排序
     * @return 返回当前内存中的页面列表
     */
    @Override
    public List<Integer> getCurrentPages() {
        return new ArrayList<>(frames);
    }

    /**
     * 获取被置换出的页面历史记录
     * @return 返回按置换顺序排列的页面号列表
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