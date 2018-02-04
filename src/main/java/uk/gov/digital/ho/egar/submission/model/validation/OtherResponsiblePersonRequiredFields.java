package uk.gov.digital.ho.egar.submission.model.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Documented
@Constraint(validatedBy = { OtherResponsiblePersonRequiredFieldsValidator.class } )
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface OtherResponsiblePersonRequiredFields {
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}