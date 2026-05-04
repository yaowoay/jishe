package com.aiinterview.utils;

import org.junit.jupiter.api.Test;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MapUtils 测试类
 */
public class MapUtilsTest {

    @Test
    public void testOfSingleEntry() {
        Map<String, Object> map = MapUtils.of("key", "value");
        
        assertEquals(1, map.size());
        assertEquals("value", map.get("key"));
    }

    @Test
    public void testOfTwoEntries() {
        Map<String, Object> map = MapUtils.of("key1", "value1", "key2", "value2");
        
        assertEquals(2, map.size());
        assertEquals("value1", map.get("key1"));
        assertEquals("value2", map.get("key2"));
    }

    @Test
    public void testOfThreeEntries() {
        Map<String, Object> map = MapUtils.of("key1", "value1", "key2", "value2", "key3", "value3");
        
        assertEquals(3, map.size());
        assertEquals("value1", map.get("key1"));
        assertEquals("value2", map.get("key2"));
        assertEquals("value3", map.get("key3"));
    }

    @Test
    public void testEmpty() {
        Map<String, Object> map = MapUtils.empty();
        
        assertTrue(map.isEmpty());
        assertEquals(0, map.size());
    }

    @Test
    public void testBuilder() {
        Map<String, Object> map = MapUtils.<String, Object>builder()
                .put("key1", "value1")
                .put("key2", "value2")
                .put("key3", "value3")
                .build();
        
        assertEquals(3, map.size());
        assertEquals("value1", map.get("key1"));
        assertEquals("value2", map.get("key2"));
        assertEquals("value3", map.get("key3"));
    }

    @Test
    public void testBuilderEmpty() {
        Map<String, Object> map = MapUtils.<String, Object>builder().build();
        
        assertTrue(map.isEmpty());
        assertEquals(0, map.size());
    }

    @Test
    public void testWithDifferentTypes() {
        Map<String, Object> map = MapUtils.of("stringKey", "stringValue", "booleanKey", true);
        
        assertEquals(2, map.size());
        assertEquals("stringValue", map.get("stringKey"));
        assertEquals(true, map.get("booleanKey"));
    }
}
