package com.daw.pms.Mapper;

import com.daw.pms.DTO.BasicPMSUserInfoDTO;
import com.daw.pms.DTO.UserDTO;
import com.daw.pms.Provider.UserSqlProvider;
import org.apache.ibatis.annotations.*;

/**
 * UserMapper.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/18/23
 */
@Mapper
public interface UserMapper {
  /**
   * Get user by id in pms.
   *
   * @param id User's id in pms.
   * @return UserDTO presenting user in pms.
   */
  @Select("select * from tb_pms_user where id = #{id}")
  @Results({
    @Result(property = "loginType", column = "login_type"),
    @Result(property = "bgPic", column = "bg_pic"),
    @Result(property = "qqmusicId", column = "qqmusic_id"),
    @Result(property = "qqmusicCookie", column = "qqmusic_cookie"),
    @Result(property = "ncmId", column = "ncm_id"),
    @Result(property = "ncmCookie", column = "ncm_cookie"),
    @Result(property = "bilibiliId", column = "bilibili_id"),
    @Result(property = "biliCookie", column = "bili_cookie")
  })
  UserDTO getUser(Long id);

  /**
   * Get user by name and login type in pms.
   *
   * @param name The name of user in pms.
   * @param loginType The login type.
   * @return UserDTO presenting user in pms.
   */
  @Select("select * from tb_pms_user where name = #{name} and login_type = #{loginType}")
  @Results({
    @Result(property = "loginType", column = "login_type"),
    @Result(property = "bgPic", column = "bg_pic"),
    @Result(property = "qqmusicId", column = "qqmusic_id"),
    @Result(property = "qqmusicCookie", column = "qqmusic_cookie"),
    @Result(property = "ncmId", column = "ncm_id"),
    @Result(property = "ncmCookie", column = "ncm_cookie"),
    @Result(property = "bilibiliId", column = "bilibili_id"),
    @Result(property = "biliCookie", column = "bili_cookie")
  })
  UserDTO getUserByName(String name, Integer loginType);

  /**
   * Get user by email and login type in pms.
   *
   * @param email The email of user in pms.
   * @param loginType The login type.
   * @return UserDTO presenting user in pms.
   */
  @Select("select * from tb_pms_user where email = #{email} and login_type = #{loginType}")
  @Results({
    @Result(property = "loginType", column = "login_type"),
    @Result(property = "bgPic", column = "bg_pic"),
    @Result(property = "qqmusicId", column = "qqmusic_id"),
    @Result(property = "qqmusicCookie", column = "qqmusic_cookie"),
    @Result(property = "ncmId", column = "ncm_id"),
    @Result(property = "ncmCookie", column = "ncm_cookie"),
    @Result(property = "bilibiliId", column = "bilibili_id"),
    @Result(property = "biliCookie", column = "bili_cookie")
  })
  UserDTO getUserByEmail(String email, Integer loginType);

  /**
   * Check if user's name exist in pms.
   *
   * @param name Username to be checked.
   * @param loginType The login type.
   * @return 1 for exist, 0 for not exist.
   */
  @Select("select COUNT(*) from tb_pms_user where name = #{name} and login_type = #{loginType}")
  int checkIfUserNameExist(String name, Integer loginType);

  /**
   * Check if the email exists in pms.
   *
   * @param email Email to be checked.
   * @param loginType The login type.
   * @return 1 for exist, 0 for not exist.
   */
  @Select("select COUNT(*) from tb_pms_user where email = #{email} and login_type = #{loginType}")
  int checkIfEmailAddressExist(String email, Integer loginType);

  /**
   * Check if the phone number exists in pms.
   *
   * @param phoneNumber The phone number to be checked.
   * @param loginType The login type.
   * @return 1 for exist, 0 for not exist.
   */
  @Select(
      "select COUNT(*) from tb_pms_user where phone = #{phoneNumber} and login_type = #{loginType}")
  int checkIfPhoneNumberExist(String phoneNumber, Integer loginType);

  /**
   * Check if there are users with the same email and login type.
   *
   * @param email Email to be checked.
   * @param loginType The login type.
   * @return 1 for exist, 0 for not exist.
   */
  @Select("select COUNT(*) from tb_pms_user where email = #{email} and login_type = #{loginType}")
  int identifyUserByEmail(String email, Integer loginType);

