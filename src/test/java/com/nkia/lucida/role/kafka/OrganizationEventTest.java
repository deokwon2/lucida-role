package com.nkia.lucida.role.kafka;

import static com.nkia.lucida.role.domain.RoleConstant.TOPIC_ACCOUNT;
import static com.nkia.lucida.role.domain.RoleConstant.TOPIC_ORGANIZATION_CREATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.nkia.lucida.account.dto.OrgUserEventType;
import com.nkia.lucida.account.dto.OrganizationEvent;
import com.nkia.lucida.account.dto.OrganizationUserEvent;
import com.nkia.lucida.common.mongodb.TenantContextHolder;
import com.nkia.lucida.common.telemetry.service.LogPropagator;
import com.nkia.lucida.role.config.KafkaTestContainer;
import com.nkia.lucida.role.config.MongoDBTestContainer;
import com.nkia.lucida.role.dao.RoleRepository;
import com.nkia.lucida.role.domain.RoleTypes;
import com.nkia.lucida.role.entity.Role;
import com.nkia.lucida.role.service.RoleService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * @author henoh@nkia.co.kr on 2022-07-07
 * @desc
 */
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import({MongoDBTestContainer.class, KafkaTestContainer.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrganizationEventTest {

  @Autowired
  private KafkaTemplate<String, OrganizationEvent> organizationTemplate;

  @Autowired
  private LogPropagator logPropagator;

  @Mock
  RoleRepository roleRepository;

  @Mock
  RoleService roleService;

  @Test
  @DisplayName("테넌트 생성 이벤트 확인")
  @Order(0)
  void on_organization_create() {
    logPropagator.setUserId("62c63b628d4e634fcc565d0d");
    logPropagator.setOrganizationId("62c63b628d4e634fcc565d0c");
    TenantContextHolder.INSTANCE.setTenantId("62c63b628d4e634fcc565d0c");
    OrganizationEvent organizationEvent
        = new OrganizationEvent("62c63b628d4e634fcc565d0c", "test organization",
        "this organization is for test");
    Message<OrganizationEvent> message = MessageBuilder.withPayload(organizationEvent)
        .setHeader("kafka_topic", TOPIC_ORGANIZATION_CREATE)
        .setHeader("kafka_messageKey", "")
        .setHeader("kafka_partitionId", 0)
        .setHeader("organizationId", "62c63b628d4e634fcc565d0c")
        .setHeader("userId", "62c63b628d4e634fcc565d0d")
        .build();

    organizationTemplate.send(message);
    organizationTemplate.flush();
    assertNotNull(roleRepository.findAll());
  }

  @Test
  @DisplayName("User 조직 추가 이벤트 확인")
  @Order(1)
  void on_user_add_to_organization() {
    logPropagator.setUserId("62c63b628d4e634fcc565d0d");
    logPropagator.setOrganizationId("62c63b628d4e634fcc565d0c");

    Set<String> userIds = new HashSet<>();
    userIds.add("User1");
    userIds.add("User2");
    Role role = new Role();
    role.setName("name");
    role.setUserIds(userIds);
    role.setPermissions(new ArrayList<>());
    role.setRoleType(RoleTypes.ADMIN);
    String fakeRoleId = "62b2c5c30536b02fe0e1aa70";
    ReflectionTestUtils.setField(role, "id", fakeRoleId);
    when(roleRepository.findById(fakeRoleId)).thenReturn(Optional.of(role));

    List<String> roleIds = new ArrayList<>();
    roleIds.add(fakeRoleId);
    OrganizationUserEvent organizationUserEvent
        = new OrganizationUserEvent(OrgUserEventType.JOIN, "62c63b628d4e634fcc565d0c",
        "62c63b628d4e634fcc565d01", roleIds);
    Message<OrganizationUserEvent> message = MessageBuilder.withPayload(organizationUserEvent)
        .setHeader("kafka_topic", TOPIC_ACCOUNT)
        .setHeader("kafka_messageKey", "")
        .setHeader("kafka_partitionId", 0)
        .setHeader("organizationId", "62c63b628d4e634fcc565d0c")
        .setHeader("userId", "62c63b628d4e634fcc565d0d")
        .build();

    organizationTemplate.send(message);
    organizationTemplate.flush();
    assertEquals(2,
        roleRepository.findById(fakeRoleId).isPresent() ? roleRepository.findById(fakeRoleId).get()
            .getUserIds().size() : 0);
  }

  @Test
  @DisplayName("User 조직 삭제 이벤트 확인")
  @Order(2)
  void on_user_delete_from_organization() {
    logPropagator.setUserId("62c63b628d4e634fcc565d0d");
    logPropagator.setOrganizationId("62c63b628d4e634fcc565d0c");
    TenantContextHolder.INSTANCE.setTenantId("62c63b628d4e634fcc565d0c");
    OrganizationUserEvent organizationUserEvent
        = new OrganizationUserEvent(OrgUserEventType.LEAVE, "62c63b628d4e634fcc565d0c",
        "62c63b628d4e634fcc565d01", null);
    Message<OrganizationUserEvent> message = MessageBuilder.withPayload(organizationUserEvent)
        .setHeader("kafka_topic", TOPIC_ACCOUNT)
        .setHeader("kafka_messageKey", "")
        .setHeader("kafka_partitionId", 0)
        .setHeader("organizationId", "62c63b628d4e634fcc565d0c")
        .setHeader("userId", "62c63b628d4e634fcc565d0d")
        .build();

    organizationTemplate.send(message);
    organizationTemplate.flush();
    assertEquals(0, roleRepository.findAllByUserIdsIsContaining("62c63b628d4e634fcc565d0d").size());
  }

  @AfterAll
  public void clear() {
    TenantContextHolder.INSTANCE.setTenantId("62c63b628d4e634fcc565d0c");
    roleService.deleteRole("62b2c5c30536b02fe0e1aa30");
    TenantContextHolder.INSTANCE.clear();
  }
}