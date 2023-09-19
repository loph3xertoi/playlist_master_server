package com.daw.pms.Config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * Mybatis config.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/19/23
 */
@Configuration
@MapperScan("com.daw.pms.Mapper")
public class MybatisConfig {}
