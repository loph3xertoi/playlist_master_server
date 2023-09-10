package com.daw.pms.Service.PMS.impl;

import com.daw.pms.DTO.UserDTO;
import com.daw.pms.Entity.PMS.PMSUserDetails;
import com.daw.pms.Mapper.UserMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {
  private final UserMapper userMapper;

  public UserDetailsServiceImpl(UserMapper userMapper) {
    this.userMapper = userMapper;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    UserDTO user = userMapper.getUserByEmail(email);
    if (user == null) {
      throw new UsernameNotFoundException("Could not find user");
    }
    return new PMSUserDetails(user);
  }
}
