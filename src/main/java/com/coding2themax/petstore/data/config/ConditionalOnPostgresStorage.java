package com.coding2themax.petstore.data.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * Only creates the bean when the service stores data in Postgres: {@code petstore.storage=postgres},
 * which is also the default.
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ConditionalOnProperty(name = "petstore.storage", havingValue = "postgres", matchIfMissing = true)
public @interface ConditionalOnPostgresStorage {
}
