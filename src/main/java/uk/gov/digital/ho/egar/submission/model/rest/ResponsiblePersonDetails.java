package uk.gov.digital.ho.egar.submission.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.digital.ho.egar.submission.model.ResponsiblePersonType;
import uk.gov.digital.ho.egar.submission.model.validation.OtherResponsiblePersonRequiredFields;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper=false)
@OtherResponsiblePersonRequiredFields
public class ResponsiblePersonDetails {

    @NotNull
    @JsonProperty("type")
    private ResponsiblePersonType type;

    @JsonProperty("name")
    private String name;

    @JsonProperty("contact_number")
    private String contactNumber;
    
    @JsonProperty("address")
    private String address;
}
