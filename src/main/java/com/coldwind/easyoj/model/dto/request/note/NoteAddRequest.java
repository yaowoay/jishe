package com.coldwind.easyoj.model.dto.request.note;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
public class NoteAddRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200字符")
    private String title;
    
    private String content;
    private String category;
    private String tags;
    private Integer isPublic = 0;
}
