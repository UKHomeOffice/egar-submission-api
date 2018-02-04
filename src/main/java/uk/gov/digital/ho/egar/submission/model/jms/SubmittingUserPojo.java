package uk.gov.digital.ho.egar.submission.model.jms;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Builder
@Value
public class SubmittingUserPojo implements SubmittingUser {
    @JsonProperty("user_uuid")
    private UUID userUuid;
    private String email;
}
