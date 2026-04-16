package com.aiinterview.service.dify.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * Dify 文件信息
 */
@Data
public class FileInfo implements Serializable {
    private String id;
    private String name;
    private int size;
    private String extension;
    private String mimeType;
    private int createdBy;
    private long createdAt;
}
