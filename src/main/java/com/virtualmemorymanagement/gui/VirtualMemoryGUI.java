package com.virtualmemorymanagement.gui;

import com.virtualmemorymanagement.algorithm.*;
import com.virtualmemorymanagement.model.ReplacementEvent;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 虚拟内存管理系统图形用户界面
 * 提供页面置换算法的可视化演示和监控
 * 功能包括：
 * - 页面置换算法的选择和参数设置
 * - 页面访问序列的加载和执行
 * - 置换过程的实时显示
 * - 性能统计和分析
 */
public class VirtualMemoryGUI extends JFrame {
    private PageReplacementAlgorithm algorithm;    // 页面置换算法
    private JTable pageTable;                      // 页面访问记录表格
    private DefaultTableModel tableModel;          // 表格数据模型
    private JTextArea logArea;                     // 日志显示区域
    private JComboBox<String> algorithmCombo;      // 算法选择下拉框
    private JSpinner framesSpinner;                // 页面帧数选择器
    private JButton fileButton;                    // 文件选择按钮
    private JButton executeButton;                 // 执行置换按钮
    private JPanel memoryStatusPanel;              // 内存状态显示面板
    private Timer timer;                           // 界面更新定时器

    /**
     * 构造函数：初始化GUI界面
     */
    public VirtualMemoryGUI() {
        setTitle("虚拟内存管理系统");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);

        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 创建顶部控制面板
        JPanel controlPanel = createControlPanel();
        mainPanel.add(controlPanel, BorderLayout.NORTH);

