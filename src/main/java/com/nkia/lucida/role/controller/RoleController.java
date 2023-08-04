package com.nkia.lucida.role.controller;

import com.nkia.lucida.role.dto.PermissionDto;
import com.nkia.lucida.role.dto.RoleDto;
import com.nkia.lucida.role.dto.SimpleRoleDtos;
import com.nkia.lucida.role.dto.UpdateUserDto;
import com.nkia.lucida.role.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api")
@Tag(name = "Role", description = "Common Role Service")
public class RoleController {

  @Autowired
  RoleService roleService;

  @Operation(security = {@SecurityRequirement(name = "bearerAuth")})
  @PostMapping(path = "/role", consumes = MediaType.APPLICATION_JSON_VALUE)
  public void saveRole(@RequestBody RoleDto roleDto) {
    roleService.saveRole(roleDto);
  }

  @Operation(security = {@SecurityRequirement(name = "bearerAuth")})
  @GetMapping(path = "/role")
  public List<RoleDto> getAllRole() {
    return roleService.getRole();
  }

  @Operation(security = {@SecurityRequirement(name = "bearerAuth")})
  @GetMapping(path = "/role/{roleId}")
  public RoleDto getRoleById(@PathVariable String roleId) {
    return roleService.getRole(roleId);
  }

  @Operation(security = {@SecurityRequirement(name = "bearerAuth")})
  @GetMapping(path = "/role/{roleId}/permissions")
  public Map<String, List<PermissionDto>> getPermissionByRoleId(@PathVariable String roleId) {
    return roleService.getPermissionByRoleId(roleId);
  }

  @Operation(security = {@SecurityRequirement(name = "bearerAuth")})
  @DeleteMapping(path = "/role/{roleId}")
  public void deleteRole(@PathVariable String roleId) {
    roleService.deleteRole(roleId);
    log.info("delete role");
  }

  @Operation(security = {@SecurityRequirement(name = "bearerAuth")})
  @DeleteMapping(path = "/role")
  public void deleteRoles(@RequestParam String[] roleIds) {
    roleService.deleteRoles(roleIds);
  }

  @Operation(security = {@SecurityRequirement(name = "bearerAuth")})
  @GetMapping(path = "/role/user/{userId}")
  public SimpleRoleDtos findRoleByUser(@PathVariable String userId) {
    return roleService.findRoleByUserId(userId);
  }

  @Operation(security = {@SecurityRequirement(name = "bearerAuth")})
  @PutMapping(path = "/role/{roleId}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public void updateRole(@PathVariable String roleId,
      @RequestBody RoleDto roleDto) {
    roleService.updateRole(roleDto);
  }

  @Operation(security = {@SecurityRequirement(name = "bearerAuth")})
  @GetMapping(path = "/permissions")
  public Map<String, List<PermissionDto>> getAllPermissions() {
    return roleService.getAllPermissions();
  }

  @Operation(security = {@SecurityRequirement(name = "bearerAuth")})
  @GetMapping(path = "/role/users")
  public Map<String, List<RoleDto>> getRolesByUsers() {
    return roleService.getRolesByUsers();
  }

  @Operation(security = {@SecurityRequirement(name = "bearerAuth")})
  @PutMapping(path = "/role/user/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public void updateUsers(@PathVariable String userId,
      @RequestBody UpdateUserDto roleIdDto) {
    roleService.updateUsers(userId, roleIdDto.getRemoveRoleIds(), roleIdDto.getAddRoleIds());
  }
}
