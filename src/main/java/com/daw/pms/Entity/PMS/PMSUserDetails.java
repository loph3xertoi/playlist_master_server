package com.daw.pms.Entity.PMS;

import com.daw.pms.DTO.UserDTO;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Data;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * User entity, used for login with username and password in spring security.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/18/23
 */
@Data
@ToString(callSuper = true)
public class PMSUserDetails implements UserDetails {
  private final UserDTO user;

  /**
   * Constructor for PMSUserDetails.
   *
   * @param user a {@link com.daw.pms.DTO.UserDTO} object.
   */
  public PMSUserDetails(UserDTO user) {
    this.user = user;
  }

  /** {@inheritDoc} */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    List<GrantedAuthority> list = new ArrayList<>();
    list.add(new SimpleGrantedAuthority(user.getRole()));
    return list;
  }

  /** {@inheritDoc} */
  @Override
  public String getPassword() {
    return user.getPass();
  }

  /** {@inheritDoc} */
  @Override
  public String getUsername() {
    return user.getEmail();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isEnabled() {
    return user.getEnabled();
  }
}
