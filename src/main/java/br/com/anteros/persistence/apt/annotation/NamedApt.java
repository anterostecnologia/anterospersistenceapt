package br.com.anteros.persistence.apt.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

@Documented
@Retention(RUNTIME)
public @interface NamedApt {

	/** The name. */
	String value() default "";
}
