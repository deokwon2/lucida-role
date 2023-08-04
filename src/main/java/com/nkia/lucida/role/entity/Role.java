package com.nkia.lucida.role.entity;

import com.nkia.lucida.common.mongodb.MongoDBIsolationCollection;
import com.nkia.lucida.role.domain.RoleTypes;
import com.nkia.lucida.role.dto.RoleDto;
import com.nkia.lucida.role.util.ImportService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "role")
@Getter
@Setter
@NoArgsConstructor
@MongoDBIsolationCollection
public class Role {

  @Id
  String id;

  String name;

  Set<String> userIds = new HashSet<>();

  String roleType;

  @Setter
  boolean isDefault;

  @Setter
  private Long cTime = System.currentTimeMillis();

  @Setter
  private Long mTime = System.currentTimeMillis();

  @DBRef(db = "shared", lazy = true)
  List<Permission> permissions = new ArrayList<>();

  public void addUserId(String userId) {
    this.userIds.add(userId);
  }

  @Transient
  public RoleTypes getRoleType() {
    return RoleTypes.fromValue(roleType);
  }

  public void setRoleType(RoleTypes roleType) {
    if (roleType != null) {
      this.roleType = roleType.name();
    }
  }

  public List<Permission> getPermissions() {
    if (RoleTypes.ADMIN.name().equals(this.roleType)) {
      return ImportService.getPermissionSavedIds();
    } else if (RoleTypes.STANDARD.name().equals(this.roleType)) {
      return new ArrayList<>();
    }
    return this.permissions;
  }

  public Role(RoleDto roleDto, List<Permission> permissions) {
    this.name = roleDto.getName();
    this.permissions = permissions;
    this.userIds = roleDto.getUserIds();
    this.isDefault = roleDto.isDefault();
    setRoleType(roleDto.getRoleType());
  }

  public RoleDto toDto() {
    RoleDto dto = new RoleDto();
    BeanUtils.copyProperties(this, dto);
    dto.setPermissions(
        getPermissions().stream().map(Permission::toDto).toList());
    dto.setRoleId(this.getId());
    return dto;
  }

}
