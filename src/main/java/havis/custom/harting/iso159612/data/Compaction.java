package havis.custom.harting.iso159612.data;

/**
 * The compaction options which can be used for write operations.
 */
public enum Compaction {
	NO_DIRECTORY("NO_DIRECTORY", false),
	PACKED_OBJECTS("Packed Objects", true);

	private boolean supported;

	private String compaction;

	private Compaction(String compaction, boolean supported) {
		this.compaction = compaction;
		this.supported = supported;
	}

	public String getCompaction() {
		return compaction;
	}

	public boolean isSupported() {
		return supported;
	}
}
