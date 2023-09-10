package com.daw.pms.Mapper;

import com.daw.pms.DTO.UserDTO;
import com.daw.pms.Provider.UserSqlProvider;
import org.apache.ibatis.annotations.*;

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

  @Select("select * from tb_pms_user where name = #{name}")
  @Results({
    @Result(property = "bgPic", column = "bg_pic"),
    @Result(property = "qqmusicId", column = "qqmusic_id"),
    @Result(property = "qqmusicCookie", column = "qqmusic_cookie"),
    @Result(property = "ncmId", column = "ncm_id"),
    @Result(property = "ncmCookie", column = "ncm_cookie"),
    @Result(property = "bilibiliId", column = "bilibili_id"),
    @Result(property = "biliCookie", column = "bili_cookie")
  })
  UserDTO getUserByName(String name);

  @Select("select * from tb_pms_user where email = #{email}")
  @Results({
    @Result(property = "bgPic", column = "bg_pic"),
    @Result(property = "qqmusicId", column = "qqmusic_id"),
    @Result(property = "qqmusicCookie", column = "qqmusic_cookie"),
    @Result(property = "ncmId", column = "ncm_id"),
    @Result(property = "ncmCookie", column = "ncm_cookie"),
    @Result(property = "bilibiliId", column = "bilibili_id"),
    @Result(property = "biliCookie", column = "bili_cookie")
  })
  UserDTO getUserByEmail(String email);

  @Select("select COUNT(*) from tb_pms_user where name = #{name}")
  int checkIfUserNameExist(String name);

  @Select("select COUNT(*) from tb_pms_user where email = #{email}")
  int checkIfEmailAddressExist(String email);

  @Select("select COUNT(*) from tb_pms_user where phone = #{phoneNumber}")
  int checkIfPhoneNumberExist(String phoneNumber);

  @Select("select COUNT(*) from tb_pms_user where email = #{email}")
  int identifyUserByEmail(String email);

  @Select("select id from tb_pms_user where email = #{email}")
  Long getUserIdByEmail(String email);

  @InsertProvider(type = UserSqlProvider.class, method = "addUser")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int addUser(UserDTO userDTO);

  @Update(
      "update tb_pms_user set qqmusic_id = #{qqmusicId}, qqmusic_cookie = #{qqmusicCookie} where id = #{pmsUserId}")
  int updateQQMusicCredential(Long pmsUserId, String qqmusicId, String qqmusicCookie);

  @Update(
      "update tb_pms_user set ncm_id = #{ncmId}, ncm_cookie = #{ncmCookie} where id = #{pmsUserId}")
  int updateNCMCredential(Long pmsUserId, String ncmId, String ncmCookie);

  @Update(
      "update tb_pms_user set bilibili_id = #{biliId}, bili_cookie = #{biliCookie} where id = #{pmsUserId}")
  int updateBiliCredential(Long pmsUserId, String biliId, String biliCookie);

  @Update("update tb_pms_user set pass = #{pass} where id = #{pmsUserId}")
  int updateUserPass(Long pmsUserId, String pass);
}
