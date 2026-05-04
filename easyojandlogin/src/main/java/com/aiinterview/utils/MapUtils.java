package com.aiinterview.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Map 工具类
 * 提供便捷的 Map 创建方法，兼容 Java 8
 */
public class MapUtils {

    private MapUtils() {
        // 私有构造函数，防止实例化
    }

    /**
     * 创建包含一个键值对的 Map
     */
    public static <K, V> Map<K, V> of(K key, V value) {
        Map<K, V> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    /**
     * 创建包含两个键值对的 Map
     */
    public static <K, V> Map<K, V> of(K key1, V value1, K key2, V value2) {
        Map<K, V> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

    /**
     * 创建包含三个键值对的 Map
     */
    public static <K, V> Map<K, V> of(K key1, V value1, K key2, V value2, K key3, V value3) {
        Map<K, V> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        return map;
    }

    /**
     * 创建包含四个键值对的 Map
     */
    public static <K, V> Map<K, V> of(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4) {
        Map<K, V> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        map.put(key4, value4);
        return map;
    }

    /**
     * 创建空的 Map
     */
    public static <K, V> Map<K, V> empty() {
        return new HashMap<>();
    }

    /**
     * 构建器模式创建 Map
     */
    public static <K, V> MapBuilder<K, V> builder() {
        return new MapBuilder<>();
    }

    /**
     * Map 构建器类
     */
    public static class MapBuilder<K, V> {
        private final Map<K, V> map = new HashMap<>();

        public MapBuilder<K, V> put(K key, V value) {
            map.put(key, value);
            return this;
        }

        public Map<K, V> build() {
            return new HashMap<>(map);
        }
    }
}
