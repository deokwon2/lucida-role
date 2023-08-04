package com.nkia.lucida.role.dto;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nkia.lucida.role.entity.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RoleDtoTest {

  @Test
  @DisplayName("RoleDto UserIds HashSet 생성 테스트")
  void construct_role_info() {
    RoleDto roleDto = new RoleDto();
    assertNotNull(roleDto.getUserIds(), "UserIds는 HashSet 이어야 합니다.");
  }

  @Test
  @DisplayName("Role Entity 객체를 통한 RoleInfo 생성자 테스트")
  void role_to_dto() {
    Role role = new Role();
    role.setId("62afede2a6c77538c2599283");
    role.setName("role name");
    RoleDto roleDto = new RoleDto(role, null);
    assertAll(
        () -> assertEquals(role.getId(), roleDto.getRoleId()),
        () -> assertEquals(role.getName(), roleDto.getName()),
        () -> assertEquals(role.getUserIds(), roleDto.getUserIds())
    );
  }

}