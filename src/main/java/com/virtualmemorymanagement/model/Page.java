package com.virtualmemorymanagement.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 页面类
 * 表示虚拟内存中的一个页面
 * 记录页面的访问信息，用于页面置换算法的决策
 */
@Setter
@Getter
public class Page {
    private int pageNumber;         // 页面号
    private int accessCount;        // 页面被访问的次数
    private long lastAccessTime;    // 最后一次访问的时间戳

    /**
     * 页面构造函数
     * @param pageNumber 页面号
     */
    public Page(int pageNumber) {
        this.pageNumber = pageNumber;
        this.accessCount = 0;
        this.lastAccessTime = System.nanoTime();
        access(); // 初始化时计为第一次访问
    }

    /**
     * 记录页面被访问
     * 更新访问次数和最后访问时间
     */
    public void access() {
        accessCount++;
        lastAccessTime = System.nanoTime();
    }

    /**
     * 重置页面的访问统计信息
     * 将访问次数清零，并更新最后访问时间
     */
    public void resetStatistics() {
        this.accessCount = 0;
        this.lastAccessTime = System.nanoTime();
        access(); // 重置后计为第一次访问
    }

    /**
     * 重写toString方法
     * @return 返回页面号的字符串表示
     */
    @Override
    public String toString() {
        return String.valueOf(pageNumber);
    }
}