package com.virtualmemorymanagement.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 虚拟内存管理中的进程控制块(PCB)类
 * 用于管理进程的虚拟内存分配和页表信息
 */
@Setter
@Getter
public class PCB {
    private int pid;                // 进程ID
    private int allocatedFrames;    // 分配给进程的物理帧数
    private PageTable pageTable;    // 进程的页表

    /**
     * PCB构造函数
     * @param pid 进程ID
     * @param allocatedFrames 分配给进程的物理帧数
     */
    public PCB(int pid, int allocatedFrames) {
        this.pid = pid;
        this.allocatedFrames = allocatedFrames;
        this.pageTable = new PageTable(allocatedFrames);
    }

    /**
     * 设置分配给进程的物理帧数
     * 如果帧数发生变化，会重新创建页表
     * @param allocatedFrames 新的物理帧数
     */
    public void setAllocatedFrames(int allocatedFrames) {
        this.allocatedFrames = allocatedFrames;
        // 如果分配的帧数改变，可能需要创建新的页表
        if (this.pageTable.getCapacity() != allocatedFrames) {
            this.pageTable = new PageTable(allocatedFrames);
        }
    }
}