package com.daw.pms.Provider;

import com.daw.pms.DTO.UserDTO;
import org.apache.ibatis.jdbc.SQL;

public class UserSqlProvider {
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
