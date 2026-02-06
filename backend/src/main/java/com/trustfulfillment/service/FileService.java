package com.trustfulfillment.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传服务接口
 */
public interface FileService {

    /**
     * 上传文件
     * @param file 文件
     * @param folder 子文件夹 (如: docs, images)
     * @return 文件访问URL
     */
    String uploadFile(MultipartFile file, String folder);

    /**
     * 删除文件
     * @param fileUrl 文件URL
     * @return 是否成功
     */
    boolean deleteFile(String fileUrl);
}
