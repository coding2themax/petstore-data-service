package com.coding2themax.petstore.data.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;

@Documented
@Constraint(validatedBy = PetStatusConstraintValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface PetStatusContraint {
  String message() default "Invalid status";

  Class<?>[] groups() default {};

  Class<?>[] payload() default {};
}
