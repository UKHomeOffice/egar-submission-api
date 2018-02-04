package uk.gov.digital.ho.egar.submission.model;

import java.util.List;
import java.util.UUID;

import uk.gov.digital.ho.egar.submission.model.rest.Attribute;

/**
 * @see SnapshotGarPojo
 * @author Keshava.Grama
 *
 */
public interface GarSubmissionRequest {
	public UUID getGarUuid();
	public Aircraft getAircraft();
	public List<GeographicLocation> getLocations();
	public PersonsSubmissionRequest getPersons();
	public List<SupportingFilesSubmissionRequest> getSupportingFiles();
	public Attribute getAttributes();
}
