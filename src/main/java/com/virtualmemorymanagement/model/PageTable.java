package com.virtualmemorymanagement.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 页表类
 * 用于管理进程的虚拟页面到物理帧的映射
 * 维护页面置换的相关信息和统计数据
 */
@Getter
public class PageTable {
    private List<Page> pages;       // 当前在内存中的页面列表
    private int capacity;           // 页表容量（最大可容纳的页面数）
    private int pageFaults;         // 缺页次数统计

    /**
     * 页表构造函数
     * @param capacity 页表容量，即可同时容纳的页面数
     */
    public PageTable(int capacity) {
        this.capacity = capacity;
        this.pages = new ArrayList<>();
        this.pageFaults = 0;
    }

    /**
     * 检查指定页面是否在内存中
     * @param pageNumber 要检查的页面号
     * @return 如果页面在内存中返回true，否则返回false
     */
    public boolean contains(int pageNumber) {
        return pages.stream().anyMatch(p -> p.getPageNumber() == pageNumber);
    }

    /**
     * 添加新页面到页表
     * @param page 要添加的页面
     * @throws IllegalStateException 如果页表已满
     */
    public void addPage(Page page) {
        if (pages.size() >= capacity) {
            throw new IllegalStateException("Page table is full");
        }
        pages.add(page);
    }

    /**
     * 从页表中移除指定页面
     * @param page 要移除的页面
     */
    public void removePage(Page page) {
        pages.remove(page);
    }

    /**
     * 设置页表中的页面列表
     * @param pages 新的页面列表
     * @throws IllegalArgumentException 如果页面数超过容量
     */
    public void setPages(List<Page> pages) {
        if (pages.size() > capacity) {
            throw new IllegalArgumentException("Page list exceeds capacity");
        }
        this.pages = new ArrayList<>(pages);
    }

    /**
     * 设置页表容量
     * @param capacity 新的容量
     * @throws IllegalArgumentException 如果新容量小于当前页面数
     */
    public void setCapacity(int capacity) {
        if (capacity < pages.size()) {
            throw new IllegalArgumentException("New capacity is smaller than current page count");
        }
        this.capacity = capacity;
    }

    /**
     * 设置缺页次数
     * @param pageFaults 新的缺页次数
     * @throws IllegalArgumentException 如果缺页次数为负数
     */
    public void setPageFaults(int pageFaults) {
        if (pageFaults < 0) {
            throw new IllegalArgumentException("Page faults cannot be negative");
        }
        this.pageFaults = pageFaults;
    }

    /**
     * 增加缺页计数
     */
    public void incrementPageFaults() {
        pageFaults++;
    }

    /**
     * 清空页表
     * 移除所有页面并重置缺页计数
     */
    public void clear() {
        pages.clear();
        pageFaults = 0;
    }
}