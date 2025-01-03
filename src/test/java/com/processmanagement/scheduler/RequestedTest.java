package com.processmanagement.scheduler;

import com.processmanagement.model.PCB;
import com.processmanagement.model.ProcessState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 按照老师要求的测试类
 * 测试进程序列：
 * 进程编号    到达时间    需要完成的时间    优先级
 * 1          1          5              1
 * 2          2          5              4
 * 3          3          1              2
 * 4          4          4              5
 */
public class RequestedTest {
    private ProcessScheduler scheduler;

    @BeforeEach
    void setUp() {
        System.out.println("\n=== 初始化进程调度系统 ===");
        System.out.println("创建进程调度器...");
        scheduler = new ProcessScheduler();
        System.out.println("进程调度器初始化完成");
    }

    @Test
    @DisplayName("测试FCFS算法")
    void testFCFS() {
        System.out.println("\n========== 先来先服务（FCFS）调度算法测试 ==========");
        
        // 设置为FCFS算法
        System.out.println("设置调度算法为：先来先服务（FCFS）");
        scheduler.setSchedulingStrategy(SchedulingStrategy.FCFS);

        // 按照到达时间顺序创建进程
        System.out.println("\n>>> 创建进程1（优先级1，执行时间5）");
        PCB process1 = scheduler.createProcess("Process1", 1, 5);
        System.out.println("进程1创建完成，当前状态：" + process1.getState());
        
        scheduler.advanceTime(); // 时间点1
        System.out.println("\n>>> 时间推进到1，Process1开始运行");
        System.out.println("Process1状态：" + process1.getState());

        System.out.println("\n>>> 创建进程2（优先级4，执行时间5）");
        PCB process2 = scheduler.createProcess("Process2", 4, 5);
        scheduler.advanceTime(); // 时间点2
        System.out.println("时间推进到2，当前状态：");
        System.out.println("Process1状态：" + process1.getState());
        System.out.println("Process2状态：" + process2.getState());

        System.out.println("\n>>> 创建进程3（优先级2，执行时间1）");
        PCB process3 = scheduler.createProcess("Process3", 2, 1);
        scheduler.advanceTime(); // 时间点3
        System.out.println("时间推进到3，当前状态：");
        System.out.println("Process1状态：" + process1.getState());
        System.out.println("Process2状态：" + process2.getState());
        System.out.println("Process3状态：" + process3.getState());

        System.out.println("\n>>> 创建进程4（优先级5，执行时间4）");
        PCB process4 = scheduler.createProcess("Process4", 5, 4);
        
        System.out.println("\n>>> 继续运行Process1直到完成");
        scheduler.advanceTime(); // 时间点4
        scheduler.advanceTime(); // 时间点5
        scheduler.advanceTime(); // 时间点6
        
        System.out.println("时间推进到6，检查Process1状态");
        assertEquals(ProcessState.TERMINATED, process1.getState(), "Process1应该已完成");
        System.out.println("Process1已完成，状态：" + process1.getState());
        
        scheduler.advanceTime(); // 让调度器选择下一个进程
        System.out.println("\n>>> 时间推进到7，检查Process2是否开始运行");
        assertEquals(ProcessState.RUNNING, process2.getState(), "Process2应该开始运行");
        System.out.println("Process2开始运行，状态：" + process2.getState());

        System.out.println("\n========== FCFS调度算法测试结束 ==========");
    }

