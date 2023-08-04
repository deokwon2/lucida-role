package com.nkia.lucida.role.dto;

import lombok.Data;
import java.util.*;

/**
 * @author henoh@nkia.co.kr on 2022-08-01
 * @desc
 */
@Data
public class SimpleRoleDtos {

  List<SimpleRoleDto> roles;
  private Map<String, List<PermissionDto>> permissions;


  public SimpleRoleDtos(List<SimpleRoleDto> roles, Map<String, List<PermissionDto>> permissions) {
    this.roles = roles;
    this.permissions = permissions;
  }

  public static SimpleRoleDtos toDto(List<SimpleRoleDto> roles,
      Map<String, List<PermissionDto>> permissions) {
    return new SimpleRoleDtos(roles, permissions);
  }


}
