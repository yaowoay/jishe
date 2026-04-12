package com.aiinterview.model.dto.note;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 笔记视图对象
 */
@Data
public class NoteVO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String category;
    private String tags;
    private List<String> tagList;
    private Integer isPublic;
    private Date createTime;
    private Date updateTime;
}
