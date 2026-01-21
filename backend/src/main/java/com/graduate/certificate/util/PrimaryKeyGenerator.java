package com.graduate.certificate.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 业务主键生成器
 * 生成规则：前缀 + 年份 + 顺序号
 */
@Slf4j
public class PrimaryKeyGenerator {

    /**
     * 用户主键前缀
     */
    public static final String USER_PREFIX = "U";

    /**
     * 学生主键前缀
     */
    public static final String STUDENT_PREFIX = "S";

    /**
     * 教师主键前缀
     */
    public static final String TEACHER_PREFIX = "T";

    /**
     * 证明模板主键前缀
     */
    public static final String CERTIFICATE_TEMPLATE_PREFIX = "CT";

    /**
     * 证明申请主键前缀
     */
    public static final String CERTIFICATE_APPLICATION_PREFIX = "CA";

    /**
     * 审批记录主键前缀
     */
    public static final String APPROVAL_RECORD_PREFIX = "AR";

    /**
     * 系统配置主键前缀
     */
    public static final String SYSTEM_CONFIG_PREFIX = "CFG";

    /**
     * 操作日志主键前缀
     */
    public static final String OPERATION_LOG_PREFIX = "OPL";

    /**
     * 序列号计数器（内存计数，用于高并发时减少数据库查询）
     */
    private static final AtomicInteger sequence = new AtomicInteger(0);

    /**
     * 生成用户主键
     * 格式：U + 年份(4位) + 序号(6位)，如：U2025000001
     *
     * @return 用户主键
     */
    public static String generateUserKey() {
        return generateKey(USER_PREFIX, 4, 6);
    }

    /**
     * 生成学生主键
     * 格式：S + 年份(4位) + 序号(6位)，如：S2021000001
     *
     * @return 学生主键
     */
    public static String generateStudentKey() {
        return generateKey(STUDENT_PREFIX, 4, 6);
    }

    /**
     * 生成教师主键
     * 格式：T + 年份(4位) + 序号(6位)，如：T2023000001
     *
     * @return 教师主键
     */
    public static String generateTeacherKey() {
        return generateKey(TEACHER_PREFIX, 4, 6);
    }

    /**
     * 生成证明模板主键
     * 格式：CT + 年份(4位) + 序号(5位)，如：CT202500001
     *
     * @return 证明模板主键
     */
    public static String generateCertificateTemplateKey() {
        return generateKey(CERTIFICATE_TEMPLATE_PREFIX, 4, 5);
    }

    /**
     * 生成证明申请主键
     * 格式：CA + 年份(4位) + 序号(7位)，如：CA2025000001
     *
     * @return 证明申请主键
     */
    public static String generateCertificateApplicationKey() {
        return generateKey(CERTIFICATE_APPLICATION_PREFIX, 4, 7);
    }

    /**
     * 生成审批记录主键
     * 格式：AR + 年份(4位) + 序号(8位)，如：AR20250000001
     *
     * @return 审批记录主键
     */
    public static String generateApprovalRecordKey() {
        return generateKey(APPROVAL_RECORD_PREFIX, 4, 8);
    }

    /**
     * 生成系统配置主键
     * 格式：CFG + 序号(6位)，如：CFG000001
     *
     * @return 系统配置主键
     */
    public static String generateSystemConfigKey() {
        return generateKeyWithoutYear(SYSTEM_CONFIG_PREFIX, 6);
    }

    /**
     * 生成操作日志主键
     * 格式：OPL + 年份(4位) + 序号(9位)，如：OPL202500000001
     *
     * @return 操作日志主键
     */
    public static String generateOperationLogKey() {
        return generateKey(OPERATION_LOG_PREFIX, 4, 9);
    }

    /**
     * 生成主键（带年份）
     *
     * @param prefix        前缀
     * @param yearLength    年份位数（2位或4位）
     * @param sequenceLength 序号位数
     * @return 主键
     */
    private static synchronized String generateKey(String prefix, int yearLength, int sequenceLength) {
        // 获取当前年份
        String year = getCurrentYear(yearLength);
        
        // 获取并递增序列号
        int seq = sequence.incrementAndGet();
        
        // 格式化序列号
        String sequenceStr = String.format("%0" + sequenceLength + "d", seq);
        
        // 拼接主键
        String key = prefix + year + sequenceStr;
        
        log.debug("生成主键: {}", key);
        return key;
    }

    /**
     * 生成主键（不带年份）
     *
     * @param prefix        前缀
     * @param sequenceLength 序号位数
     * @return 主键
     */
    private static synchronized String generateKeyWithoutYear(String prefix, int sequenceLength) {
        // 获取并递增序列号
        int seq = sequence.incrementAndGet();
        
        // 格式化序列号
        String sequenceStr = String.format("%0" + sequenceLength + "d", seq);
        
        // 拼接主键
        String key = prefix + sequenceStr;
        
        log.debug("生成主键: {}", key);
        return key;
    }

    /**
     * 获取当前年份
     *
     * @param length 年份位数（2位或4位）
     * @return 年份字符串
     */
    private static String getCurrentYear(int length) {
        LocalDateTime now = LocalDateTime.now();
        if (length == 2) {
            return now.format(DateTimeFormatter.ofPattern("yy"));
        } else {
            return now.format(DateTimeFormatter.ofPattern("yyyy"));
        }
    }

    /**
     * 从数据库获取下一个序列号（用于重启后的序列号恢复）
     * 
     * @param mapper Mapper接口
     * @param prefix 主键前缀
     * @param yearLength 年份位数
     * @param <T> 实体类型
     * @return 下一个序列号
     */
    public static <T> int getNextSequenceFromDB(BaseMapper<T> mapper, String prefix, int yearLength) {
        try {
            String year = getCurrentYear(yearLength);
            String likePattern = prefix + year + "%";
            
            QueryWrapper<T> wrapper = new QueryWrapper<>();
            wrapper.likeRight("pk_user", likePattern)
                   .orderByDesc("pk_user")
                   .last("LIMIT 1");
            
            T entity = mapper.selectOne(wrapper);
            if (entity == null) {
                return 0;
            }
            
            // 从主键中提取序列号
            // 示例：从 "U2025000001" 提取 "000001"
            String pkValue = entity.toString(); // 需要根据实际情况调整
            int prefixLength = prefix.length() + yearLength;
            String seqStr = pkValue.substring(prefixLength);
            
            return Integer.parseInt(seqStr);
        } catch (Exception e) {
            log.error("从数据库获取序列号失败", e);
            return 0;
        }
    }

    /**
     * 重置序列号（仅用于测试）
     */
    public static void resetSequence() {
        sequence.set(0);
    }
}
