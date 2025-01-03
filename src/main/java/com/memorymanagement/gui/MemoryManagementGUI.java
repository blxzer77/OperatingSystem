package com.memorymanagement.gui;

import com.memorymanagement.service.*;
import com.memorymanagement.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * 内存管理系统的图形用户界面
 * 提供可视化的内存管理操作界面
 * 
 * 功能：
 * 1. 支持固定分区和可变分区两种管理方式的切换
 * 2. 提供内存分配和释放的操作界面
 * 3. 实时显示内存使用状态
 * 4. 提供操作日志记录
 * 
 * 界面组件：
 * - 顶部：控制面板（分区类型选择、作业信息输入、操作按钮）
 * - 中部：内存状态表格（显示分区信息）
 * - 底部：日志区域（显示操作记录）
 */
public class MemoryManagementGUI extends JFrame {
    // 内存管理器，处理核心业务逻辑
    private MemoryManager memoryManager;
    // 显示内存分区状态的表格
    private JTable memoryTable;
    // 表格数据模型
    private DefaultTableModel tableModel;
    // 显示操作日志的文本区域
    private JTextArea logArea;
    // 作业名称输入框
    private JTextField jobNameField;
    // 内存大小输入框
    private JTextField sizeField;
    // 分区类型选择下拉框
    private JComboBox<String> partitionTypeCombo;

    /**
     * 构造函数
     * 初始化GUI界面和组件
     */
    public MemoryManagementGUI() {
        setTitle("内存管理系统");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 创建顶部控制面板
        JPanel controlPanel = createControlPanel();
        mainPanel.add(controlPanel, BorderLayout.NORTH);

        // 创建内存表格
        createMemoryTable();
        JScrollPane tableScrollPane = new JScrollPane(memoryTable);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        // 创建日志区域
        createLogArea();
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setPreferredSize(new Dimension(800, 150));
        mainPanel.add(logScrollPane, BorderLayout.SOUTH);

        add(mainPanel);

        // 初始化为固定分区
        memoryManager = new FixedPartitionManager();
        updateMemoryTable();
    }

    /**
     * 创建控制面板
     * 包含分区类型选择、作业信息输入和操作按钮
     * 
     * @return 配置好的控制面板
     */
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        // 分区类型选择
        partitionTypeCombo = new JComboBox<>(new String[]{"固定分区", "可变分区"});
        partitionTypeCombo.addActionListener(e -> {
            if (partitionTypeCombo.getSelectedIndex() == 0) {
                memoryManager = new FixedPartitionManager();
            } else {
                memoryManager = new DynamicPartitionManager();
            }
            updateMemoryTable();
        });
        panel.add(new JLabel("分区类型:"));
        panel.add(partitionTypeCombo);

        // 作业名称输入
        jobNameField = new JTextField(10);
        panel.add(new JLabel("作业名称:"));
        panel.add(jobNameField);

        // 内存大小输入
        sizeField = new JTextField(10);
        panel.add(new JLabel("内存大小(K):"));
        panel.add(sizeField);

        // 分配按钮
        JButton allocateButton = new JButton("分配内存");
        allocateButton.addActionListener(e -> allocateMemory());
        panel.add(allocateButton);

        // 释放按钮
        JButton releaseButton = new JButton("释放内存");
        releaseButton.addActionListener(e -> releaseMemory());
        panel.add(releaseButton);

        return panel;
    }

    /**
     * 创建内存状态表格
     * 显示所有分区的详细信息
     */
    private void createMemoryTable() {
        String[] columnNames = {"起始地址", "分区大小", "状态", "作业名"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        memoryTable = new JTable(tableModel);
        memoryTable.getTableHeader().setReorderingAllowed(false);
    }

    /**
     * 创建日志显示区域
     * 用于显示操作结果和错误信息
     */
    private void createLogArea() {
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
    }

    /**
     * 处理内存分配请求
     * 获取输入的作业信息并调用内存管理器进行分配
     */
    private void allocateMemory() {
        try {
            String jobName = jobNameField.getText().trim();
            if (jobName.isEmpty()) {
                log("错误：作业名称不能为空");
                return;
            }

            String sizeStr = sizeField.getText().trim();
            if (sizeStr.isEmpty()) {
                log("错误：内存大小不能为空");
                return;
            }

            int size = (int)(Double.parseDouble(sizeStr) * 1024); // 转换为字节
            boolean success = memoryManager.allocateMemory(jobName, size);
            
            if (success) {
                log("成功：为作业 " + jobName + " 分配 " + sizeStr + "K 内存");
            } else {
                log("失败：无法为作业 " + jobName + " 分配 " + sizeStr + "K 内存");
            }
            
            updateMemoryTable();
        } catch (NumberFormatException e) {
            log("错误：请输入有效的内存大小");
        }
    }

    /**
     * 处理内存释放请求
     * 根据作业名称释放对应的内存空间
     */
    private void releaseMemory() {
        String jobName = jobNameField.getText().trim();
        if (jobName.isEmpty()) {
            log("错误：作业名称不能为空");
            return;
        }

        boolean success = memoryManager.releaseMemory(jobName);
        if (success) {
            log("成功：释放作业 " + jobName + " 的内存");
        } else {
            log("失败：未找到作业 " + jobName + " 的内存");
        }
        
        updateMemoryTable();
    }

    /**
     * 更新内存状态表格
     * 显示当前所有分区的信息
     */
    private void updateMemoryTable() {
        // 清空表格
        tableModel.setRowCount(0);
        
        // 获取内存状态并更新表格
        List<Partition> partitions = memoryManager.getMemoryStatus();
        for (Partition partition : partitions) {
            Object[] rowData = new Object[4];
            rowData[0] = partition.getStartAddress();
            rowData[1] = partition.getSize();
            rowData[2] = partition.getStatus();
            
            if (partition instanceof AllocatedPartition) {
                rowData[3] = ((AllocatedPartition) partition).getJobName();
            } else {
                rowData[3] = "-";
            }
            
            tableModel.addRow(rowData);
        }
        
        // 更新表格外观
        for (int i = 0; i < memoryTable.getColumnCount(); i++) {
            memoryTable.getColumnModel().getColumn(i).setPreferredWidth(150);
        }
        memoryTable.setRowHeight(25);
    }

    /**
     * 添加日志信息
     * 
     * @param message 要显示的日志信息
     */
    private void log(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    /**
     * 程序入口
     * 创建并显示GUI窗口
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MemoryManagementGUI().setVisible(true);
        });
    }
} 