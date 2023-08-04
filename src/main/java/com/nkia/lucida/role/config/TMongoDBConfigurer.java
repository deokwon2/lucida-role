package com.nkia.lucida.role.config;

import com.nkia.lucida.common.mongodb.TenantMongoDBConfigure;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author henoh@nkia.co.kr on 2022-06-13
 * @desc
 */
@Configuration
@Import({TenantMongoDBConfigure.class})
public class TMongoDBConfigurer {

}
