package com.coldwind.easyoj.model.dto.request.note;

import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 笔记编辑请求DTO
 * 用于接收前端编辑笔记的请求参数
 */
@Data
public class NoteEditRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 笔记ID - 必填
     */
    @NotNull(message = "笔记ID不能为空")
    private Long id;
    
    /**
     * 笔记标题
     */
    @Size(max = 200, message = "标题长度不能超过200字符")
    private String title;
    
    /**
     * 笔记内容
     */
    private String content;
    
    /**
     * 笔记分类
     */
    private String category;
    
    /**
     * 标签 - 多个标签用逗号分隔
     */
    private String tags;
    
    /**
     * 是否公开 - 0:私有 1:公开
     */
    private Integer isPublic;
}
