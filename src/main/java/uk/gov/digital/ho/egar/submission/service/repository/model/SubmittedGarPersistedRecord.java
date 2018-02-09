package uk.gov.digital.ho.egar.submission.service.repository.model;

import java.util.Date;
import java.util.UUID;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;
import uk.gov.digital.ho.egar.submission.model.SubmissionStatus;
import uk.gov.digital.ho.egar.submission.model.SubmittedGar;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString
@Table(name="GAR_SUBMISSION")
public class SubmittedGarPersistedRecord implements SubmittedGar {
	
	@Id
	@JsonProperty("submission_uuid")
	private UUID submissionUuid;

	@JsonIgnore
	private UUID garUuid;

	@JsonIgnore
	private UUID userUuid;

	@Column(length=2000)
	@JsonProperty("type")
	private String submissionType;
	
	@Column(length=200)
	@JsonProperty("external_ref")
	private String externalSubmissionRef;
	
	@Column(length=2000)
	@JsonProperty("reason")
	private String externalSubmissionReason;

	@Column
	@Enumerated(EnumType.STRING)
	@JsonProperty("status")
	private SubmissionStatus status;

	private Date editDateTime ;

	public SubmittedGarPersistedRecord updateEditDateTime()
	{
		this.editDateTime = new Date();
		return this ;
	}
}
