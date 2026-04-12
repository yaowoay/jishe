//package com.aiinterview.constants;
//
//import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * ApplicationStatus 常量类测试
// */
//public class ApplicationStatusTest {
//
//    @Test
//    public void testStatusConstants() {
//        // 测试所有状态常量都在20字符限制内
//        assertTrue(ApplicationStatus.PENDING.length() <= 20, "PENDING状态值长度超限");
//        assertTrue(ApplicationStatus.REVIEWED.length() <= 20, "REVIEWED状态值长度超限");
//        assertTrue(ApplicationStatus.INTERVIEW.length() <= 20, "INTERVIEW状态值长度超限");
//        assertTrue(ApplicationStatus.INTERVIEW_ACCEPTED.length() <= 20, "INTERVIEW_ACCEPTED状态值长度超限");
////        assertTrue(ApplicationStatus.INTERVIEW_REJECTED.length() <= 20, "INTERVIEW_REJECTED状态值长度超限");
//        assertTrue(ApplicationStatus.HIRED.length() <= 20, "HIRED状态值长度超限");
//        assertTrue(ApplicationStatus.REJECTED.length() <= 20, "REJECTED状态值长度超限");
//        assertTrue(ApplicationStatus.INTERVIEW_COMPLETED.length() <= 20, "INTERVIEW_COMPLETED状态值长度超限");
//    }
//
//    @Test
//    public void testGetDisplayName() {
//        assertEquals("待处理", ApplicationStatus.getDisplayName(ApplicationStatus.PENDING));
//        assertEquals("已查看", ApplicationStatus.getDisplayName(ApplicationStatus.REVIEWED));
//        assertEquals("面试邀请", ApplicationStatus.getDisplayName(ApplicationStatus.INTERVIEW));
//        assertEquals("已接受面试", ApplicationStatus.getDisplayName(ApplicationStatus.INTERVIEW_ACCEPTED));
//        assertEquals("已拒绝面试", ApplicationStatus.getDisplayName(ApplicationStatus.INTERVIEW_REJECTED));
//        assertEquals("已录用", ApplicationStatus.getDisplayName(ApplicationStatus.HIRED));
//        assertEquals("已拒绝", ApplicationStatus.getDisplayName(ApplicationStatus.REJECTED));
//        assertEquals("面试完成", ApplicationStatus.getDisplayName(ApplicationStatus.INTERVIEW_COMPLETED));
//
//        // 测试未知状态
//        assertEquals("未知", ApplicationStatus.getDisplayName(null));
//        assertEquals("unknown", ApplicationStatus.getDisplayName("unknown"));
//    }
//
//    @Test
//    public void testIsValidStatus() {
//        assertTrue(ApplicationStatus.isValidStatus(ApplicationStatus.PENDING));
//        assertTrue(ApplicationStatus.isValidStatus(ApplicationStatus.REVIEWED));
//        assertTrue(ApplicationStatus.isValidStatus(ApplicationStatus.INTERVIEW));
//        assertTrue(ApplicationStatus.isValidStatus(ApplicationStatus.INTERVIEW_ACCEPTED));
//        assertTrue(ApplicationStatus.isValidStatus(ApplicationStatus.INTERVIEW_REJECTED));
//        assertTrue(ApplicationStatus.isValidStatus(ApplicationStatus.HIRED));
//        assertTrue(ApplicationStatus.isValidStatus(ApplicationStatus.REJECTED));
//        assertTrue(ApplicationStatus.isValidStatus(ApplicationStatus.INTERVIEW_COMPLETED));
//
//        // 测试无效状态
//        assertFalse(ApplicationStatus.isValidStatus(null));
//        assertFalse(ApplicationStatus.isValidStatus("invalid"));
//        assertFalse(ApplicationStatus.isValidStatus("已投递")); // 中文状态值应该无效
//    }
//
//    @Test
//    public void testStatusValues() {
//        // 确保状态值是英文的
//        assertEquals("pending", ApplicationStatus.PENDING);
//        assertEquals("reviewed", ApplicationStatus.REVIEWED);
//        assertEquals("interview", ApplicationStatus.INTERVIEW);
//        assertEquals("accepted", ApplicationStatus.INTERVIEW_ACCEPTED);
//        assertEquals("int_rejected", ApplicationStatus.INTERVIEW_REJECTED);
//        assertEquals("hired", ApplicationStatus.HIRED);
//        assertEquals("rejected", ApplicationStatus.REJECTED);
//        assertEquals("completed", ApplicationStatus.INTERVIEW_COMPLETED);
//    }
//}
