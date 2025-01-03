package com.processmanagement.scheduler;

/**
 * 进程调度策略枚举类
 * 定义了系统支持的四种基本进程调度算法
 */
public enum SchedulingStrategy {
    /**
     * 时间片轮转调度(Round Robin)
     * - 每个进程分配固定大小的时间片
     * - 时间片用完后进程被切换到就绪队列末尾
     * - 公平对待所有进程，适合交互式系统
     */
    ROUND_ROBIN,    

    /**
     * 先来先服务(First Come First Serve)
     * - 按进程到达顺序进行调度
     * - 非抢占式调度算法
     * - 对长作业有利，但可能导致短作业等待时间过长
     */
    FCFS,           

    /**
     * 短作业优先(Shortest Job First)
     * - 选择剩余执行时间最短的进程优先执行
     * - 可以减少平均等待时间
     * - 可能导致长作业饥饿
     */
    SJF,            

    /**
     * 优先级调度(Priority Scheduling)
     * - 按进程优先级进行调度
     * - 高优先级进程优先执行
     * - 需要考虑优先级反转和饥饿问题
     */
    PRIORITY        
} 