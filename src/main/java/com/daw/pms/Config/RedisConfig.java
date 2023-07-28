package com.daw.pms.Config;

import java.time.Duration;
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

@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {
  @Value("${spring.redis.host}")
  private String redisHost;

  @Value("${spring.redis.port}")
  private int redisPort;

  @Value("${spring.redis.password}")
  private String redisPassword;

  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
    config.setPassword(RedisPassword.of(redisPassword));
    return new LettuceConnectionFactory(config);
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate(
      RedisConnectionFactory redisConnectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory);
    return template;
  }

  @Bean
  @Override
  public CacheManager cacheManager() {
    RedisCacheManager cacheManager =
        RedisCacheManager.builder(redisConnectionFactory())
            .cacheDefaults(
                RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(25)))
            .build();
    return cacheManager;
  }

  //  @Bean
  //  public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
  //    RedisSerializer<Object> serializer = new JsonRedisSerializer<>(Object.class);
  //    RedisCacheConfiguration cacheConfig =
  //        RedisCacheConfiguration.defaultCacheConfig()
  //            .entryTtl(Duration.ofDays(1))
  //            .serializeKeysWith(
  //                RedisSerializationContext.SerializationPair.fromSerializer(
  //                    new StringRedisSerializer()))
  //            .serializeValuesWith(
  //                RedisSerializationContext.SerializationPair.fromSerializer(serializer));
  //    return RedisCacheManager.builder(redisConnectionFactory).cacheDefaults(cacheConfig).build();
  //  }
}
