package com.memorymanagement.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 已分配分区类
 * 表示已经分配给某个作业的内存分区
 * 
 * 功能：
 * 1. 记录分区的基本信息（继承自Partition）
 * 2. 维护占用此分区的作业信息
 * 3. 提供分区状态的查询功能
 * 
 * 使用场景：
 * - 当内存管理器成功为作业分配内存空间时创建
 * - 用于跟踪内存分区的使用情况
 * - 在内存回收时用于定位要释放的分区
 */
@Setter
@Getter
public class AllocatedPartition extends Partition {
    /**
     * 占用此分区的作业名称
     * 用于标识哪个作业正在使用这块内存空间
     * 在内存释放时用于验证释放请求的合法性
     */
    private String jobName;

    /**
     * 构造函数
     * 创建一个新的已分配分区对象
     * 
     * @param startAddress 分区的起始地址，从父类继承
     * @param size 分区的大小（单位：字节），从父类继承
     * @param jobName 占用此分区的作业名称，必须是唯一的标识符
     */
    public AllocatedPartition(int startAddress, int size, String jobName) {
        super(startAddress, size);
        this.jobName = jobName;
    }

    /**
     * 获取分区状态
     * 实现父类的抽象方法
     * 
     * @return 返回字符串"allocated"，表示这是一个已分配的分区
     */
    @Override
    public String getStatus() {
        return "allocated";
    }
}