package com.filemanagement.algorithm;

import com.filemanagement.model.SchedulingResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 电梯算法(SCAN)磁盘调度算法
 * 
 * 算法特点：
 * 1. 模拟电梯运行方式，沿一个方向移动直到没有请求，然后改变方向
 * 2. 在移动过程中顺便服务路径上的磁道访问请求
 * 3. 避免了SSTF算法可能导致的饥饿问题
 * 
 * 优点：
 * - 性能较好，平均寻道时间适中
 * - 避免了饥饿现象
 * - 对于连续性访问效率高
 * 
 * 缺点：
 * - 对于边缘磁道的访问效率较低
 * - 不考虑请求的紧急程度
 */
public class ScanAlgorithm implements DiskSchedulingAlgorithm {
    // 磁盘最大磁道号（0-199）
    private static final int MAX_TRACK = 199;
    // 磁头移动方向：true表示向大磁道号方向移动，false表示向小磁道号方向移动
    private boolean direction = true;

    /**
     * 执行SCAN磁盘调度算法
     * 
     * @param currentPosition 当前磁头位置
     * @param trackSequence 待访问的磁道序列
     * @return 包含访问顺序和总移动距离的调度结果
     */
    @Override
    public SchedulingResult schedule(int currentPosition, int[] trackSequence) {
        // 记录总移动道数
        int totalMovement = 0;
        // 记录当前磁头位置
        int currentTrack = currentPosition;
        // 用于存储访问顺序的数组
        int[] accessSequence = new int[trackSequence.length];
        // 访问序列的索引
        int accessIndex = 0;

        // 将待访问磁道分为两组：大于等于当前位置的和小于当前位置的
        List<Integer> greaterTracks = new ArrayList<>();  // 存储大于等于当前位置的磁道
        List<Integer> lesserTracks = new ArrayList<>();   // 存储小于当前位置的磁道
        
        // 根据磁道号与当前位置的关系分类
        for (int track : trackSequence) {
            if (track >= currentTrack) {
                greaterTracks.add(track);
            } else {
                lesserTracks.add(track);
            }
        }

        // 对两组磁道进行排序
        // 大于当前位置的磁道按升序排序
        Collections.sort(greaterTracks);
        // 小于当前位置的磁道按降序排序，便于反向扫描
        Collections.sort(lesserTracks, Collections.reverseOrder());

        // 根据移动方向决定访问顺序
        if (direction) {
            // 向大磁道方向移动时的处理
            // 先按升序访问大于等于当前位置的磁道
            for (int track : greaterTracks) {
                totalMovement += Math.abs(track - currentTrack);
                currentTrack = track;
                accessSequence[accessIndex++] = track;
            }
            // 到达最大位置后改变方向，按降序访问小于当前位置的磁道
            for (int track : lesserTracks) {
                totalMovement += Math.abs(track - currentTrack);
                currentTrack = track;
                accessSequence[accessIndex++] = track;
            }
        } else {
            // 向小磁道方向移动时的处理
            // 先按降序访问小于当前位置的磁道
            for (int track : lesserTracks) {
                totalMovement += Math.abs(track - currentTrack);
                currentTrack = track;
                accessSequence[accessIndex++] = track;
            }
            // 到达最小位置后改变方向，按升序访问大于等于当前位置的磁道
            for (int track : greaterTracks) {
                totalMovement += Math.abs(track - currentTrack);
                currentTrack = track;
                accessSequence[accessIndex++] = track;
            }
        }

        // 返回调度结果，包含访问顺序和总移动道数
        return new SchedulingResult(accessSequence, totalMovement);
    }

    /**
     * 设置磁头移动方向
     * 
     * @param direction true表示向大磁道号方向移动（递增），false表示向小磁道号方向移动（递减）
     */
    public void setDirection(boolean direction) {
        this.direction = direction;
    }
} 