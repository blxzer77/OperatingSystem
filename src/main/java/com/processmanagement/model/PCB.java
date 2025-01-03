package com.processmanagement.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 进程控制块(Process Control Block)类
 * 用于存储和管理进程的所有相关信息和状态
 * 每个进程在系统中都由一个PCB对象来表示
 */
@Getter
@Setter
public class PCB {
    private int pid;                // 进程唯一标识符
    private String processName;     // 进程名称
    private ProcessState state;     // 进程当前状态（新建、就绪、运行、等待、终止）
    private int priority;           // 进程优先级（数值越大优先级越高）
    private int timeSlice;          // 分配给进程的时间片大小
    private int totalTime;          // 进程需要的总执行时间
    private int elapsedTime;        // 进程已经执行的时间

    /**
     * PCB构造函数
     * @param pid 进程ID
     * @param processName 进程名称
     * @param state 初始进程状态
     * @param priority 进程优先级
     * @param totalTime 进程总执行时间
     */
    public PCB(int pid, String processName, ProcessState state, int priority, int totalTime) {
        this.pid = pid;
        this.processName = processName;
        this.state = ProcessState.NEW;    // 初始状态设置为NEW
        this.priority = priority;
        this.timeSlice = 2;               // 默认时间片为2个时间单位
        this.totalTime = totalTime;
        this.elapsedTime = 0;
    }

    /**
     * 增加进程已执行时间
     * 当执行时间达到总时间时，自动将进程状态设置为终止
     */
    public void incrementElapsedTime() {
        this.elapsedTime++;
        // 如果执行时间达到总时间，自动设置为终止状态
        if (this.elapsedTime >= this.totalTime) {
            this.state = ProcessState.TERMINATED;
        }
    }

    /**
     * 重写toString方法，用于输出进程信息
     * @return 返回格式化的进程信息字符串
     */
    @Override
    public String toString() {
        return String.format("进程ID: %d, 名称: %s, 状态: %s, 优先级: %d, 已执行时间: %d/%d", 
            pid, processName, state, priority, elapsedTime, totalTime);
    }
}
