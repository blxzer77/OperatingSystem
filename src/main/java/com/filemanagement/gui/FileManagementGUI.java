package com.filemanagement.gui;

import com.filemanagement.service.DiskScheduler;
import com.filemanagement.model.SchedulingResult;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 磁盘调度系统的图形用户界面
 * 提供可视化的磁盘调度操作界面
 * 
 * 功能：
 * 1. 支持多种磁盘调度算法（FCFS、SSTF、SCAN）
 * 2. 允许用户设置磁头初始位置
 * 3. 支持从文件读取磁道访问序列
 * 4. 实时显示调度结果和移动距离
 * 5. 提供操作日志记录
 * 
 * 界面组件：
 * - 顶部：控制面板（磁头位置、算法选择、文件选择等）
 * - 中部：结果表格（显示调度过程）
 * - 底部：日志区域（显示操作记录）
 */
public class FileManagementGUI extends JFrame {
    // 磁盘调度器，处理核心调度逻辑
    private DiskScheduler diskScheduler;
    // 显示调度结果的表格
    private JTable resultTable;
    // 表格数据模型
    private DefaultTableModel tableModel;
    // 显示操作日志的文本区域
    private JTextArea logArea;
    // 磁头位置输入框
    private JTextField headPositionField;
    // 调度算法选择下拉框
    private JComboBox<String> algorithmCombo;
    // SCAN算法方向选择复选框
    private JCheckBox directionCheckBox;
    // 文件选择按钮
    private JButton fileButton;
    // 执行调度按钮
    private JButton executeButton;

    /**
     * 构造函数
     * 初始化GUI界面和组件
     */
    public FileManagementGUI() {
        setTitle("磁盘调度系统");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 创建顶部控制面板
        JPanel controlPanel = createControlPanel();
        mainPanel.add(controlPanel, BorderLayout.NORTH);

        // 创建结果表格
        createResultTable();
        JScrollPane tableScrollPane = new JScrollPane(resultTable);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        // 创建日志区域
        createLogArea();
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setPreferredSize(new Dimension(800, 150));
        mainPanel.add(logScrollPane, BorderLayout.SOUTH);

        add(mainPanel);

        // 初始化调度器，设置初始磁头位置为0
        diskScheduler = new DiskScheduler(0);
    }

    /**
     * 创建控制面板
     * 包含磁头位置输入、算法选择、文件选择等控件
     * 
     * @return 配置好的控制面板
     */
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        // 磁头位置输入框
        headPositionField = new JTextField(5);
        panel.add(new JLabel("磁头位置:"));
        panel.add(headPositionField);

        // 调度算法选择下拉框
        algorithmCombo = new JComboBox<>(new String[]{"FCFS", "SSTF", "SCAN"});
        algorithmCombo.addActionListener(e -> {
            // 仅在选择SCAN算法时启用方向选择
            directionCheckBox.setEnabled(algorithmCombo.getSelectedItem().equals("SCAN"));
        });
        panel.add(new JLabel("调度算法:"));
        panel.add(algorithmCombo);

        // SCAN算法的移动方向选择
        directionCheckBox = new JCheckBox("向大磁道方向移动", true);
        directionCheckBox.setEnabled(false);
        panel.add(directionCheckBox);

        // 磁道序列文件选择按钮
        fileButton = new JButton("选择磁道序列文件");
        fileButton.addActionListener(e -> selectFile());
        panel.add(fileButton);

        // 执行调度按钮
        executeButton = new JButton("执行调度");
        executeButton.addActionListener(e -> executeScheduling());
        panel.add(executeButton);

        return panel;
    }

    /**
     * 创建结果显示表格
     * 用于显示调度过程中的磁道访问顺序和移动距离
     */
    private void createResultTable() {
        String[] columnNames = {"序号", "磁道号", "移动距离"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resultTable = new JTable(tableModel);
        resultTable.getTableHeader().setReorderingAllowed(false);
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
     * 处理文件选择
     * 打开文件选择对话框，让用户选择包含磁道序列的文件
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
     * 执行磁盘调度
     * 获取用户输入，配置调度器，执行调度算法
     */
    private void executeScheduling() {
        try {
            // 获取并验证磁头位置
            String headPositionText = headPositionField.getText().trim();
            log("输入的磁头位置: " + headPositionText);
            
            // 确保磁头位置是单个数字
            if (headPositionText.contains(",")) {
                log("错误：磁头位置应该是单个数字");
                return;
            }
            
            int headPosition = Integer.parseInt(headPositionText);
            log("解析后的磁头位置: " + headPosition);
            
            // 验证磁头位置范围
            if (headPosition < 0 || headPosition > 199) {
                log("错误：磁头位置必须在0-199之间");
                return;
            }

            // 设置磁头位置
            diskScheduler.setCurrentPosition(headPosition);

            // 设置选择的调度算法
            String selectedAlgorithm = (String) algorithmCombo.getSelectedItem();
            diskScheduler.setAlgorithm(selectedAlgorithm);

            // 如果是SCAN算法，设置移动方向
            if (selectedAlgorithm.equals("SCAN")) {
                diskScheduler.setScanDirection(directionCheckBox.isSelected());
            }

            // 获取并验证文件路径
            String filePath = fileButton.getToolTipText();
            if (filePath == null) {
                log("错误：请选择磁道序列文件");
                return;
            }

            // 执行调度并显示结果
            SchedulingResult result = diskScheduler.executeDiskScheduling(filePath);
            displayResult(result);

        } catch (NumberFormatException e) {
            log("错误：请输入有效的磁头位置（输入内容可能不是有效数字）");
            log("具体错误：" + e.getMessage());
        } catch (IOException e) {
            log("错误：文件读取失败 - " + e.getMessage());
        } catch (IllegalStateException | IllegalArgumentException e) {
            log("错误：" + e.getMessage());
        }
    }

    /**
     * 显示调度结果
     * 在表格中显示磁道访问顺序和移动距离
     * 
     * @param result 调度算法执行结果
     */
    private void displayResult(SchedulingResult result) {
        // 清空表格
        tableModel.setRowCount(0);
        
        // 添加结果到表格
        List<Integer> sequence = result.getAccessSequence();
        int previousTrack = diskScheduler.getCurrentPosition();
        
        for (int i = 0; i < sequence.size(); i++) {
            int currentTrack = sequence.get(i);
            int movement = Math.abs(currentTrack - previousTrack);
            
            Object[] rowData = {
                i + 1,
                currentTrack,
                movement
            };
            tableModel.addRow(rowData);
            
            previousTrack = currentTrack;
        }
        
        // 显示总移动距离
        log("调度完成！总移动道数：" + result.getTotalMovement());
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
            new FileManagementGUI().setVisible(true);
        });
    }
} 