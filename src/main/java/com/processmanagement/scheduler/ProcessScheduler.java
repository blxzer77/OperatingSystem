package com.processmanagement.scheduler;

import com.processmanagement.model.PCB;
import com.processmanagement.model.ProcessState;
import lombok.Getter;

import java.util.*;

/**
 * 进程调度器类
 * 实现了多种进程调度算法和进程管理功能
 */
public class ProcessScheduler {
    private Queue<PCB> readyQueue;      // 就绪队列，存储等待CPU的进程
    private PCB runningProcess;         // 当前正在运行的进程
    private List<PCB> allProcesses;     // 系统中所有进程的列表
    
    @Getter
    private int currentTime;            // 当前系统时间
    private int pidCounter;             // 进程ID计数器
    private SchedulingStrategy strategy; // 当前使用的调度策略

    /**
     * 进程调度器构造函数
     * 初始化各种数据结构和默认值
     */
    public ProcessScheduler() {
        this.readyQueue = new LinkedList<>();
        this.allProcesses = new ArrayList<>();
        this.currentTime = 0;
        this.pidCounter = 0;
        this.strategy = SchedulingStrategy.ROUND_ROBIN; // 默认使用时间片轮转
    }

    /**
     * 获取所有进程的列表
     * @return 返回系统中所有进程的副本
     */
    public List<PCB> getAllProcesses() {
        return new ArrayList<>(allProcesses);
    }

    /**
     * 获取就绪队列中的进程信息
     * @return 返回格式化的就绪队列信息字符串
     */
    public String getReadyQueueInfo() {
        StringBuilder info = new StringBuilder();
        for (PCB pcb : readyQueue) {
            info.append("PID:").append(pcb.getPid())
                .append("(").append(pcb.getProcessName()).append(") ");
        }
        return info.length() > 0 ? info.toString() : "空";
    }

    /**
     * 获取就绪队列的大小
     * @return 返回就绪队列中的进程数量
     */
    public int getReadyQueueSize() {
        return readyQueue.size();
    }

    /**
     * 设置进程调度策略
     * @param strategy 新的调度策略
     */
    public void setSchedulingStrategy(SchedulingStrategy strategy) {
        this.strategy = strategy;
        // 重新组织就绪队列
        reorganizeReadyQueue();
    }

    /**
     * 根据当前调度策略重新组织就绪队列
     * 不同的调度策略有不同的排序方式
     */
    private void reorganizeReadyQueue() {
        List<PCB> processes = new ArrayList<>(readyQueue);
        readyQueue.clear();

        switch (strategy) {
            case FCFS:
                // 先来先服务，保持原有顺序
                readyQueue.addAll(processes);
                break;
            
            case SJF:
                // 短作业优先，按剩余执行时间排序
                processes.sort((p1, p2) -> 
                    (p1.getTotalTime() - p1.getElapsedTime()) - 
                    (p2.getTotalTime() - p2.getElapsedTime()));
                readyQueue.addAll(processes);
                break;
            
            case PRIORITY:
                // 优先级调度，按优先级排序
                processes.sort((p1, p2) -> p2.getPriority() - p1.getPriority());
                readyQueue.addAll(processes);
                break;
            
            case ROUND_ROBIN:
            default:
                // 时间片轮转，保持原有顺序
                readyQueue.addAll(processes);
                break;
        }
    }

    /**
     * 创建新进程
     * @param name 进程名称
     * @param priority 进程优先级
     * @param totalTime 进程总执行时间
     * @return 返回新创建的PCB对象
     */
    public PCB createProcess(String name, int priority, int totalTime) {
        PCB newProcess = new PCB(++pidCounter, name, ProcessState.NEW, priority, totalTime);
        readyQueue.add(newProcess);
        allProcesses.add(newProcess);
        newProcess.setState(ProcessState.READY);
        
        // 根据调度策略重新组织队列
        if (strategy != SchedulingStrategy.ROUND_ROBIN) {
            reorganizeReadyQueue();
        }
        
        return newProcess;
    }

    /**
     * 进行进程调度
     * 根据当前调度策略选择下一个要运行的进程
     */
    public void schedule() {
        if (runningProcess == null && !readyQueue.isEmpty()) {
            switch (strategy) {
                case ROUND_ROBIN:
                case FCFS:
                case SJF:
                case PRIORITY:
                    // 所有策略都从队列头部取进程
                    runningProcess = readyQueue.poll();
                    break;
            }
            runningProcess.setState(ProcessState.RUNNING);
        }
    }

    /**
     * 推进系统时间
     * 更新进程状态和处理时间片
     */
    public void advanceTime() {
        currentTime++;

        if (runningProcess != null) {
            runningProcess.incrementElapsedTime();

            // 检查进程是否完成
            if (runningProcess.getElapsedTime() >= runningProcess.getTotalTime()) {
                runningProcess.setState(ProcessState.TERMINATED);
                runningProcess = null;
                schedule();  // 调度下一个进程
                return;
            }
            // 对于时间片轮转，检查时间片是否用完
            else if (strategy == SchedulingStrategy.ROUND_ROBIN && 
                     runningProcess.getElapsedTime() % runningProcess.getTimeSlice() == 0) {
                readyQueue.add(runningProcess);
                runningProcess.setState(ProcessState.READY);
                runningProcess = null;
            }
        }

        // 根据当前策略重新组织队列
        if (strategy == SchedulingStrategy.SJF || strategy == SchedulingStrategy.PRIORITY) {
            reorganizeReadyQueue();
        }

        schedule();
    }

