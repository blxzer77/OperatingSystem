package com.virtualmemorymanagement.algorithm;

import com.virtualmemorymanagement.model.ReplacementEvent;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 页面置换算法接口
 * 定义了所有页面置换算法必须实现的基本功能
 * 包括：
 * - FIFO（先进先出）算法
 * - LRU（最近最少使用）算法
 * - LFU（最不经常使用）算法
 * 每个具体算法都需要实现这个接口中定义的方法
 */
public interface PageReplacementAlgorithm {
    /**
     * 执行页面置换算法
     * 读取指定文件中的页面引用序列，并模拟页面置换过程
     * @param filePath 包含页面引用序列的文件路径
     * @return 返回页面置换过程中产生的所有事件列表
     * @throws IOException 当文件读取出错时抛出异常
     */
    List<ReplacementEvent> executeReplacement(String filePath) throws IOException;

    /**
     * 访问指定的页面
     * 如果页面不在内存中，则需要进行页面置换
     * @param pageNumber 要访问的页面号
     * @return 如果发生缺页返回true，否则返回false
     */
    boolean accessPage(int pageNumber);

    /**
     * 获取算法执行过程中发生的缺页次数
     * 用于评估算法性能
     * @return 返回总的缺页次数
     */
    int getPageFaults();

    /**
     * 获取最近一次页面置换中被置换出的页面
     * 用于跟踪和显示置换过程
     * @return 返回最后被置换出的页面号，如果没有发生置换则返回null
     */
    Integer getLastReplacedPage();

    /**
     * 获取当前内存中的所有页面
     * 用于显示当前内存状态
     * @return 返回当前内存中的页面列表
     */
    List<Integer> getCurrentPages();

    /**
     * 获取所有被置换出的页面的历史记录
     * 用于分析和统计置换行为
     * @return 返回按置换顺序排列的页面号列表
     */
    List<Integer> getReplacedPages();

    /**
     * 获取每个页面被访问的频率
     * 主要用于LFU算法，其他算法可以不实现此方法
     * @return 返回页面号到访问频率的映射，默认返回null
     */
    default Map<Integer, Integer> getPageFrequencies() {
        return null;
    }
}