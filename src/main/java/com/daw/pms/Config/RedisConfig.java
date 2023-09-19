package com.daw.pms.Config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Redis configuration.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/19/23
 */
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {
  @Value("${spring.redis.host}")
  private String redisHost;

  @Value("${spring.redis.port}")
  private int redisPort;

  @Value("${spring.redis.password}")
  private String redisPassword;

  /**
   * redisConnectionFactory.
   *
   * @return a {@link org.springframework.data.redis.connection.RedisConnectionFactory} object.
   */
  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
    config.setPassword(RedisPassword.of(redisPassword));
    return new LettuceConnectionFactory(config);
  }

  /**
   * redisTemplate.
   *
   * @param redisConnectionFactory a {@link
   *     org.springframework.data.redis.connection.RedisConnectionFactory} object.
   * @return a {@link org.springframework.data.redis.core.RedisTemplate} object.
   */
  @Bean
  public RedisTemplate<String, Object> redisTemplate(
      RedisConnectionFactory redisConnectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory);
    return template;
  }

  /**
   * defaultCacheConfiguration.
   *
   * @return a {@link org.springframework.data.redis.cache.RedisCacheConfiguration} object.
   */
  @Bean
  public RedisCacheConfiguration defaultCacheConfiguration() {
    return RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(25));
  }

  /**
   * bilibiliWbiKeyCacheConfiguration.
   *
   * @return a {@link org.springframework.data.redis.cache.RedisCacheConfiguration} object.
   */
  @Bean
  public RedisCacheConfiguration bilibiliWbiKeyCacheConfiguration() {
    return RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofDays(1));
  }

  /**
   * bilibiliResourceDashLinksCacheConfiguration.
   *
   * @return a {@link org.springframework.data.redis.cache.RedisCacheConfiguration} object.
   */
  @Bean
  public RedisCacheConfiguration bilibiliResourceDashLinksCacheConfiguration() {
    return RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(2));
  }

  /** {@inheritDoc} */
  @Bean
  @Override
  public CacheManager cacheManager() {
    Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
    cacheConfigurations.put("library-cache", defaultCacheConfiguration());
    cacheConfigurations.put("mv-cache", defaultCacheConfiguration());
    cacheConfigurations.put("song-cache", defaultCacheConfiguration());
    cacheConfigurations.put("user-cache", defaultCacheConfiguration());
    cacheConfigurations.put("bilibili-wbi-key", bilibiliWbiKeyCacheConfiguration());
    cacheConfigurations.put(
        "bilibili-resource-dashLinks", bilibiliResourceDashLinksCacheConfiguration());
    return RedisCacheManager.builder(redisConnectionFactory())
        .withInitialCacheConfigurations(cacheConfigurations)
        .build();
  }
}