    @Test
    @DisplayName("测试SJF算法")
    void testSJF() {
        System.out.println("\n========== 短作业优先（SJF）调度算法测试 ==========");
        
        // 设置为SJF算法
        System.out.println("设置调度算法为：短作业优先（SJF）");
        scheduler.setSchedulingStrategy(SchedulingStrategy.SJF);

        // 创建所有进程
        System.out.println("\n>>> 创建所有进程");
        System.out.println("创建进程1（执行时间5）");
        PCB process1 = scheduler.createProcess("Process1", 1, 5);
        System.out.println("创建进程2（执行时间5）");
        PCB process2 = scheduler.createProcess("Process2", 4, 5);
        System.out.println("创建进程3（执行时间1）- 最短作业");
        PCB process3 = scheduler.createProcess("Process3", 2, 1);
        System.out.println("创建进程4（执行时间4）");
        PCB process4 = scheduler.createProcess("Process4", 5, 4);

        System.out.println("\n>>> 开始调度执行");
        scheduler.advanceTime(); // 时间点1
        System.out.println("时间推进到1，检查最短作业Process3是否运行");
        assertEquals(ProcessState.RUNNING, process3.getState(), "最短作业Process3应该先运行");
        System.out.println("Process3正在运行，状态：" + process3.getState());

        scheduler.advanceTime(); // 时间点2
        System.out.println("\n>>> 时间推进到2，检查Process3是否完成");
        assertEquals(ProcessState.TERMINATED, process3.getState(), "Process3应该已完成");
        System.out.println("Process3已完成，状态：" + process3.getState());
        
        scheduler.advanceTime(); // 让调度器选择下一个进程
        System.out.println("\n>>> 时间推进到3，检查次短作业Process4是否运行");
        assertEquals(ProcessState.RUNNING, process4.getState(), "次短作业Process4应该开始运行");
        System.out.println("Process4开始运行，状态：" + process4.getState());

        System.out.println("\n========== SJF调度算法测试结束 ==========");
    }

    @Test
    @DisplayName("测试优先级调度算法")
    void testPriority() {
        System.out.println("\n========== 优先级调度算法测试 ==========");
        
        // 设置为优先级调度算法
        System.out.println("设置调度算法为：优先级调度");
        scheduler.setSchedulingStrategy(SchedulingStrategy.PRIORITY);

        // 创建所有进程
        System.out.println("\n>>> 创建所有进程");
        System.out.println("创建进程1（优先级1）");
        PCB process1 = scheduler.createProcess("Process1", 1, 5);
        System.out.println("创建进程2（优先级4）");
        PCB process2 = scheduler.createProcess("Process2", 4, 5);
        System.out.println("创建进程3（优先级2）");
        PCB process3 = scheduler.createProcess("Process3", 2, 1);
        System.out.println("创建进程4（优先级5 - 最高优先级）");
        PCB process4 = scheduler.createProcess("Process4", 5, 4);

        System.out.println("\n>>> 开始调度执行");
        scheduler.advanceTime(); // 时间点1
        System.out.println("时间推进到1，检查最高优先级的Process4是否运行");
        assertEquals(ProcessState.RUNNING, process4.getState(), "最高优先级的Process4应该先运行");
        System.out.println("Process4正在运行，状态：" + process4.getState());

        System.out.println("\n>>> 运行Process4直到完成");
        for (int i = 0; i < 4; i++) {
            scheduler.advanceTime();
            System.out.println("时间推进，Process4已运行" + (i + 2) + "个时间单位");
        }
        assertEquals(ProcessState.TERMINATED, process4.getState(), "Process4应该已完成");
        System.out.println("Process4已完成，状态：" + process4.getState());
        
        scheduler.advanceTime(); // 让调度器选择下一个进程
        System.out.println("\n>>> 检查次高优先级的Process2是否运行");
        assertEquals(ProcessState.RUNNING, process2.getState(), "次高优先级的Process2应该开始运行");
        System.out.println("Process2开始运行，状态：" + process2.getState());

        System.out.println("\n========== 优先级调度算法测试结束 ==========");
    }

