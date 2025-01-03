package com.virtualmemorymanagement.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 页面置换事件类
 * 用于记录虚拟内存管理系统中每次页面访问和置换的详细信息
 * 主要用于：
 * - 记录页面访问历史
 * - 统计缺页率
 * - 分析算法性能
 * - 可视化页面置换过程
 */
@Getter
@Setter
public class ReplacementEvent {
    private int pageNumber;      // 当前访问的页面号
    private boolean pageFault;   // 是否发生缺页中断
    private Integer replacedPage; // 被置换出的页面号（如果发生置换则有值，否则为null）

    /**
     * 页面置换事件构造函数
     * @param pageNumber 被访问的页面号
     * @param pageFault 是否发生缺页中断
     * @param replacedPage 被置换出的页面号（如果有）
     */
    public ReplacementEvent(int pageNumber, boolean pageFault, Integer replacedPage) {
        this.pageNumber = pageNumber;
        this.pageFault = pageFault;
        this.replacedPage = replacedPage;
    }
} 