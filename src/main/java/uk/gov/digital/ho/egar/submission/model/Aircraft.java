package uk.gov.digital.ho.egar.submission.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Interface for any method signatures that the aircraft object will need. 
 * @author Keshava.Grama
 * 
 */
public interface Aircraft{
    @NotNull
    @Size(max = 35)
    String getType();
    @NotNull
    @Size(max = 15)
    String getRegistration();
    @NotNull
    @Size(max = 200)
    String getBase();
    @NotNull
    Boolean getTaxesPaid();
}
