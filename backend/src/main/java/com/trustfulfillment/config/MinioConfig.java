package com.trustfulfillment.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO 配置类
 */
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {

    private static final Logger log = LoggerFactory.getLogger(MinioConfig.class);

    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private String urlPrefix;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }

    public void setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }

    @Bean
    public MinioClient minioClient() {
        try {
            MinioClient client = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();

            // 检查并创建 bucket
            boolean exists = client.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());

            if (!exists) {
                client.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
                log.info("MinIO bucket '{}' 创建成功", bucketName);
            }

            // 始终设置 bucket 公共读取策略（确保文件可以公开访问）
            String policy = """
                {
                    "Version": "2012-10-17",
                    "Statement": [
                        {
                            "Effect": "Allow",
                            "Principal": {"AWS": ["*"]},
                            "Action": ["s3:GetObject"],
                            "Resource": ["arn:aws:s3:::%s/*"]
                        }
                    ]
                }
                """.formatted(bucketName);

            client.setBucketPolicy(SetBucketPolicyArgs.builder()
                    .bucket(bucketName)
                    .config(policy)
                    .build());
            log.info("MinIO bucket '{}' 公共访问策略已设置", bucketName);

            log.info("MinIO 客户端初始化成功: {}", endpoint);
            return client;
        } catch (Exception e) {
            log.error("MinIO 客户端初始化失败: {}", e.getMessage());
            // 返回一个空的客户端，避免应用启动失败
            return MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();
        }
    }
}
