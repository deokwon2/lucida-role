package com.nkia.lucida.role.service;

import com.nkia.lucida.role.dao.PermissionRepository;
import com.nkia.lucida.role.dao.RoleRepository;
import com.nkia.lucida.role.domain.RoleTypes;
import com.nkia.lucida.role.dto.PermissionDto;
import com.nkia.lucida.role.dto.RoleDto;
import com.nkia.lucida.role.dto.SimpleRoleDto;
import com.nkia.lucida.role.dto.SimpleRoleDtos;
import com.nkia.lucida.role.entity.Permission;
import com.nkia.lucida.role.entity.Role;
import com.nkia.lucida.role.util.ImportService;
import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class RoleServiceImpl implements RoleService {

  final RoleRepository roleRepository;

  final PermissionRepository permissionRepository;

  @Autowired
  ImportService importService;

  @PostConstruct
  private void init() {
    savePermission();
  }

  private void savePermission() {
    List<String> lines = new ArrayList<>();
    try (InputStream in = getClass().getClassLoader().getResourceAsStream("permission.json.log");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
      for (; ; ) {
        String line = reader.readLine();
        if (line == null) {
          break;
        }
        lines.add(line);
      }
    } catch (IOException | NullPointerException e) {
      log.warn("File Not Found");
      throw new NoSuchElementException(e.getMessage());
    }

    if (!lines.isEmpty()) {
      importService.importTo(lines);
    }
  }

  public RoleServiceImpl(RoleRepository roleRepository, PermissionRepository permissionRepository) {
    this.roleRepository = roleRepository;
    this.permissionRepository = permissionRepository;
  }

  @Override
  @Transactional
  public void saveRole(RoleDto roleDto) {
    if (roleDto == null) {
      return;
    }
    Role role = new Role(roleDto, permissionRepository.findAllByIdIn(
        roleDto.getPermissions().stream().map(PermissionDto::getId).toList()));
    role.setRoleType(RoleTypes.CUSTOM);
    roleRepository.save(role);
  }

  @Override
  @Transactional
  public RoleDto getRole(String roleId) {
    Optional<Role> role = roleRepository.findById(roleId);
    Role orElseGet = role.orElseGet(Role::new);
    List<Permission> permissions = orElseGet.getPermissions();
    RoleDto info = new RoleDto(orElseGet,
        permissions.stream().map(Permission::toDto).toList());
    for (String id : orElseGet.getUserIds()) {
      info.getUserIds().add(id);
    }
    return info;
  }

  @Override
  @Transactional
  public List<RoleDto> getRole() {
    List<RoleDto> list = new ArrayList<>();
    List<Role> roles = roleRepository.findAll();
    if (roles.isEmpty()) {
      return list;
    }
    toRoleDto(list, roles);
    return list;
  }

  @Override
  @Transactional
  public void updateRole(RoleDto roleDto) {
    Role get = roleRepository.findById(roleDto.getRoleId()).orElseGet(Role::new);
    if (get.getId() != null) {
      setRole(roleDto, get);
    }
  }

  @Override
  @Transactional
  public void deleteRole(String roleId) {
    roleRepository.deleteById(roleId);
  }

  @Override
  @Transactional
  public void deleteRoles(String[] roleIds) {
    Iterable<Role> byIds = roleRepository.findAllById(Arrays.asList(roleIds));
    roleRepository.deleteAll(byIds);
  }

  @Override
  @Transactional
  public SimpleRoleDtos findRoleByUserId(String userId) {
    List<Role> roles = roleRepository.findAllByUserIdsIsContaining(userId);
    if (roles.isEmpty()) {
      return null;
    }
    List<SimpleRoleDto> simpleRoleDto = new ArrayList<>();
    Set<String> activePermissionIds = new HashSet<>();
    for (Role role : roles) {
      simpleRoleDto.add(SimpleRoleDto.toDto(role));
      activePermissionIds.addAll(
          role.getPermissions().stream().map(Permission::getId).toList());
    }
    return SimpleRoleDtos.toDto(simpleRoleDto, convertPermissionWithType(activePermissionIds));
  }

  private void toRoleDto(Collection<RoleDto> list, List<Role> roles) {
    for (Role role : roles) {
      List<Permission> permissionList = role.getPermissions();
      List<PermissionDto> permissions = new ArrayList<>();
      if (!permissionList.isEmpty()) {
        permissions = permissionList.stream().map(Permission::toDto).toList();
      }
      RoleDto info = new RoleDto(role, permissions);
      for (String id : role.getUserIds()) {
        info.getUserIds().add(id);
      }
      list.add(info);
    }
  }

  @Override
  @Transactional
  public void updateUsers(String userId, Set<String> removeRoleIds, Set<String> addRoleIds) {
    Iterable<Role> removeRoles = roleRepository.findAllById(removeRoleIds);
    Iterable<Role> addRoles = roleRepository.findAllById(addRoleIds);
    for (Role role : removeRoles) {
      role.getUserIds().remove(userId);
    }
    for (Role role : addRoles) {
      role.addUserId(userId);
    }
    roleRepository.saveAll(removeRoles);
    roleRepository.saveAll(addRoles);
  }

  @Override
  public Map<String, List<PermissionDto>> getAllPermissions() {
    Map<String, List<PermissionDto>> permissionWithType = new HashMap<>();
    getPermission().values().forEach(
        p -> permissionWithType.computeIfAbsent(p.getType().name(), k -> new ArrayList<>()).add(p));
    return permissionWithType;
  }

  private Map<String, PermissionDto> getPermission() {
    return ImportService.getPermissionSavedIds().stream().map(Permission::toDto)
        .collect(Collectors.toMap(PermissionDto::getId, Function.identity()));
  }

  private void setRole(RoleDto newRole, Role role) {
    List<Permission> permissions = new ArrayList<>();
    if (!newRole.getPermissions().isEmpty()) {
      permissions = permissionRepository.findAllByIdIn(
          newRole.getPermissions().stream().map(PermissionDto::getId).toList());
    }
    role.setUserIds(newRole.getUserIds());
    role.setName(newRole.getName());
    role.setPermissions(permissions);
    role.setMTime(System.currentTimeMillis());
    role.setDefault(newRole.isDefault());
    roleRepository.save(role);
  }

  @Override
  public Map<String, List<PermissionDto>> getPermissionByRoleId(String roleId) {
    Role role = roleRepository.findById(roleId)
        .orElseThrow(() -> new IllegalStateException("Role is not existed. roleId is = " + roleId));
    Set<String> activePermissionIds = role.getPermissions().stream().map(Permission::getId)
        .collect(Collectors.toSet());
    return convertPermissionWithType(activePermissionIds);
  }

  private Map<String, List<PermissionDto>> convertPermissionWithType(
      Set<String> activePermissionIds) {
    Map<String, PermissionDto> permissionDtos = getPermission();
    Set<String> permissionIds = permissionDtos.values().stream().map(PermissionDto::getId)
        .collect(Collectors.toSet());
    permissionIds.removeAll(activePermissionIds);
    permissionIds.forEach(i -> permissionDtos.get(i).setActive(false));
    Map<String, List<PermissionDto>> permissionWithType = new HashMap<>();
    permissionDtos.values().forEach(
        p -> permissionWithType.computeIfAbsent(p.getType().name(), k -> new ArrayList<>()).add(p));
    return permissionWithType;
  }


  @Override
  public Map<String, List<RoleDto>> getRolesByUsers() {
    List<Role> roles = roleRepository.findAll();
    Map<String, List<RoleDto>> list = new HashMap<>();
    roles.forEach(r -> r.getUserIds()
        .forEach(i -> list.computeIfAbsent(i, k -> new ArrayList<>()).add(r.toDto())));
    return list;
  }

  @Override
  public Permission savePermission(Permission permission) {
    Permission createPermission = new Permission();
    createPermission.setType(permission.getType());
    createPermission.setDisplayName(permission.getDisplayName());
    createPermission.setAction(permission.getAction());
    createPermission.setRestricted(permission.isRestricted());
    createPermission.setName(permission.getName());
    permission.setDescription(permission.getDescription());
    createPermission = permissionRepository.save(createPermission);
    ImportService.getPermissionSavedIds().add(createPermission);
    return createPermission;
  }

  @Override
  public void deletePermission(String permissionId) {
    Permission permission = permissionRepository.findById(permissionId)
        .orElseThrow(() -> new NoSuchElementException("No Permission Found"));
    permissionRepository.deleteById(permissionId);
    ImportService.getPermissionSavedIds().remove(permission);
  }
}