  /**
   * Get user's id in pms by email and login type.
   *
   * @param email User's email.
   * @param loginType The login type.
   * @return User's id in pms.
   */
  @Select("select id from tb_pms_user where email = #{email} and login_type = #{loginType}")
  Long getUserIdByEmail(String email, Integer loginType);

  /**
   * Get user's id in pms by name and login type.
   *
   * @param name User's name.
   * @param loginType The login type.
   * @return User's id in pms.
   */
  @Select("select id from tb_pms_user where name = #{name} and login_type = #{loginType}")
  Long getUserIdByName(String name, Integer loginType);

  /**
   * Get user's basic information in pms.
   *
   * @param id User's id in pms.
   * @return BasicPMSUserInfoDTO presenting user in pms.
   */
  @Select(
      "select id, name, role, email, phone, avatar, login_type, qqmusic_id, qqmusic_cookie, ncm_id, ncm_cookie, bilibili_id, bili_cookie from tb_pms_user where id = #{id}")
  @Results({
    @Result(property = "loginType", column = "login_type"),
    @Result(property = "qqMusicId", column = "qqmusic_id"),
    @Result(property = "qqMusicCookie", column = "qqmusic_cookie"),
    @Result(property = "ncmId", column = "ncm_id"),
    @Result(property = "ncmCookie", column = "ncm_cookie"),
    @Result(property = "biliId", column = "bilibili_id"),
    @Result(property = "biliCookie", column = "bili_cookie"),
  })
  BasicPMSUserInfoDTO getBasicPMSUserInfo(Long id);

  /**
   * Add user to pms.
   *
   * @param userDTO UserDTO presenting user to be added.
   * @return The id of new added user in pms.
   */
  @InsertProvider(type = UserSqlProvider.class, method = "addUser")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int addUser(UserDTO userDTO);

  /**
   * Update credential in qqmusic platform in pms.
   *
   * @param pmsUserId User's id in pms.
   * @param qqmusicId User's id in qqmusic platform.
   * @param qqmusicCookie User's cookie in qqmusic platform.
   * @return 1 for success, 0 for fail.
   */
  @Update(
      "update tb_pms_user set qqmusic_id = #{qqmusicId}, qqmusic_cookie = #{qqmusicCookie} where id = #{pmsUserId}")
  int updateQQMusicCredential(Long pmsUserId, String qqmusicId, String qqmusicCookie);

  /**
   * Update credential in ncm platform in pms.
   *
   * @param pmsUserId User's id in pms.
   * @param ncmId User's id in ncm platform.
   * @param ncmCookie User's cookie in ncm platform.
   * @return 1 for success, 0 for fail.
   */
  @Update(
      "update tb_pms_user set ncm_id = #{ncmId}, ncm_cookie = #{ncmCookie} where id = #{pmsUserId}")
  int updateNCMCredential(Long pmsUserId, String ncmId, String ncmCookie);

  /**
   * Update credential in bilibili platform in pms.
   *
   * @param pmsUserId User's id in pms.
   * @param biliId User's id in bilibili platform.
   * @param biliCookie User's cookie in bilibili platform.
   * @return 1 for success, 0 for fail.
   */
  @Update(
      "update tb_pms_user set bilibili_id = #{biliId}, bili_cookie = #{biliCookie} where id = #{pmsUserId}")
  int updateBiliCredential(Long pmsUserId, String biliId, String biliCookie);

  /**
   * Update user's password in pms.
   *
   * @param pmsUserId User's id in pms.
   * @param pass User's new password.
   * @return 1 for success, 0 for fail.
   */
  @Update("update tb_pms_user set pass = #{pass} where id = #{pmsUserId}")
  int updateUserPass(Long pmsUserId, String pass);

  /**
   * Update user's email in pms.
   *
   * @param pmsUserId User's id in pms.
   * @param email User's new email.
   * @return 1 for success, 0 for fail.
   */
  @Update("update tb_pms_user set email = #{email} where id = #{pmsUserId}")
  int updateUserEmail(Long pmsUserId, String email);

  /**
   * Update user's basic information in pms.
   *
   * @param pmsUserId User's id in pms.
   * @param name User's new name.
   * @param email User's new email.
   * @param avatar User's new avatar.
   * @return 1 for success, 0 for fail.
   */
  @Update(
      "update tb_pms_user set name = #{name}, email = #{email}, avatar = #{avatar} where id = #{pmsUserId}")
  int updateBasicPMSUserInfo(Long pmsUserId, String name, String email, String avatar);
}