    /**
     * 打印所有进程的状态
     */
    public void printAllProcesses() {
        System.out.println("\n当前时间： " + currentTime);
        System.out.println("所有进程状态：");
        for (PCB pcb : allProcesses) {
            System.out.println(pcb);
        }
        System.out.println();
    }

    /**
     * 根据进程ID查找进程
     * @param pid 进程ID
     * @return 返回找到的进程，如果未找到返回null
     */
    public PCB findProcessByPid(int pid) {
        return allProcesses.stream()
                .filter(p -> p.getPid() == pid)
                .findFirst()
                .orElse(null);
    }

    /**
     * 销毁指定进程
     * @param pid 要销毁的进程ID
     * @return 返回是否成功销毁
     */
    public boolean destroyProcess(int pid) {
        PCB process = findProcessByPid(pid);
        if (process == null) return false;

        if (runningProcess != null && runningProcess.getPid() == pid) {
            runningProcess = null;
        }
        readyQueue.removeIf(p -> p.getPid() == pid);
        process.setState(ProcessState.TERMINATED);
        
        // 从所有进程列表中移除
        allProcesses.removeIf(p -> p.getPid() == pid);
        System.out.println("进程 " + pid + " 已被完全移除");
        return true;
    }

    /**
     * 更新进程优先级
     * @param pid 进程ID
     * @param newPriority 新的优先级
     * @return 返回是否成功更新
     */
    public boolean updateProcessPriority(int pid, int newPriority) {
        if (newPriority < 1 || newPriority > 10) return false;
        
        PCB process = findProcessByPid(pid);
        if (process == null) return false;

        process.setPriority(newPriority);
        return true;
    }

    /**
     * 打印指定进程的状态
     * @param pid 进程ID
     */
    public void printProcess(int pid) {
        PCB process = findProcessByPid(pid);
        if (process != null) {
            System.out.println("\n当前时间: " + currentTime);
            System.out.println("进程状态：");
            System.out.println(process);
            System.out.println();
        } else {
            System.out.println("未找到PID为 " + pid + " 的进程");
        }
    }

    /**
     * 打印多个进程的状态
     * @param pids 进程ID数组
     */
    public void printProcesses(int[] pids) {
        System.out.println("\n当前时间: " + currentTime);
        System.out.println("进程状态：");
        for (int pid : pids) {
            PCB process = findProcessByPid(pid);
            if (process != null) {
                System.out.println(process);
            } else {
                System.out.println("未找到PID为 " + pid + " 的进程");
            }
        }
        System.out.println();
    }

    /**
     * 根据进程名称查找进程
     * @param name 进程名称
     * @return 返回匹配名称的进程列表
     */
    public List<PCB> findProcessesByName(String name) {
        return allProcesses.stream()
                .filter(p -> p.getProcessName().equals(name))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 打印指定名称的所有进程
     * @param name 进程名称
     */
    public void printProcessesByName(String name) {
        List<PCB> processes = findProcessesByName(name);
        if (!processes.isEmpty()) {
            System.out.println("\n当前时间: " + currentTime);
            System.out.println("进程状态：");
            for (PCB process : processes) {
                System.out.println(process);
            }
            System.out.println();
        }
    }

    // 修改进程总执行时间
    public boolean updateProcessTotalTime(int pid, int newTotalTime) {
        if (newTotalTime <= 0) return false;
        
        PCB process = findProcessByPid(pid);
        if (process == null) return false;

        // 如果新的总时间小于已执行时间，则不允许修改
        if (newTotalTime < process.getElapsedTime()) return false;

        process.setTotalTime(newTotalTime);
        return true;
    }

    // 查看就绪队列
    public void printReadyQueue() {
        System.out.println("\n当前就绪队列状态：");
        if (readyQueue.isEmpty()) {
            System.out.println("就绪队列为空");
            return;
        }
        
        int position = 1;
        for (PCB process : readyQueue) {
            System.out.println(position++ + ". " + process);
        }
        System.out.println();
    }

    // 调整进程在就绪队列中的位置（将指定进程移到队首）
    public boolean moveProcessToFront(int pid) {
        PCB process = findProcessByPid(pid);
        if (process == null || process.getState() != ProcessState.READY) {
            return false;
        }

        // 创建新的就绪队列
        Queue<PCB> newQueue = new LinkedList<>();
        boolean found = false;

        // 找到指定进程并将其放到队首
        for (PCB p : readyQueue) {
            if (p.getPid() == pid) {
                newQueue.add(p);
                found = true;
            }
        }

        // 添加其他进程
        for (PCB p : readyQueue) {
            if (p.getPid() != pid) {
                newQueue.add(p);
            }
        }

        if (found) {
            readyQueue = newQueue;
            return true;
        }
        return false;
    }

    // 将进程插入就绪队列的指定位置
    public boolean insertProcessAt(int pid, int position) {
        if (position < 1 || position > readyQueue.size() + 1) {
            return false;
        }

        PCB process = findProcessByPid(pid);
        if (process == null || process.getState() != ProcessState.READY) {
            return false;
        }

        // 创建新的就绪队列
        Queue<PCB> newQueue = new LinkedList<>();
        List<PCB> tempList = new ArrayList<>(readyQueue);
        
        // 从原队列中移除要插入的进程
        tempList.removeIf(p -> p.getPid() == pid);
        
        // 在指定位置插入进程
        tempList.add(position - 1, process);
        newQueue.addAll(tempList);
        
        readyQueue = newQueue;
        return true;
    }
}
