package com.trustfulfillment.service.impl;

import com.trustfulfillment.config.MinioConfig;
import com.trustfulfillment.service.FileService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件上传服务实现 (MinIO)
 */
@Service
public class FileServiceImpl implements FileService {

    private static final Logger log = LoggerFactory.getLogger(FileServiceImpl.class);

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioConfig minioConfig;

    @Override
    public String uploadFile(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }

        // 获取原始文件名和扩展名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 生成新文件名: folder/yyyy/MM/dd/uuid.ext
        String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String newFilename = UUID.randomUUID().toString().replace("-", "") + extension;
        String objectName = folder + "/" + dateDir + "/" + newFilename;

        try (InputStream inputStream = file.getInputStream()) {
            // 上传到 MinIO
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(objectName)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            // 返回文件访问 URL
            String fileUrl = minioConfig.getUrlPrefix() + "/" + objectName;
            log.info("文件上传成功: {}", fileUrl);
            return fileUrl;

        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return false;
        }

        try {
            // 从 URL 中提取对象名
            String urlPrefix = minioConfig.getUrlPrefix() + "/";
            if (!fileUrl.startsWith(urlPrefix)) {
                log.warn("文件URL格式不正确: {}", fileUrl);
                return false;
            }

            String objectName = fileUrl.substring(urlPrefix.length());

            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(objectName)
                    .build());

            log.info("文件删除成功: {}", objectName);
            return true;

        } catch (Exception e) {
            log.error("文件删除失败: {}", e.getMessage(), e);
            return false;
        }
    }
}
