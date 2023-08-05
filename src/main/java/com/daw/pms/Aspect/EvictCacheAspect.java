package com.daw.pms.Aspect;

import com.daw.pms.DTO.Result;
import java.util.Map;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class EvictCacheAspect {
  private final CacheManager cacheManager;
  private final RedisConnectionFactory redisConnectionFactory;

  public EvictCacheAspect(
      CacheManager cacheManager, RedisConnectionFactory redisConnectionFactory) {
    this.cacheManager = cacheManager;
    this.redisConnectionFactory = redisConnectionFactory;
  }

  @Pointcut(
      "execution(* com.daw.pms.Controller.LibraryController.*(..))"
          + "&& !execution(* com.daw.pms.Controller.LibraryController.get*(..))")
  public void controllerUpdateMethods() {}

  @Pointcut(
      "execution(* com.daw.pms.Controller.LibraryController.addSongsToLibrary(..))"
          + "|| execution(* com.daw.pms.Controller.LibraryController.moveSongsToOtherLibrary(..))"
          + "|| execution(* com.daw.pms.Controller.LibraryController.removeSongsFromLibrary(..))")
  public void controllerUpdateLibrarySongsMethods() {}

  @AfterReturning(pointcut = "controllerUpdateMethods()", returning = "resultObj")
  public void evictLibrariesCacheOnUpdated(JoinPoint joinPoint, Object resultObj) {
    RedisCache redisCache = (RedisCache) cacheManager.getCache("library-cache");
    assert redisCache != null;
    Result result = (Result) resultObj;
    if (result.getSuccess()) {
      Object[] args = joinPoint.getArgs();
      Integer platform = (Integer) args[args.length - 1];
      //      String keyStr = "getLibraries(0,1,20,web,0," + platform + ")";
      RedisConnection redisConnection = redisConnectionFactory.getConnection();
      try (Cursor<byte[]> cursor =
          redisConnection.scan(
              ScanOptions.scanOptions()
                  .match("library-cache::getLibraries(0,*" + platform + ")")
                  .count(1000)
                  .build())) {
        while (cursor.hasNext()) {
          byte[] fullKeyByte = cursor.next();
          String fullKey = new String(fullKeyByte);
          String key = fullKey.substring("library-cache::".length());
          redisCache.evictIfPresent(key);
        }
      }
    }
  }

  @AfterReturning(pointcut = "controllerUpdateLibrarySongsMethods()", returning = "resultObj")
  public void evictDetailLibraryCacheOnUpdated(JoinPoint joinPoint, Object resultObj) {
    RedisCache redisCache = (RedisCache) cacheManager.getCache("library-cache");
    assert redisCache != null;
    String cacheName = "library-cache";
    Result result = (Result) resultObj;
    if (result.getSuccess()) {
      Object[] args = joinPoint.getArgs();
      Signature signature = joinPoint.getSignature();
      String methodName = signature.getName();
      Integer platform = (Integer) args[args.length - 1];
      //      String keyStr = "library-cache::getDetailLibrary(172408678,1,20,,,null,0,3)";
      RedisConnection redisConnection = redisConnectionFactory.getConnection();

      String firstParam;
      String secondParam = null;
      if ("addSongsToLibrary".equals(methodName)) {
        firstParam = ((Map<String, String>) args[0]).get("tid");
      } else if ("moveSongsToOtherLibrary".equals(methodName)) {
        firstParam = ((Map<String, String>) args[0]).get("fromTid");
        secondParam = ((Map<String, String>) args[0]).get("toTid");
      } else {
        firstParam = (String) args[2];
      }
      String pattern = "library-cache::getDetailLibrary(" + firstParam + ",*" + platform + ")";
      try (Cursor<byte[]> cursor =
          redisConnection.scan(ScanOptions.scanOptions().match(pattern).count(1000).build())) {
        while (cursor.hasNext()) {
          byte[] fullKeyByte = cursor.next();
          String fullKey = new String(fullKeyByte);
          String key = fullKey.substring(cacheName.length() + 2);
          redisCache.evictIfPresent(key);
        }
      }
      if (secondParam == null) {
        String pattern2 = "library-cache::getDetailLibrary(" + secondParam + ",*" + platform + ")";
        try (Cursor<byte[]> cursor =
            redisConnection.scan(ScanOptions.scanOptions().match(pattern2).count(1000).build())) {
          while (cursor.hasNext()) {
            byte[] fullKeyByte = cursor.next();
            String fullKey = new String(fullKeyByte);
            String key = fullKey.substring(cacheName.length() + 2);
            redisCache.evictIfPresent(key);
          }
        }
      }
    }
  }
}