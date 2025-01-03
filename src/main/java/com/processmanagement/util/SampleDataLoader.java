package com.processmanagement.util;

import com.processmanagement.scheduler.ProcessScheduler;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 示例数据加载工具类
 * 用于从配置文件中加载预设的进程数据，方便系统测试和演示
 */
public class SampleDataLoader {
    
    /**
     * 从配置文件加载示例进程数据
     * 配置文件格式：每行一个进程，格式为"进程名称,优先级,总执行时间"
     * 例如：
     * Process1,5,10
     * Process2,3,8
     * 
     * @param scheduler 进程调度器实例
     */
    public static void loadSampleData(ProcessScheduler scheduler) {
        try {
            // 从资源文件夹加载示例数据文件
            InputStream inputStream = SampleDataLoader.class.getResourceAsStream("/sample_processes.txt");
            if (inputStream == null) {
                System.out.println("未找到示例数据文件");
                return;
            }

            // 使用UTF-8编码读取文件内容
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                // 跳过注释行和空行
                if (line.trim().startsWith("#") || line.trim().isEmpty()) {
                    continue;
                }

                // 解析进程数据并创建进程
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String name = parts[0].trim();
                    int priority = Integer.parseInt(parts[1].trim());
                    int totalTime = Integer.parseInt(parts[2].trim());
                    
                    scheduler.createProcess(name, priority, totalTime);
                }
            }
            
            System.out.println("示例数据加载完成！");
            reader.close();
            inputStream.close();
            
        } catch (Exception e) {
            System.out.println("加载示例数据时出错: " + e.getMessage());
        }
    }
} 