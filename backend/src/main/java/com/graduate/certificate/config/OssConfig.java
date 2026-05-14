package com.graduate.certificate.config;

import com.aliyun.sdk.service.oss2.OSSClient;
import com.aliyun.sdk.service.oss2.credentials.CredentialsProvider;
import com.aliyun.sdk.service.oss2.credentials.EnvironmentVariableCredentialsProvider;
import com.aliyun.sdk.service.oss2.credentials.StaticCredentialsProvider;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云OSS配置类
 */
@Data
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "oss")
public class OssConfig {

    /**
     * 地域
     */
    private String region;

    /**
     * Bucket名称
     */
    private String bucket;

    /**
     * Endpoint
     */
    private String endpoint;

    /**
     * 外网访问基础URL
     */
    private String baseUrl;

    /**
     * 文件路径前缀
     */
    private String prefix;

    /**
     * 创建OSSClient Bean（单例复用）
     * 优先从环境变量 OSS_ACCESS_KEY_ID / OSS_ACCESS_KEY_SECRET 读取
     * 也支持通过 yml 配置 access-key-id / access-key-secret
     */
    private String accessKeyId;
    private String accessKeySecret;

    @Bean
    public OSSClient ossClient() {
        CredentialsProvider provider;
        // 优先使用yml配置的AK，其次使用环境变量
        if (accessKeyId != null && !accessKeyId.isBlank()
                && accessKeySecret != null && !accessKeySecret.isBlank()) {
            log.info("使用yml配置的AccessKey初始化OSSClient");
            provider = new StaticCredentialsProvider(accessKeyId, accessKeySecret);
        } else {
            log.info("使用环境变量初始化OSSClient");
            provider = new EnvironmentVariableCredentialsProvider();
        }
        log.info("OSS初始化: region={}, endpoint={}, bucket={}", region, endpoint, bucket);
        return OSSClient.newBuilder()
                .credentialsProvider(provider)
                .region(region)
                .endpoint("https://" + endpoint)
                .build();
    }
}
