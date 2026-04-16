package com.aiinterview.service.dify.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 文件项类，包含单个文件的详细信息
 */
@Data
public class FileItem {
    /**
     * 文件类型
     * document: TXT, MD, MARKDOWN, PDF, HTML, XLSX, XLS, DOCX, CSV, EML, MSG, PPTX, PPT, XML, EPUB
     * image: JPG, JPEG, PNG, GIF, WEBP, SVG
     * audio: MP3, M4A, WAV, WEBM, AMR
     * video: MP4, MOV, MPEG, MPGA
     * custom: 其他文件类型
     */
    private String type;

    /**
     * 传递方式
     * remote_url: 图片地址
     * local_file: 上传文件
     */
    @JsonProperty("transfer_method")
    private String transferMethod;

    /**
     * 图片地址（仅当传递方式为 remote_url 时使用）
     */
    private String url;

    /**
     * 上传文件 ID（仅当传递方式为 local_file 时使用）
     */
    @JsonProperty("upload_file_id")
    private String uploadFileId;

    /**
     * 构建本地文件项
     * @param uploadFileId 上传文件ID
     * @return FileItem实例
     */
    public static FileItem buildByLocal(String uploadFileId) {
        FileItem fileItem = new FileItem();
        fileItem.type = "document";
        fileItem.transferMethod = "local_file";
        fileItem.uploadFileId = uploadFileId;
        return fileItem;
    }
}
