package com.virtualmemorymanagement.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class ModelTest {
    
    @Test
    @DisplayName("测试Page类基本功能")
    void testPageBasicFunctionality() {
        Page page = new Page(1);
        
        // 测试初始状态
        assertEquals(1, page.getPageNumber());
        assertEquals(1, page.getAccessCount());
        
        // 测试访问更新
        page.access();
        assertEquals(2, page.getAccessCount());
        
        // 测试统计重置
        page.resetStatistics();
        assertEquals(1, page.getAccessCount());
    }

    @Test
    @DisplayName("测试PageTable类基本功能")
    void testPageTableBasicFunctionality() {
        PageTable pageTable = new PageTable(3);
        
        // 测试初始状态
        assertEquals(3, pageTable.getCapacity());
        assertEquals(0, pageTable.getPageFaults());
        assertTrue(pageTable.getPages().isEmpty());
        
        // 测试添加页面
        Page page1 = new Page(1);
        pageTable.addPage(page1);
        assertEquals(1, pageTable.getPages().size());
        assertTrue(pageTable.contains(1));
        
        // 测试缺页计数
        pageTable.incrementPageFaults();
        assertEquals(1, pageTable.getPageFaults());
    }

    @Test
    @DisplayName("测试PageTable容量限制")
    void testPageTableCapacity() {
        PageTable pageTable = new PageTable(2);
        
        // 添加两个页面（应该成功）
        pageTable.addPage(new Page(1));
        pageTable.addPage(new Page(2));
        
        // 尝试添加第三个页面（应该抛出异常）
        assertThrows(IllegalStateException.class, () -> {
            pageTable.addPage(new Page(3));
        });
    }

    @Test
    @DisplayName("测试PCB类基本功能")
    void testPCBBasicFunctionality() {
        PCB pcb = new PCB(1, 3);
        
        // 测试初始状态
        assertEquals(1, pcb.getPid());
        assertEquals(3, pcb.getAllocatedFrames());
        assertNotNull(pcb.getPageTable());
        assertEquals(3, pcb.getPageTable().getCapacity());
        
        // 测试修改分配帧数
        pcb.setAllocatedFrames(4);
        assertEquals(4, pcb.getAllocatedFrames());
        assertEquals(4, pcb.getPageTable().getCapacity());
    }

    @Test
    @DisplayName("测试Page访问时间戳")
    void testPageTimestamp() {
        Page page = new Page(1);
        long firstTimestamp = page.getLastAccessTime();
        
        // 等待一小段时间后访问
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        page.access();
        long secondTimestamp = page.getLastAccessTime();
        
        // 验证时间戳已更新
        assertTrue(secondTimestamp > firstTimestamp);
    }

    @Test
    @DisplayName("测试PageTable清空功能")
    void testPageTableClear() {
        PageTable pageTable = new PageTable(3);
        
        // 添加页面和缺页计数
        pageTable.addPage(new Page(1));
        pageTable.addPage(new Page(2));
        pageTable.incrementPageFaults();
        pageTable.incrementPageFaults();
        
        // 清空页表
        pageTable.clear();
        
        // 验证状态
        assertTrue(pageTable.getPages().isEmpty());
        assertEquals(0, pageTable.getPageFaults());
        assertEquals(3, pageTable.getCapacity()); // 容量应该保持不变
    }

    @Test
    @DisplayName("测试PageTable异常处理")
    void testPageTableExceptions() {
        PageTable pageTable = new PageTable(2);
        
        // 测试设置负数页面错误
        assertThrows(IllegalArgumentException.class, () -> {
            pageTable.setPageFaults(-1);
        });
        
        // 测试设置小于当前页面数的容量
        pageTable.addPage(new Page(1));
        pageTable.addPage(new Page(2));
        assertThrows(IllegalArgumentException.class, () -> {
            pageTable.setCapacity(1);
        });
    }

    @Test
    @DisplayName("测试Page toString方法")
    void testPageToString() {
        Page page = new Page(42);
        assertEquals("42", page.toString());
    }
} 