package com.example.agent.infrastructure.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 密码加密服务
 * <p>
 * 使用 BCrypt 算法进行密码哈希，strength=12（约 0.5 秒/次）。
 * 不依赖 Spring Security 框架，仅使用独立的 spring-security-crypto 模块。
 * <p>
 * BCrypt 特性：
 * <ul>
 *   <li>自动加盐（每次加密生成随机盐值）</li>
 *   <li>不可逆（无法从哈希还原明文）</li>
 *   <li>strength 越高越安全但越慢（生产建议 12-14）</li>
 * </ul>
 */
@Slf4j
@Component
public class PasswordService {

    /** BCrypt 强度（对数级：2^12 = 4096 轮迭代） */
    private static final int BCRYPT_STRENGTH = 12;

    private final BCryptPasswordEncoder encoder;

    public PasswordService() {
        this.encoder = new BCryptPasswordEncoder(BCRYPT_STRENGTH);
        log.info("[PasswordService] BCrypt 密码编码器初始化完成，strength={}", BCRYPT_STRENGTH);
    }

    /**
     * 对明文密码进行 BCrypt 哈希
     *
     * @param rawPassword 明文密码
     * @return BCrypt 哈希（自动含盐，60 字符）
     */
    public String encode(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            log.warn("[PasswordService] 尝试对空密码进行哈希");
            throw new IllegalArgumentException("密码不能为空");
        }
        String encoded = encoder.encode(rawPassword);
        log.debug("[PasswordService] 密码哈希完成");
        return encoded;
    }

    /**
     * 校验明文密码与 BCrypt 哈希是否匹配
     *
     * @param rawPassword     明文密码（用户输入）
     * @param encodedPassword BCrypt 哈希（数据库存储）
     * @return true=匹配，false=不匹配
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            log.warn("[PasswordService] 密码匹配收到 null 参数");
            return false;
        }
        boolean matched = encoder.matches(rawPassword, encodedPassword);
        if (!matched) {
            log.debug("[PasswordService] 密码验证失败");
        }
        return matched;
    }

    /**
     * 校验密码复杂度
     * <p>
     * 规则：
     * <ul>
     *   <li>最少 8 个字符</li>
     *   <li>至少包含大写字母、小写字母、数字、特殊字符中的 3 种</li>
     * </ul>
     *
     * @param password 待校验的明文密码
     * @throws IllegalArgumentException 密码不满足复杂度要求
     */
    public void validateComplexity(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("密码长度不能少于 8 位");
        }

        int types = 0;
        if (password.matches(".*[A-Z].*")) types++;    // 大写字母
        if (password.matches(".*[a-z].*")) types++;    // 小写字母
        if (password.matches(".*[0-9].*")) types++;    // 数字
        if (password.matches(".*[^a-zA-Z0-9].*")) types++; // 特殊字符

        if (types < 3) {
            throw new IllegalArgumentException(
                    "密码必须包含大写字母、小写字母、数字、特殊字符中的至少 3 种");
        }
    }
}
