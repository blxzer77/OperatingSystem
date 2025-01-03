package com.processmanagement.scheduler;

import com.processmanagement.model.PCB;
import com.processmanagement.model.ProcessState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 进程调度算法测试类
 * 测试进程调度的基本功能：
 * 1. 进程创建和销毁
 * 2. 进程状态转换
 * 3. 进程优先级和执行时间修改
 * 4. 调度策略切换
 */
public class ProcessSchedulerTest {
    private ProcessScheduler scheduler;

    @BeforeEach
    void setUp() {
        scheduler = new ProcessScheduler();
    }

    @Test
    @DisplayName("测试进程创建功能")
    void testProcessCreation() {
        PCB process = scheduler.createProcess("TestProcess", 5, 10);
        assertNotNull(process, "创建的进程不应为空");
        assertEquals("TestProcess", process.getProcessName(), "进程名称应正确设置");
        assertEquals(5, process.getPriority(), "进程优先级应正确设置");
        assertEquals(10, process.getTotalTime(), "进程总执行时间应正确设置");
        assertEquals(ProcessState.READY, process.getState(), "新创建的进程应处于就绪状态");
    }

    @Test
    @DisplayName("测试进程状态转换")
    void testProcessStateTransition() {
        PCB process = scheduler.createProcess("TestProcess", 5, 10);
        assertEquals(ProcessState.READY, process.getState(), "初始状态应为就绪");

        // 运行进程
        scheduler.setSchedulingStrategy(SchedulingStrategy.FCFS);
        scheduler.advanceTime();
        assertEquals(ProcessState.RUNNING, process.getState(), "进程应转换为运行状态");

        // 完成进程
        for (int i = 0; i < 10; i++) {
            scheduler.advanceTime();
        }
        assertEquals(ProcessState.TERMINATED, process.getState(), "进程应转换为终止状态");
    }

    @Test
    @DisplayName("测试FCFS调度策略")
    void testFCFSScheduling() {
        scheduler.setSchedulingStrategy(SchedulingStrategy.FCFS);
        
        // 创建三个进程
        PCB process1 = scheduler.createProcess("Process1", 1, 3);
        PCB process2 = scheduler.createProcess("Process2", 5, 2);
        PCB process3 = scheduler.createProcess("Process3", 3, 1);

        // 验证FCFS顺序
        scheduler.advanceTime();
        assertEquals(ProcessState.RUNNING, process1.getState(), "第一个创建的进程应先运行");
        
        // 运行完Process1
        scheduler.advanceTime();
        scheduler.advanceTime();
        scheduler.advanceTime();
        assertEquals(ProcessState.TERMINATED, process1.getState(), "Process1应已完成");
        assertEquals(ProcessState.RUNNING, process2.getState(), "Process2应开始运行");
    }

    @Test
    @DisplayName("测试优先级调度策略")
    void testPriorityScheduling() {
        scheduler.setSchedulingStrategy(SchedulingStrategy.PRIORITY);
        
        // 创建三个不同优先级的进程
        PCB process1 = scheduler.createProcess("Process1", 1, 3);
        PCB process2 = scheduler.createProcess("Process2", 5, 2);
        PCB process3 = scheduler.createProcess("Process3", 3, 1);

        // 验证优先级顺序
        scheduler.advanceTime();
        assertEquals(ProcessState.RUNNING, process2.getState(), "优先级最高的进程应先运行");
    }

    @Test
    @DisplayName("测试时间片轮转调度策略")
    void testRoundRobinScheduling() {
        scheduler.setSchedulingStrategy(SchedulingStrategy.ROUND_ROBIN);
        
        // 创建三个进程
        PCB process1 = scheduler.createProcess("Process1", 1, 4);
        PCB process2 = scheduler.createProcess("Process2", 1, 4);
        PCB process3 = scheduler.createProcess("Process3", 1, 4);

        // 验证轮转
        scheduler.advanceTime();
        assertEquals(ProcessState.RUNNING, process1.getState(), "Process1应先运行");
        
        // 时间片到期后应切换到下一个进程
        scheduler.advanceTime();
        scheduler.advanceTime();
        scheduler.advanceTime();
        assertEquals(ProcessState.READY, process1.getState(), "Process1应回到就绪队列");
        assertEquals(ProcessState.RUNNING, process2.getState(), "Process2应开始运行");
    }

    @Test
    @DisplayName("测试进程销毁功能")
    void testProcessDestruction() {
        PCB process = scheduler.createProcess("TestProcess", 5, 10);
        assertTrue(scheduler.destroyProcess(process.getPid()), "进程销毁应成功");
        assertNull(scheduler.findProcessByPid(process.getPid()), "已销毁的进程不应存在");
    }

    @Test
    @DisplayName("测试进程优先级修改")
    void testPriorityUpdate() {
        PCB process = scheduler.createProcess("TestProcess", 5, 10);
        assertTrue(scheduler.updateProcessPriority(process.getPid(), 8), "优先级修改应成功");
        assertEquals(8, process.getPriority(), "优先级应更新为新值");
    }

    @Test
    @DisplayName("测试进程执行时间修改")
    void testTotalTimeUpdate() {
        PCB process = scheduler.createProcess("TestProcess", 5, 10);
        assertTrue(scheduler.updateProcessTotalTime(process.getPid(), 15), "执行时间修改应成功");
        assertEquals(15, process.getTotalTime(), "执行时间应更新为新值");
    }
} 