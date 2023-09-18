package com.daw.pms.Service.PMS.impl;

import com.daw.pms.DTO.UserDTO;
import com.daw.pms.Entity.PMS.PMSUserDetails;
import com.daw.pms.Mapper.UserMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * Implement for UserDetailsService, used for login by name and password in spring security.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/18/23
 */
@Component
public class NamePasswordUserDetailsServiceImpl implements UserDetailsService {
  private final UserMapper userMapper;

  public NamePasswordUserDetailsServiceImpl(UserMapper userMapper) {
    this.userMapper = userMapper;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    UserDTO user = userMapper.getUserByEmail(email, 0);
    if (user == null) {
      throw new UsernameNotFoundException("Could not find user");
    }
    return new PMSUserDetails(user);
  }
}
