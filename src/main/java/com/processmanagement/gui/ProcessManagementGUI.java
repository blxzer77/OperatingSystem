package com.processmanagement.gui;

import com.processmanagement.scheduler.ProcessScheduler;
import com.processmanagement.scheduler.SchedulingStrategy;
import com.processmanagement.model.PCB;
import com.processmanagement.model.ProcessState;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * 进程管理系统图形用户界面
 * 提供进程管理和调度的可视化操作界面
 * 功能包括：
 * - 进程的创建、销毁和监控
 * - 调度算法的选择和切换
 * - 进程状态的实时显示
 * - 系统运行日志的记录
 */
public class ProcessManagementGUI extends JFrame {
    private ProcessScheduler scheduler;         // 进程调度器
    private JTable processTable;                // 进程信息表格
    private DefaultTableModel tableModel;       // 表格数据模型
    private JTextArea logArea;                  // 日志显示区域
    private JComboBox<String> strategyCombo;    // 调度策略选择下拉框
    private JTextField processNameField;         // 进程名称输入框
    private JSpinner prioritySpinner;           // 优先级选择器
    private JSpinner totalTimeSpinner;          // 执行时间选择器
    private JButton createButton;               // 创建进程按钮
    private JButton advanceButton;              // 推进时间按钮
    private JButton destroyButton;              // 销毁进程按钮
    private Timer timer;                        // 界面更新定时器
    private String lastQueueInfo = "";          // 上次就绪队列信息

    /**
     * 构造函数：初始化GUI界面
     */
    public ProcessManagementGUI() {
        setTitle("进程管理系统");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 创建顶部控制面板
        JPanel controlPanel = createControlPanel();
        mainPanel.add(controlPanel, BorderLayout.NORTH);

        // 创建进程表格
        createProcessTable();
        JScrollPane tableScrollPane = new JScrollPane(processTable);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        // 创建日志区域
        createLogArea();
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setPreferredSize(new Dimension(1000, 150));
        mainPanel.add(logScrollPane, BorderLayout.SOUTH);

        add(mainPanel);

        // 初始化调度器
        scheduler = new ProcessScheduler();
        
        // 创建定时器，每秒更新一次显示
        timer = new Timer(1000, e -> updateDisplay());
        timer.start();
    }

    /**
     * 创建控制面板
     * 包含调度算法选择、进程创建参数输入和控制按钮
     */
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        // 调度算法选择
        strategyCombo = new JComboBox<>(new String[]{
            "时间片轮转(RR)", 
            "先来先服务(FCFS)", 
            "短作业优先(SJF)", 
            "优先级调度(Priority)"
        });
        strategyCombo.addActionListener(e -> {
            String selected = (String)strategyCombo.getSelectedItem();
            switch(selected) {
                case "时间片轮转(RR)":
                    scheduler.setSchedulingStrategy(SchedulingStrategy.ROUND_ROBIN);
                    break;
                case "先来先服务(FCFS)":
                    scheduler.setSchedulingStrategy(SchedulingStrategy.FCFS);
                    break;
                case "短作业优先(SJF)":
                    scheduler.setSchedulingStrategy(SchedulingStrategy.SJF);
                    break;
                case "优先级调度(Priority)":
                    scheduler.setSchedulingStrategy(SchedulingStrategy.PRIORITY);
                    break;
            }
            updateDisplay();
        });
        panel.add(new JLabel("调度算法:"));
        panel.add(strategyCombo);

        // 进程名称输入
        processNameField = new JTextField(10);
        panel.add(new JLabel("进程名称:"));
        panel.add(processNameField);

        // 优先级选择
        SpinnerNumberModel priorityModel = new SpinnerNumberModel(5, 1, 10, 1);
        prioritySpinner = new JSpinner(priorityModel);
        panel.add(new JLabel("优先级(1-10):"));
        panel.add(prioritySpinner);

        // 执行时间输入
        SpinnerNumberModel timeModel = new SpinnerNumberModel(10, 1, 100, 1);
        totalTimeSpinner = new JSpinner(timeModel);
        panel.add(new JLabel("执行时间:"));
        panel.add(totalTimeSpinner);

        // 创建进程按钮
        createButton = new JButton("创建进程");
        createButton.addActionListener(e -> createProcess());
        panel.add(createButton);

        // 推进时间按钮
        advanceButton = new JButton("推进时间");
        advanceButton.addActionListener(e -> advanceTime());
        panel.add(advanceButton);

        // 销毁进程按钮
        destroyButton = new JButton("销毁进程");
        destroyButton.addActionListener(e -> destroyProcess());
        panel.add(destroyButton);

        return panel;
    }

    /**
     * 创建进程信息显示表格
     */
    private void createProcessTable() {
        String[] columnNames = {"PID", "名称", "优先级", "状态", "已执行时间", "总执行时间", "剩余时间"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        processTable = new JTable(tableModel);
        processTable.getTableHeader().setReorderingAllowed(false);
    }

    /**
     * 创建日志显示区域
     */
    private void createLogArea() {
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
    }

    /**
     * 创建新进程
     * 从输入框获取进程参数并创建进程
     */
    private void createProcess() {
        try {
            String name = processNameField.getText().trim();
            if (name.isEmpty()) {
                log("错误：进程名称不能为空");
                return;
            }

            int priority = (Integer)prioritySpinner.getValue();
            int totalTime = (Integer)totalTimeSpinner.getValue();

            scheduler.createProcess(name, priority, totalTime);
            log("成功创建进程：" + name);
            updateDisplay();
            
            // 清空输入
            processNameField.setText("");
            prioritySpinner.setValue(5);
            totalTimeSpinner.setValue(10);

        } catch (Exception e) {
            log("错误：" + e.getMessage());
        }
    }

    /**
     * 推进系统时间
     * 触发调度器执行一个时间单位
     */
    private void advanceTime() {
        scheduler.advanceTime();
        updateDisplay();
    }

    /**
     * 销毁选中的进程
     */
    private void destroyProcess() {
        int selectedRow = processTable.getSelectedRow();
        if (selectedRow == -1) {
            log("请先选择要销毁的进程");
            return;
        }

        int pid = (Integer)tableModel.getValueAt(selectedRow, 0);
        if (scheduler.destroyProcess(pid)) {
            log("成功销毁进程 PID: " + pid);
        } else {
            log("销毁进程失败 PID: " + pid);
        }
        updateDisplay();
    }

    /**
     * 更新界面显示
     * 刷新进程表格和就绪队列信息
     */
    private void updateDisplay() {
        // 清空表格
        tableModel.setRowCount(0);
        
        // 获取所有进程并更新显示
        List<PCB> processes = scheduler.getAllProcesses();
        for (PCB process : processes) {
            Object[] rowData = {
                process.getPid(),
                process.getProcessName(),
                process.getPriority(),
                process.getState(),
                process.getElapsedTime(),
                process.getTotalTime(),
                process.getTotalTime() - process.getElapsedTime()
            };
            tableModel.addRow(rowData);
        }
        
        // 只在就绪队列发生变化时才输出信息
        String currentQueueInfo = scheduler.getReadyQueueInfo();
        if (!currentQueueInfo.equals(lastQueueInfo)) {
            log("当前就绪队列：" + currentQueueInfo);
            lastQueueInfo = currentQueueInfo;
        }
    }

    /**
     * 添加日志信息
     * @param message 要添加的日志消息
     */
    private void log(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    /**
     * 程序入口点
     * 设置系统外观并启动GUI
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new ProcessManagementGUI().setVisible(true);
        });
    }
} 