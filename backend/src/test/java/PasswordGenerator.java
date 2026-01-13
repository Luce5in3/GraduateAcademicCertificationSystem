import cn.hutool.crypto.digest.BCrypt;

/**
 * 密码生成工具 - 用于生成测试数据的BCrypt密码
 */
public class PasswordGenerator {
    public static void main(String[] args) {
        String password = "123456";
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        System.out.println("原始密码: " + password);
        System.out.println("BCrypt哈希: " + hash);
        
        // 验证
        boolean isMatch = BCrypt.checkpw(password, hash);
        System.out.println("验证结果: " + isMatch);
    }
}
