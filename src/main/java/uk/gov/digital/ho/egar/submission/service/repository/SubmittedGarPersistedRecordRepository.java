package uk.gov.digital.ho.egar.submission.service.repository;

import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

import uk.gov.digital.ho.egar.submission.model.SubmissionStatus;
import uk.gov.digital.ho.egar.submission.service.repository.model.SubmittedGarPersistedRecord;

@Transactional
public interface SubmittedGarPersistedRecordRepository extends JpaRepository<SubmittedGarPersistedRecord, UUID>{
	List<SubmittedGarPersistedRecord> findByGarUuidAndUserUuidAndStatusIsIn(UUID garUuid, UUID userUuid, List<SubmissionStatus> statuses);
	SubmittedGarPersistedRecord findOneBySubmissionUuidAndUserUuid(UUID submissionUuid, UUID userUuid );
}
