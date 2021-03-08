package havis.custom.harting.iso159612.data;

import havis.middleware.tdt.PackedObjects.ObjectInfoFormat;

/**
 * The id encoding options which can be used for write operations.
 */
public enum IdEncoding {
	AUTO(ObjectInfoFormat.IDLPO_DEFAULT, true),
	IDLPO_DEFAULT(ObjectInfoFormat.IDLPO_DEFAULT, true),
	IDLPO_NON_DEFAULT(ObjectInfoFormat.IDLPO_NON_DEFAULT, false),
	IDMPO(ObjectInfoFormat.IDMPO, false);

	private boolean supported;

	private ObjectInfoFormat objectInfoFormat;

	private IdEncoding(ObjectInfoFormat objectInfoFormat, boolean supported) {
		this.objectInfoFormat = objectInfoFormat;
		this.supported = supported;
	}

	public ObjectInfoFormat getObjectInfoFormat() {
		return objectInfoFormat;
	}

	public boolean isSupported() {
		return supported;
	}
}
