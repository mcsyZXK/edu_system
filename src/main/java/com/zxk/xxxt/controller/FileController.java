package com.zxk.xxxt.controller;

import com.alibaba.fastjson.JSONObject;
import com.zxk.xxxt.Utils.JsonUtils;
import com.zxk.xxxt.VO.FileVO;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/file")
public class FileController {

    /**
     * 文件上传根目录
     */
    private static final String UPLOAD_DIR = "D:/uploads";

    /**
     * 文件访问基础路径（根据实际部署情况调整）
     */
    private static final String FILE_BASE_URL = "D:/uploads";

    /**
     * 上传文件
     */
    @PostMapping("/upload")
    public JSONObject uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        try {
            // 验证文件是否为空
            if (file == null || file.isEmpty()) {
                return JsonUtils.fail("文件不能为空");
            }

            // 获取原始文件名
            String originalFileName = file.getOriginalFilename();
            if (originalFileName == null || originalFileName.isEmpty()) {
                return JsonUtils.fail("文件名不能为空");
            }

            // 验证文件大小（限制为50MB）
            long maxSize = 50 * 1024 * 1024; // 50MB
            if (file.getSize() > maxSize) {
                return JsonUtils.fail("文件大小不能超过50MB");
            }

            // 获取文件扩展名
            String fileExtension = "";
            int lastDotIndex = originalFileName.lastIndexOf(".");
            if (lastDotIndex > 0) {
                fileExtension = originalFileName.substring(lastDotIndex);
            }

            // 生成唯一文件名：日期目录 + UUID + 扩展名
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            String dateDir = dateFormat.format(new Date());
            String uuid = UUID.randomUUID().toString().replace("-", "");
            String fileName = uuid + fileExtension;

            // 构建保存路径
            String saveDir = UPLOAD_DIR + File.separator + dateDir;
            Path dirPath = Paths.get(saveDir);
            
            // 如果目录不存在，创建目录
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            // 保存文件
            Path filePath = dirPath.resolve(fileName);
            file.transferTo(filePath.toFile());

            // 构建文件访问URL
            String fileUrl = FILE_BASE_URL + "/" + dateDir.replace(File.separator, "/") + "/" + fileName;

            // 构建返回对象
            FileVO fileVO = new FileVO();
            fileVO.setFileName(fileName);
            fileVO.setOriginalFileName(originalFileName);
            fileVO.setFileSize(file.getSize());
            fileVO.setFileUrl(fileUrl);
            fileVO.setContentType(file.getContentType());
            fileVO.setFileExtension(fileExtension);

            return JsonUtils.success(fileVO);
        } catch (Exception e) {
            return JsonUtils.fail("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 下载/访问文件
     */
    @GetMapping("/download/**")
    public org.springframework.http.ResponseEntity<org.springframework.core.io.Resource> downloadFile(
            HttpServletRequest request) {
        try {
            // 获取请求路径
            String requestPath = request.getRequestURI();
            // 移除context-path（如果有）
            String contextPath = request.getContextPath();
            if (contextPath != null && !contextPath.isEmpty() && requestPath.startsWith(contextPath)) {
                requestPath = requestPath.substring(contextPath.length());
            }
            // 移除接口前缀 "/file/download/"
            String pathPrefix = "/file/download/";
            int prefixIndex = requestPath.indexOf(pathPrefix);
            if (prefixIndex < 0) {
                return org.springframework.http.ResponseEntity.badRequest().build();
            }
            String relativePath = requestPath.substring(prefixIndex + pathPrefix.length());

            // 构建文件完整路径
            String filePath = UPLOAD_DIR + File.separator + relativePath.replace("/", File.separator);
            Path path = Paths.get(filePath);

            // 验证文件是否存在
            if (!Files.exists(path) || !Files.isRegularFile(path)) {
                return org.springframework.http.ResponseEntity.notFound().build();
            }

            // 验证文件路径安全性（防止路径遍历攻击）
            Path uploadDir = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();
            Path normalizedPath = path.toAbsolutePath().normalize();
            if (!normalizedPath.startsWith(uploadDir)) {
                return org.springframework.http.ResponseEntity.badRequest().build();
            }

            // 读取文件
            org.springframework.core.io.Resource resource = new org.springframework.core.io.FileSystemResource(path);
            
            // 获取文件类型
            String contentType = Files.probeContentType(path);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return org.springframework.http.ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, 
                            "inline; filename=\"" + path.getFileName().toString() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.status(
                    org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/delete")
    public JSONObject deleteFile(@RequestParam("fileUrl") String fileUrl) {
        try {
            if (fileUrl == null || fileUrl.isEmpty()) {
                return JsonUtils.fail("文件URL不能为空");
            }

            // 从URL中提取相对路径
            String relativePath = fileUrl;
            if (fileUrl.contains(FILE_BASE_URL)) {
                relativePath = fileUrl.substring(fileUrl.indexOf(FILE_BASE_URL) + FILE_BASE_URL.length());
                if (relativePath.startsWith("/")) {
                    relativePath = relativePath.substring(1);
                }
            }

            // 构建文件完整路径
            String filePath = UPLOAD_DIR + File.separator + relativePath.replace("/", File.separator);
            Path path = Paths.get(filePath);

            // 验证文件路径安全性
            Path uploadDir = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();
            Path normalizedPath = path.toAbsolutePath().normalize();
            if (!normalizedPath.startsWith(uploadDir)) {
                return JsonUtils.fail("无效的文件路径");
            }

            // 删除文件
            if (Files.exists(path)) {
                Files.delete(path);
                return JsonUtils.success("文件删除成功");
            } else {
                return JsonUtils.fail("文件不存在");
            }
        } catch (Exception e) {
            return JsonUtils.fail("文件删除失败: " + e.getMessage());
        }
    }
}

