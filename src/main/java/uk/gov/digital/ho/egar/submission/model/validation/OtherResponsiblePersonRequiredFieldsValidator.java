package uk.gov.digital.ho.egar.submission.model.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.h2.util.StringUtils;

import uk.gov.digital.ho.egar.submission.model.ResponsiblePersonType;
import uk.gov.digital.ho.egar.submission.model.rest.ResponsiblePersonDetails;

/**
 * Does the validation for responsible persons with type of other.
 */
public class OtherResponsiblePersonRequiredFieldsValidator implements ConstraintValidator<OtherResponsiblePersonRequiredFields, ResponsiblePersonDetails> {


    @Override
    public void initialize(OtherResponsiblePersonRequiredFields requiredOtherTypeFields) {

    }

    @Override
    public boolean isValid(ResponsiblePersonDetails details, ConstraintValidatorContext ctx) {
        if (details == null)
            // Nothing so this is ok
            return true;

        boolean isOk = true;
        //If the responsible person type is other then certain details are required
        if (details.getType() == ResponsiblePersonType.OTHER) {
            if (StringUtils.isNullOrEmpty(details.getAddress()) ||
                    StringUtils.isNullOrEmpty(details.getName()) ||
                    StringUtils.isNullOrEmpty(details.getContactNumber())){
                isOk = false;
            }

            boolean isDefaultMessage = "".equals(ctx
                    .getDefaultConstraintMessageTemplate());

            if (!isOk && isDefaultMessage) {
                ctx.disableDefaultConstraintViolation();
                ctx
                        .buildConstraintViolationWithTemplate("Other responsible person type requires; address; name; contact number")
                        .addBeanNode()
                        .addConstraintViolation();
            }
        }
        return isOk;
    }
}
