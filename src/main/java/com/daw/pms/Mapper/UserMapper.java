package com.daw.pms.Mapper;

import com.daw.pms.DTO.UserDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
  @Select("select * from tb_pms_user where id = #{id}")
  @Results({
    @Result(property = "bgPic", column = "bg_pic"),
    @Result(property = "qqmusicId", column = "qqmusic_id"),
    @Result(property = "qqmusicCookie", column = "qqmusic_cookie"),
    @Result(property = "ncmId", column = "ncm_id"),
    @Result(property = "ncmCookie", column = "ncm_cookie"),
    @Result(property = "bilibiliId", column = "bilibili_id"),
    @Result(property = "biliCookie", column = "bili_cookie")
  })
  UserDTO getUser(Long id);
}
