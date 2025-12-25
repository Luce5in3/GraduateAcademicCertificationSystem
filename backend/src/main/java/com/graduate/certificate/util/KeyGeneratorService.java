package com.graduate.certificate.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * 主键生成服务（基于Redis，保证分布式环境下的唯一性）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KeyGeneratorService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Redis Key 前缀
     */
    private static final String REDIS_KEY_PREFIX = "pk:seq:";

    /**
     * 生成用户主键
     * 格式：U + 年份(4位) + 序号(6位)，如：U2025000001
     */
    public String generateUserKey() {
        return generateKey("U", 4, 6);
    }

    /**
     * 生成学生主键
     * 格式：S + 年份(4位) + 序号(6位)，如：S2021000001
     */
    public String generateStudentKey() {
        return generateKey("S", 4, 6);
    }

    /**
     * 生成教师主键
     * 格式：T + 年份(4位) + 序号(6位)，如：T2023000001
     */
    public String generateTeacherKey() {
        return generateKey("T", 4, 6);
    }

    /**
     * 生成证明模板主键
     * 格式：CT + 年份(4位) + 序号(5位)，如：CT202500001
     */
    public String generateCertificateTemplateKey() {
        return generateKey("CT", 4, 5);
    }

    /**
     * 生成证明申请主键
     * 格式：CA + 年份(4位) + 序号(7位)，如：CA2025000001
     */
    public String generateCertificateApplicationKey() {
        return generateKey("CA", 4, 7);
    }

    /**
     * 生成审批记录主键
     * 格式：AR + 年份(4位) + 序号(8位)，如：AR20250000001
     */
    public String generateApprovalRecordKey() {
        return generateKey("AR", 4, 8);
    }

    /**
     * 生成系统配置主键
     * 格式：CFG + 序号(6位)，如：CFG000001
     */
    public String generateSystemConfigKey() {
        return generateKeyWithoutYear("CFG", 6);
    }

    /**
     * 生成操作日志主键
     * 格式：OPL + 年份(4位) + 序号(9位)，如：OPL202500000001
     */
    public String generateOperationLogKey() {
        return generateKey("OPL", 4, 9);
    }

    /**
     * 生成主键（带年份）
     *
     * @param prefix         前缀
     * @param yearLength     年份位数（2位或4位）
     * @param sequenceLength 序号位数
     * @return 主键
     */
    private String generateKey(String prefix, int yearLength, int sequenceLength) {
        // 获取当前年份
        String year = getCurrentYear(yearLength);
        
        // Redis Key: pk:seq:{prefix}:{year}
        String redisKey = REDIS_KEY_PREFIX + prefix + ":" + year;
        
        // 使用 Redis 自增获取序列号
        Long sequence = redisTemplate.opsForValue().increment(redisKey, 1);
        
        // 设置过期时间为2年（防止Key永久存在）
        redisTemplate.expire(redisKey, 730, TimeUnit.DAYS);
        
        // 格式化序列号
        String sequenceStr = String.format("%0" + sequenceLength + "d", sequence);
        
        // 拼接主键
        String key = prefix + year + sequenceStr;
        
        log.debug("生成主键: {} (Redis Key: {}, Sequence: {})", key, redisKey, sequence);
        return key;
    }

    /**
     * 生成主键（不带年份）
     *
     * @param prefix         前缀
     * @param sequenceLength 序号位数
     * @return 主键
     */
    private String generateKeyWithoutYear(String prefix, int sequenceLength) {
        // Redis Key: pk:seq:{prefix}
        String redisKey = REDIS_KEY_PREFIX + prefix;
        
        // 使用 Redis 自增获取序列号
        Long sequence = redisTemplate.opsForValue().increment(redisKey, 1);
        
        // 格式化序列号
        String sequenceStr = String.format("%0" + sequenceLength + "d", sequence);
        
        // 拼接主键
        String key = prefix + sequenceStr;
        
        log.debug("生成主键: {} (Redis Key: {}, Sequence: {})", key, redisKey, sequence);
        return key;
    }

    /**
     * 获取当前年份
     *
     * @param length 年份位数（2位或4位）
     * @return 年份字符串
     */
    private String getCurrentYear(int length) {
        LocalDateTime now = LocalDateTime.now();
        if (length == 2) {
            return now.format(DateTimeFormatter.ofPattern("yy"));
        } else {
            return now.format(DateTimeFormatter.ofPattern("yyyy"));
        }
    }

    /**
     * 重置序列号（仅用于测试或初始化）
     *
     * @param prefix 前缀
     */
    public void resetSequence(String prefix) {
        String year = getCurrentYear(4);
        String redisKey = REDIS_KEY_PREFIX + prefix + ":" + year;
        redisTemplate.delete(redisKey);
        log.info("已重置序列号: {}", redisKey);
    }
}
