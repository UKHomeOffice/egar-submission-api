package uk.gov.digital.ho.egar.submission.model.rest;

import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.digital.ho.egar.submission.model.Aircraft;

@Data
@EqualsAndHashCode(callSuper = false)
public class AircraftRestPojo implements Aircraft {

    @Transient
    protected static final Log logger = LogFactory.getLog(AircraftRestPojo.class);

    private String type;
    private String registration;
    private String base;
    private Boolean taxesPaid;

    // Default no args constructor for hibernate
    public AircraftRestPojo() {
    }

    protected AircraftRestPojo(Aircraft existing) {
        type = existing.getType();
        registration = existing.getRegistration();
        base = existing.getBase();
        taxesPaid = existing.getTaxesPaid();
    }

}
