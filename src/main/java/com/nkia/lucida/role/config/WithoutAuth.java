package com.nkia.lucida.role.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author henoh@nkia.co.kr on 2022-05-11
 * @desc 추후 token이 필요하지 않은 함수가 있을 경우 사용가능
 * @info TWebInterceptor에서 WithoutAuth withoutAuth = ((HandlerMethod)
 * handler).getMethodAnnotation(WithoutAuth.class); if (withoutAuth == null) { //추후 로직 작성, 토큰이 있는 경우
 * } else{ 토큰이 없는 경우 }
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WithoutAuth {

}
