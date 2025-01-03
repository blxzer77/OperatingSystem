package com.filemanagement.service;

import com.filemanagement.algorithm.*;
import com.filemanagement.model.SchedulingResult;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 磁盘调度服务类
 * 该类负责管理磁盘调度的核心功能，包括：
 * 1. 设置和管理当前磁头位置
 * 2. 选择和配置磁盘调度算法
 * 3. 从文件读取磁道访问序列
 * 4. 执行磁盘调度并返回结果
 */
public class DiskScheduler {
    // 当前使用的磁盘调度算法
    private DiskSchedulingAlgorithm algorithm;
    // 磁头当前位置
    private int currentPosition;

    /**
     * 默认构造函数
     * 初始化磁头位置为0
     */
    public DiskScheduler() {
        this(0);
    }

    /**
     * 带参数构造函数
     * @param currentPosition 初始磁头位置
     */
    public DiskScheduler(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    /**
     * 设置磁盘调度算法
     * 支持FCFS（先来先服务）、SSTF（最短寻道时间优先）和SCAN（电梯算法）
     * @param algorithmType 算法类型的字符串表示
     * @throws IllegalArgumentException 当指定了不支持的算法类型时抛出
     */
    public void setAlgorithm(String algorithmType) {
        switch (algorithmType.toUpperCase()) {
            case "FCFS":
                algorithm = new FCFSAlgorithm();
                break;
            case "SSTF":
                algorithm = new SSTFAlgorithm();
                break;
            case "SCAN":
                algorithm = new ScanAlgorithm();
                break;
            default:
                throw new IllegalArgumentException("不支持的调度算法: " + algorithmType);
        }
    }

    /**
     * 直接设置调度算法对象
     * @param algorithm 具体的调度算法实现
     */
    public void setAlgorithm(DiskSchedulingAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * 从文件读取磁道访问序列
     * 支持两种格式：
     * 1. 以空格或逗号分隔的数字
     * 2. 连续的数字字符串
     * @param filePath 文件路径
     * @return 解析出的磁道序列数组
     * @throws IOException 文件读取错误时抛出
     * @throws IllegalStateException 文件为空时抛出
     */
    public int[] readTrackSequenceFromFile(String filePath) throws IOException {
        List<Integer> tracks = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean hasContent = false;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    hasContent = true;
                    // 处理以空格或逗号分隔的数字
                    if (line.contains(",") || line.contains(" ")) {
                        String[] numbers = line.split("[,\\s]+");
                        for (String number : numbers) {
                            number = number.trim();
                            if (!number.isEmpty()) {
                                tracks.add(Integer.parseInt(number));
                            }
                        }
                    } else {
                        // 处理连续的数字字符串
                        for (char c : line.toCharArray()) {
                            if (Character.isDigit(c)) {
                                tracks.add(Character.getNumericValue(c));
                            }
                        }
                    }
                }
            }
            if (!hasContent) {
                throw new IllegalStateException("文件为空");
            }
        }
        return tracks.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * 执行磁盘调度（从文件读取序列）
     * @param filePath 包含磁道序列的文件路径
     * @return 调度结果，包含访问顺序和总寻道距离
     * @throws IOException 文件读取错误时抛出
     * @throws IllegalStateException 未设置调度算法或序列为空时抛出
     */
    public SchedulingResult executeDiskScheduling(String filePath) throws IOException {
        if (algorithm == null) {
            throw new IllegalStateException("请先设置调度算法");
        }

        int[] trackSequence = readTrackSequenceFromFile(filePath);
        if (trackSequence.length == 0) {
            throw new IllegalStateException("没有可调度的磁道序列");
        }
        return algorithm.schedule(currentPosition, trackSequence);
    }

    /**
     * 执行磁盘调度（直接使用磁道列表）
     * @param startPosition 起始磁头位置
     * @param trackList 待访问的磁道列表
     * @return 调度结果，包含访问顺序和总寻道距离
     * @throws IllegalStateException 未设置调度算法或序列为空时抛出
     */
    public SchedulingResult executeDiskScheduling(int startPosition, List<Integer> trackList) {
        if (algorithm == null) {
            throw new IllegalStateException("请先设置调度算法");
        }

        if (trackList == null || trackList.isEmpty()) {
            throw new IllegalStateException("没有可调度的磁道序列");
        }

        int[] trackSequence = trackList.stream().mapToInt(Integer::intValue).toArray();
        return algorithm.schedule(startPosition, trackSequence);
    }

    /**
     * 设置当前磁头位置
     * @param position 新的磁头位置（0-199）
     * @throws IllegalArgumentException 位置超出有效范围时抛出
     */
    public void setCurrentPosition(int position) {
        if (position < 0 || position > 199) {
            throw new IllegalArgumentException("磁头位置必须在0-199之间");
        }
        this.currentPosition = position;
    }

    /**
     * 设置SCAN算法的移动方向
     * @param direction true表示向大磁道号方向移动，false表示向小磁道号方向移动
     */
    public void setScanDirection(boolean direction) {
        if (algorithm instanceof ScanAlgorithm) {
            ((ScanAlgorithm) algorithm).setDirection(direction);
        }
    }

    /**
     * 获取当前磁头位置
     * @return 当前磁头位置
     */
    public int getCurrentPosition() {
        return currentPosition;
    }
} 