package com.daw.pms.Annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.daw.pms.Validator.RegistrationCodeConstraintValidator;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Annotation for validating registration code.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 10/6/23
 */
@Documented
@Constraint(validatedBy = RegistrationCodeConstraintValidator.class)
@Target({ElementType.PARAMETER, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface ValidRegistrationCode {

  String message() default "Invalid Registration Code";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
