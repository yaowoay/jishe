package com.coldwind.easyoj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.coldwind.easyoj.model.entity.note.Note;
import org.apache.ibatis.annotations.Param;

/**
 * 笔记数据访问层
 * 提供笔记相关的数据库操作方法
 */
public interface NoteMapper extends BaseMapper<Note> {
    
    /**
     * 分页查询用户笔记
     * 支持按标题、内容、分类、标签进行搜索，支持自定义排序
     *
     * @param page 分页参数
     * @param userId 用户ID
     * @param keyword 搜索关键词（可选）
     * @param category 分类筛选（可选）
     * @param tags 标签筛选（可选）
     * @param sortField 排序字段（可选）
     * @param sortOrder 排序方向（可选）
     * @return 分页结果
     */
    IPage<Note> selectNotesByPage(
            @Param("page") Page<Note> page,
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            @Param("category") String category,
            @Param("tags") String tags,
            @Param("sortField") String sortField,
            @Param("sortOrder") String sortOrder
    );
    
    /**
     * 根据分类统计笔记数量
     * 
     * @param userId 用户ID
     * @return 分类统计结果
     */
    java.util.List<java.util.Map<String, Object>> countNotesByCategory(@Param("userId") Long userId);
    
    /**
     * 获取用户的所有标签
     * 
     * @param userId 用户ID
     * @return 标签列表
     */
    java.util.List<String> selectAllTagsByUserId(@Param("userId") Long userId);
}
