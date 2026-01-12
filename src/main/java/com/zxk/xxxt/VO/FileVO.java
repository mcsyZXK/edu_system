package com.zxk.xxxt.VO;

import lombok.Data;
import java.io.Serializable;

/**
 * 文件上传视图对象
 */
@Data
public class FileVO implements Serializable {
    /**
     * 文件名
     */
    private String fileName;

    /**
     * 原始文件名
     */
    private String originalFileName;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件访问URL
     */
    private String fileUrl;

    /**
     * 文件类型
     */
    private String contentType;

    /**
     * 文件扩展名
     */
    private String fileExtension;
}

