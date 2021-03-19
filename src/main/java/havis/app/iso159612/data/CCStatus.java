package havis.app.iso159612.data;

/**
 * CCStatus is an enumerated value that lists the several possible outcomes for
 * a given operation. See the Application Level Events (ALE) Specification,
 * Version 1.1.1 chapter 9.4.6 CCStatus
 */
public enum CCStatus {
	/**
	 * The operation completed successfully.
	 */
	SUCCESS("SUCCESS"),
	/**
	 * An error occurred during the processing of this operation that resulted
	 * in total failure. The state of the Tag following the operation attempt is
	 * unchanged. An ALE implementation SHALL return this code only if no more
	 * specific code applies.
	 */
	MISC_ERROR_TOTAL("MISC_ERROR_TOTAL"),
	/**
	 * An error occurred during the processing of this operation that resulted
	 * in partial failure. The state of the Tag following the operation attempt
	 * is indeterminate. For example, if a WRITE operation requires issuing two
	 * write commands via an RFID Tag’s Air Interface, a failure during the
	 * second Air Interface command results in partial failure of the overall
	 * WRITE operation. An ALE implementation SHALL return this code only if no
	 * more specific code applies.
	 */
	MISC_ERROR_PARTIAL("MISC_ERROR_PARTIAL"),
	/**
	 * The operation failed because the Tag denied permission: for example, an
	 * attempt to write to a locked field of a Gen2 RFID Tag without first
	 * supplying an access password. An ALE implementation SHALL return this
	 * code only if the denial of permission resulted in total failure.
	 */
	PERMISSION_ERROR("PERMISSION_ERROR"),
	/**
	 * (PASSWORD operation only) The supplied password was incorrect.
	 */
	PASSWORD_ERROR("PASSWORD_ERROR"),
	/**
	 * The specified field of the Tag was not found (see Section 5.4).
	 */
	FIELD_NOT_FOUND_ERROR("FIELD_NOT_FOUND_ERROR"),
	/**
	 * The specified operation is not possible on the specified field of the Tag
	 * (see the “operation not possible” condition specified in Section 5.4). In
	 * contrast to PERMISSION_ERROR, which indicates an error that could be
	 * overcome by supplying appropriate credentials or by an appropriately
	 * privileged client, OP_NOT_POSSIBLE_ERROR indicates that limitations of
	 * the Tag or the ALE implementation prevent this operation from being
	 * carried out on the specified field under any circumstances.
	 */
	OP_NOT_POSSIBLE_ERROR("OP_NOT_POSSIBLE_ERROR"),
	/**
	 * The specified value could not be encoded using the available number of
	 * bits (see the “out of range” condition specified in Section 5.4). This
	 * applies to the WRITE and ADD operations for fixed fields, as well as to
	 * the PASSWORD and KILL operations.
	 */
	OUT_OF_RANGE_ERROR("OUT_OF_RANGE_ERROR"),
	/**
	 * The ADD operation failed because the specified field already exists in
	 * memory. This error cannot occur for a fixed field fieldspec.
	 */
	FIELD_EXISTS_ERROR("FIELD_EXISTS_ERROR"),
	/**
	 * Attempting to add a new field or modify an existing variable-length field
	 * to the memory bank would overflow the free memory left in the memory
	 * bank.
	 */
	MEMORY_OVERFLOW_ERROR("MEMORY_OVERFLOW_ERROR"),
	/**
	 * The CHECK operation failed.
	 */
	MEMORY_CHECK_ERROR("MEMORY_CHECK_ERROR"),
	/**
	 * The value retrieved from the association table was not valid syntax for
	 * the datatype and format implied by the fieldspec parameter of the
	 * CCOpSpec.
	 */
	ASSOCIATION_TABLE_VALUE_INVALID("ASSOCIATION_TABLE_VALUE_INVALID"),
	/**
	 * The association table did not contain a value for the EPC read from the
	 * Tag.
	 */
	ASSOCIATION_TABLE_VALUE_MISSING("ASSOCIATION_TABLE_VALUE_MISSING"),
	/**
	 * The specified EPC Cache was empty at the time of the operation attempt.
	 */
	EPC_CACHE_DEPLETED("EPC_CACHE_DEPLETED");

	private String status;

	private CCStatus(String status) {
		this.status = status;
	}

	public boolean isEquals(String status) {
		if (status == null)
			return false;
		return this.status.toLowerCase().equals(status.toLowerCase());
	}

}