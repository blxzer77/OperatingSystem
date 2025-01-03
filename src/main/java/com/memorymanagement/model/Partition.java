package com.memorymanagement.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 分区抽象基类
 * 定义了内存分区的基本属性和方法
 * 
 * 功能：
 * 1. 提供分区的基本属性（起始地址、大小）
 * 2. 定义分区状态的抽象方法
 * 3. 作为已分配分区和空闲分区的父类
 * 
 * 设计说明：
 * - 使用抽象类而不是接口，因为需要共享基本属性的实现
 * - 使用protected访问级别允许子类直接访问属性
 * - 使用Lombok简化getter/setter方法的编写
 */
@Setter
@Getter
public abstract class Partition {
    /**
     * 分区在内存中的起始地址
     * - 对于已分配分区，表示分配给作业的内存起始位置
     * - 对于空闲分区，表示可用内存的起始位置
     */
    protected int startAddress;

    /**
     * 分区的大小（单位：字节）
     * - 对于已分配分区，表示分配给作业的内存大小
     * - 对于空闲分区，表示可用于分配的内存大小
     */
    protected int size;

    /**
     * 构造函数
     * 初始化分区的基本属性
     * 
     * @param startAddress 分区的起始地址，必须大于等于操作系统占用的空间
     * @param size 分区的大小，必须为正数
     */
    public Partition(int startAddress, int size) {
        this.startAddress = startAddress;
        this.size = size;
    }

    /**
     * 获取分区状态的抽象方法
     * 由子类实现，用于区分分区的当前状态
     * 
     * @return 分区状态
     *         - "allocated"：表示分区已被分配
     *         - "free"：表示分区空闲可用
     */
    public abstract String getStatus();
}