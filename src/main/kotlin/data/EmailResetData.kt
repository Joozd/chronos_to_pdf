package data

/**
 * Reset data for resetting an account and thereby deleting all its data.
 */
data class EmailResetData(val emailAddress: String, val resetCode: String)