        // 创建中间分割面板
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.7);

        // 创建页面表格
        createPageTable();
        JScrollPane tableScrollPane = new JScrollPane(pageTable);
        splitPane.setLeftComponent(tableScrollPane);

        // 创建内存状态面板
        createMemoryStatusPanel();
        splitPane.setRightComponent(memoryStatusPanel);

        mainPanel.add(splitPane, BorderLayout.CENTER);

        // 创建日志区域
        createLogArea();
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setPreferredSize(new Dimension(1000, 150));
        mainPanel.add(logScrollPane, BorderLayout.SOUTH);

        add(mainPanel);
    }

    /**
     * 创建控制面板
     * 包含算法选择、参数设置和控制按钮
     */
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        // 算法选择下拉框
        algorithmCombo = new JComboBox<>(new String[]{"FIFO", "LRU", "LFU"});
        algorithmCombo.addActionListener(e -> {
            String selected = (String)algorithmCombo.getSelectedItem();
            int frames = (Integer)framesSpinner.getValue();
            switch(selected) {
                case "FIFO":
                    algorithm = new FIFOAlgorithm(frames);
                    break;
                case "LRU":
                    algorithm = new LRUAlgorithm(frames);
                    break;
                case "LFU":
                    algorithm = new LFUAlgorithm(frames);
                    break;
            }
            updateMemoryStatus();
        });
        panel.add(new JLabel("置换算法:"));
        panel.add(algorithmCombo);

        // 页面帧数选择器
        SpinnerNumberModel framesModel = new SpinnerNumberModel(3, 1, 10, 1);
        framesSpinner = new JSpinner(framesModel);
        framesSpinner.addChangeListener(e -> {
            if (algorithm != null) {
                String selected = (String)algorithmCombo.getSelectedItem();
                int frames = (Integer)framesSpinner.getValue();
                switch(selected) {
                    case "FIFO":
                        algorithm = new FIFOAlgorithm(frames);
                        break;
                    case "LRU":
                        algorithm = new LRUAlgorithm(frames);
                        break;
                    case "LFU":
                        algorithm = new LFUAlgorithm(frames);
                        break;
                }
                updateMemoryStatus();
            }
        });
        panel.add(new JLabel("页面帧数:"));
        panel.add(framesSpinner);

        // 文件选择按钮
        fileButton = new JButton("选择页面引用序列文件");
        fileButton.addActionListener(e -> selectFile());
        panel.add(fileButton);

        // 执行按钮
        executeButton = new JButton("执行置换");
        executeButton.addActionListener(e -> executeReplacement());
        panel.add(executeButton);

        return panel;
    }

    /**
     * 创建页面访问记录表格
     */
    private void createPageTable() {
        String[] columnNames = {"时间", "页面号", "状态", "置换页面"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        pageTable = new JTable(tableModel);
        pageTable.getTableHeader().setReorderingAllowed(false);
        pageTable.setRowHeight(25);
        pageTable.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
    }

    /**
     * 创建内存状态显示面板
     */
    private void createMemoryStatusPanel() {
        memoryStatusPanel = new JPanel(new BorderLayout(5, 5));
        memoryStatusPanel.setBorder(BorderFactory.createTitledBorder("内存状态"));
        updateMemoryStatus();
    }

    /**
     * 更新内存状态显示
     * 显示当前内存中的页面和相关统计信息
     */
    private void updateMemoryStatus() {
        memoryStatusPanel.removeAll();
        
        if (algorithm != null) {
            // 创建当前内存状态面板
            JPanel currentPagesPanel = new JPanel();
            currentPagesPanel.setLayout(new BoxLayout(currentPagesPanel, BoxLayout.Y_AXIS));
            currentPagesPanel.setBorder(BorderFactory.createTitledBorder("当前内存页面"));
            
            List<Integer> currentPages = algorithm.getCurrentPages();
            for (Integer page : currentPages) {
                JLabel pageLabel = new JLabel("页面 " + page);
                pageLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
                currentPagesPanel.add(pageLabel);
            }
            
            memoryStatusPanel.add(currentPagesPanel, BorderLayout.NORTH);
            
            // 如果是LFU算法，显示页面访问频率
            if (algorithm instanceof LFUAlgorithm) {
                JPanel frequencyPanel = new JPanel();
                frequencyPanel.setLayout(new BoxLayout(frequencyPanel, BoxLayout.Y_AXIS));
                frequencyPanel.setBorder(BorderFactory.createTitledBorder("页面访问频率"));
                
                Map<Integer, Integer> frequencies = ((LFUAlgorithm) algorithm).getPageFrequencies();
                for (Map.Entry<Integer, Integer> entry : frequencies.entrySet()) {
                    JLabel freqLabel = new JLabel(String.format("页面 %d: %d 次", 
                        entry.getKey(), entry.getValue()));
                    freqLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
                    frequencyPanel.add(freqLabel);
                }
                
                memoryStatusPanel.add(frequencyPanel, BorderLayout.CENTER);
            }
            
            // 显示置换历史
            JPanel historyPanel = new JPanel();
            historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.Y_AXIS));
            historyPanel.setBorder(BorderFactory.createTitledBorder("置换历史"));
            
            List<Integer> replacedPages = algorithm.getReplacedPages();
            for (Integer page : replacedPages) {
                JLabel historyLabel = new JLabel("淘汰页面 " + page);
                historyLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
                historyPanel.add(historyLabel);
            }
            
            memoryStatusPanel.add(historyPanel, BorderLayout.SOUTH);
        }
        
        memoryStatusPanel.revalidate();
        memoryStatusPanel.repaint();
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
     * 选择页面引用序列文件
     */
    private void selectFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        int result = fileChooser.showOpenDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            fileButton.setText(selectedFile.getName());
            fileButton.setToolTipText(selectedFile.getAbsolutePath());
        }
    }

    /**
     * 执行页面置换算法
     */
    private void executeReplacement() {
        try {
            // 检查是否已选择文件
            String filePath = fileButton.getToolTipText();
            if (filePath == null) {
                log("错误：请选择页面引用序列文件");
                return;
            }

            // 检查是否已选择算法
            if (algorithm == null) {
                String selected = (String)algorithmCombo.getSelectedItem();
                int frames = (Integer)framesSpinner.getValue();
                switch(selected) {
                    case "FIFO":
                        algorithm = new FIFOAlgorithm(frames);
                        break;
                    case "LRU":
                        algorithm = new LRUAlgorithm(frames);
                        break;
                    case "LFU":
                        algorithm = new LFUAlgorithm(frames);
                        break;
                }
            }

            // 清空表格
            tableModel.setRowCount(0);

            // 执行页面置换
            List<ReplacementEvent> events = algorithm.executeReplacement(filePath);
            displayResults(events);

        } catch (Exception e) {
            log("错误：" + e.getMessage());
        }
    }

    /**
     * 显示页面置换结果
     * @param events 页面置换事件列表
     */
    private void displayResults(List<ReplacementEvent> events) {
        int pageFaults = 0;
        for (int i = 0; i < events.size(); i++) {
            ReplacementEvent event = events.get(i);
            if (event.isPageFault()) {
                pageFaults++;
            }
            
            Object[] rowData = {
                i + 1,
                event.getPageNumber(),
                event.isPageFault() ? "缺页" : "命中",
                event.getReplacedPage() != null ? String.valueOf(event.getReplacedPage()) : "-"
            };
            tableModel.addRow(rowData);
        }
        
        // 显示统计信息
        int totalPages = events.size();
        double faultRate = (double)pageFaults / totalPages * 100;
        log(String.format("置换完成！总页面数：%d，缺页次数：%d，缺页率：%.2f%%", 
            totalPages, pageFaults, faultRate));
            
        // 更新内存状态显示
        updateMemoryStatus();
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
            new VirtualMemoryGUI().setVisible(true);
        });
    }
} 