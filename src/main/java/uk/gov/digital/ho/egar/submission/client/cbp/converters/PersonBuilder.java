package uk.gov.digital.ho.egar.submission.client.cbp.converters;

import com.fincore.cbp.stt.xml.T_Person;
import uk.gov.digital.ho.egar.submission.model.People;

import java.util.Optional;

public interface PersonBuilder {
    Optional<T_Person> builder(People person, boolean isCrew);
}
