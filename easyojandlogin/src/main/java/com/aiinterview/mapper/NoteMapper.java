package com.aiinterview.mapper;

import com.aiinterview.model.entity.note.Note;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * 笔记数据访问层
 */
public interface NoteMapper extends BaseMapper<Note> {
    
    /**
     * 分页查询用户笔记
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
     */
    java.util.List<java.util.Map<String, Object>> countNotesByCategory(@Param("userId") Long userId);
    
    /**
     * 获取所有标签
     */
    java.util.List<String> selectAllTagsByUserId(@Param("userId") Long userId);
}
