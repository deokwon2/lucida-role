package com.nkia.lucida.role.dto;

import com.nkia.lucida.role.domain.RoleTypes;
import com.nkia.lucida.role.entity.Role;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * @author henoh@nkia.co.kr on 2022-08-01
 * @desc
 */
@Data
public class SimpleRoleDto {

  private String id;
  private String name;
  private RoleTypes roleType;

  private SimpleRoleDto(Role role) {
    BeanUtils.copyProperties(role, this);
  }

  public static SimpleRoleDto toDto(Role role) {
    return new SimpleRoleDto(role);
  }
}
