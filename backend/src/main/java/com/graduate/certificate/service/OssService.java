package com.graduate.certificate.service;

import com.aliyun.sdk.service.oss2.OSSClient;
import com.aliyun.sdk.service.oss2.models.DeleteObjectRequest;
import com.aliyun.sdk.service.oss2.models.PutObjectRequest;
import com.aliyun.sdk.service.oss2.transport.BinaryData;
import com.graduate.certificate.common.exception.BusinessException;
import com.graduate.certificate.config.OssConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

/**
 * OSS文件服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OssService {

    private final OSSClient ossClient;
    private final OssConfig ossConfig;

    /**
     * 上传头像文件
     *
     * @param file 头像文件
     * @return 文件访问URL
     */
    public String uploadAvatar(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "上传文件不能为空");
        }

        // 校验文件类型
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        if (!isImageExtension(extension)) {
            throw new BusinessException(400, "仅支持jpg/jpeg/png/gif格式的图片");
        }

        // 生成唯一文件名
        String objectKey = ossConfig.getPrefix() + "/" + UUID.randomUUID().toString().replace("-", "") + "." + extension;

        try (InputStream inputStream = file.getInputStream()) {
            PutObjectRequest putRequest = PutObjectRequest.newBuilder()
                    .bucket(ossConfig.getBucket())
                    .key(objectKey)
                    .body(BinaryData.fromStream(inputStream, file.getSize()))
                    .contentType(file.getContentType())
                    .build();

            ossClient.putObject(putRequest);
            log.info("上传头像成功: {}", objectKey);

            // 返回公网访问URL
            return ossConfig.getBaseUrl() + "/" + objectKey;
        } catch (Exception e) {
            log.error("上传头像失败: {}", e.getMessage(), e);
            throw new BusinessException(500, "上传头像失败: " + e.getMessage());
        }
    }

    /**
     * 删除OSS文件
     *
     * @param fileUrl 文件完整URL
     */
    public void deleteFile(String fileUrl) {
        if (!StringUtils.hasText(fileUrl)) {
            return;
        }

        try {
            // 从URL中提取objectKey
            String objectKey = fileUrl.replace(ossConfig.getBaseUrl() + "/", "");

            DeleteObjectRequest deleteRequest = DeleteObjectRequest.newBuilder()
                    .bucket(ossConfig.getBucket())
                    .key(objectKey)
                    .build();

            ossClient.deleteObject(deleteRequest);
            log.info("删除OSS文件成功: {}", objectKey);
        } catch (Exception e) {
            log.warn("删除OSS文件失败: {}", e.getMessage());
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (!StringUtils.hasText(filename) || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 校验是否为图片扩展名
     */
    private boolean isImageExtension(String extension) {
        return "jpg".equals(extension) || "jpeg".equals(extension)
                || "png".equals(extension) || "gif".equals(extension);
    }
}