    @Test
    @DisplayName("测试时间片轮转算法")
    void testRoundRobin() {
        System.out.println("\n========== 时间片轮转调度算法测试 ==========");
        
        // 设置为时间片轮转算法
        System.out.println("设置调度算法为：时间片轮转");
        scheduler.setSchedulingStrategy(SchedulingStrategy.ROUND_ROBIN);

        // 创建所有进程
        System.out.println("\n>>> 创建所有进程");
        System.out.println("创建进程1（执行时间5）");
        PCB process1 = scheduler.createProcess("Process1", 1, 5);
        System.out.println("创建进程2（执行时间5）");
        PCB process2 = scheduler.createProcess("Process2", 4, 5);
        System.out.println("创建进程3（执行时间1）");
        PCB process3 = scheduler.createProcess("Process3", 2, 1);
        System.out.println("创建进程4（执行时间4）");
        PCB process4 = scheduler.createProcess("Process4", 5, 4);

        System.out.println("\n>>> 开始时间片轮转调度");
        scheduler.advanceTime(); // 时间点1
        System.out.println("时间推进到1，检查Process1是否运行");
        assertEquals(ProcessState.RUNNING, process1.getState(), "Process1应该先运行");
        System.out.println("Process1正在运行，状态：" + process1.getState());

        scheduler.advanceTime(); // 时间点2
        System.out.println("\n>>> 时间推进到2，Process1继续运行");
        assertEquals(ProcessState.RUNNING, process1.getState(), "Process1应该继续运行");
        System.out.println("Process1继续运行，状态：" + process1.getState());

        scheduler.advanceTime(); // 时间点3
        System.out.println("\n>>> 时间推进到3，检查时间片轮转");
        assertEquals(ProcessState.READY, process1.getState(), "Process1应该回到就绪队列");
        assertEquals(ProcessState.RUNNING, process2.getState(), "Process2应该开始运行");
        System.out.println("Process1回到就绪队列，状态：" + process1.getState());
        System.out.println("Process2开始运行，状态：" + process2.getState());

        System.out.println("\n========== 时间片轮转调度算法测试结束 ==========");
    }

    @Test
    @DisplayName("测试PCB正确性")
    void testPCBCorrectness() {
        System.out.println("\n========== PCB正确性测试 ==========");
        
        System.out.println(">>> 创建测试进程");
        PCB process = scheduler.createProcess("TestProcess", 1, 5);
        
        System.out.println("\n>>> 验证PCB字段设置");
        System.out.println("检查进程名称...");
        assertEquals("TestProcess", process.getProcessName(), "进程名称应该正确设置");
        System.out.println("进程名称正确：" + process.getProcessName());
        
        System.out.println("检查优先级...");
        assertEquals(1, process.getPriority(), "优先级应该正确设置");
        System.out.println("优先级正确：" + process.getPriority());
        
        System.out.println("检查总执行时间...");
        assertEquals(5, process.getTotalTime(), "总执行时间应该正确设置");
        System.out.println("总执行时间正确：" + process.getTotalTime());
        
        System.out.println("检查已执行时间...");
        assertEquals(0, process.getElapsedTime(), "已执行时间初始应该为0");
        System.out.println("已执行时间正确：" + process.getElapsedTime());
        
        System.out.println("检查进程状态...");
        assertEquals(ProcessState.READY, process.getState(), "初始状态应该为READY");
        System.out.println("进程状态正确：" + process.getState());
        
        System.out.println("检查时间片...");
        assertEquals(2, process.getTimeSlice(), "时间片应该正确设置");
        System.out.println("时间片正确：" + process.getTimeSlice());

        System.out.println("\n========== PCB正确性测试结束 ==========");
    }

    @Test
    @DisplayName("测试就绪队列正确性")
    void testReadyQueueCorrectness() {
        System.out.println("\n========== 就绪队列正确性测试 ==========");
        
        System.out.println(">>> 创建多个进程");
        System.out.println("创建进程1");
        PCB process1 = scheduler.createProcess("Process1", 1, 5);
        System.out.println("创建进程2");
        PCB process2 = scheduler.createProcess("Process2", 4, 5);
        System.out.println("创建进程3");
        PCB process3 = scheduler.createProcess("Process3", 2, 1);

        System.out.println("\n>>> 验证就绪队列大小");
        assertEquals(3, scheduler.getReadyQueueSize(), "就绪队列应该包含3个进程");
        System.out.println("就绪队列大小正确：" + scheduler.getReadyQueueSize());

        System.out.println("\n>>> 开始运行一个进程");
        scheduler.advanceTime();
        assertEquals(2, scheduler.getReadyQueueSize(), "就绪队列应该剩余2个进程");
        System.out.println("就绪队列大小正确：" + scheduler.getReadyQueueSize());

        System.out.println("\n>>> 测试队列调整功能");
        assertTrue(scheduler.moveProcessToFront(process3.getPid()), "应该能够将进程移到队首");
        System.out.println("成功将Process3移到队首");
        System.out.println("当前就绪队列状态：");
        scheduler.printReadyQueue();

        System.out.println("\n========== 就绪队列正确性测试结束 ==========");
    }
} 