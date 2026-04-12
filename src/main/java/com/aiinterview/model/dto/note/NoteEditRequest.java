package com.aiinterview.model.dto.note;

import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 笔记编辑请求DTO
 */
@Data
public class NoteEditRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @NotNull(message = "笔记ID不能为空")
    private Long id;
    
    @Size(max = 200, message = "标题长度不能超过200字符")
    private String title;
    
    private String content;
    private String category;
    private String tags;
    private Integer isPublic;
}
