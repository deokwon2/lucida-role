package com.nkia.lucida.role.service;

import com.nkia.lucida.role.dto.PermissionDto;
import com.nkia.lucida.role.dto.RoleDto;
import com.nkia.lucida.role.dto.SimpleRoleDtos;
import com.nkia.lucida.role.entity.Permission;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RoleService {

  void saveRole(RoleDto role);

  RoleDto getRole(String roleId);

  List<RoleDto> getRole();

  void updateRole(RoleDto role);

  void deleteRole(String roleId);

  void deleteRoles(String[] roleIds);

  SimpleRoleDtos findRoleByUserId(String userId);

  void updateUsers(String userId, Set<String> removeRoleIds, Set<String> addRoleIds);

  Map<String, List<PermissionDto>> getAllPermissions();

  Map<String, List<PermissionDto>> getPermissionByRoleId(String roleId);

  Map<String, List<RoleDto>> getRolesByUsers();

  Permission savePermission(Permission permission);

  void deletePermission(String permissionId);
}
