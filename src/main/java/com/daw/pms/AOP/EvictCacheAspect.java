package com.daw.pms.AOP;

import com.daw.pms.DTO.Result;
import com.daw.pms.DTO.UpdateLibraryDTO;
import com.daw.pms.Utils.PmsUserDetailsUtil;
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

/**
 * Evict cache aspect.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/18/23
 */
@Aspect
@Component
public class EvictCacheAspect {
  private final CacheManager cacheManager;
  private final RedisConnectionFactory redisConnectionFactory;
  private final PmsUserDetailsUtil pmsUserDetailsUtil;

  /**
   * Constructor for EvictCacheAspect.
   *
   * @param cacheManager a {@link org.springframework.cache.CacheManager} object.
   * @param redisConnectionFactory a {@link
   *     org.springframework.data.redis.connection.RedisConnectionFactory} object.
   * @param pmsUserDetailsUtil a {@link com.daw.pms.Utils.PmsUserDetailsUtil} object.
   */
  public EvictCacheAspect(
      CacheManager cacheManager,
      RedisConnectionFactory redisConnectionFactory,
      PmsUserDetailsUtil pmsUserDetailsUtil) {
    this.cacheManager = cacheManager;
    this.redisConnectionFactory = redisConnectionFactory;
    this.pmsUserDetailsUtil = pmsUserDetailsUtil;
  }

  /** controllerUpdateMethods. */
  @Pointcut(
      "execution(* com.daw.pms.Controller.LibraryController.*(..))"
          + "&& !execution(* com.daw.pms.Controller.LibraryController.get*(..))")
  public void controllerUpdateMethods() {}

  /** controllerUpdateLibrarySongsMethods. */
  @Pointcut(
      "execution(* com.daw.pms.Controller.LibraryController.addSongsToLibrary(..))"
          + "|| execution(* com.daw.pms.Controller.LibraryController.moveSongsToOtherLibrary(..))"
          + "|| execution(* com.daw.pms.Controller.LibraryController.removeSongsFromLibrary(..))"
          + "|| execution(* com.daw.pms.Controller.LibraryController.updateLibrary(..))")
  public void controllerUpdateLibrarySongsMethods() {}

  /**
   * evictLibrariesCacheOnUpdated.
   *
   * @param joinPoint a {@link org.aspectj.lang.JoinPoint} object.
   * @param resultObj a {@link java.lang.Object} object.
   */
  @AfterReturning(pointcut = "controllerUpdateMethods()", returning = "resultObj")
  public void evictLibrariesCacheOnUpdated(JoinPoint joinPoint, Object resultObj) {
    RedisCache redisCache = (RedisCache) cacheManager.getCache("library-cache");
    assert redisCache != null;
    Result result = (Result) resultObj;
    if (result.getSuccess()) {
      Object[] args = joinPoint.getArgs();
      Signature signature = joinPoint.getSignature();
      String methodName = signature.getName();
      int platform = (int) args[args.length - 1];
      Long pmsUserId = pmsUserDetailsUtil.getCurrentLoginUserId();
      //      String keyStr = "getLibraries(1,1,20,web,0," + platform + ")";
      RedisConnection redisConnection = redisConnectionFactory.getConnection();
      try (Cursor<byte[]> cursor =
          redisConnection.scan(
              ScanOptions.scanOptions()
                  .match("library-cache::getLibraries(" + pmsUserId + ",*" + platform + ")")
                  .count(1000)
                  .build())) {
        while (cursor.hasNext()) {
          byte[] fullKeyByte = cursor.next();
          String fullKey = new String(fullKeyByte);
          String key = fullKey.substring("library-cache::".length());
          redisCache.evictIfPresent(key);
        }
      }
      // Evict cache for pms libraries when updating pms libraries.
      if ("addSongsToLibrary".equals(methodName)) {
        Map<String, Object> requestBody = (Map<String, Object>) args[0];
        Boolean isAddToPMSLibrary = (Boolean) requestBody.get("isAddToPMSLibrary");
        if (isAddToPMSLibrary) {
          try (Cursor<byte[]> cursor =
              redisConnection.scan(
                  ScanOptions.scanOptions()
                      .match("library-cache::getLibraries(" + pmsUserId + ",*0)")
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
    }
  }

  /**
   * evictDetailLibraryCacheOnUpdated.
   *
   * @param joinPoint a {@link org.aspectj.lang.JoinPoint} object.
   * @param resultObj a {@link java.lang.Object} object.
   */
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
      int platform = (int) args[args.length - 1];
      //      String keyStr = "library-cache::getDetailLibrary(172408678,1,20,,,null,0,3)";
      RedisConnection redisConnection = redisConnectionFactory.getConnection();

      String firstParam;
      Boolean isAddToPMSLibrary = false;
      String secondParam = null;
      if ("addSongsToLibrary".equals(methodName)) {
        Map<String, Object> reqeust = (Map<String, Object>) args[0];
        firstParam = String.valueOf(reqeust.get("tid"));
        isAddToPMSLibrary = (Boolean) reqeust.get("isAddToPMSLibrary");
      } else if ("moveSongsToOtherLibrary".equals(methodName)) {
        firstParam = ((Map<String, String>) args[0]).get("fromTid");
        secondParam = ((Map<String, String>) args[0]).get("toTid");
      } else if ("removeSongsFromLibrary".equals(methodName)) {
        firstParam = String.valueOf(args[2]);
      } else if ("updateLibrary".equals(methodName)) {
        firstParam = ((UpdateLibraryDTO) args[0]).getId().toString();
      } else {
        throw new RuntimeException("Invalid method name");
      }
      if (isAddToPMSLibrary) {
        platform = 0;
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
      if (secondParam != null) {
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
