package uk.gov.digital.ho.egar.submission.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;

public enum Gender {
    MALE,
    FEMALE,
    UNSPECIFIED;

    @JsonCreator
    public static Gender forValue(String value) {
        if (value == null) {
            return null;
        }

        return Gender.valueOf(StringUtils.upperCase(value));
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}