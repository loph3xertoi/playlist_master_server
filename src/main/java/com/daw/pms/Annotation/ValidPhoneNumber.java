package com.daw.pms.Annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.daw.pms.Validator.PhoneNumberConstraintValidator;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Annotation for validating phone number.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/18/23
 */
@Documented
@Constraint(validatedBy = PhoneNumberConstraintValidator.class)
@Target({FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface ValidPhoneNumber {

  String message() default "Invalid Phone Number";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
