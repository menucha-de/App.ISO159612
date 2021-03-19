package havis.app.iso159612.data;

/**
 * CCOpType is an enumerated type denoting what type of operation is represented
 * by the CCOpSpec. See the Application Level Events (ALE) Specification,
 * Version 1.1.1 chapter 9.3.5 CCOpType
 */
public enum CCOpType {
	/**
	 * Read from memory
	 */
	READ,
	/**
	 * Check memory bank contents for consistency.
	 */
	CHECK,
	/**
	 * Initialize the state of a memory so that variable fields may be used
	 */
	INITIALIZE,
	/**
	 * Add the specified field to the Tag’s memory, initialized to the specified
	 * value. For a fixed field, this operation is equivalent to WRITE.
	 */
	ADD,
	/**
	 * Write a new value to an existing field.
	 */
	WRITE,
	/**
	 * Delete the specified field from memory. For a fixed field, this operation
	 * is equivalent to WRITE with a value of zero.
	 */
	DELETE,
	/**
	 * Provide a password to enable subsequent commands; for Gen2 Tags, this
	 * transitions the tag to the “secured” state.
	 */
	PASSWORD,
	/**
	 * Kill a tag; for Gen2 Tags this means to use the Gen2 “kill” command.
	 */
	KILL,
	/**
	 * Sets access permissions for a memory field
	 */
	LOCK;
}