package com.memorymanagement.model;

/**
 * 空闲分区类
 * 表示未被分配的内存分区
 * 
 * 功能：
 * 1. 记录空闲分区的基本信息（继承自Partition）
 * 2. 提供分区状态的查询功能
 * 3. 作为可分配内存空间的标识
 * 
 * 使用场景：
 * - 系统初始化时创建初始空闲分区
 * - 作业释放内存时创建新的空闲分区
 * - 在动态分区管理中可能被分割或合并
 * - 在固定分区管理中保持大小不变
 * 
 * 特点：
 * - 不包含作业信息
 * - 可以参与分区的分割和合并操作
 * - 用于内存分配时的空间查找
 */
public class FreePartition extends Partition {
    /**
     * 构造函数
     * 创建一个新的空闲分区对象
     * 
     * @param startAddress 分区的起始地址，从父类继承
     * @param size 分区的大小（单位：字节），从父类继承
     */
    public FreePartition(int startAddress, int size) {
        super(startAddress, size);
    }

    /**
     * 获取分区状态
     * 实现父类的抽象方法
     * 
     * @return 返回字符串"free"，表示这是一个空闲的分区
     */
    @Override
    public String getStatus() {
        return "free";
    }
}