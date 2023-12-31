package com.daw.pms.Provider;

import com.daw.pms.DTO.UserDTO;
import org.apache.ibatis.jdbc.SQL;

/**
 * User sql provider.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/18/23
 */
public class UserSqlProvider {
  /**
   * addUser.
   *
   * @param userDTO a {@link com.daw.pms.DTO.UserDTO} object.
   * @return a {@link java.lang.String} object.
   */
  public String addUser(UserDTO userDTO) {
    SQL sql =
        new SQL()
            .INSERT_INTO("tb_pms_user")
            .VALUES("name", "#{name}")
            .VALUES("role", "#{role}")
            .VALUES("enabled", "#{enabled}")
            .VALUES("login_type", "#{loginType}");
    String pass = userDTO.getPass();
    if (pass != null && !pass.isEmpty()) {
      sql.VALUES("pass", "#{pass}");
    }

    String email = userDTO.getEmail();
    if (email != null && !email.isEmpty()) {
      sql.VALUES("email", "#{email}");
    }

    String phone = userDTO.getPhone();
    if (phone != null && !phone.isEmpty()) {
      sql.VALUES("phone", "#{phone}");
    }

    String avatar = userDTO.getAvatar();
    if (avatar != null && !avatar.isEmpty()) {
      sql.VALUES("avatar", "#{avatar}");
    }

    return sql.toString();
  }
}
