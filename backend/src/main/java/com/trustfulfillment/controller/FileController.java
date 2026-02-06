package com.trustfulfillment.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.trustfulfillment.common.Result;
import com.trustfulfillment.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/api/file")
@CrossOrigin(origins = "*")
public class FileController {

    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileService fileService;

    // 允许的文件类型
    private static final List<String> ALLOWED_IMAGE_TYPES = List.of(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    private static final List<String> ALLOWED_DOC_TYPES = List.of(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "text/plain"
    );

    // 允许的压缩包类型
    private static final List<String> ALLOWED_ARCHIVE_TYPES = List.of(
            "application/zip",
            "application/x-zip-compressed",
            "application/x-rar-compressed",
            "application/vnd.rar",
            "application/x-7z-compressed",
            "application/x-tar",
            "application/gzip",
            "application/x-gzip",
            "application/x-bzip2",
            "application/octet-stream"  // 某些浏览器会将压缩包识别为此类型
    );

    /**
     * 上传单个文件
     */
    @PostMapping("/upload")
    public Result<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "docs") String folder) {

        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }

        try {
            validateFile(file, folder);
            String url = fileService.uploadFile(file, folder);

            Map<String, String> result = new HashMap<>();
            result.put("url", url);
            result.put("name", file.getOriginalFilename());

            return Result.success("上传成功", result);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 批量上传文件
     */
    @PostMapping("/upload/batch")
    public Result<?> uploadFiles(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "folder", defaultValue = "docs") String folder) {

        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }

        if (files == null || files.length == 0) {
            return Result.error("请选择要上传的文件");
        }

        if (files.length > 10) {
            return Result.error("一次最多上传10个文件");
        }

        try {
            List<Map<String, String>> results = new ArrayList<>();

            for (MultipartFile file : files) {
                validateFile(file, folder);
                String url = fileService.uploadFile(file, folder);

                Map<String, String> item = new HashMap<>();
                item.put("url", url);
                item.put("name", file.getOriginalFilename());
                results.add(item);
            }

            return Result.success("上传成功", results);
        } catch (Exception e) {
            log.error("文件批量上传失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/delete")
    public Result<?> deleteFile(@RequestParam("url") String url) {
        if (!StpUtil.isLogin()) {
            return Result.unauthorized();
        }

        try {
            boolean success = fileService.deleteFile(url);
            if (success) {
                return Result.success("删除成功", null);
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            log.error("文件删除失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }

        // 文件大小限制 (100MB for evidence, 50MB for others)
        long maxSize = "evidence".equals(folder) ? 100 * 1024 * 1024 : 50 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new RuntimeException("文件大小不能超过" + (maxSize / 1024 / 1024) + "MB");
        }

        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();

        // 根据文件夹类型验证文件类型
        if ("images".equals(folder)) {
            if (!ALLOWED_IMAGE_TYPES.contains(contentType)) {
                throw new RuntimeException("只允许上传图片文件 (jpg, png, gif, webp)");
            }
        } else if ("evidence".equals(folder)) {
            // evidence 文件夹（交付物）：允许图片、文档和压缩包
            boolean isImage = ALLOWED_IMAGE_TYPES.contains(contentType);
            boolean isDoc = ALLOWED_DOC_TYPES.contains(contentType);
            boolean isArchive = ALLOWED_ARCHIVE_TYPES.contains(contentType);
            // 额外通过文件扩展名判断压缩包（处理某些浏览器MIME类型不准确的情况）
            boolean isArchiveByExt = filename != null && (
                    filename.toLowerCase().endsWith(".zip") ||
                    filename.toLowerCase().endsWith(".rar") ||
                    filename.toLowerCase().endsWith(".7z") ||
                    filename.toLowerCase().endsWith(".tar") ||
                    filename.toLowerCase().endsWith(".gz") ||
                    filename.toLowerCase().endsWith(".tar.gz") ||
                    filename.toLowerCase().endsWith(".tgz")
            );
            if (!isImage && !isDoc && !isArchive && !isArchiveByExt) {
                throw new RuntimeException("不支持的文件类型，交付物支持图片、文档和压缩包");
            }
        } else {
            // docs 文件夹允许图片、文档和压缩包
            boolean isImage = ALLOWED_IMAGE_TYPES.contains(contentType);
            boolean isDoc = ALLOWED_DOC_TYPES.contains(contentType);
            boolean isArchive = ALLOWED_ARCHIVE_TYPES.contains(contentType);
            if (!isImage && !isDoc && !isArchive) {
                throw new RuntimeException("不支持的文件类型");
            }
        }
    }
}
