package com.nkia.lucida.role.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nkia.lucida.role.domain.RoleTypes;
import com.nkia.lucida.role.entity.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@JsonIgnoreProperties
public class RoleDto {

  private String roleId;

  private String name;

  private List<PermissionDto> permissions = new ArrayList<>();

  private Set<String> userIds = new HashSet<>();

  private RoleTypes roleType;

  private boolean isDefault;

  private long cTime;

  private long mTime;

  public RoleDto(Role role, List<PermissionDto> permissions) {
    BeanUtils.copyProperties(role, this);
    this.roleId = role.getId();
    this.permissions = permissions;
    this.roleType = role.getRoleType();
    this.isDefault = role.isDefault();
  }

}
