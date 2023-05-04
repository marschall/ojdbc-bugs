package com.github.marschall.ojdbc.bugs;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Rollback
@SpringJUnitConfig(OracleConfiguration.class)
@Retention(RUNTIME)
@Target(TYPE)
public @interface OracleTest {

}
