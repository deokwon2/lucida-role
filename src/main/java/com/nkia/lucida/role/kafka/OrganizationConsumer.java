package com.nkia.lucida.role.kafka;

import static com.nkia.lucida.role.domain.RoleConstant.TOPIC_ORGANIZATION_CREATE;

import com.nkia.lucida.account.dto.OrganizationEvent;
import com.nkia.lucida.common.mongodb.TenantContextHolder;
import com.nkia.lucida.common.mongodb.TenantMongoDBUtils;
import com.nkia.lucida.common.telemetry.service.LogPropagator;
import com.nkia.lucida.role.dao.RoleRepository;
import com.nkia.lucida.role.domain.RoleConstant;
import com.nkia.lucida.role.domain.RoleTypes;
import com.nkia.lucida.role.entity.Role;
import com.nkia.lucida.role.util.ImportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * @author henoh@nkia.co.kr on 2022-06-13
 * @desc
 */

@Component
@Slf4j
public class OrganizationConsumer {

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private LogPropagator logPropagator;

  @KafkaListener(topicPattern = TOPIC_ORGANIZATION_CREATE, containerFactory = "kafkaListenerContainerFactory")
  public void listenSaveAdminRole(@Payload ConsumerRecord<String, OrganizationEvent> message,
      @Headers MessageHeaders headers) {
    this.createMongoDBDatabase(message.value().getId());
    createDefaultRole();
    log.info("Organization Create Event Received, Default Roles are created, Organization Id = {}",
        message.value().getId());
  }

  private void createDefaultRole() {
    TenantContextHolder.INSTANCE.setTenantId(logPropagator.getOrganizationId());
    Role saveRole = new Role();
    saveRole.addUserId(logPropagator.getUserId());
    saveRole.setRoleType(RoleTypes.ADMIN);
    saveRole.setName(RoleConstant.ADMIN_ROLE);
    saveRole.setDefault(true);
    saveRole.setPermissions(ImportService.getPermissionSavedIds());

    Role standardRole = new Role();
    standardRole.setRoleType(RoleTypes.STANDARD);
    standardRole.setName(RoleConstant.STANDARD_ROLE);
    standardRole.setDefault(true);

    roleRepository.save(saveRole);
    roleRepository.save(standardRole);
    TenantContextHolder.INSTANCE.clear();
  }

  private void createMongoDBDatabase(String databaseName) {
    TenantMongoDBUtils.INSTANCE.createMongoDBDatabase(applicationContext, databaseName);
  }
}
