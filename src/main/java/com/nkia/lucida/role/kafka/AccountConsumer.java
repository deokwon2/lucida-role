package com.nkia.lucida.role.kafka;

import static com.nkia.lucida.role.domain.RoleConstant.TOPIC_ACCOUNT;

import com.nkia.lucida.account.dto.OrgUserEventType;
import com.nkia.lucida.account.dto.OrganizationUserEvent;
import com.nkia.lucida.common.mongodb.TenantContextHolder;
import com.nkia.lucida.role.dao.RoleRepository;
import com.nkia.lucida.role.entity.Role;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * @author henoh@nkia.co.kr on 2022-06-20
 * @desc
 */
@Component
@Slf4j
public class AccountConsumer {

  private final RoleRepository roleRepository;


  public AccountConsumer(RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
  }

  @KafkaListener(topics = TOPIC_ACCOUNT, containerFactory = "kafkaListenerContainerFactory")
  public void listenAccountChangeEvent(
      @Payload ConsumerRecord<String, OrganizationUserEvent> message,
      @Headers MessageHeaders headers) {
    OrganizationUserEvent event = message.value();
    TenantContextHolder.INSTANCE.setTenantId(event.getOrganizationId());
    List<Role> roles = new ArrayList<>();
    if (OrgUserEventType.JOIN.equals(event.getEventType())) {
      roles = roleRepository.findAllByIdIn(event.getRoleIds());
      roles.forEach(r -> r.addUserId(event.getUserId()));
    } else if (OrgUserEventType.LEAVE.equals(event.getEventType())) {
      roles = roleRepository.findAllByUserIdsIsContaining(event.getUserId());
      roles.forEach(r -> r.getUserIds().remove(event.getUserId()));
    }
    roleRepository.saveAll(roles);
    log.info("Account Event created, Account Event Type = {}, Role is successfully updated.",
        event.getEventType());
    TenantContextHolder.INSTANCE.clear();
  }
}
