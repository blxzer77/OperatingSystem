package com.processmanagement.model;

/**
 * 进程状态枚举类
 * 定义了进程生命周期中的五种基本状态
 */
public enum ProcessState {
    /**
     * 新建状态：进程刚被创建，尚未加入就绪队列
     */
    NEW,        

    /**
     * 就绪状态：进程已经准备就绪，等待CPU调度
     */
    READY,      

    /**
     * 运行状态：进程正在CPU上运行
     */
    RUNNING,    

    /**
     * 等待状态：进程等待某个事件（如I/O完成）
     */
    WAITING,    

    /**
     * 终止状态：进程已完成或被终止
     */
    TERMINATED  
}
