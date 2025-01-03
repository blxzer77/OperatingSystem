package com.virtualmemorymanagement.algorithm;

import com.virtualmemorymanagement.model.ReplacementEvent;
import java.io.*;
import java.util.*;

/**
 * 最不经常使用(LFU)页面置换算法实现类
 * 特点：
 * - 选择访问频率最低的页面进行置换
 * - 维护每个页面的访问计数器
 * - 当多个页面访问频率相同时，选择最久未使用的页面
 * - 实现最复杂，但可能获得最好的置换效果
 */
public class LFUAlgorithm implements PageReplacementAlgorithm {
    private final int frameCount;                     // 内存帧数（可容纳的页面数）
    private final Map<Integer, Integer> pageFrequency; // 记录每个页面的访问频率
    private final Map<Integer, Long> pageAccessTime;   // 记录每个页面的最后访问时间
    private final List<Integer> replacedPages;         // 被置换出的页面历史记录
    private int pageFaults;                           // 缺页次数统计
    private Integer lastReplacedPage;                 // 最近一次被置换出的页面

    /**
     * LFU算法构造函数
     * @param frameCount 内存帧数，即可同时容纳的页面数量
     */
    public LFUAlgorithm(int frameCount) {
        this.frameCount = frameCount;
        this.pageFrequency = new HashMap<>();         // 存储页面访问频率
        this.pageAccessTime = new HashMap<>();        // 存储页面访问时间
        this.replacedPages = new ArrayList<>();
        this.pageFaults = 0;
        this.lastReplacedPage = null;
    }

    /**
     * 执行页面置换算法
     * 读取页面引用序列并模拟LFU置换过程
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
     * 如果页面在内存中，增加其访问频率
     * 如果页面不在内存中，执行LFU置换策略
     */
    @Override
    public boolean accessPage(int pageNumber) {
        long currentTime = System.nanoTime();

        // 如果页面已在内存中，增加访问频率并更新访问时间
        if (pageFrequency.containsKey(pageNumber)) {
            pageFrequency.put(pageNumber, pageFrequency.get(pageNumber) + 1);
            pageAccessTime.put(pageNumber, currentTime);
            return false;
        }

        // 发生缺页
        pageFaults++;

        // 如果内存未满，直接添加
        if (pageFrequency.size() < frameCount) {
            pageFrequency.put(pageNumber, 1);
            pageAccessTime.put(pageNumber, currentTime);
            lastReplacedPage = null;
            return true;
        }

        // 内存已满，需要置换
        // 找到使用频率最低的页面，如果频率相同则选择最久未使用的
        int lfuPage = pageFrequency.entrySet().stream()
                .min(Comparator.<Map.Entry<Integer, Integer>>comparingInt(Map.Entry::getValue)
                        .thenComparing(e -> pageAccessTime.get(e.getKey())))
                .map(Map.Entry::getKey)
                .orElseThrow();

        // 移除使用频率最低的页面
        pageFrequency.remove(lfuPage);
        pageAccessTime.remove(lfuPage);
        lastReplacedPage = lfuPage;
        replacedPages.add(lfuPage);

        // 添加新页面，初始访问频率为1
        pageFrequency.put(pageNumber, 1);
        pageAccessTime.put(pageNumber, currentTime);

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
     * @return 返回当前内存中的所有页面号
     */
    @Override
    public List<Integer> getCurrentPages() {
        return new ArrayList<>(pageFrequency.keySet());
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
     * 获取所有页面的访问频率
     * @return 返回页面号到访问频率的映射
     */
    @Override
    public Map<Integer, Integer> getPageFrequencies() {
        return new HashMap<>(pageFrequency);
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