package com.nkia.lucida.role.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nkia.lucida.common.mongodb.TenantContextHolder;
import com.nkia.lucida.role.config.KafkaTestContainer;
import com.nkia.lucida.role.config.MongoDBTestContainer;
import com.nkia.lucida.role.dao.RoleRepository;
import com.nkia.lucida.role.domain.RoleConstant;
import com.nkia.lucida.role.domain.RoleTypes;
import com.nkia.lucida.role.dto.RoleDto;
import com.nkia.lucida.role.dto.UpdateUserDto;
import com.nkia.lucida.role.entity.Permission;
import com.nkia.lucida.role.entity.Role;
import com.nkia.lucida.role.service.JwtTokenService;
import com.nkia.lucida.role.service.JwtTokenServiceImpl;
import com.nkia.lucida.role.service.RoleService;
import com.nkia.lucida.role.util.ImportService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import({MongoDBTestContainer.class, KafkaTestContainer.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RoleControllerTest {

  @Autowired
  private WebApplicationContext wac;
  private MockMvc mockmvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  RoleService roleService;
  @Autowired
  RoleRepository roleRepository;

  public static final String TOKEN = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJvcmdhbml6YXRpb25JZCI6ImFzbzM5MjQwMWtjbnNsZyIsInVzZXJJZCI6Imhlbm9oQG5raWEuY28ua3IifQ.lSC1hCBkAbURJdQOxbtZ2PMqhaNeAKK9uIvD1dyGaIY";

  private String roleId;

  @BeforeAll
  void setup() {
    JwtTokenService jwtTokenService = new JwtTokenServiceImpl();
    TenantContextHolder.INSTANCE.setTenantId(jwtTokenService.getTenantIdFromBearerToken(TOKEN));
    Set<String> userIds = new HashSet<>();
    userIds.add("User1");
    userIds.add("User2");
    Set<String> userIds1 = new HashSet<>();
    userIds1.add("User3");
    RoleDto role = new RoleDto();
    RoleDto role1 = new RoleDto();
    role.setName("name");
    role1.setName("name2");
    role.setUserIds(userIds);
    role1.setUserIds(userIds1);
    role.setRoleType(RoleTypes.STANDARD);
    role1.setRoleType(RoleTypes.ADMIN);
    role.setPermissions(ImportService.getPermissionSavedIds().stream().map(Permission::toDto)
        .collect(Collectors.toList()));
    this.mockmvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    roleService.saveRole(role);
    roleId = roleService.getRole().get(0).getRoleId();
    System.out.println("setup BoardControllerTest mockMvc...");
  }

  @Test
  @DisplayName("역할 조회 Controller 테스트")
  @Order(2)
  void get_role() throws Exception {
    this.mockmvc.perform(get("/api/role/{roleId}", roleId)
            .header(RoleConstant.AUTHORIZATION_HEADER, TOKEN))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(RoleController.class))
        .andExpect(handler().methodName("getRoleById"))
        .andReturn()
        .getResponse();
  }

  @Test
  @DisplayName("역할 전체 조회 Controller 테스트")
  @Order(1)
  void get_all_role() throws Exception {
    this.mockmvc.perform(get("/api/role")
            .header(RoleConstant.AUTHORIZATION_HEADER, TOKEN))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(RoleController.class))
        .andExpect(handler().methodName("getAllRole"))
        .andReturn();
  }

  @Test
  @DisplayName("역할 저장 Controller 테스트")
  @Order(1)
  void save_role() throws Exception {
    Map<String, String> body = new HashMap<>();
    body.put("roleId", roleId);
    body.put("name", "name");
    body.put("roleType", RoleTypes.ADMIN.name());
    this.mockmvc.perform(post("/api/role").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body))
            .header(RoleConstant.AUTHORIZATION_HEADER, TOKEN))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(RoleController.class))
        .andExpect(handler().methodName("saveRole"))
        .andReturn();
  }

  @Test
  @DisplayName("역할 수정 Controller 테스트")
  @Order(2)
  void update_role() throws Exception {
    TenantContextHolder.INSTANCE.setTenantId("aso392401kcnslg");
    RoleDto roleDto = new RoleDto(roleRepository.findById(roleId).orElseGet(Role::new),
        new ArrayList<>());
    roleDto.setName("Name_After");
    this.mockmvc.perform(put("/api/role/{roleId}", roleId).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(roleDto))
            .header(RoleConstant.AUTHORIZATION_HEADER, TOKEN))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(RoleController.class))
        .andExpect(handler().methodName("updateRole"))
        .andReturn();
  }

  @Test
  @DisplayName("유저 아이디로 역할 조회 Controller 테스트")
  @Order(1)
  void get_role_by_user_id() throws Exception {
    this.mockmvc.perform(get("/api/role/user/{userId}", "User1")
            .header(RoleConstant.AUTHORIZATION_HEADER, TOKEN))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(RoleController.class))
        .andExpect(handler().methodName("findRoleByUser"))
        .andReturn();
  }

  @Test
  @DisplayName("유저 아이디 추가하는 Controller 테스트")
  @Order(1)
  void add_user() throws Exception {
    UpdateUserDto dto = new UpdateUserDto();
    Set<String> roleIds = new HashSet<>();
    roleIds.add(roleId);
    dto.setAddRoleIds(roleIds);
    dto.setRemoveRoleIds(new HashSet<>());
    this.mockmvc.perform(put("/api/role/user/{userId}", "User1")
            .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto))
            .header(RoleConstant.AUTHORIZATION_HEADER, TOKEN))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(RoleController.class))
        .andExpect(handler().methodName("updateUsers"))
        .andReturn();
  }

  @Test
  @DisplayName("역할 삭제 Controller 테스트")
  @Order(3)
  void delete_roles() throws Exception {
    this.mockmvc.perform(delete("/api/role/{roleId}", roleId)
            .header(RoleConstant.AUTHORIZATION_HEADER, TOKEN))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(RoleController.class))
        .andExpect(handler().methodName("deleteRole"))
        .andReturn();
  }

  @Test
  @DisplayName("역할 여러개 삭제 Controller 테스트")
  @Order(3)
  void delete_role() throws Exception {
    TenantContextHolder.INSTANCE.setTenantId("aso392401kcnslg");
    String roleId1 = roleRepository.save(new Role()).getId();
    String roleId2 = roleRepository.save(new Role()).getId();
    this.mockmvc.perform(delete("/api/role")
            .queryParam("roleIds", roleId1, roleId2)
            .header(RoleConstant.AUTHORIZATION_HEADER, TOKEN))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(RoleController.class))
        .andExpect(handler().methodName("deleteRoles"))
        .andReturn();
  }

  @Test
  @DisplayName("권한 리스트 조회 Controller 테스트")
  @Order(3)
  void get_permissions() throws Exception {
    this.mockmvc.perform(get("/api/permissions")
            .header(RoleConstant.AUTHORIZATION_HEADER, TOKEN))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(RoleController.class))
        .andExpect(handler().methodName("getAllPermissions"))
        .andReturn();
  }

  @Test
  @DisplayName("roleId별 권한 리스트 조회")
  @Order(2)
  void get_permission_by_roleId() throws Exception {
    this.mockmvc.perform(get("/api/role/{roleId}/permissions", roleId)
            .header(RoleConstant.AUTHORIZATION_HEADER, TOKEN))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(RoleController.class))
        .andExpect(handler().methodName("getPermissionByRoleId"))
        .andReturn();
  }

  @Test
  @DisplayName("user별 roledto 리스트 조회 Controller 테스트")
  @Order(3)
  void get_roles_by_users() throws Exception {
    this.mockmvc.perform(get("/api/role/users")
            .header(RoleConstant.AUTHORIZATION_HEADER, TOKEN))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(handler().handlerType(RoleController.class))
        .andExpect(handler().methodName("getRolesByUsers"))
        .andReturn();
  }

  @AfterAll
  void delete_all() {
    JwtTokenService jwtTokenService = new JwtTokenServiceImpl();
    TenantContextHolder.INSTANCE.setTenantId(jwtTokenService.getTenantIdFromBearerToken(TOKEN));
    roleRepository.deleteAll();
    TenantContextHolder.INSTANCE.clear();
  }
}