package com.memorymanagement.service;

import com.memorymanagement.model.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 固定分区管理器
 * 实现固定大小分区的内存管理
 * 
 * 特点：
 * 1. 预先划分固定大小的分区
 * 2. 分区大小不可改变
 * 3. 不支持分区合并和分割
 * 4. 可能产生内部碎片
 * 
 * 分区布局：
 * - 0~4K：操作系统区域
 * - 4K~6K：两个1K分区
 * - 6K~8K：一个2K分区
 * - 8K~16K：两个4K分区
 * - 16K~24K：一个8K分区
 * - 24K~40K：一个16K分区
 * - 40K~128K：一个88K分区
 */
public class FixedPartitionManager implements MemoryManager {
    // 操作系统占用空间大小(4K)
    private static final int OS_SIZE = 4 * 1024;
    // 总内存大小(128K)
    private static final int TOTAL_MEMORY = 128 * 1024;
    // 已分配分区列表，存储已被作业占用的分区
    private List<AllocatedPartition> allocatedPartitions;
    // 空闲分区列表，存储未被分配的分区
    private List<FreePartition> freePartitions;

    /**
     * 构造函数
     * 初始化内存管理器，创建固定大小的分区
     * 初始状态：
     * 1. 创建空的已分配分区列表
     * 2. 创建空闲分区列表
     * 3. 初始化预定义大小的分区
     */
    public FixedPartitionManager() {
        allocatedPartitions = new ArrayList<>();
        freePartitions = new ArrayList<>();
        initializePartitions();
    }

    /**
     * 初始化固定大小的分区
     * 按照预定义的大小创建8个分区：
     * - 2个1K的分区：适合小型作业
     * - 1个2K的分区：适合小到中型作业
     * - 2个4K的分区：适合中型作业
     * - 1个8K的分区：适合中到大型作业
     * - 1个16K的分区：适合大型作业
     * - 1个88K的分区：适合特大型作业
     */
    private void initializePartitions() {
        int currentAddress = OS_SIZE;

        // 创建2个1K的分区，适合小型作业
        for (int i = 0; i < 2; i++) {
            freePartitions.add(new FreePartition(currentAddress, 1024));
            currentAddress += 1024;
        }

        // 创建1个2K的分区，适合小到中型作业
        freePartitions.add(new FreePartition(currentAddress, 2048));
        currentAddress += 2048;

        // 创建2个4K的分区，适合中型作业
        for (int i = 0; i < 2; i++) {
            freePartitions.add(new FreePartition(currentAddress, 4096));
            currentAddress += 4096;
        }

        // 创建1个8K的分区，适合中到大型作业
        freePartitions.add(new FreePartition(currentAddress, 8192));
        currentAddress += 8192;

        // 创建1个16K的分区，适合大型作业
        freePartitions.add(new FreePartition(currentAddress, 16384));
        currentAddress += 16384;

        // 创建1个88K的分区，适合特大型作业
        freePartitions.add(new FreePartition(currentAddress, 90112));
        currentAddress += 90112;
    }

    /**
     * 分配内存空间
     * 使用首次适应算法，在固定大小的分区中查找第一个足够大的分区
     * 
     * @param jobName 作业名称
     * @param size 申请的空间大小
     * @return 是否分配成功
     */
    @Override
    public boolean allocateMemory(String jobName, int size) {
        // 验证申请的空间大小是否有效
        if (size <= 0) {
            return false;
        }
        
        // 遍历空闲分区列表，查找第一个足够大的分区
        for (int i = 0; i < freePartitions.size(); i++) {
            FreePartition partition = freePartitions.get(i);
            if (partition.getSize() >= size) {
                // 创建新的已分配分区，整个分区都分配给作业
                AllocatedPartition allocatedPartition = new AllocatedPartition(
                        partition.getStartAddress(),
                        partition.getSize(),
                        jobName
                );
                allocatedPartitions.add(allocatedPartition);
                // 从空闲分区列表中移除已分配的分区
                freePartitions.remove(i);
                return true;
            }
        }
        // 没有找到合适的分区
        return false;
    }

    /**
     * 释放内存空间
     * 将指定作业占用的分区释放，使其重新变为空闲状态
     * 
     * @param jobName 要释放内存的作业名称
     * @return 是否释放成功
     */
    @Override
    public boolean releaseMemory(String jobName) {
        // 遍历已分配分区列表，查找要释放的分区
        for (int i = 0; i < allocatedPartitions.size(); i++) {
            AllocatedPartition partition = allocatedPartitions.get(i);
            if (partition.getJobName().equals(jobName)) {
                // 创建新的空闲分区，保持原分区大小不变
                FreePartition freePartition = new FreePartition(
                        partition.getStartAddress(),
                        partition.getSize()
                );
                freePartitions.add(freePartition);
                // 从已分配分区列表中移除该分区
                allocatedPartitions.remove(i);
                return true;
            }
        }
        // 没有找到对应的分区
        return false;
    }

    /**
     * 显示当前内存使用状态
     * 输出所有分区的信息，包括已分配和空闲分区
     */
    @Override
    public void displayMemoryStatus() {
        // 具体实现在GUI中完成
    }

    /**
     * 获取当前内存状态
     * 返回按起始地址排序的所有分区列表
     * 
     * @return 包含所有分区（已分配和空闲）的列表
     */
    @Override
    public List<Partition> getMemoryStatus() {
        List<Partition> allPartitions = new ArrayList<>();
        // 将已分配和空闲分区合并到一个列表
        allPartitions.addAll(allocatedPartitions);
        allPartitions.addAll(freePartitions);
        // 按起始地址排序
        allPartitions.sort((p1, p2) -> p1.getStartAddress() - p2.getStartAddress());
        return allPartitions;
    }

    /**
     * 辅助方法：将数字右对齐
     * 用于格式化显示数字，保持对齐
     * 
     * @param width 总宽度
     * @param number 要格式化的数字
     * @return 格式化后的字符串
     */
    private String formatNumber(int width, int number) {
        String numStr = String.valueOf(number);
        int spaces = width - numStr.length();
        return " ".repeat(spaces) + numStr + " ";
    }

    /**
     * 辅助方法：将字符串在指定宽度内居中
     * 处理中英文混合字符串的对齐显示
     * 
     * @param width 总宽度
     * @param s 要居中的字符串
     * @return 格式化后的字符串
     */
    private String centerString(int width, String s) {
        if (s == null) s = "";
        
        // 计算字符串的实际显示宽度（中文字符占两个位置）
        int actualWidth = 0;
        for (char c : s.toCharArray()) {
            actualWidth += isChinese(c) ? 2 : 1;
        }
        
        // 计算需要的空格数
        int spacesNeeded = width - actualWidth;
        int leftSpaces = spacesNeeded / 2;
        int rightSpaces = spacesNeeded - leftSpaces;
        
        // 构建结果字符串
        StringBuilder sb = new StringBuilder();
        // 添加左侧空格
        sb.append(" ".repeat(leftSpaces));
        // 添加内容
        sb.append(s);
        // 添加右侧空格
        sb.append(" ".repeat(rightSpaces));
        
        return sb.toString();
    }

    /**
     * 判断字符是否是中文字符
     * 用于计算字符串实际显示宽度
     * 
     * @param c 要判断的字符
     * @return 是否是中文字符
     */
    private boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
            || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
            || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
            || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
            || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
            || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
    }
}