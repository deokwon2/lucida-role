package com.nkia.lucida.role.dao;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nkia.lucida.common.mongodb.TenantContextHolder;
import com.nkia.lucida.role.config.MongoDBTestContainer;
import com.nkia.lucida.role.domain.RoleTypes;
import com.nkia.lucida.role.entity.Role;
import com.nkia.lucida.role.util.ImportService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import({MongoDBTestContainer.class})
class RoleRepositoryTest {

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PermissionRepository permissionRepository;

  int count = 1;

  String id;

  @ParameterizedTest(name = "이름이 {0}이고 설명이 {1}인 객체 저장 및 조회")
  @CsvSource({"name, description", "name2, description2", "name3, description3"})
  @Order(0)
  void set_up(String name, String description) {
    TenantContextHolder.INSTANCE.setTenantId("test");
    Role role = new Role();
    role.setName(name);
    //role.setDescription(description);
    role.setRoleType(RoleTypes.ADMIN);
    role.setPermissions(ImportService.getPermissionSavedIds());
    System.out.println(name + " " + description);
    roleRepository.save(role);
    id = role.getId();
    Role roleById = roleRepository.findById(id).orElseGet(Role::new);
    assertAll(
        () -> assertEquals(name, roleById.getName()),
        () -> assertEquals(id, roleById.getId())
    );
    count++;
  }

  @AfterAll
  void delete_all() {
    roleRepository.deleteAll();
    TenantContextHolder.INSTANCE.clear();
  }
}