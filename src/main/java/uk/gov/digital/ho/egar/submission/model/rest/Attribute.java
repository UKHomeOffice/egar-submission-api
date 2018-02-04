package uk.gov.digital.ho.egar.submission.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * The attributes that can be associated with a General aviation report.
 * @author Gareth.Penfold
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class Attribute {

    /**
     * Whether there are hazardous goods on board.
     */
    @NotNull
    private Boolean hazardous;


    @NotNull
    @Valid
    @JsonProperty("responsible_person")
    private ResponsiblePersonDetails otherDetails;

}
