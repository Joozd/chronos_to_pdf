package repository

import java.time.Instant

/**
 * Holds metadata for data in a repository.
 * @property versionNumber The version number of this data. Can be null.
 * @property fileName The filename of this data when it was downloaded. Can be null.
 * @property downloadInstant The moment when this data was downloaded. Can be null.
 * @property latestCheckInstant The moment when the most recent check for updated data was successfully completed. Can be null.
 */
data class RepositoryMetadata(
    val versionNumber: Long? = null,
    val fileName: String? = null,
    val downloadInstant: Instant? = null,
    val latestCheckInstant: Instant? = null
)