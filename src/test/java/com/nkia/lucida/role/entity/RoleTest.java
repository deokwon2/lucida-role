package com.nkia.lucida.role.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nkia.lucida.role.domain.RoleTypes;
import com.nkia.lucida.role.util.ImportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RoleTest {

  @Test
  @DisplayName("UserIds HashSet<Long>에 userId 추가 테스트")
  void add_group_id() {
    Role role = new Role();
    assertNotNull(role.getUserIds());
    assertEquals(0, role.getUserIds().size());
    role.addUserId("user1");
    role.addUserId("user2");
    role.addUserId("user1");
    assertEquals(2, role.getUserIds().size());
  }

  @Test
  @DisplayName("role 타입이 admin 일 때, standard 일 때 갯수 확인")
  void return_permission_by_role_type() {
    Role adminRole = new Role();
    adminRole.setRoleType(RoleTypes.ADMIN);
    assertEquals(ImportService.getPermissionSavedIds().size(), adminRole.getPermissions().size());
    Role standardRole = new Role();
    standardRole.setRoleType(RoleTypes.STANDARD);
    assertEquals(0, standardRole.getPermissions().size());
  }

}