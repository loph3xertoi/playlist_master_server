package com.daw.pms.Provider;

import com.daw.pms.DTO.UserDTO;
import org.apache.ibatis.jdbc.SQL;

public class UserSqlProvider {
  public String addUser(UserDTO userDTO) {
    SQL sql =
        new SQL()
            .INSERT_INTO("tb_pms_user")
            .VALUES("name", "#{name}")
            .VALUES("pass", "#{pass}")
            .VALUES("role", "#{role}")
            .VALUES("enabled", "#{enabled}");
    String email = userDTO.getEmail();
    if (email != null && !email.isEmpty()) {
      sql.VALUES("email", "#{email}");
    }

    String phone = userDTO.getPhone();
    if (phone != null && !phone.isEmpty()) {
      sql.VALUES("phone", "#{phone}");
    }
    return sql.toString();
  }
}
