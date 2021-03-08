package havis.custom.harting.iso159612.data;

/**
 * Represents the memory banks of a Gen 2 Tag. See EPC Tag Data Standard version
 * 1.9 chapter 9.2 Gen 2 Tag Memory Map
 */
public enum Bank {
	/**
	 * Bank 00 (Reserved)
	 */
	RESERVED(0),
	/**
	 * Bank 01 (EPC)
	 */
	EPC(1),
	/**
	 * Bank 10 (TID)
	 */
	TID(2),
	/**
	 * Bank 11 (User)
	 */
	USER(3);

	/**
	 * The integer value of the bank
	 */
	private int index;

	private Bank(int index) {
		this.index = index;
	}

	/**
	 * Returns the {@link #index}
	 * 
	 * @return The {@link #index}
	 */
	public int getIndex() {
		return index;
	}
}
