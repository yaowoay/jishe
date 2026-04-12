package com.aiinterview.model.dto.note;

import lombok.Data;
import java.io.Serializable;

/**
 * 笔记查询请求DTO
 */
@Data
public class NoteQueryRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 当前页码
     */
    private int current = 1;
    
    /**
     * 页面大小
     */
    private int pageSize = 10;
    
    /**
     * 搜索关键词 - 支持标题、内容、标签搜索
     */
    private String keyword;
    
    /**
     * 分类筛选
     */
    private String category;
    
    /**
     * 标签筛选
     */
    private String tags;
    
    /**
     * 排序字段 - 默认按更新时间倒序
     */
    private String sortField = "updateTime";
    
    /**
     * 排序方向 - asc/desc
     */
    private String sortOrder = "desc";
}
