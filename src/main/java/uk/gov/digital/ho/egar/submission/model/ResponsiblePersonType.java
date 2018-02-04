package uk.gov.digital.ho.egar.submission.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;


public enum ResponsiblePersonType {
    OTHER,
    CAPTAIN;

    @JsonCreator
    public static ResponsiblePersonType forValue(String value) {
        if (value == null){
            return null;
        }

        return ResponsiblePersonType.valueOf(StringUtils.upperCase(value));

    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
