package uk.gov.digital.ho.egar.submission.model.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = { SingleLocationValidator.class } )
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SingleLocationPresent {
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}