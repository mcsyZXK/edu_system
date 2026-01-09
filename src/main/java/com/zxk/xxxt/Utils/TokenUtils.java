package com.zxk.xxxt.Utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class TokenUtils {

    // 配置参数
    private static Long ACCESS_TOKEN_EXPIRE;  // 短token过期时间
    private static Long REFRESH_TOKEN_EXPIRE;  // 长token过期时间.
    private static String TOKEN_SECRET;  // token秘钥
    @Value("${token_config.access_expire}")
    public void setAccessTokenExpire(long accessTokenExpire) { TokenUtils.ACCESS_TOKEN_EXPIRE=accessTokenExpire; }
    @Value("${token_config.refresh_expire}")
    public void setRefreshTokenExpire(long refreshTokenExpire){ TokenUtils.REFRESH_TOKEN_EXPIRE=refreshTokenExpire; }
    @Value("${token_config.token_secret}")
    public void setTokenSecret(String tokenSecret){ TokenUtils.TOKEN_SECRET=tokenSecret; }
    // Redis键前缀
    private static final String ACCESS_TOKEN_KEY = "token:access:";
    private static final String REFRESH_TOKEN_KEY = "token:refresh:";
    private static final String BLACKLIST_KEY = "token:blacklist:";

    // Spring管理的实例
    private static TokenUtils instance;
    private RedisTemplate<String, Object> redisTemplate;

    public TokenUtils(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        TokenUtils.instance = this;
    }

    /**
     * 生成Access Token（短Token）
     * 
     * @param id 用户ID（学生主键ID）
     * @param username 用户名
     * @return String 生成的JWT access token字符串
     * 
     * 功能说明：
     * 1. 生成一个短期的JWT token（过期时间由ACCESS_TOKEN_EXPIRE配置）
     * 2. Token中包含用户ID和用户名信息
     * 3. 将token存储到Redis中，用于后续验证
     * 
     * Token内容：
     * - id: 用户主键ID
     * - username: 用户名
     * - exp: 过期时间（当前时间 + ACCESS_TOKEN_EXPIRE毫秒）
     * 
     * Redis存储：
     * - Key: "token:access:" + id
     * - Value: token字符串
     * - TTL: ACCESS_TOKEN_EXPIRE毫秒
     */
    public static String generateAccessToken(String id, String username) {
            // 检查配置和实例是否已初始化
            if (ACCESS_TOKEN_EXPIRE == null) {
                throw new RuntimeException("ACCESS_TOKEN_EXPIRE配置未初始化");
            }
            if (TOKEN_SECRET == null || TOKEN_SECRET.isEmpty()) {
                throw new RuntimeException("TOKEN_SECRET配置未初始化");
            }
            if (instance == null || instance.redisTemplate == null) {
                throw new RuntimeException("TokenUtils实例或RedisTemplate未初始化");
            }
            
            // 计算过期时间：当前时间 + 配置的过期时长
            Date expireDate = new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE);

            // 创建JWT token，包含用户信息和过期时间
            String token = JWT.create()
                    .withClaim("id", id)           // 用户主键ID
                    .withClaim("username", username)   // 用户名
                    .withExpiresAt(expireDate)     // 过期时间
                    .sign(Algorithm.HMAC256(TOKEN_SECRET));  // 使用HMAC256算法和密钥签名

            // 将token存入Redis，用于后续验证和注销
            String redisKey = ACCESS_TOKEN_KEY + id;
            instance.redisTemplate.opsForValue().set(
                    redisKey,
                    token,
                    ACCESS_TOKEN_EXPIRE,
                    TimeUnit.MILLISECONDS
            );

            return token;  // 返回生成的token字符串

    }

    /**
     * 生成Refresh Token（长Token）
     * 
     * @param id 用户ID（学生主键ID）
     * @param username 用户名
     * @return String 生成的JWT refresh token字符串
     * 
     * 功能说明：
     * 1. 生成一个长期的JWT token（过期时间由REFRESH_TOKEN_EXPIRE配置，通常比access token长）
     * 2. Token中包含用户ID、用户名和类型标识
     * 3. 将token存储到Redis中，用于刷新access token
     * 
     * Token内容：
     * - id: 用户主键ID
     * - username: 用户名
     * - type: "refresh"（标识这是refresh token）
     * - exp: 过期时间（当前时间 + REFRESH_TOKEN_EXPIRE毫秒）
     * 
     * Redis存储：
     * - Key: "token:refresh:" + id
     * - Value: refresh token字符串
     * - TTL: REFRESH_TOKEN_EXPIRE毫秒
     */
    public static String generateRefreshToken(String id, String username) {
            // 检查配置和实例是否已初始化
            if (REFRESH_TOKEN_EXPIRE == null) {
                throw new RuntimeException("REFRESH_TOKEN_EXPIRE配置未初始化");
            }
            if (TOKEN_SECRET == null || TOKEN_SECRET.isEmpty()) {
                throw new RuntimeException("TOKEN_SECRET配置未初始化");
            }
            if (instance == null || instance.redisTemplate == null) {
                throw new RuntimeException("TokenUtils实例或RedisTemplate未初始化");
            }
            
            // 计算过期时间：当前时间 + 配置的过期时长（通常比access token长）
            Date expireDate = new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE);

            // 创建JWT refresh token
            String refreshToken = JWT.create()
                    .withClaim("id", id)           // 用户主键ID
                    .withClaim("username", username)   // 用户名
                    .withClaim("type", "refresh")  // 标识为refresh token
                    .withExpiresAt(expireDate)     // 过期时间
                    .sign(Algorithm.HMAC256(TOKEN_SECRET));  // 使用HMAC256算法和密钥签名

            // 将refresh token存入Redis
            String redisKey = REFRESH_TOKEN_KEY + id;
            instance.redisTemplate.opsForValue().set(
                    redisKey,
                    refreshToken,
                    REFRESH_TOKEN_EXPIRE,
                    TimeUnit.MILLISECONDS
            );

            return refreshToken;  // 返回生成的refresh token字符串

    }

    /**
     * 生成Token对（Access + Refresh）
     * 
     * @param id 用户ID（学生主键ID）
     * @param username 用户名
     * @return Map<String, String> 包含两个token的Map
     *         - "accessToken": 短期访问token
     *         - "refreshToken": 长期刷新token
     * 
     * 功能说明：
     * 同时生成access token和refresh token，通常用于用户登录时返回
     * 
     * 使用场景：
     * - 用户登录成功后，返回token对
     * - access token用于日常API请求
     * - refresh token用于刷新access token
     */
    public static Map<String, String> generateTokenPair(String id, String username) {
        Map<String, String> tokenPair = new HashMap<>();
        tokenPair.put("accessToken", generateAccessToken(id, username));  // 生成短期token
        tokenPair.put("refreshToken", generateRefreshToken(id, username));  // 生成长期token
        return tokenPair;  // 返回包含两个token的Map
    }

    /**
     * 验证Token是否无效
     * 
     * @param token 待验证的JWT token字符串
     * @return boolean 
     *         - true: 表示token无效（已加入黑名单、JWT验证失败、Redis中不存在、或发生异常）
     *         - false: 表示token有效（通过所有验证：不在黑名单、JWT签名正确、Redis中存在且匹配）
     * 
     * 验证流程：
     * 1. 检查token是否在黑名单中（已注销的token会被加入黑名单）
     * 2. 验证JWT签名和过期时间（使用TOKEN_SECRET验证签名，自动检查过期时间）
     * 3. 检查Redis中是否存在该token（确保token未被删除或替换）
     *    - Access Token: 检查Redis中存储的token是否与当前token一致
     *    - Refresh Token: 检查Redis中存储的refresh token是否与当前token一致
     * 
     * 使用示例：
     * if (TokenUtils.isTokenInvalid(token)) {
     *     // token无效，拒绝请求
     * } else {
     *     // token有效，允许访问
     * }
     */
    public static boolean isTokenInvalid(String token) {
        try {
            // 检查实例和配置是否已初始化
            if (instance == null || instance.redisTemplate == null) {
                return true;  // 实例未初始化，token无效
            }
            if (TOKEN_SECRET == null || TOKEN_SECRET.isEmpty()) {
                return true;  // 密钥未配置，token无效
            }
            
            // 1. 检查黑名单
            // 如果token在黑名单中，说明该token已被注销，返回true表示无效
            if (instance.redisTemplate.hasKey(BLACKLIST_KEY + token)) {
                return true;  // token在黑名单中，无效
            }

            // 2. 验证JWT签名和过期时间
            // verify方法会验证：签名是否正确、是否过期
            // 如果验证失败会抛出异常，被catch捕获后返回true
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).build();
            DecodedJWT decodedJWT = verifier.verify(token);

            // 3. 检查Redis中是否存在该token
            // 从JWT中解析用户ID和token类型
            String userId = decodedJWT.getClaim("id").asString();
            if (userId == null) {
                userId = decodedJWT.getClaim("userId").asString();  // 兼容处理
            }
            String type = decodedJWT.getClaim("type").asString();

            if (type == null) {  
                // Access Token：检查Redis中存储的access token是否与当前token一致
                String redisKey = ACCESS_TOKEN_KEY + userId;
                String storedToken = (String) instance.redisTemplate.opsForValue().get(redisKey);
                // 如果Redis中不存在或token不匹配，返回true表示无效
                return !token.equals(storedToken);
            } else {  
                // Refresh Token：检查Redis中存储的refresh token是否与当前token一致
                String redisKey = REFRESH_TOKEN_KEY + userId;
                String storedToken = (String) instance.redisTemplate.opsForValue().get(redisKey);
                // 如果Redis中不存在或token不匹配，返回true表示无效
                return !token.equals(storedToken);
            }

        } catch (Exception e) {
            // 任何异常（JWT验证失败、解析错误等）都表示token无效
            return true;  // 发生异常，token无效
        }
    }

    /**
     * 刷新Token（用refresh token换新的access token）
     * 
     * @param refreshToken refresh token字符串
     * @return String 新生成的access token
     * @throws RuntimeException 当refresh token无效、过期或Redis中不存在时抛出异常
     * 
     * 功能说明：
     * 1. 验证refresh token的有效性（签名、过期时间）
     * 2. 确认token类型为"refresh"
     * 3. 检查Redis中是否存在该refresh token
     * 4. 如果验证通过，生成新的access token并返回
     * 
     * 使用场景：
     * - 当access token过期时，使用refresh token获取新的access token
     * - 不需要用户重新登录
     */
    public static String refreshAccessToken(String refreshToken) {
        try {
            // 1. 验证refresh token的JWT签名和过期时间
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).build();
            DecodedJWT decodedJWT = verifier.verify(refreshToken);

            // 2. 从token中解析用户信息
            String userId = decodedJWT.getClaim("id").asString();
            if (userId == null) {
                userId = decodedJWT.getClaim("userId").asString();
            }
            String username = decodedJWT.getClaim("username").asString();
            String type = decodedJWT.getClaim("type").asString();

            // 3. 确认是refresh token类型
            if (!"refresh".equals(type)) {
                throw new RuntimeException("无效的Refresh Token");
            }

            // 4. 检查Redis中是否存在该refresh token
            String redisKey = REFRESH_TOKEN_KEY + userId;
            String storedRefreshToken = (String) instance.redisTemplate.opsForValue().get(redisKey);

            // 5. 如果Redis中不存在或token不匹配，说明token已失效
            if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
                throw new RuntimeException("Refresh Token已失效");
            }

            // 6. 验证通过，生成新的access token
            return generateAccessToken(userId, username);

        } catch (Exception e) {
            throw new RuntimeException("刷新Token失败: " + e.getMessage());
        }
    }

    /**
     * 从Token解析用户信息
     * 
     * @param token JWT token字符串
     * @return Map<String, String> 包含用户信息的Map
     *         - "userId": 用户ID（从"id"或"userId"字段获取）
     *         - "username": 用户名
     *         - "type": token类型（如果存在，如"refresh"）
     * @throws RuntimeException 当token格式错误、签名无效或过期时抛出异常
     * 
     * 功能说明：
     * 1. 验证token的JWT签名和过期时间
     * 2. 从token中提取用户信息（ID、用户名、类型等）
     * 3. 返回包含用户信息的Map
     * 
     * 注意：
     * - 会先尝试从"id"字段获取用户ID，如果不存在则尝试"userId"字段
     * - 如果token过期或签名无效，会抛出异常
     */
    public static Map<String, String> parseToken(String token) {
        try {
            // 1. 验证token的JWT签名和过期时间
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).build();
            DecodedJWT decodedJWT = verifier.verify(token);

            Map<String, String> userInfo = new HashMap<>();
            
            // 2. 获取用户ID（生成token时使用的是"id"字段）
            String id = decodedJWT.getClaim("id").asString();
            if (id != null) {
                userInfo.put("userId", id);  // 统一使用"userId"作为key
            }
            // 兼容处理：如果"id"不存在，尝试获取"userId"字段（向后兼容）
            if (id == null) {
                String userId = decodedJWT.getClaim("userId").asString();
                if (userId != null) {
                    userInfo.put("userId", userId);
                }
            }
            
            // 3. 获取用户名
            userInfo.put("username", decodedJWT.getClaim("username").asString());

            // 4. 获取token类型（如果存在）
            String type = decodedJWT.getClaim("type").asString();
            if (type != null) {
                userInfo.put("type", type);
            }

            return userInfo;  // 返回用户信息Map

        } catch (Exception e) {
            throw new RuntimeException("Token解析失败", e);
        }
    }

    /**
     * 登出（删除所有token）
     * 
     * @param accessToken 用户的access token
     * @throws RuntimeException 当token无效或登出失败时抛出异常
     * 
     * 功能说明：
     * 1. 从token中解析用户ID
     * 2. 将access token加入黑名单（防止token被继续使用）
     * 3. 删除Redis中存储的access token和refresh token
     * 
     * 执行结果：
     * - access token被加入黑名单，后续验证会失败
     * - Redis中的token被删除，无法再刷新
     * - 用户需要重新登录才能获取新token
     */
    public static void logout(String accessToken) {
        try {
            // 1. 从access token解析用户ID
            Map<String, String> userInfo = parseToken(accessToken);
            String userId = userInfo.get("userId");

            if (userId == null) {
                throw new RuntimeException("无效的Token");
            }

            // 2. 将access token加入黑名单（防止token被继续使用）
            addToBlacklist(accessToken);

            // 3. 删除Redis中存储的token
            instance.redisTemplate.delete(ACCESS_TOKEN_KEY + userId);  // 删除access token
            instance.redisTemplate.delete(REFRESH_TOKEN_KEY + userId);  // 删除refresh token

        } catch (Exception e) {
            throw new RuntimeException("登出失败", e);
        }
    }

    /**
     * 将token加入黑名单
     * 
     * @param token 要加入黑名单的token字符串
     * 
     * 功能说明：
     * 1. 解析token的过期时间
     * 2. 计算token的剩余有效时间（TTL）
     * 3. 如果token还未过期，将其加入Redis黑名单
     * 4. 黑名单的过期时间与token的过期时间一致
     * 
     * 黑名单作用：
     * - 防止已注销的token被继续使用
     * - 在token过期前，验证时会检查黑名单
     * - token过期后，黑名单记录也会自动删除
     * 
     * Redis存储：
     * - Key: "token:blacklist:" + token
     * - Value: "1"（标识存在）
     * - TTL: token的剩余有效时间
     */
    private static void addToBlacklist(String token) {
        try {
            // 1. 解析token的过期时间（不验证签名，只解码）
            DecodedJWT decodedJWT = JWT.decode(token);
            Date expireDate = decodedJWT.getExpiresAt();
            
            // 2. 计算token的剩余有效时间（毫秒）
            long ttl = expireDate.getTime() - System.currentTimeMillis();

            // 3. 如果token还未过期，加入黑名单
            if (ttl > 0) {
                String blacklistKey = BLACKLIST_KEY + token;
                instance.redisTemplate.opsForValue().set(
                        blacklistKey,
                        "1",  // 值固定为"1"，表示存在
                        ttl,  // 过期时间与token一致
                        TimeUnit.MILLISECONDS
                );
            }
            // 如果token已过期，不需要加入黑名单（已自动失效）
        } catch (Exception e) {
            // token格式错误，无法解析，不加入黑名单（不影响流程）
        }
    }

    /**
     * 从Token中获取用户ID
     * 
     * @param token JWT token字符串
     * @return String 用户ID，如果token无效或解析失败则可能返回null
     * @throws RuntimeException 当token格式错误、签名无效或过期时抛出异常
     * 
     * 功能说明：
     * 便捷方法，从token中提取用户ID
     * 内部调用parseToken方法解析token
     */
    public static String getUserIdFromToken(String token) {
        return parseToken(token).get("userId");
    }

    /**
     * 从Token中获取用户名
     * 
     * @param token JWT token字符串
     * @return String 用户名，如果token无效或解析失败则可能返回null
     * @throws RuntimeException 当token格式错误、签名无效或过期时抛出异常
     * 
     * 功能说明：
     * 便捷方法，从token中提取用户名
     * 内部调用parseToken方法解析token
     */
    public static String getUsernameFromToken(String token) {
        return parseToken(token).get("username");
    }

}