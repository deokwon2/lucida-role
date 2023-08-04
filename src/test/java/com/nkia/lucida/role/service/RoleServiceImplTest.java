package com.nkia.lucida.role.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

import com.nkia.lucida.role.dao.PermissionRepository;
import com.nkia.lucida.role.dao.RoleRepository;
import com.nkia.lucida.role.domain.PermissionTypes;
import com.nkia.lucida.role.domain.RoleTypes;
import com.nkia.lucida.role.domain.SimpleAction;
import com.nkia.lucida.role.dto.PermissionDto;
import com.nkia.lucida.role.dto.RoleDto;
import com.nkia.lucida.role.entity.Permission;
import com.nkia.lucida.role.entity.Role;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

  @Mock
  RoleRepository roleRepository;
  @Mock
  PermissionRepository permissionRepository;
  @InjectMocks
  RoleServiceImpl roleService;

  @Test
  @DisplayName("2개 이상의 유저 아이디를 가진 역할 가지고 오기 테스트")
  void get_role() {
    //given
    Set<String> userIds = new HashSet<>();
    userIds.add("User1");
    userIds.add("User2");
    Role role = new Role();
    role.setName("name");
    role.setUserIds(userIds);
    role.setPermissions(new ArrayList<>());
    String fakeRoleId = "62b2c5c30536b02fe0e1aa30";
    ReflectionTestUtils.setField(role, "id", fakeRoleId);
    List<Role> roles = new ArrayList<>();
    roles.add(role);
    when(roleRepository.findAll()).thenReturn(roles);

    //when
    List<RoleDto> result = roleService.getRole();

    //then
    assertEquals(2, result.get(0).getUserIds().size());
  }

  @Test
  @DisplayName("User별 role 리스트 반환하는지 테스트")
  void get_roles_by_users() {
    //given
    Set<String> userIds = new HashSet<>();
    userIds.add("User1");
    userIds.add("User2");
    RoleDto role = new RoleDto();
    role.setName("name");
    role.setUserIds(userIds);
    String fakeRoleId = "62b2c5c30536b02fe0e1aa30";
    ReflectionTestUtils.setField(role, "roleId", fakeRoleId);
    Role role1 = new Role(role, new ArrayList<>());
    ReflectionTestUtils.setField(role1, "id", fakeRoleId);
    List<Role> roles = new ArrayList<>();
    roles.add(role1);
    when(roleRepository.findAll()).thenReturn(roles);

    //when
    Map<String, List<RoleDto>> rolesWithUsers = roleService.getRolesByUsers();

    //then
    assertEquals(2, rolesWithUsers.size());
    assertTrue(rolesWithUsers.containsKey("User1"));
  }

  @Test
  @DisplayName("Permission 생성시 type, action이 null일 때 동작 확인")
  void check_null_field() {
    Permission permission = new Permission();
    permission.setDescription("null type, action permission");
    permission.setRestricted(true);
    permission.setName("permission missed branch test");
    permission.setAction(null);
    permission.setType(null);
    assertThrows(IllegalArgumentException.class, permission::getAction);
    assertThrows(IllegalArgumentException.class, permission::getType);
  }

  @Test
  @DisplayName("role이 아예 없을 시 빈 리스트 리턴하는 지 확인")
  void check_non_exist_roleId() {
    assertEquals(0, roleService.getRole().size());
  }

  @Test
  @DisplayName("존재하지 않는 아이디로 update 시도시 아무런 작업도 일어나지 않는지 확인")
  void update_non_exist_roleId() {
    RoleDto roleDto = new RoleDto();
    roleDto.setName("update");
    roleDto.setRoleId("test");

    roleService.updateRole(roleDto);
    assertNotEquals("update", roleService.getRole("test").getName());
  }

  @Test
  @DisplayName("permissionList가 empty일 때 빈 값으로 저장되는 지 확인")
  void get_role_without_permission() {
    Role role = new Role();
    role.setRoleType(RoleTypes.ADMIN);
    role.setDefault(true);
    String fakeRoleId = "62b2c5c30536b02fe0e1ab21";
    ReflectionTestUtils.setField(role, "id", fakeRoleId);
    when(roleRepository.findById(anyString())).thenReturn(Optional.of(role));
    assertEquals(fakeRoleId, roleService.getRole(fakeRoleId).getRoleId());
  }

  @Test
  @DisplayName("user가 role을 안 가지고 있을 경우 null 리턴 하는 지 확인")
  void user_without_role_return_null() {
    when(roleRepository.findAllByUserIdsIsContaining(anyString())).thenReturn(new ArrayList<>());
    assertNull(roleService.findRoleByUserId("test"));
  }

  @Test
  @DisplayName("roleId와 일치하는 role이 없을 경우 error 던지는지 확인")
  void try_not_exist_roleId() {
    String fakeRoleId = "fakeId";
    when(roleRepository.findById(anyString())).thenReturn(Optional.empty());
    assertThrows(IllegalStateException.class, () -> roleService.getPermissionByRoleId(fakeRoleId));
  }

  @Test
  @DisplayName("update 대상 role에 permission이 담겨 있는 경우")
  void update_role_with_permission() {
    String fakeRoleId = "62b2c5c30536b02fe0e1ab21";
    String fakePermissionId = "62b2c5c30536b02fe0e1ab01";
    Role role = new Role();
    ReflectionTestUtils.setField(role, "id", fakeRoleId);
    role.setRoleType(RoleTypes.CUSTOM);

    List<Permission> permissions = new ArrayList<>();
    Permission permission = new Permission();
    permission.setDisplayName("test");
    permission.setAction(SimpleAction.READ);
    permission.setType(PermissionTypes.GENERAL);
    permission.setCTime(System.currentTimeMillis());
    permission.setName("test");
    permission.setRestricted(false);
    ReflectionTestUtils.setField(permission, "id", fakePermissionId);
    permissions.add(permission);

    List<PermissionDto> permissionDtos = permissions.stream().map(Permission::toDto)
        .collect(Collectors.toList());
    RoleDto roleDto = new RoleDto(role, permissionDtos);

    when(roleRepository.findById(roleDto.getRoleId())).thenReturn(Optional.of(role));
    when(permissionRepository.findAllByIdIn(any())).thenReturn(permissions);

    roleService.updateRole(roleDto);
    assertEquals(permissionDtos.size(), role.getPermissions().size());
  }

}