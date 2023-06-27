package com.daw.pms.Config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.daw.pms.Mapper")
public class MybatisConfig {}
