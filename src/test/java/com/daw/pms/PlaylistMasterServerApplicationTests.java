package com.daw.pms;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class PlaylistMasterServerApplicationTests {

  @Autowired StringRedisTemplate stringRedisTemplate;

  @Test
  void contextLoads() throws InterruptedException {
    System.out.println(stringRedisTemplate.opsForValue().get("name"));
  }
}
