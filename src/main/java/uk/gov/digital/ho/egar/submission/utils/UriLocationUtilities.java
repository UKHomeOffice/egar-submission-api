package uk.gov.digital.ho.egar.submission.utils;

import java.net.URI;
import java.util.UUID;


/**
 * The uri location utilities provide URI's for the provided parameters.
 * These can be used to construct redirection responses
 */
public interface UriLocationUtilities {

    /**
     * Gets the Submission URI from the provided submission id
     * @param submissionUuid the submission uuid.
     * @return The Submission URI
     * @throws RuntimeException When unable to construct a valid URI
     */
    URI getSubmissionUri(final UUID submissionUuid);

}
